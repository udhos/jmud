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

package jmud.jgp;

import jgp.predicate.UnaryPredicate;

import jmud.UniqueId;

public class IdMatcher implements UnaryPredicate {

    private int theId;

    public IdMatcher(int id) {
	theId = id;
    }

    public void setId(int id) {
	theId = id;
    }

    public int getId() {
	return theId;
    }

    public boolean execute(Object obj) {
	return ((UniqueId) obj).getId() == theId;
    }
}
