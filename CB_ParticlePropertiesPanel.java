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
import javax.swing.*;
import java.awt.event.*;

//this class creates a panel containing the properties of particles and enables them to be changed
public class CB_ParticlePropertiesPanel extends JPanel implements ActionListener {
	private final CB_Main main;
	private final JComboBox colorChooser;
	private final JTextField nameTextField;
	private final JFormattedTextField massF, chargeF, radiusF;
	private final JFormattedTextField momentumXF, momentumYF, momentumZF;
	private final JFormattedTextField positionXF, positionYF, positionZF;
	private boolean systemChange = false;

	public CB_ParticlePropertiesPanel(CB_Main main) {
		this.main = main;
		this.setPreferredSize(new Dimension(150,500));

		//create all components
		colorChooser = new JComboBox();
		colorChooser.setPreferredSize(new Dimension(130, 23));

		nameTextField = new JTextField("");
		nameTextField.addActionListener(this);

		massF = CB_PropertiesPanel.createDoubleField(this);
		chargeF = CB_PropertiesPanel.createDoubleField(this);
		radiusF = CB_PropertiesPanel.createDoubleField(this);
		positionXF = CB_PropertiesPanel.createDoubleField(this);
		positionYF = CB_PropertiesPanel.createDoubleField(this);
		positionZF = CB_PropertiesPanel.createDoubleField(this);
		momentumXF = CB_PropertiesPanel.createDoubleField(this);
		momentumYF = CB_PropertiesPanel.createDoubleField(this);
		momentumZF = CB_PropertiesPanel.createDoubleField(this);

		colorChooser.addActionListener(this);

		JPanel namePanel = CB_PropertiesPanel.createPropertyPanel("Name", nameTextField);
		JPanel colorPanel = CB_PropertiesPanel.createChooserPanel("Color", colorChooser);
		JPanel massPanel = CB_PropertiesPanel.createPropertyPanel("Mass", massF);
		JPanel chargePanel = CB_PropertiesPanel.createPropertyPanel("Charge", chargeF);
		JPanel radiusPanel = CB_PropertiesPanel.createPropertyPanel("Radius", radiusF);
		JPanel positionPanel = CB_PropertiesPanel.createGroupPanel("Position", "X:", "Y:", "Z:", positionXF, positionYF, positionZF);
		JPanel momentumPanel = CB_PropertiesPanel.createGroupPanel("Momentum", "X:", "Y:", "Z:", momentumXF, momentumYF, momentumZF);

		this.add(namePanel);
		this.add(colorPanel);
		this.add(massPanel);
		this.add(chargePanel);
		this.add(radiusPanel);
		this.add(positionPanel);
		this.add(momentumPanel);

		this.setEnabled(false);
	}
	//enables all the textfields
	public void setEnabled(boolean enabled) {
		nameTextField.setEnabled(enabled);
		colorChooser.setEnabled(enabled);
		radiusF.setEnabled(enabled);
		massF.setEnabled(enabled);
		chargeF.setEnabled(enabled);
		positionXF.setEnabled(enabled);
		positionYF.setEnabled(enabled);
		positionZF.setEnabled(enabled);
		momentumXF.setEnabled(enabled);
		momentumYF.setEnabled(enabled);
		momentumZF.setEnabled(enabled);
		if (!enabled) {
			this.setEmpty();
		}
	}
	//set all values shown in the textfields
	public void setEmpty() {
		systemChange = true;
		nameTextField.setText(null);
		colorChooser.setSelectedIndex(-1);
		radiusF.setText(null);
		massF.setText(null);
		chargeF.setText(null);
		positionXF.setText(null);
		positionYF.setText(null);
		positionZF.setText(null);
		momentumXF.setText(null);
		momentumYF.setText(null);
		momentumZF.setText(null);
		systemChange = false;
	}
	//update the list of colors
	public void updateColors() {
		systemChange = true;
		colorChooser.removeAllItems();
		for (int i = 0; i < main.getColors().size(); i++ ) {
			colorChooser.addItem(main.getColor(i));
		}
		systemChange = false;
		this.updateSelection();
	}
	//update the selected particles
	public void updateSelection() {
		if (main.getInstance().getSelectionType() == CB_Instance.PARTICLES) {
			if (main.getInstance().getSelectionSize() == 1) {
				this.setEnabled(true);
				this.updateProperties();
				this.updatePosition();
				this.updateMomentum();
			} else if (main.getInstance().getSelectionSize() > 1) {
				this.setEnabled(true);
				this.setEmpty();
			} else {
				this.setEnabled(false);
			}
		}
	}
	// Update the particle properties
	public void updateProperties() {
		nameTextField.setText(main.getParticle(main.getInstance().getSelection(0)).getName());
		systemChange = true;
		colorChooser.setSelectedIndex(main.getParticle(main.getInstance().getSelection(0)).getColor());
		systemChange = false;
		radiusF.setValue(main.getParticle(main.getInstance().getSelection(0)).getRadius());
		massF.setValue(main.getParticle(main.getInstance().getSelection(0)).getMass());
		chargeF.setValue(main.getParticle(main.getInstance().getSelection(0)).getCharge());
	}
	// Update the particle position
	public void updatePosition() {
		positionXF.setValue(main.getParticle(main.getInstance().getSelection(0)).getPosIndex(CB_Particle.X));
		positionYF.setValue(main.getParticle(main.getInstance().getSelection(0)).getPosIndex(CB_Particle.Y));
		positionZF.setValue(main.getParticle(main.getInstance().getSelection(0)).getPosIndex(CB_Particle.Z));
	}
	// Update the particle momentum
	public void updateMomentum() {
		momentumXF.setValue(main.getParticle(main.getInstance().getSelection(0)).getMomIndex(CB_Particle.X));
		momentumYF.setValue(main.getParticle(main.getInstance().getSelection(0)).getMomIndex(CB_Particle.Y));
		momentumZF.setValue(main.getParticle(main.getInstance().getSelection(0)).getMomIndex(CB_Particle.Z));
	}
	//get the ids of the particles which are selected
	public CB_Particles getSelectedParticles() {
		CB_Particles particles = new CB_Particles(main.getInstance().getSelectionSize());
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			particles.add(main.getParticle(main.getInstance().getSelection(i)).clone());
		}
		return particles;
	}
	public CB_UndoableEdit createUndoableEdit(CB_Particles oldParticles, CB_Particles newParticles) {
		return new CB_UndoableEdit(main.getInstance().getSelectionClone(), oldParticles, newParticles) {
			public void redo() {
				for (int i = 0; i < getIds().length; i++) {
					main.getParticles().set(getId(i), newParticles.get(i));
				}
				main.getInstance().updateParticles();
				updateSelection();
				super.redo();
			}
			public void undo() {
				for (int i = 0; i < getIds().length; i++) {
					main.getParticles().set(getId(i), oldParticles.get(i));
				}
				main.getInstance().updateParticles();
				updateSelection();
				super.undo();
			}
			public String getUndoRedoPresentationName() {
				return "Alter Multiple Particles";
			}
		};
	}
	public void setProperties(String name, int colorId, double radius, double mass, double charge, double[] position, double[] momentum) {
		CB_Particle oldParticle = main.getParticle(main.getInstance().getSelection(0)).clone();
		main.getParticle(main.getInstance().getSelection(0)).setName(name);
		main.getParticle(main.getInstance().getSelection(0)).setColor(colorId);
		main.getParticle(main.getInstance().getSelection(0)).setRadius(radius);
		main.getParticle(main.getInstance().getSelection(0)).setMass(mass);
		main.getParticle(main.getInstance().getSelection(0)).setCharge(charge);
		main.getParticle(main.getInstance().getSelection(0)).setPosition(position);
		main.getParticle(main.getInstance().getSelection(0)).setMomentum(momentum);
		main.getInstance().updateParticles();
		CB_Particle newParticle = main.getParticle(main.getInstance().getSelection(0)).clone();
		main.getInstance().addUndoableEdit(
			new CB_UndoableEdit(main.getInstance().getSelection(0), oldParticle, newParticle) {
				public void redo() {
					main.getParticles().set(getId(), newParticle);
					main.getInstance().updateParticle(getId());
					updateSelection();
					super.redo();
				}
				public void undo() {
					main.getParticles().set(getId(), oldParticle);
					main.getInstance().updateParticle(getId());
					updateSelection();
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Alter Particle Properties";
				}
			}
		);
	}
	//set the name of the selected particle
	public void setName() {
		CB_Particles oldParticles = this.getSelectedParticles();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getParticle(main.getInstance().getSelection(i)).setName(nameTextField.getText());
		}
		main.getInstance().updateParticles();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldParticles, this.getSelectedParticles()));
	}
	//set the color of the selected particle
	public void setColor() {
		CB_Particles oldParticles = this.getSelectedParticles();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getParticle(main.getInstance().getSelection(i)).setColor(colorChooser.getSelectedIndex());
		}
		main.getInstance().updateParticles();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldParticles, this.getSelectedParticles()));
	}
	//set the radius of the selected particle
	public void setRadius() {
		CB_Particles oldParticles = this.getSelectedParticles();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getParticle(main.getInstance().getSelection(i)).setRadius(((Number)radiusF.getValue()).doubleValue());
		}
		main.getInstance().updateParticles();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldParticles, this.getSelectedParticles()));
	}
	//set the mass of the selected particle
	public void setMass() {
		CB_Particles oldParticles = this.getSelectedParticles();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getParticle(main.getInstance().getSelection(i)).setMass(((Number)massF.getValue()).doubleValue());
		}
		main.getInstance().updateParticles();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldParticles, this.getSelectedParticles()));
	}
	//set the charge of the selected particle
	public void setCharge() {
		CB_Particles oldParticles = this.getSelectedParticles();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getParticle(main.getInstance().getSelection(i)).setCharge(((Number)chargeF.getValue()).doubleValue());
		}
		main.getInstance().updateParticles();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldParticles, this.getSelectedParticles()));
	}
	//set the x position of the selected particle
	public void setPositionX() {
		CB_Particles oldParticles = this.getSelectedParticles();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getParticle(main.getInstance().getSelection(i)).setPosIndex(CB_Particle.X, main.getInstance().checkParticlePosition(CB_Particle.X, ((Number)positionXF.getValue()).doubleValue()));
		}
		main.getInstance().updateParticles();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldParticles, this.getSelectedParticles()));
	}
	//set the y position of the selected particle
	public void setPositionY() {
		CB_Particles oldParticles = this.getSelectedParticles();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getParticle(main.getInstance().getSelection(i)).setPosIndex(CB_Particle.Y, main.getInstance().checkParticlePosition(CB_Particle.Y, ((Number)positionYF.getValue()).doubleValue()));
		}
		main.getInstance().updateParticles();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldParticles, this.getSelectedParticles()));
	}
	//set the z position of the selected particle
	public void setPositionZ() {
		CB_Particles oldParticles = this.getSelectedParticles();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getParticle(main.getInstance().getSelection(i)).setPosIndex(CB_Particle.Z, main.getInstance().checkParticlePosition(CB_Particle.Z, ((Number)positionZF.getValue()).doubleValue()));
		}
		main.getInstance().updateParticles();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldParticles, this.getSelectedParticles()));
	}
	//set the x momentum of the selected particle
	public void setMomentumX() {
		CB_Particles oldParticles = this.getSelectedParticles();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getParticle(main.getInstance().getSelection(i)).setMomIndex(CB_Particle.X, ((Number)momentumXF.getValue()).doubleValue());
		}
		main.getInstance().updateParticles();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldParticles, this.getSelectedParticles()));
	}
	//set the y momentum of the selected particle
	public void setMomentumY() {
		CB_Particles oldParticles = this.getSelectedParticles();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getParticle(main.getInstance().getSelection(i)).setMomIndex(CB_Particle.Y, ((Number)momentumYF.getValue()).doubleValue());
		}
		main.getInstance().updateParticles();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldParticles, this.getSelectedParticles()));
	}
	//set the z momentum of the selected particle
	public void setMomentumZ() {
		CB_Particles oldParticles = this.getSelectedParticles();
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			main.getParticle(main.getInstance().getSelection(i)).setMomIndex(CB_Particle.Z, ((Number)momentumZF.getValue()).doubleValue());
		}
		main.getInstance().updateParticles();
		main.getInstance().addUndoableEdit(createUndoableEdit(oldParticles, this.getSelectedParticles()));
	}
	//action events
	public void actionPerformed(ActionEvent e) {
		if (systemChange) {
			return;
		}
		if (main.getInstance().getSelectionSize() == 1) {
			this.setProperties(
				nameTextField.getText(),
				colorChooser.getSelectedIndex(),
				((Number)radiusF.getValue()).doubleValue(),
				((Number)massF.getValue()).doubleValue(),
				((Number)chargeF.getValue()).doubleValue(),
				new double[] {
					main.getInstance().checkParticlePosition(CB_Particle.X, ((Number)positionXF.getValue()).doubleValue()),
					main.getInstance().checkParticlePosition(CB_Particle.Y, ((Number)positionYF.getValue()).doubleValue()),
					main.getInstance().checkParticlePosition(CB_Particle.Z, ((Number)positionZF.getValue()).doubleValue())
				},
				new double[] {
					((Number)momentumXF.getValue()).doubleValue(),
					((Number)momentumYF.getValue()).doubleValue(),
					((Number)momentumZF.getValue()).doubleValue()
				}
			);
		} else {
			Object event = e.getSource();
			if (event == nameTextField) {
				this.setName();
			} else if (event == colorChooser) {
				this.setColor();
			} else if (event == radiusF) {
				this.setRadius();
			} else if (event == massF) {
				this.setMass();
			} else if (event == chargeF ) {
				this.setCharge();
			} else if (event == positionXF) {
				this.setPositionX();
			} else if (event == positionYF) {
				this.setPositionY();
			} else if (event == positionZF) {
				this.setPositionZ();
			} else if (event == momentumXF) {
				this.setMomentumX();
			} else if (event == momentumYF) {
				this.setMomentumY();
			} else if (event == momentumZF) {
				this.setMomentumZ();
			}
		}
		this.updateSelection();
	}
}