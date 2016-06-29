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

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.util.Enumeration;

import jmud.hook.Hook;
import jmud.hook.HookTable;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.log.Log;
import jmud.util.StrUtil;
import jmud.util.Separators;
import jgp.container.Vector;

import jmud.parser.SynAna;
import jmud.parser.LexAna;
import jmud.parser.SyntaxNode;
import jmud.parser.SyntaxList;
import jmud.parser.SyntaxMap;
import jmud.parser.SyntaxException;

public class Zone implements UniqueId {

    private int    theId;
    private String theTitle  = null;
    private String theName   = null;
    private int    base;
    private int    range;
    private int    period;
    private int    remainingTime;
    private World  theWorld  = null;
    private Vector theResets = new Vector();

    Zone(World aWorld, String zoneFileName, String zoneDir)
	throws InvalidFileFormatException {
	theWorld = aWorld;

	String zone = zoneDir + zoneFileName;

	LineReader zoneFile = null;
	try {
	    zoneFile = new LineReader(new FileReader(zone));

	    String id = zoneFile.readLine();
	    if (!id.startsWith(Separators.BOR))
		throw new InvalidFileFormatException();

	    id = id.substring(1);
	    try {
		theId = Integer.parseInt(id);
	    }
	    catch(NumberFormatException e) {
		Log.abort("Zona com ID não numérico: " + id);
	    }

	    theTitle = zoneFile.readLine();
	    theName  = zoneFile.readLine().toLowerCase();

	    Log.info("Zona: " + theTitle);

	    StringTokenizer tok = new StringTokenizer(zoneFile.readLine());

	    if (!tok.hasMoreTokens())
		throw new InvalidFileFormatException();

	    try {
		base     = Integer.parseInt(tok.nextToken());
	    }
	    catch(NumberFormatException e) {
		Log.abort("Zona com base não numérica: " + base);
	    }

	    if (!tok.hasMoreTokens())
		throw new InvalidFileFormatException();

	    try {
		range    = Integer.parseInt(tok.nextToken());
	    }
	    catch(NumberFormatException e) {
		Log.abort("Zona com faixa não numérica: " + range);
	    }

	    if (!tok.hasMoreTokens())
		throw new InvalidFileFormatException();

	    try {
		period   = Integer.parseInt(tok.nextToken());
	    }
	    catch(NumberFormatException e) {
		Log.abort("Zona com período não numérico: " + period);
	    }

	    while (!zoneFile.eos()) {
		String fileName = zoneFile.readLine();

		// Ignora linhas em branco e comentarios
		if (fileName.length() == 0 || fileName.startsWith(Separators.REM))
		    continue;

		if (fileName.startsWith(Separators.EOF))
		    break;

		String name = zoneDir + fileName.substring(2);
		switch(fileName.charAt(0)) {
		case 's': loadRooms(name); break;
		case 'i': loadItems(name); break;
		case 'c': loadCreatures(name); break;
		case 'a': loadResets(name); break;
		case 'l': loadShops(name); break;
		case 'm': loadMasters(name); break;
		default: throw new InvalidFileFormatException();
		}
	    }

	    zoneFile.close();
	}
	catch(FileNotFoundException e) {
	    Log.abort("Arquivo de zona não encontrado: " + zone);
	}
	catch(IOException e) {
	    Log.abort("Falha de I/O ao acessar arquivo de zona: " + zone);
	}
    }

    public boolean hasId(int id) {
	return id == getId();
    }

    public boolean hasName(String name) {
	return theName.equals(name);
    }

    public int getId() {
	return theId;
    }

    public String getTitle() {
	return theTitle;
    }

    int getBase() {
	return base;
    }

    public void reset() {
	Log.info("Automatizando zona: " + theTitle);
	Reset.setLast();
	for (Enumeration res = theResets.elements(); res.hasMoreElements(); ) {
	    Reset curr = (Reset) res.nextElement();
	    if (curr.shouldSkip()) {
		curr.resetLast(); // so' pode vir apos o teste shouldSkip()
		continue;
	    }
	    curr.resetLast(); // so' pode vir apos o teste shouldSkip()
	    curr.perform(theWorld);
	}
	remainingTime = period;
    }

