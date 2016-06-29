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

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import jmud.hook.Hook;
import jmud.hook.HookTable;
import jmud.util.StrUtil;
import jmud.util.InvalidFlagException;
import jmud.util.Separators;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.log.Log;
import jmud.jgp.NameMatcher;
import jgp.container.Vector;
import jgp.container.Set;
import jgp.algorithm.Searcher;

import jmud.parser.SyntaxMap;

public class CreatureProto implements UniqueId {

    static protected World theWorld = null;

    static void setWorld(World wld) {
	theWorld = wld;
    }

    int    theId;
    int    theType;
    String theName        = null;
    String theAliases     = null;
    String theBrief       = null;
    String theDescription = null;
    int    theLevel;
    int    sex;
    int    theProperties;
    int    experience;
    int    goodBehavior;
    int    badBehavior;

    Set    theHooks       = new Set();

    Money  money = null;

    int attr[] = {
	5, 5, 5, 5,
	5, 5, 5, 5,
	5, 5, 5, 5,
	5, 5, 5, 5,
	5, 5, 5, 5
    };

    Vector randModifiers = new Vector();

    int    numberOfCreatures = 0;

    static private int numberOfProtos = 0;

    CreatureProto(int zoneBase, int creatId, int creatType, LineReader creatFile)
	throws InvalidFileFormatException {
	theId = creatId + zoneBase;

	theType = creatType;

	try {
	    theName = creatFile.readLine();

	    Log.debug("Criatura: " + theName);

	    theAliases = StrUtil.loadAliases(creatFile);
	    theBrief   = creatFile.readLine();

	    theDescription = StrUtil.readDescription(creatFile);

	    StringTokenizer tok = new StringTokenizer(creatFile.readLine());

	    try {
		if (!tok.hasMoreTokens())
		    throw new InvalidFileFormatException();
		theLevel = Integer.parseInt(tok.nextToken());

		if (!tok.hasMoreTokens())
		    throw new InvalidFileFormatException();
		sex = Integer.parseInt(tok.nextToken());

		if (!tok.hasMoreTokens())
		    throw new InvalidFileFormatException();
		theProperties = StrUtil.parseFlags(tok.nextToken());
	    }
	    catch(InvalidFlagException e) {
		throw new InvalidFileFormatException();
	    }

	    tok = new StringTokenizer(creatFile.readLine());
	    try {
		experience   = Integer.parseInt(tok.nextToken());
	    }
	    catch(NoSuchElementException e) {
		throw new InvalidFileFormatException();
	    }

	    tok = new StringTokenizer(creatFile.readLine());
	    try {
		goodBehavior = Integer.parseInt(tok.nextToken());
		badBehavior  = Integer.parseInt(tok.nextToken());
	    }
	    catch(NoSuchElementException e) {
		throw new InvalidFileFormatException();
	    }

	    money = new Money(creatFile);

	    tok = new StringTokenizer(creatFile.readLine());
	    if (!tok.hasMoreTokens())
		throw new InvalidFileFormatException();

	    String labels = tok.nextToken();
	    if (!labels.startsWith("-")) {
		NameMatcher nm = new NameMatcher(null);
		for (tok = new StringTokenizer(labels);
		     tok.hasMoreTokens(); ) {
		    String lbl = tok.nextToken();
		    Hook hk = HookTable.lookup(lbl);
		    if (hk == null) {
			Log.err("Gancho não encontrado: " + lbl);
			continue;
		    }
		    nm.setName(lbl);
		    if (Searcher.linearSearch(theHooks, nm) != null)
			Log.warn("Gancho duplicado '" + lbl + "' para protótipo de criatura: " + getId());
		    theHooks.insert(hk);
		}
		theHooks.trim();
	    }

	    // atributos
	    for (; ; ) {
		if (creatFile.eos())
		    throw new InvalidFileFormatException();

		String line = creatFile.readLine();
		if (line.startsWith(Separators.EOR))
		    break;

		tok = new StringTokenizer(line);
		if (!tok.hasMoreTokens())
		    throw new InvalidFileFormatException();

		String opt = tok.nextToken();

		if (opt.equals("t")) {
		    setAllAttr(Integer.parseInt(tok.nextToken()));
		}
		else if (opt.equals("a")) {
		    int at = Integer.parseInt(tok.nextToken());
		    int v  = Integer.parseInt(tok.nextToken());
		    setAttr(at, v);
		}
		else if (opt.equals("l")) {
		    int li = Integer.parseInt(tok.nextToken());
		    int v  = Integer.parseInt(tok.nextToken());
		    setAttrRow(li, v);
		}
		else if (opt.equals("c")) {
		    int co = Integer.parseInt(tok.nextToken());
		    int v  = Integer.parseInt(tok.nextToken());
		    setAttrColumn(co, v);
		}

		else if (opt.equals("fis")) {
		    for (int i = 0; i < 5; ++i)
			setAttr(i, Char.C_PHY, Integer.parseInt(tok.nextToken()));
		}
		else if (opt.equals("men")) {
		    for (int i = 0; i < 5; ++i)
			setAttr(i, Char.C_MEN, Integer.parseInt(tok.nextToken()));
		}
		else if (opt.equals("mis")) {
		    for (int i = 0; i < 5; ++i)
			setAttr(i, Char.C_MYS, Integer.parseInt(tok.nextToken()));
		}
		else if (opt.equals("soc")) {
		    for (int i = 0; i < 5; ++i)
			setAttr(i, Char.C_SOC, Integer.parseInt(tok.nextToken()));
		}

		else if (opt.equals("per")) {
		    for (int i = 0; i < 4; ++i)
			setAttr(Char.R_PER, i, Integer.parseInt(tok.nextToken()));
		}
		else if (opt.equals("agi")) {
		    for (int i = 0; i < 4; ++i)
			setAttr(Char.R_AGI, i, Integer.parseInt(tok.nextToken()));
		}
		else if (opt.equals("aca")) {
		    for (int i = 0; i < 4; ++i)
			setAttr(Char.R_ACT, i, Integer.parseInt(tok.nextToken()));
		}
		else if (opt.equals("res")) {
		    for (int i = 0; i < 4; ++i)
			setAttr(Char.R_RES, i, Integer.parseInt(tok.nextToken()));
		}
		else if (opt.equals("vit")) {
		    for (int i = 0; i < 4; ++i)
			setAttr(Char.R_VIT, i, Integer.parseInt(tok.nextToken()));
		}

		else
		    throw new InvalidFileFormatException();
	    }

	    // modificadores aleatórios
	    for (; ; ) {
		if (creatFile.eos())
		    throw new InvalidFileFormatException();

		String line = creatFile.readLine();
		if (line.startsWith(Separators.EOR))
		    break;

		tok = new StringTokenizer(line);
		if (!tok.hasMoreTokens())
		    throw new InvalidFileFormatException();

		String opt = tok.nextToken();

		int target, which, value, mode;

		if (opt.equals("t")) {
		    target = RandAttr.T_ALL;
		    which  = 0;
		}
		else if (opt.equals("a")) {
		    target = RandAttr.T_ATTR;
		    which  = Integer.parseInt(tok.nextToken());
		}
		else if (opt.equals("l")) {
		    target = RandAttr.T_ROW;
		    which  = Integer.parseInt(tok.nextToken());
		}
		else if (opt.equals("c")) {
		    target = RandAttr.T_COL;
		    which  = Integer.parseInt(tok.nextToken());
		}

		else
		    throw new InvalidFileFormatException();

		value = Integer.parseInt(tok.nextToken());

		mode = RandAttr.M_CHANGE;
		if (tok.hasMoreTokens()) {
		    String m = tok.nextToken();
		    if (m.equals("+"))
			mode = RandAttr.M_ADD;
		    else if (m.equals("-"))
			mode = RandAttr.M_SUB;
		}

		randModifiers.insert(new RandAttr(target, which, value, mode));
	    }
	    randModifiers.trim();

	}
	catch(IOException e) {
	    throw new InvalidFileFormatException();
	}
	catch(NumberFormatException e) {
	    throw new InvalidFileFormatException();
	}

	++numberOfProtos;
    }

