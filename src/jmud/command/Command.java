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

import java.util.Enumeration;

import jmud.Char;
import jmud.Player;
import jmud.Creature;
import jmud.World;
import jmud.Manager;
import jmud.util.Separators;
import jmud.util.bit.Bit;
import jmud.util.log.Log;

public abstract class Command {

    /* options */
    static final int OPT_PLR  = Bit.BIT0;
    static final int OPT_CRT  = Bit.BIT1;

    /* parameters */
    static final int PAR_NONE = -1;

    static protected Manager theManager = null;
    static protected World   theWorld   = null;

    public static void setManager(Manager aManager) {
	theManager = aManager;
    }

    public static void setWorld(World aWorld) {
	theWorld = aWorld;
    }

    private String theName = null;
    private int    minRank;
    private int    minPos;
    private int    options;
    private int    parameter;
    private String syntax = null;

    public static void execute(Command cmd, Char ch, CommandTokenizer toker, String word) {

	if (cmd == null) {
	    ch.send("Comando inv�lido.");
	    return;
	}

	if (ch instanceof Player && !cmd.allowPlayer()) {
	    ch.send("Jogadores n�o podem usar esse comando.");
	    return;
	}

	if (ch instanceof Creature && !cmd.allowCreature()) {
	    ch.send("Criaturas n�o podem usar esse comando.");
	    return;
	}

	int minPos = cmd.getMinPos();
	int chPos  = ch.getPosition();
	if (chPos < minPos) {
	    switch(chPos) {
	    case Char.P_DISABLED:
		ch.send("Sua execu��o de comandos est� desabilitada.");
		break;
	    case Char.P_DEAD:
		ch.send("Sinto muito, mas voc� est� morto.");
		break;
	    case Char.P_UNCONSCIOUS:
		ch.send("Voc� est� inconsciente.");
		break;
	    case Char.P_INCAPACITATED:
		ch.send("Voc� tenta, mas seu corpo n�o responde.");
		break;
	    case Char.P_ASLEEP:
		ch.send("Que tal acordar primeiro ?");
		break;
	    case Char.P_RESTING:
		ch.send("N�o, voc� est� relaxado demais pra isso.");
		break;
	    case Char.P_SITTING:
		ch.send("N�o � poss�vel fazer isso sentado.");
		break;
	    case Char.P_STANDING:
		ch.send("Seria preciso mais adrenalina.");
		break;
	    case Char.P_AWARE:
		ch.send("Voc� s� pode fazer isso em batalha.");
		break;
	    case Char.P_FIGHTING:
		Log.warn(ch.getName() + " (posi��o: " + chPos + ") n�o p�de executar comando '" + cmd.getName() + "' (posi��o m�nima: " + minPos + ")");
		break;
	    default:
		Log.warn(ch.getName() + " com posi��o inv�lida (" + chPos + ") invocando comando `" + cmd.getName() + "' (posi��o m�nima: " + minPos + ")");
	    }

	    return;
	}

	/*
	// aciona ganchos de todas as criaturas da sala para ver
	// se alguma proibe o comando
	if (Hook.apply(ch.getPlace(), ch, HookTable.TRIGGERS, cmd, toker,
		       word, line) == 0)
	*/
	cmd.execute(ch, toker, word);

    }

  Command(String name, int mRank, int mPos, int opt, int param, String syn) {
    theName   = name;
    minRank   = mRank;
    minPos    = mPos;
    options   = opt;
    parameter = param;
    syntax    = syn;
  }

    Command(String name, int mRank, int mPos, int opt, int param) {
	this(name, mRank, mPos, opt, param, null);
    }

    Command(String name, int mRank, int mPos, int opt, String syn) {
	this(name, mRank, mPos, opt, PAR_NONE, syn);
    }

    Command(String name, int mRank, int mPos, int opt) {
	this(name, mRank, mPos, opt, PAR_NONE, null);
    }

    String getName() {
      return theName;
    }

    boolean hasName(String name) {
      return getName().startsWith(name);
    }

    public int getMinRank() {
      return minRank;
    }

    public int getMinPos() {
	return minPos;
    }

    boolean isPriviledged() {
	return getMinRank() >= Char.R_ADMIN;
    }

    int getParameter() {
	return parameter;
    }

    String getSyntax() {
	return syntax;
    }

    void setSyntax(String syn) {
	syntax = syn;
    }

    void sendSyntax(Char ch) {
	ch.send("Sintaxe: " + getSyntax());
    }

    String showOptions() {
	String opt = "";
	if (allowPlayer())
	    opt += "jogador ";
	if (allowCreature())
	    opt += "criatura ";
	return opt;
    }

    void sendProperties(Char ch) {
	ch.send("Nome: '" + theName + "'" + Separators.NL +
		"Posto m�nimo: " + Char.getRankName(minRank) + " [" + minRank + "]" + Separators.NL +
		"Posi��o m�nima: " + Char.getPositionLabel(minPos, false) + " [" + minPos + "]" + Separators.NL +
		"Op��es: " + showOptions() + "[" + options + "]" + Separators.NL +
		"Par�metro: " + parameter);
    }

    boolean allowPlayer() {
	return Bit.isSet(options, OPT_PLR);
    }

    boolean allowCreature() {
	return Bit.isSet(options, OPT_CRT);
    }

    abstract void execute(Char aChar, CommandTokenizer toker, String cmd);
}

