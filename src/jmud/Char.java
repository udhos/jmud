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

// 	$Id: Char.java,v 1.1.1.1 2001/11/14 09:01:49 fmatheus Exp $

import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import jmud.util.StrUtil;
import jmud.util.Separators;
import jmud.util.IntUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.job.Job;
import jmud.job.JobTitle;
import jmud.ability.Ability;
import jmud.ability.Proficiency;
import jmud.hook.Hook;
import jmud.magic.Magic;
import jmud.command.Command;
import jmud.command.IndexedToken;
import jmud.jgp.Enchainer;
import jmud.jgp.FancyNameExtractor;
import jmud.jgp.VisibilityNameMatcher;
import jmud.jgp.AbilityMatcher;
import jmud.jgp.JobMatcher;
import jmud.jgp.NameMatcher;
import jmud.jgp.NameExtractor;
import jmud.jgp.ObjectMatcher;
import jmud.jgp.StringExtractor;
import jmud.jgp.SheetExtractor;
import jmud.jgp.MasterDetector;
import jmud.jgp.GroupMemberDetector;
import jmud.jgp.ItemCloneDetector;
import jgp.container.List;
import jgp.container.Vector;
import jgp.container.Set;
import jgp.interfaces.Iterator;
import jgp.interfaces.Enumerable;
import jgp.algorithm.Searcher;
import jgp.algorithm.Transformer;
import jgp.adaptor.Finder;
import jgp.predicate.UnaryPredicate;
import jgp.functor.UnaryFunction;

public abstract class Char implements Owner, Affectable, Named, FancyNamed, UniqueId, Briefed, Viewable {

    protected static Manager theManager = null;
    protected static World   theWorld   = null;

    static void setManager(Manager mgr) {
	theManager = mgr;
    }

    static void setWorld(World wld) {
	theWorld = wld;
    }

    public static final int R_DISABLED  = 0;
    public static final int R_GUEST     = 1;
    public static final int R_USER      = 2;
    public static final int R_ADMIN     = 3;
    public static final int R_MODERATOR = 4;
    public static final int R_THRESHOLD = 5;

    static final String rankNames[] = {
	"nenhum",
	"visitante",
	"usuário",
	"administrador",
	"moderador",
	"limiar"
    };

    static final int S_NEUTRAL = 0;
    static final int S_MALE    = 1;
    static final int S_FEMALE  = 2;

    static final String sexLabels[] = {
	"neutro",
	"macho",
	"fêmea"
    };

    public static final int P_DISABLED      = 0;
    public static final int P_DEAD          = 1;
    public static final int P_UNCONSCIOUS   = 2;
    public static final int P_INCAPACITATED = 3;
    public static final int P_ASLEEP        = 4;
    public static final int P_RESTING       = 5;
    public static final int P_SITTING       = 6;
    public static final int P_STANDING      = 7;
    public static final int P_AWARE         = 8;
    public static final int P_FIGHTING      = 9;

    static final String positionLabels[] = {
	"desabilitad*",
	"mort*",
	"inconsciente",
	"incapacitad*",
	"dormindo",
	"descansando",
	"sentad*",
	"levantad*",
	"em guarda",
	"lutando"
    };

    protected Client    theClient       = null;
    protected String    theName         = null;
    protected String    theDescription  = "";
    protected Place     thePlace        = null;
    protected List      theItems        = new List();
    protected int       theLevel        = 0;
    protected List      theEffects      = new List();
    protected int       thePosition     = P_STANDING;
    protected int       sex             = S_NEUTRAL;

    protected int       experience      = 0;
    protected int       totalExperience = 0;
    protected int       goodBehavior    = 0;
    protected int       badBehavior     = 0;

    protected int       fights          = 0;
    protected int       kills           = 0;
    protected int       flees           = 0;
    protected int       deaths          = 0;

    protected Char      enemy           = null;
    protected int       localDamage     = 0;

    protected int       invisibilityRank  = 0;
    protected int       invisibilityLevel = 0;

    protected Vector    spells            = new Vector();
    protected Set       jobs              = new Set();
    protected Set       abilities         = new Set();
    protected Set       roles             = new Set();

    static final int I_GROUPED = Bit.BIT0;

    static final String infoLabels[] = {
	"grupo"
    };

    protected int       infoFlags       = 0;

    public int getInvisRank() {
	return invisibilityRank;
    }

    public void setInvisRank(int invisRank) {
	invisibilityRank = invisRank;
    }

    public int getInvisLevel() {
	return invisibilityLevel;
    }

    public boolean isGrouped() {
	return Bit.isSet(infoFlags, I_GROUPED);
    }

    public void group() {
	infoFlags = Bit.set(infoFlags, I_GROUPED);
    }

    public void ungroup() {
	infoFlags = Bit.reset(infoFlags, I_GROUPED);
    }

    protected Item      theEq[] = {
	null, // 0
	null,
	null,
	null,
	null,
	null, // 5
	null,
	null,
	null,
	null,
	null, // 10
	null,
	null,
	null,
	null,
	null, // 15
	null,
	null,
	null,
	null,
	null, // 21
	null
    };

    public static final int C_PHY = 0;
    public static final int C_MEN = 1;
    public static final int C_MYS = 2;
    public static final int C_SOC = 3;

    public static final int R_PER = 0;
    public static final int R_AGI = 1;
    public static final int R_ACT = 2;
    public static final int R_RES = 3;
    public static final int R_VIT = 4;

    public static final int A_SEN = 0;
    public static final int A_ALE = 1;
    public static final int A_AWA = 2;
    public static final int A_SLY = 3;
    public static final int A_DEX = 4;
    public static final int A_WIT = 5;
    public static final int A_GHO = 6;
    public static final int A_MAN = 7;
    public static final int A_STR = 8;
    public static final int A_INT = 9;
    public static final int A_AUR = 10;
    public static final int A_CHA = 11;
    public static final int A_BOD = 12;
    public static final int A_MIN = 13;
    public static final int A_SPI = 14;
    public static final int A_APP = 15;
    public static final int A_STA = 16;
    public static final int A_WIL = 17;
    public static final int A_SOU = 18;
    public static final int A_CON = 19;

