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

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;

import jmud.util.log.Log;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.Separators;
import jmud.util.StrUtil;

import jgp.container.Vector;

public class Board implements UniqueId {

    private int    theId;
    private String theTitle;
    private int    readLevel;
    private int    writeLevel;
    private int    admLevel;
    private Vector messages = new Vector();

    Board(int id, String title, int read, int write, int adm, Config cfg) {
	theId      = id;
	theTitle   = title;
	readLevel  = read;
	writeLevel = write;
	admLevel   = adm;

	load(cfg);
    }

    public int getId() {
	return theId;
    }

    public String getTitle() {
	return theTitle;
    }

    public int getReadLevel() {
	return readLevel;
    }

    public int getWriteLevel() {
	return writeLevel;
    }

    public int getAdmLevel() {
	return admLevel;
    }

    void load(Config cfg) {

	String fName = cfg.getBoardFilesDir() + theId;

	LineReader reader = null;
	try {
	    reader = new LineReader(new FileReader(fName));

	    for (; ; ) {
		if (reader.eos()) {
		    Log.err("Arquivo de quadro com formato inválido: " + fName);
		    break;
		}

		String line = reader.readLine();

		if (line.startsWith(Separators.EOF))
		    break;

		if (!line.startsWith(Separators.BOR)) {
		    Log.err("Arquivo de quadro com formato invalido: " + fName);

		    break;
		}

		BoardEntry entry = new BoardEntry(reader);

		Log.debug("Entrada: " + entry.getHeader());

		messages.insert(entry);
	    }

	}
	catch(FileNotFoundException e) {
	    Log.err("Arquivo de quadro não encontrado: " + fName);
	}
	catch(IOException e) {
	    Log.err("Falha de E/S ao ler arquivo de quadro: " + fName);
	}
	catch(InvalidFileFormatException e) {
	    Log.err("Arquivo de quadro com formato inválido: " + fName);
	}

	if (reader != null) {
	    try {
		reader.close();
	    }
	    catch(IOException e) {
		Log.err("Falha de E/S ao fechar arquivo de quadro: " + fName);
	    }
	}

	messages.trim();
    }

    void save(Config cfg) {

	String fName = cfg.getBoardFilesDir() + theId;

	BufferedWriter writer = null;
	try {
	    writer = new BufferedWriter(new FileWriter(fName));

	    for (Enumeration enum = messages.elements(); enum.hasMoreElements(); ) {
		BoardEntry be = (BoardEntry) enum.nextElement();
		writer.write(Separators.BOR); writer.newLine();
		be.save(writer);
	    }

	    writer.write(Separators.EOF); writer.newLine();
	}
	catch(IOException e) {
	    Log.err("Falha de E/S ao gravar arquivo de quadro: " + fName);
	}

	if (writer != null) {
	    try {
		writer.close();
	    }
	    catch(IOException e) {
		Log.err("Falha de E/S ao fechar arquivo de quadro: " + fName);
	    }
	}
    }

    String getSheet() {
	return "ID [" + theId + "], Título: " + theTitle + ", Ler: " + readLevel + ", Escrever: " + writeLevel + ", Adm: " + admLevel;
    }

    public String getSummary() {
	String summ = "";
	int i = 1;
	for (Enumeration enum = messages.elements(); enum.hasMoreElements(); ++i) {
	    BoardEntry entry = (BoardEntry) enum.nextElement();
	    summ += Separators.NL + StrUtil.formatNumber(i, 2) + ". " + entry.getHeader();
	}
	return summ;
    }

    public BoardEntry getMessage(int ind) {
	try {
	    return (BoardEntry) messages.getElementAt(ind - 1);
	}
	catch(ArrayIndexOutOfBoundsException e) {
	}
	return null;
    }

    public void putMessage(BoardEntry be) {
	messages.insert(be);
    }

    public void eraseMessage(int ind) throws ArrayIndexOutOfBoundsException {
	messages.removeFrom(ind - 1);
    }
}

