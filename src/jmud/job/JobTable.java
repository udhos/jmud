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

package jmud.job;

import jmud.Char;
import jmud.jgp.Enchainer;
import jmud.jgp.NameMatcher;
import jmud.jgp.NameExtractor;
import jgp.adaptor.Enumerator;
import jgp.algorithm.Searcher;

public class JobTable {

    static final Job jobList[] = {
	new Job("guerreiro"),
	new Job("mago"),
	new Job("monge")
    };

    public static Job findJobByName(String name) {
	return (Job) Searcher.linearSearch(new Enumerator(jobList), new NameMatcher(name));
    }

    public static void sendJobList(Char rcpt, String sep) {
	Enchainer.headedList("Nenhuma", rcpt, new Enumerator(jobList), new NameExtractor(), sep);
    }

}
