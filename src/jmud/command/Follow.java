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

class Follow extends Command {

    Follow(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    static void stopFollow(Char ch) {
	Char oldLeader = ch.getLeader();
	ch.send("Você pára de seguir " + oldLeader.getName() + ".");
	ch.stopFollow();
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	Char oldLeader = aChar.getLeader();

	if (!toker.hasMoreTokens()) {
	    if (oldLeader == null)
		aChar.send("Quem você quer seguir?");
	    else
		stopFollow(aChar);
	    return;
	}

	Char vict = aChar.getPlace().findCharByName(toker.nextIndexedToken(), aChar);

	if (vict == null) {
	    aChar.send("Não há ninguém aqui com esse nome.");
	    return;
	}

	if (vict == aChar) {
	    if (oldLeader == null)
		aChar.send("Você não está seguindo ninguém.");
	    else
		stopFollow(aChar);
	    return;
	}

	if (vict.follows(aChar)) {
	    aChar.send("Mas " + vict.getName() + " já está seguindo você!");
	    return;
	}

	if (oldLeader != null)
	    stopFollow(aChar);

	aChar.startFollow(vict);
	aChar.send("Você começa seguir " + vict.getName() + ".");
    }
}
