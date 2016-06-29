/*-GNU-GPL-BEGIN-*
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*-GNU-GPL-END-*/

package jmud;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.util.Date;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;

import jmud.magic.Magic;
import jmud.command.Command;
import jmud.command.CommandTable;
import jmud.command.CommandTokenizer;
import jmud.util.pair.StrIntPair;
import jmud.util.state.StateKeeper;
import jmud.util.state.StateUser;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.StrUtil;
import jmud.util.StringParseException;
import jmud.util.Separators;
import jmud.util.log.Log;
import jmud.util.log.LogRecipient;
import jmud.jgp.PrefixMatcher;
import jgp.container.Set;
import jgp.container.Queue;
import jgp.container.Vector;
import jgp.algorithm.Searcher;
import jgp.container.Scheduler;
import jgp.interfaces.Event;


public class Manager implements LogRecipient, StateUser {

    static final int    MAX_RECENT_SESSIONS = 20;
    static final String stateUserId         = "Manager";
    static final String newbieLockKey       = "TravaDeIniciantes";
    static final String emptyLockStr        = "-";

    private Set                  clientMap       = new Set();
    private Set                  incomingClients = new Set();
    private Set                  playingClients  = new Set();
    private Set                  fighters        = new Set();
    private Interpreter          theInterpreter  = null;
    private World                theWorld        = null;
    private SerialGenerator      theSerialGen    = null;
    private ServerSocket         serverSocket    = null;
    private Help                 theHelp         = null;
    private StateKeeper          keeper          = null;
    private EffectTimer          effecter        = new EffectTimer();
    private int                  portNumber;
    private boolean              listening;
    private int                  shutdownTime    = 0;
    private String               motd            = "";
    private InterpreterSuspender suspender       = new InterpreterSuspender();
    private int                  maxClients;
    private Authenticable        newbieLock      = null;
    private Vector               recentSessions  = new Vector(); // ordem importa

  Manager(int port) {

      //
      // Define a porta de espera por conexoes
      //

    portNumber = port;

    //
    // Instala socket na porta.
    //

    try {
      serverSocket = new ServerSocket(portNumber);
    }
    catch (IOException e) {
      Log.abort("Falha ao ouvir porta: " + portNumber);
    }
    Log.info("Executando na porta: " + portNumber);

    //
    // Obtem maximo de clientes simultaneos
    //

    maxClients = Global.getConfig().getMaxClients();
    if (maxClients < 0)
	maxClients = 0;
    Log.info("Máximo de clientes: " + maxClients);

    //
    // Carrega ajuda
    //

    String help = Global.getConfig().getHelpFileName();
    Log.info("Carregando arquivo de ajuda: " + help);

    try {
      theHelp = new Help(help);
    }
    catch(FileNotFoundException e) {
      Log.err("Arquivo de ajuda não encontrado: " + help);
    }
    catch(IOException e) {
      Log.err("Erro de E/S ao abrir arquivo de ajuda: " + help);
    }
    catch(InvalidFileFormatException e) {
      Log.err("Arquivo de ajuda com formato inválido: " + help);
    }

    //
    // Carrega subsistema de manutencao de persistencia de estado
    //

    String state = Global.getConfig().getStateFileName();
    Log.info("Carregando arquivo de estado: " + state);

    try {
      keeper = new StateKeeper(state);
    }
    catch(IOException e) {
      Log.abort("Erro de E/S ao abrir arquivo de estado: " + state);
    }
    catch(InvalidFileFormatException e) {
      Log.abort("Arquivo de estado com formato inválido: " + state);
    }

    //
    // Carrega mensagem do dia
    //

    String motdfile = Global.getConfig().getMotdFileName();
    Log.info("Carregando mensagem do dia: " + motdfile);

    try {
      LineReader reader = new LineReader(new FileReader(motdfile));
      while (!reader.eos())
	motd += reader.readLine() + Separators.NL;
      reader.close();
    }
    catch(IOException e) {
      Log.err("Erro de E/S ao carregar mensagem do dia: " + motdfile);
    }

    //
    // Carrega mapa de clientes
    //

    loadClientMap();

    //
    // Cria o interpretador
    //

    theInterpreter  = new Interpreter(this, keeper);

    //
    // Cria o gerador de IDs seriais
    //

    theSerialGen = new SerialGenerator(keeper);

    //
    // Carrega trava de iniciantes
    //
    keeper.register(this);
    String newbieLockStr = keeper.lookupInfo(this, newbieLockKey);
    if (newbieLockStr != null && !newbieLockStr.startsWith(emptyLockStr)) {
	try {
	    newbieLock = new PlayerCredentials(newbieLockStr);
	}
	catch (StringParseException e) {
	    Log.err("StateKeeper fornecendo trava de iniciantes invalida: " + newbieLockStr);
	}
    }

  }

