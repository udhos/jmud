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

package jmud.command;

import jmud.Char;
import jmud.Item;
import jmud.Money;
import jmud.Place;
import jmud.Shop;
import jmud.ShopCreature;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Price extends Command {

    Price(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	Place room = aChar.getPlace();

	ShopCreature keeper = room.findShopKeeper();
	if (keeper == null) {
	    aChar.send("Não há um vendedor aqui.");
	    return;
	}

	if (!toker.hasMoreTokens()) {
	    room.action("$p pergunta 'Qual item?' a $P.", false, keeper, aChar);
	    return;
	}

	Item it = aChar.findItemByName(toker.nextIndexedToken(), aChar);
	if (it == null) {
	    aChar.send("Você não possui isso.");
	    return;
	}

	Shop sh = keeper.getShop();

	if (!sh.buyItem(it)) {
	    room.action("$p diz 'Não compro esse tipo de coisa!' a $P.", false, keeper, aChar);
	    return;
	}

	int price = it.getPrice(sh.getBuyTax());

	Money priceMoney = new Money(price, sh.getAcceptedCurrencies());

	room.action("$p diz 'Eu daria " + priceMoney + "(" + price + ") por isso' a $P.", false,
		    keeper, aChar);
    }
}

