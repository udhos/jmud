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

import jmud.util.Separators;

class Abilities extends Command {

    Abilities(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	Char vict = null;

	if (toker.hasMoreTokens() && (aChar.isAdmin())) {

	    vict = aChar.getPlace().findCharByName(toker.nextIndexedToken(), aChar);
	    if (vict == null) {
		aChar.send("Ninguém com esse nome por aqui.");
		return;
	    }

	    if (aChar != vict && aChar.forbidAccess(vict))
		return;
	}
	else
	    vict = aChar;

	aChar.send("Habilidades de '" + vict.getName() + "':" + Separators.NL);
	vict.showAbilityList(aChar, Separators.NL);
    }
}



