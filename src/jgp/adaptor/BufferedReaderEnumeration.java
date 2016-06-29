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

package jgp.adaptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Enumeration;

public class BufferedReaderEnumeration implements Enumeration {

    private BufferedReader br;
    private String         lastLine;

    public BufferedReaderEnumeration(BufferedReader reader)
	throws IOException {
	br       = reader;
	lastLine = br.readLine();
    }

    public boolean hasMoreElements() {
	return lastLine != null;
    }

    public Object nextElement()
	throws NoSuchElementException {
	if (!hasMoreElements())
	    throw new NoSuchElementException();

	String tmp = lastLine;
	try {
	    lastLine = br.readLine();
	}
	catch (IOException e) {
	    throw new NoSuchElementException(e.getMessage());
	}
	return tmp;
    }
}
