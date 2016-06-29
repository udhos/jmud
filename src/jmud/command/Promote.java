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

class Promote extends Command {

    Promote(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    static final String SYNTAX = "Sintaxe: promova <nome> <posto>";

    void promotion(Char ch, Char vict, int rank) {
	Log.info(ch.getName() + " promovendo " + vict.getName() + " de posto " + vict.getRank() + " para " + rank);
	vict.promote(rank);
	ch.send(vict.getName() + " agora possui posto " + vict.getRank() + ".");
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}
	String name = toker.nextToken();

	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	int rank = 0;
	try {
	    rank = Integer.parseInt(toker.nextToken());
	}
	catch(NumberFormatException e) {
	    aChar.send(SYNTAX);
	    return;
	}

	Client cli = theManager.findClientByName(name);
	if (cli == null) {
	    aChar.send("Cliente não encontrado.");
	    return;
	}

	Char vict = cli.getChar();

	if (!aChar.isFirstModerator()) {
	    if (rank >= aChar.getRank()) {
		aChar.send("Impossível fornecer posto tão alto.");
		return;
	    }

	    if (aChar != vict && aChar.forbidAccess(vict))
		return;
	}

	promotion(aChar, vict, rank);
    }
}
