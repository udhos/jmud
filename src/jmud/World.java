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

// 	$Id: World.java,v 1.1.1.1 2001/11/14 09:01:50 fmatheus Exp $

package jmud;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import jgp.container.Vector;
import jgp.container.Set;
import jgp.container.List;
import jgp.algorithm.QuickSorter;
import jgp.algorithm.Searcher;
import jgp.adaptor.Finder;
import jmud.jgp.Enchainer;
import jmud.jgp.NameMatcher;
import jmud.jgp.NameExtractor;
import jmud.jgp.VisibilityNameMatcher;
import jmud.jgp.IdMatcher;
import jmud.jgp.IdComparator;
import jmud.jgp.IdRangeVerifier;
import jmud.jgp.IdTitleFormatter;
import jmud.jgp.IdLessThanComparator;
import jmud.jgp.CreatureIdMatcher;
import jmud.jgp.VisibilityIdMatcher;
import jmud.jgp.VisibilityCreatureIdMatcher;
import jmud.jgp.PlayerVisibilityVerifier;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.command.IndexedToken;

public class World {

    private Manager    theManager     = null;
    private Vector     theZones       = new Vector();
    private Vector     theRooms       = new Vector();
    private Set        thePlayers     = new Set();
    private Set        theChars       = new Set();
    private Vector     theItemProtos  = new Vector();
    private Set        theItems       = new Set();
    private Vector     theCreatProtos = new Vector();
    private Vector     theShops       = new Vector();
    private Vector     theBoards      = new Vector();
    private Set        theMasters     = new Set();

    private int    theId;
    private String theTitle  = null;
    private Room   startRoom = null;

    static final int B_ADMIN    = 0;
    static final int B_IMMORTAL = 1;
    static final int B_SOCIAL   = 2;


    World(Manager aManager) {
	theManager     = aManager;

	String worldFileName = Global.getConfig().getWorldDir() + Global.getConfig().getWorldFileName();

	LineReader worldFile = null;
	try {
	    worldFile = new LineReader(new FileReader(worldFileName));

	    String idStr = worldFile.readLine();

	    try {
		theId = Integer.parseInt(idStr.substring(1));
	    }
	    catch(NumberFormatException e) {
		Log.abort("Mundo com ID inválido: '" + idStr + "'");
	    }

	    theTitle = worldFile.readLine();

	    Log.info("Mundo: " + theTitle);

	    while (!worldFile.eos()) {
		StringTokenizer tok = new StringTokenizer(worldFile.readLine());

		String zoneName = tok.nextToken();
		String zoneDir = Global.getConfig().getWorldDir() + (tok.hasMoreTokens() ? tok.nextToken() : "");

		if (zoneName.startsWith(Separators.EOR))
		    break;
		try {
		    theZones.insert(new Zone(this, zoneName, zoneDir));
		}
		catch(InvalidFileFormatException e) {
		    Log.abort("Formato inválido do arquivo de zona: " + zoneName);
		}
	    }

	    Log.info("Comprimindo vetor de zonas");
	    theZones.trim();
	    Log.info("Ordenando zonas");
	    (new QuickSorter(theZones, new IdLessThanComparator())).sort();

	    Zone zn = findZoneById(Global.getConfig().getStartZone());
	    if (zn == null)
		Log.err("Zona inicial default não encontrada");
	    else
		startRoom = (Room) Searcher.linearSearch(theRooms, new IdMatcher(zn.getBase() + Global.getConfig().getStartRoom()));
	    if (startRoom == null) {
		Log.err("Sala inicial default não encontrada");
		startRoom = new Room(null, 0, 0, "Limbo", "limbo", "Aqui é o limbo." + Separators.NL);
		Log.warn("Inserindo sala inicial internamente");
		insertRoom(startRoom);
	    }
	    else
		Log.warn("Suprimindo geração interna de sala inicial");

	    Log.info("Comprimindo vetor de salas");
	    theRooms.trim();
	    Log.info("Ordenando salas");
	    (new QuickSorter(theRooms, new IdLessThanComparator())).sort();

	    while (!worldFile.eos()) {
		String line = worldFile.readLine();
		if (line.startsWith(Separators.EOF))
		    break;
		StringTokenizer tok = new StringTokenizer(line);

		String dir  = tok.nextToken();

		Log.info("Porta interzona: " + line);

		int srcZone;
		int srcRoom;
		int trgZone;
		int trgRoom;

		try {

		    srcZone = Integer.parseInt(tok.nextToken());
		    srcRoom = Integer.parseInt(tok.nextToken());
		    trgZone = Integer.parseInt(tok.nextToken());
		    trgRoom = Integer.parseInt(tok.nextToken());

		}
		catch(NumberFormatException e) {
		    Log.err("Porta interzona inválida: " + line);
		    continue;
		}

		Zone z1 = findZoneById(srcZone);
		if (z1 == null) {
		    Log.err("Zona de origem não existe.");
		    break;
		}
		Room r1 = findRoomById(srcRoom + z1.getBase());
		if (r1 == null) {
		    Log.err("Sala de origem não existe.");
		    break;
		}
		Zone z2 = findZoneById(trgZone);
		if (z2 == null) {
		    Log.err("Zona de destino não existe.");
		    break;
		}
		if (r1.superDoorExists(dir))
		    Log.err("Colisão de porta inter-zonas: " + line);
		else {
		    try {
			r1.insertDoor(new SuperDoor(z2.getBase(), dir, trgRoom));
		    }
		    catch(NoSuchElementException e) {
			Log.err("Porta inter-zona com direção inválida: " + line);
		    }
		}
	    }

	    worldFile.close();

	    Log.info("Comprimindo vetor de ítens");
	    theItemProtos.trim();
	    Log.info("Ordenando ítens");
	    (new QuickSorter(theItemProtos, new IdLessThanComparator())).sort();

	    Log.info("Comprimindo vetor de criaturas");
	    theCreatProtos.trim();
	    Log.info("Ordenando criaturas");
	    (new QuickSorter(theCreatProtos, new IdLessThanComparator())).sort();

	    Log.info("Comprimindo vetor de lojas");
	    theShops.trim();
	    Log.info("Ordenando lojas");
	    (new QuickSorter(theShops, new IdLessThanComparator())).sort();

	    adjustDoors();

	    /*
	      cleanRooms();

	      checkExits();
	      checkReaching();
	    */

	}
	catch(FileNotFoundException e) {
	    Log.abort("Arquivo de mundo não encontrado: " + worldFileName);
	}
	catch(IOException e) {
	    Log.abort("Falha de E/S ao ler arquivo de mundo: " + worldFileName);
	}

	loadBoards();

    }

