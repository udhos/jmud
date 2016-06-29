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

// 	$Id: Bit.java,v 1.1.1.1 2001/11/14 09:01:53 fmatheus Exp $

package jmud.util.bit;

public class Bit {
  static public final int BIT0  = 1 << 0;
  static public final int BIT1  = 1 << 1;
  static public final int BIT2  = 1 << 2;
  static public final int BIT3  = 1 << 3;
  static public final int BIT4  = 1 << 4;
  static public final int BIT5  = 1 << 5;
  static public final int BIT6  = 1 << 6;
  static public final int BIT7  = 1 << 7;
  static public final int BIT8  = 1 << 8;
  static public final int BIT9  = 1 << 9;
  static public final int BIT10 = 1 << 10;
  static public final int BIT11 = 1 << 11;
  static public final int BIT12 = 1 << 12;
  static public final int BIT13 = 1 << 13;
  static public final int BIT14 = 1 << 14;
  static public final int BIT15 = 1 << 15;
  static public final int BIT16 = 1 << 16;
  static public final int BIT17 = 1 << 17;
  static public final int BIT18 = 1 << 18;
  static public final int BIT19 = 1 << 19;
  static public final int BIT20 = 1 << 20;
  static public final int BIT21 = 1 << 21;
  static public final int BIT22 = 1 << 22;
  static public final int BIT23 = 1 << 23;
  static public final int BIT24 = 1 << 24;
  static public final int BIT25 = 1 << 25;
  static public final int BIT26 = 1 << 26;
  static public final int BIT27 = 1 << 27;
  static public final int BIT28 = 1 << 28;
  static public final int BIT29 = 1 << 29;
  static public final int BIT30 = 1 << 30;
  static public final int BIT31 = 1 << 31;
    static public final int BIT_ALL = -1;

  static public boolean isSet(int vector, int mask) {
    return (vector & mask) != 0;
  }

  static public int set(int vector, int mask) {
    return vector | mask;
  }

  static public int reset(int vector, int mask) {
    return vector & ~mask;
  }

  static public int toggle(int vector, int mask) {
    return vector ^ mask;
  }
}
