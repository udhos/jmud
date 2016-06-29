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

package jmud;

import java.util.NoSuchElementException;
import java.util.Enumeration;

import jgp.container.Vector;
import jmud.command.IndexedToken;

public interface Owner {

    int getId();
    boolean hasName(String name);

  String getOwnerType();

  void insertItem(Item anItem);
  void removeItem(Item anItem) throws NoSuchElementException;

    /*
  Item findItemById(int itemId) throws NoSuchElementException;
  Item findItemByName(String name, int ind) throws NoSuchElementException;
  Vector findItemsByName(IndexedToken it);
    */

  Item findItemByName(String name, int ind, Char looker)
      throws NoSuchElementException;
  Vector findItemsByName(IndexedToken it, Char looker);

  String getItemList(Char looker);

  void extractItems();

  boolean hasFreeLoad(int load);

  Owner getOwner();

    Enumeration getContents();
}

