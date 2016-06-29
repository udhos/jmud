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

class Repeat extends Command {

    Repeat(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	if (!toker.hasMoreTokens()) {
	    aChar.send("Repetir quantas vezes?");
	    return;
	}
	String vezes = toker.nextToken();

	if (!toker.hasMoreTokens()) {
	    aChar.send("Repetir o que?");
	    return;
	}
	String line = toker.getTail();

	Client cli = aChar.getClient();

	try {
	    int vz = Integer.parseInt(vezes);
	    int available = cli.getFreeCommandSlots();
	    if (vz > available) {
		vz = available;
		aChar.send("Reduzindo para o máximo de repetições: " + vz);
	    }
	    for (int i = 0; i < vz; ++i)
		cli.parse(line);
	}
	catch (NumberFormatException e) {
	    aChar.send("Você deve fornecer o número de repetições.");
	}
    }
}


