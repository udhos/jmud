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

import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.io.IOException;

import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.InvalidFlagException;
import jmud.util.color.Color;
import jmud.util.bit.Bit;
import jmud.util.log.Log;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.command.IndexedToken;
import jmud.jgp.NameExtractor;
import jmud.jgp.BriefExtractor;
import jmud.jgp.FancyNameExtractor;
import jmud.jgp.VisibilityNameMatcher;
import jmud.jgp.CharCloneDetector;
import jmud.jgp.ItemCloneDetector;
import jgp.container.List;
import jgp.container.Vector;
import jgp.algorithm.Searcher;
import jgp.adaptor.Finder;
import jgp.interfaces.Transversable;

public class Room implements Place, UniqueId, Named, Entitled, Transversable {

    static final int NOWHERE = -1;

    static final int P_OPEN = Bit.BIT0; // lugar aberto (nao sala? campo?)

    static final String propLabels[] = {
	"aberta"
    };

    static private World theWorld = null;

    static void setWorld(World wld) {
	theWorld = wld;
    }

    private   int        theId;
    private   String     theTitle       = null;
    private   String     theAliases     = null;
    private   String     theDescription = null;
    private   int        properties     = 0;
    private   Vector     theDoors       = new Vector(); // deve ser vetor (cleanDoors)
    protected List       theChars       = new List();
    protected List       theItems       = new List();
    private   Zone       theZone        = null;

    private   Object     token          = null;

    Room(Zone zn, int zoneBase, int roomId, String title, String aliases, String desc) {
	theId          = roomId + zoneBase;
	theZone        = zn;
	theTitle       = title;
	theAliases     = aliases;
	theDescription = desc;
    }


    Room(Zone zn, int zoneBase, int roomId, LineReader roomFile)
	throws InvalidFileFormatException {

	theId = roomId + zoneBase;
	theZone = zn;

	try {
	    theTitle = roomFile.readLine();

	    Log.debug("Sala: " + theTitle);

	    theAliases = StrUtil.loadAliases(roomFile);
	    if (theAliases.startsWith("-"))
		theAliases = null;

	    theDescription = StrUtil.readDescription(roomFile);

	    try {
		properties = StrUtil.parseFlags(roomFile.readLine());
	    }
	    catch(InvalidFlagException e) {
		throw new InvalidFileFormatException();
	    }

	    for (; ; ) {
		if (roomFile.eos())
		    throw new InvalidFileFormatException();

		String door = roomFile.readLine();
		if (door.startsWith(Separators.EOR))
		    break;

		try {
		    if (superDoorExists(door))
			Log.err("Redefinição de porta: " + door);
		    else
			theDoors.insert(new SuperDoor(zoneBase, door));
		}
		catch (NumberFormatException e) {
		    Log.err("O destino deve ser um número:" +  door);
		}
		catch (NoSuchElementException e) {
		    Log.err("Direção inválida: " + door);
		}
		catch (InvalidFlagException e) {
		    Log.err("Propriedade inválida para porta: " + door);
		}
	    }

	    // comprime o vetor de portas
	    theDoors.trim();

	}
	catch(IOException e) {
	    throw new InvalidFileFormatException();
	}

    }

    boolean hasId(int roomId) {
	return roomId == getId();
    }

    public boolean hasName(String name) {
	// uma sala pode não ter aliases
	return theAliases == null ? false : StrUtil.findAliasPrefix(name, theAliases);
    }

    boolean doorExists(String door) {
	return findDoorByName(door) != null;
    }

    boolean superDoorExists(String door) {
	return findSuperDoorByName(door) != null;
    }


    String getExitNames(Char looker) {
	String exitNames = "";
	for (Enumeration doorEnum = theDoors.elements(); doorEnum.hasMoreElements(); ) {
	    Door currDoor = (Door) doorEnum.nextElement();
	    if (currDoor.canBeSeenBy(looker)) {
		String name = currDoor.getDirectionName();
		if (currDoor.isHidden())
		    name = "<" + name + ">";
		if (!currDoor.isOpen())
		    name = "(" + name + ")";
		exitNames += name + " ";
	    }
	}
	return ((exitNames.length() > 0) ? exitNames : "nenhuma");
    }

