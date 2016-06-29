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
import jmud.Client;
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
import jgp.algorithm.Searcher;

class CopyCommand extends Command {

    CopyCommand(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send("De que quadro voc� quer copiar?");
	    return;
	}

	IndexedToken brdIndTok = toker.nextIndexedToken();

	if (!toker.hasMoreTokens()) {
	    aChar.send("Qual o n�mero da mensagem?");
	    return;
	}

	Item brd = aChar.searchItem(brdIndTok, aChar);
	if (brd == null) {
	    aChar.send("N�o h� isso aqui.");
	    return;
	}

	if (!(brd instanceof BoardItem)) {
	    aChar.send(StrUtil.upcaseFirst(brd.getName()) + " n�o � um quadro.");
	    return;
	}

	BoardItem brdItem = (BoardItem) brd;

	Board bs = brdItem.getBoardSystem();

	if (bs == null) {
	  Log.warn(aChar.getName() + " acessando item de quadro n�o operacional: " + brdItem.getId());
	  aChar.send("Este quadro n�o est� operacional.");
	  return;
	}

	if (bs.getReadLevel() > aChar.getRank()) {
	  aChar.send("Voc� n�o consegue entender tais hier�glifos.");
	  return;
	}

	if (!toker.hasMoreTokens()) {
	  aChar.send("Voc� deve especificar o n�mero da mensagem.");
	  return;
	}

	String msgInd = toker.nextToken();
	int ind = 0;
	try {
	  ind = Integer.parseInt(msgInd);
	}
	catch(NumberFormatException e) {
	  aChar.send("'" + msgInd + "' n�o � um n�mero v�lido de mensagem.");
	}

	BoardEntry be = bs.getMessage(ind);
	if (be == null) {
	  aChar.send("N�o existe essa mensagem.");
	  return;
	}

	Client cli = aChar.getClient();

	cli.clearNote();
	cli.buildNote(be.getBody());

	aChar.send("Mensagem copiada para suas anota��es.");
	aChar.getPlace().actionNotToChar("$p copia algo do quadro.", true, aChar);

    }
}



