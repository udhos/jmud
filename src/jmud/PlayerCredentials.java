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

import java.util.StringTokenizer;

import jmud.util.StringParseException;

class PlayerCredentials implements Authenticable {

    private String playerName  = null;
    private int    playerId    = -1;
    private int    playerRank  = Char.R_DISABLED;
    private int    playerLevel = 0;

    PlayerCredentials(Player plr) {
	playerName  = plr.getName();
	playerId    = plr.getId();
	playerRank  = plr.getRank();
	playerLevel = plr.getLevel();
    }

    PlayerCredentials(String str) throws StringParseException {
	StringTokenizer toker = new StringTokenizer(str, "\"");

	if (!toker.hasMoreTokens())
	    throw new StringParseException();
	playerName = toker.nextToken();

	if (!toker.hasMoreTokens())
	    throw new StringParseException();
	toker = new StringTokenizer(toker.nextToken());

	try {
	    if (!toker.hasMoreTokens())
		throw new StringParseException();
	    playerId = Integer.parseInt(toker.nextToken());

	    if (!toker.hasMoreTokens())
		throw new StringParseException();
	    playerRank = Integer.parseInt(toker.nextToken());

	    if (!toker.hasMoreTokens())
		throw new StringParseException();
	    playerLevel = Integer.parseInt(toker.nextToken());
	}
	catch(NumberFormatException e) {
	    throw new StringParseException();
	}
    }

    public String getName() {
	return playerName;
    }

    public boolean hasName(String name) {
	return Player.sameName(getName(), name);
    }

    public int getId() {
	return playerId;
    }

    public int getRank() {
	return playerRank;
    }

    public int getLevel() {
	return playerLevel;
    }

    public String getString() {
	return playerName + " [" + playerId + "] (posto " + playerRank + ", nível "+ playerLevel + ")";
    }

    public String toString() {
	return "\"" + playerName + "\" " + playerId + " " + playerRank + " " + playerLevel;
    }
}
