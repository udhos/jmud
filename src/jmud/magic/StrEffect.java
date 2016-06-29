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

package jmud.magic;

import jmud.Char;
import jmud.Effect;
import jmud.Affectable;

class StrEffect extends Effect {

  private int            theValue;

  StrEffect(int duration, Affectable vict, int value) {
    super(duration, vict);
    theValue  = value;
  }

    public void apply(boolean quiet) {
	super.apply(quiet);
	theVictim.addStr(theValue);
	if (!quiet)
	    ((Char) theVictim).send(theValue >= 0 ?
				    "Sua força aumenta!" :
				    "Sua força diminui.");
    }

    public void undo(boolean quiet) {
	super.undo(quiet);
	theVictim.addStr(-theValue);
	if (!quiet)
	    ((Char) theVictim).send(theValue >= 0 ?
				    "Sua força diminui!" :
				    "Sua força aumenta.");
    }

    public String getSheet() {
	return "força " + theValue + ": " + super.getSheet();
    }
}
