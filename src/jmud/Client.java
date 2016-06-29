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

// 	$Id: Client.java,v 1.1.1.1 2001/11/14 09:01:49 fmatheus Exp $

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;

import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.log.Log;
import jgp.container.Queue;
import jgp.container.Vector;

public class Client extends Thread {

    private static Manager theManager = null;

    public static final int TERM_WIDTH       = 80;  // colunas
    public static final int HOST_NAME_LENGTH = 30;  // caracteres
    public static final int MAX_NAME_LENGTH  = 10;  // caracteres
    public static final int MAX_INPUT_LENGTH = 256; // caracteres
    public static final int MAX_CMD_SLOTS    = 10;  // comandos
    public static final int MAX_NOTE_SIZE    = 20;  // linhas

  static void setManager(Manager aManager) {
    theManager = aManager;
  }

    private Socket         clientSocket = null;
    private Char           theChar      = null;
    private BufferedReader is           = null;
    private BufferedWriter os           = null;
    private String         hostName     = null;
    private String         clientName   = null;
    private boolean        playing;
    private Counter        cmdSlots     = new Counter();
    private Counter        delaySlots   = new Counter();
    private Queue          commands     = new Queue();
    private String         lastCommand  = ""; // DEVE ser string vazia
    private int            maxSpam;
    private int            spam         = 0;
    //private String         msgBuffer    = "";
    private Vector         msgBuffer    = new Vector();
    private boolean        prompt       = false;

    private TextBuffer     text         = new TextBuffer();

    // usado para que um cliente duplicado espere
    // que o anterior saia antes de entrar
    private Monitor        holder       = null;

  Client(Socket aSocket)
    throws IOException {
    clientSocket = aSocket;
    is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    os = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    hostName = clientSocket.getInetAddress().getHostName();
    maxSpam = Global.getConfig().getMaxSpam();
  }

    void setHolder(Monitor mon) {
	holder = mon;
    }

    ////////////
    // Annotate:

    public void lookNote() {
	String note = "";
	int i = 1;
	for (Enumeration enum = text.elements(); enum.hasMoreElements(); ++i) {
	    String str = (String) enum.nextElement();
	    note += Separators.NL + StrUtil.formatNumber(i, 2) + ": " + str;
	}

	send("Anotações:" + (note.length() == 0 ? (Separators.NL + "Nenhuma") : note));
    }

    public String getNoteText() {
	return text.toString();
    }

    public void addNoteLine(String str) {
	text.insertAtEnd(str);
    }

    public void remNoteLine() {
	text.removeFromEnd();
    }

    public void clearNote() {
	text.clear();
    }

    public int getNoteSize() {
	return text.getSize();
    }

    public void buildNote(String txt) {
	text.buildFrom(txt);
    }

    //
    ////////////

    public Char getChar() {
	return theChar;
    }

    void setChar(Char ch) {
	theChar = ch;
    }

    Player getPlayer() {
	return (Player) getChar();
    }

  String getUserName() {
    return clientName + " [" + getId() + "] (" + hostName + ")";
  }

  String getHostName() {
    return hostName;
  }

  boolean hasName(String name) {
    return clientName.equalsIgnoreCase(name);
  }

  int getId() {
    Player plr = getPlayer();
    return plr == null ? -1 : plr.getId();
  }

    void setId(int id) {
	getPlayer().setId(id);
    }

  void rawSend(String msg) {
    try {
      os.write(msg);
      os.flush();
    }
    catch(IOException e) {
      // Log não pode ser usado, pois ele próprio usa "send"
      // (para fazer broadcast do erro aos receptores de log)
    }
  }

    /*
  void flush() {
    if (msgBuffer.length() > 0) {
      rawSend(msgBuffer + Separators.NL + getChar().getPrompt());
      msgBuffer = "";
    }
    else if (prompt) {
      rawSend(getChar().getPrompt());
      prompt = false;
    }
  }

    // Envia mensagem.
    void send(String msg) {
	msgBuffer += msg;
    }
    */

    void flush() {
	int size = msgBuffer.getSize();
	if (size > 0) {
	    for (int i = 0; i < size; ++i) {
		rawSend((String) msgBuffer.getElementAt(i));
		msgBuffer.setElementAt(0, null);
	    }
	    rawSend(Separators.NL);
	    rawSend(getChar().getPrompt());
	    msgBuffer.removeAll();
	}
	else if (prompt) {
	    rawSend(getChar().getPrompt());
	    prompt = false;
	}
    }

    void send(String msg) {
	msgBuffer.insert(msg);
    }

    String receive() {
      String line = null;

      try {
	  line = is.readLine();

	  // conexao perdida e' tratada no laco que envolve o receive
	  if (line == null)
	      return null;
      }
      catch(IOException e) {
	  Log.err("Conexão perdida: " + getUserName());
	  return null;
      }


      int len = line.length();

      if (len > 0) {
	  byte[] array = line.getBytes();

	  if (array[0] == Telnet.IAC)
	      line = line.substring(3);
      }

      if (len > MAX_INPUT_LENGTH) {
	  Log.warn("Truncando entrada muito longa para: " + getUserName());
	  line = line.substring(0, MAX_INPUT_LENGTH);
      }

      return line;
    }

