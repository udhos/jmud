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
import jmud.Board;
import jmud.BoardItem;
import jmud.BoardEntry;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Read extends Command {

    Read(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	if (!toker.hasMoreTokens()) {
	    aChar.send("O que você quer ler?");
	    return;
	}

	IndexedToken brdIndTok = toker.nextIndexedToken();

	Item brd = aChar.searchItem(brdIndTok, aChar);
	if (brd == null) {
	    aChar.send("Não há isso aqui.");
	    return;
	}

	if (!(brd instanceof BoardItem)) {
	    aChar.send(StrUtil.upcaseFirst(brd.getName()) + " não é um quadro.");
	    return;
	}

	BoardItem brdItem = (BoardItem) brd;

	Board bs = brdItem.getBoardSystem();

	if (bs == null) {
	    Log.warn(aChar.getName() + " acessando item de quadro não operacional: " + brdItem.getId());
	    aChar.send("Este quadro não está operacional.");
	    return;
	}

	if (bs.getReadLevel() > aChar.getRank()) {
	    aChar.send("Você não consegue entender tais hieróglifos.");
	    return;
	}

	if (toker.hasMoreTokens()) {
	    int ind = 0;
	    try {
		ind = Integer.parseInt(toker.nextToken());
	    }
	    catch(NumberFormatException e) {
		aChar.send("Você deve especificar o número da mensagem.");
	    }

	    BoardEntry be = bs.getMessage(ind);
	    if (be == null) {
		aChar.send("Não existe essa mensagem.");
		return;
	    }

	    aChar.send(StrUtil.formatNumber(ind, 2) + ". "  + be.getHeader() + Separators.NL +
		       "------------------------------------------------------------------------------" + Separators.NL + be.getBody());
	    aChar.getPlace().actionNotToChar("$p lê algo no quadro.", true, aChar);
	}
	else {
	    String summ = bs.getSummary();
	    aChar.send("Sumário: " + (summ.length() == 0 ? (Separators.NL + "Vazio") : summ));
	    aChar.getPlace().actionNotToChar("$p lê algo no quadro.", true, aChar);
	}
    }
}

