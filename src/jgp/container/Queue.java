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

// 	$Id: Queue.java,v 1.1.1.1 2001/11/14 09:01:58 fmatheus Exp $

package jgp.container;

import java.util.NoSuchElementException;

public class Queue {

    private Object[] elem   = null;
    private int      first    = 0;
    private int      pastEnd  = 0;

    public Queue() {
	elem = new Object[1];
    }

    public Queue(int cap) {
	elem = new Object[cap];
    }

    public int getCapacity() {
	return elem == null ? 0 : elem.length;
    }

    public void insert(Object obj) {

	int cap = getCapacity();

	if ((pastEnd + 1) % cap == first) {

	    int newCap = cap << 1;
	    Object[] newElem = new Object[newCap];

	    int i = first;
	    int k = i % cap;
	    int j = 0;
	    while (k != pastEnd) {
		newElem[j] = elem[k];
		++i;
		k = i % cap;
		++j;
	    }

	    elem = newElem;

	    first   = 0;
	    pastEnd = cap;

	    elem[pastEnd - 1] = obj;

	    return;
	}

	elem[pastEnd++] = obj;
	pastEnd %= elem.length;
    }

    public Object remove() throws NoSuchElementException {
	if (isEmpty())
	    throw new NoSuchElementException();

	Object obj = elem[first++];

	first %= elem.length;

	return obj;
    }

    public boolean isEmpty() {
	return first == pastEnd;
    }

    public int getSize() {
	int dist = Math.abs(pastEnd - first);
	return first <= pastEnd ? dist : getCapacity() - dist;
    }

    ////////
    // Debug

    public int getFirst() {
	return first;
    }

    public int getPastEnd() {
	return pastEnd;
    }

    public String toString() {
	String s = "";

	int cap = getCapacity();

	int i = first;
	int k = i % cap;
	int j = 0;
	while (k != pastEnd) {
	    s += elem[k] + " ";
	    ++i;
	    k = i % cap;
	    ++j;
	}

	return s;
    }

    // Debug
    ////////
}
