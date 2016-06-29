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
import jmud.jgp.TransferTo;
import jmud.jgp.MultipleItemsHandler;
import jgp.container.Vector;
import jgp.algorithm.Transformer;

class Give extends Command {

    Give(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send("O que voc� quer entregar?");
	    return;
	}

	Vector items = aChar.findItemsByName(toker.nextIndexedToken(), aChar);
	if (items.isEmpty()) {
	    aChar.send("Voc� n�o possui isso.");
	    return;
	}

	if (!toker.hasMoreTokens()) {
	    aChar.send("A quem voc� quer entregar isso?");
	    return;
	}

	Char vict = aChar.getPlace().findCharByName(toker.nextIndexedToken(), aChar);
	if (vict == null) {
	    aChar.send("Essa pessoa n�o est� aqui no momento.");
	    return;
	}

	// Efetiva a tranfer�ncia e manda as respectivas mensagens
	Transformer.applyToAll(items, new MultipleItemsHandler("$p entregou $i para $P.",
							true, aChar, vict, new TransferTo(vict),
							"$P n�o aguenta o peso de $i."));
    }
}



