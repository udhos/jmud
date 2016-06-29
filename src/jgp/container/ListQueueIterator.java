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

// 	$Id: ListQueueIterator.java,v 1.1.1.1 2001/11/14 09:01:58 fmatheus Exp $

package jgp.container;

import java.util.NoSuchElementException;

import jgp.interfaces.Iterator;

public class ListQueueIterator implements Iterator {

  private ListQueue myQueue = null;
  private ListLink  cursor  = null;

  ListQueueIterator(ListQueue aQueue) {
    myQueue = aQueue;
    start();
  }

  public void start() {
    cursor = myQueue.getHead();
  }

  public boolean cont() {
    return cursor != null;
  }

  public void next() {
    cursor = cursor.getNext();
  }

  public Object getCurrent() {
    return cursor.getInfo();
  }

  public Object getNext() throws NoSuchElementException {
    if (!hasNext())
      throw new NoSuchElementException();

    return cursor.getNext().getInfo();
  }

  public boolean hasNext() {
    return (cont() && cursor.getNext() != null);
  }

  public void insertNext(Object info) {
    cursor.setNext(new ListLink(info, cursor.getNext()));

    ListLink last = myQueue.getTail().getNext();

    if (last != null)
      myQueue.setTail(last);
  }

  public void removeNext() throws NoSuchElementException {
    if (!hasNext())
      throw new NoSuchElementException();

    ListLink next = cursor.getNext();

    if (myQueue.getTail() == next)
      myQueue.setTail(cursor);

    cursor.setNext(next.getNext());
  }
}