    Room getStartRoom() {
	return startRoom;
    }

    void loadBoards() {

	theBoards.insert(new Board(B_ADMIN, "Administrativo", Char.R_DISABLED, Char.R_DISABLED, Char.R_MODERATOR, Global.getConfig()));
	theBoards.insert(new Board(B_IMMORTAL, "Divino", Char.R_ADMIN, Char.R_ADMIN, Char.R_ADMIN, Global.getConfig()));
	theBoards.insert(new Board(B_SOCIAL, "Social", Char.R_GUEST, Char.R_USER, Char.R_ADMIN, Global.getConfig()));
	theBoards.trim();
    }

    void saveBoards() {
	Log.debug("Gravando quadros");

	for (int i = 0; i < theBoards.getSize(); ++i)
	    ((Board) theBoards.getElementAt(i)).save(Global.getConfig());
    }

    Board findBoardById(int id) {
	int ind = Searcher.binarySearch(theBoards, new IdComparator(id));
	if (ind == -1)
	    return null;
	return (Board) theBoards.getElementAt(ind);
    }

    public void resetZones() {
	Log.info("Automatizando mundo");
	for (Enumeration zones = theZones.elements(); zones.hasMoreElements(); ) {
	    Zone curr = (Zone) zones.nextElement();
	    curr.reset();
	}
    }

    void checkResets() {
	for (Enumeration zones = theZones.elements(); zones.hasMoreElements(); ) {
	    Zone curr = (Zone) zones.nextElement();
	    curr.tick();
	    if (curr.isTimeToReset())
		curr.reset();
	}
    }


    public Zone findZoneById(int zoneId) {
	int ind = Searcher.binarySearch(theZones, new IdComparator(zoneId));
	if (ind == -1)
	    return null;
	return (Zone) theZones.getElementAt(ind);
    }

    public String getZoneSheet() {
	String list = "";
	for (Enumeration zones = theZones.elements(); zones.hasMoreElements(); ) {
	    Zone curr = (Zone) zones.nextElement();
	    list += Separators.NL + curr.getSheet();
	}
	return list;
    }

    void sendToOpenPlace(String msg) {
	for (Enumeration enum = thePlayers.elements(); enum.hasMoreElements(); ) {
	    Char ch = (Char) enum.nextElement();
	    if (ch.getPlace().isOpen())
		ch.send(msg);
	}
    }

