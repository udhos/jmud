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

// 	$Id: Iterator.java,v 1.1.1.1 2001/11/14 09:01:58 fmatheus Exp $

package jgp.interfaces;

import java.util.NoSuchElementException;

public interface Iterator {

  void start();
  boolean cont();
  void next();

  Object getCurrent();
  Object getNext() throws NoSuchElementException;

  boolean hasNext();
  void insertNext(Object obj);
  void removeNext() throws NoSuchElementException;
}