    int getAttr(int row, int column) {
	return attr[row * 4 + column];
    }

    int getAttr(int ind) {
	return attr[ind];
    }

    void setAttr(int row, int column, int val) {
	attr[row * 4 + column] = val;
    }

    void setAttr(int ind, int val) {
	attr[ind] = val;
    }

    void setAttrRow(int row, int val) {
	for (int i = 0; i < 5; ++i)
	    setAttr(row, i, val);
    }

    void setAttrColumn(int column, int val) {
	for (int i = 0; i < 4; ++i)
	    setAttr(i, column, val);
    }

    void setAllAttr(int v) {
	for (int i = 0; i < attr.length; ++i)
	    attr[i] = v;
    }

    protected void finalize() {
	--numberOfProtos;
    }

    public Creature create() {
	Creature cr = null;

	Shop sh = theWorld.findShopByKeeperId(theId);

	if (sh == null)
	    cr = new Creature(this);
	else {
	    ShopCreature sc = new ShopCreature(this);
	    sc.bindShop(sh);
	    cr = sc;
	}

	Master mstr = theWorld.findMasterById(theId);
	if (mstr != null)
	    cr.addRole(mstr);

	return cr;
    }

    int getNumberOfCreatures() {
	return numberOfCreatures;
    }

    public int getId() {
	return theId;
    }

    public String getName() {
	return theName;
    }

    boolean hasId(int id) {
	return id == theId;
    }

    boolean hasName(String name) {
	return StrUtil.findAliasPrefix(name, theAliases);
    }

    ////////////
    // Sheetable

    private String randModList() {
	String list = "";
	for (Enumeration enum = randModifiers.elements(); enum.hasMoreElements(); ) {
	    RandAttr ra = (RandAttr) enum.nextElement();
	    list += Separators.NL + ra.getSheet();
	}
	return list;
    }

    private String getSheetLine(int row) {
	String line = "";
	for (int c = 0; c < 4; ++c) {
	    int i = row * 4 + c;
	    line += StrUtil.leftWidth(Char.attrLabels[i], 3) + ": " + StrUtil.formatNumber(attr[i], 3) + "  ";
	}
	return line;
    }

    private String getSheetAttr() {
	String sh = "";
	for (int r = 0; r < 5; ++r)
	    sh += Separators.NL + getSheetLine(r);
	return sh;
    }

    public void sendSheet(Char rcpt) {
	rcpt.send("CRIATURA - ID: [" + getId() + "]  Nível: [" + theLevel  + "]  Tipo: [" + theType + "]" + Separators.NL +
		  "Nome: [" + theName + "]" + Separators.NL +
		  "Sinônimos: " + theAliases + Separators.NL +
		  "Propriedades: " + StrUtil.makeFlags(theProperties) + Separators.NL +
		  "Ganchos: ");

	Creature.sendHookStr(rcpt, theHooks);

	rcpt.send("Experiência: " + experience + "  Comportamento: " + Char.strBehavior(goodBehavior, badBehavior) + Separators.NL +
		  "Quantidade: [" + numberOfCreatures + "]" +

		  getSheetAttr() + randModList() + Separators.NL + "Finanças: " +

		  money);
    }

    // Sheetable
    ////////////

}
