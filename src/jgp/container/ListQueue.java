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

// 	$Id: ListQueue.java,v 1.1.1.1 2001/11/14 09:01:58 fmatheus Exp $

package jgp.container;

import java.util.NoSuchElementException;
import java.util.Enumeration;

import jgp.interfaces.Iterator;

public class ListQueue extends List {

  private ListLink tail = null;

  public ListQueue() {
  }

  private void add(ListLink aLink) {
    if (tail != null)
      tail.setNext(aLink);
    tail = aLink;
    if (head == null)
      head = tail;
  }

  public void insert(Object info) {
    add(new ListLink(info, null));
  }

  ListLink getTail() {
    return tail;
  }

  void setTail(ListLink t) {
    tail = t;
  }

  private ListLink findLastLink() {
    if (head == null)
      return null;

    ListLink curr = head;

    while (curr.getNext() != null)
      curr = curr.getNext();

    return curr;
  }

  public void remove(Object info) throws NoSuchElementException {
    super.remove(info);

    if (head == null)
      tail = null;
    else if (tail.getInfo() == info)
      tail = findLastLink();
  }

  public Object remove() throws NoSuchElementException {
    if (head == null)
      throw new NoSuchElementException();

    Object info = head.getInfo();
    head = head.getNext();

    if (head == null)
      tail = null;

    return info;
  }

  public Object clone() {

    ListQueue cloneQueue = new ListQueue();
    Enumeration myElements = elements();

    if (myElements.hasMoreElements()) {

      ListLink prevLink = new ListLink(myElements.nextElement(), null);
      cloneQueue.add(prevLink);

      while (myElements.hasMoreElements()) {
	prevLink.setNext(new ListLink(myElements.nextElement(), null));
	prevLink = prevLink.getNext();
      }

    }

    return cloneQueue;
  }

  public Iterator getIterator() {
    return new ListQueueIterator(this);
  }
}


