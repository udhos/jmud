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

import java.util.Enumeration;

import jmud.Char;
import jmud.Room;
import jmud.util.Separators;
import jmud.command.IndexedToken;
import jmud.command.CommandTokenizer;
import jmud.jgp.Enchainer;
import jmud.jgp.NameMatcher;
import jmud.jgp.NameExtractor;
import jgp.algorithm.Searcher;
import jgp.adaptor.Enumerator;

public class MagicTable {

    static final Magic theMagics[] = {
	new Flash("brilho"),
	new Weakness("fraqueza"),
	new Strength("forca"),
	new Earthquake("terremoto")
    };

    public static Magic findMagicByName(String name) {
	return (Magic) Searcher.linearSearch(new Enumerator(theMagics), new NameMatcher(name));
    }

    public static void sendMagicList(Char rcpt) {
	Enchainer.headedList("Nenhuma", rcpt, new Enumerator(theMagics), new NameExtractor(), Separators.NL);
    }
}

class Flash extends Magic {

  Flash(String name) {
    super(name);
  }

    public void spell(Char ch, CommandTokenizer toker) {
	if (!toker.hasMoreTokens()) {
	    ch.send("Em quem você quer lançar ?");
	    return;
	}

	ch.getPlace().action("Intensa luminosidade emana brevemente de $p.", false, ch);
    }
}

class Weakness extends Magic {

    Weakness(String name) {
	super(name);
    }

    public void spell(Char ch, CommandTokenizer toker) {
	if (!toker.hasMoreTokens()) {
	    ch.send("Em quem você quer lançar ?");
	    return;
	}

	IndexedToken indTok = toker.nextIndexedToken();
	Char vict = ch.getPlace().findCharByName(indTok.getTarget(), indTok.getIndex(), ch);
	if (vict != null) {
	    super.spellEcho(ch, vict);
	    theManager.insertEffect(new StrEffect(2, vict, -1));
	}
	else
	    ch.send("Essa pessoa não está aqui.");
    }
}

class Strength extends Magic {

    Strength(String name) {
	super(name);
    }

    public void spell(Char ch, CommandTokenizer toker) {
	if (!toker.hasMoreTokens()) {
	    ch.send("Em quem você quer lançar ?");
	    return;
	}

	IndexedToken indTok = toker.nextIndexedToken();
	Char vict = ch.getPlace().findCharByName(indTok.getTarget(), indTok.getIndex(), ch);
	if (vict != null) {
	    super.spellEcho(ch, vict);
	    theManager.insertEffect(new StrEffect(2, vict, 1));
	}
	else
	    ch.send("Essa pessoa não está aqui.");
    }
}

class Earthquake extends Magic {

    Earthquake(String name) {
	super(name);
    }

    public void spell(Char ch, CommandTokenizer toker) {
        Room center = null;
        if (ch.getPlace() instanceof Room)
            center = (Room) ch.getPlace();
        else {
            ch.send("Essa magia não funciona nesse lugar.");
            return;
        }

	for (Enumeration e = Searcher.breadthFirstSearch(center, 3).elements(); e.hasMoreElements(); ) {
	    Room curr = (Room) e.nextElement();
	    curr.action("A terra treme sob seus pés.", true, ch);
	    curr.unmark();
	}
    }
}

