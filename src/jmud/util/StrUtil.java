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

// 	$Id: StrUtil.java,v 1.2 2001/12/07 01:55:53 fmatheus Exp $

package jmud.util;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.BufferedWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jmud.JMud;
import jmud.Clock;
import jmud.jgp.StringExtractor;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.InvalidFlagException;
import jmud.util.Separators;
import jmud.util.bit.Bit;
import jmud.util.pair.StrPair;
import jmud.util.log.Log;
import jgp.container.Vector;
import jgp.functor.UnaryFunction;

public class StrUtil {

  static private String DAYS_OF_WEEK[] = {
    "domingo",
    "segunda",
    "terça",
    "quarta",
    "quinta",
    "sexta",
    "sábado"
  };

    static public String centerPad(String str, int width, char c) {
	int len = str.length();
	if (width <= len)
	    return str;
	int pad = width - len;
	int col = pad >> 1;

	StringBuffer buf = new StringBuffer(width);

	int i = 0;
	for (; i < col; ++i)
	    buf.append(c);
	buf.append(str);
	for (; i < pad; ++i)
	    buf.append(c);

	return buf.toString();
    }

  static public String centerPad(String str, int width) {
    return centerPad(str, width, ' ');
  }

    static public String rightPad(String str, int width, char c) {
	int len = str.length();
	if (width <= len)
	    return str;
	int pad = width - len;

	StringBuffer buf = new StringBuffer(width);

	buf.append(str);
	for (int i = 0; i < pad; ++i)
	    buf.append(c);

	return buf.toString();
    }

  static public String rightPad(String str, int width) {
    return rightPad(str, width, ' ');
  }

    static public String leftPad(String str, int width, char c) {
	int len = str.length();
	if (width <= len)
	    return str;
	int pad = width - len;

	StringBuffer buf = new StringBuffer(width);

	for (int i = 0; i < pad; ++i)
	    buf.append(c);
	buf.append(str);

	return buf.toString();
    }

  static public String leftPad(String str, int width) {
    return leftPad(str, width, ' ');
  }

    static public String leftWidth(String str, int width, char c) {
	return str.length() > width ? str.substring(0, width) : leftPad(str, width, c);
    }

    static public String leftWidth(String str, int width) {
	return leftWidth(str, width, ' ');
    }

    static public String rightWidth(String str, int width, char c) {
	return str.length() > width ? str.substring(0, width) : rightPad(str, width, c);
    }

    static public String rightWidth(String str, int width) {
	return rightWidth(str, width, ' ');
    }

  static public String formatNumber(int value, int width, char c) {
    String str = String.valueOf(value);
    if (str.length() > width)
      str = str.substring(0, width);
    return leftPad(str, width, c);
  }

  static public String formatNumber(int value, int width) {
    return formatNumber(value, width, ' ');
  }

  static public String getDate() {
      Calendar calendar = JMud.getClock().getCalendar();

    return DAYS_OF_WEEK[calendar.get(Calendar.DAY_OF_WEEK) - 1].substring(0, 3) + " " +
      String.valueOf(calendar.get(Calendar.YEAR)) + "/" +
      formatNumber(calendar.get(Calendar.MONTH) + 1, 2, '0') + "/" +
      formatNumber(calendar.get(Calendar.DAY_OF_MONTH), 2, '0') + " " +
      formatNumber(calendar.get(Calendar.HOUR_OF_DAY), 2, '0') + ":" +
      formatNumber(calendar.get(Calendar.MINUTE), 2, '0') + ":" +
      formatNumber(calendar.get(Calendar.SECOND), 2, '0');
  }

    static public String replace(String str, String oldStr, String newStr) {
	int i = str.indexOf(oldStr);
	if (i == -1)
	  return str;

	return str.substring(0, i) + newStr + str.substring(i + oldStr.length());
    }

    static public String replaceAll(String str, String oldStr, String newStr) {
	int i = str.indexOf(oldStr);
	if (i == -1)
	    return str;
	int start = 0;
	int oldLen = oldStr.length();
	StringBuffer result = new StringBuffer(str.length());
	while (i >= 0) {
	    result.append(str.substring(start, i) + newStr);
	    start = i + oldLen;
	    i = str.indexOf(oldStr, start);
	}
	result.append(str.substring(start));
	return new String(result);
    }

  static public String readDescription(LineReader file)
    throws InvalidFileFormatException {
      String desc = "";

      try {
	for (; ; ) {
	  if (file.eos())
	    throw new InvalidFileFormatException();

	  String descLine = file.readLine();
	  if (descLine.startsWith(Separators.EOD))
	    break;

	  desc += ((desc == "") ? descLine : Separators.NL + descLine);
	}
	return desc;
      }
      catch(IOException e) {
	throw new InvalidFileFormatException();
      }
  }