    void tick() {
	if (remainingTime > 0)
	    --remainingTime;
    }

    boolean isTimeToReset() {
	return remainingTime == 0;
    }

    void loadRooms(String rooms) {

	LineReader roomFile = null;
	try {
	    roomFile = new LineReader(new FileReader(rooms));
	}
	catch(FileNotFoundException e) {
	    Log.abort("Arquivo de salas não encontrado: " + rooms);
	}
	catch(IOException e) {
	    Log.abort("Falha ao ler arquivo de salas: " + rooms);
	}

	try {
	    while (!roomFile.eos()) {
		String roomId = roomFile.readLine();
		if (roomId.startsWith(Separators.EOF))
		    break;
		if (!roomId.startsWith(Separators.BOR))
		    throw new InvalidFileFormatException();

		roomId = roomId.substring(1);
		int id = 0;
		try {
		    id = Integer.parseInt(roomId);
		}
		catch(NumberFormatException e) {
		    Log.abort("Sala com ID não numérico: " + id);
		}

		if (id >= range)
		    Log.abort("Sala com ID fora da faixa da zona: " + id);

		theWorld.insertRoom(new Room(this, base, id, roomFile));
	    }

	    roomFile.close();

	}
	catch(InvalidFileFormatException e) {
	    Log.err("Arquivo de salas com formato inválido: " + rooms);
	}
	catch(IOException e) {
	    Log.err("Falha de E/S ao ler arquivo de salas: " + rooms);
	}
    }

    void loadItems(String items) {

	LineReader itemFile = null;
	try {
	    itemFile = new LineReader(new FileReader(items));
	}
	catch(FileNotFoundException e) {
	    Log.err("Arquivo de items não encontrado: " + items);
	    return;
	}
	catch(IOException e) {
	    Log.abort("Falha ao ler arquivo de itens: " + items);
	    return;
	}

	try {
	    while (!itemFile.eos()) {
		String itemId = itemFile.readLine();
		if (itemId.startsWith(Separators.EOF))
		    break;
		if (!itemId.startsWith(Separators.BOR))
		    throw new InvalidFileFormatException();

		itemId = itemId.substring(1);
		int id = 0;
		try {
		    id = Integer.parseInt(itemId);
		}
		catch(NumberFormatException e) {
		    Log.abort("Item com ID não numérico: " + itemId);
		}

		if (id >= range)
		    Log.abort("Item com ID fora da faixa da zona: " + id);

		theWorld.insertItemProto(ItemProto.load(id, base, itemFile));

		String eor = itemFile.readLine();
		if (!eor.startsWith(Separators.EOR))
		    throw new InvalidFileFormatException();
	    }

	    itemFile.close();

	}
	catch(InvalidFileFormatException e) {
	    Log.err("Arquivo de itens com formato inválido: " + items);
	}
	catch(IOException e) {
	    Log.err("Falha de E/S ao ler arquivo de itens: " + items);
	}
    }

    void loadCreatures(String creats) {

	LineReader creatFile = null;
	try {
	    creatFile = new LineReader(new FileReader(creats));
	}
	catch(FileNotFoundException e) {
	    Log.err("Arquivo de criaturas não encontrado: " + creats);
	    return;
	}
	catch(IOException e) {
	    Log.err("Falha ao ler arquivo de criaturas: " + creats);
	    return;
	}

	try {
	    while (!creatFile.eos()) {
		String creatId = creatFile.readLine();
		if (creatId.startsWith(Separators.EOF))
		    break;
		if (!creatId.startsWith(Separators.BOR))
		    throw new InvalidFileFormatException();

		creatId = creatId.substring(1);
		int id = 0;
		try {
		    id = Integer.parseInt(creatId);
		}
		catch(NumberFormatException e) {
		    Log.abort("Criatura com ID não numérico: " + creatId);
		}

		if (id >= range)
		    Log.abort("Criatura com ID fora da faixa da zona: " + id);

		String ty = creatFile.readLine();
		int type = 0;
		try {
		    type = Integer.parseInt(ty);
		}
		catch(NumberFormatException e) {
		    Log.abort("Criatura com tipo não numérico: " + ty);
		}

		theWorld.insertCreatProto(new CreatureProto(base, id, type, creatFile));
	    }

	    creatFile.close();
	}
	catch(InvalidFileFormatException e) {
	    Log.err("Arquivo de criaturas com formato inválido: " + creats);
	}
	catch(IOException e) {
	    Log.err("Falha de E/S ao ler arquivo de criaturas: " + creats);
	}
    }

