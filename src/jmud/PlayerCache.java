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

package jmud;

import jmud.Player;
import jmud.util.log.Log;
import jmud.jgp.IdMatcher;
import jgp.container.LRU;
import jgp.predicate.UnaryPredicate;
import jgp.functor.UnaryFunction;

public class PlayerCache extends LRU /* implements Storage */ {

    private int hits   = 0;
    private int misses = 0;

    PlayerCache(int cap) {
	super(cap);
    }

    public int getHits() {
	return hits;
    }

    public int getMisses() {
	return misses;
    }

    synchronized /* hits, misses */
    Player lookup(UnaryPredicate detector) {
	Player plr = (Player) search(detector, new PlayerLoader(detector));

	if (lastSearchHit())
	    ++hits;
	else
	    ++misses;

	if (plr == null)
	    Log.debug("PlayerCache: " + (lastSearchHit() ? "HIT" : "MISS"));
	else
	    Log.debug("PlayerCache: " + (lastSearchHit() ? "HIT" : "MISS") + ", carregado: " + plr.getName() + " [" + plr.getId() + "]");

	return plr;
    }

    void update(UnaryPredicate detector, Player plr) {
	super.update(detector, plr);
    }

}

class PlayerLoader implements UnaryFunction {

    private UnaryPredicate detector;

    PlayerLoader(UnaryPredicate det) {
	detector = det;
    }

    public Object execute(Object obj) {
	return Player.rawLoad(((IdMatcher) detector).getId());
    }
}

