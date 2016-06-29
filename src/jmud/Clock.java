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

import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Clock {

    private int            localStandardTime;
    private String         ids[];
    private SimpleTimeZone localTimeZone;
    private Calendar       calendar;

    public Clock(int lst) {
	localStandardTime = lst;
	ids               = TimeZone.getAvailableIDs(localStandardTime);
	localTimeZone     = new SimpleTimeZone(localStandardTime, ids[0]);
	calendar          = new GregorianCalendar(localTimeZone);
    }

    public Calendar getCalendar(Date dt) {
	calendar.setTime(dt);
	return calendar;
    }

    public Date getDate() {
	return new Date();
    }

    public Calendar getCalendar() {
	return getCalendar(getDate());
    }

}
