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

package jmud.hook;

import java.util.StringTokenizer;

import jmud.Char;
import jmud.Creature;

class Agressive extends Hook {

    Agressive(String lbl) {
	super(lbl);
    }

    public boolean go(Creature creat, int context, Char vict, int bogus) {

	if (context != Hook.CTX_ARRIVAL)
	    return false;

	if (creat == vict)
	    return false;

	if (!vict.canBeSeenBy(creat))
	    return false;

	int pos = creat.getPosition();
	if (pos < Char.P_STANDING || pos > Char.P_AWARE)
	    return false;

	creat.getPlace().action("$p agride $P!", true, creat, vict);
	creat.startFight(vict);

	return true;
    }

}
