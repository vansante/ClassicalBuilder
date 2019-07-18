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

// Class that creates a draggable toolbar with buttons.

public class CB_Toolbar extends JToolBar implements ActionListener {

	private final CB_Main main;
	private final JButton newButton, loadButton, saveButton, saveAllButton, closeButton, jobListButton;
	private final JButton undoButton, redoButton, cutButton, copyButton, pasteButton, deleteButton, editRelationButton;
	private final JButton selectButton, deselectButton, newParticleButton, newInteractionButton, newConstraintButton;
	private final JButton boxButton, colorsButton, zoomInButton, zoomOutButton, zoomResetButton, helpButton, aboutButton;

	public CB_Toolbar(CB_Main main) {
		super("Toolbar");
		this.main = main;

		// Start creating all the buttons
		newButton = this.createButton("new.png", "New File");
		loadButton = this.createButton("load.png", "Open File");
		saveButton = this.createButton("save.png", "Save File");
		saveAllButton = this.createButton("saveall.png", "Save All Files");
		closeButton = this.createButton("close.png", "Close File");

		this.addSeparator();

		cutButton = this.createButton("cut.png", "Cut");
		copyButton = this.createButton("copy.png", "Copy");
		pasteButton = this.createButton("paste.png", "Paste");
		deleteButton = this.createButton("delete.png", "Delete");

		this.addSeparator();

		undoButton = this.createButton("undo.png", "Undo");
		redoButton = this.createButton("redo.png", "Redo");

		this.addSeparator();

		selectButton = this.createButton("select.png", "Select All");
		deselectButton = this.createButton("deselect.png", "Deselect");

		this.addSeparator();

		newParticleButton = this.createButton("newparticle.png", "New Particle");
		newInteractionButton = this.createButton("newinteraction.png", "New Interaction");
		newConstraintButton = this.createButton("newconstraint.png", "New Constraint");

		this.addSeparator();

		editRelationButton = this.createButton("editrelation.png", "Edit Relation Particles");

		this.addSeparator();

		boxButton = this.createButton("box.png", "Box Properties");
		colorsButton = this.createButton("colors.png", "Colors");

		this.addSeparator();

		zoomInButton = this.createButton("zoomin.png", "Zoom In");
		zoomOutButton = this.createButton("zoomout.png", "Zoom Out");
		zoomResetButton = this.createButton("zoomreset.png", "Reset Zoom");

		this.addSeparator();

		jobListButton = this.createButton("joblist.png", "Open job list");

		this.addSeparator();

		helpButton = this.createButton("help.png", "Help");
		aboutButton = this.createButton("icon16.png", "About");
	}
	// Function that enables/disables buttons by checking what and how much is selected.
	public void updateSelection() {
		int type = main.getInstance().getSelectionType();
		if (main.getInstance().getSelectionSize() > 0) {
			cutButton.setEnabled(true);
			copyButton.setEnabled(true);
			deleteButton.setEnabled(true);
		} else {
			cutButton.setEnabled(false);
			copyButton.setEnabled(false);
			deleteButton.setEnabled(false);
		}
		if (main.getInstance().getSelectionSize() == 1 && (type == CB_Instance.INTERACTIONS || type == CB_Instance.CONSTRAINTS)) {
			editRelationButton.setEnabled(true);
		} else {
			editRelationButton.setEnabled(false);
		}
		if (main.getInstance().getSelectionSize() > 1 && type == CB_Instance.PARTICLES) {
			newInteractionButton.setEnabled(true);
			newConstraintButton.setEnabled(true);
		} else {
			newInteractionButton.setEnabled(false);
			newConstraintButton.setEnabled(false);
		}
	}
	// Function that enables/disables the redo and undo buttons.
	public void updateUndoRedo() {
		undoButton.setEnabled(main.getInstance().canUndo());
		redoButton.setEnabled(main.getInstance().canRedo());
	}
	public void updateCanPaste() {
		pasteButton.setEnabled(main.canPaste());
	}
	// Function that creates a button, puts an icon and tooltip on it, and adds an actionlistener.
	public JButton createButton(String icon, String tooltip) {
		JButton button = new JButton(CB_Tools.getIcon(icon));
		button.setBorderPainted(false);
		button.setPreferredSize(new Dimension(25, 25));
		button.setToolTipText(tooltip);
		button.addActionListener(this);
		this.add(button);
		return button;
	}
	// The actionlistener for the buttons
	public void actionPerformed (ActionEvent e) {
		// Get the source button
		Object event = e.getSource();
		// Do the action for the button
		if (event == newButton) {
			main.newInstance();
	  	} else if (event == loadButton) {
			main.loadFile();
		} else if (event == saveButton) {
			main.saveFile(main.getInstanceIndex(), false);
		} else if (event == saveAllButton) {
			main.saveAllFiles();
		} else if (event == closeButton) {
			main.closeInstance(main.getInstanceIndex());
		} else if (event == undoButton) {
			main.getInstance().undo();
		} else if (event == redoButton) {
			main.getInstance().redo();
		} else if (event == cutButton) {
			main.cut();
		} else if (event == copyButton) {
			main.copy();
		} else if (event == pasteButton) {
			main.paste();
		} else if (event == deleteButton) {
			main.getInstance().removeSelectedItems();
		} else if (event == selectButton) {
			main.getInstance().selectAll();
		} else if (event == deselectButton) {
			main.getInstance().deselect();
		} else if (event == editRelationButton) {
			main.getInstance().showRelationParticlesDialog();
		} else if (event == newParticleButton) {
			main.getInstance().addParticle();
		} else if (event == newInteractionButton) {
			main.getInstance().addRelation(CB_Instance.INTERACTIONS);
		} else if (event == newConstraintButton) {
			main.getInstance().addRelation(CB_Instance.CONSTRAINTS);
		} else if (event == boxButton) {
			main.getInstance().showBoxDialog();
		} else if (event == colorsButton) {
			main.getInstance().showColorDialog();
		} else if (event == zoomInButton) {
			main.getView().zoomIn();
		} else if (event == zoomOutButton) {
			main.getView().zoomOut();
		} else if (event == zoomResetButton) {
			main.getView().zoomReset();
		} else if (event == jobListButton) {
			main.showJobListDialog();
		} else if (event == helpButton) {
			main.showHelpDialog();
		} else if (event == aboutButton) {
			main.showAboutDialog();
		}
	}
}