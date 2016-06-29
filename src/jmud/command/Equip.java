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

import java.util.NoSuchElementException;

import jmud.Char;
import jmud.Item;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Equip extends Command {

    Equip(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    String list = aChar.getEqList(aChar);
	    aChar.send(list.length() == 0 ? "Você não usa nada." : ("Você está usando:" + list));
	    return;
	}

	Item it = aChar.findItemByName(toker.nextIndexedToken(), aChar);
	if (it == null) {
	    aChar.send("Você não possui isso.");
	    return;
	}

	if (!toker.hasMoreTokens()) {
	    if (it.wearAll()) {
		if (aChar.canWear(it)) {
		    it.equip(aChar);
		    aChar.getPlace().action("$p equipa $i.", true, aChar, it);
		}
		else
		    aChar.send("Esse equipamento precisa de mais posições livres.");
	    }
	    else {
		try {
		    int wearPos = aChar.findWearPosition(it); // throws NoSuchElementException
		    it.equip(aChar, wearPos);
		    aChar.getPlace().action("$p " + Item.wearAction[wearPos] + " $i.", true, aChar, it);
		}
		catch(NoSuchElementException e) {
		    aChar.send("Você não tem onde equipar isso.");
		}
	    }
	    return;
	}

	String wearName = toker.nextToken();
	aChar.send("Não implementado.");

	// vestir o equipamento na posição dada
  }
}



