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

import jmud.Char;
import jmud.Named;
import jmud.DependenceNode;
import jmud.job.Job;
import jmud.jgp.NameMatcher;
import jmud.jgp.ObjectMatcher;
import jgp.container.Set;
import jgp.algorithm.Searcher;

public class Ability implements Named, DependenceNode {

    private String abilityName;
    private Set    abilityDep      = new Set();
    private Set    abilityJobs     = new Set();

    Ability (String name) {
	abilityName = name;
    }

    public String getName() {
	return abilityName;
    }

    public boolean hasName(String name) {
	return getName().equals(name);
    }

    public Enumeration getDependenceList() {
	return abilityDep.elements();
    }

    void addDependence(Ability ab) {
	abilityDep.insert(ab);
    }

    Enumeration getJobList() {
	return abilityJobs.elements();
    }

    Job findJob(Job jb) {
	return (Job) Searcher.linearSearch(abilityJobs, new ObjectMatcher(jb));
    }

    Job findJob(String name) {
	return (Job) Searcher.linearSearch(abilityJobs, new NameMatcher(name));
    }

    void addJob(Job jb) {
	abilityJobs.insert(jb);
    }

    public void execute(Char ch, Ability ab, int abLevel, String arg) {
	ch.send("Habilidade não-implementada: '" + ab.getName() + "', nível: " + abLevel + ", argumentos: '" + arg + "'");
    }

    int getExperienceCost(int level) {
	return level;
    }
}

