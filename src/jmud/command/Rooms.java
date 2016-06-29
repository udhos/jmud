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
import jmud.Keywords;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jmud.jgp.IdTitleFormatter;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Rooms extends Command {

    Rooms(String name, int mRank, int mPos, int opt, String syn) {
	super(name, mRank, mPos, opt, syn);
    }

    private void send(Char ch) {
	ch.send(StrUtil.upcaseFirst(Keywords.ROOMS) + ":");
	ch.send("ID    TÍTULO" + Separators.NL +
		"----- ------------------------------------------------------------------------");
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	if (!toker.hasMoreTokens()) {
	    sendSyntax(aChar);
	    return;
	}

	String name = toker.nextQuotedToken();
	String list = null;
	int id;

	try {
	    id = Integer.parseInt(name);
	}
	catch(NumberFormatException e) {
	    send(aChar);
	    theWorld.listRoomsByName(aChar, name);
	    return;
	}

	if (!toker.hasMoreTokens()) {
	    Room r = theWorld.findRoomById(id);
	    if (r == null) {
		aChar.send("Sala não encontrada.");
		return;
	    }

	    send(aChar);
	    aChar.send((String) (new IdTitleFormatter(5, ' ')).execute(r));

	    return;
	}

	try {
	    send(aChar);
	    int first = id;
	    theWorld.listRoomsById(aChar, first, Integer.parseInt(toker.nextToken()));
	}
	catch(NumberFormatException e) {
	    aChar.send("O segundo argumento deve ser um número de sala.");
	}
    }
}
