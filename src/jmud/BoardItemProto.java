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
import jmud.util.Separators;
import jmud.util.log.Log;

import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.log.Log;

class BoardItemProto extends ItemProto {

    int boardId;

  BoardItemProto(int zoneBase, int itemId, LineReader itemFile)
    throws InvalidFileFormatException {

    super(zoneBase, itemId, itemFile);
    Log.debug("Quadro: " + getName());

    try {
      boardId = Integer.parseInt(itemFile.readLine());
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
    return Item.T_BOARD;
  }

  public Item create() {
    return new BoardItem(this);
  }

    /////////////
    // Sheetable:

  public String getSheet() {
    return super.getSheet() + Separators.NL +
	"Quadro: " + boardId;
  }

    //
    /////////////

}

