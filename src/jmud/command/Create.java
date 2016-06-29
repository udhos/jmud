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
import jmud.Item;
import jmud.ItemProto;
import jmud.Creature;
import jmud.CreatureProto;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Create extends Command {

    Create(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    static final String SYNTAX = "Sintaxe: crie item|criatura <n�mero>";
    static final String item   = "item";
    static final String creat  = "criatura";

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}
	String opt = toker.nextToken();

	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	int num = 0;
	try {
	    num = Integer.parseInt(toker.nextToken());
	}
	catch(NumberFormatException e) {
	    aChar.send("Voc� deve especificar um n�mero.");
	    return;
	}

	if (item.startsWith(opt)) {
	    ItemProto itPro = theWorld.findItemProtoById(num);
	    if (itPro == null)
		aChar.send("Item n�o encontrado.");
	    else {
		Item newItem = itPro.create();
		theWorld.insertItem(newItem, aChar);
		aChar.getPlace().action("$p criou $i!", true, aChar, newItem);
	    }
	}
	else if (creat.startsWith(opt)) {
	    CreatureProto crPro = theWorld.findCreatProtoById(num);
	    if (crPro == null)
		aChar.send("Criatura n�o encontrada.");
	    else {
		Creature newCreat = crPro.create();
		theWorld.insertChar(newCreat, aChar.getPlace());
		aChar.getPlace().action("$p criou $P!", true, aChar, newCreat);
	    }
	}
	else
	    aChar.send(SYNTAX);
    }
}


