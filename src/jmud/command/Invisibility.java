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
import jmud.Place;

class Invisibility extends Command {

    Invisibility(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	int rank      = aChar.getRank();
	int invisRank = rank;

	if (toker.hasMoreTokens()) {
	    try {
		invisRank = Integer.parseInt(toker.nextToken());
	    }
	    catch (NumberFormatException e) {
		aChar.send("O posto especificado deve ser numérico.");
		return;
	    }
	}

	if (invisRank > rank)
	    invisRank = rank;

	Place plc = aChar.getPlace();

	if (invisRank == 0) {
	    aChar.setInvisRank(invisRank);
	    plc.action("$p aparece.", true, aChar);
	}
	else {
	    plc.action("$p desaparece.", true, aChar);
	    aChar.setInvisRank(invisRank);
	}

    }
}