    void loadResets(String resets) {

	LineReader resetsFile = null;
	try {
	    resetsFile = new LineReader(new FileReader(resets));
	}
	catch(FileNotFoundException e) {
	    Log.abort("Arquivo de automações não encontrado: " + resets);
	}
	catch(IOException e) {
	    Log.abort("Falha ao ler arquivo de automações: " + resets);
	}

	try {
	    int count = 0;
	    while (!resetsFile.eos()) {
		++count;
		String line = resetsFile.readLine();
		if (line.startsWith(Separators.EOF))
		    break;
		else if (line.startsWith(Separators.REM))
		    continue;
		StringTokenizer tok = new StringTokenizer(line);
		if (tok.hasMoreTokens()) {
		    try {
			switch(tok.nextToken().charAt(0)) {
			case 'c':
			    theResets.insert(new ResetCreat(base, tok));
			    break;
			case 'i':
			    theResets.insert(new ResetItem(base, tok));
			    break;
			case 'e':
			    theResets.insert(new ResetEquip(base, tok));
			    break;
			case 'd':
			    theResets.insert(new ResetGive(base, tok));
			    break;
			case 'p':
			    theResets.insert(new ResetPut(base, tok));
			    break;
			case 'x':
			    theResets.insert(new ResetBind(base, tok));
			    break;
			case 's':
			    theResets.insert(new ResetDoor(base, tok));
			    break;
			default:
			    throw new InvalidFileFormatException();
			}
			if (tok.hasMoreTokens() && !tok.nextToken().startsWith(Separators.REM))
			    throw new InvalidFileFormatException(resets, "Zone.loadResets", "excesso de parâmetros na linha de automação", line, count);
		    }
		    catch(NoSuchElementException e) {
			throw new InvalidFileFormatException();
		    }
		    catch(NumberFormatException e) {
			throw new InvalidFileFormatException();
		    }
		}
	    }

	    resetsFile.close();
	}
	catch(InvalidFileFormatException e) {
	    Log.err("Arquivo de automações com formato inválido: " + resets);
	}
	catch(IOException e) {
	    Log.err("Falha de E/S ao ler arquivo de automações: " + resets);
	}
    }

    void loadShops(String shops) {

	LineReader shopFile = null;

	try {
	    shopFile = new LineReader(new FileReader(shops));
	}
	catch(FileNotFoundException e) {
	    Log.err("Arquivo de lojas não encontrado: " + shops);
	    return;
	}
	catch(IOException e) {
	    Log.err("Falha ao ler arquivo de lojas: " + shops);
	    return;
	}

	try {
	    while (!shopFile.eos()) {
		String shopId = shopFile.readLine();
		if (shopId.startsWith(Separators.EOF))
		    break;
		if (!shopId.startsWith(Separators.BOR))
		    throw new InvalidFileFormatException();

		shopId = shopId.substring(1);
		int id = 0;
		try {
		    id = Integer.parseInt(shopId);
		}
		catch(NumberFormatException e) {
		    Log.abort("Loja com ID não numérico: " + shopId);
		}

		if (id >= range)
		    Log.abort("Loja com ID fora da faixa da zona: " + id);

		theWorld.insertShop(new Shop(base, id, shopFile));
	    }

	    shopFile.close();
	}
	catch(InvalidFileFormatException e) {
	    Log.err("Arquivo de lojas com formato inválido: " + shops);
	}
	catch(IOException e) {
	    Log.err("Falha de E/S ao ler arquivo de lojas: " + shops);
	}

    }

