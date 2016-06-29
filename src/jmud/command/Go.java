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
import jmud.Place;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Go extends Command {

    Go(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    static final String SYNTAX = "Sintaxe: va <número da sala|nome do personagem>";

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	IndexedToken indTok = toker.nextIndexedToken();
	String trg = indTok.getTarget();
	Place  plc =  null;

	if (trg == "") {

	    int roomId = indTok.getIndex();

	    plc = theWorld.findRoomById(roomId);
	    if (plc == null) {
		aChar.send("Sala não existente.");
		return;
	    }

	}
	else {
	    Char vict = theWorld.findCharByName(indTok, aChar);
	    if (vict == null) {
		aChar.send("Esse personagem não se encontra neste mundo.");
		return;
	    }
	    plc = vict.getPlace();
	}

	aChar.getPlace().actionNotToChar("$p desaparece em uma nuvem de fumaça.", true, aChar);
	aChar.moveTo(plc);
	aChar.getPlace().actionNotToChar("$p aparece em uma explosão brilhante.", true, aChar);
	aChar.look();
    }
}