  static public void writeDescription(BufferedWriter writer, String desc)
    throws IOException {
    writer.write(desc); writer.newLine();
    writer.write(Separators.EOD); writer.newLine();
  }

  static public int parseFlags(String str) throws InvalidFlagException {
    str = str.trim();
    int result = 0;
    for (int i = 0; i < str.length(); ++i) {
      char c = str.charAt(i);
      if (c == '-')
	continue;
      else if (c >= 'a' && c <= 'z')
	result |= (1 << (c - 'a'));
      else if (c >= 'A' && c <= 'F')
	result |= (1 << (c - 'A' + 26));
      else
	throw new InvalidFlagException();
    }
    return result;
  }

  static public String makeFlags(int flags) {
    String f = "";
    for (int i = 0; i < 32; ++i)
      if ((flags & (1 << i)) != 0)
	if (i < 26)
	  f += (char) (i + 'a');
	else
	  f += (char) (i + 'A' - 26);
    return ((f.length() == 0) ? "-" : f);
  }

    static public String listFlags(int vector, String labels[]) {
	String list = "";

	int i = 0;
	int currFlag = 1;
	for (; i < labels.length; ++i) {
	    if (Bit.isSet(vector, currFlag)) {
		list += labels[i++];
		currFlag <<= 1;
		break;
	    }
	    currFlag <<= 1;
	}
	for (; i < labels.length; ++i) {
	    if (Bit.isSet(vector, currFlag))
		list += ", " + labels[i];
	    currFlag <<= 1;
	}

	return list;
    }

    static public String wrapList(String list, String none) {
	return list.length() == 0 ? none : list;
    }

    static public String fancyListFlags(int vector, String labels[], String none) {
	return wrapList(listFlags(vector, labels), none);
    }

    static public String strTable(int ind, String table[]) {
	return (ind >= 0 && ind < table.length) ? table[ind] : "*ERRO*";
    }

    static public String loadAliases(LineReader reader) throws IOException {
	return reader.readLine().toLowerCase();
    }

    static public boolean findAliasPrefix(String alias, StringTokenizer tok) {
 	while (tok.hasMoreTokens())
	    if (tok.nextToken().startsWith(alias))
	 	return true;
	return false;
    }

    static public boolean findAliasPrefix(String alias, String aliasList) {
	if (alias.indexOf('|') != -1)
	    return findAliasPrefixOr(alias, aliasList);
	return findAliasPrefixAnd(alias, aliasList);
    }

    static public boolean findAliasPrefixAnd(String alias, String aliasList) {
	StringTokenizer aliases = new StringTokenizer(alias, " &");
	while (aliases.hasMoreTokens())
	    if (!findAliasPrefix(aliases.nextToken(), new StringTokenizer(aliasList)))
		return false;
	return true;
    }

    static public boolean findAliasPrefixOr(String alias, String aliasList) {
	StringTokenizer aliases = new StringTokenizer(alias, " |");
	while (aliases.hasMoreTokens())
	    if (findAliasPrefix(aliases.nextToken(), new StringTokenizer(aliasList)))
		return true;
	return false;
    }

    static public boolean findAliasMatch(String alias, StringTokenizer tok) {
	while (tok.hasMoreTokens())
	    if (tok.nextToken().equals(alias))
		return true;
	return false;
    }

    static public boolean findAliasMatch(String alias, String aliasList) {
	StringTokenizer aliases = new StringTokenizer(alias);
	while (aliases.hasMoreTokens())
	    if (!findAliasMatch(aliases.nextToken(), new StringTokenizer(aliasList)))
		return false;
	return true;
    }

  static public boolean isSpc(char c) {
    return (c == ' ' || c == '\t');
  }

  static public int findSpc(String str, int i) {
    for (; i < str.length(); ++i) {
      char c = str.charAt(i);
      if (isSpc(c))
	break;
    }
    return i;
  }

  static public int skipSpc(String str, int i) {
    for (; i < str.length(); ++i) {
      char c = str.charAt(i);
      if (!isSpc(c))
	break;
    }
    return i;
  }

  static public int nextWord(String str, int i) {
    return skipSpc(str, findSpc(str, i));
  }

  static public String eatFirstWord(String str) {
    return str.substring(nextWord(str, skipSpc(str, 0)));
  }

    static public String eatWords(String str, int n) {
	while (n-- > 0)
	    str = eatFirstWord(str);
	return str;
    }

    static public char getArticle(boolean female) {
	return female ? 'a' : 'o';
    }

    static public String adjustSex(String str, boolean female, char ch) {

	int len = str.length();
	StringBuffer buf = new StringBuffer(len);
	buf.setLength(len);

	for (int i = 0; i < len; ++i) {
	    char c = str.charAt(i);
	    buf.setCharAt(i, c == ch ? getArticle(female) : c);
	}

	return buf.toString();
    }

