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

// 	$Id: Searcher.java,v 1.1.1.1 2001/11/14 09:01:57 fmatheus Exp $

package jgp.algorithm;

import java.util.Enumeration;

import jgp.predicate.UnaryPredicate;
import jgp.predicate.UnaryFalse;
import jgp.predicate.BinaryPredicate;
import jgp.functor.UnaryComparator;
import jgp.functor.UnaryFunction;
import jgp.interfaces.Enumerable;
import jgp.interfaces.BackEnumerable;
import jgp.interfaces.RandomAccessable;
import jgp.interfaces.Transversable;
import jgp.container.Vector;
import jgp.container.BFSTree;

public class Searcher {

    // Encontra a ind-ésima ocorrência.
    // Retorna ponteiro para a ocorrência.
    public static Object linearSearch(Enumeration enum, UnaryPredicate cmp, int ind) {
	while (enum.hasMoreElements()) {
	    Object curr = enum.nextElement();
	    if (cmp.execute(curr) && --ind == 0)
		return curr;
	}
	return null;
    }

    // Encontra a primeira ocorrência.
    // Retorna ponteiro para a ocorrência.
    public static Object linearSearch(Enumeration enum, UnaryPredicate cmp) {
	return linearSearch(enum, cmp, 1);
    }

    // Encontra a ind-ésima ocorrência.
    // Retorna ponteiro para a ocorrência.
    public static Object linearSearch(Enumerable cont, UnaryPredicate cmp, int ind) {
	return linearSearch(cont.elements(), cmp, ind);
    }

    // Encontra a primeira ocorrência.
    // Retorna ponteiro para a ocorrência.
    public static Object linearSearch(Enumerable cont, UnaryPredicate cmp) {
	return linearSearch(cont, cmp, 1);
    }

    // Encontra a ind-ésima ocorrência.
    // Retorna ponteiro para a ocorrência.
    public static Object backLinearSearch(Enumeration enum, UnaryPredicate cmp, int ind) {
	while (enum.hasMoreElements()) {
	    Object curr = enum.nextElement();
	    if (cmp.execute(curr) && --ind == 0)
		return curr;
	}
	return null;
    }

    // Encontra a primeira ocorrência.
    // Retorna ponteiro para a ocorrência.
    public static Object backLinearSearch(Enumeration enum, UnaryPredicate cmp) {
	return backLinearSearch(enum, cmp, 1);
    }

    // Encontra a ind-ésima ocorrência.
    // Retorna ponteiro para a ocorrência.
    public static Object backLinearSearch(BackEnumerable cont, UnaryPredicate cmp,
					  int ind) {
	return backLinearSearch(cont.backElements(), cmp, ind);
    }

    // Encontra a primeira ocorrência.
    // Retorna ponteiro para a ocorrência.
    public static Object backLinearSearch(BackEnumerable cont, UnaryPredicate cmp) {
	return backLinearSearch(cont, cmp, 1);
    }

    // Encontra uma ocorência, que deve ser única.
    // Retorna o índice da ocorrência,
    // ou -1 em caso de não encontrar.
    public static int binarySearch(RandomAccessable v, UnaryComparator cmp, int first, int last) {
	while (first <= last) {
	    int mid = (first + last) >> 1;
	    Object curr = v.getElementAt(mid);

	    switch(cmp.execute(curr)) {
	    case -1: first = mid + 1; break;
	    case 0: return mid;
	    case 1: last = mid - 1; break;
	    }
	}
	return -1;
    }

    public static int binarySearch(RandomAccessable v, UnaryComparator cmp) {
	return binarySearch(v, cmp, 0, v.getSize() - 1);
    }

    public static BFSTree breadthFirstSearch(Transversable root, int maxDepth) {
	return breadthFirstSearch(root, new UnaryFalse(), maxDepth, true);
    }

    public static BFSTree breadthFirstSearch(Transversable root, UnaryPredicate cmp, int maxDepth) {
	return breadthFirstSearch(root, cmp, maxDepth, false);
    }

    public static BFSTree breadthFirstSearch(Transversable root, UnaryPredicate cmp, int maxDepth, boolean returnOnFail) {
	if (maxDepth <= 0)
	    return null;

	BFSTree tree = new BFSTree(maxDepth, root);

	root.mark(root);
	if (cmp.execute(root))
	    return tree;

	for (int depth = 1; depth < maxDepth; ++depth) {

	    // e e' um nome significativo?

	    Enumeration e = tree.sublevel(depth - 1);
	    if (!e.hasMoreElements())
		break;
	    tree.newLevel();
	    while (e.hasMoreElements()) {
		Transversable trans = (Transversable) e.nextElement();

		// n tambem e' um bom nome?

		for (Enumeration n = trans.getNeighbors(); n.hasMoreElements(); ) {
		    Transversable curr = (Transversable) n.nextElement();
		    if (!curr.isMarked()) {
			tree.insert(curr);
			curr.mark(trans);
			if (cmp.execute(curr))
			    return tree;
		    }
		}
	    }
	}

	if (returnOnFail)
	    return tree;

	// Limpa a bagunça antes de falhar na busca
	for (Enumeration e = tree.elements(); e.hasMoreElements(); )
	    ((Transversable)e.nextElement()).unmark();

	return null;
    }

}


