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

// 	$Id: Vector.java,v 1.1.1.1 2001/11/14 09:01:58 fmatheus Exp $

package jgp.container;

import java.util.NoSuchElementException;
import java.util.Enumeration;

import jgp.interfaces.Enumerable;
import jgp.interfaces.BackEnumerable;
import jgp.interfaces.Iterateable;
import jgp.interfaces.RandomAccessable;
import jgp.interfaces.Iterator;
import jgp.interfaces.Appendable;
import jgp.adaptor.VectorEnumeration;
import jgp.adaptor.BackVectorEnumeration;
import jgp.iterator.VectorIterator;

public class Vector implements Enumerable, BackEnumerable, RandomAccessable, Iterateable, Appendable {

  protected Object elem[]  = null;
  protected int    theSize = 0;

  public Vector() {
  }

  public Vector(int cap) {
    changeCapacity(cap);
  }

    public Vector(Enumeration e) {
	while (e.hasMoreElements())
	    insert(e.nextElement());
    }

  public Vector(Enumerable e) {
    for (Enumeration en = e.elements(); en.hasMoreElements(); )
      insert(en.nextElement());
  }

  public int getSize() {
    return theSize;
  }

  public int getCapacity() {
    return elem == null ? 0 : elem.length;
  }

  public void removeAll() {
    elem = null;
    theSize = 0;
  }

  private void changeCapacity(int newCap) {
    if (newCap == 0) {
      removeAll();
      return;
    }

    Object newElem[] = new Object[newCap];

    if (theSize > newCap)
      theSize = newCap;

    for (int i = 0; i < getSize(); ++i)
      newElem[i] = elem[i];
    elem = newElem;
  }

  public void insert(Object obj) {
    int cap = getCapacity();

    if (getSize() >= cap)
      changeCapacity(cap == 0 ? 1 : cap << 1);

    elem[theSize++] = obj;
  }

    public void insertAt(int ind, Object obj)
	throws ArrayIndexOutOfBoundsException {
	if (ind >= theSize)
	    throw new ArrayIndexOutOfBoundsException(ind);

	int cap = getCapacity();

	if (getSize() >= cap)
	    changeCapacity(cap == 0 ? 1 : cap << 1);

	int from = theSize - 1;
	int to   = theSize;
	while (to > ind)
	    elem[to--] = elem[from--];

	++theSize;

	elem[ind] = obj;
    }

    /////////////
    // Appendable

    public void append(Object obj) {
	insert(obj);
    }

    // Appendable
    /////////////

  public void append(Enumerable e) {
      if (e == this)
	  e = new Vector(e);

    for (Enumeration en = e.elements(); en.hasMoreElements(); )
      insert(en.nextElement());
  }

  public boolean isEmpty() {
    return theSize == 0;
  }

  public Object last() throws NoSuchElementException {
    if (isEmpty())
      throw new NoSuchElementException();

    return elem[theSize - 1];
  }

  public Object remove() throws NoSuchElementException {
    if (isEmpty())
      throw new NoSuchElementException();

    return elem[--theSize];
  }

    public Object remove(Object obj) throws NoSuchElementException {
	for (int i = 0; i < getSize(); ++i)
	    if (getElementAt(i) == obj)
		return removeFrom(i);

	throw new NoSuchElementException();
    }

    public int removeEvery(Object obj) {
        int removed = 0;

	for (int i = 0; i < getSize(); )
	    if (getElementAt(i) == obj) {
		removeFrom(i);
		++removed;
	    }
	    else
		++i;

	return removed;
    }


    public Object removeFrom(int ind)
	throws ArrayIndexOutOfBoundsException {
	if (ind >= theSize)
	    throw new ArrayIndexOutOfBoundsException(ind);

	Object obj = elem[ind];

	int to   = ind;
	int from = ind + 1;
	while (from < theSize)
	    elem[to++] = elem[from++];
	--theSize;

	return obj;
    }

  public void trim() {
    changeCapacity(getSize());
  }

  public Object getElementAt(int index) throws ArrayIndexOutOfBoundsException {
    if (index < 0 || index >= getSize())
      throw new ArrayIndexOutOfBoundsException(index);

    return elem[index];
  }

  public void setElementAt(int index, Object obj)
    throws ArrayIndexOutOfBoundsException {
    if (index < 0 || index >= getSize())
      throw new ArrayIndexOutOfBoundsException(index);

    elem[index] = obj;
  }

  public Enumeration elements() {
    return new VectorEnumeration(this);
  }

    public Enumeration backElements() {
	return new BackVectorEnumeration(this);
    }

    public Iterator getIterator() {
	return new VectorIterator(this);
    }
}
