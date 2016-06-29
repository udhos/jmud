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

class Tell extends Command {

    Tell(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send("Com quem voc� quer falar?");
	    return;
	}

	Char vict = theWorld.findCharByName(toker.nextIndexedToken(), aChar);
	if (vict == null) {
	    aChar.send("Essa pessoa n�o est� aqui no momento.");
	    return;
	}

	if (!toker.hasMoreTokens()) {
	    aChar.send("O que voc� quer dizer?");
	    return;
	}
	aChar.action("$p diz para $P '" + toker.getTail() + "'", false, aChar, vict);
	vict.action("$p diz para $P '" + toker.getTail() + "'", false, aChar, vict);
    }
}

