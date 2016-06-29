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
import jmud.Owner;
import jmud.jgp.TransferTo;
import jmud.jgp.MultipleItemsHandler;
import jgp.container.Vector;
import jgp.algorithm.Transformer;

class Get extends Command {

    Get(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send("O que você quer pegar?");
	    return;
	}

	IndexedToken indTok  = toker.nextIndexedToken();
	Vector     itemLst = null;
	Item       bagItem = null;
	String     act     = "$p pegou $i.";

	if (!toker.hasMoreTokens()) {
	    // Procura na sala
	    itemLst = aChar.getPlace().findItemsByName(indTok, aChar);
	    if (itemLst.isEmpty()) {
		aChar.send("Não há isso por aqui.");
		return;
	    }
	}
	else {
	    // Procura pelo recipiente
	    IndexedToken bagIndTok = toker.nextIndexedToken();
	    bagItem = aChar.searchItem(bagIndTok, aChar);
	    if (bagItem == null) {
		String place = " aqui";
		if (bagIndTok.hasContext())
		    place = " em " + bagIndTok.getContext();
		aChar.send("Não há " + bagIndTok.getTarget() + place + ".");
		return;
	    }

	    // Verifica se é de fato um recipiente
	    Owner ow = bagItem.getContainer();
	    if (ow == null) {
		aChar.send("Aquilo não é um recipiente.");
		return;
	    }

	    // Procura dentro do recipiente
	    itemLst = ow.findItemsByName(indTok, aChar);
	    if (itemLst.isEmpty()) {
		aChar.send("Não há nada desse tipo em " + bagItem.getName() + ".");
		return;
	    }

	    act = "$p pegou $i em $I.";
	}

	// Efetiva a tranferência e manda as respectivas mensagens
	Transformer.applyToAll(itemLst,
			       new MultipleItemsHandler(act, true, aChar, bagItem,
						 new TransferTo(aChar),
						 "$p não suporta o peso de $i."));
    }
}
