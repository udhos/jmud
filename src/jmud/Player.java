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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.util.Enumeration;

import jmud.job.Job;
import jmud.job.JobTable;
import jmud.ability.Ability;
import jmud.ability.AbilityTable;
import jmud.magic.Magic;
import jmud.magic.MagicTable;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.InvalidFlagException;
import jmud.util.StrUtil;
import jmud.util.Separators;
import jmud.util.pair.StrIntPair;
import jmud.util.bit.Bit;
import jmud.util.log.Log;
import jmud.util.color.Color;
import jmud.jgp.IdMatcher;
import jmud.jgp.StringMatcher;
import jmud.jgp.NameExtractor;
import jmud.jgp.StringExtractor;
import jgp.container.Vector;
import jgp.algorithm.Searcher;

public class Player extends Char {

    static final int MAX_RECENT_HOSTS = 5; // Maximo de maquinas a lembrar

    static final int P_DELETED = Bit.BIT0;
    static final int P_LOG     = Bit.BIT1;
    static final int P_COLOR   = Bit.BIT2;
    static final int P_OMNI    = Bit.BIT3;

    static final String prefLabels[] = {
	"apagado",
	"log",
	"cores",
	"onisciência"
    };

    static private PlayerCache cache = new PlayerCache(10);

    private int    theId       = -2;
    private String passPhrase  = null;
    private int    theRank     = R_USER;
    private int    preferences = 0;
    private int    roomId;
    private Vector recentHosts = new Vector(); // A ordem importa

    // Usar com cautela para nao alterar!
    public static PlayerCache getPlayerCache() {
	return cache;
    }

    // Apenas a classe PlayerCache deve usar este metodo.
    static Player rawLoad(int playerId) {
	Player pl = null;

	Log.debug("Buscando usuário em disco: [" + playerId + "]");

	try {
	    pl = new Player(playerId);
	}
	catch(IOException e) {
	    Log.err("Falha de E/S ao ler arquivo de usuário: " + playerId);
	    return null;
	}
	catch(InvalidFileFormatException e) {
	    Log.err("Arquivo de usuário com formato inválido: " + playerId);
	    return null;
	}

	return pl;
    }

    static Player load(int playerId) {
	return cache.lookup(new IdMatcher(playerId));
    }

    public static Player load(String name) {

	for (Enumeration enum = theManager.getClientMapEnum();
	     enum.hasMoreElements(); ) {
	    StrIntPair pair = (StrIntPair) enum.nextElement();
	    int playerId = pair.getInt();
	    if (name.equalsIgnoreCase(pair.getStr())) {
		Player plr = load(playerId);
		if (!plr.isDeleted() || plr.isFirstModerator())
		    return plr;
	    }
	}

	return null;
    }

    static Player create(String name) {
	return new Player(name);
    }

    static void save(Player plr) throws IOException {
	cache.update(new IdMatcher(plr.getId()), plr);
	plr.save();
    }

    static boolean sameName(String name1, String name2) {
	return name1.equalsIgnoreCase(name2);
    }

    // Novo jogador
  Player(String name) {
    theName   = name;
    money     = new Money();
  }