    void sendToPlayers(String msg) {
	for (Enumeration enum = thePlayers.elements(); enum.hasMoreElements(); ) {
	    Char ch = (Char) enum.nextElement();
	    ch.send(msg);
	}
    }

    void insertRoom(Room newRoom) {
	theRooms.insert(newRoom);
    }

    public Room findRoomById(int roomId) {
	int ind = Searcher.binarySearch(theRooms, new IdComparator(roomId));
	if (ind == -1)
	    return null;
	return (Room) theRooms.getElementAt(ind);
    }

    /*
      String listRoomsById(int firstId, int lastId) {
      String list = "";
      for (Enumeration rooms = theRooms.elements(); rooms.hasMoreElements(); ) {
      Room curr = (Room) rooms.nextElement();
      int id = curr.getId();
      if (id >= firstId && id <= lastId)
      list += Separators.NL + StrUtil.formatNumber(curr.getId(), 5, ' ') + " " + curr.getTitle();
      }
      return list;
      }
    */

    public void listRoomsById(Char rcpt, int firstId, int lastId) {
	Enchainer.wrapNone(Keywords.NONE1, rcpt, Enchainer.listIf(rcpt, theRooms.elements(), new IdTitleFormatter(5, ' '), Separators.NL, new IdRangeVerifier(firstId, lastId)));
    }

    /*
      String listRoomsByName(String name) {
      String list = "";
      for (Enumeration rooms = theRooms.elements(); rooms.hasMoreElements(); ) {
      Room curr = (Room) rooms.nextElement();
      if (curr.hasName(name))
      list += Separators.NL + StrUtil.formatNumber(curr.getId(), 5, ' ') + " " + curr.getTitle();
      }
      return list;
      }
    */

    public void listRoomsByName(Char rcpt, String name) {
	Enchainer.wrapNone(Keywords.NONE1, rcpt, Enchainer.listIf(rcpt, theRooms.elements(), new IdTitleFormatter(5, ' '), Separators.NL, new NameMatcher(name)));
    }

    void insertShop(Shop sh) {
	theShops.insert(sh);
    }

    public Shop findShopById(int shId) {
	int ind = Searcher.binarySearch(theShops, new IdComparator(shId));
	if (ind == -1)
	    return null;
	return (Shop) theShops.getElementAt(ind);
    }

    Shop findShopByKeeperId(int keeperId) {
	for (Enumeration shops = theShops.elements(); shops.hasMoreElements(); ) {
	    Shop sh = (Shop) shops.nextElement();
	    if (sh.getKeeperId() == keeperId)
		return sh;
	}
	return null;
    }

    public String getShopList() {
	String list = "";
	for (Enumeration shops = theShops.elements(); shops.hasMoreElements(); ) {
	    Shop sh = (Shop) shops.nextElement();
	    list += Separators.NL + sh.getShortSheet();
	}
	return list;
    }

    /////////
    // Master

    void insertMaster(Master mstr) {
	theMasters.insert(mstr);
    }

    public Enumeration getMasterList() {
	return theMasters.elements();
    }

    public Master findMasterById(int id) {
	return (Master) Searcher.linearSearch(theMasters, new IdMatcher(id));
    }

    // Master
    /////////

    void insertItemProto(ItemProto itemProto) {
	theItemProtos.insert(itemProto);
    }

    public ItemProto findItemProtoById(int itemId) {
	int ind = Searcher.binarySearch(theItemProtos, new IdComparator(itemId));
	if (ind == -1)
	    return null;
	return (ItemProto) theItemProtos.getElementAt(ind);
    }

    public String listItemProtosByName(String name) {
	String list = "";
	for (Enumeration items = theItemProtos.elements(); items.hasMoreElements(); ) {
	    ItemProto curr = (ItemProto) items.nextElement();
	    if (curr.hasName(name))
		list += Separators.NL + StrUtil.formatNumber(curr.getId(), 5, ' ') + " " + StrUtil.rightPad(curr.getItemProtoType(), 23) +
		    " " + curr.getName();
	}
	return list;
    }

    void insertCreatProto(CreatureProto creatProto) {
	theCreatProtos.insert(creatProto);
    }

    public CreatureProto findCreatProtoById(int creatId) {
	int ind = Searcher.binarySearch(theCreatProtos, new IdComparator(creatId));
	if (ind == -1)
	    return null;
	return (CreatureProto) theCreatProtos.getElementAt(ind);
    }

