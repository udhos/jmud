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

// 	$Id: StrPair.java,v 1.1.1.1 2001/11/14 09:01:53 fmatheus Exp $

package jmud.util.pair;

public class StrPair {

    private String str1;
    private String str2;

    public StrPair() {
	str1 = null;
	str2 = null;
    }

    public StrPair(String s1, String s2) {
	str1 = s1;
	str2 = s2;
    }

    public String getStr1() {
	return str1;
    }

    public String getStr2() {
	return str2;
    }

    public void setStr1(String s1) {
	str1 = s1;
    }

    public void setStr2(String s2) {
	str2 = s2;
    }
}
