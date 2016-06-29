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
import jmud.PlayerCache;
import jmud.jgp.Enchainer;
import jmud.jgp.NameIdExtractor;

class Cache extends Command {

    Cache(String name, int mRank, int mPos, int opt, String syn) {
	super(name, mRank, mPos, opt, syn);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	aChar.send("CACHE");

	PlayerCache plrCache = Player.getPlayerCache();

	aChar.send("Jogadores - tamanho: " + plrCache.getSize() + "/" + plrCache.getCapacity() + ", estatística: " + plrCache.getHits() + "/" + plrCache.getMisses());

	Enchainer.list(aChar, plrCache.elements(), new NameIdExtractor(), ", ");

    }
}
