/*
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

import java.util.ArrayList;
import java.io.IOException;

public abstract class CB_Job {

	private int id;
	private String name = "Unnamed", status = "Unknown";
	// A long with the unix timestamp, -1 if unknown
	private long time;

	public static final int LOCAL = 0;
	public static final int LC = 1;
	public static final int LGI = 2;

	public static final String[] NAMES = {"Local", "Leiden Classical", "LGI" };

	public CB_Job(int id, String name, String status, long time) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.time = time;
	}
	// Set methods
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setTime(long time) {
		this.time = time;
	}
	// Get methods
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getStatus() {
		return status;
	}
	public long getTime() {
		return time;
	}
	public String getTypeString() {
		return NAMES[this.getType()];
	}
	// Abstract functions that have to be implemented by subclasses
	public abstract int getType();
	public abstract boolean hasInput();
	public abstract boolean hasOutput();
	public abstract boolean hasResult();
	public abstract String getInput() throws IOException;
	public abstract String getOutput() throws IOException;
	public abstract String getResult() throws IOException;
}