    void loadMasters(String masters) {

	Log.debug("Carregando arquivo de mestres: " + masters);

	LineReader masterFile = null;

	try {
	    masterFile = new LineReader(new FileReader(masters));
	}
	catch(FileNotFoundException e) {
	    Log.err("Arquivo de mestres não encontrado: " + masters);
	    return;
	}
	catch(IOException e) {
	    Log.err("Falha de E/S ao ler arquivo de mestres: " + masters);
	    return;
	}

	boolean debugMode = true;
	LexAna lexer = new LexAna(masterFile, Log.getStream(), debugMode);
	SynAna parser = new SynAna(lexer, debugMode);
	try {
	    parser.parse();
	}
	catch(Exception e) {
	    Log.err("Erro de sintaxe no arquivo: " + masters + ": " + e.getMessage());
	    e.printStackTrace();
	    return;
	}
	finally {
	    try {
		masterFile.close();
	    }
	    catch(IOException e) {
		Log.err("Falha de E/S ao fechar arquivo de mestres: " + masters);
	    }
	}

	Log.debug("Arquivo de mestres carregado: " + masters);

	String identPrefix = "";
	String identInc    = "  ";

	SyntaxNode tree = SynAna.getTree();
	if (debugMode) {
	    PrintWriter pw = new PrintWriter(System.err);
	    tree.show(pw, identPrefix, identInc);
	    pw.flush();
	}

	SyntaxList list = (SyntaxList) tree;
	for (Enumeration enum = list.elements(); enum.hasMoreElements(); ) {
	    SyntaxNode node = (SyntaxNode) enum.nextElement();
	    if (node instanceof SyntaxMap) {
		SyntaxMap map = (SyntaxMap) node;
		try {
		    theWorld.insertMaster(new Master(base, map, identPrefix, identInc));
		}
		catch(SyntaxException e) {
		    Log.err("Falha ao criar mestre: " + e.getMessage());
		    map.show(Log.getStream(), identPrefix, identInc);
		}
	    }
	    else {
		Log.err("Falha ao esperar mapa de mestre:");
		node.show(Log.getStream(), identPrefix, identInc);
	    }
	}

    }

    String getSheet() {
	return StrUtil.formatNumber(theId, 5, ' ') + " " +
	    StrUtil.rightPad(theTitle, 31) + " " +
	    StrUtil.rightPad(theName, 16) + " " +
	    StrUtil.formatNumber(base, 5, ' ') + " " +
	    StrUtil.formatNumber(range, 5, ' ') + " " +
	    StrUtil.formatNumber(remainingTime, 5, ' ') + " " +
	    StrUtil.formatNumber(period, 5, ' ');
    }
}

abstract class Reset {

    static protected boolean last;

    static void setLast() {
	last = true;
    }

    protected boolean depend;

    Reset(StringTokenizer tok) throws NumberFormatException, NoSuchElementException {
	Log.info("Automação: genérico");

	depend = Integer.parseInt(tok.nextToken()) != 0;
    }

    boolean shouldSkip() {
	return depend && !last;
    }

    static void resetLast() {
	last = false;
    }

    abstract void perform(World theWorld);
}

abstract class ImprovedReset extends Reset {

    protected int     max;

    ImprovedReset(StringTokenizer tok) throws NumberFormatException, NoSuchElementException {
	super(tok);

	max = Integer.parseInt(tok.nextToken());
    }

}

class ResetCreat extends ImprovedReset {

    private int creatId;
    private int roomId;

    ResetCreat(int zoneBase, StringTokenizer tok) throws NumberFormatException {
	super(tok);

	Log.info("Automação: criatura");
	creatId = Integer.parseInt(tok.nextToken()) + zoneBase;
	roomId  = Integer.parseInt(tok.nextToken()) + zoneBase;
    }

    void perform(World theWorld) {
	Log.debug("Inserindo criatura " + creatId + " na sala " + roomId);

	CreatureProto cr = theWorld.findCreatProtoById(creatId);
	if (cr == null) {
	    Log.err("Criatura a ser criada não encontrada: " + creatId);
	    return;
	}

	Place pl = theWorld.findRoomById(roomId);
	if (pl == null) {
	    Log.err("Sala não encontrada: " + roomId);
	    return;
	}

	if (cr.getNumberOfCreatures() >= max)
	    return;

	Creature newCr = (Creature) cr.create();
	theWorld.insertChar(newCr, pl);

	setLast();
	Log.debug("Inserindo criatura " + creatId + " na sala " + roomId + ": Ok");
    }
}

