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
import java.io.BufferedWriter;
import java.io.IOException;

import jmud.util.bit.Bit;
import jmud.util.StrUtil;
import jmud.util.Separators;
import jmud.util.log.Log;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;

public class Item implements UniqueId, Named, FancyNamed, Briefed, Viewable {

  static protected World theWorld = null;

  static void setWorld(World wld) {
    theWorld = wld;
  }

    static final int T_NONE      = 0;
    static final int T_WEAPON    = 1;
    static final int T_ARMOR     = 2;
    static final int T_CONTAINER = 3;
    static final int T_BOARD     = 4;
    static final int T_KEY       = 5;
    static final int T_CURRENCY  = 6;

    static final int F_NONE      = Bit.BIT0; // a
    static final int F_WEAPON    = Bit.BIT1; // b
    static final int F_ARMOR     = Bit.BIT2; // c
    static final int F_CONTAINER = Bit.BIT3; // d
    static final int F_BOARD     = Bit.BIT4; // e
    static final int F_KEY       = Bit.BIT5; // f
    static final int F_CURRENCY  = Bit.BIT6; // g

    static final String typeLabels[] = {
	"genérico",
	"arma",
	"armadura",
	"recipiente",
	"quadro",
	"chave",
	"moeda"
    };

    static final int P_CANTAKE   = 0; // a
    static final int P_WEARALL   = 1; // b
    static final int P_INVISIBLE = 2; // c
    static final int P_GLOWING   = 3; // d
    static final int P_HUMMING   = 4; // e
    static final int P_PHYSICAL  = 5; // f
    static final int P_MENTAL    = 6; // g
    static final int P_MYSTIC    = 7; // h
    static final int P_VOLATILE  = 8; // i  --  não pode ser salvo
    static final int P_STATIC    = 9; // j  --  não pode ser modificado

    static final int PM_CANTAKE   = 1 << P_CANTAKE;
    static final int PM_WEARALL   = 1 << P_WEARALL;
    static final int PM_INVISIBLE = 1 << P_INVISIBLE;
    static final int PM_GLOWING   = 1 << P_GLOWING;
    static final int PM_HUMMING   = 1 << P_HUMMING;
    static final int PM_PHYSICAL  = 1 << P_PHYSICAL;
    static final int PM_MENTAL    = 1 << P_MENTAL;
    static final int PM_MYSTIC    = 1 << P_MYSTIC;
    static final int PM_VOLATILE  = 1 << P_VOLATILE;
    static final int PM_STATIC    = 1 << P_STATIC;


    static final String propLabels[] = {
	"pegável",
	"multi-veste",
	"invisível",
	"brilho",
	"zumbido",
	"físico",
	"mental",
	"místico",
	"volátil",
	"estático"
    };

    public static final int W_LHOLD     = 0; // a
    public static final int W_RHOLD     = 1; // b
    public static final int W_HEAD      = 2; // c
    public static final int W_NECK      = 3; // d
    public static final int W_BODY      = 4; // e
    public static final int W_WAIST     = 5; // f
    public static final int W_AROUND    = 6; // g
    public static final int W_LSHOULDER = 7; // h
    public static final int W_RSHOULDER = 8; // i
    public static final int W_LARM      = 9; // j
    public static final int W_RARM      = 10; // k
    public static final int W_LWRIST    = 11; // l
    public static final int W_RWRIST    = 12; // m
    public static final int W_LHAND     = 13; // n
    public static final int W_RHAND     = 14; // o
    public static final int W_LFINGER   = 15; // p
    public static final int W_RFINGER   = 16; // q
    public static final int W_LLEG      = 17; // r
    public static final int W_RLEG      = 18; // s
    public static final int W_LFOOT     = 19; // t
    public static final int W_RFOOT     = 20; // u

    public static final int WEAR_POSITIONS = 21;

  static final String wearPositions[] = {
    "segurando",         // 0
    "segurando",
    "cabeça",
    "pescoço",
    "tronco",
    "cintura",           // 5
    "ao redor",
    "ombro esquerdo",
    "ombro direito",
    "braço esquerdo",
    "braço direito",     // 10
    "pulso esquerdo",
    "pulso direito",
    "mão esquerda",
    "mão direita",
    "dedo esquerdo",     // 15
    "dedo direito",
    "perna esquerda",
    "perna direita",
    "pé esquerdo",
    "pé direito"         // 20
  };