    // Jogador conhecido
  Player(int playerId)
    throws IOException, InvalidFileFormatException {

      setId(playerId);

      String id = String.valueOf(getId());

      LineReader reader = new LineReader(new FileReader(Global.getConfig().getUserFilesDir() + id));

      theName        = reader.readLine();
      passPhrase     = reader.readLine();

      theDescription = StrUtil.readDescription(reader);

      try {
	preferences  = StrUtil.parseFlags(reader.readLine());
      }
      catch(InvalidFlagException e) {
	throw new InvalidFileFormatException();
      }


      try {

	StringTokenizer toker = new StringTokenizer(reader.readLine());

	  roomId           = Integer.parseInt(toker.nextToken());
	  invisibilityRank = Integer.parseInt(toker.nextToken());

	  toker = new StringTokenizer(reader.readLine());

	theRank      = Integer.parseInt(toker.nextToken());
	theLevel     = Integer.parseInt(toker.nextToken());

	toker = new StringTokenizer(reader.readLine());

	thePosition  = Integer.parseInt(toker.nextToken());
	sex          = Integer.parseInt(toker.nextToken());

	toker = new StringTokenizer(reader.readLine());

	experience   = Integer.parseInt(toker.nextToken());
	totalExperience = Integer.parseInt(toker.nextToken());

	toker = new StringTokenizer(reader.readLine());

	goodBehavior = Integer.parseInt(toker.nextToken());
	badBehavior  = Integer.parseInt(toker.nextToken());

	toker = new StringTokenizer(reader.readLine());

	fights       = Integer.parseInt(toker.nextToken());
	kills        = Integer.parseInt(toker.nextToken());
        flees        = Integer.parseInt(toker.nextToken());
        deaths       = Integer.parseInt(toker.nextToken());

      for (int r = 0; r < 5; ++r) {
	  toker = new StringTokenizer(reader.readLine());
	  for (int c = 0; c < 4; ++c)
	      realAttr[r * 4 + c] = Integer.parseInt(toker.nextToken());
      }

      for (int i = 0; i < 20; ++i)
	  attr[i] = realAttr[i];

	toker = new StringTokenizer(reader.readLine());
	stam = Integer.parseInt(toker.nextToken());
	will = Integer.parseInt(toker.nextToken());
	soul = Integer.parseInt(toker.nextToken());
	conf = Integer.parseInt(toker.nextToken());
      }
      catch(NumberFormatException e) {
	throw new InvalidFileFormatException();
      }
      catch(NoSuchElementException e) {
	  throw new InvalidFileFormatException();
      }

      money = new Money(reader);

      recentHosts = StrUtil.tokenize(reader.readLine());

      StringTokenizer toker = new StringTokenizer(reader.readLine(), ",");
      while (toker.hasMoreTokens()) {
	  String name = toker.nextToken();
	  Log.debug("Magia: '" + name + "'");
	  Magic mag = MagicTable.findMagicByName(name);
	  if (mag == null) {
	      Log.warn("Ignorando carregamento da magia '" + name + "' não encontrada para: " + theName + " [" + id + "]");
	      continue;
	  }
	  learnSpell(mag);
      }

      //////
      // Job

      StringTokenizer tok = new StringTokenizer(reader.readLine(), ",");
      while (tok.hasMoreTokens()) {
	  String name = tok.nextToken();
	  Log.debug("Profissão: '" + name + "'");
	  StringTokenizer tk = new StringTokenizer(name, "=");
	  if (tk.countTokens() != 2) {
	      Log.err("Formato incorreto de profissão: '" + name + "'");
	      continue;
	  }

	  String jobName = tk.nextToken();
	  Job jb = JobTable.findJobByName(jobName);
	  if (jb == null) {
	      Log.warn("Ignorando carregamento da profissão '" + jobName + "' não encontrada para: " + theName + " [" + id + "]");
	      continue;
	  }

	  String jobLevel = tk.nextToken();
	  int level = 0;
	  try {
	      level = Integer.parseInt(jobLevel);
	  }
	  catch(NumberFormatException e) {
	      Log.err("Ignorando carregamento de profissão com nível não numérico: " + jobLevel);
	      continue;
	  }

	  joinJob(jb, level);
      }

      // Job
      //////

      //////////
      // Ability

      StringTokenizer tkzr = new StringTokenizer(reader.readLine(), ",");
      while (tkzr.hasMoreTokens()) {
	  String name = tkzr.nextToken();
	  Log.debug("Habilidade: '" + name + "'");
	  StringTokenizer tk = new StringTokenizer(name, "=");
	  if (tk.countTokens() != 2) {
	      Log.err("Formato incorreto de habilidade: '" + name + "'");
	      continue;
	  }

	  String abilityName = tk.nextToken();
	  Ability abl = AbilityTable.findAbilityByName(abilityName);
	  if (abl == null) {
	      Log.warn("Ignorando carregamento da habilidade '" + abilityName + "' não encontrada para: " + theName + " [" + id + "]");
	      continue;
	  }

	  String abilityLevel = tk.nextToken();
	  int level = 0;
	  try {
	      level = Integer.parseInt(abilityLevel);
	  }
	  catch(NumberFormatException e) {
	      Log.err("Ignorando carregamento de habilidade com nível não numérico: " + abilityLevel);
	      continue;
	  }

	  gainAbility(abl, level);
      }

      // Ability
      //////////

      String eof = reader.readLine();
      if (!eof.startsWith(Separators.EOF))
	throw new InvalidFileFormatException();

      reader.close();

      setId(playerId);

      Log.debug("Carregado usuário: " + theName + " [" + id + "], sala: " + roomId);
  }

