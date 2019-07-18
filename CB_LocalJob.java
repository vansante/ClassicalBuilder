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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CB_LocalJob extends CB_Job {
	private String workingDir;

	private static final String PREFIX = "CBJob_";
	private static final String INPUTNAME = "_Input.cbs";
	private static final String OUTPUTNAME = "_Output.cbs";
	private static final String RESULTNAME = "_Result.txt";

	private static final Pattern filePattern = Pattern.compile("^\\Q" + PREFIX + "\\E(.+)\\Q" + INPUTNAME + "\\E$");
	private static final Pattern timePattern = Pattern.compile("^\\Q#  On '\\E[^']+\\' \\(([\\d]+)\\)$", Pattern.MULTILINE);
	private static final Pattern finishedPattern = Pattern.compile("^Dynamical simulation has finished:[ ]+$", Pattern.MULTILINE);

	public CB_LocalJob(String workingDir, int id, String name, String status, long time) {
		super(id, name, status, time);
		this.workingDir = workingDir;
	}
	public int getType() {
		return CB_Job.LOCAL;
	}
	public boolean hasInput() {
		return new File(workingDir + PREFIX + super.getName() + INPUTNAME).exists();
	}
	public boolean hasOutput() {
		return new File(workingDir + PREFIX + super.getName() + OUTPUTNAME).exists();
	}
	public boolean hasResult() {
		return new File(workingDir + PREFIX + super.getName() + RESULTNAME).exists();
	}
	public String getInput() throws IOException {
		if (new File(workingDir + PREFIX + super.getName() + INPUTNAME).exists()) {
			return CB_Tools.readFile(workingDir + PREFIX + super.getName() + INPUTNAME);
		}
		throw new IOException("The jobs inputfile was not found");
	}
	public String getOutput() throws IOException {
		if (this.hasOutput()) {
			return CB_Tools.readFile(workingDir + PREFIX + super.getName() + OUTPUTNAME);
		}
		throw new IOException("The jobs outputfile was not found");
	}
	public String getResult() throws IOException {
		if (this.hasResult()) {
			return CB_Tools.readFile(workingDir + PREFIX + super.getName() + RESULTNAME);
		}
		throw new IOException("The jobs resultfile was not found");
	}
	public static void removeJob(String workingDir, String name) throws IOException {
		File input = new File(workingDir + PREFIX + name + INPUTNAME);
		if (input.exists() && !input.delete()) {
			throw new IOException("Could not delete file '" + workingDir + PREFIX + name + INPUTNAME + "'.");
		}
		File output = new File(workingDir + PREFIX + name + OUTPUTNAME);
		if (output.exists() && !output.delete()) {
			throw new IOException("Could not delete file '" + workingDir + PREFIX + name + OUTPUTNAME + "'.");
		}
		File result = new File(workingDir + PREFIX + name + RESULTNAME);
		if (result.exists() && !result.delete()) {
			throw new IOException("Could not delete file '" + workingDir + PREFIX + name + RESULTNAME + "'.");
		}
	}
	public static void submitJob(String workingDir, String executable, String input, String name) throws IOException {
		BufferedWriter writer = null;
		if (!(new File(workingDir).exists())) {
			throw new IOException("The specified jobs directory does not exist!");
		}
		if (!(new File(executable).exists())) {
			throw new IOException("The specified executable does not exist!");
		}
		// See if theres already a job with this name, and if there is, number it
		File file = new File(workingDir + PREFIX + name + INPUTNAME);
		if (file.exists()) {
			String newName = name;
			int nr = 2;
			while (file.exists()) {
				newName = name + "_" + nr;
				file = new File(workingDir + PREFIX + newName + INPUTNAME);
				nr++;
			}
			System.out.println("Renaming local job '" + name + "' to '" + newName + "'.");
			name = newName;
		}
		try {
			writer = new BufferedWriter(new FileWriter(workingDir + PREFIX + name + INPUTNAME, false));
			writer.write(input);
		} catch (IOException e) {
			throw new IOException("Could not write input file '" + workingDir + PREFIX + name + INPUTNAME + "'.");
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {}
			}
		}
		try {
			Runtime.getRuntime().exec(
				new String[] {
					executable,
					workingDir + PREFIX + name + INPUTNAME,
					workingDir + PREFIX + name + OUTPUTNAME,
					workingDir + PREFIX + name + RESULTNAME
				}
			);
		} catch (Exception e) {
			throw new IOException("There was an error while trying to run the process.");
		}
	}
	public static List<CB_Job> getJobs(String workingDir) throws IOException {
		File dir = new File(workingDir);
		String[] files = dir.list();
		ArrayList<CB_Job> jobs = new ArrayList<CB_Job>();
		if (files == null) {
			throw new IOException("The specified jobs directory does not exist!");
		}
		int id = 1;
		Matcher matcher, finishedMatcher, timeMatcher;
		for (int i = 0; i < files.length; i++) {
			matcher = filePattern.matcher(files[i]);
			if (matcher.find()) {
				String status = "Unknown";
				long time = 0;
				File resultFile = new File(workingDir + PREFIX + matcher.group(1) + RESULTNAME);
				if (resultFile.exists()) {
					finishedMatcher = finishedPattern.matcher(CB_Tools.readFile(workingDir + PREFIX + matcher.group(1) + RESULTNAME));
					if (finishedMatcher.find()) {
						status = "Finished";
					} else {
						status = "Not finished";
					}
				}
				timeMatcher = timePattern.matcher(CB_Tools.readFile(workingDir + PREFIX + matcher.group(1) + INPUTNAME));
				if (timeMatcher.find()) {
					try {
						time = Long.parseLong(timeMatcher.group(1));
					} catch (NumberFormatException e) {
						time = -1;
					}
				} else {
					time = -1;
				}
				CB_LocalJob job = new CB_LocalJob(workingDir, id, matcher.group(1), status, time);
				jobs.add(job);
				id++;
			}
		}
		return jobs;
	}
}