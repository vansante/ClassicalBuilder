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
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import javax.swing.table.AbstractTableModel;
import java.util.Date;

public class CB_JobListDialog extends JFrame implements ActionListener, WindowListener, MouseListener {
	private final CB_Main main;
	private final JButton deleteButton, openInputButton, openOutputButton, resultsButton, closeButton;
	private final JMenuItem deleteMItem, openInputMItem, openOutputMItem, resultsMItem;
	private final JTable jobsTable;
	private final JobsTableModel jobsModel;
	private final ArrayList<CB_Job> jobs;
	private final JPopupMenu mouseMenu;

	private static boolean localWarningShown = false;

	public CB_JobListDialog(CB_Main main) {
		super(CB_Main.TITLE + " - Job List");
        this.main = main;
		this.setSize(650, 400);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable(true);
		this.addWindowListener(this);
		this.setLocationRelativeTo(main);
		this.getContentPane().setLayout(new BorderLayout());

		// Set an icon
		this.setIconImage(CB_Tools.getImage("joblist.png"));

		jobs = new ArrayList<CB_Job>();

		jobsModel = new JobsTableModel();

		jobsTable = new JTable(jobsModel);
		jobsTable.setColumnSelectionAllowed(false);
		jobsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jobsTable.getColumn("Job ID").setPreferredWidth(5);
		jobsTable.getColumn("Type").setPreferredWidth(5);
		jobsTable.getColumn("Status").setPreferredWidth(5);

		JScrollPane scrollPane = new JScrollPane(jobsTable);

		this.getContentPane().add(scrollPane, BorderLayout.CENTER);

		openInputButton = this.createButton("Open Input", 80, KeyEvent.VK_I);
		openOutputButton = this.createButton("Open Output", 80, KeyEvent.VK_O);
		resultsButton = this.createButton("Show Results", 80, KeyEvent.VK_R);
		deleteButton = this.createButton("Delete Job", 80, KeyEvent.VK_D);
		closeButton = this.createButton("Close", 50, KeyEvent.VK_C);

		JPanel southPanel = new JPanel(new BorderLayout());

		JPanel actionPanel = new JPanel();

		actionPanel.add(openInputButton);
		actionPanel.add(openOutputButton);
		actionPanel.add(resultsButton);
		actionPanel.add(deleteButton);

		southPanel.add(actionPanel, BorderLayout.NORTH);

		JPanel closePanel = new JPanel();
		closePanel.add(closeButton);

		southPanel.add(closePanel, BorderLayout.SOUTH);

		this.getContentPane().add(southPanel, BorderLayout.SOUTH);

		mouseMenu = new JPopupMenu();

		openInputMItem = this.createMenuItem("Open Input", KeyEvent.VK_I, null, mouseMenu);
		openOutputMItem = this.createMenuItem("Open Output", KeyEvent.VK_O, null, mouseMenu);
		resultsMItem = this.createMenuItem("Show Results", KeyEvent.VK_R, null, mouseMenu);
		deleteMItem = this.createMenuItem("Delete Job", KeyEvent.VK_D, "delete.png", mouseMenu);

		jobsTable.addMouseListener(this);

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				closeButton.requestFocusInWindow();
			}
		});
	}
	public JButton createButton(String title, int size, int mnemonic) {
		JButton button = new JButton(title);
		button.setPreferredSize(new Dimension(size, 25));
		button.addActionListener(this);
		button.setMnemonic(mnemonic);
		return button;
	}
	public JMenuItem createMenuItem(String title, int mnemonic, String icon, JPopupMenu menu) {
		JMenuItem item = new JMenuItem(title, mnemonic);
		if (icon != null) {
			item.setIcon(CB_Tools.getIcon(icon));
		}
		item.addActionListener(this);
		menu.add(item);
		return item;
	}
	public void update() {
		jobsModel.updateTable();
	}
	public void updateJobs() {
		jobs.clear();
		try {
			jobs.addAll(CB_LocalJob.getJobs(main.getPreference(CB_Main.PREF_WORKING_DIR, "")));
		} catch (IOException ex) {
			if (!localWarningShown) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
				localWarningShown = true;
			}
		}
		jobs.addAll(CB_LCJob.getJobs(main.getPreference(CB_Main.PREF_ACCOUNT_KEY, "")));
		jobs.addAll(CB_LGIJob.getJobs());
	}
	public void openInput(int job) {
		if (jobs.get(job).hasInput()) {
			try {
				main.newInstance(jobs.get(job).getName() + "_Input", jobs.get(job).getInput());
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "There is no input available for this job.", "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}
	public void openOutput(int job) {
		if (jobs.get(job).hasOutput()) {
			try {
				main.newInstance(jobs.get(job).getName() + "_Output", jobs.get(job).getOutput());
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "There is no output available for this job.", "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}
	public void showResults(int job) {
		if (jobs.get(job).hasResult()) {
			try {
				ResultsDialog resultsDialog = new ResultsDialog(jobs.get(job).getResult());
				resultsDialog.setVisible(true);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "There are no results available for this job.", "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}
	public void deleteJob(int job) {
		int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this job?", "Delete Job", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (result == JOptionPane.YES_OPTION) {
			try {
				if (jobs.get(job).getType() == CB_Job.LOCAL) {
					CB_LocalJob.removeJob(main.getPreference(CB_Main.PREF_WORKING_DIR, ""), jobs.get(job).getName());
				} else if (jobs.get(job).getType() == CB_Job.LC) {
					CB_LCJob.removeJob(main.getPreference(CB_Main.PREF_ACCOUNT_KEY, ""), jobs.get(job).getId());
				} else if (jobs.get(job).getType() == CB_Job.LGI) {

				}
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "There was an error while trying to delete the job: \n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			jobsModel.updateTable();
		}
	}
	public void actionPerformed(ActionEvent e) {
		Object event = e.getSource();
		if (event == deleteButton || event == openInputButton || event == openOutputButton || event == resultsButton) {
			if (jobsTable.getSelectedRow() == -1) {
				JOptionPane.showMessageDialog(this, "No job selected, select one first", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
		}
		if (event == deleteButton || event == deleteMItem) {
			this.deleteJob(jobsTable.getSelectedRow());
		} else if (event == openInputButton || event == openInputMItem) {
			this.openInput(jobsTable.getSelectedRow());
		} else if (event == openOutputButton || event == openOutputMItem) {
			this.openOutput(jobsTable.getSelectedRow());
		} else if (event == resultsButton || event == resultsMItem) {
			this.showResults(jobsTable.getSelectedRow());
		} else if (event == closeButton) {
			jobsModel.pleaseStop();
			this.setVisible(false);
		}
	}
	public void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			int row = jobsTable.rowAtPoint(e.getPoint());
			jobsTable.setRowSelectionInterval(row, row);
			mouseMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			int job = jobsTable.getSelectedRow();
			if (jobs.get(job).hasOutput()) {
				this.openOutput(job);
			} else {
				this.openInput(job);
			}
		}
	}
	public void mousePressed(MouseEvent e) {
		this.maybeShowPopup(e);
	}
	public void mouseReleased(MouseEvent e) {
		this.maybeShowPopup(e);
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public class JobsTableModel extends AbstractTableModel {
		private final Thread thread;
		private boolean update = true;

		public JobsTableModel() {
			thread = new Thread("JobList Updater") {
				public void run() {
					while (update) {
						try {
							Thread.sleep(30000);
						} catch( InterruptedException e) {
							System.out.println("Table Model: Interrupted Exception caught");
						}
						System.out.println("JobsDialog: Auto updating table");
						updateTable();
					}
				}
			};
			thread.start();
		}
		public void updateTable() {
			updateJobs();
			this.fireTableDataChanged();
		}
		public String getColumnName(int col) {
			switch (col) {
				case 0:
					return "Type";
				case 1:
					return "Job ID";
				case 2:
					return "Name";
				case 3:
					return "Submit time";
				case 4:
					return "Status";
			}
			return null;
		}
		public int getRowCount() {
			return jobs.size();
		}
		public int getColumnCount() {
			return 5;
		}
		public Object getValueAt(int row, int col) {
			switch (col) {
				case 0:
					return jobs.get(row).getTypeString();
				case 1:
					return jobs.get(row).getId();
				case 2:
					return jobs.get(row).getName();
				case 3:
					if (jobs.get(row).getTime() == -1) {
						return "Unknown";
					} else {
						return new Date(jobs.get(row).getTime()).toString();
					}
				case 4:
					return jobs.get(row).getStatus();
			}
			return null;
		}
		public boolean isCellEditable(int row, int col) {
			return false;
		}
		public void setValueAt(Object value, int row, int col) {}
		public void pleaseStop() {
			update = false;
		}
		protected void finalize() throws Throwable {
			this.pleaseStop();
		}
	}
	public class ResultsDialog extends JDialog implements ActionListener {
		private final JTextArea detailsField;
		private final JButton closeDetailsbutton;

		public ResultsDialog(String results) {
			super(main, "Job Details", true);
			this.setSize(700, 700);
			this.setResizable(true);
			this.setLocationRelativeTo(main);
			this.getContentPane().setLayout(new BorderLayout());

			detailsField = new JTextArea(results);
			detailsField.setFont(new Font("Monospaced", Font.PLAIN, 12));

			JScrollPane scrollPane = new JScrollPane(detailsField);

			this.getContentPane().add(scrollPane, BorderLayout.CENTER);

			closeDetailsbutton = new JButton("Close");
			closeDetailsbutton.setPreferredSize(new Dimension(50,25));
			closeDetailsbutton.addActionListener(this);

			JPanel closeDetailsPanel = new JPanel();
			closeDetailsPanel.add(closeDetailsbutton);

			this.getContentPane().add(closeDetailsPanel, BorderLayout.SOUTH);
		}
		public void actionPerformed(ActionEvent e) {
			this.setVisible(false);
		}
	}
	public void windowClosing(WindowEvent e) {
		jobsModel.pleaseStop();
		this.setVisible(false);
	}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
}