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

import java.util.Enumeration;

import jmud.Char;
import jmud.Room;
import jmud.jgp.NameMatcher;
import jgp.algorithm.Searcher;
import jgp.container.BFSTree;

class SpeedWalk extends Command {

    SpeedWalk(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

        Room start = null;
        if (aChar.getPlace() instanceof Room)
            start = (Room) aChar.getPlace();
        else {
            aChar.send("Esse comando funciona apenas em salas.");
            return;
        }

        if (!toker.hasMoreTokens()) {
            aChar.send("Sintaxe: atalho <nome da sala>");
            return;
        }

	BFSTree tree = Searcher.breadthFirstSearch(start, new NameMatcher(toker.nextQuotedToken()), 1000);

	if (tree == null) {
	    aChar.send("Região não encontrada dentro dos limites.");
	    return;
	}

	int depth = tree.getDepth();
	if (depth == 1) {
	    aChar.send("Você já está aqui.");
	    return;
	}

	StringBuffer result = new StringBuffer(--depth);
	result.setLength(depth);
	for (Room curr, last = (Room) tree.getLast(); depth > 0; last = curr) {
	    curr = (Room) last.getMark();
	    result.setCharAt(--depth, curr.findDoorName(last).charAt(0));
	}

	// Limpa a bagunça
	for (Enumeration e = tree.elements(); e.hasMoreElements(); )
	    ((Room)e.nextElement()).unmark();

	aChar.send("Atalho: " + result);
    }
}
