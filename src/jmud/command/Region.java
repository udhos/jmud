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
import jmud.jgp.IdTitleFormatter;
import jgp.algorithm.Searcher;
import jgp.container.BFSTree;

class Region extends Command {

    Region(String name, int mRank, int mPos, int opt) {
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
            aChar.send("Sintaxe: regiao <distância> | <nome da sala>");
            return;
        }

	String target = toker.nextQuotedToken();
	BFSTree tree = null;

	try {
	    int dep = Integer.parseInt(target);
	    tree = Searcher.breadthFirstSearch(start, ++dep);
	}
	catch(NumberFormatException e) {
	    tree = Searcher.breadthFirstSearch(start, new NameMatcher(target), 10);
	}

	if (tree == null) {
	    aChar.send("Região não encontrada dentro dos limites.");
	    return;
	}

	IdTitleFormatter form = new IdTitleFormatter(5, ' ');
	int depth = tree.getDepth();
	for (int lvl = 0; lvl < depth; ++lvl) {
	    aChar.send("Distância: " + lvl);
	    for (Enumeration e = tree.sublevel(lvl); e.hasMoreElements(); ) {
		Room curr = (Room) e.nextElement();
		aChar.send((String) form.execute(curr));
		curr.unmark();
	    }
	}
    }
}
