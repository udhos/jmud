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

import java.util.Enumeration;
import java.util.Hashtable;
import java.io.PrintWriter;

import jgp.functor.UnaryFunction;
import jmud.Merger;

public class SyntaxMap extends SyntaxNode {

    private Hashtable map = new Hashtable();

    public void add(SyntaxLValue key, SyntaxNode value) {
	map.put(key, value);
    }

    public void show(PrintWriter pw, String prefix, String inc) {
	String newPrefix = prefix + inc;
	pw.print("{");
	showNewLine(pw, newPrefix);
	Merger.headedList(map.keys(), new MapItemPrinter(map, pw, newPrefix, inc), new SyntaxNodeSeparator(pw, ";", newPrefix));
	showNewLine(pw, prefix);
	pw.print("}");
    }

    public SyntaxNode get(String key) {
	return (SyntaxNode) map.get(key);
    }

    public Enumeration keys() {
	return map.keys();
    }
}

class MapItemPrinter implements UnaryFunction {

    private Hashtable   map;
    private PrintWriter writer;
    private String      prefix;
    private String      inc;

    MapItemPrinter(Hashtable m, PrintWriter pw, String pre, String i) {
	map    = m;
	writer = pw;
	prefix = pre;
	inc    = i;
    }

    public Object execute(Object obj) {
	SyntaxLValue key = (SyntaxLValue) obj;
	key.show(writer, prefix, inc);
	writer.print(" = ");
	SyntaxNode value = (SyntaxNode) map.get(key);
	value.show(writer, prefix, inc);
	return null;
    }
}