  void loadClientMap() {
    Log.info("Carregando mapa de clientes");

    File f = new File(Global.getConfig().getUserFilesDir());
    if (!f.isDirectory())
      Log.abort("Falha ao abrir diretório de usuários: " + Global.getConfig().getUserFilesDir());

    String users[] = f.list();

    Player pl = null;
    for (int i = 0; i < users.length; ++i) {
      try {
	int id = Integer.parseInt(users[i]);
	pl = Player.load(id);
	addClientMapEntry(pl.getName(), id);
      }
      catch(NumberFormatException e) {
	Log.err("Nome inválido para arquivo de usuário: " + users[i]);
      }
    }
  }

    Enumeration getClientMapEnum() {
	return clientMap.elements();
    }

    void addClientMapEntry(String name, int id) {
	clientMap.insert(new StrIntPair(name, id));
    }

    public String listClientMap() {
	String list = "";
	for (Enumeration e = getClientMapEnum(); e.hasMoreElements(); ) {
	    StrIntPair pair = (StrIntPair) e.nextElement();
	    list += Separators.NL + StrUtil.rightPad(pair.getStr(), Client.MAX_NAME_LENGTH) + " " + StrUtil.formatNumber(pair.getInt(), 5);
	}
	return list;
    }

  ////////////////////////
  // Métodos synchronized:

  /*                      desconectado
                                | (income)
                          recém-chegado
			  / (play)   \ (retreat)
		      jogando        desconectado
                         | (quit)
                    desconectado
   */

  // Entra na lista de recém-chegados
  synchronized /* incomingClients, playingClients */
  void income(Client aClient) {
    incomingClients.insert(aClient);
  }

  // Desiste de se conectar (sai da lista de recém-chegados)
  synchronized /* incomingClients */
  void retreat(Client aClient) {
    incomingClients.remove(aClient);
  }

    // O Interpreter usa este método para avisar o Manager de que
    // um personagem saiu efetivamente do mundo
    //
    // Executado na thread do Interpreter
    void quitDone() {
	suspender.oneLessClient();
    }

    // O Client usa este método (via metodo play) para inserir
    // seu personagem no mundo.
    //
    // Executado na thread do Client

    synchronized /* playingClients, incomingClients */
    private void arrive(Client aClient) {
	incomingClients.remove(aClient);
	playingClients.insert(aClient);

	suspender.oneMoreClient();
    }

    // Usado _apenas_ pelo Interpreter para se congelar
    // aguardando por conexoes apos a carga do sistema.
    void suspendWhileEmpty() {
	suspender.blockWhileEmpty();
    }

    // Remove um personagem do lista de jogadores

    synchronized /* playingClients */
    private void leave(Client aClient) {
	playingClients.remove(aClient);
    }

    //////////////////////////////////
    // Muda para a lista de conectados

    // * e' chamado pela thread do cliente (Client) para
    //   inserir o respectivo personagem no mundo
    // 1. chaveia o personagem para a lista efetiva de jogadores
    // 2. agenda BeginTask, que faz o restante do processamento
    //    na thread do Interpreter

