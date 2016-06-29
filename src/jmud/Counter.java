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

// 	$Id: Counter.java,v 1.1.1.1 2001/11/14 09:01:51 fmatheus Exp $

class Counter {

    private int value;

    Counter() {
	set(0);
    }

    Counter(int v) {
	set(v);
    }

    void set(int v) {
	value = v;
    }

    int getValue() {
	return value;
    }

    synchronized void inc(int increment) {
	value += increment;
    }

    synchronized void inc() {
	++value;
    }

    synchronized void dec(int decrement) {
	value -= decrement;
    }

    synchronized void dec() {
	--value;
    }

    synchronized boolean decIfGreaterThan(int low) {
	if (value > low) {
	    --value;
	    return true;
	}
	return false;
    }

    synchronized boolean incIfLessThan(int high) {
	if (value < high) {
	    ++value;
	    return true;
	}
	return false;
    }
}
