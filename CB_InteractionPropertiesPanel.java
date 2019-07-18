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

//this class creates a panel containing the properties of intersactions and enables them to be changed
public class CB_InteractionPropertiesPanel extends JPanel implements ActionListener {

	private final CB_Main main;

	private final JComboBox interactionChooser;
	private final JTextField nameTF;
	private final JFormattedTextField forceF, equilibriumF, exponentialF;
	private final JFormattedTextField a1F, a2F, a3F, degreeF, periodF;

	private boolean systemChange = false;

	public CB_InteractionPropertiesPanel(CB_Main main) {
		this.main = main;
		this.setPreferredSize(new Dimension(150, 500));
		this.setLayout(new FlowLayout());

		nameTF = new JTextField("");
		nameTF.addActionListener(this);

		forceF = CB_PropertiesPanel.createDoubleField(this);
		equilibriumF = CB_PropertiesPanel.createDoubleField(this);
		exponentialF = CB_PropertiesPanel.createDoubleField(this);
		a1F = CB_PropertiesPanel.createDoubleField(this);
		a2F = CB_PropertiesPanel.createDoubleField(this);
		a3F = CB_PropertiesPanel.createDoubleField(this);
		degreeF = CB_PropertiesPanel.createDoubleField(this);
		periodF = CB_PropertiesPanel.createDoubleField(this);

		interactionChooser = new JComboBox(CB_Interaction.NAMES);
		interactionChooser.setPreferredSize(new Dimension(130, 23));
		interactionChooser.addActionListener(this);

		//Creating the panels
		JPanel namePanel = CB_PropertiesPanel.createPropertyPanel("Name", nameTF);
		JPanel typePanel = CB_PropertiesPanel.createChooserPanel("Interaction Type", interactionChooser);
		JPanel forcePanel = CB_PropertiesPanel.createPropertyPanel("Force", forceF);
		JPanel equilibriumPanel = CB_PropertiesPanel.createPropertyPanel("Equilibrium Distance", equilibriumF);
		JPanel exponentialPanel = CB_PropertiesPanel.createPropertyPanel("Exponential Parameter", exponentialF);
		JPanel axPanel = CB_PropertiesPanel.createGroupPanel("Ax", "1:", "2:", "3:", a1F, a2F, a3F);
		JPanel degreePanel = CB_PropertiesPanel.createPropertyPanel("Degree", degreeF);
		JPanel periodPanel = CB_PropertiesPanel.createPropertyPanel("Period", periodF);

		this.add(namePanel);
		this.add(typePanel);
		this.add(forcePanel);
		this.add(equilibriumPanel);
		this.add(exponentialPanel);
		this.add(axPanel);
		this.add(degreePanel);
		this.add(periodPanel);
	}
	//create textFields
	public JTextField createTextField(String value) {
		JTextField textField = new JTextField(value);
		textField.addActionListener(this);
		textField.setPreferredSize(new Dimension(0, 20));
		return textField;
	}
	//enable the textfields which are needed, depending in the interaction type
	public void setEnabled(int type) {
		this.disableAll();
		this.setEmpty();
		if (type != -2) {
			interactionChooser.setEnabled(true);
			nameTF.setEnabled(true);
			forceF.setEnabled(true);
			switch (type) {
				case -1:
					equilibriumF.setEnabled(true);
					exponentialF.setEnabled(true);
					a1F.setEnabled(true);
					a2F.setEnabled(true);
					a3F.setEnabled(true);
					degreeF.setEnabled(true);
					periodF.setEnabled(true);
				break;
				case CB_Interaction.LENNARD_JONES:
					equilibriumF.setEnabled(true);
				break;
				case CB_Interaction.MORSE:
					equilibriumF.setEnabled(true);
					exponentialF.setEnabled(true);
				break;
				case CB_Interaction.RYDBERG:
					equilibriumF.setEnabled(true);
					exponentialF.setEnabled(true);
					a1F.setEnabled(true);
					a2F.setEnabled(true);
					a3F.setEnabled(true);
				break;
				case CB_Interaction.HARMONIC_STRETCH:
					equilibriumF.setEnabled(true);
				break;
				case CB_Interaction.HARMONIC_BENDING:
					degreeF.setEnabled(true);
				break;
				case CB_Interaction.PERIODIC_TORSIONAL:
					degreeF.setEnabled(true);
					periodF.setEnabled(true);
				break;
			}
		}
	}
	//set all textfield values
	public void setEmpty() {
		systemChange = true;
		interactionChooser.setSelectedIndex(-1);
		systemChange = false;
		nameTF.setText(null);
		forceF.setText(null);
		equilibriumF.setText(null);
		exponentialF.setText(null);
		a1F.setText(null);
		a2F.setText(null);
		a3F.setText(null);
		degreeF.setText(null);
		periodF.setText(null);
	}
	//disable all textfield
	public void disableAll() {
		interactionChooser.setEnabled(false);
		nameTF.setEnabled(false);
		forceF.setEnabled(false);
		equilibriumF.setEnabled(false);
		exponentialF.setEnabled(false);
		a1F.setEnabled(false);
		a2F.setEnabled(false);
		a3F.setEnabled(false);
		degreeF.setEnabled(false);
		periodF.setEnabled(false);
	}
	//update the selected interactions
	public void updateSelection() {
		if (main.getInstance().getSelectionType() == CB_Instance.INTERACTIONS) {
			if (main.getInstance().getSelectionSize() == 1) {
				this.setEnabled(main.getInteraction(main.getInstance().getSelection(0)).getType());
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
		interactionChooser.setSelectedIndex(main.getInteraction(main.getInstance().getSelection(0)).getType());
		systemChange = false;
		nameTF.setText(main.getInteraction(main.getInstance().getSelection(0)).getName());
		forceF.setValue(main.getInteraction(main.getInstance().getSelection(0)).getForce());
		equilibriumF.setValue(main.getInteraction(main.getInstance().getSelection(0)).getEquilibrium());
		exponentialF.setValue(main.getInteraction(main.getInstance().getSelection(0)).getExponential());
		a1F.setValue(main.getInteraction(main.getInstance().getSelection(0)).getA1());
		a2F.setValue(main.getInteraction(main.getInstance().getSelection(0)).getA2());
		a3F.setValue(main.getInteraction(main.getInstance().getSelection(0)).getA3());
		degreeF.setValue(main.getInteraction(main.getInstance().getSelection(0)).getDegree());
		periodF.setValue(main.getInteraction(main.getInstance().getSelection(0)).getPeriod());
	}
	//get the ids of the selected interactions
	public CB_Relations getSelectedInteractions() {
		CB_Relations interactions = new CB_Relations(main.getInstance().getSelectionSize());
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			interactions.add(main.getInteraction(main.getInstance().getSelection(i)).clone(false));
		}
		return interactions;
	}
	//create a change which can be undone
	public CB_UndoableEdit createUndoableEdit(CB_Relations oldInteractions, CB_Relations newInteractions) {
		return new CB_UndoableEdit(main.getInstance().getSelectionClone(), oldInteractions, newInteractions) {
			public void redo() {
				for (int i = 0; i < getIds().length; i++) {
					main.getInteractions().set(getId(i), newInteractions.get(i));
				}
				main.getInteractionPanel().update();
				main.getInstance().updateSelection();
				super.redo();
			}
			public void undo() {
				for (int i = 0; i < getIds().length; i++) {
					main.getInteractions().set(getId(i), oldInteractions.get(i));
				}
				main.getInteractionPanel().update();
				main.getInstance().updateSelection();
				super.undo();
			}
			public String getUndoRedoPresentationName() {
				return "Edit Multiple Interactions";
			}
		};
	}
	//set the values of the textfields
	public void setProperties(String name, int type, double force, double equilibriumDistance, double exponentialParameter, double a1, double a2, double a3, double degree, double period) {
		CB_Interaction oldInteraction = main.getInteraction(main.getInstance().getSelection(0)).clone(false);
		main.getInteraction(main.getInstance().getSelection(0)).setName(name);
		main.getInteraction(main.getInstance().getSelection(0)).setType(type);
		main.getInteraction(main.getInstance().getSelection(0)).setForce(force);
		main.getInteraction(main.getInstance().getSelection(0)).setEquilibrium(equilibriumDistance);
		main.getInteraction(main.getInstance().getSelection(0)).setExponential(exponentialParameter);
		main.getInteraction(main.getInstance().getSelection(0)).setA1(a1);
		main.getInteraction(main.getInstance().getSelection(0)).setA2(a2);
		main.getInteraction(main.getInstance().getSelection(0)).setA3(a3);
		main.getInteraction(main.getInstance().getSelection(0)).setDegree(degree);
		main.getInteraction(main.getInstance().getSelection(0)).setPeriod(period);
		main.getInstance().updateSelection();
		main.getInteractionPanel().update();
		CB_Interaction newInteraction = main.getInteraction(main.getInstance().getSelection(0)).clone(false);
		main.getInstance().addUndoableEdit(
			new CB_UndoableEdit(main.getInstance().getSelection(0), oldInteraction, newInteraction) {
				public void redo() {
					main.getInteractions().set(getId(), newInteraction);
					main.getInteractionPanel().update();
					main.getInstance().updateSelection();
					super.redo();
				}
				public void undo() {
					main.getInteractions().set(getId(), oldInteraction);
					main.getInteractionPanel().update();
					main.getInstance().updateSelection();
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Edit Interaction";
				}
			}
		);
	}
	//set the name of the interaction(s)
	public void setName() {
		CB_Relations oldInteractions = this.getSelectedInteractions();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getInteraction(main.getInstance().getSelection(0)).setName(nameTF.getText());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(this.createUndoableEdit(oldInteractions, this.getSelectedInteractions()));
	}
	//set the type of the interaction(s)
	public void setType() {
		CB_Relations oldInteractions = this.getSelectedInteractions();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getInteraction(main.getInstance().getSelection(0)).setType(interactionChooser.getSelectedIndex());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(this.createUndoableEdit(oldInteractions, this.getSelectedInteractions()));
	}
	//set the force of the interaction(s)
	public void setForce() {
		CB_Relations oldInteractions = this.getSelectedInteractions();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getInteraction(main.getInstance().getSelection(0)).setForce(((Number)forceF.getValue()).doubleValue());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(this.createUndoableEdit(oldInteractions, this.getSelectedInteractions()));
	}
	//set the equilibrium distance of the interaction(s)
	public void setEquilibrium() {
		CB_Relations oldInteractions = this.getSelectedInteractions();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getInteraction(main.getInstance().getSelection(0)).setEquilibrium(((Number)equilibriumF.getValue()).doubleValue());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(this.createUndoableEdit(oldInteractions, this.getSelectedInteractions()));
	}
	//set the exponetial parameter of the interaction(s)
	public void setExponential() {
		CB_Relations oldInteractions = this.getSelectedInteractions();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getInteraction(main.getInstance().getSelection(0)).setExponential(((Number)exponentialF.getValue()).doubleValue());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(this.createUndoableEdit(oldInteractions, this.getSelectedInteractions()));
	}
	//set the A1 value of the interaction(s)
	public void setAOne() {
		CB_Relations oldInteractions = this.getSelectedInteractions();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getInteraction(main.getInstance().getSelection(0)).setA1(((Number)a1F.getValue()).doubleValue());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(this.createUndoableEdit(oldInteractions, this.getSelectedInteractions()));
	}
	//set the A2 value of the interaction(s)
	public void setATwo() {
		CB_Relations oldInteractions = this.getSelectedInteractions();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getInteraction(main.getInstance().getSelection(0)).setA2(((Number)a2F.getValue()).doubleValue());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(this.createUndoableEdit(oldInteractions, this.getSelectedInteractions()));
	}
	//set the A3 value of the interaction(s)
	public void setAThree() {
		CB_Relations oldInteractions = this.getSelectedInteractions();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getInteraction(main.getInstance().getSelection(0)).setA3(((Number)a3F.getValue()).doubleValue());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(this.createUndoableEdit(oldInteractions, this.getSelectedInteractions()));
	}
	//set the degree of the interaction(s)
	public void setDegree() {
		CB_Relations oldInteractions = this.getSelectedInteractions();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getInteraction(main.getInstance().getSelection(0)).setDegree(((Number)degreeF.getValue()).doubleValue());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(this.createUndoableEdit(oldInteractions, this.getSelectedInteractions()));
	}
	//set the period of the interaction(s)
	public void setPeriod() {
		CB_Relations oldInteractions = this.getSelectedInteractions();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getInteraction(main.getInstance().getSelection(0)).setPeriod(((Number)periodF.getValue()).doubleValue());
		}
		main.getInstance().updateSelection();
		main.getInstance().addUndoableEdit(this.createUndoableEdit(oldInteractions, this.getSelectedInteractions()));
	}
	//action events
	public void actionPerformed(ActionEvent e) {
		//if a interaction is selected
		if (systemChange) {
			return;
		}
		//if exactly one interaction is selected, set all values.
		if (main.getInstance().getSelectionSize() == 1) {
			this.setProperties(
				nameTF.getText(),
				interactionChooser.getSelectedIndex(),
				((Number)forceF.getValue()).doubleValue(),
				((Number)equilibriumF.getValue()).doubleValue(),
				((Number)exponentialF.getValue()).doubleValue(),
				((Number)a1F.getValue()).doubleValue(),
				((Number)a2F.getValue()).doubleValue(),
				((Number)a3F.getValue()).doubleValue(),
				((Number)degreeF.getValue()).doubleValue(),
				((Number)periodF.getValue()).doubleValue()
			);
		} else {
			Object event = e.getSource();
			if (event == nameTF) {
				this.setName();
			} else if (event == interactionChooser) {
				this.setType();
			} else if (event == forceF) {
				this.setForce();
			} else if (event == equilibriumF) {
				this.setEquilibrium();
			} else if (event == exponentialF) {
				this.setExponential();
			} else if (event == a1F) {
				this.setAOne();
			} else if (event == a2F) {
				this.setATwo();
			} else if (event == a3F) {
				this.setAThree();
			} else if (event == degreeF) {
				this.setDegree();
			} else if (event == periodF) {
				this.setPeriod();
			}
		}
	}
}