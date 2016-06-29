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

package jmud.magic;

import jmud.Char;
import jmud.Named;
import jmud.World;
import jmud.Manager;
import jmud.command.CommandTokenizer;

public abstract class Magic implements Named {

  static protected Manager theManager = null;
  static protected World   theWorld   = null;

  public static void setManager(Manager aManager) {
    theManager = aManager;
  }

  public static void setWorld(World aWorld) {
    theWorld = aWorld;
  }

  protected String theName;

  Magic(String name) {
    theName = name;
  }

    public String getName() {
	return theName;
    }

    public boolean hasName(String name) {
	return getName().startsWith(name);
    }

    void spellEcho(Char ch, Char vict) {
	ch.getPlace().action("$p lança uma magia em $P.", true, ch, vict);
    }

    public abstract void spell(Char ch, CommandTokenizer toker);
}