    static public String adjustSex(String str, boolean female) {
	return adjustSex(str, female, '*');
    }

    static public String headedConcat(Enumeration enum, UnaryFunction extractor, String sep) {
	String str = enum.hasMoreElements() ? (String) extractor.execute(enum.nextElement()) : "";

	while (enum.hasMoreElements())
	    str += sep + (String) extractor.execute(enum.nextElement());

	return str;
    }

    static public String concat(Enumeration enum, UnaryFunction extractor, String sep) {
	String str = "";

	while (enum.hasMoreElements())
	    str += sep + (String) extractor.execute(enum.nextElement());

	return str;
    }

    static public String headedConcat(Enumeration enum, String sep) {
	return headedConcat(enum, new StringExtractor(), sep);
    }

    static public String headedConcat(Enumeration enum, String sep, String none) {
	if (!enum.hasMoreElements())
	    return none;

	return headedConcat(enum, new StringExtractor(), sep);
    }

    static public String concat(Enumeration enum, String sep) {
	return concat(enum, new StringExtractor(), sep);
    }

    static public String concat(Enumeration enum, String sep, String none) {

	if (!enum.hasMoreElements())
	    return none;

	return concat(enum, sep);
    }

    /*
    static public String listVector(Vector vec, String sep, String none) {
	return concat(vec.elements(), sep, none);
    }
    */

    static public Vector tokenize(String str, StringTokenizer tokenizer) {
	Vector list = new Vector();
	while (tokenizer.hasMoreElements())
	    list.insert(tokenizer.nextToken());
	return list;
    }

    static public Vector tokenize(String str, String sep) {
	return tokenize(str, new StringTokenizer(str, sep));
    }

    static public Vector tokenize(String str) {
	return tokenize(str, new StringTokenizer(str));
    }

    static public StrPair parseQuoted(String str) {
	boolean ok = true;
	int i = 0;
	int j = 0;
	int len = str.length();
	StringBuffer buf = new StringBuffer(len);
	buf.setLength(len);
	for ( ; i < len; ++i) {
	    char c = str.charAt(i);
	    if (c == '\'') {
		ok = !ok;
	    }
	    else {
		if (isSpc(c) && ok)
		    break;
		buf.setCharAt(j, c);
		++j;
	    }
	}
	buf.setLength(j);
	return new StrPair(buf.toString(), (i < len) ? str.substring(i+1) : "");
    }

    // 0x0F => 'f'
    public static byte hexaNibble(byte by) {
	return (byte) ((by > 9) ? (by + 'a' - 10) : (by + '0'));
    }

    public static StringBuffer unpack(byte[] array) {

	int len = array.length;
	int length = len << 1;

	StringBuffer buf = new StringBuffer(length);
	buf.setLength(length);

	for (int i = 0, j = 0; i < len; ++i) {

	    byte by = array[i];

	    byte hi = (byte) ((by & 0xF0) >> 4);
	    byte lo = (byte) (by & 0x0F);

	    buf.setCharAt(j++, (char) hexaNibble(hi));
	    buf.setCharAt(j++, (char) hexaNibble(lo));
	}

	return buf;
    }

    public static String cryptoHash(String message) {

	String algo = "MD5";

	try {
	    MessageDigest md = MessageDigest.getInstance(algo);
	    md.reset();
	    return unpack(md.digest(message.getBytes())).toString();
	}
	catch(NoSuchAlgorithmException e) {
	    Log.warn("Algoritmo 'digest' não encontrado: " + algo);
	    return message;
	}
    }

    public static String predicate(boolean cond) {
	return cond ? "sim" : "não";
    }

    public static boolean isAll(String all) {
        if (all == null) return false;
        return (all.equalsIgnoreCase("tudo") ||
		all.equalsIgnoreCase("todos") ||
		all.equalsIgnoreCase("todas"));
    }

  public static String upcaseFirst(String str) {
    return (str.length() == 0) ? str : (Character.toUpperCase(str.charAt(0)) + str.substring(1));
  }

  public static String ensureTrailingSlash(String path) {
    return (path.charAt(path.length() - 1) == Separators.SLASH) ? path : (path + Separators.SLASH);
  }

    public static String enclose(String str, String before, String after) {
	return " " + before + str + after + " ";
    }

    // replace
    public static String rplEnclose(String str) {
	return enclose(str, "<", ">");
    }

    // group
    public static String grpEnclose(String str) {
	return enclose(str, "{", "}");
    }

    // optional
    public static String optEnclose(String str) {
	return enclose(str, "[", "]");
    }

    public static String or(String str1, String str2) {
	return enclose("|", str1, str2);
    }
}

