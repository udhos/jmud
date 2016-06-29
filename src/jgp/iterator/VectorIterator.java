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

// 	$Id: VectorIterator.java,v 1.1.1.1 2001/11/14 09:01:58 fmatheus Exp $

package jgp.iterator;

import java.util.NoSuchElementException;

import jgp.container.Vector;
import jgp.interfaces.Iterator;

public class VectorIterator implements Iterator {

    private Vector myVec  = null;
    private int    cursor = 0;

    public VectorIterator(Vector vec) {
	myVec = vec;
	start();
    }

    public void start() {
	cursor = 0;
    }

  public boolean cont() {
    return cursor < myVec.getSize();
  }

  public void next() {
      ++cursor;
  }

  public Object getCurrent() {
    return myVec.getElementAt(cursor);
  }

  public Object getNext() throws NoSuchElementException {
    if (!hasNext())
      throw new NoSuchElementException();

    return myVec.getElementAt(cursor + 1);
  }

  public boolean hasNext() {
    return (cursor + 1 < myVec.getSize());
  }

  public void insertNext(Object obj) {
    myVec.insertAt(cursor + 1, obj);
  }

  public void removeNext() throws NoSuchElementException {
    if (!hasNext())
      throw new NoSuchElementException();

    myVec.removeFrom(cursor + 1);
  }
}
