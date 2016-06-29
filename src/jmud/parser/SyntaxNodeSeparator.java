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

import jgp.functor.UnaryFunction;

class SyntaxNodeSeparator implements UnaryFunction {

    private PrintWriter writer;
    private String      separator;
    private String      prefix;

    SyntaxNodeSeparator(PrintWriter pw, String sep, String pre) {
	writer    = pw;
	separator = sep;
	prefix    = pre;
    }

    public Object execute(Object obj) {
	writer.print(separator);
	SyntaxNode.showNewLine(writer, prefix);
	return null;
    }
}
