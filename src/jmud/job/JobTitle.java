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

package jmud.job;

import jmud.Named;

public class JobTitle implements Named, JobReference {

    private Job jobRef;
    private int jobLevel;

    public JobTitle(Job ref, int level) {
	jobRef   = ref;
	jobLevel = level;
    }

    public Job getJob() {
	return jobRef;
    }

    public String getName() {
	return getJob().getName();
    }

    public int getLevel() {
	return jobLevel;
    }

    public void setLevel(int level) {
	jobLevel = level;
    }

    public boolean hasName(String name) {
	return getJob().hasName(name);
    }

    public String toString() {
	return getName() + "=" + getLevel();
    }
}
