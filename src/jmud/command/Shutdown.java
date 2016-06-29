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

package jmud.command;

import jmud.Char;


import jmud.util.Separators;
import jmud.util.StrUtil;
import jmud.util.log.Log;
import jmud.util.bit.Bit;
import jmud.util.BoolUtil;
import jmud.util.color.Color;
import jgp.container.Vector;
import jgp.adaptor.Finder;
import jgp.algorithm.Transformer;

class Shutdown extends Command {

    Shutdown(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    static final String NOW    = "agora";
    static final String CANCEL = "cancelar";
    static final String TIME   = "tempo";
    static final String SYNTAX = "Sintaxe: desative agora|cancelar|tempo|<horas>";

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {
	if (!toker.hasMoreTokens()) {
	    aChar.send(SYNTAX);
	    return;
	}

	String arg = toker.nextToken();

	if (TIME.startsWith(arg)) {
	    int horas = theManager.getShutdownTime();
	    aChar.send((horas == 0) ? "N�o h� desativa��o agendada." : ("Falta(m) " + horas + " hora(s) para desativa��o agendada."));
	    return;
	}
	else if (NOW.equalsIgnoreCase(arg)) {
	    Log.info(aChar.getName() + " desativando JMud agora");
	    theManager.shutdown();
	}
	else if (CANCEL.startsWith(arg)) {
	    Log.info(aChar.getName() + " cancelando desativa��o agendada");
	    theManager.cancelShutdown();
	    aChar.send("Desativa��o cancelada.");
	    return;
	}

	try {
	    int horas = Integer.parseInt(arg);
	    Log.info(aChar.getName() + " agendando desativa��o para " + horas + " hora(s)");
	    theManager.scheduleShutdown(horas);
	    aChar.send("Desativa��o agendada para " + horas + " hora(s).");
	}
	catch(NumberFormatException e) {
	    aChar.send(SYNTAX);
	}
    }
}