  static final String wearKeywords[] = {
    "empunhadura",      // 0
    "empunhadura",
    "cabeca",
    "pescoco",
    "tronco",
    "cintura",          // 5
    "aoredor",
    "ombroesquerdo",
    "ombrodireito",
    "bracoesquerdo",
    "bracodireito",     // 10
    "pulsoesquerdo",
    "pulsodireito",
    "maoesquerda",
    "maodireita",
    "dedoesquerdo",     // 15
    "dedodireito",
    "pernaesquerda",
    "pernadireita",
    "peesquerdo",
    "pedireito"         // 20
  };

    public static final String wearAction[] = {
	"segura",           // 0
	"segura",
	"coloca",
	"prende",
	"veste",
	"prende",           // 5
	"veste",
	"prende",
	"prende",
	"veste",
	"veste",            // 10
	"prende",
	"prende",
	"veste",
	"veste",
	"põe",              // 15
	"põe",
	"veste",
	"veste",
	"calça",
	"calça"             // 20
    };


  protected ItemProto  prototype      = null;
  private   int        theId;
  private   String     theName        = null;
  private   String     theAliases     = null;
  private   String     theBrief       = null;
  private   String     theDescription = null;

  private   int        theLevel;
  protected int        theLoad;
  private   int        theCost;

  private   int        properties;
  private   int        wearVector;

  private   Owner      theOwner       = null;
  private   Char       equipper       = null;

  Item(ItemProto pro) {
    prototype      = pro;
    theId          = pro.theId;
    theName        = pro.theName;
    theAliases     = pro.theAliases;
    theBrief       = pro.theBrief;
    theDescription = pro.theDescription;
    theLevel       = pro.theLevel;
    theLoad        = pro.theLoad;
    theCost        = pro.theCost;
    properties     = pro.properties;
    wearVector     = pro.wearVector;
    ++prototype.numberOfItems;
  }

  protected void finalize() {
    --prototype.numberOfItems;
  }

    public ItemProto getPrototype() {
	return prototype;
    }

  int getType() {
    return T_NONE;
  }

    String getItemType() {
	return typeLabels[getType()];
    }

    String addFlagStr(String str, int flag, int flagInd) {
	if (Bit.isSet(properties, flag))
	    return str + " (" + propLabels[flagInd] + ")";

	return str;
    }

    String checkFlags(String str) {
	String s = str;
	s = addFlagStr(s, PM_INVISIBLE, P_INVISIBLE);
	s = addFlagStr(s, PM_GLOWING,   P_GLOWING);
	s = addFlagStr(s, PM_HUMMING,   P_HUMMING);
	return s;
    }

    public String getName() {
	return theName;
    }

  public String getFancyName() {
      return checkFlags(getName());
  }

  public String getBrief(boolean showId) {
      if (showId)
	  return "[" + getId() + "] " + checkFlags(theBrief);
      return checkFlags(theBrief);
  }

  String getDescription() {
    return theDescription;
  }

  String getFullDescription(Char ch) {
    return theDescription;
  }

  public int getId() {
    return theId;
  }

  boolean hasId(int itemId) {
    return itemId == theId;
  }

  public boolean hasName(String name) {
    return StrUtil.findAliasPrefix(name, theAliases);
  }

    public int getLoad() {
	return theLoad;
    }

    int getValue() {
	return theCost;
    }

    public int getPrice(float tax) {
	int price = Math.round(tax * (float) getValue());

	if (price < Money.MIN_PRICE)
	    price = Money.MIN_PRICE;

	return price;
    }

    // Esta á a maneira certa de entregar um item que está no "ar"
    // (sem dono) para um Owner
    public void giveTo(Owner aOwner) {
	theOwner = aOwner;
	theOwner.insertItem(this);
    }

    // Esta é a maneira certa de equipar um personagem com este item
    public void equip(Char eq, int wearPos) {
	drop();
	equipper = eq;
	equipper.wearOn(this, wearPos);
    }

    public void equip(Char eqpr) {
	drop();
	equipper = eqpr;
	equipper.wear(this);
    }

    // Esta é a maneira correta de transferir um equipamento para o inventário
    public void unequip() {
	Owner ow = equipper;
	drop();
	giveTo(ow);
    }

