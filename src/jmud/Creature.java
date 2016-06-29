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
import java.util.Random;
import java.util.NoSuchElementException;
import java.util.Enumeration;

import jmud.hook.Hook;
import jmud.util.StrUtil;
import jmud.util.Separators;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.jgp.Enchainer;
import jmud.jgp.ObjectMatcher;
import jmud.jgp.NameExtractor;
import jgp.container.Set;
import jgp.algorithm.Searcher;

public class Creature extends Char {

    static final int P_WALKER = Bit.BIT0;

    static final String propLabels[] = {
	"andarilho"
    };

    static final int T_HUMAN = 0;
    static final int T_BEAST = 1;

    static final String typeLabels[] = {
	"humano",
	"animal"
    };

    private CreatureProto prototype  = null;
    private int           theId;
    private int           theType;
    private String        theAliases = null;
    private String        theBrief   = null;
    private int           theProperties;
    private Set           theHooks = new Set();

  Creature(CreatureProto cr) {
    prototype      = cr;
    theType        = cr.theType;
    theId          = cr.theId;
    theName        = cr.theName;
    theAliases     = cr.theAliases;
    theBrief       = cr.theBrief;
    theDescription = cr.theDescription;
    theLevel       = cr.theLevel;
    sex            = cr.sex;
    theProperties  = cr.theProperties;
    experience     = cr.experience;
    goodBehavior   = cr.goodBehavior;
    badBehavior    = cr.badBehavior;

    // eventualmente pode ser necessario
    // copiar os Hooks do prototipo,
    // caso eles precisem ser modificados
    theHooks       = cr.theHooks;

    money          = new Money(cr.money);

    readAttr();
    randAttr();
    attrToReal();

    ++prototype.numberOfCreatures;
  }

    void readAttr() {
	for (int i = 0; i < realAttr.length; ++i)
	    attr[i] = prototype.attr[i];

	stam = getAttr(A_STA);
	will = getAttr(A_WIL);
	soul = getAttr(A_SOU);
	conf = getAttr(A_CON);
    }

    private int rndChange(int mode, int value) {
	int change = 0;

	int x = Rnd.nextInt();
	if (x < 0)
	    x = -x;
	switch(mode) {
	case RandAttr.M_CHANGE:
	    change = (x % ((value << 1) + 1)) - value;
	    break;
	case RandAttr.M_ADD:
	    change = x % (value + 1);
	    break;
	case RandAttr.M_SUB:
	    change = -(x % (value + 1));
	    break;
	default:
	    Log.err("Modo inválido de modificador aleatório: " + mode);
	}

	return change;
    }

    void randAttr() {
	for (Enumeration enum = prototype.randModifiers.elements(); enum.hasMoreElements(); ) {
	    RandAttr ra = (RandAttr) enum.nextElement();

	    int target = ra.getTarget();
	    int which = ra.getWhich();
	    int value = ra.getValue();
	    int mode = ra.getMode();

	    switch(target) {
	    case RandAttr.T_ALL:
		for (int i = 0; i < 20; ++i)
		    addAttr(i, rndChange(mode, value));
		break;
	    case RandAttr.T_ATTR:
		addAttr(which, rndChange(mode, value));
		break;
	    case RandAttr.T_ROW:
		for (int c = 0; c < 4; ++c)
		    addAttr(which, c, rndChange(mode, value));
		break;
	    case RandAttr.T_COL:
		for (int r = 0; r < 5; ++r)
		    addAttr(r, which, rndChange(mode, value));
		break;
	    default:
		Log.err("Tipo inválido de modificador aleatório: " + target);
	    }
	}
    }

  protected void finalize() {
    --prototype.numberOfCreatures;
  }

  boolean hasId(int id) {
    return id == theId;
  }

  public String getBrief(boolean showId) {
      if (showId)
	  return "[" + getId() + "] " + theBrief;
    return theBrief;
  }

  String getPrompt() {
    return "creature [" + getId() + "] " + super.getPrompt();
  }

    /*
  boolean canExecute(Command cmd) {
    return Char.R_USER >= cmd.getMinRank();
  }
    */

    /*
  boolean canCast(Magic mag) {
    return false;
  }
    */

  public boolean hasName(String name) {
    return StrUtil.findAliasPrefix(name, theAliases);
  }

    public boolean isFirstModerator() {
	return false;
    }

  public int getRank() {
    return Char.R_USER;
  }

    public String getColor(int color) {
	return "";
    }

    public String buildColor(int colorVector) {
	return "";
    }

    public String getNormalColor() {
	return "";
    }

    public boolean isOmni() {
	return false;
    }

    public void promote(int aRank) {
	Log.err("Tentativa de promover uma criatura: " + theId + ", " + theName);
    }

    ////
    //

    private void doWalk() {

	if (getPosition() != P_STANDING)
	    return;

	Place plc = getPlace();
	if (plc.isRoom()) {
	    Room rm = (Room) plc;
	    int dir = DicePool.d6.roll() - 1;

	    Door dr = rm.findDoorByDir(dir);
	    if (dr != null) {
		    leaderWalkTo(dr);
		    Log.debug("Andou: '" + getName() + "'");
		}
	}
    }

    // chamado a cada hora do mundo
    void doAction() {
	if (Bit.isSet(theProperties, P_WALKER))
	    doWalk();
    }

    //
    ////

    public boolean isPlayer() {
	return false;
    }

    void addHook(Hook proc) {

      // evita modificacao nos ganchos do prototipo
      if (theHooks == prototype.theHooks)
	theHooks = new Set(prototype.theHooks);

      // avisa sobre duplicacao de gancho
      if (Searcher.linearSearch(theHooks, new ObjectMatcher(proc)) != null)
	Log.warn("Gancho duplicado '" + proc.getLabel() + "' para criatura: " + getId());

      theHooks.insert(proc);
    }

    public Enumeration getHookEnum() {
	return theHooks.elements();
    }

  void save() throws IOException {
    Log.err("Tentativa de gravar uma criatura: [" +  getId() + "] " + theName);
  }

  /////////
  // Owner:

  public int getId() {
    return theId;
  }

  public String getOwnerType() {
    return "criatura";
  }

  /////////////
  // Sheetable:

    static void sendHookStr(Recipient rcpt, Set hooks) {
	Enchainer.wrapNone("Nenhum", rcpt, Enchainer.headedList(rcpt, hooks.elements(), new NameExtractor(), " "));
    }

  public void sendSheet(Char rcpt) {
    rcpt.send("CRIATURA - ID: [" + theId + "]  Posto: " + getRankName() + " [" + getRankName() + "]  Nível: [" + theLevel  + "]  Tipo: " +  StrUtil.strTable(theType, typeLabels) + " [" + theType + "]"+ Separators.NL +
	"Nome: [" + theName + "]" + Separators.NL +
	"Sinônimos: " + theAliases + Separators.NL +
	"Propriedades: " + StrUtil.wrapList(StrUtil.listFlags(theProperties, propLabels), "Nenhuma") + Separators.NL +
	"Onde: " + getWhere() + Separators.NL +
	"Ganchos: ");

    sendHookStr(rcpt, theHooks);

    super.sendSheet(rcpt);
  }

}