    public String getCreatProtosByName(String name) {
	String list = "";
	for (Enumeration creats = theCreatProtos.elements(); creats.hasMoreElements(); ) {
	    CreatureProto curr = (CreatureProto) creats.nextElement();
	    if (curr.hasName(name))
		list += Separators.NL + StrUtil.formatNumber(curr.getId(), 5, ' ') + " " + curr.getName();
	}
	return list;
    }

    void adjustDoors() {
	Log.info("Ajustando portas");

	IdComparator idCmp = new IdComparator(-1);

	for (Enumeration sourceRooms = theRooms.elements(); sourceRooms.hasMoreElements(); ) {
	    Room currRoom = (Room) sourceRooms.nextElement();
	    Room trgRoom = null;
	    for (Enumeration doors = currRoom.getExits(); doors.hasMoreElements(); ) {
		SuperDoor currDoor = (SuperDoor) doors.nextElement();
		idCmp.setId(currDoor.getDestinationId());
		int dest = Searcher.binarySearch(theRooms, idCmp);
		if (dest != -1) {
		    trgRoom = (Room) theRooms.getElementAt(dest);
		    currDoor.adjust(trgRoom);
		    trgRoom.mark(trgRoom);
		}
		else {
		    Log.err("Porta " + currDoor.getDirectionName() + " da sala " + currRoom.getId() +
			    " conduz a sala inexistente: " + currDoor.getDestinationId());
		    currDoor.setError();
		}
	    }
	}

	Log.info("Substituindo portas auxiliares por permanentes");

	for (Enumeration roomEnum = theRooms.elements(); roomEnum.hasMoreElements(); ) {
	    Room currRoom = (Room) roomEnum.nextElement();
	    currRoom.cleanDoors();

	    Enumeration doors = currRoom.getExits();
	    if (!doors.hasMoreElements())
		Log.warn("Sala sem saída: " + currRoom.getId());

	    if (!currRoom.isMarked())
		Log.warn("Sala não alcançável: " + currRoom.getId());
	    else
		currRoom.unmark();
	}
    }

    /*
      void adjustDoors() {
      Log.info("Ajustando portas");

      for (Enumeration sourceRooms = theRooms.elements(); sourceRooms.hasMoreElements(); ) {
      Room currRoom = (Room) sourceRooms.nextElement();
      for (Enumeration doors = currRoom.getExits(); doors.hasMoreElements(); ) {
      SuperDoor currDoor = (SuperDoor) doors.nextElement();
      int dest = Searcher.binarySearch(theRooms, new IdComparator(currDoor.getDestinationId()));
      if (dest != -1) {
      currDoor.adjust((Room) theRooms.getElementAt(dest));
      }
      else {
      Log.err("Porta " + currDoor.getDirectionName() + " da sala " + currRoom.getId() + " conduz a sala inexistente: " + currDoor.getDestinationId());
      currDoor.setError();
      }
      }
      }
      }

      void cleanRooms() {
      Log.info("Substituindo portas auxiliares por permanentes");

      for (Enumeration roomEnum = theRooms.elements(); roomEnum.hasMoreElements(); ) {
      Room currRoom = (Room) roomEnum.nextElement();
      currRoom.cleanDoors();
      }
      }

      void checkExits() {
      Log.info("Verificando saídas");

      for (Enumeration rooms = theRooms.elements(); rooms.hasMoreElements(); ) {
      Room curr = (Room) rooms.nextElement();
      Enumeration doors = curr.getExits();
      if (!doors.hasMoreElements())
      Log.warn("Sala sem saída: " + curr.getId());
      }
      }

      void checkReaching() {
      Log.info("Verificando alcançabilidade das salas");

      for (Enumeration trgRooms = theRooms.elements(); trgRooms.hasMoreElements(); ) {
      Room trg = (Room) trgRooms.nextElement();
      Ok: {
      for (Enumeration srcRooms = theRooms.elements(); srcRooms.hasMoreElements(); ) {
      Room src = (Room) srcRooms.nextElement();
      for (Enumeration doors = src.getExits(); doors.hasMoreElements(); ) {
      Door dr = (Door) doors.nextElement();
      if (dr.getDestinationRoom() == trg)
      break Ok;
      }
      }
      Log.warn("Sala não alcançável: " + trg.getId());
      } // Ok
      }
      }

    */

    public void insertChar(Char aChar, Place plc) {
	theChars.insert(aChar);
	aChar.enterPlace(plc);
    }

