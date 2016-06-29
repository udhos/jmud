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

import java.util.StringTokenizer;
import java.util.Enumeration;

import jgp.interfaces.Enumerable;
import jgp.container.Vector;
import jmud.util.Separators;

class TextBuffer implements Enumerable {

    private Vector buf  = new Vector();

    void insertAtBegin(String str) {
	if (buf.isEmpty())
	    buf.insert(str);
	else
	    buf.insertAt(0, str);
    }

    void insertAtEnd(String str) {
	buf.insert(str);
    }

    void insertAt(int ind, String str) throws ArrayIndexOutOfBoundsException {
	buf.insertAt(ind, str);
    }

    // null if empty
    String removeFromBegin() {
	return buf.isEmpty() ? null : (String) buf.removeFrom(0);
    }

    // null if empty
    String removeFromEnd() {
	return buf.isEmpty() ? null : (String) buf.remove();
    }

    String removeFrom(int ind) throws ArrayIndexOutOfBoundsException {
	return (String) buf.removeFrom(ind);
    }

    void clear() {
	buf.removeAll();
    }

    public Enumeration elements() {
	return buf.elements();
    }

    int getSize() {
	return buf.getSize();
    }

    public String toString() {
	String txt = "";
	for (Enumeration enum = buf.elements(); enum.hasMoreElements(); ) {
	    String str = (String) enum.nextElement();
	    txt += ((txt.length() == 0) ? str : (Separators.NL + str));
	}
	return txt;
    }

    void buildFrom(String txt) {
	StringTokenizer toker = new StringTokenizer(txt, Separators.NL);
	while (toker.hasMoreElements())
	    {
		String line = (String) toker.nextToken();
		insertAtEnd(line);
	    }
    }
}
