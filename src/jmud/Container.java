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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Enumeration;

import jmud.util.Separators;
import jmud.command.IndexedToken;
import jmud.jgp.FancyNameExtractor;
import jmud.jgp.VisibilityNameMatcher;
import jmud.jgp.ItemCloneDetector;
import jgp.container.Vector;
import jgp.container.List;
import jgp.interfaces.Iterator;
import jgp.algorithm.Searcher;
import jgp.adaptor.Finder;

class Container extends Item implements Owner {

  protected List theItems  = new List();
  private   int  capacity  = 0;
  private   int  available = 0;

  Container(ContainerProto c) {
    super(c);

    capacity  = c.capacity;
    available = c.capacity;
  }

  protected void finalize() {
    super.finalize();
  }

  int getType() {
    return T_CONTAINER;
  }

  String getFullDescription(Char looker) {
    String list = getItemList(looker);
    return getDescription() + Separators.NL + "Contém:" + ((list.length() == 0) ? (Separators.NL + "Nada") : list);
  }

  public Owner getContainer() {
    return this;
  }

  //////////////////////////
  // Implementação de Owner:

  public int getId() {
    return super.getId();
  }

  public String getOwnerType() {
    return "recipiente";
  }

  private void addLoadOf(Item it) {
    int load = it.getLoad();
    available -= load;
    theLoad += load;
  }

  private void subLoadOf(Item it) {
    int load = it.getLoad();
    available += load;
    theLoad -= load;
  }

  public void insertItem(Item it) {
    CloneAlgo.insert(theItems, it, new ItemCloneDetector());
    addLoadOf(it);
  }

  public void removeItem(Item anItem) throws NoSuchElementException {
    theItems.remove(anItem);
    subLoadOf(anItem);
  }

    /*
  public Item findItemById(int itemId) {
    return (Item) Searcher.linearSearch(theItems, new IdMatcher(itemId));
  }

  public Item findItemByName(String name, int ind) {
    return (Item) Searcher.linearSearch(theItems, new IdMatcher(ind), ind);
  }

  public Vector findItemsByName(IndexedToken it) {
      if (it.isAll())
	  return Finder.findAll(theItems, new NameMatcher(it.getTarget()));

      Vector set = new Vector();
      Object obj = Searcher.linearSearch(theItems, new NameMatcher(it.getTarget()), it.getIndex());
      if (obj != null)
	  set.insert(obj);
      return set;
  }
    */

  public Item findItemByName(String name, int ind, Char looker) {
    return (Item) Searcher.linearSearch(theItems, new VisibilityNameMatcher(name, looker), ind);
  }

  public Vector findItemsByName(IndexedToken it, Char looker) {
      if (it.isAll())
	  return Finder.findAll(theItems, new VisibilityNameMatcher(it.getTarget(), looker));

      Vector set = new Vector();
      Object obj = Searcher.linearSearch(theItems, new VisibilityNameMatcher(it.getTarget(), looker), it.getIndex());
      if (obj != null)
	  set.insert(obj);
      return set;
  }

  public String getItemList(Char looker) {
      return CloneAlgo.getList(theItems, looker, new ItemCloneDetector(), new FancyNameExtractor());
  }

  public void extractItems() {
    for (Enumeration items = theItems.elements(); items.hasMoreElements(); ) {
      Item curr = (Item) items.nextElement();
      Owner ow = curr.getContainer();
      if (ow != null)
	ow.extractItems();
      theWorld.removeItem(curr);
    }
  }

  public boolean hasFreeLoad(int load) {
    return load <= available;
  }

    public Enumeration getContents() {
	return theItems.elements();
    }

  //
  //////////////////////////

  /////////////
  // Sheetable:

  public String getSheet() {
    return super.getSheet() + Separators.NL +
      "Capacidade: " + capacity + "  Disponível: " + available;
  }

    //
    //////////////


    void save(BufferedWriter writer) throws IOException {
	super.save(writer);
	writer.write(String.valueOf(capacity)); writer.newLine();
    }

}

