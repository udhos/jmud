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

// 	$Id: BFSTree.java,v 1.1.1.1 2001/11/14 09:01:58 fmatheus Exp $

package jgp.container;

import java.util.Enumeration;

import jgp.adaptor.VectorRangeEnumeration;

public class BFSTree {

    private Vector vect  = null;
    private int theDepth = 0;
    private int index[]  = null; // index e' um nome expressivo?

    public BFSTree(int maxDepth) {
	index = new int[maxDepth];
	vect  = new Vector();
    }

    public BFSTree(int maxDepth, Object ob) {
	this(maxDepth);
	newLevel();
	insert(ob);
    }

    public int getDepth() {
	return theDepth;
    }

    public int newLevel()
	throws ArrayIndexOutOfBoundsException {

	if (theDepth >= index.length)
	    throw new ArrayIndexOutOfBoundsException(theDepth);

	index[theDepth++] = vect.getSize();
	return theDepth;
    }

    public void insert(Object ob) {
	vect.insert(ob);
    }

    public Object getLast() {
	return vect.getElementAt(vect.getSize()-1);
    }

    public Enumeration elements() {
	return vect.elements();
    }

    // O que e' um sublevel?
    // O que e' "d"?
    public Enumeration sublevel(int d)
	throws ArrayIndexOutOfBoundsException {
	if (theDepth <= d || d < 0)
	    throw new ArrayIndexOutOfBoundsException(d);

	int last = ((d == (theDepth - 1)) ? vect.getSize() : index[d + 1]);

	return new VectorRangeEnumeration(vect, index[d], last);
    }

}
