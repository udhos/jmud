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

import java.util.Enumeration;

import jmud.Char;
import jmud.Named;
import jmud.Place;
import jmud.Creature;

public abstract class Hook implements Named {

    public static final int CTX_NONE    = 0;
    public static final int CTX_WALK    = 1;
    public static final int CTX_ARRIVAL = 2;

    // Aciona ganchos de todas as criaturas da sala.
    // Retorna o numero de criaturas que executaram a acao do
    // gancho.
    public static int apply(Place plc, int context, Char vict, int subContext) {

	int count = 0;
	for (Enumeration e = plc.getChars(); e.hasMoreElements(); ) {
	    Char currCh = (Char) e.nextElement();
	    if (!(currCh instanceof Creature))
		continue;

	    Creature currCreat = (Creature) currCh;
	    for (Enumeration hEnum = currCreat.getHookEnum();
		 hEnum.hasMoreElements(); ) {
		Hook proc = (Hook) hEnum.nextElement();
		if (proc.go(currCreat, context, vict, subContext))
		    ++count;
	    }
	}

	return count;
    }

    String label = null;

    Hook(String lbl) {
	label = lbl;
    }

    public String getLabel() {
	return label;
    }

    ////////
    // Named

    public boolean hasName(String name) {
	return name.equals(getLabel());
    }

    public String getName() {
	return getLabel();
    }

    // Named
    ////////

    // returns: true = deny command
    public abstract boolean go(Creature creat, int context,
			       Char vict, int subContext);
}