    public String findDoorName(Room ro) {
	for (Enumeration enum = theDoors.elements(); enum.hasMoreElements(); ) {
	    Door currDoor = (Door) enum.nextElement();
	    if (currDoor.getDestinationRoom() == ro)
		return currDoor.getDirectionName();
	}
	return "Sala não adjacente";
    }

    void insertDoor(SuperDoor dr) {
	theDoors.insert(dr);
    }

    //////////////////////////
    // Implementação de Place:

    public String getTitle() {
	return theTitle;
    }

    public String getName() {
	return getTitle();
    }

    public String getPlaceType() {
	return "sala";
    }

    public Zone getZone() {
	return theZone;
    }

    public boolean isRoom() {
	return true;
    }

    public boolean isOpen() {
	return Bit.isSet(properties, P_OPEN);
    }

    public Enumeration getExits() {
	return theDoors.elements();
    }

    public String getFullDescription(Char aChar) {
	String desc = aChar.buildColor(ColorMap.ROOM_TITLE) + theTitle + aChar.getNormalColor() + Separators.NL + theDescription + Separators.NL + "Saídas: " + getExitNames(aChar) + getItemBriefList(aChar) + getCharBriefList(aChar);

	return aChar.isOmni() ? ("[" + getId() + "] " + desc) : desc;
    }

    public void insertChar(Char ch) {
	CloneAlgo.insert(theChars, ch, new CharCloneDetector());
    }

    public void removeChar(Char aChar)
	throws NoSuchElementException {
	theChars.remove(aChar);
    }

    public Char findCharByName(String name, int ind, Char looker) {
	return (Char) Searcher.linearSearch(theChars, new VisibilityNameMatcher(name, looker), ind);
    }

    public Char findCharByName(IndexedToken it, Char looker) {
	return (Char) Searcher.linearSearch(theChars, new VisibilityNameMatcher(it.getTarget(), looker), it.getIndex());
    }

    public ShopCreature findShopKeeper() {
	for (Enumeration enum = theChars.elements(); enum.hasMoreElements(); ) {
	    Char ch = (Char) enum.nextElement();
	    if (ch instanceof ShopCreature)
		return (ShopCreature) ch;
	}
	return null;
    }

    public Object[] findMaster() {
	for (Enumeration enum = theChars.elements(); enum.hasMoreElements(); ) {
	    Char ch = (Char) enum.nextElement();
	    Master mstr = (Master) ch.findMaster();
	    if (mstr != null) {
		Object[] pair = new Object[2];
		pair[0] = ch;
		pair[1] = mstr;
		return pair;
	    }
	}
	return null;
    }

    public String getItemBriefList(Char looker) {
	return CloneAlgo.getList(theItems, looker, new ItemCloneDetector(), new BriefExtractor(looker.isOmni()));
    }

    public Enumeration getChars() {
	return theChars.elements();
    }

    public String getCharBriefList(Char looker) {
	return CloneAlgo.getList(theChars, looker, new CharCloneDetector(), new BriefExtractor(looker.isOmni()));
    }

    public Door findDoorByName(String door) {
	for (Enumeration enum = theDoors.elements(); enum.hasMoreElements(); ) {
	    Door currDoor = (Door) enum.nextElement();
	    if (currDoor.leadsTo(door.substring(0, 1)))
		return currDoor;
	}
	return null;
    }

    public SuperDoor findSuperDoorByName(String door) {
	for (Enumeration enum = theDoors.elements(); enum.hasMoreElements(); ) {
	    SuperDoor currDoor = (SuperDoor) enum.nextElement();
	    if (currDoor.leadsTo(door.substring(0, 1)))
		return currDoor;
	}
	return null;
    }

    public Door findDoorByDir(int dir) {
	for (Enumeration enum = theDoors.elements(); enum.hasMoreElements(); ) {
	    Door currDoor = (Door) enum.nextElement();
	    if (currDoor.getDirection() == dir)
		return currDoor;
	}
	return null;
    }

