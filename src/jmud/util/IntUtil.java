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

// 	$Id: IntUtil.java,v 1.1.1.1 2001/11/14 09:01:53 fmatheus Exp $

package jmud.util;

public class IntUtil {

    static public int get3of4(int n) {
	return (n >> 1) + get1of4(n);
    }

    static public int get1of4(int n) {
	return n >> 2;
    }

    static public int max(int a, int b) {
	return a > b ? a : b;
    }

    static public int min(int a, int b) {
	return a < b ? a : b;
    }

    static public int range(int v, int min, int max) {
	return min(max(v, min), max);
    }
}
