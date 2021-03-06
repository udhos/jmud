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

package jmud;

import java.util.Enumeration;

import jgp.functor.UnaryFunction;

public class Merger {

    public static void list(Enumeration e, UnaryFunction performer, UnaryFunction separator) {
	while (e.hasMoreElements()) {
	    Object obj = e.nextElement();
	    separator.execute(obj);
	    performer.execute(obj);
	}
    }

    public static void headedList(Enumeration e, UnaryFunction performer, UnaryFunction separator) {
	if (e.hasMoreElements()) {
	    performer.execute(e.nextElement());
	    list(e, performer, separator);
	}
    }
}



