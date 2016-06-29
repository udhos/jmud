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

package jgp.container;

import java.util.NoSuchElementException;
import java.util.Enumeration;

import jgp.predicate.UnaryPredicate;
import jgp.functor.UnaryFunction;
import jgp.adaptor.VectorEnumeration;
import jgp.interfaces.Enumerable;

public class LRU implements Enumerable {

    private ListLink tail       = null;
    private int      capacity;
    private int      size;
    private boolean  lastWasHit = false;
    // private previous ListLink   = null;

    public LRU(int cap) {
	capacity = cap;
	size     = 0;
    }

    public int getSize() {
	return size;
    }

    public int getCapacity() {
	return capacity;
    }

    public boolean lastSearchHit() {
	return lastWasHit;
    }

    synchronized
    public void update(UnaryPredicate detector, Object info) {

	if (tail == null)
	    return;

	ListLink curr = tail;
	for ( ; ; ) {
	    ListLink next = curr.getNext();

	    // hit ?
	    if (detector.execute(next.getInfo())) {
		next.setInfo(info);
		return;
	    }

	    // miss ?
	    if (next == tail)
		return;

	    curr = next;
	}

    }

    synchronized
    public Object search(UnaryPredicate detector, UnaryFunction loader) {

	// empty ?
	if (tail == null) {

	    lastWasHit = false;

	    Object info = loader.execute(null);
	    if (info == null)
		return null;

	    if (size < capacity) {
		tail = new ListLink(info, null);
		tail.setNext(tail);
		++size;
	    }

	    return info;
	}

	// search
	ListLink curr = tail;
	for ( ; ; ) {
	    ListLink next = curr.getNext();

	    Object nextInfo = next.getInfo();

	    // hit ?
	    if (detector.execute(nextInfo)) {

		lastWasHit = true;

		// ja' esta' no lugar certo ?
		if (next != tail.getNext()) {

		    // esta' no final ?
		    if (next == tail)
			tail = curr;
		    else {
			curr.setNext(next.getNext());
			next.setNext(tail.getNext());
			tail.setNext(next);
		    }

		}

		return nextInfo;
	    }

	    // miss ?
	    if (next == tail) {

		lastWasHit = false;

		Object info = loader.execute(null);
		if (info == null)
		    return null;

		if (size < capacity) {
		    ListLink link = new ListLink(info, tail.getNext());
		    tail.setNext(link);
		    ++size;
		}
		else {
		    tail.setInfo(info);
		    tail = curr;
		}

		return info;
	    }

	    curr = next;
	}

    }

    synchronized
    public Enumeration elements() {

	Vector            v = new Vector();
	VectorEnumeration e = new VectorEnumeration(v);

	if (tail == null)
	    return e;

	ListLink curr = tail;
	for ( ; ; ) {
	    ListLink next = curr.getNext();

	    v.insert(next.getInfo());

	    if (next == tail)
		return e;

	    curr = next;
	}

    }

}
