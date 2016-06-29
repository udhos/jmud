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

import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.jgp.ItemCloneDetector;
import jgp.predicate.BinaryPredicate;

public class ShopCreature extends Creature {

    private Shop theShop = null;

    ShopCreature(CreatureProto cr) {
	super(cr);
    }

    void bindShop(Shop sh) {
	theShop = sh;
    }

    public Shop getShop() {
	return theShop;
    }

    static String oneLine(int ind, int count, Item it, Shop sh) {
	String c = sh.producesItem(it.getId()) ?
	    StrUtil.rightPad("Ilimitada", 10) :
	    StrUtil.formatNumber(count, 10);

	return Separators.NL + StrUtil.formatNumber(ind, 6) + " " + c + " " + StrUtil.rightPad(it.getName(), 54) + " " + StrUtil.formatNumber(it.getPrice(sh.getSellTax()), 5);
    }

    static Item findNextItem(Enumeration enum, Char looker) {
	while (enum.hasMoreElements()) {
	    Item it = (Item) enum.nextElement();
	    if (it.canBeSeenBy(looker))
		return it;
	}
	return null;
    }

    public String getShopList(Char looker) {
	String list = "";

	Enumeration itEnum = theItems.elements();
	Item it = (Item) findNextItem(itEnum, looker);
	if (it == null)
	    return list;

	Shop sh = getShop();
	BinaryPredicate isClone = new ItemCloneDetector();

	int ind = 1;
	Item next = null;
	do {
	    next = findNextItem(itEnum, looker);

	    int count = 1;
	    while (next != null && isClone.execute(it, next)) {
		++count;
		it = next;
		next = findNextItem(itEnum, looker);
	    }
	    list += oneLine(ind, count, it, sh);

	    ++ind;
	    it = next;
	} while (it != null);

	return list;
    }

    public void sendSheet(Char rcpt) {
	super.sendSheet(rcpt);
	rcpt.send(Separators.NL + "(Vendedor: loja " + getShop().getId() + ")");
    }
}
