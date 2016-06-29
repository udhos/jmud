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
import jmud.Door;
import jmud.Creature;
import jmud.util.bit.Bit;

class Blocker extends Hook {

    static final int D_NORTH = Bit.BIT0;
    static final int D_SOUTH = Bit.BIT1;
    static final int D_EAST  = Bit.BIT2;
    static final int D_WEST  = Bit.BIT3;
    static final int D_UP    = Bit.BIT4;
    static final int D_DOWN  = Bit.BIT5;

    private int directions;

    Blocker(String lbl, int dir) {
	super(lbl);
	directions = dir;
    }

    boolean block(Creature creat, Char ch) {
	creat.getPlace().action("$p impede que $P prossiga.", true, creat, ch);
	return true;
    }

    public boolean go(Creature creat, int context, Char vict, int direction) {

	if (context != Hook.CTX_WALK)
	    return false;

	if (creat == vict)
	    return false;

	if (!vict.canBeSeenBy(creat))
	    return false;

	int pos = creat.getPosition();
	if (pos < Char.P_STANDING || pos > Char.P_AWARE)
	    return false;

	if (direction == Door.D_NORTH && Bit.isSet(directions, D_NORTH))
	    return block(creat, vict);

	if (direction == Door.D_SOUTH && Bit.isSet(directions, D_SOUTH))
	    return block(creat, vict);

	if (direction == Door.D_EAST && Bit.isSet(directions, D_EAST))
	    return block(creat, vict);

	if (direction == Door.D_WEST && Bit.isSet(directions, D_WEST))
	    return block(creat, vict);

	if (direction == Door.D_UP && Bit.isSet(directions, D_UP))
	    return block(creat, vict);

	if (direction == Door.D_DOWN && Bit.isSet(directions, D_DOWN))
	    return block(creat, vict);

	return false;
    }

}
