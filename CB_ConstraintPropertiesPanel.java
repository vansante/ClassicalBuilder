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
import javax.swing.event.*;

//this class creates a panel containing the properties of constraints and enables them to be changed
public class CB_ConstraintPropertiesPanel extends JPanel implements ActionListener {

	private final CB_Main main;

	private final JTextField nameTF;
	private final JFormattedTextField distanceF, maxstepF, errorF;
	private final JCheckBox distanceCB, maxstepCB, errorCB;
	private final JComboBox constraintChooser;

	private boolean systemChange = false;

    public CB_ConstraintPropertiesPanel(CB_Main main) {
		this.main = main;
		this.setPreferredSize(new Dimension(150,550));
		this.setLayout(new FlowLayout());

		constraintChooser = new JComboBox(CB_Constraint.NAMES);
		constraintChooser.setPreferredSize(new Dimension(130, 23));
		constraintChooser.addActionListener(this);

		nameTF = new JTextField("");
		nameTF.addActionListener(this);

		distanceF = CB_PropertiesPanel.createDoubleField(this);
		maxstepF = CB_PropertiesPanel.createIntField(this);
		errorF = CB_PropertiesPanel.createDoubleField(this);

		distanceCB = createCheckBox();
		maxstepCB = createCheckBox();
		errorCB = createCheckBox();

		JPanel namePanel = CB_PropertiesPanel.createPropertyPanel("Name", nameTF);
		JPanel typePanel = CB_PropertiesPanel.createChooserPanel("Constraint Type", constraintChooser);
		JPanel distancePanel = CB_PropertiesPanel.createOptionalPropertyPanel("Distance", distanceCB, distanceF);
		JPanel maxstepPanel = CB_PropertiesPanel.createOptionalPropertyPanel("Maxstep", maxstepCB, maxstepF);
		JPanel errorPanel = CB_PropertiesPanel.createOptionalPropertyPanel("Error Parameter", errorCB, errorF);

		this.add(namePanel);
		this.add(typePanel);
		this.add(distancePanel);
		this.add(maxstepPanel);
		this.add(errorPanel);
	}
	public JCheckBox createCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.addActionListener(this);
		return checkBox;
	}
	//enable needed textfields, considering the constraint type
	public void setEnabled(int type) {
		this.disableAll();
		this.setEmpty();
		if (type != -2) {
			switch (type) {
				case -1:
					constraintChooser.setEnabled(true);
					nameTF.setEnabled(true);
					distanceF.setEnabled(true);
					maxstepF.setEnabled(true);
					errorF.setEnabled(true);
					distanceCB.setEnabled(true);
					maxstepCB.setEnabled(true);
					errorCB.setEnabled(true);
				break;
				case CB_Constraint.DISTANCE:
					constraintChooser.setEnabled(true);
					nameTF.setEnabled(true);
					distanceF.setEnabled(true);
					maxstepF.setEnabled(true);
					errorF.setEnabled(true);
					distanceCB.setEnabled(true);
					maxstepCB.setEnabled(true);
					errorCB.setEnabled(true);
				break;
			}
		}
	}
	//empty all the textfield values
	public void setEmpty() {
		systemChange = true;
		constraintChooser.setSelectedIndex(-1);
		nameTF.setText(null);
		distanceF.setText(null);
		maxstepF.setText(null);
		errorF.setText(null);
		distanceCB.setSelected(false);
		maxstepCB.setSelected(false);
		errorCB.setSelected(false);
		systemChange = false;
	}
	//disable all the textfields
	public void disableAll() {
		constraintChooser.setEnabled(false);
		nameTF.setEnabled(false);
		distanceF.setEnabled(false);
		maxstepF.setEnabled(false);
		errorF.setEnabled(false);
		distanceCB.setEnabled(false);
		maxstepCB.setEnabled(false);
		errorCB.setEnabled(false);
	}
	//update the selected interaction
	public void updateSelection() {
		if (main.getInstance().getSelectionType() == CB_Instance.CONSTRAINTS) {
			if (main.getInstance().getSelectionSize() == 1) {
				this.setEnabled(main.getConstraint(main.getInstance().getSelection(0)).getType());
				this.updateProperties();
			} else if (main.getInstance().getSelectionSize() > 1) {
				this.setEnabled(-1);
				this.setEmpty();
			}
		} else {
			this.setEnabled(-2);
		}
	}
	public void updateProperties() {
		systemChange = true;
		constraintChooser.setSelectedIndex(main.getConstraint(main.getInstance().getSelection(0)).getType());
		systemChange = false;
		nameTF.setText(main.getConstraint(main.getInstance().getSelection(0)).getName());
		distanceF.setValue(main.getConstraint(main.getInstance().getSelection(0)).getDistance());
		maxstepF.setValue(main.getConstraint(main.getInstance().getSelection(0)).getMaxstep());
		errorF.setValue(main.getConstraint(main.getInstance().getSelection(0)).getError());
		distanceCB.setSelected(main.getConstraint(main.getInstance().getSelection(0)).isDistanceEnabled());
		maxstepCB.setSelected(main.getConstraint(main.getInstance().getSelection(0)).isMaxstepEnabled());
		errorCB.setSelected(main.getConstraint(main.getInstance().getSelection(0)).isErrorEnabled());
		distanceF.setEnabled(main.getConstraint(main.getInstance().getSelection(0)).isDistanceEnabled());
		maxstepF.setEnabled(main.getConstraint(main.getInstance().getSelection(0)).isMaxstepEnabled());
		errorF.setEnabled(main.getConstraint(main.getInstance().getSelection(0)).isErrorEnabled());
	}
	//get the ids of the selected constraints
	public CB_Relations getSelectedConstraints() {
		CB_Relations constraints = new CB_Relations(main.getInstance().getSelectionSize());
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			constraints.add(main.getConstraint(main.getInstance().getSelection(i)).clone(false));
		}
		return constraints;
	}
	//create a change which can be undone
	public CB_UndoableEdit createUndoableEdit(CB_Relations oldConstraints, CB_Relations newConstraints) {
		return new CB_UndoableEdit(main.getInstance().getSelectionClone(), oldConstraints, newConstraints) {
			public void redo() {
				for (int i = 0; i < getIds().length; i++) {
					main.getConstraints().set(getId(i), newConstraints.get(i));
				}
				main.getConstraintPanel().update();
				main.getInstance().updateSelection();
				super.redo();
			}
			public void undo() {
				for (int i = 0; i < getIds().length; i++) {
					main.getConstraints().set(getId(i), oldConstraints.get(i));
				}
				main.getConstraintPanel().update();
				main.getInstance().updateSelection();
				super.undo();
			}
			public String getUndoRedoPresentationName() {
				return "Edit Multiple Constraints";
			}
		};
	}
	//set all the textfield values to the values of the constraint
	public void setProperties(String name, int type, boolean distanceEnabled, boolean maxstepEnabled, boolean errorEnabled, double distance, int maxStep, double error) {
		CB_Constraint oldConstraint = main.getConstraint(main.getInstance().getSelection(0)).clone(false);
		main.getConstraint(main.getInstance().getSelection(0)).setName(name);
		main.getConstraint(main.getInstance().getSelection(0)).setType(type);
		main.getConstraint(main.getInstance().getSelection(0)).setDistance(distance);
		main.getConstraint(main.getInstance().getSelection(0)).setMaxstep(maxStep);
		main.getConstraint(main.getInstance().getSelection(0)).setError(error);
		main.getConstraint(main.getInstance().getSelection(0)).enableDistance(distanceEnabled);
		main.getConstraint(main.getInstance().getSelection(0)).enableMaxstep(maxstepEnabled);
		main.getConstraint(main.getInstance().getSelection(0)).enableError(errorEnabled);
		main.getInstance().updateSelection();
		main.getConstraintPanel().update();
		CB_Constraint newConstraint = main.getConstraint(main.getInstance().getSelection(0)).clone(false);
		main.getInstance().addUndoableEdit(
			new CB_UndoableEdit(main.getInstance().getSelection(0), oldConstraint, newConstraint) {
				public void redo() {
					main.getConstraints().set(getId(), newConstraint);
					main.getConstraintPanel().update();
					main.getInstance().updateSelection();
					super.redo();
				}
				public void undo() {
					main.getConstraints().set(getId(), oldConstraint);
					main.getConstraintPanel().update();
					main.getInstance().updateSelection();
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Edit Constraint";
				}
			}
		);
	}
	//set the constraint name
	public void setName() {
		CB_Relations oldConstraints = this.getSelectedConstraints();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getConstraint(main.getInstance().getSelection(i)).setName(nameTF.getText());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldConstraints, this.getSelectedConstraints()));
	}
	//set the constraint type
	public void setType() {
		CB_Relations oldConstraints = this.getSelectedConstraints();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getConstraint(main.getInstance().getSelection(i)).setType(constraintChooser.getSelectedIndex());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldConstraints, this.getSelectedConstraints()));
	}
	//set the constraint distance
	public void setDistance() {
		CB_Relations oldConstraints = this.getSelectedConstraints();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getConstraint(main.getInstance().getSelection(i)).setDistance(((Number)distanceF.getValue()).doubleValue());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldConstraints, this.getSelectedConstraints()));
	}
	//set the constraint distance
	public void setMaxstep() {
		CB_Relations oldConstraints = this.getSelectedConstraints();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getConstraint(main.getInstance().getSelection(i)).setMaxstep(((Number)maxstepF.getValue()).intValue());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldConstraints, this.getSelectedConstraints()));
	}
	//set the constraint error
	public void setError() {
		CB_Relations oldConstraints = this.getSelectedConstraints();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getConstraint(main.getInstance().getSelection(i)).setError(((Number)errorF.getValue()).doubleValue());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldConstraints, this.getSelectedConstraints()));
	}
	//set the value if distance is enabled or not
	public void setDistanceEnabled() {
		CB_Relations oldConstraints = this.getSelectedConstraints();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getConstraint(main.getInstance().getSelection(i)).enableDistance(distanceCB.isSelected());
		}
		main.getInstance().addUndoableEdit(createUndoableEdit(oldConstraints, this.getSelectedConstraints()));
	}
	//set the value if distance is enabled or not
	public void setMaxstepEnabled() {
		CB_Relations oldConstraints = this.getSelectedConstraints();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getConstraint(main.getInstance().getSelection(i)).enableMaxstep(maxstepCB.isSelected());
		}
		main.getInstance().addUndoableEdit(createUndoableEdit(oldConstraints, this.getSelectedConstraints()));
	}
	//set the value if distance is enabled or not
	public void setErrorEnabled() {
		CB_Relations oldConstraints = this.getSelectedConstraints();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getConstraint(main.getInstance().getSelection(i)).enableError(errorCB.isSelected());
		}
		main.getInstance().addUndoableEdit(createUndoableEdit(oldConstraints, this.getSelectedConstraints()));
	}
	//action event
	public void actionPerformed(ActionEvent e) {
		if (systemChange) {
			return;
		}
		Object event = e.getSource();
		//if exactly one constraint is selected, set all values.
		if (main.getInstance().getSelectionSize() == 1) {
			this.setProperties(
				nameTF.getText(),
				constraintChooser.getSelectedIndex(),
				distanceCB.isSelected(),
				maxstepCB.isSelected(),
				errorCB.isSelected(),
				((Number)distanceF.getValue()).doubleValue(),
				((Number)maxstepF.getValue()).intValue(),
				((Number)errorF.getValue()).doubleValue()
			);
			if (event == distanceCB) {
				distanceF.setEnabled(distanceCB.isSelected());
			} else if (event == maxstepCB) {
				maxstepF.setEnabled(maxstepCB.isSelected());
			} else if (event == errorCB) {
				errorF.setEnabled(errorCB.isSelected());
			}
		} else {
			if (event == nameTF) {
				this.setName();
			} else if (event == constraintChooser) {
				this.setType();
			} else if (event == distanceCB) {
				this.setDistanceEnabled();
			} else if (event == maxstepCB) {
				this.setMaxstepEnabled();
			} else if (event == errorCB) {
				this.setErrorEnabled();
			} else if (event == distanceF) {
				this.setDistance();
			} else if (event == maxstepF) {
				this.setMaxstep();
			} else if (event == errorF) {
				this.setError();
			}
		}
	}
}