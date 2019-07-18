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
import javax.swing.JOptionPane;
import javax.swing.undo.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CB_Instance {
	// The parent executable class
	private final CB_Main main;
	// The box variable class
	private CB_Box box;
	// The colors class
	private CB_Colors colors;
	// The particles class
	private final CB_Particles particles;
	// Two relation classes
	private final CB_Relations interactions, constraints;
	// Swing undomanager to undo/redo actions
	private final UndoManager undoManager;
	// Array that stores the IDs of the selected items
	private int[] selectedItems = new int[] {};
	// Integer that stores the type of the selected items
	private int selectionType = PARTICLES;
	// String that stores the last saved filename + path
	private String fullFilename, filename;
	// True when no changes have occurred since last change
	private boolean saved = true;
	// Holds an easy array with selected particles for other classes to use
	private boolean[] selectedParticles;
	// Store the open item tab
	private int openTab = PARTICLES;
	// View2D properties
	private double scale = 1.0;
	private int[] offset = new int[] {0, 0, 0};
	// View3D transform values
	private float[] transform3D = new float[] { 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };

	public static final int PARTICLES = 0;
	public static final int INTERACTIONS = 1;
	public static final int CONSTRAINTS = 2;

	public CB_Instance(CB_Main main, String filename) {
		this.main = main;
		this.filename = filename;
		box = new CB_Box();
		colors = new CB_Colors(true);
		particles = new CB_Particles();
		interactions = new CB_Relations(CB_Instance.INTERACTIONS);
		constraints = new CB_Relations(CB_Instance.CONSTRAINTS);
		undoManager = new UndoManager();
	}
	public CB_Instance(CB_Main main, String filename, CB_Box box, CB_Colors colors, CB_Particles particles, CB_Relations interactions, CB_Relations constraints) {
		this.main = main;
		this.filename = filename;
		this.box = box;
		this.colors = colors;
		this.particles = particles;
		this.interactions = interactions;
		this.constraints = constraints;
		this.undoManager = new UndoManager();
		this.lastSaved(filename, true);
	}
	// Function that displays the color dialog, if a file is open
	public void showColorDialog() {
		CB_ColorDialog colorDialog = new CB_ColorDialog(this, colors.cloneColors());
		colorDialog.setVisible(true);
	}
	// Function that closes the color dialog, sets the color changes and creates an undoable edit
	public void setNewColors(CB_Colors colors) {
		CB_Colors oldColors = colors.cloneColors();
		CB_Particles oldParticles = particles.clone();
		this.colors = colors;
		this.checkParticleColors();
		main.getParticlePropertiesPanel().updateColors();
		CB_Particles newParticles = particles.clone();
		this.addUndoableEdit(
			new CB_UndoableEdit(oldColors, colors, oldParticles, newParticles) {
				public void redo() {
					setColors(newColors);
					particles.setParticles(newParticles);
					main.getParticlePropertiesPanel().updateColors();
					updateParticles();
					super.redo();
				}
				public void undo() {
					setColors(oldColors);
					particles.setParticles(oldParticles);
					main.getParticlePropertiesPanel().updateColors();
					updateParticles();
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Edit Colors";
				}
			}
		);
	}
	// Function that sets the new colors
	public void setColors(CB_Colors colors) {
		this.colors = colors;
	}
	// Function that shows the box dialog, if a file is open
	public void showBoxDialog() {
		CB_BoxDialog boxDialog = new CB_BoxDialog(this, box.clone());
		boxDialog.setVisible(true);
	}
	// Function that closes the box dialog, sets the box changes, moves particles if necessary and creates an undoable edit
	public void setNewBox(CB_Box box) {
		CB_Box oldBox = box.clone();
		CB_Particles oldParticles = particles.clone();
		this.box = box;
		this.updateBox();
		this.checkParticlePositions();
		this.updateSelection();
		CB_Particles newParticles = particles.clone();
		this.addUndoableEdit(
			new CB_UndoableEdit(oldBox, box, oldParticles, newParticles) {
				public void redo() {
					setBox(newBox);
					particles.setParticles(newParticles);
					update();
					super.redo();
				}
				public void undo() {
					setBox(oldBox);
					particles.setParticles(oldParticles);
					update();
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Edit Box Properties";
				}
			}
		);
	}
	// Function that sets the new box
	public void setBox(CB_Box box) {
		this.box = box;
	}
	// Function that shows the relation particles dialog if a file is open and the selection type and size is right
	public void showRelationParticlesDialog() {
		if (selectionType == CB_Instance.PARTICLES) {
			JOptionPane.showMessageDialog(main, "No relation selected", "Warning", JOptionPane.WARNING_MESSAGE);
		} else if (selectedItems.length != 1) {
			JOptionPane.showMessageDialog(main, "Please select one relation", "Warning", JOptionPane.WARNING_MESSAGE);
		} else {
			CB_EditRelationParticlesDialog editRelationParticlesDialog = new CB_EditRelationParticlesDialog(this, selectionType, selectedItems[0]);
			editRelationParticlesDialog.setVisible(true);
		}
	}
	public void editRelationParticles(final int type, int id, int[] newParticles) {
		int[] oldIds = this.getRelation(type, id).cloneParticles();
		this.getRelation(type, id).setParticles(newParticles);
		this.updateItemPanels();
		this.updateSelection();
		int[] newIds = this.getRelation(type, id).cloneParticles();
		this.addUndoableEdit(
			new CB_UndoableEdit(id, oldIds, newIds) {
				public void redo() {
					getRelation(type, this.getId()).setParticles(newArray);
					updateItemPanels();
					updateSelection();
					super.redo();
				}
				public void undo() {
					getRelation(type, this.getId()).setParticles(oldArray);
					updateItemPanels();
					updateSelection();
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Edit " + CB_Relations.getTypeString(type) + " Particles";
				}
			}
		);
	}
	// Add particle to interaction
	public void addRelationParticle(int type, int id, int particle) {
		int[] particles = this.getRelation(type, id).cloneParticles();
		particles = CB_Tools.arrayAdd(particles, particle);
		this.editRelationParticles(type, id, particles);
	}
	// Remove particle from interaction
	public void removeRelationParticle(int type, int id, int particle) {
		int[] particles = this.getRelation(type, id).cloneParticles();
		particles = CB_Tools.arrayRemove(particles, CB_Tools.arraySearch(particles, particle));
		this.editRelationParticles(type, id, particles);
	}
	public void lastSaved(String filename, boolean saved) {
		this.setFilename(filename);
		this.saved = saved;
		main.updateUI();
	}
	public void setFilename(String filename) {
    	String seperator;
		if (File.separator.equals("\\")) {
			seperator = "\\\\";
		} else {
			seperator = "/";
		}
		String[] pieces = filename.split(seperator);
		this.filename = pieces[pieces.length - 1];
		this.fullFilename = filename;
    }
	// Creates an export instance and gets the output string from export, then writes it to the specified file
	public String getFileData() {
		CB_Export fileExport = new CB_Export(this, true);
		return fileExport.getOutput();
	}
	// Runs the open file as a local job with the local glut executable
	public void runLocalJob() {
		String name = this.getFilename().replace(' ', '_').replaceAll(".cbs", "");
		do {
			name = JOptionPane.showInputDialog(main, "Please supply a valid job name:", name);
		} while (name != null && !name.matches("[-a-zA-Z0-9_]{3,20}"));
		if (name != null) {
			try {
				CB_LocalJob.submitJob(main.getPreference(CB_Main.PREF_WORKING_DIR, ""), main.getPreference(CB_Main.PREF_EXECUTABLE_PATH, ""), this.getFileData(), name);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(main, "There was an error while trying to run the program:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	// Submits the open file to the boinc website
	public void submitJob() {
		if (main.testLeidenClassical()) {
			String name = this.getFilename().replace(' ', '_').replaceAll(".cbs", "");
			do {
				name = JOptionPane.showInputDialog(main, "Please supply a valid job name:", name);
			} while (name != null && !name.matches("[-a-zA-Z0-9_]{3,20}"));
			if (name != null) {
				String result = CB_LCJob.submitJob(main.getPreference(CB_Main.PREF_ACCOUNT_KEY, ""), this.getFileData(), name);
				if (result != null) {
					JOptionPane.showMessageDialog(main, "There was an error while submitting the job:\n" + result, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	// Adds a new undoable edit to the undomanager and updates the menus
	public void addUndoableEdit(CB_UndoableEdit edit) {
		undoManager.addEdit(edit);
		saved = false;
		main.updateUI();
	}
	// Undo the last action if possible and the update menus
	public void undo() {
		if (undoManager.canUndo()) {
			try {
				undoManager.undo();
			} catch (CannotUndoException e) {
				System.out.println("Instance: Cannot undo!");
			}
			main.updateUI();
		}
	}
	// Redo the last action if possible and the update menus
	public void redo() {
		if (undoManager.canRedo()) {
			try {
				undoManager.redo();
			} catch (CannotRedoException e) {
				System.out.println("Instance: Cannot redo!");
			}
			main.updateUI();
		}
	}
	public void fillSelectedArray() {
		for (int i = 0; i < main.getParticles().size(); i++) {
			selectedParticles[i] = false;
		}
		if (selectionType == CB_Instance.PARTICLES) {
			for (int i = 0; i < this.getSelectionSize(); i++) {
				selectedParticles[this.getSelection(i)] = true;
			}
		} else {
			for (int i = 0; i < this.getSelectionSize(); i++) {
				for (int u = 0; u < getRelation(selectionType, this.getSelection(i)).getParticlesSize(); u++) {
					selectedParticles[getRelation(selectionType, this.getSelection(i)).getParticle(u)] = true;
				}
			}
		}
	}
	// Update everything
	public void update() {
		this.updateBox();
		this.updateParticles();
		this.updateSelection();
		main.getParticlePanel().update();
		main.getInteractionPanel().update();
		main.getConstraintPanel().update();
		main.getParticlePropertiesPanel().updateColors();
	}
	// Update the selection display
	public void updateSelection() {
		this.fillSelectedArray();
		main.getView().updateSelection();
		main.getParticlePanel().updateSelection();
		main.getParticlePropertiesPanel().updateSelection();
		main.getInteractionPanel().updateSelection();
		main.getInteractionPropertiesPanel().updateSelection();
		main.getConstraintPanel().updateSelection();
		main.getConstraintPropertiesPanel().updateSelection();
	}
	// Refreshes an itempanel
	public void updateItemPanel(int type) {
		if (type == CB_Instance.PARTICLES) {
			main.getParticlePanel().update();
		} else if (type == CB_Instance.INTERACTIONS) {
			main.getInteractionPanel().update();
		} else if (type == CB_Instance.CONSTRAINTS) {
			main.getConstraintPanel().update();
		}
	}
	// Refreshes all the itempanels by reloading from the data
	public void updateItemPanels() {
		main.getParticlePanel().update();
		main.getInteractionPanel().update();
		main.getConstraintPanel().update();
	}
	// Deletes the selected items by calling the right function
	public void removeSelectedItems() {
		if (selectionType == CB_Instance.PARTICLES) {
			this.removeParticles(selectedItems);
		} else {
			this.removeRelations(selectionType, selectedItems);
		}
	}
	// Updates the box in the views
	public void updateBox() {
		main.getView().updateBox();
	}
	// Updates the particles in the views and in the particle items panel
	public void updateParticles() {
		main.getView().updateParticles();
		main.getParticlePanel().update();
		main.getInteractionPanel().update();
		main.getConstraintPanel().update();
	}
	public void updateParticle(int id) {
		main.getView().updateParticle(id);
		main.getParticlePanel().update();
		main.getInteractionPanel().update();
		main.getConstraintPanel().update();
	}
	// Updates the particles in the views and in the particle items panel, also works if new particles are added
	public void resetParticles() {
		selectedParticles = new boolean[main.getParticles().size()];
		this.fillSelectedArray();
		main.getView().resetParticles();
		this.updateSelection();
		this.updateItemPanels();
	}
	// Opens the add particle dialog if a file is open
	public void addParticle() {
		CB_NewParticleDialog newParticleDialog = new CB_NewParticleDialog(this);
		newParticleDialog.setVisible(true);
	}
	// Adds the supplied particle after checking its position, updates the UI and creates an undoable edit
	public void addParticle(CB_Particle particle) {
		CB_Particles oldParticles = particles.clone();
		particles.add(particle);
		this.resetParticles();
		this.checkParticlePositions();
		CB_Particles newParticles = particles.clone();
		this.addUndoableEdit(
			new CB_UndoableEdit(oldParticles, newParticles) {
				public void redo() {
					particles.setParticles(newParticles);
					resetParticles();
					super.redo();
				}
				public void undo() {
					particles.setParticles(oldParticles);
					resetParticles();
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Add Particle";
				}
			}
		);
	}
	public void addParticles(CB_Particles addParticles) {
		CB_Particles oldParticles = particles.clone();
		particles.addAll(addParticles);
		this.resetParticles();
		this.checkParticlePositions();
		CB_Particles newParticles = particles.clone();
		this.addUndoableEdit(
			new CB_UndoableEdit(oldParticles, newParticles) {
				public void redo() {
					particles.setParticles(newParticles);
					resetParticles();
					super.redo();
				}
				public void undo() {
					particles.setParticles(oldParticles);
					resetParticles();
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Add Particle(s)";
				}
			}
		);
	}
	// Removes the supplied particles, if an interaction and/or constraint uses the particles,
	// they are removed from their particle list, then updates the UI and creates an undoable edit
	public void removeParticles(int[] ids) {
		CB_Particles oldParticles = particles.clone();
		CB_Relations oldInteractions = interactions.clone(false);
		CB_Relations oldConstraints = constraints.clone(false);
		for (int i = 0; i < ids.length; i++) {
			particles.remove(ids[i]);
			interactions.removeRelationsParticles(ids[i]);
			constraints.removeRelationsParticles(ids[i]);
			// Update the ids because by removing a particle, its id changes..
			for (int j = i; j < ids.length; j++) {
				if (ids[j] > ids[i]) {
					ids[j]--;
				}
			}
		}
		selectedItems = new int[] {};
		this.resetParticles();
		this.updateSelection();
		CB_Particles newParticles = particles.clone();
		CB_Relations newInteractions = interactions.clone(false);
		CB_Relations newConstraints = constraints.clone(false);
		this.addUndoableEdit(
			new CB_UndoableEdit(ids, oldParticles, newParticles, oldInteractions, newInteractions, oldConstraints, newConstraints) {
				public void redo() {
					particles.setParticles(newParticles);
					interactions.setRelations(newInteractions);
					constraints.setRelations(newConstraints);
					selectedItems = new int[] {};
					resetParticles();
					updateSelection();
					super.redo();
				}
				public void undo() {
					particles.setParticles(oldParticles);
					interactions.setRelations(oldInteractions);
					constraints.setRelations(oldConstraints);
					selectedItems = getIds();
					resetParticles();
					updateSelection();
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Remove Particle(s)";
				}
			}
		);
	}
	// Checks if the particle uses a non-exisiting color, if it does, their colorID is set to the last existing one
	public void checkParticleColor(int index) {
		if (particles.get(index).getColor() >= colors.size()) {
			particles.get(index).setColor(colors.size() - 1);
		}
	}
	// Checks if the particles use a non-exisiting color, if they do, their colorID is set to the last existing one
	public void checkParticleColors() {
		for (int i = 0; i < particles.size(); i++) {
			this.checkParticleColor(i);
		}
		this.updateParticles();
	}
	// Checks if all the particles have a position in the box bounds, if not, they are moved within the box
	public void checkParticlePositions() {
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).setPosIndex(CB_Particle.X, this.checkParticlePosition(CB_Particle.X, particles.get(i).getPosIndex(CB_Particle.X)));
			particles.get(i).setPosIndex(CB_Particle.Y, this.checkParticlePosition(CB_Particle.Y, particles.get(i).getPosIndex(CB_Particle.Y)));
			particles.get(i).setPosIndex(CB_Particle.Z, this.checkParticlePosition(CB_Particle.Z, particles.get(i).getPosIndex(CB_Particle.Z)));
		}
		this.updateParticles();
	}
	// Checks if a single axis position is within the box, if not, a new coordinate within the box is given
	public double checkParticlePosition(int index, double pos) {
		if (pos > (box.getDimension(index) / 2)) {
			pos = box.getDimension(index) / 2;
		} else if (pos < (- box.getDimension(index) / 2)) {
			pos =  - box.getDimension(index) / 2;
		}
		return pos;
	}
	// Gets the position of the center of mass of the supplied particle Ids
	public double[] getParticlesCenter(int[] particleIds) {
		double[] center = new double[3];
		double totalMass = 0;
		for (int i = 0; i < particleIds.length; i++) {
			center[0] += particles.get(particleIds[i]).getPosIndex(CB_Particle.X) * particles.get(particleIds[i]).getMass();
			center[1] += particles.get(particleIds[i]).getPosIndex(CB_Particle.Y) * particles.get(particleIds[i]).getMass();
			center[2] += particles.get(particleIds[i]).getPosIndex(CB_Particle.Z) * particles.get(particleIds[i]).getMass();
			totalMass += particles.get(particleIds[i]).getMass();
		}
		center[0] /= totalMass;
		center[1] /= totalMass;
		center[2] /= totalMass;
		return center;
	}
	public double getMaxBoxSize() {
		if (box.getDimension(CB_Box.WIDTH) >= box.getDimension(CB_Box.HEIGHT) && box.getDimension(CB_Box.WIDTH) >= box.getDimension(CB_Box.DEPTH)) {
			return box.getDimension(CB_Box.WIDTH);
		} else if (box.getDimension(CB_Box.HEIGHT) >= box.getDimension(CB_Box.WIDTH) && box.getDimension(CB_Box.HEIGHT) >= box.getDimension(CB_Box.DEPTH)) {
			return box.getDimension(CB_Box.HEIGHT);
		} else {
			return box.getDimension(CB_Box.DEPTH);
		}
	}
	// Adds a single particle to the selection if control is pressed, otherwise just selects the new one
	public void pickParticle(int id, boolean controlDown) {
		if (controlDown && selectionType == CB_Instance.PARTICLES) {
			int[] newSelect;
			int search = CB_Tools.arraySearch(selectedItems, id);
			if (search >= 0) {
				newSelect = CB_Tools.arrayRemove(selectedItems, search);
			} else {
				newSelect = CB_Tools.arrayAdd(selectedItems, id);
			}
			this.select(CB_Instance.PARTICLES, newSelect);
		} else {
			this.select(CB_Instance.PARTICLES, new int[] {id});
		}
	}
	// Selects a new set and updates the UI to reflect, then adds an undoable edit
	public boolean select(final int type, int[] ids) {
		if (selectionType == type && Arrays.equals(selectedItems, ids)) {
			return false;
		}
		this.addUndoableEdit(
			new CB_UndoableEdit(selectionType, selectedItems, ids) {
				public void redo() {
					selectionType = type;
					selectedItems = newArray;
					updateSelection();
					super.redo();
				}
				public void undo() {
					selectionType = getId();
					selectedItems = oldArray;
					updateSelection();
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "New Selection";
				}
			}
		);
		selectionType = type;
		selectedItems = ids;
		main.getPropertiesPanel().setPanel(type);
		this.updateSelection();
		main.updateSelection();
		return true;
	}
	// Selects all available particles
	public void selectAll(int type) {
		int[] selection = null;
		if (type == CB_Instance.PARTICLES) {
			selection = new int[particles.size()];
			for (int i = 0; i < particles.size(); i++) {
				selection[i] = i;
			}
		} else {
			selection = new int[this.getRelations(type).size()];
			for (int i = 0; i < this.getRelations(type).size(); i++) {
				selection[i] = i;
			}
		}
		this.select(type, selection);
	}
	// Selects all items from the current open itempanel tab
	public void selectAll() {
		this.selectAll(main.getItemPanel().getIndex());
	}
	// Deselects whatever is selected
	public void deselect() {
		this.select(selectionType, new int[] {} );
	}
	// Opens the new relation dialog if 2 or more particles are selected
	public void addRelation(int type) {
		if (this.selectionType != CB_Instance.PARTICLES) {
			JOptionPane.showMessageDialog(main, "No particles selected", "Warning", JOptionPane.WARNING_MESSAGE);
		} else if (this.selectedItems.length < 2) {
			JOptionPane.showMessageDialog(main, "Please select two or more particles", "Warning", JOptionPane.WARNING_MESSAGE);
		} else {
			if (type == CB_Instance.INTERACTIONS) {
				CB_NewInteractionDialog newInteractionDialog = new CB_NewInteractionDialog(this, this.selectedItems);
				newInteractionDialog.setVisible(true);
			} else if (type == CB_Instance.CONSTRAINTS) {
				CB_NewConstraintDialog newConstraintDialog = new CB_NewConstraintDialog(this, this.selectedItems);
				newConstraintDialog.setVisible(true);
			}
		}
	}
	// Adds the supplied relation, updates the UI and creates an undoable edit
	public void addRelation(final int type, CB_Relation relation) {
		CB_Relations oldRelations = this.getRelations(type).clone(false);
		this.getRelations(type).add(relation);
		CB_Relations newRelations = this.getRelations(type).clone(false);
		this.updateItemPanel(type);
		this.addUndoableEdit(
			new CB_UndoableEdit(oldRelations, newRelations) {
				public void redo() {
					getRelations(type).setRelations(newRelations);
					updateItemPanel(type);
					super.redo();
				}
				public void undo() {
					getRelations(type).setRelations(oldRelations);
					updateItemPanel(type);
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Add " + CB_Relations.getTypeStringSingle(type);
				}
			}
		);
	}
	public void addRelations(final int type, CB_Relations relations) {
		CB_Relations oldRelations = this.getRelations(type).clone(false);
		this.getRelations(type).addAll(relations);
		CB_Relations newRelations = this.getRelations(type).clone(false);
		this.updateItemPanel(type);
		this.addUndoableEdit(
			new CB_UndoableEdit(oldRelations, newRelations) {
				public void redo() {
					getRelations(type).setRelations(newInteractions);
					updateItemPanel(type);
					super.redo();
				}
				public void undo() {
					getRelations(type).setRelations(oldInteractions);
					updateItemPanel(type);
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Add " + CB_Relations.getTypeString(type);
				}
			}
		);
	}
	// Removes the supplied interactions, then updates the UI and creates an undoable edit
	public void removeRelations(final int type, int[] ids) {
		CB_Relations oldRelations = this.getRelations(type).clone(false);
		for( int i = 0; i < ids.length; i++) {
			this.getRelations(type).remove(ids[i]);
			for (int j = i; j < ids.length; j++) {
				if (ids[j] > ids[i]) {
					ids[j]--;
				}
			}
		}
		selectedItems = new int[] {};
		this.updateSelection();
		this.updateItemPanel(type);
		CB_Relations newRelations = this.getRelations(type).clone(false);
		this.addUndoableEdit(
			new CB_UndoableEdit(ids, oldRelations, newRelations) {
				public void redo() {
					getRelations(type).setRelations(newRelations);
					selectedItems = new int[] {};
					updateSelection();
					updateItemPanel(type);
					super.redo();
				}
				public void undo() {
					getRelations(type).setRelations(oldRelations);
					selectedItems = getIds();
					updateSelection();
					updateItemPanel(type);
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Remove " + CB_Relations.getTypeString(type);
				}
			}
		);
	}
	// Gets the main class instance
	public CB_Main getMain() {
		return main;
	}
	public String getPreference(String property, String defaultValue) {
		return main.getPreferences().get(property, defaultValue);
	}
	public CB_Particles getParticles() {
		return particles;
	}
	public CB_Box getBox() {
		return box;
	}
	public CB_Colors getColors() {
		return colors;
	}
	public CB_Color getColor(int index) {
		return colors.get(index);
	}
	public CB_Relations getRelations(int type) {
		if (type == CB_Instance.INTERACTIONS) {
			return this.getInteractions();
		} else if (type == CB_Instance.CONSTRAINTS) {
			return this.getConstraints();
		}
		return null;
	}
	public CB_Relation getRelation(int type, int id) {
		if (type == CB_Instance.INTERACTIONS) {
			return this.getInteraction(id);
		} else if (type == CB_Instance.CONSTRAINTS) {
			return this.getConstraint(id);
		}
		return null;
	}
	public CB_Particle getParticle(int index) {
		return particles.get(index);
	}
	public CB_Relations getInteractions() {
		return interactions;
	}
	public CB_Interaction getInteraction(int index) {
		return (CB_Interaction) interactions.get(index);
	}
	public CB_Relations getConstraints() {
		return constraints;
	}
	public CB_Constraint getConstraint(int index) {
		return (CB_Constraint) constraints.get(index);
	}
	public int getSelectionType() {
		return selectionType;
	}
	public int[] getSelection() {
		return selectedItems;
	}
	public int[] getSelectionClone() {
		int[] clone = new int[selectedItems.length];
		for (int i = 0; i < selectedItems.length; i++) {
			clone[i] = selectedItems[i];
		}
		return clone;
	}
	public int getSelection(int index) {
		return selectedItems[index];
	}
	public int getSelectionSize() {
		return selectedItems.length;
	}
	public String getFullFilename() {
		return fullFilename;
	}
	public boolean[] getParticleSelected() {
		return selectedParticles;
	}
	public boolean getParticleSelected(int index) {
		return selectedParticles[index];
	}
	public String getFilename() {
		return filename;
	}
	public boolean getSaved() {
		return saved;
	}
	public boolean canUndo() {
		return undoManager.canUndo();
	}
	public boolean canRedo() {
		return undoManager.canRedo();
	}
	public double getScale() {
		return scale;
	}
	public void setScale(double scale) {
		this.scale = scale;
	}
	public int[] getOffset() {
		return offset;
	}
	public int getOffset(int index) {
		return offset[index];
	}
	public void setOffset(int[] offset) {
		this.offset = offset;
	}
	public void setOffset(int index, int offset) {
		this.offset[index] = offset;
	}
	public int getOpenTab() {
		return openTab;
	}
	public void setOpenTab(int openTab) {
		this.openTab = openTab;
	}
	public float[] getTransform3D() {
		return transform3D;
	}
	public void setTransform3D(float[] transform3D) {
		this.transform3D = transform3D;
	}
}