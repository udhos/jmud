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

class Write extends Command {

    Write(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	if (!toker.hasMoreTokens()) {
	    aChar.send("Em que você quer escrever?");
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

	if (bs.getWriteLevel() > aChar.getRank()) {
	    aChar.send("Uma força irresistível o impede de tocar o quadro.");
	    return;
	}

	if (!toker.hasMoreTokens()) {
	    aChar.send("Você deve especificar um título para a mensagem.");
	    return;
	}

	String title = toker.getTail();

	Client cli = aChar.getClient();

	if (cli.getNoteSize() < 1) {
	    aChar.send("Suas anotações estão vazias.");
	    return;
	}

	String body = cli.getNoteText();

	BoardEntry be = new BoardEntry(aChar.getId(), aChar.getRank(), aChar.isPlayer(), aChar.getName(), theManager.getWorldTime(), title, body);

	bs.putMessage(be);

	aChar.send("Mensagem transcrita das suas anotações.");

	aChar.getPlace().actionNotToChar("$p escreve algo no quadro.", true, aChar);
    }
}



