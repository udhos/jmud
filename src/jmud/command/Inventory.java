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


import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Inventory extends Command {

    Inventory(String name, int mRank, int mPos, int opt, String syn) {
	super(name, mRank, mPos, opt, syn);
    }

    static final String CARRY = "Inventário:";
    static final String NOTHING = "Nada";

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	Char vict = null;

	if (toker.hasMoreTokens() && (aChar.isAdmin())) {

	    vict = aChar.getPlace().findCharByName(toker.nextIndexedToken(), aChar);
	    if (vict == null) {
		aChar.send("Ninguém com esse nome por aqui.");
		return;
	    }

	    if (aChar.forbidAccess(vict))
		return;
	}
	else
	    vict = aChar;

	String inv = vict.getItemList(aChar);
	aChar.send(CARRY + ((inv.length() == 0) ? (Separators.NL + NOTHING) : (inv)));
    }
}

