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

class Buy extends Command {

    Buy(String name, int mRank, int mPos, int opt) {
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

	Item it = keeper.findItemByName(toker.nextIndexedToken(), keeper);
	if (it == null) {
	    room.action("$p diz 'Não tenho isso.' a $P.", false, keeper, aChar);
	    return;
	}

	Shop sh = keeper.getShop();

	int price = it.getPrice(sh.getSellTax());

	Money customerMoney = aChar.getMoney();

	int funds = customerMoney.getCredits();

	if (funds < price) {
	    room.action("$p diz 'Você não tem recursos suficientes!' a $P.", false, keeper, aChar);
	    return;
	}

	Money customerPayload = new Money(price, customerMoney);

	int goodFunds = customerPayload.getCredits();

	if (goodFunds < price) {
	    room.action("$p diz 'Você não tem moedas aceitáveis!' a $P.", false, keeper, aChar);
	    return;
	}

	/* transfere o item */
	if (sh.producesItem(it.getId())) {
	    Item newItem = it.getPrototype().create();
	    theWorld.insertItem(newItem, aChar);
	}
	else
	    it.transferTo(aChar);

	/* acerta pagamento */

	customerMoney.sub(customerPayload);
	keeper.getMoney().add(customerPayload);

	/* anuncia pagamento */
	room.action("$p paga " + customerPayload + "(" + price + ") a $P.", false,
		    aChar, keeper);

    }
}



