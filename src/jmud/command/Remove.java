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
import jmud.Client;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Remove extends Command {

    Remove(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	if (!toker.hasMoreTokens()) {
	    aChar.send("O que você quer remover?");
	    return;
	}
	IndexedToken itok = toker.nextIndexedToken();

	if (!toker.hasMoreTokens()) {
	    try {
		Item it = aChar.findEqByName(itok.getTarget(), itok.getIndex(), aChar);
		it.unequip();
		aChar.getPlace().action("$p remove $i.", true, aChar, it);
	    }
	    catch(NoSuchElementException e) {
		aChar.send("Você não está usando isso.");
	    }
	    return;
	}

	String wearName = toker.nextToken();

	// remover o equipamento da posição dada ?
    }
}

