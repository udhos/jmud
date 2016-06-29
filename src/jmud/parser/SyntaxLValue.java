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

public abstract class SyntaxLValue extends SyntaxNode {

    private String str;

    SyntaxLValue(String s) {
	str = s;
    }

    public int hashCode() {
	return str.hashCode();
    }

    public boolean equals(Object obj) {
	if (obj instanceof String)
	    return str.equals((String) obj);
	if (obj instanceof SyntaxLValue)
	    return str.equals(((SyntaxLValue) obj).str);
	return false;
    }

    public void show(PrintWriter pw, String prefix, String inc) {
	pw.print(str);
    }

    public String getString() {
	return str;
    }

}