    void play(Client aClient) {
	arrive(aClient);
	enqueueTask(new BeginTask(aClient, -1));
    }

    void play(Client aClient, int roomId) {
	arrive(aClient);
	enqueueTask(new BeginTask(aClient, roomId));
    }

    // Muda para a lista de conectados
    //////////////////////////////////

    ///////////////////////
    // Complemento do 'play

    // * executado pela thread do cliente

    // Sai da lista de conectados
    void quit(Client aClient) {
	leave(aClient);
	enqueueTask(new QuitTask(aClient));
    }

    // Complemento do 'play
    ///////////////////////

    //
    //////////////////////////

    public String getWorldTime() {
	return theInterpreter.getYear() + "/" +
	    StrUtil.formatNumber(theInterpreter.getMonth(), 2, '0') + "/" +
	    StrUtil.formatNumber(theInterpreter.getDay(), 2, '0') + " " +
	    StrUtil.formatNumber(theInterpreter.getHour(), 2, '0') + ":" +
	    StrUtil.formatNumber(theInterpreter.getMinute(), 2, '0');
    }

    public String findHelpTopic(String topic) {
	if (theHelp == null) {
	    Log.warn("Manager.findElement() ignorando Ajuda vazia");
	    return null;
	}

	return theHelp.findHelpByKey(topic);
    }

  void enqueueTask(Task tsk) {
    theInterpreter.enqueueTask(tsk);
  }

    // apenas para nao passar ponteiro de interpreter
    // para o comando "force"
    public void enqueueRequest(Request req) {
	theInterpreter.enqueueRequest(req);
    }

  // consuta ao Serial Generator para o fornecimento
  // de um novo ID de cliente
  int newClientId() {
    return theSerialGen.next();
  }

  synchronized /* incomingClients, playingClients */
  void disconnectClients() {
    Log.info("Desconectando clientes");

    for (Enumeration ce = incomingClients.elements(); ce.hasMoreElements(); ) {
      Client c = (Client) ce.nextElement();
      c.flush();
      c.rawSend(Separators.NL + "Sistema desativado." + Separators.NL);
      c.disconnect();
    }

    for (Enumeration ce = playingClients.elements(); ce.hasMoreElements(); ) {
      Client c = (Client) ce.nextElement();
      c.saveChar();
      c.flush();
      c.rawSend(Separators.NL + "Sistema desativado." + Separators.NL);
      c.disconnect();
    }
  }

    void saveState() {
	Log.debug("Gravando estado");

	try {
	    keeper.shutdown();
	}
	catch(IOException e) {
	    Log.err("Falha ao gravar estado");
	}
    }

    /*
      Este método é invocado pelo Interpreter,
      para pedir ao Manager que encerre o programa.
      É executado na Thread do Interpreter!
    */
    public void shutdown() {
	disconnectClients();
	saveState();
	theWorld.saveBoards();
	Log.info("Desativado");
	System.exit(0);
    }

    public synchronized /* playingClients */
	Client findClientByName(String name) {
	for (Enumeration ce = playingClients.elements(); ce.hasMoreElements(); ) {
	    Client c = (Client) ce.nextElement();
	    if (c.hasName(name))
		return c;
	}
	return null;
    }

  synchronized /* playingClients */
    private void logToClients(String msg, int minRank) {
      for (Enumeration ce = playingClients.elements(); ce.hasMoreElements(); ) {
	Client cli = (Client) ce.nextElement();
	Char ch = cli.getChar();
	if (ch.getRank() >= minRank && ((Player) ch).isReceivingLog())
	  ch.send(msg);
      }
  }

    public synchronized /* playingClients */
	void sendToClients(String msg, int minRank) {
	for (Enumeration ce = playingClients.elements(); ce.hasMoreElements(); ) {
	    Client cli = (Client) ce.nextElement();
	    Char ch = cli.getChar();
	    if (ch.getRank() >= minRank)
		ch.send(msg);
	}
    }

