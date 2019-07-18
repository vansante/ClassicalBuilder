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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.prefs.Preferences;
import java.io.File;

//this class creates a dialog in which you can edit your preferences
public class CB_PreferencesDialog extends JDialog implements ActionListener, WindowListener {

	private final CB_Main main;
	private final JButton okButton, cancelButton, accountKeyButton, executablePathButton, workingDirButton;
	private final JTextField accountKeyField, workingDirField, executablePathField;
	private final JComboBox lnfChooser;

	public CB_PreferencesDialog(CB_Main main) {
		super(main, "Preferences", true);
        this.main = main;
		this.setSize(450, 400);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		this.setLocationRelativeTo(main);
		this.getContentPane().setLayout(new BorderLayout(2, 2));
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		accountKeyField = new JTextField(35);
		accountKeyField.setText(main.getPreferences().get(CB_Main.PREF_ACCOUNT_KEY, ""));
		executablePathField = new JTextField(40);
		executablePathField.setText(main.getPreferences().get(CB_Main.PREF_EXECUTABLE_PATH, ""));
		workingDirField = new JTextField(40);
		workingDirField.setText(main.getPreferences().get(CB_Main.PREF_WORKING_DIR, ""));

		lnfChooser = new JComboBox(CB_Main.LOOK_AND_FEELS);
		lnfChooser.setSelectedIndex(main.getPreferences().getInt(CB_Main.PREF_LNF, CB_Main.DEFAULT_LNF));

		accountKeyButton = new JButton("Get Accountkey");
		accountKeyButton.setPreferredSize(new Dimension(100, 20));
		accountKeyButton.addActionListener(this);

		executablePathButton = new JButton("Browse");
		executablePathButton.setPreferredSize(new Dimension(50, 20));
		executablePathButton.addActionListener(this);

		workingDirButton = new JButton("Browse");
		workingDirButton.setPreferredSize(new Dimension(50, 20));
		workingDirButton.addActionListener(this);

		okButton = new JButton("Ok");
		okButton.setPreferredSize(new Dimension(50, 25));
		okButton.addActionListener(this);
		okButton.setMnemonic(KeyEvent.VK_O);

		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(50, 25));
		cancelButton.addActionListener(this);
		cancelButton.setMnemonic(KeyEvent.VK_C);

		// The label, field and button for the local job's executable path
		JPanel executablePathPanel = new JPanel(new BorderLayout(2, 2));
		executablePathPanel.add(new JLabel("Path to the classical dynamics application:"), BorderLayout.NORTH);
		executablePathPanel.add(executablePathField, BorderLayout.CENTER);
		executablePathPanel.add(executablePathButton, BorderLayout.EAST);

		// The label, field and button for the local job's working dir
		JPanel workingDirPanel = new JPanel(new BorderLayout(2, 2));
		workingDirPanel.add(new JLabel("Directory where local job results are stored:"), BorderLayout.NORTH);
		workingDirPanel.add(workingDirField, BorderLayout.CENTER);
		workingDirPanel.add(workingDirButton, BorderLayout.EAST);

		// Putting the local job panel together
		JPanel localJobPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		localJobPanel.setBorder(BorderFactory.createTitledBorder("Local Job"));
		localJobPanel.setPreferredSize(new Dimension(450, 70));
		localJobPanel.add(executablePathPanel);
		localJobPanel.add(workingDirPanel);

		// The label, field and button for leiden classical's accountkey
		JPanel accountKeyPanel = new JPanel(new BorderLayout(2, 2));
		accountKeyPanel.add(new JLabel("Please enter your Leiden Classical account key:"), BorderLayout.NORTH);
		accountKeyPanel.add(accountKeyField, BorderLayout.CENTER);
		accountKeyPanel.add(accountKeyButton, BorderLayout.EAST);

		// Putting the leiden classical panel together
		JPanel lcGridPanel = new JPanel(new GridLayout(1, 1, 2, 2));
		lcGridPanel.setBorder(BorderFactory.createTitledBorder("Leiden Classical Grid"));
		lcGridPanel.setPreferredSize(new Dimension(450, 30));
		lcGridPanel.add(accountKeyPanel);

		JPanel lgiGridPanel = new JPanel();
		lgiGridPanel.setBorder(BorderFactory.createTitledBorder("LGI"));

		JPanel themePanel = new JPanel();
		themePanel.setBorder(BorderFactory.createTitledBorder("Program Theme"));
		themePanel.add(new JLabel("Please select the desired theme:"));
		themePanel.add(lnfChooser);
		JLabel themeLabel = new JLabel("Changing the theme requires a restart to take effect.");
		themeLabel.setForeground(Color.red);
		themePanel.add(themeLabel);

		JPanel southPanel = new JPanel();
		southPanel.add(okButton);
		southPanel.add(cancelButton);

		mainPanel.add(localJobPanel);
		mainPanel.add(lcGridPanel);
		mainPanel.add(lgiGridPanel);
		mainPanel.add(themePanel);

		this.add(mainPanel, BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				okButton.requestFocusInWindow();
			}
		});
	}
	public void actionPerformed(ActionEvent e) {
		Object event = e.getSource();
		if (event == okButton) {
			main.getPreferences().put(CB_Main.PREF_ACCOUNT_KEY, accountKeyField.getText());
			main.getPreferences().put(CB_Main.PREF_EXECUTABLE_PATH, executablePathField.getText());
			String workingDir = workingDirField.getText();
			if (!workingDir.endsWith(File.separator)) {
				workingDir += File.separator;
			}
			main.getPreferences().put(CB_Main.PREF_WORKING_DIR, workingDir);
			main.getPreferences().putInt(CB_Main.PREF_LNF, lnfChooser.getSelectedIndex());
			this.closeDialog();
		} else if (event == cancelButton) {
			this.closeDialog();
		} else if (event == accountKeyButton) {
			CB_Tools.openURL(CB_Main.LEIDENCLASSICAL_GET_KEY_WEBSITE);
		} else if (event == executablePathButton) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Choose Executable");
			fileChooser.addChoosableFileFilter(new ExtensionFilter());
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				executablePathField.setText(fileChooser.getCurrentDirectory().getAbsolutePath() + File.separator + fileChooser.getSelectedFile().getName());
			}
		} else if (event == workingDirButton) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Choose Working Directory");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				workingDirField.setText(fileChooser.getCurrentDirectory().getAbsolutePath() + File.separator + fileChooser.getSelectedFile().getName());
			}
		}
	}
	public void closeDialog() {
		this.setVisible(false);
	}
	public void windowClosing(WindowEvent e) {
		this.closeDialog();
	}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public class ExtensionFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().toLowerCase().endsWith(".exe") || file.getName().toLowerCase().endsWith(".x") ;
		}
		public String getDescription() {
			return "Executable (*.exe or *.x)";
		}
	}
}