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

package jmud.jgp;

import jgp.functor.UnaryFunction;

import jmud.Char;
import jmud.Item;

public class MultipleItemsHandler implements UnaryFunction {

    private UnaryFunction toDo  = null; // função a ser executada em it1 - efeito "físico"
    private String        act   = null; // cadeia de caracteres podendo conter os seguintes símbolos:
    private Char          ch    = null; // $p - personagem agente da ação - ch
    private Char          vict  = null; // $P - personagem vítima da ação - vict
    //      Object        it1              $i - item primário             - it1
    private Item          it2   = null; // $I - item secundário           - it2
    private boolean       hide  = true; // esconder ação se ela não puder ser enxergada
    private String        noAct = null; // cadeia usada no caso da função retorna null, falha

    // 1 Char & 1 Item
    public MultipleItemsHandler(String act, boolean hide, Char ch, UnaryFunction toDo, String noAct) {
	this.act    = act;
	this.hide   = hide;
	this.ch     = ch;
	this.toDo   = toDo;
	this.noAct  = noAct;
    }

    // 2 Chars & 1 Item
    public MultipleItemsHandler(String act, boolean hide, Char ch, Char vict,  UnaryFunction toDo, String noAct) {
	this.act    = act;
	this.hide   = hide;
	this.ch     = ch;
	this.vict   = vict;
	this.toDo   = toDo;
	this.noAct  = noAct;
    }

    // 1 Char & 2 Items
    public MultipleItemsHandler(String act, boolean hide, Char ch, Item it2,  UnaryFunction toDo, String noAct) {
	this.act    = act;
	this.hide   = hide;
	this.ch     = ch;
	this.it2    = it2;
	this.toDo   = toDo;
	this.noAct  = noAct;
    }

    public Object execute(Object it1) {
	if (toDo.execute(it1) != null)
	    ch.getPlace().action(act, hide, ch, vict, (Item) it1, it2);
	else
	    ch.action(noAct, hide, ch, vict, (Item) it1, it2);
	return null;
    }
}
