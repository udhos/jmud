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
import jmud.Keywords;
import jmud.jgp.Enchainer;
import jmud.jgp.StringExtractor;
import jmud.util.Separators;

class Hosts extends Command {

    Hosts(String name, int mRank, int mPos, int opt, String syn) {
	super(name, mRank, mPos, opt, syn);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	if (!toker.hasMoreTokens()) {
	    sendSyntax(aChar);
	    return;
	}

	IndexedToken indTok = toker.nextIndexedToken();

	Player plr = null;

	if (toker.hasMoreTokens()) {
	    String name = indTok.getTarget();
	    String opt = toker.nextIndexedToken().getTarget();
	    if (!Keywords.FILE.startsWith(opt)) {
		sendSyntax(aChar);
		return;
	    }

	    plr = Player.load(name);
	    if (plr == null) {
		aChar.send("Não encontrado em arquivo.");
		return;
	    }
	}

	else {
	    Char vict = theWorld.findCharByName(indTok, aChar);
	    if (vict == null) {
		aChar.send("Personagem não encontrado.");
		return;
	    }

	    if (!(vict instanceof Player)) {
		aChar.send("Esse personagem não é jogador.");
		return;
	    }

	    plr = (Player) vict;
	}

	aChar.send("Últimas máquinas de '" + plr.getName() + "':");

	Enchainer.list(aChar, plr.getHostEnum(), new StringExtractor(), Separators.NL);

    }
}



