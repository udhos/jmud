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

// 	$Id: Transformer.java,v 1.1.1.1 2001/11/14 09:01:57 fmatheus Exp $

package jgp.algorithm;

import java.util.Enumeration;

import jgp.functor.UnaryFunction;
import jgp.predicate.UnaryPredicate;
import jgp.interfaces.Enumerable;

public class Transformer {

    public static void applyToAll(Enumeration e, UnaryFunction func) {
	while (e.hasMoreElements())
	    func.execute(e.nextElement());
    }

    public static int countApplyToAll(Enumeration e, UnaryFunction func) {
	int count = 0;

	while (e.hasMoreElements()) {
	    func.execute(e.nextElement());
	    ++count;
	}

	return count;
    }

    public static void applyToAll(Enumerable cont, UnaryFunction func) {
	applyToAll(cont.elements(), func);
    }

    public static void applyIf(Enumeration e, UnaryFunction func, UnaryPredicate cond) {
	while (e.hasMoreElements()) {
	    Object curr = e.nextElement();
	    if (cond.execute(curr))
		func.execute(curr);
	}
    }

    public static void applyIf(Enumerable cont, UnaryFunction func, UnaryPredicate cond) {
	applyIf(cont.elements(), func, cond);
    }

    public static int countApplyIf(Enumeration e, UnaryFunction func, UnaryPredicate cond) {
	int count = 0;

	while (e.hasMoreElements()) {
	    Object curr = e.nextElement();
	    if (cond.execute(curr)) {
		func.execute(curr);
		++count;
	    }
	}

	return count;
    }

}