class ResetItem extends ImprovedReset {

    private int itemId;
    private int roomId;

    ResetItem(int zoneBase, StringTokenizer tok) throws NumberFormatException, NoSuchElementException {
	super(tok);

	Log.info("Automação: item");
	itemId = Integer.parseInt(tok.nextToken()) + zoneBase;
	roomId = Integer.parseInt(tok.nextToken()) + zoneBase;
    }

    void perform(World theWorld) {
	Log.debug("Inserindo item " + itemId + " na sala " + roomId);

	ItemProto it = theWorld.findItemProtoById(itemId);
	if (it == null) {
	    Log.err("Item a ser criado não encontrado: " + itemId);
	    return;
	}

	Place pl = theWorld.findRoomById(roomId);
	if (pl == null) {
	    Log.err("Sala não encontrada: " + roomId);
	    return;
	}

	if (it.getNumberOfItems() >= max)
	    return;

	Item newIt = it.create();
	theWorld.insertItem(newIt, pl);

	setLast();
	Log.debug("Inserindo item " + itemId + " na sala " + roomId + ": Ok");
    }
}

class ResetGive extends ImprovedReset {

    private int itemId;
    private int creatId;

    ResetGive(int zoneBase, StringTokenizer tok) throws NumberFormatException, NoSuchElementException {
	super(tok);

	Log.info("Automação: entrega item");
	itemId  = Integer.parseInt(tok.nextToken()) + zoneBase;
	creatId = Integer.parseInt(tok.nextToken()) + zoneBase;
    }

    void perform(World theWorld) {
	Log.debug("Entregando item " + itemId + " para criatura " + creatId);

	ItemProto it = theWorld.findItemProtoById(itemId);
	if (it == null) {
	    Log.err("Item a ser entregue não encontrado: " + itemId);
	    return;
	}

	Creature cr = theWorld.backFindCreatureById(creatId);
	if (cr == null) {
	    Log.err("Criatura não encontrada: " + creatId);
	    return;
	}

	if (cr.isPlayer()) {
	    Log.warn("Jogador encontrado ao tentar entregar item " + itemId + " para criatura " + creatId);
	    return;
	}

	if (it.getNumberOfItems() >= max)
	    return;

	Item newIt = it.create();
	theWorld.insertItem(newIt, cr);

	setLast();
	Log.debug("Entregando item " + itemId + " para criatura " + creatId + ": Ok");
    }
}

class ResetEquip extends ImprovedReset {

    private int itemId;
    private int creatId;

    ResetEquip(int zoneBase, StringTokenizer tok) throws NumberFormatException, NoSuchElementException {
	super(tok);

	Log.info("Automação: equipa item");
	itemId  = Integer.parseInt(tok.nextToken()) + zoneBase;
	creatId = Integer.parseInt(tok.nextToken()) + zoneBase;
    }

    void perform(World theWorld) {
	Log.debug("Equipando criatura " + creatId + " com item " + itemId);

	ItemProto it = theWorld.findItemProtoById(itemId);
	if (it == null) {
	    Log.err("Item a ser equipado não encontrado: " + itemId);
	    return;
	}

	Creature cr = theWorld.backFindCreatureById(creatId);
	if (cr == null) {
	    Log.err("Criatura não encontrada: " + creatId);
	    return;
	}

	if (cr.isPlayer()) {
	    Log.warn("Jogador encontrado ao tentar equipar item " + itemId + " na criatura " + creatId);
	    return;
	}

	if (it.getNumberOfItems() >= max)
	    return;

	Item newIt = it.create();

	int wearPos;
	try {
	    wearPos = cr.findWearPosition(newIt);
	}
	catch(NoSuchElementException e) {
	    Log.err(creatId + " não tem onde equipar :" + itemId);
	    return;
	}

	theWorld.insertItem(newIt, cr);
	newIt.equip(cr, wearPos);

	setLast();
	Log.debug("Equipando criatura " + creatId + " com item " + itemId + ": Ok");
    }
}

