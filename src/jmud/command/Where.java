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

import java.util.Enumeration;

import jmud.Char;
import jmud.Item;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Where extends Command {

    Where(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    static final String CREATURE = "criatura";
    static final String ITEM     = "item";
    static final String SYNTAX   = "Sintaxe: onde item|criatura <nome>";
    static final int LEFT_WIDTH  = 30;

    static String getIdent(Char ch) {
	return StrUtil.rightWidth(ch.getName() + " [" + ch.getId() + "]", LEFT_WIDTH) + " " + ch.getWhere();
    }

    static String getIdent(Item it) {
	return StrUtil.rightWidth(it.getName() + " [" + it.getId() + "]", LEFT_WIDTH) + " " + it.getWhere();
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	String kind   = toker.nextToken();

	IndexedToken indTok = null;
	if (toker.hasMoreTokens())
	    indTok = toker.nextIndexedToken();
	else
	    indTok = new IndexedToken(0);

	if (CREATURE.startsWith(kind)) {

	    Vector list = theWorld.findAllCharsByName(indTok, aChar);

	    String str = "";

	    Enumeration e = list.elements();
	    if (e.hasMoreElements()) {
		Char ch = (Char) e.nextElement();
		str += getIdent(ch);
	    }

	    while (e.hasMoreElements()) {
		Char ch = (Char) e.nextElement();
		str += Separators.NL + getIdent(ch);
	    }

	    aChar.send(str);
	}
	else if (ITEM.startsWith(kind)) {

	    Vector list = theWorld.findAllItemsByName(indTok, aChar);

	    String str = "";

	    Enumeration e = list.elements();
	    if (e.hasMoreElements()) {
		Item it = (Item) e.nextElement();
		str += getIdent(it);
	    }

	    while (e.hasMoreElements()) {
		Item it = (Item) e.nextElement();
		str += Separators.NL + getIdent(it);
	    }

	    aChar.send(str);
	}
	else
	    aChar.send(SYNTAX);
    }
}
