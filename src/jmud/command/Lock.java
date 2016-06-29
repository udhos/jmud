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
import jmud.Key;
import jmud.Item;
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

class Lock extends Command {

    Lock(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    static final String DOOR = "porta";

    private static boolean canLock(Item it, Room rm, Door dr) {
	return it != null && it instanceof Key && ((Key) it).canUnlockDoor(rm, dr);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send("O que você quer trancar?");
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
		aChar.send("Qual porta você quer trancar?");
		return;
	    }

	    String subTrg = toker.nextToken();

	    Room rm = (Room) plc;
	    Door dr = rm.findDoorByName(subTrg);
	    if (dr == null) {
		aChar.send("Essa porta não existe.");
		return;
	    }

	    if (!dr.isLockable()) {
		aChar.send("Essa porta não pode ser trancada.");
		return;
	    }

	    switch(dr.getStatus()) {
	    case Door.S_OPEN:
		aChar.send("A porta está aberta.");
		break;
	    case Door.S_CLOSED:
		if (!canLock(aChar.getEq(Item.W_LHOLD), rm, dr) &&
		    !canLock(aChar.getEq(Item.W_RHOLD), rm, dr)) {
		    aChar.send("Você não está segurando a chave adequada.");
		    return;
		}
		dr.setBothStatus(Door.S_LOCKED, rm);
		plc.action("$p tranca a porta " + dr.getDirectionName() + ".", true, aChar);
		break;
	    case Door.S_LOCKED:
		aChar.send("A porta já está trancada.");
		break;
	    default:
	    }
	    return;
	}

	aChar.send("Não implementado: não é porta.");
    }
}