    public static final String attrLabels[] = {
	"sentidos", "atenção",      "consciência", "malícia",
	"destreza", "raciocínio",   "fantasma",    "manipulação",
	"força",    "inteligência", "aura",        "carisma",
	"corpo",    "mente",        "espírito",    "aparência",
	"vigor",    "vontade",      "alma",        "confiança"
    };

    public static final String attrNames[] = {
	"sentidos", "atencao",      "consciencia", "malicia",
	"destreza", "raciocinio",   "fantasma",    "manipulacao",
	"forca",    "inteligencia", "aura",        "carisma",
	"corpo",    "mente",        "espirito",    "aparencia",
	"vigor",    "vontade",      "alma",        "confianca"
    };


    /*           PHYSICAL  MENTAL       MYSTIC    SOCIAL
		 PERCEPTION sense     alertness    awareness slyness
		 AGILITY    dexterity witness      ghost     manipulation
		 ACTION     strength  intelligence aura      charisma
		 RESISTANCE body      mind         spirit    appearance
		 VITALITY   stamina   will         soul      confidence
    */

    public static final String attrRowNames[] = {
        "percepcao", "agilidade", "acao", "resistencia", "vitalidade"
    };

    public static final String attrColumnNames[] = {
        "fisico", "mental", "mistico", "social"
    };

    public static final String attrRowLabels[] = {
        "percepção", "agilidade", "ação", "resistência", "vitalidade"
    };

    public static final String attrColumnLabels[] = {
        "físico", "mental", "místico", "social"
    };

    protected int realAttr[] = {
	1, 1, 1, 1,
	1, 1, 1, 1,
	1, 1, 1, 1,
	1, 1, 1, 1,
	1, 1, 1, 1
    };

    protected int attr[] = {
	1, 1, 1, 1,
	1, 1, 1, 1,
	1, 1, 1, 1,
	1, 1, 1, 1,
	1, 1, 1, 1
    };

    /* "fuel" */
    protected int stam = 1;
    protected int will = 1;
    protected int soul = 1;
    protected int conf = 1;

    // Modificados por equipamentos
    protected int attackPool[]     = { 0, 0, 0, 0 };
    protected int defensePool[]    = { 0, 0, 0, 0 };
    protected int damagePool[]     = { 0, 0, 0, 0 };
    protected int resistancePool[] = { 0, 0, 0, 0 };

    // Creature e Player inicializam money
    Money money = null;

    Char   leader    = null;
    Vector followers = new Vector();

    public Char getLeader() {
	return leader;
    }

    Char getGroupHead() {
	return isGrouped() ? getLeader().getGroupHead() : this;
    }

    Char getGroupHead(Place plc) {

	if (!isGrouped())
	    return this;

	Char ldr = getLeader();
	if (ldr.getPlace() != plc)
	    return this;

	return ldr.getGroupHead(plc);
    }

    public Vector getFollowers() {
	return followers;
    }

    public void startFollow(Char newLeader) {
	leader = newLeader;
	leader.getFollowers().insert(this);
    }

    public void stopFollow() {
	ungroup();
	leader.getFollowers().remove(this);
	leader = null;
    }

    public void disableFollowing() {
	if (getLeader() != null)
	    stopFollow();
	//Vector fol = new Vector(getFollowers());
	Vector fol = getFollowers();
	for (Enumeration e = fol.elements(); e.hasMoreElements(); ) {
	    Char ch = (Char) e.nextElement();
	    ch.stopFollow();
	}
    }

    public boolean tightlyFollows(Char ch) {
	return leader == ch;
    }

    public boolean follows(Char ch) {
	return leader != null && (tightlyFollows(ch) || leader.follows(ch));
    }

    public Money getMoney() {
	return money;
    }

    public void setMoney(Money mon) {
	money = mon;
    }

    public int getStam() { return stam; }
    public int getWill() { return will; }
    public int getSoul() { return soul; }
    public int getConf() { return conf; }

    public void setStam(int st) { stam = st; }
    public void setWill(int wi) { will = wi; }
    public void setSoul(int so) { soul = so; }
    public void setConf(int co) { conf = co; }

    /////////////
    // Atributos:

    public void attrToReal() {
	for (int i = 0; i < realAttr.length; ++i)
	    realAttr[i] = attr[i];
    }

    public void attrFromReal() {
	for (int i = 0; i < realAttr.length; ++i)
	    attr[i] = realAttr[i];
    }

    // lendo:

    // Reais:

    public int getRealAttr(int row, int column) {
	return realAttr[row * 4 + column];
    }

    public int getRealAttr(int ind) {
	return realAttr[ind];
    }

    // Efetivos:

    public int getAttr(int row, int column) {
	return attr[row * 4 + column];
    }

    public int getAttr(int ind) {
	return attr[ind];
    }

    // mudando:

    // Reais:

    public void setRealAttr(int row, int column, int val) {
        realAttr[row * 4 + column] = val;
    }

    public void setRealAttr(int ind, int val) {
        realAttr[ind] = val;
    }

    public void setRealAttrRow(int row, int val) {
        for (int i = 0; i < 4; ++i)
            setRealAttr(row, i, val);
    }

    public void setRealAttrColumn(int column, int val) {
        for (int i = 0; i < 5; ++i)
            setRealAttr(i, column, val);
    }

    public void setAllRealAttr(int v) {
        for (int i = 0; i < attr.length; ++i)
            realAttr[i] = v;
    }

    // Efetivos:

    public void setAttr(int row, int column, int val) {
	attr[row * 4 + column] = val;
    }

    public void setAttr(int ind, int val) {
	attr[ind] = val;
    }

    public void setAttrRow(int row, int val) {
	for (int i = 0; i < 4; ++i)
	    setAttr(row, i, val);
    }

    public void setAttrColumn(int column, int val) {
	for (int i = 0; i < 5; ++i)
	    setAttr(i, column, val);
    }

    public void setAllAttr(int v) {
	for (int i = 0; i < attr.length; ++i)
	    attr[i] = v;
    }