  synchronized /* playingClients */
    void feedInterpreter() {
      for (Enumeration ce = playingClients.elements(); ce.hasMoreElements(); ) {
	Client cli = (Client) ce.nextElement();
	Request req = cli.oneRequest();
	if (req != null)
	  theInterpreter.enqueueRequest(req);
      }
  }

  synchronized /* playingClients */
    void flushToClients() {
      for (Enumeration ce = playingClients.elements(); ce.hasMoreElements(); ) {
	Client cli = (Client) ce.nextElement();
	cli.flush();
      }
  }

    public int getShutdownTime() {
	return shutdownTime;
    }

    public void scheduleShutdown(int time) {
	shutdownTime = time;
    }

    public void cancelShutdown() {
	shutdownTime = 0;
    }

  private void checkShutdown() {
    if (shutdownTime > 0)
      if (--shutdownTime == 0) {
	Log.info("Executando desativação agendada");
	shutdown();
      }
      else
	sendToClients("Desativação agendada ocorrerá em " + shutdownTime + " hora(s).", Char.R_DISABLED);
  }

  ///////////////////
  // LogRecipient:

  public void forward(String msg, int priority) {
    logToClients("[" + msg + "]", priority);
  }

  //
  ///////////////////

  ///////////////////
  // Usados por Char:

  void enterFight(Char ch) {
    fighters.insert(ch);
  }

  void leaveFight(Char ch) {
    fighters.remove(ch);
  }

  //
  ///////////////////

  private void pulseBattles() {
    for (Enumeration fig = fighters.elements(); fig.hasMoreElements(); ) {
      Char curr = (Char) fig.nextElement();
      curr.attack();
    }
  }

  //////////////////////////////////////////////////////////////////
  // Chamados pelo interpreter quando os respectivos ticks ocorrem:

  void yearAction() {
  }

  void monthAction() {
  }

  void dayAction() {
  }

  void hourAction() {
    effecter.updateEffects();

    checkShutdown();

    theWorld.checkResets();

    int hour = theInterpreter.getHour();
    switch (hour) {
    case 6:
      theWorld.sendToOpenPlace("Amanheceu.");
      break;
    case 18:
      theWorld.sendToOpenPlace("Anoiteceu.");
      break;
    }

    theWorld.performRegeneration();
    theWorld.creatureAction();
  }

  void minuteAction() {
    pulseBattles();
  }

  void tickAction() {
    flushToClients();
  }

  //
  //////////////////////////////////////////////////////////////////

    public void insertEffect(Effect eff) {
	effecter.insert(eff, false);
    }

    public void removeEffect(Effect eff) {
	effecter.remove(eff, true);
    }

  String getMotd() {
    return motd;
  }

    // Usado por Client para verificar se um
    // novo cliente pode entrar no sistema.
    boolean acceptOneMoreClient() {
	return suspender.getNumberOfClients() < maxClients;
    }

    public Authenticable getNewbieLock() {
	return newbieLock;
    }

    public void setNewbieLock(Authenticable pc) {
	newbieLock = pc;
    }

  ////////////
  // StateUser

  public String getStateUserId() {
    return stateUserId;
  }

  public void updateStateKeeper(StateKeeper keeper) {
    keeper.updateInfo(this, newbieLockKey, (newbieLock == null) ? emptyLockStr :newbieLock.toString());
  }

  // StateUser
  ////////////

    synchronized /* recentSessions */
    void logSession(String playerName, String hostName) {
	String session = (String) Searcher.linearSearch(recentSessions, new PrefixMatcher(playerName));
	if (session == null) {
	    if (recentSessions.getSize() >= MAX_RECENT_SESSIONS) {
		try {
		    recentSessions.remove(recentSessions.getElementAt(0));
		}
		catch (NoSuchElementException e) {
		    Log.err("Removendo primeira sessão não-existente");
		}
		catch (ArrayIndexOutOfBoundsException e) {
		    Log.err("Acessando primeira máquina não-existente");
		}
	    }
	}
	else
	    recentSessions.remove(session);
	recentSessions.insert(StrUtil.rightPad(playerName, Client.MAX_NAME_LENGTH) + " "+ StrUtil.rightPad(hostName, Client.HOST_NAME_LENGTH) + " " + StrUtil.getDate());
    }

