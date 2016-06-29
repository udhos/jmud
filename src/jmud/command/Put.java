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

class Put extends Command {

    Put(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send("O que voc� quer colocar?");
	    return;
	}

	IndexedToken itok = toker.nextIndexedToken();

	if (!toker.hasMoreTokens()) {
	    aChar.send("Onde voc� quer colocar?");
	    return;
	}

	Vector items = aChar.findItemsByName(itok, aChar); // Procura pelo(s) item(s), alvo
	if (items.isEmpty()) {
	    aChar.send("Voc� n�o possui isso.");
	    return;
	}

	IndexedToken trg = toker.nextIndexedToken();
	Item cont = aChar.searchItem(trg, aChar);          // Procura por um item, destino
	if (cont == null) {
	    String place = " aqui";
	    if (trg.hasContext())
		place = " em " + trg.getContext();
	    aChar.send("Voc� n�o v� " + trg.getTarget() + place +".");
	    return;
	}
	// Verifica se � um recipiente
	Owner ow = cont.getContainer();
	if (ow == null) {
	    aChar.send("Isso n�o � um recipiente.");
	    return;
	}
	// Verifica se s�o diferentes (exclue do vetor)
	items.removeEvery(cont);

	// Efetiva a tranfer�ncia e manda as respectivas mensagens
	Transformer.applyToAll(items,
			       new MultipleItemsHandler("$p colocou $i em $I.",
							true, aChar, cont, new TransferTo(ow),
							"$i n�o cabe em $I."));
    }
}

