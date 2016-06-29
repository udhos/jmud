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
import jmud.Player;
import jmud.Authenticable;

class NewbieLock extends Command {

    NewbieLock(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

  static final String SYNTAX = "Sintaxe: iniciantes [habilite|desabilite]";
  static final String DISABLE = "desabilite";
  static final String ENABLE = "habilite";

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

      Authenticable newbieLock = theManager.getNewbieLock();

      if (!toker.hasMoreTokens()) {
	if (newbieLock == null) {
	  aChar.send("A criação de novos personagens está habilitada.");
	}
	else
	  aChar.send("Criação de personagens desabilitada por: " + newbieLock.getString());

	return;
      }

      String opt = toker.nextToken();

      if (DISABLE.startsWith(opt)) {

	if (newbieLock == null) {
	  theManager.setNewbieLock(((Player) aChar).getCredentials());
	  aChar.send("Desabilitando criação de novos personagens.");
	}
	else
	  aChar.send("A criação de personagens já está desabilitada.");

      }
      else if (ENABLE.startsWith(opt)) {

	if (newbieLock == null)
	  aChar.send("A criação de personagens já está habilitada.");
	else {
	  if (aChar.getRank() < newbieLock.getRank()) {
	    aChar.send("Você não tem poder para remover a trava de criação de personagens.");
	    return;
	  }

	  theManager.setNewbieLock(null);
	  aChar.send("Habilitando criação de novos personagens.");
	}

      }
      else
	aChar.send(SYNTAX);

    }
}
