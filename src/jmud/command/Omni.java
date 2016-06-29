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
/*
  Sintaxe: onisciencia [<personagem>]

  Funcao: habilita ou desabilita onisciencia do personagem alvo. Se o
  personagem alvo nao for especificado, atua sobre o invocador.

  Onisciencia e' uma ferramenta utilizada para testes. Permite que usuarios
  privilegiados possam visualizar coisas como itens invisiveis, portas ocultas,
  identificadores de salas, etc.
*/

class Omni extends Command {

    Omni(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	Char vict = null;
	if (toker.hasMoreTokens()) {
	    vict = aChar.getPlace().findCharByName(toker.nextIndexedToken(), aChar);
	    if (vict == null) {
		aChar.send("Esse personagem não está nesta sala.");
		return;
	    }
	}
	else
	    vict = aChar;

	if (!(vict instanceof Player)) {
	    aChar.send("Criaturas não podem ter onisciência ativada.");
	    return;
	}

	Player pl = (Player) vict;

	if (pl.isOmni()) {
	    pl.turnOmni(false);
	    aChar.send("Onisciência desativada para " + pl.getName() + ".");
	}
	else {
	    pl.turnOmni(true);
	    aChar.send("Onisciência ativada para " + pl.getName() + ".");
	}

    }
}