  void save()
    throws IOException {
      BufferedWriter writer = new BufferedWriter(new FileWriter(Global.getConfig().getUserFilesDir() + String.valueOf(getId())));

      roomId = (thePlace == null) ? Room.NOWHERE : thePlace.getId();

      writer.write(theName);    writer.newLine();
      writer.write(passPhrase); writer.newLine();

      StrUtil.writeDescription(writer, theDescription);
      writer.write(StrUtil.makeFlags(preferences)); writer.newLine();
      writer.write(roomId + " " + invisibilityRank);   writer.newLine();

      writer.write(theRank + " " + theLevel);  writer.newLine();

      writer.write(thePosition + " " + sex); writer.newLine();

      writer.write(experience + " " + totalExperience); writer.newLine();

      writer.write(goodBehavior + " " + badBehavior); writer.newLine();

      writer.write(String.valueOf(fights) + " " + String.valueOf(kills) + " " + String.valueOf(deaths) + " " + String.valueOf(flees));   writer.newLine();

      for (int r = 0; r < 5; ++r) {
	  for (int c = 0; c < 4; ++c)
	      writer.write(realAttr[r * 4 + c] + " ");
	  writer.newLine();
      }

      writer.write(stam + " " + will + " " + soul + " " + conf); writer.newLine();

      money.save(writer);

      writer.write(StrUtil.headedConcat(recentHosts.elements(), " ")); writer.newLine();

      writer.write(StrUtil.headedConcat(spells.elements(), new NameExtractor(), ",")); writer.newLine();

      writer.write(StrUtil.headedConcat(jobs.elements(), new StringExtractor(), ",")); writer.newLine();

      writer.write(StrUtil.headedConcat(abilities.elements(), new StringExtractor(), ",")); writer.newLine();

      writer.write(Separators.EOF); writer.newLine();

      writer.close();
  }

    void logHost(String hostName) {
	String host = (String) Searcher.linearSearch(recentHosts, new StringMatcher(hostName));
	if (host == null) {
	    if (recentHosts.getSize() >= MAX_RECENT_HOSTS) {

		try {
		    recentHosts.remove(recentHosts.getElementAt(0));
		}
		catch(NoSuchElementException e) {
		    Log.err("Removendo primeira máquina não-existente para: " + getName());
		}
		catch(ArrayIndexOutOfBoundsException e) {
		    Log.err("Acessando primeira máquina não-existente para: " + getName());
		}
	    }

	    recentHosts.insert(hostName);
	}
    }

    public Enumeration getHostEnum() {
	return recentHosts.elements();
    }

  int getLoadRoom() {
    return roomId;
  }

  public String getBrief(boolean showId) {
      String brief = getName() + " está aqui.";
      if (showId)
	  brief = "[" + getId() + "] " + brief;
      return brief;
  }

  String getPrompt() {
    return "ID:"+ getId() + " P:" + theRank + " " + super.getPrompt();
  }

    /*
  boolean canExecute(Command cmd) {
    return theRank >= cmd.getMinRank();
  }
    */

    /*
  boolean canCast(Magic mag) {
    return true;
  }
    */

  public boolean hasName(String name) {
    return Player.sameName(getName(), name);
  }

    void setPassPhrase(String phrase) {
	passPhrase = phrase;
    }

    String getPassPhrase() {
	return passPhrase;
    }

    void turnPreference(int flag, boolean on) {
	if (on)
	    preferences = Bit.set(preferences, flag);
	else
	    preferences = Bit.reset(preferences, flag);
    }

    boolean queryPreference(int flag) {
	return Bit.isSet(preferences, flag);
    }

    public void delete() {
	preferences = Bit.set(preferences, P_DELETED);
    }

    public void undelete() {
	preferences = Bit.reset(preferences, P_DELETED);
    }

    public boolean isDeleted() {
	return Bit.isSet(preferences, P_DELETED);
    }

    public void startReceiveLog() {
	preferences = Bit.set(preferences, P_LOG);
    }
    public void stopReceiveLog() {
	preferences = Bit.reset(preferences, P_LOG);
    }
    public boolean isReceivingLog() {
	return Bit.isSet(preferences, P_LOG);
    }

