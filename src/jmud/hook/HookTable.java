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

package jmud.hook;

import jmud.util.log.Log;

public class HookTable {

    static final Hook GUARDIAN_N  = new Blocker("guardiao_norte",     Blocker.D_NORTH);
    static final Hook GUARDIAN_S  = new Blocker("guardiao_sul",       Blocker.D_SOUTH);
    static final Hook GUARDIAN_NS = new Blocker("guardiao_norte_sul", Blocker.D_NORTH | Blocker.D_SOUTH);
    static final Hook GUARDIAN_U  = new Blocker("guardiao_cima",      Blocker.D_UP);

    static final Hook AGRESSIVE   = new Agressive("agressivo");

    // lista todos os ganchos
    static final Hook theHooks[] = {
	GUARDIAN_N,
	GUARDIAN_S,
	GUARDIAN_NS,
	GUARDIAN_U,
	AGRESSIVE
    };

    /*
    // ganchos que sao tratados como gatilhos
    // de execucao de comandos
    static final Hook TRIGGERS[] = {
	GUARDIAN_N,
	GUARDIAN_S,
	GUARDIAN_NS,
	GUARDIAN_U
    };
    */

    static Hook lookup(int ind) {
	return (ind >= 0 && ind < theHooks.length) ? theHooks[ind] : null;
    }

    public static Hook lookup(String str) {
	int len = theHooks.length;
	for (int i = 0; i < len; ++i) {
	    Hook hk = theHooks[i];
	    if (str.equals(hk.getLabel()))
		return hk;
	}
	return null;
    }

    public static void verify() {
	Log.info("Verificando ganchos");

	int len = theHooks.length;
	for (int i = 0; i < len; ) {
	    Hook hk = theHooks[i];
	    String lbl = hk.getLabel();
	    for (int j = ++i; j < len; ++j) {
		Hook hk2 = theHooks[j];
		String lbl2 = hk2.getLabel();
		if (lbl2.equals(lbl))
		    Log.err("Rótulos de gancho duplicados: '" + lbl + "' (" + i + " e " + j + ")");
	    }
	}
    }
}
