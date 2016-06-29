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

// 	$Id: Money.java,v 1.1.1.1 2001/11/14 09:01:52 fmatheus Exp $

import java.io.IOException;
import java.io.BufferedWriter;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.bit.Bit;
import jmud.util.IntUtil;

public class Money {

  //////////////////////////////
  // Dados da Classe (estaticos)

  static final int K_GOLD   = 0;  // a
  static final int K_SILVER = 1;  // b
  static final int K_COPPER = 2;  // c
  static final int K_CRAVO  = 3;

  static final int BK_GOLD   = Bit.BIT0;
  static final int BK_SILVER = Bit.BIT1;
  static final int BK_COPPER = Bit.BIT2;
  static final int BK_CRAVO  = Bit.BIT3;

  static final int BK_ALL    = Bit.BIT_ALL;

  static final int MIN_PRICE = 1;

  static final String kindLabel[] = {
    "ouro",
    "prata",
    "cobre",
    "cravo"
  };

  static final String kindName[] = {
    "peça(s) de ouro",
    "peça(s) de prata",
    "peça(s) de cobre",
    "cravo(s)"
  };

  static final String kindAbbr[] = {
    "po",
    "pp",
    "pc",
    "cr"
  };

  /*
    Lista das moedas:
    1) decrescente da mais valorizada para a menos valorizada
    2) a menos valorizada de todas deve valer 1
  */
  static final int credit[] = {
    1000,
    100,
    10,
    1
  };

  static final int unitWeight[] = {
    400,
    300,
    200,
    100
  };

  static String getKindLabel(int k) {
    return (k >= 0 && k < kindLabel.length) ? kindLabel[k] : "desconhecido";
  }

  static String getKindAbbr(int k) {
    return (k >= 0 && k < kindLabel.length) ? kindAbbr[k] : "(?)";
  }

  static int findKindByAbbr(String abbr) {
    int len = kindAbbr.length;
    for (int i = 0; i < len; ++i)
      if (abbr.equals(getKindAbbr(i)))
	return i;
    return -1;
  }

  static void creditToCurrency(int[] currencyVector, int credits, int currencyMask) {
    int len = credit.length;
    int curr = 1;
    for (int i = 0; i < len; ++i) {
      if (Bit.isSet(currencyMask, curr)) {
	int cr = credit[i];
	currencyVector[i] = credits / cr;
	credits %= cr;
      }
      else
	currencyVector[i] = 0;
      curr <<= 1;
    }
  }

  static void creditToCurrency(int[] currencyVector, int credits, int[] available) {
    int len = credit.length;
    int curr = 1;
    for (int i = 0; i < len; ++i) {
      int avail = available[i];
      if (avail > 0) {
	int cr = credit[i];
	currencyVector[i] = IntUtil.min(credits / cr, avail);
	credits -= currencyVector[i] * cr;
      }
      else
	currencyVector[i] = 0;
      curr <<= 1;
    }
  }

  // Dados da Classe (estaticos)
  //////////////////////////////

    int[] currencyVector;

  void newVector() {
    currencyVector = new int[credit.length];
  }

  void initializeVector() {
    newVector();
    int len = currencyVector.length;
    for (int i = 0; i < len; ++i)
      currencyVector[i] = 0;
  }

  void set(String line) throws NumberFormatException {

    StringTokenizer tok = new StringTokenizer(line);

    while (tok.hasMoreTokens()) {

      int val = Integer.parseInt(tok.nextToken());

      if (!tok.hasMoreTokens())
	throw new NumberFormatException();

      String abbr = tok.nextToken();

      int    kind = findKindByAbbr(abbr);
      if (kind == -1)
	throw new NumberFormatException();

      currencyVector[kind] = val;
    }

  }

    Money() {
	initializeVector();
    }

    public Money(int credits, int acceptedCurrencies) {
	newVector();
	creditToCurrency(currencyVector, credits, acceptedCurrencies);
    }

    public Money(int credits, Money available) {
	newVector();
	creditToCurrency(currencyVector, credits, available.currencyVector);
    }

    public Money(Money oldMoney) {
	newVector();

	int len = currencyVector.length;
	for (int i = 0; i < len; ++i)
	    currencyVector[i] = oldMoney.currencyVector[i];
    }

    public Money(String moneyLine) throws NumberFormatException {
	initializeVector();

	set(moneyLine);
    }

  Money(LineReader reader) throws InvalidFileFormatException, IOException {
    initializeVector();

    try {
      set(reader.readLine());
    }
    catch(NumberFormatException e) {
      throw new InvalidFileFormatException();
    }

  }

  void save(BufferedWriter writer) throws IOException {
    writer.write(toString());
    writer.newLine();
  }

  public int getCredits() {
    int len = currencyVector.length;
    int credits = 0;
    for (int i = 0; i < len; ++i)
      credits += currencyVector[i] * credit[i];
    return credits;
  }

  void copy(Money source) {
    int len = currencyVector.length;
    for (int i = 0; i < len; ++i)
      currencyVector[i] = source.currencyVector[i];
  }

    public void sub(int credits, int acceptedCurrencies) {
	int remaining = getCredits() - credits;
	creditToCurrency(currencyVector, remaining, acceptedCurrencies);
    }

    public void sub(Money dec) {
	int len = currencyVector.length;
	for (int i = 0; i < len; ++i)
	    currencyVector[i] -= dec.currencyVector[i];
    }

    public void add(Money inc) {
	int len = currencyVector.length;
	for (int i = 0; i < len; ++i)
	    currencyVector[i] += inc.currencyVector[i];
    }

  public String toString() {
    int len = currencyVector.length;
    String pr = "";
    int i = 0;
    for (; i < len; ++i) {
      int cr = currencyVector[i];
      if (cr > 0) {
	pr += cr + " " + getKindAbbr(i);
	break;
      }
    }
    while (++i < len) {
      int cr = currencyVector[i];
      if (cr > 0)
	pr += " " + cr + " " + getKindAbbr(i);
    }
    if (pr.length() == 0)
	return "0 " + kindAbbr[kindAbbr.length - 1];
    return pr;
  }
}
