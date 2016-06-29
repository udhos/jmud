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

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

import jgp.container.Vector;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.log.Log;
import jmud.util.Separators;
import jmud.util.pair.IntPair;
import jmud.util.StrUtil;


class KeyProto extends ItemProto {

    Vector doorReferences = new Vector();

    // salva base da zona para permitir salvar os identificadores
    // de porta corretos (obtidos via subtracao deste valor)
    int savedZoneBase;

    KeyProto(int zoneBase, int itemId, LineReader itemFile)
	throws InvalidFileFormatException {

	super(zoneBase, itemId, itemFile);
	Log.debug("Chave: " + getName());

	savedZoneBase = zoneBase;

	try {
	    for (; ; ) {
		if (itemFile.eos())
		    throw new InvalidFileFormatException();
		String line = itemFile.readLine();
		if (line.startsWith(Separators.EOR))
		    break;
		StringTokenizer toker = new StringTokenizer(line);
		if (!toker.hasMoreTokens())
		    throw new InvalidFileFormatException();
		int room = Integer.parseInt(toker.nextToken());
		if (!toker.hasMoreTokens())
		    throw new InvalidFileFormatException();
		int dir  = Door.getDirectionByName(toker.nextToken());
		if (dir == -1)
		    throw new InvalidFileFormatException();
		doorReferences.insert(new IntPair(room + zoneBase, dir));
	    }
	    doorReferences.trim();
	}
	catch(IOException e) {
	    throw new InvalidFileFormatException();
	}
	catch(NumberFormatException e) {
	    throw new InvalidFileFormatException();
	}
    }

    protected void finalize() {
	super.finalize();
    }

    int getProtoType() {
	return Item.T_KEY;
    }

    public Item create() {
	return new Key(this);
    }

    /////////////
    // Sheetable:

    private String listDoors() {
	String list = "";
	for (Enumeration e = doorReferences.elements(); e.hasMoreElements(); ) {
	    IntPair pair = (IntPair) e.nextElement();
	    list += Separators.NL + (pair.getValue1() - savedZoneBase) + " " + Door.getDirectionName(pair.getValue2());
	}
	return list;
    }

    public String getSheet() {
	return super.getSheet() + Separators.NL + "Portas:" + StrUtil.wrapList(listDoors(), "Nenhuma");
    }

    //
    /////////////
}
