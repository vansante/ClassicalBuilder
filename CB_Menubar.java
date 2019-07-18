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

public class CB_Menubar extends JMenuBar implements ActionListener {

	private final CB_Main main;

	// All the menus
	private final JMenu menuFile, menuEdit, menuInsert, menuView, menu3DRotation, menuGrid, menuSettings, menuWindow, menuHelp;
	// Menu items for "File"
	private final JMenuItem mItemExit, mItemOpen, mItemSave, mItemSaveAs, mItemSaveAll, mItemNew, mItemClose, mItemCloseAll, mItemCloseExceptTab;
	// Menu items for "Edit"
	private final JMenuItem mItemDeselect, mItemBoxProperties, mItemCut, mItemCopy, mItemPaste, mItemDelete, mItemSelectAll;
	private final JMenuItem mItemUndo, mItemRedo, mItemColors, mItemRelationParticles;
	// Menu items for "Insert"
	private final JMenuItem mItemNewParticle, mItemNewInteraction, mItemNewConstraint;
	// Menu items for "View"
	private final ButtonGroup viewGroup;
	private final JMenuItem mItemVFront, mItemVTop, mItemVLeft, mItemV2D, mItemVAll, mItemV3D, mItemZoomIn, mItemZoomOut, mItemZoomReset;
	// Menu items for submenu "3D rotation"
	private final ButtonGroup rotationGroup;
	private final JMenuItem mItemUnlock, mItemLockX, mItemLockY, mItemLockXY, mItemSemiLock;
	// Menu items for "Grid"
	private final JMenuItem mItemJobList, mItemSubmitJob, mItemRunLocalJob;
	// Menu items for "Settings"
	private final JMenuItem mItemPreferences;
	// Menu items for "Window"
	private JMenuItem mItemNextWindow, mItemPreviousWindow;
	private JMenuItem[] mItemsWindow;
	// Menu items for "Help"
	private final JMenuItem mItemHelp, mItemAbout, mItemCbWebsite, mItemLcWebsite;