    public synchronized /* recentSessions */
	Enumeration getSessions() {
	return recentSessions.elements();
    }

  void go(boolean testOnly) {

    Log.info("Carregando mundo");
    theWorld = new World(this);

    if (testOnly) {
	Log.info("Terminando carregamento do mundo");
	return;
    }

    Command.setWorld(theWorld);
    Task.setWorld(theWorld);
    Char.setWorld(theWorld);
    Item.setWorld(theWorld);
    Room.setWorld(theWorld);
    Magic.setWorld(theWorld);
    CreatureProto.setWorld(theWorld);

    theWorld.resetZones();

    Log.info("Lançando interpretador");
    theInterpreter.start();

    Log.info("Esperando por conexões");

    listening = true;
    while (listening) {
      Socket clientSocket = null;

      try {
	clientSocket = serverSocket.accept();
      }
      catch (IOException e) {
	Log.err("Falha ao aceitar conexão na porta: " + portNumber);
	continue;
      }

      Client newClient = null;
      try {
	newClient = new Client(clientSocket);
      }
      catch(IOException e) {
	Log.err("Falha ao criar novo cliente");
	continue;
      }

      income(newClient);
      newClient.start();
    }

    try {
      serverSocket.close();
    }
    catch (IOException e) {
      Log.err("Falha ao fechar socket servidor");
    }

    Log.info("Abandonada espera por conexões");
  }
}


class SerialGenerator implements StateUser {

  static final String stateUserId = "SerialGenerator";
  static final String serialKey   = "ContadorSerial";
  private      int    current;

  SerialGenerator(StateKeeper keeper) {
    keeper.register(this);
    String info = keeper.lookupInfo(this, serialKey);

    try {
      current = (info == null) ? 0 : Integer.parseInt(info);
    }
    catch(NumberFormatException e) {
      Log.abort("SerialGenerator com valor corrente inválido: " + info);
    }

  }

  synchronized int next() {
    return current++;
  }

  //////////////
  // StateUser:

  public String getStateUserId() {
    return stateUserId;
  }

  public void updateStateKeeper(StateKeeper keeper) {
    keeper.updateInfo(this, serialKey, String.valueOf(current));
  }

  //
  /////////////
}

class TaskQueue extends Queue {

  synchronized public void insert(Object event) {
    super.insert(event);
  }

  synchronized public Object remove() {
    return super.remove();
  }
}

class Interpreter extends Thread implements StateUser {

  private Manager   theManager   = null;
  private boolean   interpreting = true;
  private TaskQueue theRequests  = new TaskQueue();
  private TaskQueue theTasks     = new TaskQueue();

    private Scheduler theScheduler = new Scheduler();

  static private final int  PULSES_PER_SEC = 4;
  static private final int  SLEEP_TIME     = 1000 / PULSES_PER_SEC;

  private int        pulses       = 0;

  private int        minute       = 0;
  private int        hour         = 0;

  private int        day          = 1;
  private int        month        = 1;
  private int        year         = 0;

  static final String stateUserId = "Interpreter";
  static final String infoKey     = "TempoDoMundo";

  Interpreter(Manager aManager, StateKeeper keeper) {
    theManager   = aManager;

    keeper.register(this);
    String info = keeper.lookupInfo(this, infoKey);
    if (info != null) {
      StringTokenizer tok = new StringTokenizer(info);
      try {
	year   = Integer.parseInt(tok.nextToken());
	month  = Integer.parseInt(tok.nextToken());
	day    = Integer.parseInt(tok.nextToken());
	hour   = Integer.parseInt(tok.nextToken());
	minute = Integer.parseInt(tok.nextToken());
      }
      catch (NoSuchElementException e) {
	Log.err("Falta informação de tempo para o Interpreter no StateKeeper");
      }
      catch (NumberFormatException e) {
	Log.err("Interpreter recebendo informação de tempo inválida: '" + info + "'");
      }
    }
  }

