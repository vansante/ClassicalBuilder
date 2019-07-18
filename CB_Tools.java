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

import java.lang.reflect.Method;
import javax.swing.JOptionPane;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class CB_Tools {
	// Private; No instances allowed
	private CB_Tools() {}

	/////////////////////////////////////////////////////////
	//  Bare Bones Browser Launch                          //
	//  Version 1.5                                        //
	//  December 10, 2005                                  //
	//  Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
	//  Example Usage:                                     //
	//     String url = "http://www.centerkey.com/";       //
	//     BareBonesBrowserLaunch.openURL(url);            //
	//  Public Domain Software -- Free to Use as You Like  //
	/////////////////////////////////////////////////////////
	public static void openURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
				openURL.invoke(null, new Object[] {url});
			} else if (osName.startsWith("Windows")) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else { //assume Unix or Linux
				String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++) {
					if (Runtime.getRuntime().exec(new String[] {"which", browsers[count]}).waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else {
					Runtime.getRuntime().exec(new String[] {browser, url});
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error attempting to launch web browser:\n" + e.getLocalizedMessage());
		}
	}
	// Function that moves the array selection up or down in the array, with the specified amount
	public static int[] moveSelectionInArray(int[] array, int[] selection, boolean up, int amount) {
		int[] newArray = new int[array.length];
		if (selection.length > 0) {
			if (up && selection[0] - amount + 1 > 0) {
				// Move up
				for (int i = 0; i < array.length; i++) {
					if (i > selection[selection.length - 1] || i < selection[0] - amount) {
						newArray[i] = array[i];
					} else if (i > selection[selection.length - 1] - amount) {
						newArray[i] = array[i - selection.length];
					} else if (i > selection[selection.length - 1] - amount - selection.length) {
						newArray[i] = array[i + amount];
					}
				}
				return newArray;
			} else if (!up && selection[selection.length - 1] + amount < array.length) {
				// Move down
				for (int i = 0; i < array.length; i++) {
					if (i < selection[0] || i > selection[selection.length - 1] + amount) {
						newArray[i] = array[i];
					} else if (i < selection[0] + amount) {
						newArray[i] = array[i + selection.length];
					} else if (i < selection[0] + amount + selection.length) {
						newArray[i] = array[i - amount];
					}
				}
				return newArray;
			}
		}
		return array;
	}
	public static int[] moveInArray(int[] array, int from, int to) {
		if (from < to) {
			int temp = array[from];
			for (int i = 0; i < array.length; i++) {
				if (i >= from && i < to) {
					array[i] = array[i + 1];
				}
			}
			array[to] = temp;
		} else {
			int temp = array[to];
			for (int i = 0; i < array.length; i++) {
				if (i >= to && i < from) {
					array[i] = array[i + 1];
				}
			}
			array[from] = temp;
		}
		return array;
	}
	// Gets the new selection after calling the moveSelectionInArray function
	public static int[] getMoveSelectionArray(int[] selection, boolean up, int amount) {
		int[] newSelect = new int[selection.length];
		for (int i = 0; i < selection.length; i++) {
			if (up) {
				newSelect[i] = selection[i] - amount;
			} else {
				newSelect[i] = selection[i] + amount;
			}
		}
		return newSelect;
	}
	// Adds an element to an Integer array
	public static int[] arrayAdd(int[] array, int add) {
		int[] newArray = new int[array.length + 1];
		for (int i = 0; i < array.length; i++) {
			newArray[i] = array[i];
		}
		newArray[array.length] = add;
		return newArray;
	}
	// Removes an element from an Integer array
	public static int[] arrayRemove(int[] array, int remove) {
		if (remove >= 0 && remove < array.length) {
			int[] newArray = new int[array.length - 1];
			for (int i = 0; i < array.length; i++) {
				if (i < remove) {
					newArray[i] = array[i];
				} else if (i > remove) {
					newArray[i - 1] = array[i];
				}
			}
			return newArray;
		}
		return array;
	}
	// Searches the array for the specified item, returns the position when found, otherwise -1
	public static int arraySearch(int[] array, int item) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == item) {
				return i;
			}
		}
		return -1;
	}
	public static double arraySearch(double[] array, double item) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == item) {
				return i;
			}
		}
		return -1;
	}
	public static JSpinner createDoubleField(double current) {
		return CB_Tools.createDoubleField(current, -Double.MAX_VALUE, Double.MAX_VALUE);
	}
	public static JSpinner createDoubleField(double current, double min) {
		return CB_Tools.createDoubleField(current, min, Double.MAX_VALUE);
	}
	public static JSpinner createDoubleField(double current, double min, double max) {
		SpinnerModel model = new SpinnerNumberModel(current, min, max, 1.0);
		JSpinner spinner = new JSpinner(model);
		((JSpinner.NumberEditor) spinner.getEditor()).getFormat().setMinimumFractionDigits(1);
		((JSpinner.NumberEditor) spinner.getEditor()).getFormat().setMaximumFractionDigits(12);
		return spinner;
	}
	public static JSpinner createIntField(int current) {
		return CB_Tools.createIntField(current, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	public static JSpinner createIntField(int current, int min) {
		return CB_Tools.createIntField(current, min, Integer.MAX_VALUE);
	}
	public static JSpinner createIntField(int current, int min, int max) {
		SpinnerModel model = new SpinnerNumberModel(current, min, max, 1);
		JSpinner spinner = new JSpinner(model);
		return spinner;
	}
	public static JMenuItem createMenuItem(JPopupMenu target, String title, String icon, ActionListener listener) {
		JMenuItem item = new JMenuItem(title);
		if (icon != null) {
			item.setIcon(CB_Tools.getIcon(icon));
		}
		item.addActionListener(listener);
		target.add(item);
		return item;
	}
	public static JMenu createSubMenu(JPopupMenu target, String title, String icon) {
		JMenu item = new JMenu(title);
		if (icon != null) {
			item.setIcon(CB_Tools.getIcon(icon));
		}
		target.add(item);
		return item;
	}
	public static JMenuItem createSubItem(JMenu target, String title, ActionListener listener) {
		return CB_Tools.createSubItem(target, title, listener, null);
	}
	public static JMenuItem createSubItem(JMenu target, String title, ActionListener listener, Icon icon) {
		JMenuItem item = new JMenuItem(title);
		if (icon != null) {
			item.setIcon(icon);
		}
		item.addActionListener(listener);
		target.add(item);
		return item;
	}
	public static ImageIcon getIcon(String icon) {
		java.net.URL url = CB_Tools.class.getResource("images/" + icon);
		if (url != null) {
			return new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
		}
		System.out.println("Warning: Could not load image '" + icon + "'");
		return null;
	}
	public static Image getImage(String image) {
		java.net.URL url = CB_Tools.class.getResource("images/" + image);
		if (url != null) {
			return Toolkit.getDefaultToolkit().getImage(url);
		}
		System.out.println("Warning: Could not load image '" + image + "'");
		return null;
	}
	public static String readFile(String filename) throws IOException {
		StringBuilder fileData = new StringBuilder(100);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = null;
			while ((line = reader.readLine()) != null) {
				fileData.append(line + "\n");
			}
		} catch (IOException error) {
			throw new IOException("There was an error reading the file '" + filename + "'.");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {}
			}
		}
		return fileData.toString();
	}
	public static void printArray(int[] array) {
		System.out.println("START INT ARRAY");
		for (int i = 0; i < array.length; i++) {
			System.out.println(i + ":  " + array[i]);
		}
		System.out.println("END INT ARRAY");
	}
	public static void printArray(double[] array) {
		System.out.println("START DOUBLE ARRAY");
		for (int i = 0; i < array.length; i++) {
			System.out.println(i + ":  " + array[i]);
		}
		System.out.println("END DOUBLE ARRAY");
	}
	public static void printArray(float[] array) {
		System.out.println("START FLOAT ARRAY");
		for (int i = 0; i < array.length; i++) {
			System.out.println(i + ":  " + array[i]);
		}
		System.out.println("END FLOAT ARRAY");
	}
}