	public CB_Menubar(CB_Main main) {
		super();

		this.main = main;

		// Create menu item: File
		menuFile = this.createMenu(this, "File", KeyEvent.VK_F);
		mItemNew = this.createMenuItem(menuFile, "New", "new.png", KeyEvent.VK_N, KeyEvent.VK_N, 2);
		mItemOpen = this.createMenuItem(menuFile, "Open", "load.png", KeyEvent.VK_O, KeyEvent.VK_O, 2);
		mItemClose = this.createMenuItem(menuFile, "Close", "close.png", KeyEvent.VK_C, KeyEvent.VK_F4, 2);
		mItemCloseAll = this.createMenuItem(menuFile, "Close All", null, 0, 0, 0);
		mItemCloseExceptTab = this.createMenuItem(menuFile, "Close All Except Active Tab", null, 0, 0, 0);
		menuFile.addSeparator();
		mItemSave = this.createMenuItem(menuFile, "Save", "save.png", KeyEvent.VK_S, KeyEvent.VK_S, 2);
		mItemSaveAs = this.createMenuItem(menuFile, "Save As", null, KeyEvent.VK_A, 0, 0);
		mItemSaveAll = this.createMenuItem(menuFile, "Save All", "saveall.png", KeyEvent.VK_A, 0, 0);
		menuFile.addSeparator();
		mItemExit = this.createMenuItem(menuFile, "Exit", null, KeyEvent.VK_E, KeyEvent.VK_F4, 3);

		// Create menu Item: Edit
		menuEdit = this.createMenu(this, "Edit", KeyEvent.VK_E);
		mItemUndo = this.createMenuItem(menuEdit, "Undo", "undo.png", KeyEvent.VK_U, KeyEvent.VK_Z, 2);
		mItemRedo = this.createMenuItem(menuEdit, "Redo", "redo.png", KeyEvent.VK_R, KeyEvent.VK_Y, 2);
		menuEdit.addSeparator();
		mItemCut = this.createMenuItem(menuEdit, "Cut", "cut.png", KeyEvent.VK_X, KeyEvent.VK_X, 2);
		mItemCopy = this.createMenuItem(menuEdit, "Copy", "copy.png", KeyEvent.VK_C, KeyEvent.VK_C, 2);
		mItemPaste = this.createMenuItem(menuEdit, "Paste", "paste.png", KeyEvent.VK_P, KeyEvent.VK_V, 2);
		mItemDelete = this.createMenuItem(menuEdit, "Delete", "delete.png", KeyEvent.VK_D, KeyEvent.VK_DELETE, 1);
		menuEdit.addSeparator();
		mItemSelectAll = this.createMenuItem(menuEdit, "Select All", "select.png", KeyEvent.VK_A, KeyEvent.VK_A, 2);
		mItemDeselect = this.createMenuItem(menuEdit, "Deselect", "deselect.png", KeyEvent.VK_E, KeyEvent.VK_D, 2);
		menuEdit.addSeparator();
		mItemRelationParticles = this.createMenuItem(menuEdit, "Relation Particles", "editrelation.png", KeyEvent.VK_R, KeyEvent.VK_R, 2);
		menuEdit.addSeparator();
		mItemColors = this.createMenuItem(menuEdit, "Colors", "colors.png", KeyEvent.VK_O, 0, 0);
		mItemBoxProperties = this.createMenuItem(menuEdit, "Box Properties", "box.png", KeyEvent.VK_B, 0, 0);

		// Create menu Item: Insert
		menuInsert = this.createMenu(this, "Insert", KeyEvent.VK_I);
		mItemNewParticle = this.createMenuItem(menuInsert, "New Particle", "newparticle.png", KeyEvent.VK_P, KeyEvent.VK_P, 2);
		mItemNewInteraction = this.createMenuItem(menuInsert, "New Interaction", "newinteraction.png", KeyEvent.VK_I, KeyEvent.VK_I, 2);
		mItemNewConstraint = this.createMenuItem(menuInsert, "New Constraint", "newconstraint.png", KeyEvent.VK_C, KeyEvent.VK_U, 2);

		// Create menu Item: View
		menuView = this.createMenu(this, "View", KeyEvent.VK_V);
		mItemZoomIn = this.createMenuItem(menuView, "Zoom In", "zoomin.png", KeyEvent.VK_I, KeyEvent.VK_ADD, 2);
		mItemZoomOut = this.createMenuItem(menuView, "Zoom Out", "zoomout.png", KeyEvent.VK_O, KeyEvent.VK_SUBTRACT, 2);
		mItemZoomReset = this.createMenuItem(menuView, "Reset Zoom", "zoomreset.png", KeyEvent.VK_R, KeyEvent.VK_MULTIPLY, 2);
		menuView.addSeparator();

		// Create submenu Item: 3D Rotation
		menu3DRotation = this.createMenu(menuView, "3D Rotation", KeyEvent.VK_3);
		rotationGroup = new ButtonGroup();
		mItemUnlock = this.createRadioMenuItem(menu3DRotation, rotationGroup, "Unlock Both Axes", KeyEvent.VK_U, 0, 0);
		mItemLockX = this.createRadioMenuItem(menu3DRotation, rotationGroup, "Lock X-axis", KeyEvent.VK_X, 0, 0);
		mItemLockY = this.createRadioMenuItem(menu3DRotation, rotationGroup, "Lock Y-axis", KeyEvent.VK_Y, 0, 0);
		mItemLockXY = this.createRadioMenuItem(menu3DRotation, rotationGroup, "Lock Both Axes", KeyEvent.VK_B, 0, 0);
		mItemSemiLock = this.createRadioMenuItem(menu3DRotation, rotationGroup, "Semi-Lock", KeyEvent.VK_S, 0, 0);
		mItemSemiLock.setSelected(true);

		menuView.addSeparator();
		viewGroup = new ButtonGroup();
		mItemVFront = this.createRadioMenuItem(menuView, viewGroup, "Front View", KeyEvent.VK_1, KeyEvent.VK_1, 2);
		mItemVTop = this.createRadioMenuItem(menuView, viewGroup, "Top View", KeyEvent.VK_2, KeyEvent.VK_2, 2);
		mItemVLeft = this.createRadioMenuItem(menuView, viewGroup, "Left View", KeyEvent.VK_3, KeyEvent.VK_3, 2);
		mItemV3D = this.createRadioMenuItem(menuView, viewGroup, "3D View", KeyEvent.VK_4, KeyEvent.VK_4, 2);
		mItemV2D = this.createRadioMenuItem(menuView, viewGroup, "Front, Top & Side View", KeyEvent.VK_5, KeyEvent.VK_5, 2);
		mItemVAll = this.createRadioMenuItem(menuView, viewGroup, "Front, Top, Side & 3D View", KeyEvent.VK_6, KeyEvent.VK_6, 2);
		mItemVAll.setSelected(true);

		// Create menu Item: Grid
		menuGrid = this.createMenu(this, "Grid", KeyEvent.VK_G);
		mItemJobList = this.createMenuItem(menuGrid, "Job List", "joblist.png", KeyEvent.VK_J, KeyEvent.VK_J, 2);
		mItemSubmitJob = this.createMenuItem(menuGrid, "Submit Job", null, 0, 0, 0);
		mItemRunLocalJob = this.createMenuItem(menuGrid, "Run Local Job", null, 0, 0, 0);

		// Create menu Item: Settings
		menuSettings = this.createMenu(this, "Settings", KeyEvent.VK_S);
		mItemPreferences = this.createMenuItem(menuSettings, "Preferences", "settings.png", 0, 0, 0);

		// Create menu Item: Window
		menuWindow = this.createMenu(this, "Window", KeyEvent.VK_W);

		// Create menu Item: Help
		menuHelp = this.createMenu(this, "Help", KeyEvent.VK_H);
		mItemHelp = this.createMenuItem(menuHelp, "Help", "help.png", KeyEvent.VK_H, KeyEvent.VK_F1, 1);
		mItemCbWebsite = this.createMenuItem(menuHelp, "ClassicalBuilder Website", null, KeyEvent.VK_C, 0, 0);
		mItemLcWebsite = this.createMenuItem(menuHelp, "Leiden Classical Website", null, KeyEvent.VK_L, 0, 0);
		mItemAbout = this.createMenuItem(menuHelp, "About", "icon16.png", KeyEvent.VK_A, 0, 0);
	}
	public JMenu createMenu(JComponent target, String text, int mnemonic) {
		JMenu menu = new JMenu(text);
		menu.setMnemonic(mnemonic);
		target.add(menu);
		return menu;
	}
	// Function that creates a menu item and attaches an accelerator and an actionlistener
	public JMenuItem createMenuItem(JMenu target, String text, String icon, int mnemonic, int accelerator, int acceleratorUse) {
		JMenuItem menuItem = new JMenuItem(text, mnemonic);
		menuItem.addActionListener(this);
		if (icon != null) {
			menuItem.setIcon(CB_Tools.getIcon(icon));
		}
		if (acceleratorUse == 1) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, 0));
		} else if (acceleratorUse == 2) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, ActionEvent.CTRL_MASK));
		} else if (acceleratorUse == 3) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, ActionEvent.ALT_MASK));
		}
		target.add(menuItem);
		return menuItem;
	}
	// Function that creates a menu radiobutton and attaches an accellerator and an actionlistener
	public JRadioButtonMenuItem createRadioMenuItem(JMenu target, ButtonGroup group, String text, int mnemonic, int accelerator, int acceleratorUse) {
		JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(text);
		menuItem.setMnemonic(mnemonic);
		menuItem.addActionListener(this);
		if (acceleratorUse == 1) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, 0));
		} else if (acceleratorUse == 2) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, ActionEvent.CTRL_MASK));
		} else if (acceleratorUse == 3) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, ActionEvent.ALT_MASK));
		}
		group.add(menuItem);
		target.add(menuItem);
		return menuItem;
	}
	public void updateWindowMenu() {
		menuWindow.removeAll();
		mItemPreviousWindow = this.createMenuItem(menuWindow, "Previous Window", null, KeyEvent.VK_P, KeyEvent.VK_PAGE_DOWN, 2);
		mItemNextWindow = this.createMenuItem(menuWindow, "Next Window", null, KeyEvent.VK_N, KeyEvent.VK_PAGE_UP, 2);
		menuWindow.addSeparator();
		mItemsWindow = new JMenuItem[main.getInstancesSize()];
		for (int i = 0; i< mItemsWindow.length; i++) {
			mItemsWindow[i] = this.createMenuItem(menuWindow, main.getInstance(i).getFilename(), null, 0, 0, 0);
		}
	}
	// Function that enables/disables menu items by checking what and how much is selected.
	public void updateSelection() {
		int type = main.getInstance().getSelectionType();
		if (main.getInstance().getSelectionSize() > 0) {
			mItemCut.setEnabled(true);
			mItemCopy.setEnabled(true);
			mItemDelete.setEnabled(true);
		} else {
			mItemCut.setEnabled(false);
			mItemCopy.setEnabled(false);
			mItemDelete.setEnabled(false);
		}
		if (main.getInstance().getSelectionSize() == 1 && (type == CB_Instance.INTERACTIONS || type == CB_Instance.CONSTRAINTS)) {
			mItemRelationParticles.setEnabled(true);
		} else {
			mItemRelationParticles.setEnabled(false);
		}
		if (main.getInstance().getSelectionSize() > 1 && type == CB_Instance.PARTICLES) {
			mItemNewInteraction.setEnabled(true);
			mItemNewConstraint.setEnabled(true);
		} else {
			mItemNewInteraction.setEnabled(false);
			mItemNewConstraint.setEnabled(false);
		}
	}
	public void updateUndoRedo() {
		mItemUndo.setEnabled(main.getInstance().canUndo());
		mItemRedo.setEnabled(main.getInstance().canRedo());
	}
	public void updateCanPaste() {
		mItemPaste.setEnabled(main.canPaste());
	}
	public void actionPerformed(ActionEvent e) {
		// Get the source menu item
		Object event = e.getSource();
		// Do the action for the menu item
		if (event == mItemExit) {
			main.shutdown();
	  	} else if (event == mItemNew) {
			main.newInstance();
		} else if (event == mItemClose) {
			main.closeInstance(main.getInstanceIndex());
		} else if (event == mItemCloseAll) {
			main.closeAllInstances();
		} else if (event == mItemCloseExceptTab) {
			main.closeAllExcept(main.getInstanceIndex());
		} else if (event == mItemOpen) {
			main.loadFile();
		} else if (event == mItemSave) {
			main.saveFile(main.getInstanceIndex(), false);
		} else if (event == mItemSaveAs) {
			main.saveFile(main.getInstanceIndex(), true);
		} else if (event == mItemSaveAll) {
			main.saveAllFiles();
		}  else if (event == mItemCut) {
			main.cut();
		} else if (event == mItemCopy) {
			main.copy();
		} else if (event == mItemPaste) {
			main.paste();
		} else if (event == mItemDelete) {
			main.getInstance().removeSelectedItems();
		} else if (event == mItemNewParticle) {
			main.getInstance().addParticle();
		} else if (event == mItemNewInteraction) {
			main.getInstance().addRelation(CB_Instance.INTERACTIONS);
		} else if (event == mItemNewConstraint) {
			main.getInstance().addRelation(CB_Instance.CONSTRAINTS);
		} else if (event == mItemSelectAll) {
			main.getInstance().selectAll();
		} else if (event == mItemDeselect) {
			main.getInstance().deselect();
		} else if (event == mItemUndo) {
			main.getInstance().undo();
		} else if (event == mItemRedo) {
			main.getInstance().redo();
		} else if (event == mItemRelationParticles) {
			main.getInstance().showRelationParticlesDialog();
		} else if (event == mItemBoxProperties) {
			main.getInstance().showBoxDialog();
		} else if (event == mItemColors) {
			main.getInstance().showColorDialog();
		} else if (event == mItemVFront) {
			main.getView().setViewMode(CB_View.V_FRONT);
		} else if (event == mItemVTop) {
			main.getView().setViewMode(CB_View.V_TOP);
		} else if (event == mItemVLeft) {
			main.getView().setViewMode(CB_View.V_LEFT);
		} else if (event == mItemV3D) {
			main.getView().setViewMode(CB_View.V_3D);
		} else if (event == mItemV2D) {
			main.getView().setViewMode(CB_View.V_2D);
		} else if (event == mItemVAll) {
			main.getView().setViewMode(CB_View.V_ALL);
		} else if (event == mItemZoomIn) {
			main.getView().zoomIn();
		} else if (event == mItemZoomOut) {
			main.getView().zoomOut();
		} else if (event == mItemZoomReset) {
			main.getView().zoomReset();
		} else if (event == mItemUnlock) {
			main.getView().getView3D().setRotation(CB_View3D.ROT_NORMAL);
		} else if (event == mItemLockX) {
			main.getView().getView3D().setRotation(CB_View3D.ROT_LOCK_X);
		} else if (event == mItemLockY) {
			main.getView().getView3D().setRotation(CB_View3D.ROT_LOCK_Y);
		} else if (event == mItemLockXY) {
			main.getView().getView3D().setRotation(CB_View3D.ROT_LOCK);
		} else if (event == mItemSemiLock) {
			main.getView().getView3D().setRotation(CB_View3D.ROT_SEMILOCK);
		} else if (event == mItemJobList) {
			main.showJobListDialog();
		} else if (event == mItemSubmitJob) {
			main.getInstance().submitJob();
		} else if (event == mItemRunLocalJob) {
			main.getInstance().runLocalJob();
		} else if (event == mItemPreferences) {
			main.showPreferencesDialog();
		} else if (event == mItemNextWindow) {
			main.nextInstance();
		}  else if (event == mItemPreviousWindow) {
			main.previousInstance();
		}  else if (event == mItemHelp) {
			main.showHelpDialog();
		} else if (event == mItemAbout) {
			main.showAboutDialog();
		} else if (event == mItemCbWebsite) {
			CB_Tools.openURL(CB_Main.CLASSICALBUILDER_WEBSITE);
		} else if (event == mItemLcWebsite) {
			CB_Tools.openURL(CB_Main.LEIDENCLASSICAL_WEBSITE);
		} else {
			for (int i = 0; i < mItemsWindow.length; i++) {
				if (event == mItemsWindow[i]) {
					main.switchInstance(i, false);
					return;
				}
			}
		}
	}
}