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

// 	$Id: Log.java,v 1.1.1.1 2001/11/14 09:01:53 fmatheus Exp $

package jmud.util.log;

import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Enumeration;

import jgp.container.Vector;
import jmud.util.StrUtil;

public class Log {

    static private PrintWriter stream          = new PrintWriter(System.out, true);
    static private boolean     console         = false;
    static private boolean     recipients      = false;
    static private int         defaultPriority = 0;
    static private Vector      theRecipients   = new Vector();

    static final int L_INF = 0;
    static final int L_DBG = 1;
    static final int L_WRN = 2;
    static final int L_ERR = 3;

    static private int defaultLevel = L_INF;

    static private final String logLabels[] = {
	"",
	"(DEP) ",
	"(AVS) ",
	"(ERR) "
    };

    public static PrintWriter getStream() {
	return stream;
    }

    private static void echo(String msg, int priority, int level) {
	msg = logLabels[level] + msg;
	if (recipients)
	    for (Enumeration enum = theRecipients.elements(); enum.hasMoreElements(); ) {
		LogRecipient rec = (LogRecipient) enum.nextElement();
		rec.forward(msg, priority);
	    }
	if (console)
	    getStream().println(StrUtil.getDate() + " " + msg);
    }

    private static void echo(String msg, int priority) {
	echo(msg, priority, defaultLevel);
    }

    private static void echo(String msg) {
	echo(msg, defaultPriority);
    }

    public static void info(String msg, int priority) {
	echo(msg, priority, L_INF);
    }

    public static void info(String msg) {
	info(msg, defaultPriority);
    }

    public static void debug(String msg, int priority) {
	echo(msg, priority, L_DBG);
    }

    public static void debug(String msg) {
	debug(msg, defaultPriority);
    }

    public static void warn(String msg, int priority) {
	echo(msg, priority, L_WRN);
    }

    public static void warn(String msg) {
	warn(msg, defaultPriority);
    }

    public static void err(String msg, int priority) {
	echo(msg, priority, L_ERR);
    }

    public static void err(String msg) {
	err(msg, defaultPriority);
    }

    public static void abort(String msg, int priority) {
	err(msg, priority);
	info("Abortando.", priority);
	System.exit(1);
    }

    public static void abort(String msg) {
	abort(msg, defaultPriority);
    }

    public static void insert(LogRecipient rec) {
	theRecipients.insert(rec);
    }

    public static void remove(LogRecipient rec) throws NoSuchElementException {
	theRecipients.remove(rec);
    }

    public static void turnConsoleOn(boolean b) {
	console = b;
    }

    public static void turnRecipientsOn(boolean b) {
	recipients = b;
    }

    public static void setDefaultPriority(int priority) {
	defaultPriority = priority;
    }
}

