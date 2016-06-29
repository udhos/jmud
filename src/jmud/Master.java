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

import java.util.Enumeration;

import jgp.algorithm.Searcher;
import jgp.container.Set;

import jmud.job.Job;
import jmud.job.JobTitle;
import jmud.job.JobTable;
import jmud.ability.Ability;
import jmud.ability.AbilityTable;
import jmud.ability.Proficiency;
import jmud.util.log.Log;
import jmud.jgp.Enchainer;
import jmud.jgp.NameMatcher;
import jmud.jgp.StringExtractor;
import jmud.parser.SyntaxNode;
import jmud.parser.SyntaxMap;
import jmud.parser.SyntaxLValue;
import jmud.parser.SyntaxNumber;
import jmud.parser.SyntaxException;

public class Master implements UniqueId {

    private int creatureId;
    private Set masteredAbilities = new Set();
    private Set masteredJobs      = new Set();

    Master(int zoneBase, SyntaxMap map, String identPrefix, String identInc) throws SyntaxException {

	String creatureIdKey = "criatura_id";
	SyntaxNode creatureIdValue = (SyntaxNode) map.get(creatureIdKey);
	if (creatureIdValue == null)
	    throw new SyntaxException("ID não especificado para criatura mestre");

	if (!(creatureIdValue instanceof SyntaxLValue))
	    throw new SyntaxException("ID especificado incorretamente para criatura mestre");

	String value = ((SyntaxLValue) creatureIdValue).getString();
	try {
	    creatureId = Integer.parseInt(value) + zoneBase;
	}
	catch (NumberFormatException e) {
	    throw new SyntaxException("ID não numérico para criatura mestre '" + creatureIdKey + "' = '" + value + "'");
	}

	SyntaxNode masteredAbilitiesValue = map.get("habilidades_ensinadas");
	if (masteredAbilitiesValue != null) {
	    if (masteredAbilitiesValue instanceof SyntaxMap) {
		SyntaxMap abMap = (SyntaxMap) masteredAbilitiesValue;
		for (Enumeration e = abMap.keys(); e.hasMoreElements(); ) {
		    SyntaxLValue key = (SyntaxLValue) e.nextElement();
		    String abName = key.getString();
		    Ability ab = AbilityTable.findAbilityByName(abName);
		    if (ab == null) {
			Log.err("Habilidade não encontrada: '" + abName + "'");
			continue;
		    }
		    SyntaxNode abNode = abMap.get(abName);
		    if (!(abNode instanceof SyntaxNumber)) {
			Log.err("Nível não especificado para habilidade '" + abName + "':");
			abNode.show(Log.getStream(), identPrefix, identInc);
			continue;
		    }
		    String abLevel = ((SyntaxNumber) abNode).getString();
		    int level = -1;
		    try {
			level = Integer.parseInt(abLevel);
		    }
		    catch (NumberFormatException ex) {
			Log.err("Nível inválido para habilidade '" + abName + "':" + abLevel);
			continue;
		    }
		    masteredAbilities.insert(new Proficiency(ab, level));
		}
	    }
	}

	SyntaxNode masteredJobsValue = map.get("profissoes_ensinadas");
	if (masteredJobsValue != null) {
	    if (masteredJobsValue instanceof SyntaxMap) {
		SyntaxMap jbMap = (SyntaxMap) masteredJobsValue;
		for (Enumeration e = jbMap.keys(); e.hasMoreElements(); ) {
		    SyntaxLValue key = (SyntaxLValue) e.nextElement();
		    String jbName = key.getString();
		    Job jb = JobTable.findJobByName(jbName);
		    if (jb == null) {
			Log.err("Profissão não encontrada: '" + jbName + "'");
			continue;
		    }
		    SyntaxNode jbNode = jbMap.get(jbName);
		    if (!(jbNode instanceof SyntaxNumber)) {
			Log.err("Nível não especificado para profissão '" + jbName + "':");
			jbNode.show(Log.getStream(), identPrefix, identInc);
			continue;
		    }
		    String jbLevel = ((SyntaxNumber) jbNode).getString();
		    int level = -1;
		    try {
			level = Integer.parseInt(jbLevel);
		    }
		    catch (NumberFormatException ex) {
			Log.err("Nível inválido para profissão '" + jbName + "':" + jbLevel);
			continue;
		    }
		    masteredJobs.insert(new JobTitle(jb, level));
		}
	    }
	}

    }

    public int getId() {
	return creatureId;
    }

    public Enumeration getAbilities() {
	return masteredAbilities.elements();
    }

    public Enumeration getJobs() {
	return masteredJobs.elements();
    }

    public JobTitle findJob(String name) {
	return (JobTitle) Searcher.linearSearch(masteredJobs, new NameMatcher(name));
    }

    public void sendSheet(Char rcpt) {
	rcpt.send("MESTRE [" + creatureId + "]\nProfissões: ");
	Enchainer.headedList("Nenhuma", rcpt, masteredJobs.elements(), new StringExtractor(), ", ");
	rcpt.send("Habilidades: ");
	Enchainer.headedList("Nenhuma", rcpt, masteredAbilities.elements(), new StringExtractor(), ", ");
    }

}

