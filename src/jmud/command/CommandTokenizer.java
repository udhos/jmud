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

package jmud.command;

import jmud.util.log.Log;
import jmud.util.StrUtil;

public class CommandTokenizer {

    private String line;
    private int    cursor;
    private int    length;

    public CommandTokenizer(String str) {
	cursor = 0;
	line   = str.trim();
	length = line.length();
    }

    private void skipSpace() {
	while ((cursor < length) && (line.charAt(cursor) == ' '))
	    ++cursor;
    }

    public boolean hasMoreTokens() {
	skipSpace();
	return cursor < length;
    }

    public String nextToken() {
	skipSpace();
	if (cursor >= length) {
	    Log.err("Faltou hasMoreTokens aqui: " + line);
	    return "*ERRO*";
	}

	int start = cursor;
	cursor = line.indexOf(' ', start);
	if (cursor == -1)
	    cursor = length;
	return line.substring(start, cursor);
    }

    private String specialToken() {
	if (cursor >= length)
	    return null;

	if (line.charAt(cursor) == '.') {
	    ++cursor;
	    if (cursor >= length)
		return null;
	}

	if (line.charAt(cursor) == ' ')
	    return null;

	int start = cursor;

	if (line.charAt(start) == '\'') {
	    cursor = line.indexOf('\'', ++start);
	    return line.substring(start, (cursor == -1) ? length : cursor++);
	}

	cursor = line.indexOf(' ', start);
	if (cursor == -1)
	    cursor = length;
	for (int i = start; i < cursor; ++i)
	    if (line.charAt(i) == '.') {
		cursor = i;
		return line.substring(start, cursor++);
	    }

	return line.substring(start, cursor);
    }

    public IndexedToken nextIndexedToken() {

	skipSpace();
	if (cursor >= length) {
	    Log.err("Faltou hasMoreTokens aqui: " + line);
	    return new IndexedToken(1, "*ERRO*", "*ERRO*");
	}

	int    index   = 1;
	String target  = specialToken();
	try {
	    index = Integer.parseInt(target);
	    target = specialToken();
	}
	catch(NumberFormatException e) {
	    if (StrUtil.isAll(target)) {
		index = 0;
		target = specialToken();
	    }
	}

	String context = specialToken();

	return new IndexedToken(index, target, context);
    }

    public String nextQuotedToken() {
	skipSpace();
	if (cursor >= length) {
	    Log.err("Faltou hasMoreTokens aqui: " + line);
	    return "*ERRO*";
	}
	int start  = cursor;
	if (line.charAt(start) == '\'')
	    cursor = line.indexOf('\'', ++start);
	else
	    cursor = line.indexOf(' ', start);

	if (cursor == -1)
	    cursor = length;

	return line.substring(start, cursor++);
    }

    public String getTail() {
	skipSpace();
	if (cursor >= length)
	    return "";
	return line.substring(cursor, length);
    }
}
