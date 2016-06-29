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

import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.StrUtil;

public class Door implements Viewable {

    public static final int D_NORTH = 0;
    public static final int D_SOUTH = 1;
    public static final int D_EAST  = 2;
    public static final int D_WEST  = 3;
    public static final int D_UP    = 4;
    public static final int D_DOWN  = 5;
    public static final int D_ERROR = 6;

    public static final String directions[] = {
	"norte",
	"sul",
	"leste",
	"oeste",
	"cima",
	"baixo",
	"*ERRO*"
    };

    static final int inverse[] = {
	D_SOUTH,
	D_NORTH,
	D_WEST,
	D_EAST,
	D_DOWN,
	D_UP,
	D_ERROR
    };

    static final int PB_CLOSEABLE = Bit.BIT0;  // a
    static final int PB_LOCKABLE  = Bit.BIT1;  // b
    static final int PB_HIDDEN    = Bit.BIT2;  // c

    static final String propLabels[] = {
	"fechável",
	"trancável",
	"oculta"
    };

    public static final int S_OPEN   = 0;
    public static final int S_CLOSED = 1;
    public static final int S_LOCKED = 2;

    static final String stateLabels[] = {
	"aberta",
	"fechada",
	"trancada"
    };

    public static String getDirectionName(int dir) {
	if (dir >= 0 && dir <= directions.length)
	    return directions[dir];
	return directions[D_ERROR];
    }

    static int getInverseDir(int dir) {
	if (dir >=0 && dir <= inverse.length)
	    return inverse[dir];
	return D_ERROR;
    }

    static int getDirectionByName(String dirName) {
	char c = dirName.charAt(0);
	for (int i = 0; i < directions.length -1; ++i)
	    if (c == directions[i].charAt(0))
		return i;
	return -1;
    }

    static String expandDirectionName(String dirName) {
	int dir = getDirectionByName(dirName);
	return (dir == -1) ? directions[D_ERROR] : directions[dir];
    }

    private   int  theProperties   = 0;
    private   int  state           = S_OPEN;
    protected int  theDirection    = D_ERROR;
    protected Room destinationRoom = null;

    Door(int direction, int properties, Room destination) {
	theDirection    = direction;
	theProperties   = properties;
	destinationRoom = destination;
    }

    boolean hasId(int id) {
	return theDirection == id;
    }

    boolean hasName(String name) {
	return getDirectionName(theDirection).startsWith(name);
    }

    int getDirection() {
	return theDirection;
    }

    public String getDirectionName() {
	if (theDirection >=0 && theDirection <= directions.length)
	    return directions[theDirection];
	Log.err("Porta da sala " + destinationRoom.getId() + " com direção inválida: " + theDirection);
	return directions[D_ERROR];
    }

    int getDestinationId() {
	return destinationRoom.getId();
    }

    Room getDestinationRoom() {
	return destinationRoom;
    }

    void adjust(Room targetRoom) {
	destinationRoom = targetRoom;
    }

    Door getInverseDoor() {
	return getDestinationRoom().findDoorByDir(Door.getInverseDir(getDirection()));
    }

    boolean leadsTo(String directionName) {
	return getDirectionName(theDirection).startsWith(directionName);
    }

    public boolean isCloseable() {
	return Bit.isSet(theProperties, PB_CLOSEABLE);
    }

    public boolean isLockable() {
	return Bit.isSet(theProperties, PB_LOCKABLE);
    }

    boolean isHidden() {
	return Bit.isSet(theProperties, PB_HIDDEN);
    }

    public int getStatus() {
	return state;
    }

    void setStatus(int st) {
	state = st;
    }

    public void setBothStatus(int st, Room rm) {
	setStatus(st);
	Door invDoor = getInverseDoor();
	if (invDoor.getDestinationRoom() == rm)
	    invDoor.setStatus(st);
    }

    boolean isOpen() {
	return getStatus() == S_OPEN;
    }

    ////////////
    // Sheetable

    public String getSheet() {
	return getDirectionName() + " [" + getDestinationId() + "] " + stateLabels[state] + " (" + StrUtil.listFlags(theProperties, propLabels) + ")";
    }

    // Sheetable
    ////////////

    ///////////
    // Viewable

    public boolean canBeSeenBy(Char looker) {
	return looker.isOmni() || !isHidden();
    }

    // Viewable
    ///////////
}

