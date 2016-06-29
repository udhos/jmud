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

import jmud.command.IndexedToken;

public interface Place extends Owner {

    String getTitle();
    String getPlaceType();

    boolean isRoom(); // lugar de fato
    boolean isOpen(); // lugar aberto (nao sala?)

    Zone getZone();
    Enumeration getExits();

    String getFullDescription(Char aChar);

    void insertChar(Char aChar);
    void removeChar(Char aChar) throws NoSuchElementException;

    Char findCharByName(String name, int ind, Char looker);
    Char findCharByName(IndexedToken it, Char looker);

    ShopCreature findShopKeeper();
    Object[] findMaster();

    Enumeration getChars();

    String getItemBriefList(Char aChar);
    String getCharBriefList(Char aChar);

    Door findDoorByName(String door);

    void action(String act, boolean hide, Char ch, Char vict, Item it1, Item it2);
    void action(String act, boolean hide, Char ch, Char vict, Item it);
    void action(String act, boolean hide, Char ch, Char vict);
    void action(String act, boolean hide, Char ch, Item it1, Item it2);
    void action(String act, boolean hide, Char ch, Item it);
    void action(String act, boolean hide, Char ch);
    void actionNotToChar(String act, boolean hide, Char ch, Char vict, Item it1, Item it2);
    void actionNotToChar(String act, boolean hide, Char ch, Char vict, Item it);
    void actionNotToChar(String act, boolean hide, Char ch, Char vict);
    void actionNotToChar(String act, boolean hide, Char ch, Item it1, Item it2);
    void actionNotToChar(String act, boolean hide, Char ch, Item it);
    void actionNotToChar(String act, boolean hide, Char ch);
}

