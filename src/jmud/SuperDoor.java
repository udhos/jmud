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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import jmud.util.InvalidFlagException;
import jmud.util.StrUtil;

class SuperDoor {

    private int  theDirection;
    private int  destinationId;
    private int  theProperties;
    private Room destination   = null;

    // porta nao-interzona
    SuperDoor(int zoneBase, String doorLine)
	throws NoSuchElementException, InvalidFlagException,
	       NumberFormatException {

	StringTokenizer toker = new StringTokenizer(doorLine);

	theDirection = Door.getDirectionByName(toker.nextToken());
	if (theDirection == -1)
	    throw new NoSuchElementException();

	int id = Integer.parseInt(toker.nextToken());

	if (toker.hasMoreTokens())
	    theProperties = StrUtil.parseFlags(toker.nextToken());
	else
	    theProperties = 0;

	destinationId = zoneBase + id;
    }

    // porta interzona
    SuperDoor(int zoneBase, String doorDir, int roomId)
	throws NoSuchElementException {

	theDirection = Door.getDirectionByName(doorDir);
	if (theDirection == -1)
	    throw new NoSuchElementException();

	destinationId = zoneBase + roomId;
    }

    int getDestinationId() {
	return destinationId;
    }

    void adjust(Room targetRoom) {
	destination = targetRoom;
    }

    Door makeDoor() {
	return new Door(theDirection, theProperties, destination);
    }

    void setError() {
	theDirection = Door.D_ERROR;
    }

    String getDirectionName() {
	return Door.getDirectionName(theDirection);
    }

    boolean leadsTo(String directionName) {
	return getDirectionName().startsWith(directionName);
    }


}

