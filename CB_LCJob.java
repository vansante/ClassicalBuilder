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


// Warning: This is a really dirty hackish class, and should probably be rewritten in a decent manner...

package ClassicalBuilder;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class CB_LCJob extends CB_Job {

	private String accountKey = "";

	public static final String GORLAEUS_SUBMIT_URL = "http://boinc.gorlaeus.net/queue_new_job_form_action.php";
	public static final String GORLAEUS_LIST_URL = "http://boinc.gorlaeus.net/queue_show_queue.php";
	public static final String GORLAEUS_DELETE_URL = "http://boinc.gorlaeus.net/queue_remove_job.php?workunitid=";
	public static final String GORLAEUS_DETAIL_URL = "http://boinc.gorlaeus.net/queue_show_job.php?workunitid=";
	public static final String GORLAEUS_INPUT_URL = "http://boinc.gorlaeus.net/queue_show_job.php?workunitid=";

	public CB_LCJob(int id, String name, String status, long time, String accountKey) {
		super(id, name, status, time);
		this.accountKey = accountKey;
	}
	public int getType() {
		return CB_Job.LC;
	}
	public boolean hasInput() {
		String pattern;
		pattern  = "<tr><td width=40% class=fieldname>Job input file:</td><td class=fieldvalue>";
		pattern += "<a href=\"([^<>]+)\">";
		pattern += " ([^<>]+)</a></td></tr>";
		return this.getJobFile(GORLAEUS_DETAIL_URL + this.getId(), pattern, 1) != null;
	}
	public boolean hasOutput() {
		String pattern;
		pattern  = "<tr><td width=40% class=fieldname>Output file 1: </td><td class=fieldvalue>";
		pattern += "<a href=\"([^<>]+)\">";
		pattern += "([^<>]+)</a></td></tr>";
		return this.getJobFile(GORLAEUS_DETAIL_URL + this.getId(), pattern, 1) != null;
	}
	public boolean hasResult() {
		String pattern;
		pattern  = "<tr><td width=40% class=fieldname>Output file 2: </td><td class=fieldvalue>";
		pattern += "<a href=\"([^<>]+)\">";
		pattern += "([^<>]+)</a></td></tr>";
		return this.getJobFile(GORLAEUS_DETAIL_URL + this.getId(), pattern, 1) != null;
	}
	public String getInput() {
		String pattern;
		pattern  = "<tr><td width=40% class=fieldname>Job input file:</td><td class=fieldvalue>";
		pattern += "<a href=\"([^<>]+)\">";
		pattern += " ([^<>]+)</a></td></tr>";
		return this.getJobFile(GORLAEUS_DETAIL_URL + this.getId(), pattern, 1);
	}
	public String getOutput() {
		String pattern;
		pattern  = "<tr><td width=40% class=fieldname>Output file 1: </td><td class=fieldvalue>";
		pattern += "<a href=\"([^<>]+)\">";
		pattern += "([^<>]+)</a></td></tr>";
		return this.getJobFile(GORLAEUS_DETAIL_URL + this.getId(), pattern, 1);
	}
	public String getResult() {
		String pattern;
		pattern  = "<tr><td width=40% class=fieldname>Output file 2: </td><td class=fieldvalue>";
		pattern += "<a href=\"([^<>]+)\">";
		pattern += "([^<>]+)</a></td></tr>";
		return this.getJobFile(GORLAEUS_DETAIL_URL + this.getId(), pattern, 1);
	}
	public static boolean removeJob(String accountKey, int id) {
		String page = htmlRequest(GORLAEUS_DELETE_URL + id, new String[][] {}, true, false, accountKey);
		if (page != null) {
			if (page.matches("^.*<td class=fieldvalue>deleted</td>.*$")) {
				return true;
			}
		}
		return false;
	}
	public static String submitJob(String accountKey, String input, String name) {
		String[][] post = {
			{ "application", "2" },
			{ "name", name },
			{ "input", input },
			{ "fops", "33211786946400" },
			{ "mem", "134217728" },
			{ "disk", "536870912" }
		};
		String page = htmlRequest(GORLAEUS_SUBMIT_URL, post, true, false, accountKey);
		if (page != null) {
			if (page.matches("^.*Your job has been submitted.*$")) {
				return null;
			} else {
				String pattern;
				pattern = "<table border=1 cellpadding=5 width=100%><tr><td class=heading colspan=2>";
				pattern += "<font color='red'><b>([\\sa-zA-Z0-9:/_%-+#@=.,!?]+)</b></font></td></tr>";
				Pattern submitPattern = Pattern.compile(pattern);
				Matcher matcher = submitPattern.matcher(page);
				if (matcher.find()) {
					return matcher.group(1);
				}
			}
		}
		return "Unknown error";
	}
	public static List<CB_Job> getJobs(String accountKey) {
		ArrayList<CB_Job> jobs = new ArrayList<CB_Job>();

		String page = htmlRequest(GORLAEUS_LIST_URL, new String[][] {}, true, false, accountKey);
		if (page == null) {
			return jobs;
		} else if (page.matches("^.*<title>Please log in</title>.*$")) {
			return jobs;
		}
		String pattern;
		pattern  = "<tr><td width=20% valign=top>(\\d+)</td><td width=20%>([\\sa-zA-Z0-9:/]+)</td>";
		pattern += "<td width=20% >(<font color='(green|red|blue)'>)?(<b>)?(\\w+)(</b>)?(</font>)?</td>";
		pattern += "<td width=%20><a href=\"queue_show_job.php\\?workunitid=(\\d+)\">([^<>]+)</a></td>";
		pattern += "<td width=20%><a href=workunit.php\\?wuid=(\\d+)>(\\d+)</a></td></tr>";

		Pattern jobPattern = Pattern.compile(pattern);

		Matcher matcher = jobPattern.matcher(page);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz");

		while (matcher.find()) {
			long date = -1;
			try {
				date = dateFormat.parse(matcher.group(2)).getTime();
			} catch (ParseException e) {}

			CB_LCJob job = new CB_LCJob(
				Integer.parseInt(matcher.group(9)),
				matcher.group(10),
				matcher.group(6),
				date,
				accountKey
			);
			jobs.add(job);
		}
		return jobs;
	}
	public static int testConnection(String accountKey) {
		String page = htmlRequest(GORLAEUS_LIST_URL, new String[][] {}, true, false, accountKey);
		if (page == null) {
			return 1;
		} else if (page.matches("^.*<title>Please log in</title>.*$")) {
			return 2;
		}
		return 0;
	}
	private boolean isClassicalJob(String page) {
		String pattern = "Job application: </td><td class=fieldvalue>Classical";
		return (page.indexOf(pattern) != -1);
	}
	private String getJobFile(String url, String pattern, int number) {
		String page = htmlRequest(url, new String[][] {}, true, false, accountKey);
		if (!this.isClassicalJob(page)) {
			return null;
		}
		if (page != null) {
			Pattern jobPattern = Pattern.compile(pattern);
			Matcher matcher = jobPattern.matcher(page);

			String address;
			if (matcher.find()) {
				address = matcher.group(number);
			} else {
				return null;
			}
			// If its a relative url with no http and address, then add them manually:
			if (!address.toLowerCase().startsWith("http://")) {
				String[] pieces = url.split("/");
				pieces[pieces.length - 1] = address;
				address = "";
				for (int i = 0; i < pieces.length; i++) {
					address += pieces[i];
					if (i < pieces.length - 1) {
						address += "/";
					}
				}
			}
			page = htmlRequest(address, new String[][] {}, true, true, accountKey);
			if (page != null) {
				return page;
			}
		}
		return null;
	}
	private static String htmlRequest(String address, String[][] post, boolean getOutput, boolean newlines, String accountKey) {
		URLConnection connection;
		try {
			String data = "";
			// Send data
			// Create a URLConnection object for a URL
			URL url = new URL(address);
			connection = url.openConnection();
			connection.setRequestProperty("Cookie", "auth=" + accountKey);
			connection.setDoOutput(getOutput);
			OutputStreamWriter oswr = new OutputStreamWriter(connection.getOutputStream());
			if (post != null && post.length > 0) {
				for (int i = 0; i < post.length; i++) {
					data += URLEncoder.encode(post[i][0], "UTF-8") + "=" + URLEncoder.encode(post[i][1], "UTF-8");
					if (i < post.length - 1) {
						data += "&";
					}
				}
				oswr.write(data);
			}
			oswr.flush();
			oswr.close();
		} catch (Exception e) {
			System.out.println("Error: Could not connect to: " + address);
			return null;
		}
		String page = "";
		if (getOutput) {
			try {
				// Get the response
				BufferedReader brd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				StringBuilder pageBuf = new StringBuilder();
				while ((line = brd.readLine()) != null) {
					pageBuf.append(line);
					if (newlines) {
						pageBuf.append("\n");
					}
				}
				page = pageBuf.toString();
				brd.close();
			} catch (Exception e) {
				System.out.println("Error: Could not get html output of: " + GORLAEUS_LIST_URL);
				return null;
			}
		}
		return page;
	}
}