    // incrementando:

    public void addAttr(int row, int column, int val) {
	attr[row * 4 + column] += val;
    }

    public void addAttr(int ind, int val) {
	attr[ind] += val;
    }

    public void addAttrRow(int row, int val) {
	for (int c = 0; c < 4; ++c)
	    setAttr(row, c, getAttr(row, c) + val);
    }

    public void addAttrColumn(int column, int val) {
	for (int r = 0; r < 5; ++r)
	    setAttr(r, column, getAttr(r, column) + val);
    }

    public void addAllAttr(int val) {
	for (int i = 0; i < attr.length; ++i)
	    attr[i] += val;
    }

    //
    ///////////////

    /////////////////////
    // Envia uma mensagem
    //
    // A classe Client cuida de preceder rajadas de mensagens por
    // quebras de linhas.

    private void snd(String msg) {
	if (theClient != null)
	    theClient.send(msg);
    }

    ////////////
    // Recipient

    public void snd(Object obj) {
	snd((String) obj);
    }

    // Recipient
    ////////////

    void send() {
	snd(Separators.NL);
    }

    public void send(String msg) {
	send();
	snd(msg);
    }

    // Envia uma mensagem
    /////////////////////

    void enterPlace(Place target) {
	thePlace = target;
	thePlace.insertChar(this);
    }

    void leavePlace() {
	try {
	    thePlace.removeChar(this);
	}
	catch(NoSuchElementException e) {
	    Log.err("Personagem '" + getName() + "' não encontrado ao ser removido de: " + thePlace.getOwnerType() + " [" + thePlace.getId() + "]");
	}
	thePlace = null;
    }

    public void moveTo(Place target) {
	leavePlace();
	enterPlace(target);
    }

    void walkTo(Place oldPlace, Place target, int dir) {
	moveTo(target);
	oldPlace.actionNotToChar("$p saiu para " + Door.getDirectionName(dir) + ".", true, this);
	getPlace().actionNotToChar("$p chegou.", true, this);
	look();

	Hook.apply(target, Hook.CTX_ARRIVAL, this, -1);
    }

    boolean tryWalkTo(Place from, Door dr) {
	int pos = getPosition();
	if (pos < P_STANDING || pos > P_AWARE)
	    return false;

	if (!dr.isOpen()) {
	    send("A passagem está fechada nessa direção.");
	    return false;
	}

	int dir = dr.getDirection();

	if (Hook.apply(from, Hook.CTX_WALK, this, dir) > 0)
	  return false;

	walkTo(from, dr.getDestinationRoom(), dir);
	return true;
    }

    public void leaderWalkTo(Door dr) {
	Place pl = getPlace();
	if (tryWalkTo(pl, dr)) {
	    for (Enumeration e = getFollowers().elements(); e.hasMoreElements(); ) {
		Char ch = (Char) e.nextElement();
		if (pl == ch.getPlace() && this.canBeSeenBy(ch)) {
		    ch.send("Você segue " + getName() + ".");
		    ch.leaderWalkTo(dr);
		}
	    }
	}
    }

    static final String SEE_NOTHING = "Você não vê nada de especial.";
    static final String YOU_SEE     = "Você vê:" + Separators.NL;

    public void lookAt(Char ch) {
	send(YOU_SEE + ch.getFullDescription(this));
    }

    public void lookAt(Item it) {
	send(YOU_SEE + it.getFullDescription(this));
    }

    String getFullDescription(Char looker) {
	return (((theDescription.length() == 0) ? SEE_NOTHING : theDescription) + getEqList(looker));
    }

    void lookAt(Place aPlace) {
	send(aPlace.getFullDescription(this));
    }

    public void look() {
	lookAt(getPlace());
    }

    public String getName() {
	return theName;
    }

    public String getFancyName() {
	String name = getName();

	int invis = getInvisLevel();
	if (invis > 0)
	    name += " <" + invis + ">";

	int rank = getInvisRank();
	if (rank > 0)
	    name += " <" + rank + ">";

	return name;
    }

    public int getLevel() {
	return theLevel;
    }

    public int getExp() {
	return experience;
    }

    public void setExp(int exp) {
	experience = exp;
    }

    void gainExp(int exp) {

	exp = IntUtil.max(exp, 1);
	totalExperience += exp;

	setExp(getExp() + exp);
	send("Você ganhou " + exp + " ponto(s) de experiência.");
    }

    void groupExp(int exp) {
	int members = 1;
	for (Enumeration e = getFollowers().elements(); e.hasMoreElements(); ) {
	    Char ch = (Char) e.nextElement();
	    if (ch.isGrouped())
		++members;
	}
	if (members == 1)
	    gainExp(exp);
	else {
	    int xp = exp / members;
	    gainExp(xp);
	    for (Enumeration e = getFollowers().elements(); e.hasMoreElements(); ) {
		Char ch = (Char) e.nextElement();
		if (ch.isGrouped())
		    ch.groupExp(xp);
	    }
	}
    }

    void shareExp(int exp) {
	getGroupHead(getPlace()).groupExp(exp);
    }

    int getGoodBehavior() {
	return goodBehavior;
    }

    int getBadBehavior() {
	return badBehavior;
    }

    int getTotalExp() {
	return totalExperience;
    }

    String getExpStr() {
	return getExp() + " (" + getTotalExp() + ")";
    }

    public int getPosition() {
	return isFighting() ? P_FIGHTING : thePosition;
    }

    public void setPosition(int pos) {
	thePosition = pos;
    }

    public boolean isFemale() {
	return sex == S_FEMALE;
    }

    public static String getPositionLabel(int pos, boolean female) {
	return StrUtil.adjustSex(positionLabels[pos], female);
    }

    String getPositionLabel() {
	return getPositionLabel(getPosition(), isFemale());
    }

    public void setDescription(String desc) {
	theDescription = desc;
    }

    public String getDescription() {
	return theDescription;
    }

