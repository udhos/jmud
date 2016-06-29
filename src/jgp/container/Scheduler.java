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

package jgp.container;

import java.util.Date;

import jgp.interfaces.Event;
import jgp.interfaces.PriorityQueue;
import jgp.predicate.BinaryPredicate;

public class Scheduler {

    private BinaryPredicate after = new HappenAfter();
    private PriorityQueue   pool  = new Heap(after);

    public void schedule(Event ev) {
	pool.insert(ev);
    }

    public int treatEvents(Date now) {

	int count = 0;

	NowEvent nowEv = new NowEvent(now);

	while (!pool.isEmpty()) {
	    Event ev = (Event) pool.top();
	    if (after.execute(ev, nowEv))
		break;

	    pool.remove();
	    ev.execute(now);

	    ++count;
	}

	return count;
    }
}

class NowEvent implements Event {

    private Date date;

    NowEvent(Date dt) {
	date = dt;
    }

    public Date scheduledTime() {
	return date;
    }

    public void execute(Date bogus) {
	throw new NullPointerException(); // nao pode ser executado
    }
}

class HappenAfter implements BinaryPredicate {

    public boolean execute(Object obj1, Object obj2) {
	return ((Event) obj1).scheduledTime().after(((Event) obj2).scheduledTime());
    }
}
