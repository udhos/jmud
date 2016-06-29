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

package jmud.ability;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import jmud.Char;
import jmud.Global;
import jmud.job.Job;
import jmud.job.JobTable;
import jmud.util.Separators;
import jmud.util.log.Log;
import jmud.util.file.LineReader;
import jmud.jgp.Enchainer;
import jmud.jgp.NameMatcher;
import jmud.jgp.NameExtractor;
import jmud.jgp.NameDependenceExtractor;
import jgp.container.Set;
import jgp.algorithm.Searcher;
import jgp.adaptor.Enumerator;

public class AbilityTable {

    static final Ability AB_MEDITATION   = new Ability("meditacao");
    static final Ability AB_PEACE        = new Ability("serenidade");
    static final Ability AB_FOCUS        = new Ability("foco");
    static final Ability AB_PERSEVERANCE = new Ability("perseveranca");
    static final Ability AB_DISTURB      = new Ability("disturbio");
    static final Ability AB_DIMMAK       = new Ability("dim-mak");
    static final Ability AB_CHI          = new ChiAbility("chi");
    static final Ability AB_PUNCH        = new Ability("soco");
    static final Ability AB_KICK         = new Ability("chute");
    static final Ability AB_SABRE        = new Ability("esgrima");

    static final Ability abilityList[] = {
	AB_MEDITATION,
	AB_PEACE,
	AB_FOCUS,
	AB_DISTURB,
	AB_DIMMAK,
	AB_CHI,
	AB_PERSEVERANCE,
	AB_PUNCH,
	AB_KICK,
	AB_SABRE
    };

    public static Ability findAbilityByName(String name) {
	return (Ability) Searcher.linearSearch(new Enumerator(abilityList), new NameMatcher(name));
    }

    public static void sendAbilityList(Char rcpt, String sep) {
	Enchainer.headedList("Nenhuma", rcpt, new Enumerator(abilityList), new NameExtractor(), sep);
    }

    public static void sendDependenceList(Char rcpt, String sep) {
	Enchainer.headedList("Nenhuma", rcpt, new Enumerator(abilityList), new NameDependenceExtractor(new NameExtractor(), ": ", " "), sep);
    }

    static void loadAbilityDependences(FileReader fr, String fileName) {

	LineReader reader = null;
	try {
	    reader = new LineReader(fr);
	}
	catch (IOException e) {
	    Log.err("Falha de E/S ao iniciar leitura do arquivo de dependências de habilidades: " + fileName);
	    return;
	}

	/*
	 * Carrega dependencias.
	 */
	Log.debug("Dependências de habilidades: preenchendo tabela de dispersão");
	Hashtable abilities = new Hashtable();
	try {
	    for (;;) {
		String line = reader.readLine();
		if (line == null)
		    break;
		if (line.length() == 0 || line.startsWith(Separators.REM))
		    continue;
		StringTokenizer toker = new StringTokenizer(line, ":");
		if (!toker.hasMoreTokens()) {
		    Log.err("Formato inválido no arquivo de dependências de habilidades na linha (" + reader.getLineNumber() + "): '" + line + "'");
		    continue;
		}
		String abName = toker.nextToken();
		Ability abil = findAbilityByName(abName);
		if (abil == null) {
		    Log.err("Habilidade desconhecida '" + abName + "' no arquivo de dependências, linha (" + reader.getLineNumber() + "): '" + line + "'");
		    continue;
		}
		if (abilities.containsKey(abName)) {
		    Log.err("Habilidade duplicada '" + abName + "' no arquivo de dependências, linha (" + reader.getLineNumber() + "): '" + line + "'");
		    continue;
		}
		abilities.put(abName, abil);
		if (!toker.hasMoreTokens())
		    continue;
		String depList = toker.nextToken();
		for (StringTokenizer tkr = new StringTokenizer(depList);
		     tkr.hasMoreElements(); ) {
		    String depName = tkr.nextToken();
		    Ability dep = findAbilityByName(depName);
		    if (dep == null) {
			Log.err("Habilidade desconhecida '" + depName + "' no arquivo de dependências, linha (" + reader.getLineNumber() + "): '" + line + "'");
			continue;
		    }
		    abil.addDependence(dep);
		}
	    }
	}
	catch (IOException e) {
	    Log.err("Falha de E/S ao ler arquivo de dependências de habilidades: " + fileName);
	    return;
	}

	/*
	 * Resolve dependencias.
	 */
	Log.debug("Dependências de habilidades: resolvendo");
	boolean repeat;
	do {
	    repeat = false;
	    for (Enumeration abList = abilities.elements();
		 abList.hasMoreElements(); ) {
		Ability abil = (Ability) abList.nextElement();
		Enumeration depList = abil.getDependenceList();
		boolean done = true;
		while (depList.hasMoreElements()) {
		    Ability dep = (Ability) depList.nextElement();
		    if (abilities.containsKey(dep.getName())) {
			done = false;
			break;
		    }
		}
		if (done) {
		    abilities.remove(abil.getName());
		    repeat = true;
		}
	    }
	} while (repeat);

	/*
	 * Aponta pendencias.
	 */
	Log.debug("Dependências de habilidades: verificando pendências");
	for (Enumeration abList = abilities.elements();
	     abList.hasMoreElements(); ) {
	    Ability abil = (Ability) abList.nextElement();
	    Log.err("Habilidade pendente: '" + abil.getName() + "'");
	}
    }

