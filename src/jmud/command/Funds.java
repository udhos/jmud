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
import jmud.Money;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Funds extends Command {

    Funds(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	if (!toker.hasMoreTokens()) {
	    sendSyntax(aChar);
	    return;
	}

	Char vict = aChar.getPlace().findCharByName(toker.nextIndexedToken(), aChar);
	if (vict == null) {
	    aChar.send("Ningu�m aqui com esse nome.");
	    return;
	}

	try {
	    Money mon = new Money(toker.getTail());
	    vict.setMoney(mon);
	    aChar.send("Finan�as de '" + vict.getName() + "' modificada para '" + vict.getMoney() + "'.");
	}
	catch (NumberFormatException e) {
	    aChar.send("Formato inv�lido para finan�as.");
	}
    }
}