    public void removeChar(Char aChar) throws NoSuchElementException {
	aChar.leavePlace();
	theChars.remove(aChar);
    }

    // BeginTask
    public void insertPlayer(Char ch) {
	thePlayers.insert(ch);
    }

    // QuitTask
    public void removePlayer(Char ch) throws NoSuchElementException {
	thePlayers.remove(ch);
    }

    public Player findPlayerByName(String name) {
	return (Player) Searcher.linearSearch(thePlayers, new NameMatcher(name));
    }

    // destrói os items e equipamento do personagem antes de
    // remove-lo do mundo
    public void extractChar(Char ch) {
	ch.extractItems();
	ch.extractEqs();
	removeChar(ch);
    }

    /*
      public Char findCharByName(String name, int ind, Char looker) {
      return (Char) Searcher.linearSearch(theChars, new VisibilityNameMatcher(name, looker), ind);
      }
    */

    public Char findCharByName(IndexedToken indTok, Char looker) {
	return (Char) Searcher.linearSearch(theChars, new VisibilityNameMatcher(indTok.getTarget(), looker), indTok.getIndex());
    }

    /*
      public Vector findAllCharsByName(String name, Char looker) {
      return Finder.findAll(theChars, new VisibilityNameMatcher(name, looker));
      }
    */

    public Vector findAllCharsByName(IndexedToken indTok, Char looker) {

	String name = indTok.getTarget();
	int ind = indTok.getIndex();

	if (indTok.isAll())
	    return Finder.findAll(theChars, new VisibilityNameMatcher(name, looker));

	Object obj = Searcher.linearSearch(theChars, new VisibilityNameMatcher(name, looker), ind);
	Vector set = new Vector();
	if (obj != null)
	    set.insert(obj);

	return set;
    }

    /*
      public Char findCharById(int id) {
      return (Char) Searcher.linearSearch(theChars, new IdMatcher(id));
      }
    */

    public Char findCharById(int id, Char looker) {
	return (Char) Searcher.linearSearch(theChars, new VisibilityIdMatcher(id, looker));
    }

    public Creature findCreatureById(int id) {
	return (Creature) Searcher.linearSearch(theChars, new CreatureIdMatcher(id));
    }

    public Creature backFindCreatureById(int id) {
	return (Creature) Searcher.backLinearSearch(theChars, new CreatureIdMatcher(id));
    }

    public Creature findCreatureById(int id, Char looker) {
	return (Creature) Searcher.linearSearch(theChars, new VisibilityCreatureIdMatcher(id, looker));
    }

    public Creature backFindCreatureById(int id, Char looker) {
	return (Creature) Searcher.backLinearSearch(theChars, new VisibilityCreatureIdMatcher(id, looker));
    }

    public void sendPlayerList(Recipient rcpt, Char looker) {
	Enchainer.listIf(rcpt, theChars.elements(), new NameExtractor(), Separators.NL, new PlayerVisibilityVerifier(looker));
    }

    public String getFullPlayerList(Char looker) {
	String list = "";
	for (Enumeration charEnum = theChars.elements(); charEnum.hasMoreElements(); ) {
	    Char curr = (Char) charEnum.nextElement();
	    if (curr.isPlayer() && curr.canBeSeenBy(looker)) {
		Place plc  = curr.getPlace();
		list += Separators.NL +
		    StrUtil.formatNumber(curr.getId(), 5, ' ') + " " +
		    StrUtil.formatNumber(curr.getRank(), 5, ' ') + " " +
		    StrUtil.formatNumber(curr.getLevel(), 5, ' ') + " " +
		    StrUtil.rightPad(curr.getName(), 10) + " " +
		    curr.getClient().getHostName();
	    }
	}
	return list;
    }

    public void insertItem(Item it, Owner ow) {
	theItems.insert(it);
	it.giveTo(ow);
    }

    public void removeItem(Item it) throws NoSuchElementException {
	theItems.remove(it);
	it.drop();
    }

    // destrói os itens possivelmente contidos num
    // recipiente antes de removê-lo do mundo
    public void extractItem(Item it) {
	Owner ow = it.getContainer();
	if (ow != null)
	    ow.extractItems();
	try { removeItem(it); /* drop, takeoffeq */ } catch(NoSuchElementException e) { }
    }

    public Item findItemById(int itemId) {
	return (Item) Searcher.linearSearch(theItems, new IdMatcher(itemId));
    }

    public Item backFindItemById(int itemId) {
	return (Item) Searcher.backLinearSearch(theItems, new IdMatcher(itemId));
    }