  void enqueueTask(Task aTask) {
    theTasks.insert(aTask); // TaskQueue e' synchronized
  }

  void enqueueRequest(Request aRequest) {
    theRequests.insert(aRequest); // TaskQueue e' synchronized
  }

    Scheduler getScheduler() {
	return theScheduler;
    }

  private void nextYear() {
    ++year;
    theManager.yearAction();
  }

  private void nextMonth() {
    ++month;
    if (month > 12) {
      month = 1;
      nextYear();
    }
    theManager.monthAction();
  }

  private void nextDay() {
    ++day;
    if (day > 30) {
      day = 1;
      nextMonth();
    }
    theManager.dayAction();
  }

  private void nextHour() {
    ++hour;
    if (hour > 23) {
      hour = 0;
      nextDay();
    }
    theManager.hourAction();
  }

  private void nextMinute() {
    ++minute;
    if (minute > 59) {
      minute = 0;
      nextHour();
    }
    theManager.minuteAction();
  }

  private void nextTick() {
    ++pulses;
    if (pulses >= PULSES_PER_SEC) {
      pulses = 0;
      nextMinute();
    }
    theManager.tickAction();
  }

  int getYear() {
    return year;
  }

  int getMonth() {
    return month;
  }

  int getDay() {
    return day;
  }

  int getHour() {
    return hour;
  }

  int getMinute() {
    return minute;
  }

  public void run () {

      theManager.suspendWhileEmpty();

    while (interpreting) {
      try {
	sleep(SLEEP_TIME);
      }
      catch(InterruptedException e) {
      }

      // perform Tasks:

      while (!theTasks.isEmpty())
	((Task) theTasks.remove()).execute();

      // verify pending Events:

      theScheduler.treatEvents(JMud.getClock().getDate());

      // perform Requests:

      theManager.feedInterpreter(); // obtem 1 request de cada cliente

      // executa todos os requests

      while (!theRequests.isEmpty()) {
	  Request req = (Request) theRequests.remove();

	  String  line  = req.getCommandLine();
	  Char    ch    = req.getChar();

	  Log.debug("Interpreter: " + ch.getName() + " {" + line + "}");

	  CommandTokenizer toker = new CommandTokenizer(line);
	  String word = toker.nextToken();

	  Command cmd = CommandTable.findCommandByName(ch, word);
	  Command.execute(cmd, ch, toker, word);
      }

      // executa ações dependentes de tempo

      nextTick();

    }

    Log.info("Interpretador terminado");
  }

  private String stateInfo() {
    return String.valueOf(year) + " " + String.valueOf(month) + " " + String.valueOf(day) + " " + String.valueOf(hour) + " " + String.valueOf(minute);
  }

  ///////////////////////////////
  // Implementação de StateUser:

  public String getStateUserId() {
    return stateUserId;
  }

  public void updateStateKeeper(StateKeeper keeper) {
    keeper.updateInfo(this, infoKey, stateInfo());
  }

  //
  ///////////////////////////////
}


class InterpreterSuspender {
    private int numberOfClients = 0;

    synchronized void blockWhileEmpty() {
	try {
	    Log.info("Interpretador indo dormir");
	    wait();
	    Log.info("Interpretador acordando");
	}
	catch (InterruptedException e) {
	}
    }

    synchronized void oneMoreClient() {
	++numberOfClients;
	Log.debug("Número de clientes: " + numberOfClients);

        notifyAll();
    }

    synchronized void oneLessClient() {
	--numberOfClients;
	Log.debug("Número de clientes: " + numberOfClients);

        if (numberOfClients == 0)
	    blockWhileEmpty();
    }

    int getNumberOfClients() {
	return numberOfClients;
    }
}