    void cleanDoors() {
	int len = theDoors.getSize();
	for (int i = 0; i < len; ++i) {
	    SuperDoor curr = (SuperDoor) theDoors.getElementAt(i);
	    theDoors.setElementAt(i, curr.makeDoor());
	}
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

    // Place
    //////////////////////////

    //////////////////////////
    // Implementação de Owner:

    public int getId() {
	return theId;
    }

    public String getOwnerType() {
	return "sala";
    }

    private void addLoadOf(Item it) {
    }

    private void subLoadOf(Item it) {
    }

    public void insertItem(Item it) {
	CloneAlgo.insert(theItems, it, new ItemCloneDetector());
	addLoadOf(it);
    }

    public void removeItem(Item it)
	throws NoSuchElementException {
	theItems.remove(it);
	subLoadOf(it);
    }

    public Item findItemByName(String name, int ind, Char looker) {
	return (Item) Searcher.linearSearch(theItems, new VisibilityNameMatcher(name, looker), ind);
    }

    public Vector findItemsByName(IndexedToken it, Char looker) {
	if (it.isAll())
	    return Finder.findAll(theItems, new VisibilityNameMatcher(it.getTarget(), looker));

	Vector set = new Vector();
	Object obj = Searcher.linearSearch(theItems, new VisibilityNameMatcher(it.getTarget(), looker), it.getIndex());
	if (obj != null)
	    set.insert(obj);
	return set;
    }

    public String getItemList(Char looker) {
	return CloneAlgo.getList(theItems, looker, new ItemCloneDetector(), new FancyNameExtractor());
    }

    public boolean hasFreeLoad(int load) {
	return true;
    }

    public void extractItems() {
	for (Enumeration items = theItems.elements(); items.hasMoreElements(); ) {
	    Item curr = (Item) items.nextElement();
	    Owner ow = curr.getContainer();
	    if (ow != null)
		ow.extractItems();
	    theWorld.removeItem(curr);
	}
    }

    public Owner getOwner() {
	return null;
    }

    public Enumeration getContents() {
	return theItems.elements();
    }

    // Owner
    //////////////////////////

    //////////////////////////////////
    // Implementação de Transversable:

    public void mark(Object s) {
	token = s;
    }

    public Object getMark() {
	return token;
    }

    public void unmark() {
	token = null;
    }

    public boolean isMarked() {
	return token != null;
    }

    public Enumeration getNeighbors() {
	return new NeighborsEnumeration(this);
    }

    // Transversable
    //////////////////////////////////

    String getItemNameCommaList(Char looker) {
	return StrUtil.wrapList(CloneAlgo.getHeadedList(theItems, looker, new ItemCloneDetector(), new NameExtractor(), ", "), "Nenhum");
    }

    String listDoors() {
	String list = "";
	for (Enumeration doorEnum = theDoors.elements(); doorEnum.hasMoreElements(); ) {
	    Door currDoor = (Door) doorEnum.nextElement();
	    String name = currDoor.getDirectionName();
	    list += Separators.NL + currDoor.getSheet();
	}
	return list;
    }

    String getCharNameCommaList(Char looker) {
	return StrUtil.wrapList(CloneAlgo.getHeadedList(theChars, looker, new CharCloneDetector(), new NameExtractor(), ", "), "Nenhum");
    }

    public String getSheet(Char looker) {
	return "SALA - ID: [" + getId() + "] " + "Título: [" + getTitle() + "]" + Separators.NL +
	    "Sinônimos: " + theAliases + Separators.NL +
	    "Propriedades: " + StrUtil.fancyListFlags(properties, propLabels, "Nenhuma") + Separators.NL +
	    "Itens: " + getItemNameCommaList(looker) + Separators.NL +
	    "Personagens: " +  getCharNameCommaList(looker) + Separators.NL +
	    "Portas: " + StrUtil.wrapList(listDoors(), "Nenhuma");
    }
}

class NeighborsEnumeration implements Enumeration {

    private Enumeration theDoors = null;

    public NeighborsEnumeration(Room room) {
	theDoors = room.getExits();
    }

    public boolean hasMoreElements() {
	return theDoors.hasMoreElements();
    }

    public Object nextElement() throws NoSuchElementException {
	if (!hasMoreElements())
	    throw new NoSuchElementException();

	return ((Door) theDoors.nextElement()).getDestinationRoom();
    }
}