    boolean canSeeAnyOf(Char ch, Char vict, Item it1, Item it2) {
	return (ch != null && ch.canBeSeenBy(this)) ||
	    (vict != null && vict.canBeSeenBy(this)) ||
	    (it1 != null && it1.canBeSeenBy(this)) ||
	    (it2 != null && it2.canBeSeenBy(this));
    }

    String replaceCharName(String act, String simb, Char ch) {
	return StrUtil.replace(act, simb, (ch != null && ch.canBeSeenBy(this)) ? ((ch == this) ? "você" : ch.getName()) : "alguém");
    }

    String replaceItemName(String act, String simb, Item it) {
	return StrUtil.replace(act, simb, (it != null && it.canBeSeenBy(this)) ? it.getName() : "algo");
    }

    /*
      act = cadeia de caracteres podendo conter os seguintes símbolos:
      $p - personagem agente da ação - ch
      $P - personagem vítima da ação - vict
      $i - item primário             - it1
      $I - item secundário           - it2
      hide = esconder ação se ela não puder ser enxergada
    */

    // 2 personagens e 2 itens
    public void action(String act, boolean hide,
		Char ch, Char vict,
		Item it1, Item it2) {
	if (hide && !canSeeAnyOf(ch, vict, it1, it2))
	    return;

	String ac = new String(act);
	ac = replaceCharName(ac, "$p", ch);
	ac = replaceCharName(ac, "$P", vict);
	ac = replaceItemName(ac, "$i", it1);
	ac = replaceItemName(ac, "$I", it2);

	send(StrUtil.upcaseFirst(ac));
    }

