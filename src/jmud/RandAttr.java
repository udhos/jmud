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

import jmud.util.log.Log;
import jmud.util.StrUtil;

class RandAttr {

    static final int T_ALL  = 0;
    static final int T_ATTR = 1;
    static final int T_ROW  = 2;
    static final int T_COL  = 3;

    static final String targetNames[] = {
	"todos",
	"atributo",
        "linha",
	"coluna"
    };

    static final int M_CHANGE = 0;
    static final int M_ADD    = 1;
    static final int M_SUB    = 2;

    static final String modeNames[] = {
	"mudar",
	"somar",
	"subtrair"
    };

    private int target;
    private int which;
    private int value;
    private int mode;

    RandAttr(int t, int w, int v, int m) {
	target = t;
	which  = w;
	value  = v;
	mode   = m;
    }

    int getTarget() {
	return target;
    }

    int getWhich() {
	return which;
    }

    int getValue() {
	return value;
    }

    int getMode() {
	return mode;
    }

    String getSheet() {
	return StrUtil.rightPad(targetNames[target], 8) + " " +
	    StrUtil.formatNumber(which, 3) + " " +
	    StrUtil.rightPad(modeNames[mode], 8) + " " +
	    StrUtil.formatNumber(value, 3);
    }
}