    static void loadAbilityDependences() {

	String fileName = Global.getConfig().getAbilityDepFileName();
	if (fileName == null)
	    Log.err("Arquivo de dependências de habilidades não definido na configuração");
	else {
	    Log.info("Carregando dependências de habilidades: " + fileName);
	    try {
		FileReader fr = new FileReader(fileName);
		loadAbilityDependences(fr, fileName);
		fr.close();
	    }
	    catch (FileNotFoundException e) {
		Log.err("Arquivo de dependências de habilidades não encontrado: " + fileName);
	    }
	    catch (IOException e) {
		Log.err("Falha de E/S: " + fileName);
	    }
	}
    }

    static void loadAbilityJobs(FileReader fr, String fileName) {

	LineReader reader = null;
	try {
	    reader = new LineReader(fr);
	}
	catch (IOException e) {
	    Log.err("Falha de E/S ao iniciar leitura do arquivo de profissões das habilidades: " + fileName);
	    return;
	}

	try {
	    for (;;) {
		String line = reader.readLine();
		if (line == null)
		    break;
		if (line.length() == 0 || line.startsWith(Separators.REM))
		    continue;

		StringTokenizer toker = new StringTokenizer(line, ":");
		if (!toker.hasMoreTokens()) {
		    Log.err("Formato inválido no arquivo de profissões das habilidades na linha (" + reader.getLineNumber() + "): '" + line + "'");
		    continue;
		}
		String abName = toker.nextToken();
		Ability abil = findAbilityByName(abName);
		if (abil == null) {
		    Log.err("Habilidade desconhecida '" + abName + "' no arquivo de profissões, linha (" + reader.getLineNumber() + "): '" + line + "'");
		    continue;
		}
		if (!toker.hasMoreTokens()) {
		    Log.err("Habilidade '" + abName + "' não associada a profissões, linha (" + reader.getLineNumber() + "): '" + line + "'");
		    continue;
		}
		String jobList = toker.nextToken();
		for (StringTokenizer tkr = new StringTokenizer(jobList);
		     tkr.hasMoreTokens(); ) {
		    String jobName = tkr.nextToken();
		    Job jb = JobTable.findJobByName(jobName);
		    if (jb == null) {
			Log.err("Profissão '" + jobName + "' desconhecida, linha (" + reader.getLineNumber() + "): '" + line + "'");
			continue;
		    }
		    if (abil.findJob(jb) != null) {
			Log.err("Profissão '" + jobName + "' redefinida para habilidade '" + abName + "', linha (" + reader.getLineNumber() + "): '" + line + "'");
			continue;
		    }
		    abil.addJob(jb);
		}
	    }
	}
	catch (IOException e) {
	    Log.err("Falha de E/S ao ler arquivo de profissões de habilidades: " + fileName);
	    return;
	}

    }

    static void loadAbilityJobs() {

	String fileName = Global.getConfig().getAbilityJobsFileName();
	if (fileName == null)
	    Log.err("Arquivo de profissões das habilidades não definido na configuração");
	else {
	    Log.info("Carregando profissões das habilidades: " + fileName);
	    try {
		FileReader fr = new FileReader(fileName);
		loadAbilityJobs(fr, fileName);
		fr.close();
	    }
	    catch (FileNotFoundException e) {
		Log.err("Arquivo de profissões das habilidades não encontrado: " + fileName);
	    }
	    catch (IOException e) {
		Log.err("Falha de E/S: " + fileName);
	    }
	}
    }

    static {
	loadAbilityDependences();
	loadAbilityJobs();
    }

}