    // Esta é a maneira certa de soltar um item/eq e deixá-lo no "ar"
    // (sem dono)
    public void drop() {
	if (equipper != null) {
	    try {
		equipper.takeOffEq(this);
		equipper = null;
	    }
	    catch(NoSuchElementException e) {
		Log.err("Equipamento '" + getName() + "' [" + getId() + "] não encontrado ao ser removido de: " + equipper.getName() + "[" + equipper.getId() + "]");
	    }
	}
	if (theOwner != null) {
	    try {
		theOwner.removeItem(this);
		theOwner = null;
	    }
	    catch(NoSuchElementException e) {
		Log.err("Item '" + getName() + "' [" + getId() + "] não encontrado ao ser tomado de: " + theOwner.getOwnerType() + " [" + theOwner.getId() + "]");
	    }
	}
    }

    // Esta é a maneira certa de transferir itens entre Owners
    public void transferTo(Owner target) {
	drop();
	giveTo(target);
    }

    public boolean wearAll() {
	return Bit.isSet(properties, 1 << P_WEARALL);
    }

    int getWearVector() {
	return wearVector;
    }

    public Owner getContainer() {
	return null;
    }

  /////////////
  // Sheetable:

    static String getOwnerChain(Owner ow) {
	String owners = "";
	if (ow != null) {
	    owners += ow.getOwnerType() + " [" + ow.getId() + "]";
	    while ((ow = ow.getOwner()) != null)
		owners += ", " + ow.getOwnerType() + " [" + ow.getId() +"]";
	}
	return owners;
    }

    public String getWhere() {
	if (theOwner != null)
	    return getOwnerChain(theOwner);

	if (equipper != null)
	    return "eq: " + getOwnerChain(equipper);

	return "?";
    }

    private String getPropList() {
	return StrUtil.listFlags(properties, propLabels);
    }

    private String getPositionList() {
	return StrUtil.listFlags(wearVector, wearPositions);
    }

  public String getSheet() {
      String props     = getPropList();
      String positions = getPositionList();

    return "ITEM - ID: [" + theId + "]  Nome: [" + theName + "]  Tipo: [" + getItemType() + "]" + Separators.NL +
      "Sinônimos: " + theAliases + Separators.NL +
      "Nível: [" + theLevel + "]  Peso: [" + theLoad + "]  Valor: [" + theCost + "]" + Separators.NL +
	"Propriedades: " + (props == "" ? "Nenhuma" : props) + Separators.NL +
      "Posições: " + ((positions == "") ? "Nenhuma" : positions) + Separators.NL +
      "Dono: " + getWhere() + Separators.NL +
      "Equipado por: " + ((equipper == null)? "Ninguém" : (equipper.getName() + " [" + equipper.getId() + "]"));
  }

  public Owner getOwner() {
    return theOwner;
  }

  //
  /////////////

  //////////////
  // Clone:

    /*
  public boolean isCloneOf(Clone c) {
    return c instanceof Item && getId() == ((Item) c).getId();
  }
    */

  //
  //////////////

    void save(BufferedWriter writer) throws IOException {
	writer.write(Separators.BOR); writer.write(String.valueOf(theId)); writer.newLine();
	writer.write(String.valueOf(getType())); writer.newLine();
	writer.write(theName); writer.newLine();
	writer.write(theAliases); writer.newLine();
	writer.write(theBrief); writer.newLine();
	StrUtil.writeDescription(writer, theDescription);
	writer.write(theLevel + " " + theLoad + " " + theCost + " " + StrUtil.makeFlags(properties) + " " + StrUtil.makeFlags(wearVector)); writer.newLine();
    }

    static Item load(int itemId, LineReader itemFile, ItemProto itPro)
	throws InvalidFileFormatException {
	Item it = ItemProto.load(itemId, 0, itemFile).create();
	if (itPro != null) {
	    it.prototype = itPro;
	    ++itPro.numberOfItems;
	}
	return it;
    }

    ////////////////////////////////////
    // Ha metodos identicos em ItemProto

    boolean isVolatile() {
	return Bit.isSet(properties, PM_VOLATILE);
    }

    boolean isStatic() {
	return Bit.isSet(properties, PM_STATIC);
    }

    // Em particular, este nao e' necessario no ItemProto
    boolean isInvisible() {
	return Bit.isSet(properties, PM_INVISIBLE);
    }

    //
    ////////////////////////////////////

    void affectEquipper(Char ch) {
    }

    void unnaffectEquipper(Char ch) {
    }

    ///////////
    // Viewable

    public boolean canBeSeenBy(Char looker) {
	return looker.isOmni() || !isInvisible();
    }

    //
    ///////////

}
