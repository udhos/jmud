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

import java.util.Enumeration;

import jgp.algorithm.Searcher;
import jgp.algorithm.Transformer;
import jgp.functor.UnaryFunction;
import jgp.predicate.UnaryPredicate;

import jmud.Recipient;

public class Enchainer {

    public static void wrapNone(String none, Recipient rcpt, int count) {
	if (count == 0)
	    rcpt.snd(none);
    }

    public static int list(Recipient rcpt, Enumeration e,
			    UnaryFunction extractor, String separator) {

	SeparatorSender sndr = new SeparatorSender(rcpt, extractor, separator);

	return Transformer.countApplyToAll(e, sndr);
    }

    public static void list(String none,
			   Recipient rcpt, Enumeration e,
			   UnaryFunction extractor, String separator) {
	wrapNone(none, rcpt, list(rcpt, e, extractor, separator));
    }

    public static int headedList(Recipient rcpt, Enumeration e,
				  UnaryFunction extractor, String separator) {

	if (!e.hasMoreElements())
	    return 0;

	Object head = e.nextElement();

	SeparatorSender sndr = new SeparatorSender(rcpt, extractor, separator);

	sndr.send(head);

	return Transformer.countApplyToAll(e, sndr) + 1;
    }

    public static void headedList(String none,
				 Recipient rcpt, Enumeration e,
				 UnaryFunction extractor, String separator) {

	wrapNone(none, rcpt, headedList(rcpt, e, extractor, separator));
    }

    public static int listIf(Recipient rcpt, Enumeration e,
			     UnaryFunction extractor, String separator,
			     UnaryPredicate condition) {

	SeparatorSender sndr = new SeparatorSender(rcpt, extractor, separator);

	return Transformer.countApplyIf(e, sndr, condition);
    }

    public static int headedListIf(Recipient rcpt, Enumeration e,
				    UnaryFunction extractor, String separator,
				    UnaryPredicate condition) {

	Object head = Searcher.linearSearch(e, condition);
	if (head == null)
	    return 0;

	SeparatorSender sndr = new SeparatorSender(rcpt, extractor, separator);

	sndr.send(head);

	return Transformer.countApplyIf(e, sndr, condition) + 1;
    }

}

class SeparatorSender implements UnaryFunction {

    protected Recipient     theRecipient = null;
    protected UnaryFunction extractor    = null;
    protected String        separator    = null;

    SeparatorSender(Recipient rcpt, UnaryFunction extrac, String sep) {
	theRecipient = rcpt;
	extractor    = extrac;
	separator    = sep;
    }

    void send(Object obj) {
	theRecipient.snd(extractor.execute(obj));
    }

    public Object execute(Object obj) {
	theRecipient.snd(separator);
	send(obj);
	return null;
    }
}