  void closeSocket() {
    Log.info("Fechando conexão para: " + getUserName());

    try {
      clientSocket.close();
    }
    catch(IOException e) {
      Log.err("Falha ao fechar socket: " + getUserName());
    }
  }

    public void disconnect() {
	closeSocket();
    }

  void reject() {
    theManager.retreat(this);
    disconnect();
  }

  boolean isValidName(String name) {
      if (name == null)
	  return false;

      int len = name.length();

    if (len < 1 || len > MAX_NAME_LENGTH)
      return false;

    for (int i = 0; i < len; ++i) {
      char ch = name.charAt(i);
      if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z'))
	return false;
    }

    return true;
  }

    public void saveChar() {
	// Quando o Mud capota e o personagem esta' salvo,
	// mas o estado nao, ocorrem problemas de inconsistencia.
	// A linha a seguir garante que o estado
	// sempre esteja consistente com os personagens salvos
	// mesmo em caso de crash.
	theManager.saveState();

	Player plr = getPlayer();
	Place plc = plr.getPlace();

	Log.debug("Gravando usuário: " + getUserName() + ", sala: " + (plc == null ? -1 : plc.getId()));

	try {
	    Player.save(plr);
	}
	catch (IOException e) {
	    Log.err("Falha ao gravar usuário: " + getUserName());
	}

	Log.debug("Gravando itens para: " + getUserName());

	plr.saveItems(Global.getConfig());
    }

    public void addDelay(int delay) {
	delaySlots.inc(delay);
    }

  synchronized private void addCmd(String cmd) {
    commands.insert(cmd);
  }

  synchronized private Request getCmd() {
    return commands.isEmpty() ? null : new Request(getChar(), (String) commands.remove());
  }

  Request oneRequest() {
      if (delaySlots.decIfGreaterThan(0))
	  return null;

      Request req = getCmd(); // synchronized: commands
      if (req != null)
	  cmdSlots.dec();

      return req;
  }

    public int getFreeCommandSlots() {
	return MAX_CMD_SLOTS - cmdSlots.getValue();
    }

  private void treatCommand(String cmd) {

      if (cmdSlots.incIfLessThan(MAX_CMD_SLOTS))
	  addCmd(cmd);          // synchronized: commands
      else
	  Log.warn("Estouro da fila de comandos: " + getChar().getName() + " {" + cmd + "}");
  }

    public void parse(String cmdLine) {

	Log.debug("Client: " + getChar().getName() + " {" + cmdLine + "}");

	boolean repeat = cmdLine.startsWith("!");

	// joga brancos fora
	cmdLine = cmdLine.trim();

	boolean empty = cmdLine.length() == 0;

	// dificulta spamming
	if (empty || repeat || cmdLine.equals(lastCommand)) {
	    if (++spam == maxSpam)
		Log.debug("Abuso: " + getUserName());
	    if (spam >= maxSpam)
		return;                     // ou alguma maldade calculista
	}
	else
	    spam = 0;

	// ignora comando vazio
	if (empty) {
	    prompt = true;
	    return;
	}

	// repete último comando
	if (repeat)
	    cmdLine = lastCommand;
	else
	    lastCommand = cmdLine;

	// extrai os comandos de uma linha separados por ';' (ponto-e-vírgula)
	for (StringTokenizer cmds = new StringTokenizer(cmdLine, ";") ; cmds.hasMoreTokens(); ) {
	    String cmd = cmds.nextToken().trim();
	    if (cmd.length() != 0)
		treatCommand(cmd);
	}
    }


    boolean newPassphrase(String pass) {
	if (pass == null)
	    return false;

	String encoded = StrUtil.cryptoHash(pass);
	((Player) getChar()).setPassPhrase(encoded);

	return true;
    }

    boolean checkPassphrase(String pass) {
	if (pass == null)
	    return false;

	String encoded = StrUtil.cryptoHash(pass);

	return encoded.equals(((Player) getChar()).getPassPhrase());
    }

    private Player bindPlayer(Player pl) {
	pl.setClient(this);
	return pl;
    }

    private Player loaded(Player pl) {
	if (pl == null)
	    return null;

	return bindPlayer(pl);
    }

    Player loadPlayer(int playerId) {
	return loaded(Player.load(playerId));
    }

    Player loadPlayer(String playerName) {
	return loaded(Player.load(playerName));
    }

    Player createPlayer(String playerName) {
	return bindPlayer(Player.create(playerName));
    }

