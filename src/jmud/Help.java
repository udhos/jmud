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

import java.util.Enumeration;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;

import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.Separators;

class Help {

  private Vector theHelp = null;

  Help(String helpFileName)
    throws InvalidFileFormatException, FileNotFoundException, IOException {
      theHelp = new Vector();
      LineReader helpFile = null;

      helpFile = new LineReader(new FileReader(helpFileName));

      for ( ; ; ) {
	if (helpFile.eos())
	  throw new InvalidFileFormatException();
	String helpText = helpFile.readLine();

	if (helpText.startsWith(Separators.EOF))
	  break;

	if (!helpText.startsWith(Separators.BOR))
	  throw new InvalidFileFormatException();
	helpText = helpText.substring(1);
	for ( ; ; ) {
	  if (helpFile.eos())
	    throw new InvalidFileFormatException();
	  String helpLine = helpFile.readLine();

	  if (helpLine.startsWith(Separators.BOR))
	    throw new InvalidFileFormatException();
	  if (helpLine.startsWith(Separators.EOR))
	    break;

	  helpText += Separators.NL + helpLine;
	}
	theHelp.addElement(helpText);
      }

      helpFile.close();
  }

    String findHelpByKey(String keyWord) {

      String helpText = "";
      for (Enumeration help = theHelp.elements(); help.hasMoreElements(); ) {
	String currHelp = (String) help.nextElement();
	StringTokenizer helpIndex = new StringTokenizer(new StringTokenizer(currHelp, Separators.NL).nextToken());
	for ( ; ; ) {
	  if (helpIndex.hasMoreTokens()) {
	    if (helpIndex.nextToken().toLowerCase().startsWith(keyWord.toLowerCase())) {
	      helpText += ((helpText == "") ? currHelp : Separators.NL +
	      "----------------------------------------------------------------------" +
	      Separators.NL + currHelp);
	      break;
	    }
	  }
	  else
	      break;
	}
      }
      if (helpText == "")
	  return null;
      else
	  return helpText;
  }
}
