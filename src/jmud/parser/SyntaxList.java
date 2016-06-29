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


package jmud.parser;

import java.io.PrintWriter;
import java.util.Enumeration;

import jmud.Merger;
import jgp.container.Vector;

public class SyntaxList extends SyntaxNode {

    private Vector list = new Vector();

    public void add(SyntaxNode node) {
	list.insert(node);
    }

    public void show(PrintWriter pw, String prefix, String inc) {
	String newPrefix = prefix + inc;
	pw.print("(");
	showNewLine(pw, newPrefix);
	Merger.headedList(list.elements(), new SyntaxNodePrinter(pw, newPrefix, inc), new SyntaxNodeSeparator(pw, ";", newPrefix));
	showNewLine(pw, prefix);
	pw.print(")");
    }

    public Enumeration elements() {
	return list.elements();
    }
}
