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
import java.io.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.prefs.Preferences;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.datatransfer.*;
import java.text.ParseException;

// Theme import
import com.jgoodies.looks.plastic.*;
import com.jgoodies.looks.plastic.theme.*;

public class CB_Main extends JFrame implements ChangeListener, MouseListener, WindowListener, ClipboardOwner {

	// Holds all the open file instances
	private final ArrayList<CB_Instance> instances;
	private int activeInstance = 0;

	// A class that stores and retrieves preferences
	private final Preferences preferences;

	// Clipboard to store the copy / cut / paste actions
	private final Clipboard clipboard;
	private boolean canPaste = false;

	// All the different UI panel classes
	private final CB_Menubar menubar;
	private final CB_Toolbar toolbar;
	private final CB_ItemPanel itemPanel;
	private final CB_ParticlePanel particlePanel;
	private final CB_RelationPanel interactionPanel, constraintPanel;
	private final CB_ParticlePropertiesPanel particlePropertiesPanel;
	private final CB_InteractionPropertiesPanel interactionPropertiesPanel;
	private final CB_ConstraintPropertiesPanel constraintPropertiesPanel;
	private final CB_PropertiesPanel propertiesPanel;
	private final CB_View view;

	private CB_HelpDialog helpDialog;
	private CB_JobListDialog jobListDialog;

	private final JTabbedPane tabPane;
	private final JPanel mainPanel;

	private boolean newInstance = true;
	private boolean systemChange = false;

	public static final ImageIcon iconSaved = CB_Tools.getIcon("saved.png");
	public static final ImageIcon iconUnsaved = CB_Tools.getIcon("unsaved.png");

	// CONSTANTS:
	public static final String TITLE = "ClassicalBuilder";
	public static final String VERSION = "2.00";
	public static final int REVISION = 1031;

	public static final String JAVA3D_WEBSITE = "http://java3d.dev.java.net/binary-builds.html";
	public static final String CLASSICALBUILDER_WEBSITE = "http://boinc.gorlaeus.net/ClassicalBuilder.php";
	public static final String LEIDENCLASSICAL_WEBSITE = "http://boinc.gorlaeus.net/";
	public static final String LEIDENCLASSICAL_GET_KEY_WEBSITE = "http://boinc.gorlaeus.net/get_passwd.php";
	public static final String GNU_LICENSE_WEBSITE = "http://www.gnu.org/licenses/gpl.txt";

	public static final String PREF_LNF = "lookandfeel";
	public static final String PREF_ACCOUNT_KEY = "account_key";
	public static final String PREF_EXECUTABLE_PATH = "executable_path";
	public static final String PREF_WORKING_DIR = "working_dir";

	public static final int DEFAULT_LNF = 8;
	public static final String[] LOOK_AND_FEELS = new String[] {"DesertBlue", "DesertGreen", "DesertRed", "ExperienceBlue", "ExperienceGreen", "ExperienceRoyale", "LightGray", "Silver", "SkyBlue", "SkyGreen", "SkyRed"};

