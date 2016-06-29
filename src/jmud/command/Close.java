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
import jmud.Door;
import jmud.Room;
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

class Close extends Command {

    Close(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    static final String DOOR = "porta";

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	if (!toker.hasMoreTokens()) {
	    aChar.send("O que você quer fechar?");
	    return;
	}

	String trg = toker.nextToken();

	if (DOOR.startsWith(trg)) {
	    Place plc = aChar.getPlace();
	    if (!plc.isRoom()) {
		aChar.send("Não há porta neste lugar.");
		return;
	    }

	    if (!toker.hasMoreTokens()) {
		aChar.send("Qual porta você quer fechar?");
		return;
	    }

	    String subTrg = toker.nextToken();

	    Room rm = (Room) plc;
	    Door dr = rm.findDoorByName(subTrg);
	    if (dr == null) {
		aChar.send("Essa porta não existe.");
		return;
	    }

	    if (!dr.isCloseable()) {
		aChar.send("Essa porta não pode ser fechada.");
		return;
	    }

	    switch(dr.getStatus()) {
	    case Door.S_OPEN:
		dr.setBothStatus(Door.S_CLOSED, rm);
		plc.action("$p fecha a porta " + dr.getDirectionName() + ".", false, aChar);
		break;
	    case Door.S_CLOSED:
		aChar.send("A porta já está fechada.");
		break;
	    case Door.S_LOCKED:
		aChar.send("A porta está trancada.");
		break;
	    default:
	    }

	    return;
	}

      aChar.send("Não implementado: não é porta.");
    }
}