class ResetPut extends ImprovedReset {

    private int itemId;
    private int containerId;

    ResetPut(int zoneBase, StringTokenizer tok) throws NumberFormatException {
	super(tok);

	Log.info("Automação: coloca item");
	itemId       = Integer.parseInt(tok.nextToken()) + zoneBase;
	containerId  = Integer.parseInt(tok.nextToken()) + zoneBase;
    }

    void perform(World theWorld) {
	Log.debug("Colocando item " + itemId + " no recipiente " + containerId);

	ItemProto it = theWorld.findItemProtoById(itemId);
	if (it == null) {
	    Log.err("Item a ser inserido não encontrado: " + itemId);
	    return;
	}

	Item cnt = theWorld.backFindItemById(containerId);
	if (cnt == null) {
	    Log.err("Recipiente não encontrado: " + containerId);
	    return;
	}

	Owner ow = cnt.getContainer();
	if (ow == null) {
	    Log.err("Item " + containerId + " não é um recipiente.");
	    return;
	}

	if (it.getNumberOfItems() >= max)
	    return;

	Item newIt = it.create();
	theWorld.insertItem(newIt, ow);

	setLast();
	Log.debug("Colocando item " + itemId + " no recipiente " + containerId + ": Ok");
    }
}


class ResetBind extends Reset {

    private int creatId;
    private Hook proc;

    ResetBind(int zoneBase, StringTokenizer tok) throws NumberFormatException, NoSuchElementException {
	super(tok);

	Log.info("Automação: anexa procedimento");
	creatId = Integer.parseInt(tok.nextToken()) + zoneBase;
	String label = tok.nextToken();
	proc = HookTable.lookup(label);
	if (proc == null)
	    Log.err("Gancho não encontrado: " + label);
    }

    void perform(World theWorld) {
	Log.debug("Anexando gancho '" + proc.getLabel() + "' à criatura " + creatId);

	Creature cr = theWorld.backFindCreatureById(creatId);
	if (cr == null) {
	    Log.err("Criatura não encontrada: " + creatId);
	    return;
	}

	cr.addHook(proc);

	setLast();
	Log.debug("Anexando gancho '" + proc.getLabel() + "' à criatura " + creatId + ": Ok");
    }
}


class ResetDoor extends Reset {

    private int roomId;
    private int direction;
    private char action = '\0';

    ResetDoor(int zoneBase, StringTokenizer tok) throws NumberFormatException, NoSuchElementException {
	super(tok);

	Log.info("Automação: manipula porta");

	roomId = Integer.parseInt(tok.nextToken()) + zoneBase;
	String dirLab = tok.nextToken();
	direction = Door.getDirectionByName(dirLab);
	if (direction == -1)
	    Log.err("Direção inválida de porta: " + dirLab);
	String act = tok.nextToken();
	action = act.charAt(0);
	if (action != 'a' && action != 'f' && action != 't' && action != 'd')
	    Log.err("Ação inválida para porta [" + dirLab + "]: " + action);
    }

    void perform(World theWorld) {
	String dirLab = Door.getDirectionName(direction);

	Log.debug("Manipulando porta: " + dirLab + ": " + action);

	Room r = theWorld.findRoomById(roomId);
	if (r == null) {
	    Log.err("Sala não encontrada: " + roomId);
	    return;
	}
	Door d = r.findDoorByDir(direction);
	if (d == null) {
	    Log.err("Porta em direção não encontrada: " + dirLab +
		    "(sala " + roomId +")");
	    return;
	}

	int st = 0;
	switch (action) {
	case 'a':
	    st = Door.S_OPEN;
	    break;
	case 'f':
	case 'd':
	    st = Door.S_CLOSED;
	    break;
	case 't':
	    st = Door.S_LOCKED;
	    break;
	default:
	    Log.err("Ação inválida para porta [" + dirLab + "]: " + action);
	    return;
	}

	d.setBothStatus(st, r);

	Log.debug("Manipulando porta: " + dirLab + ": " + action +": Ok");
	setLast();
    }
}
