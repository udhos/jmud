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
import jmud.Master;
import jmud.Keywords;
import jmud.job.Job;
import jmud.job.JobTitle;
import jmud.jgp.Enchainer;
import jmud.jgp.StringExtractor;
import jmud.util.StrUtil;

class Learn extends Command {

    Learn(String name, int mRank, int mPos, int opt) {
	super(name, mRank, mPos, opt);
    }

    public void execute(Char aChar, CommandTokenizer toker, String cmd) {

	Object[] pair = aChar.getPlace().findMaster();
	if (pair == null) {
	    aChar.send("Mestre não encontrado.");
	    return;
	}

	Char ch = (Char) pair[0];
	Master mstr = (Master) pair[1];

	if (!toker.hasMoreTokens()) {
	    aChar.send("Profissões disponíveis:\n");
	    Enchainer.headedList("Nenhuma", aChar, mstr.getJobs(), new StringExtractor(), "\n");
	    aChar.send("Habilidades disponíveis:\n");
	    Enchainer.headedList("Nenhuma", aChar, mstr.getAbilities(), new StringExtractor(), "\n");
	    return;
	}

	String opt = toker.nextToken();

	if (Keywords.JOB.startsWith(opt)) {
	    Enumeration e = mstr.getJobs();

	    if (!toker.hasMoreTokens()) {
		aChar.send("PROFISSÃO MÁXIMO NÍVEL PRÓXIMO EXPERIÊNCIA\n" +
                           "--------- ------ ----- ------- -----------");
		while (e.hasMoreElements()) {
		    JobTitle jt      = (JobTitle) e.nextElement();
		    Job jb           = jt.getJob();
		    String jbName    = jb.getName();
		    int maxLevel     = jt.getLevel();
		    JobTitle knownJt = aChar.findJob(jb);

		    int knownLevel = -1;
                    if (knownJt != null)
			knownLevel = knownJt.getLevel();

		    int nextLevel = -1;
		    if (knownLevel < maxLevel)
			nextLevel = knownLevel + 1;

		    int expCost = -1;
		    if (nextLevel != -1)
			expCost = jb.getExperienceCost(nextLevel);

		    String knownLevelStr = (knownLevel == -1) ? "-" : String.valueOf(knownLevel);
		    String nextLevelStr = (nextLevel == -1) ? "-" : String.valueOf(nextLevel);
		    String expCostStr = (expCost == -1) ? "-" : String.valueOf(expCost);
		    aChar.send(StrUtil.leftPad(jbName, 9) + StrUtil.formatNumber(maxLevel, 7) + StrUtil.leftPad(knownLevelStr, 6) + StrUtil.leftPad(nextLevelStr, 8) + StrUtil.leftPad(expCostStr, 12));
		}
		return;
	    }

	    String jbName = toker.nextToken();
	    JobTitle jt = mstr.findJob(jbName);
	    if (jt == null) {
		aChar.send("Profissão desconhecida.");
		return;
	    }
	    Job jb = jt.getJob();
	    int maxLevel = jt.getLevel();

	    int knownLevel = -1;
	    JobTitle knownJt = aChar.findJob(jb);
	    if (knownJt != null)
		knownLevel = knownJt.getLevel();

	    if (knownLevel >= maxLevel) {
		aChar.send("Não é possível evoluir mais nessa profissão.");
		return;
	    }

	    int nextLevel = knownLevel + 1;
	    int expCost = jb.getExperienceCost(nextLevel);
	    int availableExp = aChar.getExp();

	    if (expCost > availableExp) {
		aChar.send("Experiência insuficiente.");
		return;
	    }

	    if (nextLevel == 0) {
		aChar.joinJob(jb, 0);
		aChar.send("Profissão aprendida.");
	    }
	    else {
		knownJt.setLevel(nextLevel);
		aChar.send("Profissão aperfeiçoada.");
	    }
	    aChar.setExp(availableExp - expCost);

	    return;
	}

	if (Keywords.ABILITY.startsWith(opt)) {
	    Enumeration e = mstr.getAbilities();

	    if (!toker.hasMoreTokens()) {
		return;
	    }

	    String abName = toker.nextToken();
	    return;
	}

	aChar.send("Aprender profissão ou habilidade?");
    }

}
