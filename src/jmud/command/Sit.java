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

package jmud.command;

import jmud.Char;

import jmud.util.StrUtil;

class Sit extends Command {

    Sit(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (aChar.isFighting()) {
	    aChar.send("Voc� j� est� ocupado defendendo sua vida.");
	    return;
	}
	if (aChar.getPosition() == Char.P_SITTING)
	    aChar.send("Voc� j� est� " + StrUtil.adjustSex("sentad*.", aChar.isFemale()));
	else {
	    aChar.setPosition(Char.P_SITTING);
	    aChar.getPlace().action("$p sentou.", true, aChar);
	}
    }
}
