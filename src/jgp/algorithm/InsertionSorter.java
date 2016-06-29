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

// 	$Id: InsertionSorter.java,v 1.1.1.1 2001/11/14 09:01:57 fmatheus Exp $

package jgp.algorithm;

import jgp.interfaces.RandomAccessable;
import jgp.predicate.BinaryPredicate;

public class InsertionSorter {

    private RandomAccessable vec      = null;
    private BinaryPredicate  lessThan = null;

    public InsertionSorter(RandomAccessable v, BinaryPredicate less) {
	vec      = v;
	lessThan = less;
    }

    public void sort(int first, int last) {
	for (int i = first; i <= last; ++i) {
	    Object obj = vec.getElementAt(i);

	    int j;
	    for (j = first; j < i; ++j)
		if (lessThan.execute(obj, vec.getElementAt(j))) {

		    int from = i - 1;
		    int to   = i;
		    while (to > j)
			vec.setElementAt(to--, vec.getElementAt(from--));

		    break;
		}

	    vec.setElementAt(j, obj);
	}
    }

    public void sort() {
	sort(0, vec.getSize() - 1);
    }
}