    // 2 personagens e 1 item
    public void action(String act, boolean hide,
		Char ch, Char vict,
		Item it1) {
	action(act, hide, ch, vict, it1, null);
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

    public Place getPlace() {
	return thePlace;
    }

    public Client getClient() {
	return theClient;
    }

    Item searchItem(String name, int ind, Char looker) {
	Item it = findItemByName(name, ind, looker);
	if (it == null) {
	    try {
		it = findEqByName(name, ind, looker);
	    }
	    catch (NoSuchElementException e) {
	    }
	    if (it == null)
		it = getPlace().findItemByName(name, ind, looker);
	}
	return it;
    }

    static final String ROOM = "sala";
    static final String INVENTORY = "inventario";
    static final String EQUIPMENT = "equipamentos";

    public Item searchItem(IndexedToken it, Char looker) {

	String ctx  = it.getContext();
	String name = it.getTarget();
	int    ind  = it.getIndex();

	if (it.hasContext()) {
	    if (INVENTORY.startsWith(ctx))
		return findItemByName(name, ind, looker);
	    if (EQUIPMENT.startsWith(ctx))
		try {
		    return findEqByName(name, ind, looker);
		}
	    catch (NoSuchElementException e) {
	    }
	    if (ROOM.startsWith(ctx))
		return getPlace().findItemByName(name, ind, looker);
	}
	return searchItem(name, ind, looker);
    }

    //////////////
    // Equippable:

    public int findEqPositionByName(String name, int ind, Char looker)
	throws NoSuchElementException {
	for (int i = 0; i < Item.WEAR_POSITIONS; ++i) {
	    Item it = theEq[i];
	    if (it != null && it.canBeSeenBy(looker) && it.hasName(name) &&
		--ind == 0)
		return i;
	}
	throw new NoSuchElementException();
    }

    public Item findEqByName(String name, int ind, Char looker) throws NoSuchElementException {
	return theEq[findEqPositionByName(name, ind, looker)];
    }

    public int findEqPosition(Item it) throws NoSuchElementException {
	for (int i = 0; i < Item.WEAR_POSITIONS; ++i)
	    if (theEq[i] == it)
		return i;
	throw new NoSuchElementException();
    }

    // apenas para itens que não sejam multi-veste
    public int findWearPosition(Item it) throws NoSuchElementException {
	for (int wearPos = 0; wearPos < Item.WEAR_POSITIONS; ++wearPos)
	    if (canWearOn(it, wearPos))
		return wearPos;
	throw new NoSuchElementException();
    }

    public boolean canWear(Item it) {
	if (it.wearAll()) {
	    int currFlag = 1;
	    for (int i = 0; i < Item.WEAR_POSITIONS; ++i) {
		if (Bit.isSet(it.getWearVector(), currFlag) && theEq[i] != null)
		    return false;
		currFlag <<= 1;
	    }
	    return true;
	}

	try {
	    findWearPosition(it);
	    return true;
	}
	catch(NoSuchElementException e) {
	}

	return false;
    }

    public void wear(Item it) {
	if (!canWear(it)) {
	    Log.err("Tentativa de equipar item sem posição apropriada.");
	    return;
	}

	if (it.wearAll()) {
	    int currFlag = 1;
	    for (int i = 0; i < Item.WEAR_POSITIONS; ++i) {
		if (Bit.isSet(it.getWearVector(), currFlag))
		    theEq[i] = it;
		currFlag <<= 1;
	    }
	    affectPool(it);
	}
	else
	    wearOn(it, findWearPosition(it));

    }

    // apenas para equipamentos que não sejam multi-veste
    public boolean canWearOn(Item it, int wearPos) {
	return !it.wearAll() && Bit.isSet(it.getWearVector(), 1 << wearPos) && theEq[wearPos] == null;
    }

    // apenas para equipamentos que não sejam multi-veste
    public void wearOn(Item it, int wearPos) {
	if (it.wearAll()) {
	    Log.err("Tentativa de equipar item multi-veste [" + it.getId()+ "] em uma posição específica: " + wearPos);
	    return;
	}

	if (!canWearOn(it, wearPos)) {
	    Log.err("Tentativa de vestir item em posição ocupada/inválida: " + wearPos);
	    return;
	}

	theEq[wearPos] = it;

	affectPool(it);
    }

    public void takeOffEq(int wearPos) throws NoSuchElementException {
	Item it = theEq[wearPos];

	if (it == null)
	    throw new NoSuchElementException();

	takeOffEq(it);
    }

    public void takeOffEq(Item it) {
	for (int i = 0; i < Item.WEAR_POSITIONS; ++i)
	    if (theEq[i] == it)
		theEq[i] = null;

	unnaffectPool(it);
    }

    public Item getEq(int ind) {
	return theEq[ind];
    }

    public String getEqList(Char looker) {
	String eq = "";

	int wearFlags = 0;
	int currFlag  = 1;
	for (int i = 0; i < Item.WEAR_POSITIONS; ++i) {
	    if (theEq[i] != null && !Bit.isSet(wearFlags, currFlag)) {
		wearFlags = Bit.set(wearFlags, currFlag);

		eq += Separators.NL + StrUtil.rightPad("<" + Item.wearPositions[i] + ">", 17) + (theEq[i].canBeSeenBy(looker) ? theEq[i].getFancyName() : "algo");

		if (theEq[i].wearAll()) {

		    int j = i + 1;
		    int auxFlag = currFlag << 1;
		    for (; j < Item.WEAR_POSITIONS; ++j) {
			if (theEq[j] == theEq[i] && !Bit.isSet(wearFlags, auxFlag)) {
			    wearFlags = Bit.set(wearFlags, auxFlag);

			    eq += Separators.NL + StrUtil.rightPad("<" + Item.wearPositions[j] + ">", 17) + (theEq[i].canBeSeenBy(looker) ? theEq[i].getFancyName() : "algo") + " *";

			} // if
			auxFlag <<= 1;
		    } // for

		} // if wearAll
	    }

	    currFlag <<= 1;
	} // for


	return eq;
    }

    void extractEqs() {
	for (int i = 0; i < Item.WEAR_POSITIONS; ++i)
	    if (theEq[i] != null) {
		Owner ow = theEq[i].getContainer();
		if (ow != null)
		    ow.extractItems();
		theWorld.removeItem(theEq[i]); // faz drop, takeoffeq
	    }
    }

    // Equippable
    /////////////

    String getPrompt() {
	return stam + "/" + getAttr(A_STA) + " " + will + "/" + getAttr(A_WIL) + " " + soul + "/" + getAttr(A_SOU) + " " + conf + "/" + getAttr(A_CON) + "> ";
    }

    public abstract String getColor(int color);
    public abstract String buildColor(int colorVector);
    public abstract String getNormalColor();

    public abstract boolean isOmni();

    //////////////////////////
    // Implementação de Owner:

    abstract public int getId();

    public String getOwnerType() {
	return "personagem";
    }

    private void addLoadOf(Item it) {
    }

    private void subLoadOf(Item it) {
    }

    public void insertItem(Item it) {
	CloneAlgo.insert(theItems, it, new ItemCloneDetector());
	addLoadOf(it);
    }

    public void removeItem(Item anItem) throws NoSuchElementException {
	theItems.remove(anItem);
	subLoadOf(anItem);
    }

    public Item findItemByName(IndexedToken it, Char looker) {
	return (Item) Searcher.linearSearch(theItems, new VisibilityNameMatcher(it.getTarget(), looker), it.getIndex());
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
	return thePlace;
    }

    public Enumeration getContents() {
	return theItems.elements();
    }

    //
    //////////////////////////

    abstract public String getBrief(boolean showId);

    abstract boolean hasId(int id);

    public boolean canExecute(Command cmd) {
	return getRank() >= cmd.getMinRank();
    }

    // abstract boolean canCast(Magic mag);

    abstract public boolean hasName(String name);

    public abstract boolean isFirstModerator();

    abstract public int getRank();

    public boolean isAdmin() {
	return getRank() >= R_ADMIN;
    }

    public boolean isModerator() {
	return getRank() >= R_MODERATOR;
    }

    public boolean forbidAdminParam() {
	if (isAdmin())
	    return false;
	send("Parâmetro não disponível.");
	return true;
    }

    public boolean forbidAccess(Char vict) {
	if (vict.getRank() < getRank())
	    return false;
	send("Você não possui poder suficiente.");
	return true;
    }

    public static String getRankName(int rank) {
	if (rank >= R_THRESHOLD)
	    rank = R_THRESHOLD;
	return rankNames[rank];
    }

    String getRankName() {
	return getRankName(getRank());
    }

    public abstract void promote(int aRank);

    public abstract boolean isPlayer();

    abstract void save() throws IOException;

    /////////////////
    // Affectable:

    public void insertEffect(Effect eff) {
	theEffects.insert(eff);
    }

    public void removeEffect(Effect eff) {
	theEffects.remove(eff);
    }

    public void addStr(int value) {
	setAttr(A_STR, getAttr(A_STR) + value);
    }

    public void unnaffect() {
	for (Enumeration e = theEffects.elements(); e.hasMoreElements(); ) {
	    Effect eff = (Effect) e.nextElement();
	    theManager.removeEffect(eff);
	}
    }

    public Enumeration getEffects() {
	return theEffects.elements();
    }

    //
    /////////////////

    public void listEffects(Char rcpt) {
	Enchainer.headedList("Nenhum", rcpt, getEffects(), new SheetExtractor(), Separators.NL);
    }

    // O equipamento ja' deve estar equipado
    private void affectPool(Item it) {
	it.affectEquipper(this);
    }

    // O equipamento ainda deve estar equipado
    private void unnaffectPool(Item it) {
	it.unnaffectEquipper(this);
    }

    void addToAttackPool(int col, int val) {
	attackPool[col] += val;
    }

    void addToDefensePool(int col, int val) {
	defensePool[col] += val;
    }

    void addToDamagePool(int col, int val) {
	damagePool[col] += val;
    }

    void addToResistancePool(int col, int val) {
	resistancePool[col] += val;
    }


    /////////////
    // Fightable:

    int getAttack(int col) {
	return getAttr(R_AGI, col) + attackPool[col];
    }

    int getDefense(int col) {
	return getAttr(R_AGI, col) + defensePool[col];
    }

    int getDamage(int col) {
	return getAttr(R_ACT, col) + damagePool[col];
    }

    int getResistance(int col) {
	return getAttr(R_RES, col) + resistancePool[col];
    }

    Char getEnemy() {
	return enemy;
    }

    public void startFight(Char en) {
	if (isFighting())
	    return;

	getPlace().action("$p começa a lutar com $P.", true, this, en);

	enemy = en;
	theManager.enterFight(this);
	++fights;
	updatePosition();
    }

    void stopFight() {
	if (!isFighting())
	    return;

	enemy = null;
	theManager.leaveFight(this);
	updatePosition();
    }

    Item makeCorpse() {
	final int CORPSE_ID = 0;

	Item corpse = theWorld.findItemProtoById(CORPSE_ID).create();

	if (corpse == null)
	    Log.warn("Cadáver não encontrado: " + CORPSE_ID);
	else
	    theWorld.insertItem(corpse, getPlace());

	return corpse;
    }

    void transferItemsTo(Owner ow) {
	for (Enumeration items = theItems.elements(); items.hasMoreElements(); ) {
	    Item it = (Item) items.nextElement();
	    it.transferTo(ow); // drop (remove ou solta), giveTo
	}
    }

    void transferEqsTo(Owner ow) {
	for (int i = 0; i < Item.WEAR_POSITIONS; ++i) {
	    Item it = theEq[i];
	    if (it != null)
		it.transferTo(ow); // drop (remove ou solta), giveTo
	}
    }

    void transferAllItemsTo(Owner ow) {
	transferItemsTo(ow);
	transferEqsTo(ow);
    }

    void die() {
	if (isFighting())   // garante que existe um inimigo (enemy != null)
	    enemy.stopFight();

	stopFight();        // stopFight verifica se esta' lutando
	unnaffect();
	++deaths;
	disableFollowing();

	setExp(0);    // perde o XP temporario (nao utilizado)

	Item it = makeCorpse();
	if (it != null) {
	    Owner corpse = it.getContainer();
	    if (corpse == null)
		Log.warn("Cadáver não é um recipiente: '" + it.getName() +"' [" + it.getId() + "]");
	    else
		transferAllItemsTo(corpse);
	}

	getPlace().action("$p morre.", true, this);

	if (this instanceof Player)
	    moveTo(theWorld.getStartRoom());
	else
	    theWorld.extractChar(this);
    }

    void killedBy(Char en) {
	en.shareExp(getExp() >> 1);
	en.goodBehavior += getBadBehavior();
	en.badBehavior  += getGoodBehavior();
	++en.kills;

	die();
    }

    // procura o inimigo na sala, para atacar
    public void attack() {
	if (!isFighting())  // garante que nao vai atacar apos stopFight()
	    return;

	if (getEnemy().getPlace() != getPlace()) {
	    stopFight();
	    return;
	}

	if (getEnemy().getPosition() == P_DEAD) {
	    stopFight();
	    return;
	}

	prepareToFight(); // tenta levantar para atacar
	if (thePosition >= P_STANDING)
	    hitEnemy();
    }

    // ataca o respectivo inimigo
    void hitEnemy() {
	enemy.harmBy(this);

	if (isFighting() && (enemy.getPosition() != P_DEAD)) {
	    enemy.showCondition();
	    enemy.startFight(this);
	}
    }

    boolean isDeadlyHurt() {
	return -stam > getAttr(A_STA);
    }

    boolean isCommatous() {
	return -stam > getAttr(A_STA) >> 1;
    }

    boolean isSpanked() {
	return stam < 0;
    }

    void updatePosition() {
	if (isDeadlyHurt())
	    thePosition = P_DEAD;
	else if (isCommatous())
	    thePosition = P_UNCONSCIOUS;
	else if (isSpanked())
	    thePosition = P_INCAPACITATED;
	else if (thePosition >= P_DEAD && thePosition <= P_INCAPACITATED)
	    thePosition = P_RESTING;
    }

    // toma providencias referentes a ser espancado sem reagir
    void prepareToFight() {
	switch(thePosition) {
	case P_DISABLED:
	case P_DEAD:
	case P_UNCONSCIOUS:
	case P_INCAPACITATED:
	    break;
	case P_ASLEEP:
	    thePosition = P_RESTING;
	    getPlace().action("$p acorda.", true, this);
	    break;
	case P_RESTING:
	    thePosition = P_SITTING;
	    getPlace().action("$p senta-se.", true, this);
	    break;
	case P_SITTING:
	    thePosition = P_STANDING;
	    getPlace().action("$p levanta-se.", true, this);
	    break;
	case P_STANDING:
	    /* startFight(en); */
	    break;
	case P_AWARE:
	    /*
	      getPlace().action("$p reage violentamente!", true, this);
	      startFight(en);
	      hitEnemy();
	    */
	    break;
	default:
	    Log.err("Char.beenHitBy(...): personagem " + getName() + " com posição inválida: " + thePosition);
	}
    }

    // função de mais alto nível para tentar causar dano
    // faz todas as rolagens de dados necessárias
    // e, por fim, inflige o dano

    static final double P = 0.5;  // probabilidade basica (0 <= P <= 1)   = 50%
    static final double G = 0.05; // ganho por ponto      (0 <= G <= 0.5) =  5%

    void harmBy(Char en) {

	// probabilidade de ser atingido
	double p = en.hitProb(this);

	// numero aleatorio: r (0 < r <= 1)
	double r = Rnd.nextDouble();

	// sair se nao for atingido
	if (r > p) {
	    getPlace().action("$p erra $P.", true, en, this);
	    return;
	}

	// bonus
	int b = (int) Math.round((p - r) / G);

	// efeito bruto
	int m = en.getDamage(C_PHY) - getResistance(C_PHY);

	// efeito final
	int eff = (b > -m) ? (b + m) : 0;

	getPlace().action("$p causa " + eff + " ponto(s) de dano em $P.", true, en, this);

	acumulateDamage(eff);

	inflictLocalDamage();

	// deve ficar apos o updatePosition (chamado por inflictLocalDamage)
	if (thePosition == P_DEAD)
	    killedBy(en);
    }

    private double hitProb (Char vict) {
	// distancia de agilidade
	int d = getAttack(C_PHY) - vict.getDefense(C_PHY);

	// termo auxiliar
	double k = P + G * d;

	// probabilidade efetiva: p (G <= p <= 1-G) = (0 <= p <= 0.5)
	double p;
	double cG = 1 - G; // complemento de G (auxiliar)
	if (k > cG)
	    p = cG;
	else if (k < G)
	    p = G;
	else
	    p = k;

	return p;
    }

    private void acumulateDamage(int damage) {
	localDamage += damage;
    }

    void inflictLocalDamage() {
	stam -= localDamage;
	localDamage = 0;
	updatePosition(); // atualiza posição devido a ferimentos
    }

    private void showCondition() {

	switch(thePosition) {
	case P_DEAD:
	    //showCondition("$p está mort*.");
	    Log.warn("showCondition() para personagem morto.");
	    break;
	case P_UNCONSCIOUS:
	    showCondition("$p está inconsciente.");
	    break;
	case P_INCAPACITATED:
	    showCondition("$p está incapacitad*.");
	    break;
	default:
	    if (isPrettyHurt())
		showCondition("$p está gravemente ferid*.");
	    else if (isHurt())
		showCondition("$p está muito ferid*.");
	    else if (isWounded())
		showCondition("$p está ferid*.");
	    else if (isBruised())
		showCondition("$p está escoriad*.");
	    else
		showCondition("$p está em excelentes condições.");
	}
    }

    private void showCondition(String msg) {
	getPlace().action(StrUtil.adjustSex(msg, isFemale()), true, this);
    }


    boolean isPrettyHurt() {
	return stam < IntUtil.get1of4(getAttr(A_STA));
    }

    boolean isHurt() {
	return stam < getAttr(A_STA) >> 1;
    }

    boolean isWounded() {
	return stam < IntUtil.get3of4(getAttr(A_STA));
    }

    boolean isBruised() {
	return stam < getAttr(A_STA);
    }

    public boolean isFighting() {
	return enemy != null;
    }

    public void flee() {
	if (enemy.getEnemy() == this)
	    enemy.stopFight();
	stopFight();
	++flees;

	Place plc = getPlace();

	plc.action("$p tenta fugir em pânico!", true, this);
	Enumeration e = plc.getExits();

	if (e.hasMoreElements()) {
	    Door d = (Door) e.nextElement();
	    moveTo(d.getDestinationRoom());
	    plc.actionNotToChar("$p fugiu para " + d.getDirectionName() + ".", true, this);
	    look();
	    getPlace().actionNotToChar("$p chegou.", true, this);
	    return;
	}

	send("Impossível escapar!");
    }

    void fall() {
	getPlace().action("$p cai!", true, this);
        thePosition = P_SITTING;
    }

    //
    /////////////


    public void fullHeal() {
        stam = getAttr(A_STA);
        will = getAttr(A_WIL);
        soul = getAttr(A_SOU);
        conf = getAttr(A_CON);
        thePosition = P_STANDING;
    }

    void regenerate() {
        int st = getAttr(A_STA);
        int wi = getAttr(A_WIL);
        int so = getAttr(A_SOU);
        int co = getAttr(A_CON);

	int rStam;
	int rWill;
	int rSoul;
	int rConf;

	switch (getPosition()) {
	case P_ASLEEP:
	    rStam = st >> 1;
	    rWill = wi >> 1;
	    rSoul = so >> 1;
	    rConf = co >> 1;
	    break;
	case P_RESTING:
	    rStam = st >> 2;
	    rWill = wi >> 2;
	    rSoul = so >> 2;
	    rConf = co >> 2;
	    break;
	case P_SITTING:
	    rStam = st >> 3;
	    rWill = wi >> 3;
	    rSoul = so >> 3;
	    rConf = co >> 3;
	    break;
	case P_STANDING:
	    rStam = st >> 4;
	    rWill = wi >> 4;
	    rSoul = so >> 4;
	    rConf = co >> 4;
	    break;
	case P_AWARE:
	    rStam = st >> 5;
	    rWill = wi >> 5;
	    rSoul = so >> 5;
	    rConf = co >> 5;
	    break;
	case P_FIGHTING:
	    rStam = st >> 6;
	    rWill = wi >> 6;
	    rSoul = so >> 6;
	    rConf = co >> 6;
	    break;
	default:
	    rStam = 0;
	    rWill = 0;
	    rSoul = 0;
	    rConf = 0;
	}

	stam = IntUtil.min(st, stam + IntUtil.max(1, rStam));
        will = IntUtil.min(wi, will + IntUtil.max(1, rWill));
        soul = IntUtil.min(so, soul + IntUtil.max(1, rSoul));
        conf = IntUtil.min(co, conf + IntUtil.max(1, rConf));

	updatePosition();
    }

    ////////
    // Magic

    public Magic findSpell(String name) {
	return (Magic) Searcher.linearSearch(spells, new NameMatcher(name));
    }

    public Magic findSpell(Magic mag) {
	return (Magic) Searcher.linearSearch(spells, new ObjectMatcher(mag));
    }

    public void learnSpell(Magic mag) {
	spells.insert(mag);
    }

    public void forgetSpell(Magic mag) {
	spells.remove(mag);
    }

    public void getSpellList(Char rcpt) {
	Enchainer.headedList("Nenhuma", rcpt, spells.elements(), new NameExtractor(), Separators.NL);
    }

    // Magic
    ////////

    //////
    // Job

    public JobTitle findJob(String name) {
	return (JobTitle) Searcher.linearSearch(jobs, new NameMatcher(name));
    }

    public JobTitle findJob(Job jb) {
	return (JobTitle) Searcher.linearSearch(jobs, new JobMatcher(jb));
    }

    public void joinJob(Job jb, int level) {
	jobs.insert(new JobTitle(jb, level));
    }

    public void leaveJob(Job jb) {
	jobs.remove(findJob(jb));
    }

    void showJobList(Char rcpt, String sep) {
	Enchainer.headedList("Nenhuma", rcpt, jobs.elements(), new StringExtractor(), sep);
    }

    // Job
    //////

    //////////
    // Ability

    public Proficiency findAbility(String name) {
	return (Proficiency) Searcher.linearSearch(abilities, new NameMatcher(name));
    }

    public Proficiency findAbility(Ability abl) {
	return (Proficiency) Searcher.linearSearch(abilities, new AbilityMatcher(abl));
    }

    public void gainAbility(Ability abl, int level) {
	abilities.insert(new Proficiency(abl, level));
    }

    public void dropAbility(Ability abl) {
	abilities.remove(findAbility(abl));
    }

    public void showAbilityList(Char rcpt, String sep) {
	Enchainer.headedList("Nenhuma", rcpt, abilities.elements(), new StringExtractor(), sep);
    }

    boolean meetsDependenceRequirement(Ability abl) {
	for (Enumeration e = abl.getDependenceList();
	     e.hasMoreElements(); ) {
	    Ability dep = (Ability) e.nextElement();
	    if (findAbility(dep) == null)
		return false;
	}
	return true;
    }

    // Ability
    //////////

    ///////
    // Role

    void addRole(Object role) {
	roles.insert(role);
    }

    Object findRole(UnaryPredicate roleDetector) {
	return Searcher.linearSearch(roles, roleDetector);
    }

    Master findMaster() {
	return (Master) findRole(new MasterDetector());
    }

    // Role
    ///////

    /////////////
    // Sheetable:

    private String getSheetLine(int row) {
	String line = "";
	for (int c = 0; c < 4; ++c) {
	    int i = row * 4 + c;
	    line += StrUtil.leftWidth(attrLabels[i], 5) + ": " + StrUtil.formatNumber(getAttr(i), 3) + " (" + StrUtil.formatNumber(realAttr[i], 3) + ")  ";
	}
	return line;
    }

    private String listAttacks() {
	return StrUtil.formatNumber(getAttack(C_PHY), 3) + " " +
	    StrUtil.formatNumber(getAttack(C_MEN), 3) + " " +
	    StrUtil.formatNumber(getAttack(C_MYS), 3);
    }

    private String listDefenses() {
 	return StrUtil.formatNumber(getDefense(C_PHY), 3) + " " +
	    StrUtil.formatNumber(getDefense(C_MEN), 3) + " " +
	    StrUtil.formatNumber(getDefense(C_MYS), 3);
    }

    private String listDamages() {
	return StrUtil.formatNumber(getDamage(C_PHY), 3) + " " +
	    StrUtil.formatNumber(getDamage(C_MEN), 3) + " " +
	    StrUtil.formatNumber(getDamage(C_MYS), 3);
    }

    private String listResistances() {
	return StrUtil.formatNumber(getResistance(C_PHY), 3) + " " +
	    StrUtil.formatNumber(getResistance(C_MEN), 3) + " " +
	    StrUtil.formatNumber(getResistance(C_MYS), 3);
    }

    private String getConditions() {
	return getStam() + " " + getWill() + " " + getSoul() + " " + getConf();
    }

    public String getWhere() {
	Place plc = getPlace();
	return plc.getTitle() + " (" + plc.getPlaceType() + " [" + plc.getId() + "])";
    }

    private String getLeaderName() {
	return leader == null ? "Nenhum" : leader.getName();
    }

    private void sendFollowerNames(Recipient rcpt) {
	Enchainer.headedList("Nenhum", rcpt, followers.elements(), new NameExtractor(), ", ");
    }

    public void sendGroupNames(Recipient rcpt) {
	Enchainer.wrapNone("Ninguém", rcpt, Enchainer.headedListIf(rcpt, followers.elements(), new NameExtractor(), ", ", new GroupMemberDetector()));
    }

    static String strBehavior(int good, int bad) {
	final int M = 1000;
	float gd    = good;
	float bd    = bad;
	float total = good + bad;
	if (total == 0)
	    total = 1;
	gd = M * good / total;
	bd = M * bad / total;
	return Math.round(gd) + "/" + Math.round(bd) + " (" + good + "/" + bad + ")";
    }

    private String strBehavior() {
	return strBehavior(goodBehavior, badBehavior);
    }

    private void sendCharSheet(Char rcpt) {
	rcpt.send("Profissões: ");
	showJobList(rcpt, ", ");
	rcpt.send("Experiência: " + getExpStr() + "  Comportamento: " + strBehavior() + Separators.NL + "Sexo: " + sexLabels[sex] + Separators.NL + "Lutas: " + fights + "  Assassinatos: " + kills + "  Fugas: " + flees + "  Mortes: " + deaths + Separators.NL +
	    "Ataque: " + listAttacks() +
	    "  Defesa:      " + listDefenses() +
	    Separators.NL +
	    "Dano:   " + listDamages() +
	    "  Resistência: " + listResistances());
	for (int r = 0; r < 5; ++r)
	    rcpt.send(getSheetLine(r));
	rcpt.send("Condições: " + getConditions() +
	    "  Posição: " + getPositionLabel() + Separators.NL + "Finanças: " +
	    money + " (" + money.getCredits() + ")" + Separators.NL +
	    "Líder: " + getLeaderName() + Separators.NL +
	    "Seguidores: ");

	sendFollowerNames(rcpt);

	if (findMaster() != null)
	    rcpt.send("(Mestre)");
    }

    public void sendSheet(Char rcpt) {
	rcpt.send("Info: " + StrUtil.fancyListFlags(infoFlags, infoLabels, "Nenhuma") + Separators.NL + "Invisibilidade: posto [" + getInvisRank() + "] nível [" + getInvisLevel() + "] ");

	sendCharSheet(rcpt);
    }

    //
    /////////////

    public void sendScore(Char rcpt) {
	rcpt.send("Personagem: " + getName() + "  Nível: " + getLevel());
	sendCharSheet(rcpt);
    }

    ///////////
    // Viewable

    public boolean canBeSeenBy(Char looker) {
	if (looker.isOmni())
	    return true;

	int rank          = looker.getRank();
	int victInvisRank = getInvisRank();

	if (rank > victInvisRank)
	    return true;

	if (rank < victInvisRank)
	    return false;

	return looker.getLevel() >= getInvisLevel();

    }

    //
    ///////////

}

