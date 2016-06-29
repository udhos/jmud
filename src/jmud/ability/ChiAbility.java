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

package jmud.ability;

import jmud.Char;

class ChiAbility extends Ability {

    ChiAbility (String name) {
	super(name);
    }

    public void execute(Char ch, Ability ab, int abLevel, String arg) {

	// Conhecimento de Chi
	if (abLevel < 1) {
	    ch.send("Falta conhecimento de Chi.");
	    return;
	}

	// Necessidade de cura
	int stam = ch.getStam();
	int body = ch.getAttr(Char.A_BOD);
	int need = body - stam;
	if (need < 1) {
	    ch.send("Sua saúde física já está adequada.");
	    return;
	}

	// Disponibilidade de vontade
	int will = ch.getWill();
	if (will < 1) {
	    ch.send("Falta vontade.");
	    return;
	}

	// Requisito de vontade
	int pts = need / abLevel;
	if (pts < 1)
	    pts = 1;

	// Nao pode requisitar mais do que o disponivel
	if (pts > will)
	    pts = will;

	// Cura nao pode ultrapassar maximo de vitalidade
	stam += pts * abLevel;
	if (stam > body)
	    stam = body;

	// Executa
	ch.setStam(stam);
	ch.setWill(will - pts);

	ch.send("Reestabelecendo saúde física.");
    }
}

