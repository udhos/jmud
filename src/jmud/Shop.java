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

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.io.IOException;

import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.log.Log;
import jgp.container.Vector;
import jmud.util.bit.Bit;
import jmud.util.StrUtil;
import jmud.util.InvalidFlagException;
import jmud.util.Separators;


public class Shop implements UniqueId {

    private int    theId;
    private String theTitle = null;
    private float  buyTax;
    private float  sellTax;
    private int    toBuy;
    private int    keeper;
    private Vector products = new Vector();
    private int    goodMoney;

    Shop(int zoneBase, int shopId, LineReader shopFile)
	throws InvalidFileFormatException {

	theId = shopId + zoneBase;

	StringTokenizer tok = null;
	try {
	    theTitle = shopFile.readLine();

	    Log.debug("Loja: " + theTitle);

	    tok     = new StringTokenizer(shopFile.readLine());
	    buyTax  = Float.valueOf(tok.nextToken()).floatValue();
	    sellTax = Float.valueOf(tok.nextToken()).floatValue();

	    toBuy = StrUtil.parseFlags(shopFile.readLine());
	    goodMoney = StrUtil.parseFlags(shopFile.readLine());

	    keeper = Integer.parseInt(shopFile.readLine()) + zoneBase;

	    while (!shopFile.eos()) {
		String line = shopFile.readLine();
		if (line.startsWith(Separators.EOR))
		    break;
		if (line.startsWith(Separators.EOF))
		    throw new InvalidFileFormatException();

		products.insert(new Integer(Integer.parseInt(line) + zoneBase));
	    }

	    products.trim();
	}
	catch(IOException e) {
	    throw new InvalidFileFormatException();
	}
	catch(NoSuchElementException e) {
	    throw new InvalidFileFormatException();
	}
	catch(NumberFormatException e) {
	    throw new InvalidFileFormatException();
	}
	catch(InvalidFlagException e) {
	    throw new InvalidFileFormatException();
	}
    }

    public int getId() {
	return theId;
    }

    public boolean hasId(int id) {
	return getId() == id;
    }

    int getKeeperId() {
	return keeper;
    }

    public float getBuyTax() {
	return buyTax;
    }

    public float getSellTax() {
	return sellTax;
    }

    public boolean producesItem(int itemId) {
	for (Enumeration enum = products.elements(); enum.hasMoreElements(); ) {
	    Integer it = (Integer) enum.nextElement();
	    if (it.intValue() == itemId)
		return true;
	}
	return false;
    }

    public boolean buyItem(Item it) {
	return Bit.isSet(toBuy, 1 << it.getType());
    }

    public boolean acceptCurrency(int currKind) {
	return Bit.isSet(goodMoney, 1 << currKind);
    }

    public int getAcceptedCurrencies() {
	return goodMoney;
    }

    /////////////
    // Sheetable:

    String getShortSheet() {
	return StrUtil.leftPad(String.valueOf(theId), 5) + " " + StrUtil.leftPad(String.valueOf(keeper), 8) + " " + StrUtil.leftPad(String.valueOf(buyTax), 6) + " " + StrUtil.leftPad(String.valueOf(sellTax), 5) + " " + StrUtil.rightPad(StrUtil.makeFlags(toBuy), 32);
    }

    String getProductList() {
	return StrUtil.headedConcat(products.elements(), ", ", "Nenhum");
    }

    public String getSheet() {
	return "LOJA - ID: [" + theId + "]" + Separators.NL +
	    "Título: [" + theTitle + "]"+ Separators.NL +
	    "Compra: " + buyTax + "  Venda: " + sellTax + Separators.NL +
	    "Itens comprados: " + StrUtil.fancyListFlags(toBuy, Item.typeLabels, "Nenhum") + Separators.NL +
	    "Moedas aceitas: " + StrUtil.fancyListFlags(goodMoney, Money.kindLabel, "Nenhuma") + Separators.NL +
	    "Vendedor: [" + keeper + "]" + Separators.NL +
	    "Produtos: " + getProductList();
    }
}
