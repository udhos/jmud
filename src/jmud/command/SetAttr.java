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

import jmud.Char;


import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class SetAttr extends Command {

    SetAttr(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    static final String SYNTAX = "Sintaxe: atribua <personagem> [todos|<atributo>|<coluna>|<linha>] <valor>";

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	Char vict = aChar.getPlace().findCharByName(toker.nextIndexedToken(), aChar);
	if (vict == null) {
	    aChar.send("Essa pessoa não está aqui no momento.");
	    return;
	}

	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	String attrName = toker.nextToken();

	if (!toker.hasMoreTokens()) {
	    aChar.send("Você deve passar o valor a ser atribuido.");
	    return;
	}

	int val = 0;
	try {
	    val = Integer.parseInt(toker.nextToken());
	}
	catch(NumberFormatException e) {
	    aChar.send("O valor deve ser numérico.");
	    return;
	}

	if (StrUtil.isAll(attrName)) {
	    vict.setAllRealAttr(val);
	    vict.attrFromReal();
	    aChar.send("Agora " + vict.getName() + " tem todos atributos em " + val + ".");
	    return;
	}

	int attrNum = -1;
	try {
	    attrNum = Integer.parseInt(attrName);
	    if (attrNum > -1 && attrNum < Char.attrNames.length) {
		vict.addAttr(attrNum, val - vict.getRealAttr(attrNum));
		vict.setRealAttr(attrNum, val);
		aChar.send("Agora " + vict.getName() + " tem " + Char.attrLabels[attrNum] + " em " + val + ".");
		return;
	    }
	}
	catch(NumberFormatException e) {
	    for (int i = 0; i < Char.attrNames.length; ++i)
		if (Char.attrNames[i].startsWith(attrName)) {
		    vict.addAttr(i, val - vict.getRealAttr(i));
		    vict.setRealAttr(i, val);
		    aChar.send("Agora " + vict.getName() + " tem " + Char.attrLabels[i] + " em " + val + ".");
		    return;
		}

	    for (int i = 0; i < Char.attrRowNames.length; ++i)
		if (Char.attrRowNames[i].startsWith(attrName)) {
		    vict.addAttrRow(i, val - vict.getRealAttr(i));
		    vict.setRealAttrRow(i, val);
		    aChar.send("Agora " + vict.getName() + " tem " + Char.attrRowLabels[i] + " em " + val + ".");
		    return;
		}

	    for (int i = 0; i < Char.attrColumnNames.length; ++i)
		if (Char.attrColumnNames[i].startsWith(attrName)) {
		    vict.addAttrColumn(i, val - vict.getRealAttr(i));
		    vict.setRealAttrColumn(i, val);
		    aChar.send("Agora " + vict.getName() + " tem " + Char.attrColumnLabels[i] + " em " + val + ".");
		    return;
		}
	}
	aChar.send("Esse atributo não existe.");
    }
}



