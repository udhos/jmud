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
import java.io.FileNotFoundException;
import java.io.IOException;

import jmud.hook.HookTable;
import jmud.magic.Magic;
import jmud.command.Command;
import jmud.util.Separators;
import jmud.util.file.LineReader;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.log.Log;
import jmud.lang.Lang;

public class JMud {

  static private final String VERSION = "JMud 0.25 Alfa";

    static private final int localStandardTime = -3 * 60 * 60 * 1000; // Brazil East

    static private final Clock clk = new Clock(localStandardTime);

    public static Clock getClock() {
	return clk;
    }

  private static void error(String msg) {
    System.err.println(Lang.JMUD_MSG_ERROR + msg);
  }

  private static void abort(String msg) {
    error(msg);
    System.err.println(Lang.JMUD_MSG_ABORTING);
    System.exit(1);
  }

  private static void usage(String msg) {
    error(msg);
    System.err.println(Lang.JMUD_MSG_USAGE);
    System.exit(1);
  }

    public static String getVersion() {
	return VERSION;
    }

    public static String getFullVersion() {
	String subversion = Global.getSubversion();
	String version = getVersion();
	return subversion == null ? version : version + " " + subversion;
    }

    public static void loadConfig(String configFileName) {
	Log.info(Lang.JMUD_MSG_LOADING_CONFIG + configFileName);

	try {
	    Global.setConfig(new Config(configFileName));
	}
	catch (FileNotFoundException e) {
	    Log.abort(Lang.JMUD_MSG_MISSING_CONFIG + configFileName);
	}
	catch (IOException e) {
	    Log.abort(Lang.JMUD_MSG_CONFIG_IO_ERROR + configFileName);
	}
	catch (InvalidFileFormatException e) {
	    Log.abort(Lang.JMUD_MSG_CONFIG_FORMAT_ERROR + configFileName);
	}
    }

    public static void loadSubversion(String subversionFileName) {

	if (subversionFileName != null) {

	    try {
		LineReader reader = new LineReader(new FileReader(subversionFileName));

		String subversion = reader.readLine();
		if (subversion == null)
		    Log.warn(Lang.JMUD_MSG_EMPTY_SUBVERSION + subversionFileName);

		Global.setSubversion(subversion);
	    }
	    catch (FileNotFoundException e) {
		Log.warn(Lang.JMUD_MSG_SUBVERSION_OPEN_ERROR + subversionFileName);
	    }
	    catch (IOException e) {
		Log.warn(Lang.JMUD_MSG_SUBVERSION_READ_ERROR + subversionFileName);
	    }
	}
	else
	    Log.warn(Lang.JMUD_MSG_SUBVERSION_UNDEF_ERROR);

    }

  public static void main(String args[]) {

    final String defaultConfig = "jmud.cfg";
    final int    defaultPort   = 1234;

    String configFileName = null;
    int    portNumber     = -1;

    boolean testOnly = false;

    Log.turnConsoleOn(true);
    Log.turnRecipientsOn(true);
    Log.setDefaultPriority(Char.R_ADMIN);

    for (int i = 0; i < args.length; ++i) {
      String arg = args[i];

      if (arg.equals("-c")) {
	if (configFileName == null) {
	  if (i < args.length - 1)
	    configFileName = args[++i];
	  else
	    usage("-c " + Lang.JMUD_MSG_REQ_CFG_FILE);
	}
	else
	  usage(Lang.JMUD_MSG_DUP_OPT + arg);
      }
      else if (arg.equals("-p")) {
	if (portNumber < 0) {
	  if (i < args.length - 1) {
	    try {
	      portNumber = Integer.parseInt(args[++i]);
	    }
	    catch(NumberFormatException e) {
	      usage("-p " + Lang.JMUD_MSG_REQ_NUM_VALUE);
	    }
	  }
	  else
	    usage("-p " + Lang.JMUD_MSG_REQ_PORT);
	}
	else
	  usage(Lang.JMUD_MSG_DUP_OPT + arg);
      }
      else if (arg.equals(Lang.JMUD_CMDLINE_VERSION_SWITCH)) {
	  if (configFileName == null)
	      loadConfig(defaultConfig);
	  else
	      loadConfig(configFileName);
	  loadSubversion(Global.getConfig().getSubversionFileName());
	  System.out.println(getFullVersion());
	  System.exit(0);
      }
      else if (arg.equals(Lang.JMUD_CMDLINE_TEST_SWITCH))
	  testOnly = true;
      else
	usage(Lang.JMUD_MSG_INV_OPT + arg);
    }

    Log.info(Lang.JMUD_MSG_LANGUAGE);

    if (configFileName == null)
      configFileName = defaultConfig;

    loadConfig(configFileName);

    loadSubversion(Global.getConfig().getSubversionFileName());

    Log.info(Lang.JMUD_MSG_VERSION + JMud.getFullVersion());

    if (portNumber < 0)
      portNumber = defaultPort;

    Manager theManager = new Manager(portNumber);

    Log.insert(theManager);

    Client.setManager(theManager);
    Command.setManager(theManager);
    Char.setManager(theManager);
    Task.setManager(theManager);
    Magic.setManager(theManager);

    HookTable.verify();

    theManager.go(testOnly);
  }
}
