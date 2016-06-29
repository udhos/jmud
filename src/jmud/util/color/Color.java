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

// 	$Id: Color.java,v 1.1.1.1 2001/11/14 09:01:53 fmatheus Exp $

package jmud.util.color;

import jmud.util.bit.Bit;

public class Color {

    // VT100 colors

    public static final int BIT_DEFAULT        = Bit.BIT0;
    public static final int BIT_BOLD           = Bit.BIT1;
    public static final int BIT_UNDERLINED     = Bit.BIT2;
    public static final int BIT_BLINK          = Bit.BIT3;
    public static final int BIT_INVERSE        = Bit.BIT4;
    public static final int BIT_INVISIBLE      = Bit.BIT5;
    public static final int BIT_NORMAL         = Bit.BIT6;
    public static final int BIT_NOT_UNDERLINED = Bit.BIT7;
    public static final int BIT_STEADY         = Bit.BIT8;
    public static final int BIT_POSITIVE       = Bit.BIT9;
    public static final int BIT_VISIBLE        = Bit.BIT10;
    public static final int BIT_FG_BLACK       = Bit.BIT11;
    public static final int BIT_FG_RED         = Bit.BIT12;
    public static final int BIT_FG_GREEN       = Bit.BIT13;
    public static final int BIT_FG_YELLOW      = Bit.BIT14;
    public static final int BIT_FG_BLUE        = Bit.BIT15;
    public static final int BIT_FG_MAGENTA     = Bit.BIT16;
    public static final int BIT_FG_CYAN        = Bit.BIT17;
    public static final int BIT_FG_WHITE       = Bit.BIT18;
    public static final int BIT_FG_DEFAULT     = Bit.BIT19;
    public static final int BIT_BG_BLACK       = Bit.BIT20;
    public static final int BIT_BG_RED         = Bit.BIT21;
    public static final int BIT_BG_GREEN       = Bit.BIT22;
    public static final int BIT_BG_YELLOW      = Bit.BIT23;
    public static final int BIT_BG_BLUE        = Bit.BIT24;
    public static final int BIT_BG_MAGENTA     = Bit.BIT25;
    public static final int BIT_BG_CYAN        = Bit.BIT26;
    public static final int BIT_BG_WHITE       = Bit.BIT27;
    public static final int BIT_BG_DEFAULT     = Bit.BIT28;

    public static final int DEFAULT        = 0;
    public static final int BOLD           = 1;
    public static final int UNDERLINED     = 2;
    public static final int BLINK          = 3;
    public static final int INVERSE        = 4;
    public static final int INVISIBLE      = 5;
    public static final int NORMAL         = 6;
    public static final int NOT_UNDERLINED = 7;
    public static final int STEADY         = 8;
    public static final int POSITIVE       = 9;
    public static final int VISIBLE        = 10;
    public static final int FG_BLACK       = 11;
    public static final int FG_RED         = 12;
    public static final int FG_GREEN       = 13;
    public static final int FG_YELLOW      = 14;
    public static final int FG_BLUE        = 15;
    public static final int FG_MAGENTA     = 16;
    public static final int FG_CYAN        = 17;
    public static final int FG_WHITE       = 18;
    public static final int FG_DEFAULT     = 19;
    public static final int BG_BLACK       = 20;
    public static final int BG_RED         = 21;
    public static final int BG_GREEN       = 22;
    public static final int BG_YELLOW      = 23;
    public static final int BG_BLUE        = 24;
    public static final int BG_MAGENTA     = 25;
    public static final int BG_CYAN        = 26;
    public static final int BG_WHITE       = 27;
    public static final int BG_DEFAULT     = 28;

    public static final String vt100[] = {
	"0",   // 00  normal (default)
	"1",   // 01  bold
	"4",   // 02  underlined
	"5",   // 03  blink (appears as bold)
	"7",   // 04  inverse
	"8",   // 05  invisible (hidden)
	"22",  // 06  normal (neither bold nor faint)
	"24",  // 07  not underlined
	"25",  // 08  steady (not blinking)
	"27",  // 09  positive (not inverse)
	"28",  // 10  visible (not hidden)
	"30",  // 11  FG to black
	"31",  // 12  FG to red
	"32",  // 13  FG to green
	"33",  // 14  FG to yellow
	"34",  // 15  FG to blue
	"35",  // 16  FG to magenta
	"36",  // 17  FG to cyan
	"37",  // 18  FG to white
	"39",  // 19  FG to default (original)
	"40",  // 20  BG to black
	"41",  // 21  BG to red
	"42",  // 22  BG to green
	"43",  // 23  BG to yellow
	"44",  // 24  BG to blue
	"45",  // 25  BG to magenta
	"46",  // 26  BG to cyan
	"47",  // 27  BG to white
	"49"   // 28  BG to default (original)
    };

    public static final String WHITE_ON_BLACK = "\033[37;40;0m";
    public static final String BLACK_ON_WHITE = "\033[30;47;0m";
    public static final String FORCE_NORMAL   = WHITE_ON_BLACK;

    // XTERM colors

    public static final int XT_BLACK   = 0;
    public static final int XT_RED     = 1;
    public static final int XT_GREEN   = 2;
    public static final int XT_YELLOW  = 3;
    public static final int XT_BLUE    = 4;
    public static final int XT_MAGENTA = 5;
    public static final int XT_CYAN    = 6;
    public static final int XT_WHITE   = 7;

    public static final String xterm[] = {
	"\033[200m",  // 00  FG to black
	"\033[201m",  // 01  FG to red
	"\033[202m",  // 02  FG to green
	"\033[203m",  // 03  FG to yellow
	"\033[204m",  // 04  FG to blue
	"\033[205m",  // 05  FG to magenta
	"\033[206m",  // 06  FG to cyan
	"\033[207m",  // 07  FG to white
	"\033[208m",  // 08  FG to ?
	"\033[209m",  // 09  FG to ?
	"\033[210m",  // 10  FG to ?
	"\033[211m",  // 11  FG to ?
	"\033[212m",  // 12  FG to ?
	"\033[213m",  // 13  FG to ?
	"\033[214m",  // 14  FG to ?
	"\033[215m"   // 15  FG to ?
    };


    public static String getVT100(int color) {
	return "\033[" + vt100[color] + "m";
    }

    public static String buildVT100(int colorVector) {
	String str = "\033[";
	if (Bit.isSet(colorVector, 1))
	    str += vt100[0];
	int mask = 2;
	for (int i = 1; i < vt100.length; ++i) {
	    if (Bit.isSet(colorVector, mask))
		str += ";" + vt100[i];
	    mask <<= 1;
	}
	return str + "m";
    }

    public static String getXterm(int color) {
	return xterm[color];
    }

    public static String getNormal() {
	return FORCE_NORMAL;
    }
}