  public void run() {
    Log.info("Nova conexão de: " + hostName);

    rawSend(Separators.NL + StrUtil.centerPad(JMud.getVersion(), TERM_WIDTH) + Separators.NL);

    String subversion = Global.getSubversion();
    if (subversion != null)
	rawSend(StrUtil.centerPad(subversion, TERM_WIDTH) + Separators.NL);

    rawSend(Separators.NL + StrUtil.centerPad("Por:", TERM_WIDTH) + Separators.NL + StrUtil.centerPad("Éverton da Silva Marques <evertonm@users.sourceforge.net>", TERM_WIDTH) + Separators.NL +  StrUtil.centerPad("Fabrício Matheus Gonçalves <fmatheus@users.sourceforge.net>", TERM_WIDTH) + Separators.NL + Separators.NL);

    rawSend(Separators.NL + StrUtil.centerPad("Bem-vindo!", TERM_WIDTH) + Separators.NL + Separators.NL +
	    "Nome: ");


    //
    // Obtém nome do personagem
    //
    clientName = receive(); // possivel null e'tratado em isValidName()

    if (!isValidName(clientName)) {
      Log.info("Cliente com nome inválido: '" + getUserName() + "'");
      rawSend("Nome inválido." + Separators.NL);
      reject();
      return;
    }

    clientName = Character.toUpperCase(clientName.charAt(0)) + clientName.substring(1);

    //
    // Procura arquivo do personagem
    //
    Player pl = loadPlayer(clientName);

    //
    // Novos usuários podem ser criados?
    //
    if (pl == null && theManager.getNewbieLock() != null) {
      Log.info("Impedindo criação de novo personagem: " + getUserName());
      rawSend(Separators.NL + "A criação de novos personagens está desabilitada." + Separators.NL);
      reject();
      return;
    }

    //
    // Verifica a senha
    //
    boolean goodPass = false;

    // desabilita ecoamento dos caracteres
    rawSend(new String(TelnetMap.DO_ECHO_OFF));

    // novo usuário
    if (pl == null) {
	rawSend("Escolha uma senha: ");

	String pass = receive(); // possivel null e' tratado a seguir
	if (pass != null) {
	    rawSend(Separators.NL + "Redigite: ");

	    String again = receive(); // possivel null e' tratado a seguir
	    if (pass.equals(again)) {
		setChar(createPlayer(clientName));

		goodPass = newPassphrase(pass);
	    }

	    again = null;
	}
	pass = null;

    }

    // usuário conhecido
    else {
	setChar(pl);

	rawSend("Senha: ");

	goodPass = checkPassphrase(receive()); 	// possivel null e' tratado em checkPassphrase()
    }

    // reabilita ecoamento dos caracteres
    rawSend(new String(TelnetMap.DO_ECHO_ON));

    if (!goodPass) {
	Log.info("Cliente com senha inválida: " + getUserName());
	rawSend(Separators.NL + "Senha inválida." + Separators.NL);
	reject();
	return;
    }

    //
    // Certifica-se de que o personagem já não está em jogo
    //
    Client dup = theManager.findClientByName(clientName);
    if (dup != null) {

	int playerId = dup.getPlayer().getId();

	//
	// desconecta cliente duplicado
	//
	Log.info("Desconectando cliente duplicado: " + dup.getUserName());

	// solicita que o cliente duplicado avise quando terminar de sair
	Monitor mon = new Monitor();
	dup.setHolder(mon);

	// "pede" que o cliente duplicado saia
	dup.disconnect();

	// espera aviso de saida emitido pelo cliente duplicado
	mon.block();

	setChar(loadPlayer(playerId));
    }

    //
    // Devemos aceitar mais um cliente?
    //
    if (!getChar().isModerator() && !theManager.acceptOneMoreClient()) {
	Log.info("Sistema cheio rejeitando cliente: " + getUserName());
	rawSend(Separators.NL + "Sinto muito, sistema cheio. Tente mais tarde." + Separators.NL);
	reject();
	return;
    }

    //
    // Conecta o personagem
    //

    // registra host remoto utilizado
    getPlayer().logHost(hostName + "(" + clientSocket.getInetAddress().getHostAddress() + ")");

    // registra sessao
    theManager.logSession(clientName, hostName);

    // novo usuário
    if (pl == null) {
      setId(theManager.newClientId());
      theManager.addClientMapEntry(clientName, getId());
      Log.info("Novo usuário: " + getUserName());

      // garante que a senha entre em vigor imediatamente
      saveChar();

      theManager.play(this);                   // agenda BeginTask
    }

    // usuário conhecido
    else {
      Log.info("Usuário conectado: " + getUserName());
      theManager.play(this, getPlayer().getLoadRoom()); // agenda BeginTask
    }

    //
    // Mensagem do dia
    //
    rawSend(Separators.NL + theManager.getMotd());

    //
    // Carrega itens do usuário
    //

    Log.debug("Carregando itens para: " + getUserName());
    ((Player) getChar()).loadItems(Global.getConfig());

    //
    // Laço de atendimento ao usuário
    //
    for (; ; ) {
      String cmdLine = receive();

      if (cmdLine == null)
	break;

      parse(cmdLine);
      yield();          // dar uma chance aos outros (redundante)
    }

    //
    // Aqui o socket JÁ está desconectado
    //

    saveChar();

    theManager.quit(this); // agenda QuitTask

    if (holder != null)
	holder.release();
  }
}

