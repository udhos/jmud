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
import java.util.NoSuchElementException;

import jmud.util.InvalidFlagException;
import jmud.util.StrUtil;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.Separators;
import jmud.util.bit.Bit;
import jmud.util.log.Log;

public class ItemProto implements UniqueId {

  int        theId;
  String     theName        = null;
  String     theAliases     = null;
  String     theBrief       = null;
  String     theDescription = null;

  int        theLevel;
  int        theLoad;
  int        theCost;

  int        properties;
  int        wearVector;

  int        numberOfItems = 0;

  static     private int numberOfProtos = 0;

  ItemProto(int zoneBase, int itemId, LineReader itemFile)
    throws InvalidFileFormatException {

    theId = itemId + zoneBase;

    StringTokenizer tok = null;
    try {
      theName = itemFile.readLine();

      Log.debug("Item: " + theName);

      theAliases = StrUtil.loadAliases(itemFile);
      theBrief   = itemFile.readLine();

      theDescription = StrUtil.readDescription(itemFile);

      tok = new StringTokenizer(itemFile.readLine());
    }
    catch(IOException e) {
      throw new InvalidFileFormatException();
    }

    try {

    // level
    if (!tok.hasMoreTokens())
      throw new InvalidFileFormatException();
    theLevel = Integer.parseInt(tok.nextToken());

    // load
    if (!tok.hasMoreTokens())
      throw new InvalidFileFormatException();
    theLoad = Integer.parseInt(tok.nextToken());

    // cost
    if (!tok.hasMoreTokens())
      throw new InvalidFileFormatException();
    theCost = Integer.parseInt(tok.nextToken());

    }
    catch(NumberFormatException e) {
      throw new InvalidFileFormatException();
    }

    // properties
    if (!tok.hasMoreTokens())
      throw new InvalidFileFormatException();
    try {
      properties = StrUtil.parseFlags(tok.nextToken());
    }
    catch(InvalidFlagException e) {
      throw new InvalidFileFormatException();
    }

    // wearing
    if (!tok.hasMoreTokens())
      throw new InvalidFileFormatException();
    try {
      wearVector = StrUtil.parseFlags(tok.nextToken());
    }
    catch(InvalidFlagException e) {
      throw new InvalidFileFormatException();
    }

    ++numberOfProtos;
  }

  protected void finalize() {
    --numberOfProtos;
  }

  int getNumberOfItems() {
    return numberOfItems;
  }

  public Item create() {
    return new Item(this);
  }

  public int getId() {
    return theId;
  }

  boolean hasId(int id) {
    return getId() == id;
  }

  public String getName() {
    return theName;
  }

  boolean hasName(String name) {
    return StrUtil.findAliasPrefix(name, theAliases);
  }

    int getProtoType() {
	return Item.T_NONE;
    }

  String getItemProtoType() {
    return Item.typeLabels[getProtoType()];
  }

  /////////////
  // Sheetable:

  public String getSheet() {

    return "ITEM - ID: [" + theId + "]  Nome: [" + theName + "]  Tipo: [" + getItemProtoType() + "]" + Separators.NL +
      "Sinônimos: " + theAliases + Separators.NL +
      "Nível: [" + theLevel + "]  Peso: [" + theLoad + "]  Valor: [" + theCost + "]" + Separators.NL +
	"Propriedades: " + StrUtil.makeFlags(properties) + Separators.NL +
      "Posições: " + StrUtil.makeFlags(wearVector) + Separators.NL +
	"Quantidade: [" + numberOfItems + "]";
  }

    // usado por Item.load(...)
    static ItemProto load(int itemId, int zoneBase, LineReader itemFile)
	throws InvalidFileFormatException {

	int itemType;
	try {
	    itemType = Integer.parseInt(itemFile.readLine());
	}
	catch(IOException e) {
	    throw new InvalidFileFormatException();
	}
	catch(NumberFormatException e) {
	  throw new InvalidFileFormatException();
	}

	switch(itemType) {
	case Item.T_WEAPON:    return new WeaponProto(zoneBase, itemId, itemFile);
	case Item.T_ARMOR:     return new ArmorProto(zoneBase, itemId, itemFile);
	case Item.T_CONTAINER: return new ContainerProto(zoneBase, itemId, itemFile);
	case Item.T_BOARD:     return new BoardItemProto(zoneBase, itemId, itemFile);
	case Item.T_KEY:       return new KeyProto(zoneBase, itemId, itemFile);
	case Item.T_CURRENCY:  return new CurrencyProto(zoneBase, itemId, itemFile);
	default: throw new InvalidFileFormatException();
	}
    }

    //////////////////////////////////////
    // Estes metodos sao identicos em Item

    boolean isVolatile() {
	return Bit.isSet(properties, Item.PM_VOLATILE);
    }

    boolean isStatic() {
	return Bit.isSet(properties, Item.PM_STATIC);
    }

    //
    //////////////////////////////////////

}
