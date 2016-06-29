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

// 	$Id: Finder.java,v 1.1.1.1 2001/11/14 09:01:57 fmatheus Exp $

package jgp.adaptor;

import java.util.Enumeration;

import jgp.algorithm.Transformer;
import jgp.functor.UnaryFunction;
import jgp.container.Vector;
import jgp.predicate.UnaryPredicate;
import jgp.interfaces.Enumerable;

public class Finder {

    // encontra todas as ocorrências
    public static Vector findAll(Enumeration e, UnaryPredicate cmp) {
	FinderAdd addFunc = new FinderAdd();
	Transformer.applyIf(e, addFunc, cmp);
	return addFunc.getSet();
    }

    // encontra todas as ocorrências
    public static Vector findAll(Enumerable cont, UnaryPredicate cmp) {
	return findAll(cont.elements(), cmp);
    }

}

class FinderAdd implements UnaryFunction {

    private Vector set = new Vector();

    Vector getSet() {
	return set;
    }

    public Object execute(Object obj) {
	set.insert(obj);
	return null;
    }
}
