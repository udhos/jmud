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
import jmud.Item;
import jmud.Room;
import jmud.Keywords;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Sheet extends Command {

    Sheet(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    static final String SYNTAX = "Sintaxe: ficha {item|personagem <número>} | <nome> | {sala [<número>]}";

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	IndexedToken itok = toker.nextIndexedToken();

	if (!toker.hasMoreTokens()) {
	    if (Keywords.ROOM.equals(itok.getTarget())) {
		Room rm = (Room) aChar.getPlace();
		aChar.send(rm.getSheet(aChar));
		return;
	    }
	    Item it = aChar.searchItem(itok, aChar);
	    if (it != null) {
		aChar.send(it.getSheet());
		return;
	    }
	    Char ch = aChar.getPlace().findCharByName(itok, aChar);
	    if (ch != null)
		ch.sendSheet(aChar);
	    else
		aChar.send("Não há isso aqui.");
	    return;
	}

	int id = 0;
	String opt = itok.getTarget();
	try {
	    id = Integer.parseInt(toker.nextToken());
	}
	catch(NumberFormatException e) {
	    aChar.send("O argumento deve ser um número.");
	    return;
	}

	if (Keywords.ROOM.startsWith(opt)) {
	    Room ro = theWorld.findRoomById(id);
	    if (ro == null) {
		aChar.send("Sala não encontrada.");
		return;
	    }
	    aChar.send(ro.getSheet(aChar));
	    return;
	}

	if (Keywords.ITEM.startsWith(opt)) {
	    Item it = theWorld.findItemById(id, aChar);
	    if (it != null) {
		aChar.send(it.getSheet());
		return;
	    }
	}
	if (Keywords.CHAR.startsWith(opt)) {
	    Char ch = theWorld.findCharById(id, aChar);
	    if (ch != null)
		ch.sendSheet(aChar);
	    else
		aChar.send("No momento não existe tal coisa no mundo.");
	    return;
	}
	else {
	    aChar.send(SYNTAX);
	    return;
	}
    }
}



