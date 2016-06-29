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
import jmud.Keywords;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Annotate extends Command {

    Annotate(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	Client cli = aChar.getClient();

	if (!toker.hasMoreTokens()) {
	    cli.lookNote();
	    return;
	}

	String subCmd = toker.getTail();

	if (subCmd.startsWith("+"))
	    if (cli.getNoteSize() < Client.MAX_NOTE_SIZE) {
		String line = subCmd.substring(1);

		// impede caractere inválido
		// (marcador de fim de descrição)
		if (line.startsWith(Separators.EOD))
		    line = " " + line;
		cli.addNoteLine(line);
		aChar.send("Linha anotada.");
	    }
	    else
		aChar.send("Você não pode adicionar mais linhas às anotações.");
	else if (subCmd.startsWith("-")) {
	    cli.remNoteLine();
	    aChar.send("Linha removida.");
	}
	else if (subCmd.startsWith(Keywords.CLEANUP)) {
	    cli.clearNote();
	    aChar.send("Anotações apagadas.");
	}
	else
	    sendSyntax(aChar);
    }
}



