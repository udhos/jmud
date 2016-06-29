
package jmud.parser;

import java_cup.runtime.Symbol;

public class SyntaxSymbol extends Symbol {

    private int lineNumber;

    public SyntaxSymbol(int tk, String val, int line, int position) {
	super(tk, position, position + val.length(), val);
	lineNumber = line;
    }

    public String toString() {
	return "'" + value + "' (" + sym + ") at line " + (lineNumber + 2);
    }
}