    public void colorOn() {
	turnPreference(P_COLOR, true);
    }

    public void colorOff() {
	turnPreference(P_COLOR, false);
    }

    public boolean useColor() {
	return queryPreference(P_COLOR);
    }

    public String getColor(int color) {
	return useColor() ? Color.getVT100(color) : "";
    }

    public String buildColor(int colorVector) {
	return useColor() ? Color.buildVT100(colorVector) : "";
    }

    public String getNormalColor() {
	return useColor() ? Color.FORCE_NORMAL : "";
    }


    public boolean isOmni() {
	return queryPreference(P_OMNI);
    }

    public void turnOmni(boolean bool) {
	turnPreference(P_OMNI, bool);
    }

  boolean hasId(int id) {
    return id == getId();
  }

    public int getRank() {
	return theRank;
    }

    public void promote(int aRank) {
	theRank = aRank;
    }

    /*
  boolean isCloneOf(Clone cl) {
    return false;
  }
    */

    public boolean isPlayer() {
	return true;
    }

    void setId(int id) {
	theId = id;
    }

  /////////
  // Owner:

  public int getId() {
    return theId;
  }

  public String getOwnerType() {
    return "jogador";
  }

  //
  /////////

    public Client getClient() {
	return theClient;
    }

    void setClient(Client clnt) {
	theClient = clnt;
    }

    String getHostName() {
	return getClient().getHostName();
    }

    public boolean isFirstModerator() {
	return getId() == 0;
    }

  /////////////
  // Sheetable:

  public void sendSheet(Char rcpt) {
      String prefs = StrUtil.listFlags(preferences, prefLabels);

    rcpt.send("JOGADOR - ID: [" + getId() + "]  Posto: " + getRankName() +  " [" + getRank() + "]  Nível: [" + theLevel  + "]"+ Separators.NL +
      "Nome: [" + theName + "]  Máquina: [" + getHostName() + "]" + Separators.NL +
	"Preferências: " + (prefs == "" ? "Nenhuma" : prefs) + Separators.NL +
      "Onde: " + getWhere());

    super.sendSheet(rcpt);
  }

  //
  /////////////

    void saveItemVerbatim(Item curr, BufferedWriter writer)
	throws IOException {
	if (curr.isVolatile()) {
	    Log.debug("Item VOLÁTIL [" + curr.getId() + "] não salvo para: " + getName());
	    return;
	}

	if (curr.isStatic()) {
	    Log.debug("Item estático [" + curr.getId() + "] salvo para: " + getName());
	    writer.write(Separators.BOR + curr.getId()); writer.newLine();
	}
	else {
	    Log.debug("Item salvo [" + curr.getId() + "] para: " + getName());
	    curr.save(writer);
	}

	writer.write(Separators.EOR); writer.newLine();
    }

    void saveItemContents(Item curr, BufferedWriter writer)
	throws IOException {
	Owner ow = curr.getContainer();
	if (ow != null)
	    for (Enumeration itemEnum = ow.getContents(); itemEnum.hasMoreElements(); ) {
		Item it = (Item) itemEnum.nextElement();
		saveItem(it, writer);
	    }
    }

    void saveItem(Item curr, BufferedWriter writer)
	throws IOException {
	saveItemVerbatim(curr, writer);
	saveItemContents(curr, writer);
    }

    void saveInventory(BufferedWriter writer)
	throws IOException {
	for (Enumeration items = theItems.elements(); items.hasMoreElements(); ) {
	    Item curr = (Item) items.nextElement();
	    saveItem(curr, writer);
	}
    }

    void saveEquipment(BufferedWriter writer)
	throws IOException {

	// visitar todas as posicoes de equipamento
	int wearFlags = 0;
	int currFlag  = 1;
	for (int i = 0; i < Item.WEAR_POSITIONS; ++i, currFlag <<= 1)

	    // so' interessa se estiver ocupada e ainda nao visitada
	    if (theEq[i] != null && !Bit.isSet(wearFlags, currFlag)) {

		// salvo o equipamento
		saveItem(theEq[i], writer);

		// marco como posicao visitada
		wearFlags = Bit.set(wearFlags, currFlag);

		// verifico se pode estar em multiplas posicoes
		if (theEq[i].wearAll()) {
		    int auxFlag = currFlag << 1;

		    // marco cada posicao que ocupa
		    for (int j = i + 1; j < Item.WEAR_POSITIONS; ++j, auxFlag <<= 1)
			if (theEq[j] == theEq[i])
			    wearFlags = Bit.set(wearFlags, auxFlag);
		}
	    }
    }