	public CB_Main(String[] args) {
		super(CB_Main.TITLE + " " + CB_Main.VERSION);
		this.setSize(900, 700);
		// Maximize the window
		this.setExtendedState(Frame.MAXIMIZED_BOTH);

		// Set an icon
		this.setIconImage(CB_Tools.getImage("icon32.png"));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable(true);
		this.addWindowListener(this);

		// Get the stored preferences
		preferences = Preferences.userNodeForPackage(this.getClass());

		// Get the system clipboard
		clipboard = getToolkit().getSystemClipboard();

		this.setLookAndFeel(preferences.getInt(PREF_LNF, DEFAULT_LNF));

		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

		ToolTipManager.sharedInstance().setInitialDelay(2000);
		ToolTipManager.sharedInstance().setReshowDelay(200);

		// Detect if java3D is installed
		try {
			new com.sun.j3d.utils.geometry.Sphere();
		} catch (Error e) {
			// Not detected, display warning and quit program.
			int result = JOptionPane.showConfirmDialog(this, "Java3D not found, please install Java3D.\nClick yes to visit the Java3D download page.\n\nhttp://java.sun.com/products/java-media/3D/download.html", "Missing Java3D library", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (result == JOptionPane.YES_OPTION) {
				CB_Tools.openURL(CB_Main.JAVA3D_WEBSITE);
			}
			// Exit program
			System.exit(0);
		}

		tabPane = new JTabbedPane();
		tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabPane.setPreferredSize(new Dimension(0, 30));
		tabPane.addMouseListener(this);

		menubar = new CB_Menubar(this);
		this.setJMenuBar(menubar);

		toolbar = new CB_Toolbar(this);

		mainPanel = new JPanel(new BorderLayout());

		particlePropertiesPanel = new CB_ParticlePropertiesPanel(this);
		interactionPropertiesPanel = new CB_InteractionPropertiesPanel(this);
		constraintPropertiesPanel = new CB_ConstraintPropertiesPanel(this);
		propertiesPanel = new CB_PropertiesPanel(particlePropertiesPanel, interactionPropertiesPanel, constraintPropertiesPanel);

		if (args.length == 0) {
			instances = new ArrayList<CB_Instance>(1);
			this.newInstance();
		} else {
			instances = new ArrayList<CB_Instance>(args.length);
			for (int i = 0; i < args.length; i++) {
				this.loadFile(args[i]);
			}
		}

		particlePanel = new CB_ParticlePanel(this);
		interactionPanel = new CB_RelationPanel(this, CB_Instance.INTERACTIONS);
		constraintPanel = new CB_RelationPanel(this, CB_Instance.CONSTRAINTS);
		itemPanel = new CB_ItemPanel(particlePanel, interactionPanel, constraintPanel);

		view = new CB_View(this, CB_View.V_ALL);

		mainPanel.add(tabPane, BorderLayout.NORTH);
		mainPanel.add(itemPanel, BorderLayout.WEST);
		mainPanel.add(view, BorderLayout.CENTER);
		mainPanel.add(propertiesPanel, BorderLayout.EAST);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(toolbar, BorderLayout.NORTH);
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);

		tabPane.addChangeListener(this);

		this.updateUI();
		this.updateCanPaste();
		this.setVisible(true);
	}
	// Main method
	public static void main(String[] args) {
		new CB_Main(args);
	}
	public void switchInstance(int index) {
		this.switchInstance(index, true);
	}
	public void switchInstance(int index, boolean saveState) {
		systemChange = true;
		tabPane.setSelectedIndex(index);
		systemChange = false;
		if (view != null && saveState) {
			instances.get(activeInstance).setOpenTab(itemPanel.getIndex());
			instances.get(activeInstance).setTransform3D(view.getView3D().getTransform3D());
		}
		activeInstance = index;
		particlePropertiesPanel.updateColors();
		if (view != null) {
			this.getInstance(index).resetParticles();
			view.resetParticles();
			view.getView3D().updateBox();
			itemPanel.setIndex(instances.get(index).getOpenTab());
			view.getView3D().setTransform3D(instances.get(index).getTransform3D());
		}
		this.updateUI();
	}
	public void newInstance() {
		CB_Instance instance = new CB_Instance(this, "New file " + (instances.size() + 1));
		instances.add(instance);
		tabPane.addTab(instance.getFilename(), null, null, instance.getFullFilename());
		this.switchInstance(instances.size() - 1);
		this.updateUI();
		if (view != null) {
			view.zoomReset();
		}
	}
	public void newInstance(String filename, String inputData) {
		CB_Instance instance;
		try {
			CB_Import fileImport = new CB_Import(inputData);
			instance = new CB_Instance(this, filename, fileImport.getBox(), fileImport.getColors(), fileImport.getParticles(), fileImport.getInteractions(), fileImport.getConstraints());
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Fileformat Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		instances.add(instance);
		tabPane.addTab(instance.getFilename(), null, null, instance.getFullFilename());
		this.switchInstance(instances.size() - 1);
		this.updateUI();
		if (instances.size() == 2 && this.getInstance(0).getSaved() && newInstance) {
			this.closeInstance(0);
			newInstance = false;
		}
		if (view != null) {
			view.zoomReset();
		}
	}
	public boolean closeInstance(int id) {
		if (this.getInstance(id).getSaved() || this.confirmClose(id)) {
			if (instances.size() == 1) {
				instances.set(0, new CB_Instance(this, "New file 1"));
				particlePropertiesPanel.updateColors();
				view.resetParticles();
				view.zoomReset();
				this.getInstance(0).resetParticles();
				this.updateUI();
				newInstance = true;
			} else {
				instances.remove(id);
				systemChange = true;
				tabPane.remove(id);
				systemChange = false;
				if ((id - 1) >= 0) {
					this.switchInstance(id - 1, false);
				} else {
					this.switchInstance(id, false);
				}
			}
			return true;
		}
		return false;
	}
	public boolean closeAllInstances() {
		for (int i = instances.size(); i > 0; i--) {
			if (!this.closeInstance(0)) {
				return false;
			}
		}
		instances.trimToSize();
		return true;
	}
	public void closeAllExcept(int id) {
		int temp = 0;
		int size = instances.size();
		for (int i = 0; i < size; i++) {
			if (i != id) {
				this.closeInstance(temp);
			} else {
				temp++;
			}
		}
	}
	// Asks for confirmation of file closure if it is unsaved, and asks if you want to save the changes
	public boolean confirmClose(int id) {
		int result = JOptionPane.showConfirmDialog(this, "The file '" + this.getInstance(id).getFilename() + "' has unsaved changes.\nDo you want to save?", "Save changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (result == JOptionPane.YES_OPTION) {
			return this.saveFile(id, false);
		} else if (result == JOptionPane.NO_OPTION) {
			return true;
		}
		return false;
	}
	// Function that displays the preferences dialog
	public void showPreferencesDialog() {
		CB_PreferencesDialog preferencesDialog = new CB_PreferencesDialog(this);
		preferencesDialog.setVisible(true);
	}
	// Function that displays the jobs dialog
	public void showJobListDialog() {
		if (jobListDialog == null) {
			jobListDialog = new CB_JobListDialog(this);
		}
		jobListDialog.setVisible(true);
		jobListDialog.requestFocus();
		jobListDialog.update();
	}
	// Function that displays the about dialog
	public void showAboutDialog() {
		CB_AboutDialog aboutDialog = new CB_AboutDialog(this);
		aboutDialog.setVisible(true);
	}
	// Function that displays the help dialog
	public void showHelpDialog() {
		if (helpDialog == null) {
			helpDialog = new CB_HelpDialog(this);
		}
		helpDialog.setVisible(true);
		helpDialog.requestFocus();
	}
	// Function that shows the load dialog and calls the loadFile function if the filename is supplied
	public void loadFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Load File");
		fileChooser.addChoosableFileFilter(new ExtensionFilter());
		fileChooser.setMultiSelectionEnabled(true);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String directory = fileChooser.getCurrentDirectory().getAbsolutePath() + File.separator;
			for (int i = 0; i < fileChooser.getSelectedFiles().length; i++) {
				String filename = fileChooser.getSelectedFiles()[i].getName();
				this.loadFile(directory + filename);
			}
		}
	}
	// Opens the file, loads the data into a string
	public void loadFile(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			JOptionPane.showMessageDialog(this, "File not found. \nPlease specify an existing file.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = null;
			while ((line = reader.readLine()) != null) {
				fileData.append(line + "\n");
			}
			this.newInstance(filename, fileData.toString());
		} catch (IOException error) {
			JOptionPane.showMessageDialog(this, "IO Exception\nCould not load file.", "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {}
			}
		}
	}
	public boolean saveAllFiles() {
		for (int i = 0; i < instances.size(); i++) {
			this.saveFile(i, false);
		}
		return true;
	}
	// Display the save file dialog if a file is open and calls the save data function if a filename is given or saveAs is false
	public boolean saveFile(int id, boolean saveAs) {
		if (this.getInstance(id).getFullFilename() != null && !saveAs) {
			return this.saveData(id, this.getInstance(id).getFullFilename());
		} else {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(new File(this.getInstance(id).getFilename()));
			fileChooser.setDialogTitle("Save as");
			fileChooser.addChoosableFileFilter(new ExtensionFilter());
			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				String filename = fileChooser.getSelectedFile().getName();
				String directory = fileChooser.getCurrentDirectory().getAbsolutePath() + File.separator;
				if (!filename.endsWith(".cbs")) {
					filename += ".cbs";
				}
				return this.saveData(id, directory + filename);
			}
		}
		return false;
	}
	// Creates an export instance and gets the output string from export, then writes it to the specified file
	public boolean saveData(int id, String saveFile) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(saveFile, false));
			writer.write(this.getInstance(id).getFileData());
			this.getInstance(id).lastSaved(saveFile, true);
		} catch (IOException error) {
			JOptionPane.showMessageDialog(this, "IO Exception.\nCould not write file.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {}
			}
		}
		return true;
	}
	// Copies selected items in open instance to clipboard
	public void copy() {
		if (this.getInstance().getSelectionSize() > 0) {
			if (this.getInstance().getSelectionType() == CB_Instance.PARTICLES) {
				CB_Particles particles = new CB_Particles(this.getInstance().getSelectionSize());
				for (int i = 0; i < this.getInstance().getSelectionSize(); i++) {
					particles.add(this.getParticle(this.getInstance().getSelection(i)).clone());
				}
				clipboard.setContents(particles, this);
			} else if (this.getInstance().getSelectionType() == CB_Instance.INTERACTIONS) {
				CB_Relations interactions = new CB_Relations(CB_Instance.INTERACTIONS);
				for (int i = 0; i < this.getInstance().getSelectionSize(); i++) {
					interactions.add(this.getInteraction(this.getInstance().getSelection(i)).clone(true));
				}
				clipboard.setContents(interactions, this);
			} else if (this.getInstance().getSelectionType() == CB_Instance.CONSTRAINTS) {
				CB_Relations constraints = new CB_Relations(CB_Instance.CONSTRAINTS);
				for (int i = 0; i < this.getInstance().getSelectionSize(); i++) {
					constraints.add(this.getConstraint(this.getInstance().getSelection(i)).clone(true));
				}
				clipboard.setContents(constraints, this);
			}
			canPaste = true;
			this.updateCanPaste();
		}
	}
	// Pastes clipboard items to open instance
	public void paste() {
		Transferable clipboardContent = clipboard.getContents(this);
		if ((clipboardContent != null)) {
			if (clipboardContent.isDataFlavorSupported(CB_Particles.getDataFlavor())) {
				try {
					this.getInstance().addParticles((CB_Particles) clipboardContent.getTransferData(CB_Particles.getDataFlavor()));
					this.getInstance().checkParticleColors();
					this.getInstance().checkParticlePositions();
				} catch (Exception e) {
					System.out.println("Main: Error while pasting particle(s)");
				}
			} else if (clipboardContent.isDataFlavorSupported(CB_Relations.getDataFlavor(CB_Instance.INTERACTIONS))) {
				try {
					this.getInstance().addRelations(CB_Instance.INTERACTIONS, (CB_Relations) clipboardContent.getTransferData(CB_Relations.getDataFlavor(CB_Instance.INTERACTIONS)));
				} catch (Exception e) {
					System.out.println("Main: Error while pasting interaction(s)");
				}
			} else if (clipboardContent.isDataFlavorSupported(CB_Relations.getDataFlavor(CB_Instance.CONSTRAINTS))) {
				try {
					this.getInstance().addRelations(CB_Instance.CONSTRAINTS, (CB_Relations) clipboardContent.getTransferData(CB_Relations.getDataFlavor(CB_Instance.CONSTRAINTS)));
				} catch (Exception e) {
					System.out.println("Main: Error while pasting constraint(s)");
				}
			} else {
				System.out.println("Main: Unknown clipboard data");
			}
		}
	}
	// Pastes clipboard items to open instance
	public void cut() {
		this.copy();
		this.getInstance().removeSelectedItems();
	}
	public boolean testLeidenClassical() {
		int testResult = CB_LCJob.testConnection(this.getPreference(PREF_ACCOUNT_KEY, ""));
		if (testResult == 1) {
			JOptionPane.showMessageDialog(this, "The Leiden Classical website is not reachable!", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		} else if (testResult == 2) {
			JOptionPane.showMessageDialog(this, "You haven't set a correct Leiden Classical accountkey.\nPlease do so in the preferences dialog.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	public boolean testLGI() {
		// TODO: Check for LGI connection and stuff
		return true;
	}
	// Shuts down the program after confirming there are no unsaved changes
	public void shutdown() {
		if (this.closeAllInstances()) {
			System.out.println("Main: Shutting down");
			this.dispose();
			System.exit(0);
		}
	}
	public void updateUI() {
		for (int i = 0; i < instances.size(); i++) {
			if (this.getInstance(i).getSaved()) {
				tabPane.setTitleAt(i, this.getInstance(i).getFilename());
				tabPane.setIconAt(i, iconSaved);
			} else {
				tabPane.setTitleAt(i, this.getInstance(i).getFilename() + " *");
				tabPane.setIconAt(i, iconUnsaved);
			}
			tabPane.setToolTipTextAt(i, this.getInstance(i).getFullFilename());
		}
		this.setTitle();
		this.updateSelection();
		this.updateUndoRedo();
		menubar.updateWindowMenu();
	}
	// Sets the program title with opened filename and saved state.
	public void setTitle() {
		String title = CB_Main.TITLE + " " + CB_Main.VERSION + " - ";
		title += " [" + this.getInstance().getFilename();
		if (this.getInstance().getSaved()) {
			title += "]";
		} else {
			title += " *]";
		}
		this.setTitle(title);
	}
	// Function that enables/disables menu items by checking what and how much is selected.
	public void updateSelection() {
		menubar.updateSelection();
		toolbar.updateSelection();
	}
	// Function that enables/disables the redo and undo menu items.
	public void updateUndoRedo() {
		menubar.updateUndoRedo();
		toolbar.updateUndoRedo();
	}
	public void updateCanPaste() {
		menubar.updateCanPaste();
		toolbar.updateCanPaste();
	}
	public void nextInstance() {
		if (activeInstance + 1 < instances.size()) {
			this.switchInstance(activeInstance + 1);
		} else {
			this.switchInstance(0);
		}
	}
	public void previousInstance() {
		if (activeInstance - 1 >= 0) {
			this.switchInstance(activeInstance - 1);
		} else {
			this.switchInstance(instances.size() - 1);
		}
	}
	public boolean canPaste() {
		return canPaste;
	}
	// Retrieve the preferences class
	public Preferences getPreferences() {
		return preferences;
	}
	public String getPreference(String property, String defaultValue) {
		return preferences.get(property, defaultValue);
	}
	// Retrieve the current active Builder
	public CB_Instance getInstance() {
		return instances.get(activeInstance);
	}
	public CB_Instance getInstance(int index) {
		return instances.get(index);
	}
	public int getInstancesSize() {
		return instances.size();
	}
	public int getInstanceIndex() {
		return activeInstance;
	}
	public CB_Box getBox() {
		return this.getInstance().getBox();
	}
	public CB_Colors getColors() {
		return this.getInstance().getColors();
	}
	public CB_Color getColor(int index) {
		return this.getInstance().getColors().get(index);
	}
	public CB_Particles getParticles() {
		return this.getInstance().getParticles();
	}
	public CB_Particle getParticle(int index) {
		return this.getInstance().getParticle(index);
	}
	public CB_Relations getInteractions() {
		return this.getInstance().getInteractions();
	}
	public CB_Interaction getInteraction(int index) {
		return this.getInstance().getInteraction(index);
	}
	public CB_Relations getConstraints() {
		return this.getInstance().getConstraints();
	}
	public CB_Constraint getConstraint(int index) {
		return this.getInstance().getConstraint(index);
	}
	public CB_Relations getRelations(int type) {
		return this.getInstance().getRelations(type);
	}
	public CB_Relation getRelation(int type, int id) {
		return this.getInstance().getRelation(type, id);
	}
	// Various functions that give the other classes to the UI
	public CB_PropertiesPanel getPropertiesPanel() {
		return propertiesPanel;
	}
	public CB_ParticlePropertiesPanel getParticlePropertiesPanel() {
		return particlePropertiesPanel;
	}
	public CB_InteractionPropertiesPanel getInteractionPropertiesPanel() {
		return interactionPropertiesPanel;
	}
	public CB_ConstraintPropertiesPanel getConstraintPropertiesPanel() {
		return constraintPropertiesPanel;
	}
	public CB_ItemPanel getItemPanel() {
		return itemPanel;
	}
	public CB_ParticlePanel getParticlePanel() {
		return particlePanel;
	}
	public CB_RelationPanel getInteractionPanel() {
		return interactionPanel;
	}
	public CB_RelationPanel getConstraintPanel() {
		return constraintPanel;
	}
	public CB_View getView() {
		return view;
	}
	public CB_Toolbar getToolbar() {
		return toolbar;
	}
	public CB_Menubar getMenubar() {
		return menubar;
	}
	public void setLookAndFeel(int lnf) {
		LookAndFeel laf = null;
		switch (lnf) {
			case 0:
				PlasticLookAndFeel.setPlasticTheme(new DesertBlue());
			break;
			case 1:
				PlasticLookAndFeel.setPlasticTheme(new DesertGreen());
			break;
			case 2:
				PlasticLookAndFeel.setPlasticTheme(new DesertRed());
			break;
			case 3:
				PlasticLookAndFeel.setPlasticTheme(new ExperienceBlue());
			break;
			case 4:
				PlasticLookAndFeel.setPlasticTheme(new ExperienceGreen());
			break;
			case 5:
				PlasticLookAndFeel.setPlasticTheme(new ExperienceRoyale());
			break;
			case 6:
				PlasticLookAndFeel.setPlasticTheme(new LightGray());
			break;
			case 7:
				PlasticLookAndFeel.setPlasticTheme(new Silver());
			break;
			case 8:
				PlasticLookAndFeel.setPlasticTheme(new SkyBlue());
			break;
			case 9:
				PlasticLookAndFeel.setPlasticTheme(new SkyGreen());
			break;
			case 10:
				PlasticLookAndFeel.setPlasticTheme(new SkyRed());
			break;
		}
		laf = new PlasticXPLookAndFeel();
		try {
			UIManager.setLookAndFeel(laf);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			System.out.println("Warning: Could not set Look and Feel.");
		}
	}
	public void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			int tab = tabPane.indexAtLocation(e.getX(), e.getY());
			if (tab >= 0) {
				TabMenu menu = new TabMenu(tab);
				menu.show(e.getComponent(),e.getX(), e.getY());
			}
		}
	}
	public void stateChanged(ChangeEvent e) {
		if (!systemChange) {
			this.switchInstance(tabPane.getSelectedIndex());
		}
	}
	public void lostOwnership(Clipboard parClipboard, Transferable parTransferable) {
		System.out.println("Main: Clipboard content lost");
		canPaste = false;
		this.updateCanPaste();
	}
	public void mousePressed(MouseEvent e) {
		this.maybeShowPopup(e);
	}
	public void mouseReleased(MouseEvent e) {
		this.maybeShowPopup(e);
	}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void windowClosing(WindowEvent e) {
		// When the close window button is pressed, shut down the program.
		this.shutdown();
	}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}

	// Class that checks for the right extension
	public class ExtensionFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().toLowerCase().endsWith(".cbs");
		}
		public String getDescription() {
			return "*.cbs (Classical Builder Savefile)";
		}
	}
	public class TabMenu extends JPopupMenu implements ActionListener {
		private int tab;
		private JMenuItem  mClose, mCloseAll, mSave, mSaveAs;

		public TabMenu(int tab) {
			this.tab = tab;
			mClose = CB_Tools.createMenuItem(this, "Close Tab", "close.png", this);
			mCloseAll = CB_Tools.createMenuItem(this, "Close All Except This Tab", null, this);
			mSave = CB_Tools.createMenuItem(this, "Save", "save.png", this);
			mSaveAs = CB_Tools.createMenuItem(this, "Save As", null, this);
		}
		public void actionPerformed(ActionEvent e) {
			Object event = e.getSource();
			if (event == mClose) {
				closeInstance(tab);
			} else if (event == mCloseAll) {
				closeAllExcept(tab);
			} else if (event == mSave) {
				saveFile(tab, false);
			} else if (event == mSaveAs) {
				saveFile(tab, true);
			}
		}
	}
}