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

import jmud.Named;

public class NameDependenceExtractor implements UnaryFunction {

    private DependenceExtractor depExtractor;
    private String              separator;

    public NameDependenceExtractor(UnaryFunction extractor,
				   String sep1, String sep2) {
	depExtractor = new DependenceExtractor(extractor, sep2);
	separator    = sep1;
    }

    public Object execute(Object obj) {
	return ((Named) obj).getName() + separator + depExtractor.execute(obj);
    }
}
