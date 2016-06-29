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

class Damage {
  static final int SLASH      = 0;
  static final int PIERCE     = 1;
  static final int BLUDGEON   = 2;
  static final int BLAST      = 3;
  static final int HEAT       = 4;
  static final int COLD       = 5;
  static final int ELETRICITY = 6;
  static final int POISON     = 7;
  static final int OTHER      = 8;

    static final String labels[] = {
	"corte",
	"perfuração",
	"pancada",
	"calor",
	"frio",
	"eletricidade",
	"veneno",
	"outro"
    };
}
