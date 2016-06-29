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

class Erase extends Command {

    Erase(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	if (!toker.hasMoreTokens()) {
	    aChar.send("O que você quer apagar?");
	    return;
	}

	IndexedToken brdIndTok = toker.nextIndexedToken();

	if (!toker.hasMoreTokens()) {
	    aChar.send("Qual o número da mensagem?");
	    return;
	}

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
	    aChar.send("Você deve especificar o número da mensagem.");
	    return;
	}

	int ind = 0;
	try {
	    ind = Integer.parseInt(toker.nextToken());
	}
	catch(NumberFormatException e) {
	    aChar.send("Isso não é um número válido de mensagem.");
	}

	BoardEntry be = bs.getMessage(ind);
	if (be == null) {
	    aChar.send("Não existe essa mensagem.");
	    return;
	}

	if (bs.getAdmLevel() > aChar.getRank() && !BoolUtil.xor(aChar.isPlayer(), be.authorIsPlayer())) {
	    aChar.send("Você não pode apagar uma mensagem alheia!");
	    return;
	}

	if (aChar.getRank() < be.getAuthorRank()) {
	    aChar.send("Você não consegue apagar uma escrita tão poderosa.");
	    return;
	}

	try {
	    bs.eraseMessage(ind);
	    aChar.getPlace().actionNotToChar("$p apaga algo do quadro.", true, aChar);
	    aChar.send("Mensagem apagada.");
	}
	catch(ArrayIndexOutOfBoundsException e) {
	    Log.err(aChar.getName() + " apagando mensagem inexistente [" + ind + "] do item de quadro: " + brdItem.getId());
	    aChar.send("Falha interna no sistema de quadros. Avise a administração.");
	}
    }
}


