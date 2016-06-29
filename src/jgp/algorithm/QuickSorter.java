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

// 	$Id: QuickSorter.java,v 1.1.1.1 2001/11/14 09:01:57 fmatheus Exp $

package jgp.algorithm;

import jgp.interfaces.RandomAccessable;
import jgp.predicate.BinaryPredicate;

public class QuickSorter {

    static final int THRESHOLD = 50;

    private RandomAccessable vec      = null;
    private BinaryPredicate  lessThan = null;
    private InsertionSorter  sorter   = null;

    public QuickSorter(RandomAccessable v, BinaryPredicate less) {
	vec      = v;
	lessThan = less;
	sorter   = new InsertionSorter(v, less);
    }

    public void sort(int first, int last) {

	if (Math.abs(last - first) < THRESHOLD) {
	    sorter.sort(first, last);
	    return;
	}

	int i = first;
	int j = last;
	// Object mid = vec.getElementAt(first);
	Object mid = vec.getElementAt((first + last) >> 1);

	do
	    {
		while (lessThan.execute(vec.getElementAt(i), mid))
		    ++i;
		while (lessThan.execute(mid, vec.getElementAt(j)))
		    --j;
		if (i <= j) {
		    Object tmp = vec.getElementAt(i);
		    vec.setElementAt(i, vec.getElementAt(j));
		    vec.setElementAt(j, tmp);
		    ++i;
		    --j;
		}
	    }
	while (i <= j);
	if (first < j)
	    sort(first, j);
	if (i < last)
	    sort(i, last);

    }

    public void sort() {
	int last = vec.getSize() - 1;
	if (0 < last)
	    sort(0, last);
    }
}
