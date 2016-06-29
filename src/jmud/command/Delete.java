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
import jmud.Player;
import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Delete extends Command {

    Delete(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    static final String SELF   = "me";
    static final String SYNTAX = "Sintaxe: exclua me|<nome>";

    void delete(Player ch, Player vict) {
	if (vict.isDeleted()) {
	    vict.undelete();
	    ch.send("Exclusão de " + vict.getName() + " cancelada.");
	    Log.info("Exclusão de " + vict.getName() + " cancelada por " + ch.getName());
	}
	else {
	    vict.delete();
	    ch.send("Personagem " + vict.getName() + " marcado para exclusão.");
	    Log.info(vict.getName() + " marcado para exclusão por " + ch.getName());
	}
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	String name = toker.nextToken();

	if (name.equals(SELF) || aChar.hasName(name))
	    delete((Player) aChar, (Player) aChar);
	else {
	    if (aChar.getRank() < Char.R_MODERATOR) {
		aChar.send("Você não pode fazer isso com os outros.");
		return;
	    }

	    Client cli = theManager.findClientByName(name);
	    if (cli == null) {
		aChar.send("Cliente não encontrado.");
		return;
	    }

	    Char vict = cli.getChar();

	    if (aChar.forbidAccess(vict))
		return;

	    delete((Player) aChar, (Player) vict);
	}
    }
}


