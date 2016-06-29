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

// 	$Id: CloneAlgo.java,v 1.1.1.1 2001/11/14 09:01:49 fmatheus Exp $

package jmud;

import java.util.NoSuchElementException;
import java.util.Enumeration;

import jmud.util.Separators;
import jmud.util.log.Log;
import jmud.jgp.VisibilityVerifier;
import jgp.interfaces.Iterator;
import jgp.predicate.UnaryPredicate;
import jgp.predicate.BinaryPredicate;
import jgp.functor.UnaryFunction;
import jgp.container.List;

public class CloneAlgo {

    public static final String LEFT_MARK = "(";
    public static final String RIGHT_MARK = ") ";

  public static void insert(List store, Object clo, BinaryPredicate isClone) {
    if (store.isEmpty()) {
      store.insert(clo);
      return;
    }

    Iterator iter = store.getIterator();

    Object curr = iter.getCurrent();

    if (isClone.execute(clo, curr)) {
	store.insert(clo);
	return;
    }

    try {
      for ( ; ; iter.next()) {
	Object next = iter.getNext();
	if (isClone.execute(clo, next)) {
	    iter.insertNext(clo);
	    return;
	}
      }
    }
    catch(NoSuchElementException e) {
      store.insert(clo);
    }
  }

    public static String getList(List store, Char looker, BinaryPredicate isClone, UnaryFunction selectField) {
	return getList(store, looker, isClone, selectField, Separators.NL);
    }

  public static String getList(List store, Char looker, BinaryPredicate isClone, UnaryFunction selectField, String sep) {
    String cloneList = "";

    if (store.isEmpty())
      return cloneList;

    UnaryPredicate canSee = new VisibilityVerifier(looker);

    int count = 1;

    Enumeration cloneEnum = store.elements();
    Object prevClone = cloneEnum.nextElement();

    if (prevClone == looker || !canSee.execute(prevClone))
      if (cloneEnum.hasMoreElements())
	prevClone = cloneEnum.nextElement();
      else
	return cloneList;

    while (cloneEnum.hasMoreElements()) {
      Object currClone = cloneEnum.nextElement();

      /*
      if (prevClone instanceof Char)
	  Log.debug(looker.getName() + ":" + ((Char) prevClone).getName() + "=" + canSee.execute(prevClone));
      */

      if (currClone == looker || !canSee.execute(currClone))
	continue;
      else if (isClone.execute(currClone, prevClone))
	++count;
      else {
	cloneList += (count == 1) ?
	  sep + ((String) selectField.execute(prevClone)) :
	  sep + LEFT_MARK + String.valueOf(count) + RIGHT_MARK +
	  ((String) selectField.execute(prevClone));
	prevClone = currClone;
	count = 1;
      }
    }

    if (prevClone != looker && canSee.execute(prevClone))
	cloneList += (count == 1) ?
	    sep + ((String) selectField.execute(prevClone)) :
	sep + LEFT_MARK + String.valueOf(count) + RIGHT_MARK +
	    ((String) selectField.execute(prevClone));

    return cloneList;
  }


    public static String getHeadedList(List store, Char looker, BinaryPredicate isClone, UnaryFunction selectField) {
	return getHeadedList(store, looker, isClone, selectField, Separators.NL);
    }

  public static String getHeadedList(List store, Char looker, BinaryPredicate isClone, UnaryFunction selectField, String sep) {
    String cloneList = "";

    if (store.isEmpty())
      return cloneList;

    UnaryPredicate canSee = new VisibilityVerifier(looker);

    int count = 1;
    int lines = 0;

    Enumeration cloneEnum = store.elements();
    Object prevClone = cloneEnum.nextElement();

    if (prevClone == looker || !canSee.execute(prevClone))
      if (cloneEnum.hasMoreElements())
	prevClone = cloneEnum.nextElement();
      else
	return cloneList;

    if (cloneEnum.hasMoreElements()) {
      Object currClone = cloneEnum.nextElement();
      if (currClone != looker || canSee.execute(currClone)) {
	  if (isClone.execute(currClone, prevClone))
	      ++count;
	  else {
	      cloneList += (count == 1) ?
		  ((String) selectField.execute(prevClone)) :
		  LEFT_MARK + String.valueOf(count) + RIGHT_MARK +
		  ((String) selectField.execute(prevClone));
	      prevClone = currClone;
	      count = 1;
	      ++lines;
	  }
      }
    }

    while (cloneEnum.hasMoreElements()) {
      Object currClone = cloneEnum.nextElement();
      if (currClone == looker || !canSee.execute(currClone))
	continue;
      else if (isClone.execute(currClone, prevClone))
	++count;
      else {
	cloneList += (count == 1) ?
	  sep + ((String) selectField.execute(prevClone)) :
	  sep + LEFT_MARK + String.valueOf(count) + RIGHT_MARK +
	  ((String) selectField.execute(prevClone));
	prevClone = currClone;
	count = 1;
	++lines;
      }
    }


    if (prevClone != looker && canSee.execute(prevClone))
	if (lines == 0)
	    cloneList += (count == 1) ?
		((String) selectField.execute(prevClone)) :
	LEFT_MARK + String.valueOf(count) + RIGHT_MARK +
		((String) selectField.execute(prevClone));
	else
	    cloneList += (count == 1) ?
		sep + ((String) selectField.execute(prevClone)) :
	sep + LEFT_MARK + String.valueOf(count) + RIGHT_MARK +
		((String) selectField.execute(prevClone));

    return cloneList;
  }

}





