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

import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.StrUtil;

public class BoardEntry {

    static final String fieldSep = " :: ";

    private int     authorId;
    private int     authorRank;
    private boolean authorIsPlayer;
    private String  header;
    private String  body;

    public BoardEntry(int id, int rank, boolean player, String authorName, String date, String title, String bd) {
	authorId       = id;
	authorRank     = rank;
	authorIsPlayer = player;
	header         = StrUtil.rightPad(authorName, Client.MAX_NAME_LENGTH) + fieldSep + date + fieldSep + title;
	body           = bd;
    }

    BoardEntry(LineReader reader) throws InvalidFileFormatException {
	try {
	    if (reader.eos())
		throw new InvalidFileFormatException();

	    authorId         = Integer.parseInt(reader.readLine());
	    authorRank       = Integer.parseInt(reader.readLine());
	    authorIsPlayer   = Boolean.getBoolean(reader.readLine());
	    header           = reader.readLine();

	    body             = StrUtil.readDescription(reader);
	}
	catch(IOException e) {
	    throw new InvalidFileFormatException();
	}
	catch(NumberFormatException e) {
	  throw new InvalidFileFormatException();
	}
    }

    void save(BufferedWriter writer) throws IOException {
	writer.write(String.valueOf(authorId));       writer.newLine();
	writer.write(String.valueOf(authorRank));     writer.newLine();
	writer.write(String.valueOf(authorIsPlayer)); writer.newLine();
	writer.write(header);                         writer.newLine();

	StrUtil.writeDescription(writer, body);
    }

    public int getAuthorId() {
	return authorId;
    }

    public int getAuthorRank() {
	return authorRank;
    }

    public boolean authorIsPlayer() {
	return authorIsPlayer;
    }

    public String getHeader() {
	return  header;
    }

    public String getBody() {
	return body;
    }
}
