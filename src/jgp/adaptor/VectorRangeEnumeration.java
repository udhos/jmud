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

// 	$Id: VectorRangeEnumeration.java,v 1.1.1.1 2001/11/14 09:01:57 fmatheus Exp $

package jgp.adaptor;

import java.util.NoSuchElementException;
import java.util.Enumeration;

import jgp.container.Vector;

public class VectorRangeEnumeration implements Enumeration {

  private Vector vect   = null;
  private int    cursor;
  private int    pastEnd;

  public VectorRangeEnumeration(Vector v, int first, int size) {
    vect    = v;
    cursor  = first;
    pastEnd = size;
  }

  public boolean hasMoreElements() {
    return cursor < pastEnd;
  }

  public Object nextElement() throws NoSuchElementException {
    if (!hasMoreElements())
      throw new NoSuchElementException();

    return vect.getElementAt(cursor++);
  }
}
