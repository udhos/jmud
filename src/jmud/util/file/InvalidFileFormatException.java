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

// 	$Id: InvalidFileFormatException.java,v 1.1.1.1 2001/11/14 09:01:53 fmatheus Exp $

package jmud.util.file;

public class InvalidFileFormatException extends Exception {

  private String theFileName   = null;
  private String theLocation   = null;  // where in the source code
  private String theReason     = null;  // why?
  private String theLine       = null;
  private int    theLineNumber = 0;

  static private final String UNKNOWN = "desconhecido(a)";

  public InvalidFileFormatException() {
    theFileName   = UNKNOWN;
    theLocation   = UNKNOWN;
    theReason     = UNKNOWN;
    theLine       = UNKNOWN;
  }

  public InvalidFileFormatException(String fileName, String location, String reason, String line, int lineNumber) {
    super("\r\n     arquivo: " + fileName +
	  "\r\n localização: " + location +
	  "\r\n      motivo: " + reason +
	  "\r\n       linha: " + line +
	  "\r\n   no. linha: " + lineNumber);
    theFileName   = fileName;
    theLocation   = location;
    theReason     = reason;
    theLine       = line;
    theLineNumber = lineNumber;
  }
}
