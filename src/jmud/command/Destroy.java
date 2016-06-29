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

class Destroy extends Command {

    Destroy(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    static final String SYNTAX = "Sintaxe: destrua <item>|<personagem>";

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	IndexedToken itok = toker.nextIndexedToken();
	Place room = aChar.getPlace();
	Item  it   = aChar.searchItem(itok, aChar);
	if (it != null) {
	    room.action("$p desintegrou $i.", true, aChar, it);
	    theWorld.extractItem(it);
	    return;
	}

	Char vict = room.findCharByName(itok, aChar);
	if (vict == null) {
	    aChar.send("Você não vê isso por aqui.");
	    return;
	}

	if (vict.isPlayer()) {
	    aChar.send("Não pode ser feito com jogadores.");
	    return;
	}

	theWorld.extractChar(vict);
	room.action("$p desintegrou $P.", true, aChar, vict);
    }
}



