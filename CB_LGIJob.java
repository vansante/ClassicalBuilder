/**
 * Copyright © 2006-2008 Paul van Santen & Erik Kerkvliet,
 *
 * This file is part of ClassicalBuilder.
 *
 * ClassicalBuilder is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ClassicalBuilder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ClassicalBuilder; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * http://www.gnu.org/licenses/gpl.txt
**/

package ClassicalBuilder;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class CB_LGIJob extends CB_Job {

	public CB_LGIJob(int id, String name, String status, long time) {
		super(id, name, status, time);
	}
	public int getType() {
		return CB_Job.LGI;
	}
	public boolean hasInput() {
		return false;
	}
	public boolean hasOutput() {
		return false;
	}
	public boolean hasResult() {
		return false;
	}
	public String getInput() {
		return null;
	}
	public String getOutput() {
		return null;
	}
	public String getResult() {
		return null;
	}
	public static boolean removeJob(int id) {
		return true;
	}
	public static String submitJob(String input, String name) {

		return null;
	}
	public static List<CB_Job> getJobs() {
		ArrayList<CB_Job> jobs = new ArrayList<CB_Job>();

		return jobs;
	}
}