    void saveItems(BufferedWriter writer)
	throws IOException {
	saveInventory(writer);
	saveEquipment(writer);
	writer.write(Separators.EOF); writer.newLine();
    }

    void saveItems(String fileName) {
	BufferedWriter itemFile = null;

	try {
	    itemFile = new BufferedWriter(new FileWriter(fileName));
	}
	catch(IOException e) {
	    Log.err("Falha ao criar arquivo de itens: " + fileName);
	    return;
	}

	try {
	    saveItems(itemFile);
	}
	catch(IOException e) {
	    Log.err("Falha ao gravar arquivo de itens: " + fileName);
	}

	try {
	    itemFile.close();
	}
	catch(IOException e) {
	    Log.warn("Falha ao fechar arquivo de itens: " + fileName);
	}
    }

    void saveItems(Config cfg) {
	saveItems(cfg.getItemFilesDir() + getId());
    }

  // ignora todas as linhas ate' o comeco do proximo item
  private String findNextRecord(LineReader reader)
    throws InvalidFileFormatException, IOException {
    for ( ; ; ) {
      if (reader.eos())
	throw new InvalidFileFormatException();

      String itemId = reader.readLine();
      if (itemId.startsWith(Separators.EOF) ||
	  itemId.startsWith(Separators.BOR))
	return itemId;
    }

  }

    void loadItems(LineReader reader)
	throws InvalidFileFormatException, IOException {

	FOR: for ( ; ; ) {
	    if (reader.eos())
		throw new InvalidFileFormatException();

	    String itemId = reader.readLine();
	    ItemProto itPro = null;
	    int id = -1;

	    boolean repeat;

	    do {
		repeat = false;

		if (itemId.startsWith(Separators.EOF))
		    break FOR;
		if (!itemId.startsWith(Separators.BOR))
		    throw new InvalidFileFormatException();

		try {
		  id = Integer.parseInt(itemId.substring(1));
		  itPro = theWorld.findItemProtoById(id);
		}
		catch(NoSuchElementException e) {
		    Log.warn("Ignorando item sem protótipo [" + id + "] carregado para: " + getName());
		    itemId = findNextRecord(reader);
		    repeat = true;
		}
		catch(NumberFormatException e) {
		  Log.warn("Ignorando item com ID inválido '" + itemId + "' para: " + getName());
		  itemId = findNextRecord(reader);
		  repeat = true;
		}
	    } while (repeat);

	    Item it = null;

	    if (itPro != null)
		if (itPro.isVolatile()) {
		    Log.warn("Ignorando item volátil [" + id + "] carregado para: " + getName());
		}
		else if (itPro.isStatic()) {
		    it = itPro.create();
		}
		else {
		    it = Item.load(id, reader, itPro);
		}

	    // Entrega o item para o usuario
	    if (it != null)
		theWorld.insertItem(it, this);

	    String eor = reader.readLine();
	    if (!eor.startsWith(Separators.EOR))
		throw new InvalidFileFormatException();
	}
    }

    void loadItems(String fileName) {
	LineReader itemFile = null;
	try {
	    itemFile = new LineReader(new FileReader(fileName));
	}
	catch(FileNotFoundException e) {
	    Log.warn("Arquivo de items não encontrado: " + fileName);
	    return;
	}
	catch(IOException e) {
	    Log.warn("Falha ao ler arquivo de itens: " + fileName);
	    return;
	}

	try {
	    loadItems(itemFile);
	}
	catch(InvalidFileFormatException e) {
	    Log.err("Arquivo de itens com formato inválido: " + fileName);
	}
	catch(IOException e) {
	    Log.err("Falha ao carregar arquivo de itens: " + fileName);
	}

	try {
	    itemFile.close();
	}
	catch(IOException e) {
	    Log.warn("Falha ao fechar arquivo de itens: " + fileName);
	}
    }

    void loadItems(Config cfg) {
	loadItems(cfg.getItemFilesDir() + getId());
    }

    public Authenticable getCredentials() {
	return new PlayerCredentials(this);
    }
}
