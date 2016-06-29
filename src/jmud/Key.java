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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import jgp.container.Vector;
import jmud.util.Separators;
import jmud.util.pair.IntPair;
import jmud.util.StrUtil;

public class Key extends Item {

    // para poupar memoria, sera' o mesmo do prototipo...
    // entao, nao podera' ser modificado;
    // se modificacao for futuramente necessaria, as
    // chaves terao de copiar os vetores de referencia a portas dos prototipos
    // (uma outra solucao seria copiar apenas quando ha' alteracao)
    private Vector doorReferences = null;

    Key(KeyProto kp) {
	super(kp);

	doorReferences = kp.doorReferences;
    }

    protected void finalize() {
	super.finalize();
    }

    int getType() {
	return T_KEY;
    }

    // Pode esta chave abrir uma porta de uma dada sala?
    public boolean canUnlockDoor(Room rm, Door dr) {
	int  room      = rm.getId();
	int  direction = dr.getDirection();

	for (Enumeration e = doorReferences.elements(); e.hasMoreElements(); ) {
	    IntPair pair = (IntPair) e.nextElement();
	    if (pair.getValue1() == room && pair.getValue2() == direction)
		return true;
	}

	Room destRoom  = dr.getDestinationRoom();
	int  destId    = destRoom.getId();
	int  invDir    = Door.getInverseDir(direction);
	Door invDoor   = destRoom.findDoorByDir(invDir);

	if (invDoor != null)
	    if (invDoor.getDestinationRoom() == rm)
		for (Enumeration e = doorReferences.elements(); e.hasMoreElements(); ) {
		    IntPair pair = (IntPair) e.nextElement();
		    if (pair.getValue1() == destId && pair.getValue2() == invDir)
			return true;
		}

	return false;
    }

    /////////////
    // Sheetable:

    private String listDoors() {
	String list = "";
	for (Enumeration e = doorReferences.elements(); e.hasMoreElements(); ) {
	    IntPair pair = (IntPair) e.nextElement();
	    list += Separators.NL + pair.getValue1() + " " + Door.getDirectionName(pair.getValue2());
	}
	return list;
    }

    public String getSheet() {
	return super.getSheet() + Separators.NL + "Portas:" + StrUtil.wrapList(listDoors(), "Nenhuma");
    }

    //
    /////////////

    private void saveDoors(BufferedWriter writer) throws IOException {
	for (Enumeration e = doorReferences.elements(); e.hasMoreElements(); ) {
	    IntPair pair = (IntPair) e.nextElement();
	    writer.write((pair.getValue1() - ((KeyProto) getPrototype()).savedZoneBase)+ " " + Door.getDirectionName(pair.getValue2()).charAt(0)); writer.newLine();
	}
	writer.write(Separators.EOR); writer.newLine();
    }

    void save(BufferedWriter writer) throws IOException {
	super.save(writer);
	saveDoors(writer);
    }
}
