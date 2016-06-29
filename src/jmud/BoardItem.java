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

import jmud.util.Separators;
import jmud.util.log.Log;

public class BoardItem extends Item {

    Board boardSystem = null;

  BoardItem(BoardItemProto w) {
    super(w);

    boardSystem = theWorld.findBoardById(w.boardId);
    if (boardSystem == null)
	Log.err("Sistema de quadros não encontrado: " + getId());
  }

  protected void finalize() {
    super.finalize();
  }

  int getType() {
    return T_BOARD;
  }

    public Board getBoardSystem() {
	return boardSystem;
    }

    /////////////
    // Sheetable:

  public String getSheet() {
    return super.getSheet() + Separators.NL +
      "Sistema: " + (boardSystem == null ? "Nenhum" : boardSystem.getSheet());
  }

    //
    /////////////

    void save(BufferedWriter writer) throws IOException {
	super.save(writer);
	writer.write(String.valueOf(getPrototype().getId())); writer.newLine();
    }

}
