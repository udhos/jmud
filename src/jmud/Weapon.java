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

import java.io.BufferedWriter;
import java.io.IOException;

import jmud.util.Separators;
import jmud.util.StrUtil;

class Weapon extends Item {

  private int physical;
  private int mental;
  private int mystic;

  private int damageType;

  Weapon(WeaponProto w) {
    super(w);

    physical   = w.physical;
    mental     = w.mental;
    mystic     = w.mystic;

    damageType = w.damageType;
  }

  protected void finalize() {
    super.finalize();
  }

  int getType() {
    return T_WEAPON;
  }

    /////////////
  // Sheetable:

  public String getSheet() {
    return super.getSheet() + Separators.NL +
      "Dano: F�sico [" + physical + "]  Mental [" + mental + "]  M�stico [" + mystic + "]" + Separators.NL + "Tipo: " + StrUtil.listFlags(damageType, Damage.labels);
  }

    //
    /////////////

    void save(BufferedWriter writer) throws IOException {
	super.save(writer);
	writer.write(physical + " " + mental + " " + mystic + " " + StrUtil.makeFlags(damageType)); writer.newLine();
    }

    void affectEquipper(Char ch) {
	ch.addToDamagePool(Char.C_PHY, physical);
	ch.addToDamagePool(Char.C_MEN, mental);
	ch.addToDamagePool(Char.C_MYS, mystic);
    }

    void unnaffectEquipper(Char ch) {
	ch.addToDamagePool(Char.C_PHY, -physical);
	ch.addToDamagePool(Char.C_MEN, -mental);
	ch.addToDamagePool(Char.C_MYS, -mystic);
    }

}