    public Item findItemById(int itemId, Char looker) {
	return (Item) Searcher.linearSearch(theItems, new VisibilityIdMatcher(itemId, looker));
    }

    public Item backFindItemById(int itemId, Char looker) {
	return (Item) Searcher.backLinearSearch(theItems, new VisibilityIdMatcher(itemId, looker));
    }

    public Item findItemByName(String name, int ind, Char looker) {
	return (Item) Searcher.linearSearch(theItems, new VisibilityNameMatcher(name, looker), ind);
    }

    /*
      public Vector findAllItemsByName(String name, Char looker) {
      return Finder.findAll(theItems, new VisibilityNameMatcher(name, looker));
      }
    */

    public Vector findAllItemsByName(IndexedToken indTok, Char looker) {

	String name = indTok.getTarget();
	int ind = indTok.getIndex();

	if (indTok.isAll())
	    return Finder.findAll(theItems, new VisibilityNameMatcher(name, looker));

	Object obj = Searcher.linearSearch(theItems, new VisibilityNameMatcher(name, looker), ind);
	Vector set = new Vector();
	if (obj != null)
	    set.insert(obj);

	return set;
    }

    // 2 personagens e 2 itens
    public void action(String act, boolean hide,
		       Char ch, Char vict,
		       Item it1, Item it2) {
	for (Enumeration charEnum = theChars.elements(); charEnum.hasMoreElements(); ) {
	    Char currChar = (Char) charEnum.nextElement();
	    currChar.action(act, hide, ch, vict, it1, it2);
	}
    }

    // 2 personagens e um 1 item
    public void action(String act, boolean hide,
		       Char ch, Char vict,
		       Item it) {
	action(act, hide, ch, vict, it, null);
    }

    // 2 personagens e nenhum item
    public void action(String act, boolean hide,
		       Char ch, Char vict) {
	action(act, hide, ch, vict, null, null);
    }


    // 1 personagem e 2 itens
    public void action(String act, boolean hide,
		       Char ch,
		       Item it1, Item it2) {
	action(act, hide, ch, null, it1, it2);
    }

    // 1 personagem e 1 item
    public void action(String act, boolean hide,
		       Char ch,
		       Item it) {
	action(act, hide, ch, null, it, null);
    }

    // 1 personagem e nenhum item
    public void action(String act, boolean hide,
		       Char ch) {
	action(act, hide, ch, null, null, null);
    }

    // 2 personagens e 2 itens
    public void actionNotToChar(String act, boolean hide,
				Char ch, Char vict,
				Item it1, Item it2) {
	for (Enumeration charEnum = theChars.elements(); charEnum.hasMoreElements(); ) {
	    Char currChar = (Char) charEnum.nextElement();
	    if (currChar != ch)
		currChar.action(act, hide, ch, vict, it1, it2);
	}
    }

    // 2 personagens e 1 item
    public void actionNotToChar(String act, boolean hide,
				Char ch, Char vict,
				Item it) {
	actionNotToChar(act, hide, ch, vict, it, null);
    }

    // 2 personagens e nenhum item
    public void actionNotToChar(String act, boolean hide,
				Char ch, Char vict) {
	actionNotToChar(act, hide, ch, vict, null, null);
    }

    // 1 personagem e 2 itens
    public void actionNotToChar(String act, boolean hide,
				Char ch,
				Item it1, Item it2) {
	actionNotToChar(act, hide, ch, null, it1, it2);
    }

    // 1 personagem e 1 item
    public void actionNotToChar(String act, boolean hide,
				Char ch, Item it) {
	actionNotToChar(act, hide, ch, null, it, null);
    }

    // 1 personagem e nenhum item
    public void actionNotToChar(String act, boolean hide,
				Char ch) {
	actionNotToChar(act, hide, ch, null, null, null);
    }

    ////
    //

    // chamados a cada hora do mundo

    void creatureAction() {

	Log.debug("Acionando criaturas andarilhas");

	for (Enumeration charEnum = theChars.elements(); charEnum.hasMoreElements(); ) {
	    Char currChar = (Char) charEnum.nextElement();
	    if (!currChar.isPlayer())
		((Creature) currChar).doAction();
	}
    }

    void performRegeneration() {

	Log.debug("Regenerando personagens");

	for (Enumeration charEnum = theChars.elements(); charEnum.hasMoreElements(); ) {
	    Char currChar = (Char) charEnum.nextElement();
	    currChar.regenerate();
	}

    }

    //
    ////
}

