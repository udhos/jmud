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

// 	$Id: Heap.java,v 1.1.1.1 2001/11/14 09:01:57 fmatheus Exp $

package jgp.container;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import jgp.interfaces.RandomAccessable;
import jgp.interfaces.Enumerable;
import jgp.interfaces.PriorityQueue;
import jgp.predicate.BinaryPredicate;
import jgp.adaptor.RandomEnumeration;

public class Heap implements PriorityQueue, Enumerable, RandomAccessable {

    private BinaryPredicate greaterThan;
    private int             size;
    private int             capacity;
    private Object          data[];

    //////////
    // private

    public void setElementAt(int ind, Object info) {
	data[ind] = info;
    }

    public Object getElementAt(int ind) {
	return data[ind];
    }

    // private
    //////////

    private int leftChild(int parentInd) {
	return (parentInd << 1) + 1;
    }

    private int lowestChild(int parentInd) {
	int leftInd = leftChild(parentInd);
	int rightInd = leftInd + 1;
	return ((rightInd >= size) ? leftInd :
		greaterThan.execute(getElementAt(rightInd), getElementAt(leftInd)) ?
		leftInd : rightInd);
    }

    private int parent(int childInd) {
	return (childInd - 1) >> 1;
    }

    private void swap(int ind1, int ind2) {
	Object tmp = getElementAt(ind1);
	setElementAt(ind1, getElementAt(ind2));
	setElementAt(ind2, tmp);
    }

    private void up(int ind) {
	while (ind > 0) {
	    int parent = parent(ind);
	    if (greaterThan.execute(getElementAt(ind), getElementAt(parent)))
		break;
	    swap(ind, parent);
	    ind = parent;
	}
    }

    private void down(int ind) {
	int lowest = lowestChild(ind);
	while (lowest < size &&
	       greaterThan.execute(getElementAt(ind), getElementAt(lowest))) {

	    swap(ind, lowest);
	    ind = lowest;
	    lowest = lowestChild(ind);
	}
    }

    public Heap(BinaryPredicate gt, int cap) {
	greaterThan = gt;
	size        = 0;
	capacity    = cap;
	data        = new Object[capacity];
    }

    public Heap(BinaryPredicate gt) {
	greaterThan = gt;
	size        = 0;
	capacity    = 1;
	data        = new Object[capacity];
    }

    public void insert(Object info) {

	if (size >= capacity)
	    {
		capacity <<= 1;
		Object newData[] = new Object[capacity];

		for (int ind = 0; ind < size; ++ind)
		    newData[ind] = data[ind];

		data = newData;
	    }

	data[size] = info;
	up(size++);
    }

    public Object remove()
	throws NoSuchElementException {

	if (size < 1)
	    throw new NoSuchElementException();

	swap(0, --size);
	down(0);

	return data[size];
    }

    public Object top()
	throws NoSuchElementException {

	if (size < 1)
	    throw new NoSuchElementException();

	return data[0];
    }

    public int getSize() {
	return size;
    }

    public boolean isEmpty() {
	return getSize() == 0;
    }

    public Enumeration elements() {
	return new RandomEnumeration(this);
    }

    public void sort() {
	int sz = size;

	while (size > 0)
	    remove();

	size = sz;
    }
}
