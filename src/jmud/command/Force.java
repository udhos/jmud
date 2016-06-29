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
import jmud.Request;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Force extends Command {

    Force(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }
    static final String SYNTAX = "Sintaxe: force <nome do personagem> <linha de comando>";

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	Char vict = aChar.getPlace().findCharByName(toker.nextIndexedToken(), aChar);
	if (vict == null) {
	    aChar.send("Esse personagem não se encontra neste mundo.");
	    return;
	}

	// evita loophole
	if (vict.getRank() >= aChar.getRank()) {
	    aChar.send("Você não possui poder suficiente.");
	    return;
	}

	String command = toker.getTail();
	Log.info(aChar.getName() + " força " + vict.getName() + " a '" + command + "'");
	vict.action("$p força $P a '" + command + "'.", true, aChar, vict);
	aChar.action("$p força $P a '" + command + "'.", true, aChar, vict);
	theManager.enqueueRequest(new Request(vict, command));
    }
}



