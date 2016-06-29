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
import jmud.Room;
import jmud.util.StrUtil;

class Transfer extends Command {

    Transfer(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }
    static final String SYNTAX = "Sintaxe: transfira <nome do personagem> <número da sala>";

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	Char vict = theWorld.findCharByName(toker.nextIndexedToken(), aChar);
	if (vict == null) {
	    aChar.send("Esse personagem não se encontra neste mundo.");
	    return;
	}

	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	int roomId;
	try {
	  roomId = Integer.parseInt(toker.nextToken());
	}
	catch (NumberFormatException e) {
	    aChar.send("Você deve especificar o número da sala destino.");
	    return;
	}

	Room target = theWorld.findRoomById(roomId);
	if (target == null) {
	    aChar.send("Sala destino não encontrada.");
	    return;
	}

	aChar.action("$p transfere $P para " + target.getTitle() + ".", true, aChar, vict);
	vict.send(StrUtil.adjustSex("Você foi transferid*.", aChar.isFemale()));
	vict.moveTo(target);
	vict.getPlace().action("$P surge abruptamente!", true, aChar, vict);
	vict.look();
    }
}

