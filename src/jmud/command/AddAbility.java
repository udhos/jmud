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
import jmud.ability.Ability;
import jmud.ability.AbilityTable;
import jmud.ability.Proficiency;

class AddAbility extends Command {

    AddAbility(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	if (!toker.hasMoreTokens()) {
	    sendSyntax(aChar);
	    return;
	}
	IndexedToken chIndTok = toker.nextIndexedToken();

	if (!toker.hasMoreTokens()) {
	    sendSyntax(aChar);
	    return;
	}
	String abilityName = toker.nextQuotedToken();

	int level = 0;
	if (toker.hasMoreTokens()) {
	    try {
		level = Integer.parseInt(toker.nextToken());
	    }
	    catch(NumberFormatException e) {
		aChar.send("Nível inválido de habilidade.");
		return;
	    }
	}

	Char vict = aChar.getPlace().findCharByName(chIndTok, aChar);
	if (vict == null) {
	    aChar.send("Ninguém aqui tem esse nome.");
	    return;
	}

	if (aChar != vict && aChar.forbidAccess(vict))
	    return;

	Ability abl = AbilityTable.findAbilityByName(abilityName);
	if (abl == null) {
	    aChar.send("Habilidade não encontrada.");
	    return;
	}

	Proficiency oldProf = vict.findAbility(abl);
	if (oldProf != null) {
	    aChar.send("Habilidade já conhecida pelo alvo.");
	    return;
	}

	vict.gainAbility(abl, level);
	vict.send("Você aprende uma nova habilidade: '" + abl.getName() + "'!");
	aChar.send("Habilidade ensinada.");
    }
}



