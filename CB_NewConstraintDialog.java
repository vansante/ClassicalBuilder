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
import java.util.Arrays;
import javax.swing.*;

//this class creates a new dialog in which you can add a new interaction
public class CB_NewConstraintDialog extends JDialog implements ActionListener {
	private final CB_Instance instance;
	private final int[] particles;
	//List
	private final CB_ParticleList particleList;
	// Buttons
	private final JButton upButton, downButton, okButton, cancelButton;
	private final JTextField nameTF;
	private final JSpinner distanceF, maxstepF, errorF;
	private final JCheckBox distanceCB, maxstepCB, errorCB;
	//labels
	private final JLabel nameL, typeL;
	//other
	private final JComboBox constraintChooser;

    public CB_NewConstraintDialog(CB_Instance instance, int[] particles) {
    	super(instance.getMain(), "New Constraint", true);
		this.instance = instance;
		this.particles = particles;
		this.setSize(400, 375);
		this.setResizable(false);
		this.setLocationRelativeTo(instance.getMain());
		this.setLayout(new BorderLayout(2, 2));

		// Constraint combobox
		constraintChooser = new JComboBox(CB_Constraint.NAMES);
		constraintChooser.setSelectedIndex(-1);
		constraintChooser.setPreferredSize(new Dimension(0,23));
		constraintChooser.addActionListener(this);

		// Buttons
		upButton = new JButton("Up");
		upButton.setPreferredSize(new Dimension(50,25));
		upButton.addActionListener(this);
		downButton = new JButton("Down");
		downButton.setPreferredSize(new Dimension(50,25));
		downButton.addActionListener(this);
		okButton = new JButton("Ok");
		okButton.setPreferredSize(new Dimension(50,25));
		okButton.addActionListener(this);
		okButton.setMnemonic(KeyEvent.VK_O);
		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(50,25));
		cancelButton.addActionListener(this);
		cancelButton.setMnemonic(KeyEvent.VK_C);

		// Particle List
		particleList = new CB_ParticleList(instance, particles);

		particleList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		// Particle list scroller
		JScrollPane particleScroll =  new JScrollPane(particleList);

		nameL = new JLabel("Constraint Name:");
		typeL = new JLabel("Constraint Type:");

		nameTF = createTextField("Constraint " + (instance.getConstraints().size() + 1));
		distanceF = CB_Tools.createDoubleField(1.0);
		maxstepF = CB_Tools.createIntField(1000);
		errorF = CB_Tools.createDoubleField(1.0E-12);

		distanceCB = this.createCheckBox("Distance:");
		maxstepCB = this.createCheckBox("Maxstep:");
		errorCB = this.createCheckBox("Error:");

		// Particles panel
		JPanel particlesPanel = new JPanel();
		particlesPanel.setLayout(new BorderLayout(2, 2));
		particlesPanel.setBorder(BorderFactory.createTitledBorder("Particles"));
		particlesPanel.add(particleScroll, BorderLayout.CENTER);

		JPanel movePanel = new JPanel();
		movePanel.setLayout(new GridLayout(1, 2, 2, 2));
		movePanel.add(upButton);
		movePanel.add(downButton);

		particlesPanel.add(movePanel, BorderLayout.SOUTH);

		JPanel panelNameType = new JPanel();
		panelNameType.setLayout(new GridLayout(2, 2, 2, 2));
		panelNameType.setBorder(BorderFactory.createTitledBorder("Name & Type"));
		panelNameType.add(nameL);
		panelNameType.add(nameTF);
		panelNameType.add(typeL);
		panelNameType.add(constraintChooser);

		JPanel propertiesPanel = new JPanel();
		propertiesPanel.setBorder(BorderFactory.createTitledBorder("Properties"));
		propertiesPanel.setPreferredSize(new Dimension(0,100));
		propertiesPanel.setLayout(new GridLayout(3,2,2,2));
		propertiesPanel.add(distanceCB);
		propertiesPanel.add(distanceF);
		propertiesPanel.add(maxstepCB);
		propertiesPanel.add(maxstepF);
		propertiesPanel.add(errorCB);
		propertiesPanel.add(errorF);

		// Right panel
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout(2, 2));
		rightPanel.add(panelNameType, BorderLayout.NORTH);
		rightPanel.add(propertiesPanel, BorderLayout.SOUTH);

		// buttonPanel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		particlesPanel.setPreferredSize(new Dimension(150,100));

		this.add(buttonPanel, BorderLayout.SOUTH);
		this.add(particlesPanel, BorderLayout.WEST);
		this.add(rightPanel, BorderLayout.CENTER);

		this.disableAll();

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				okButton.requestFocusInWindow();
			}
		});
	}
	//create textFields
	public JTextField createTextField(String value) {
		JTextField textField = new JTextField(value);
		textField.addActionListener(this);
		textField.setPreferredSize(new Dimension(0, 20));
		return textField;
	}
	//create textFields
	public JCheckBox createCheckBox(String value) {
		JCheckBox checkBox = new JCheckBox(value);
		checkBox.addActionListener(this);
		checkBox.setPreferredSize(new Dimension(0, 20));
		return checkBox;
	}
	//disable all the textfields and labels
	public void disableAll() {
		distanceF.setEnabled(false);
		maxstepF.setEnabled(false);
		errorF.setEnabled(false);
		distanceCB.setEnabled(false);
		maxstepCB.setEnabled(false);
		errorCB.setEnabled(false);
	}
	// The actionlistener for the buttons
	public void actionPerformed(ActionEvent e) {
		// Get the source button
		Object event = e.getSource();
		// Do the action that for the button
		if (event == upButton) {
			//move the selected particle up one place in the list
			particleList.moveUp(1);
		} else if (event == downButton) {
			//move the selected particle down one place in the list
			particleList.moveDown(1);
		} else if (event == distanceCB) {
			distanceF.setEnabled(distanceCB.isSelected());
		}  else if (event == errorCB) {
			errorF.setEnabled(errorCB.isSelected());
		} else if (event == maxstepCB) {
			maxstepF.setEnabled(maxstepCB.isSelected());
		} else if (event == constraintChooser) {
			this.disableAll();
			//enable textfields and labels depending on which constraint is selected
			switch (constraintChooser.getSelectedIndex()) {
				case CB_Constraint.DISTANCE:
					distanceF.setEnabled(distanceCB.isSelected());
					maxstepF.setEnabled(maxstepCB.isSelected());
					errorF.setEnabled(errorCB.isSelected());
					distanceCB.setEnabled(true);
					maxstepCB.setEnabled(true);
					errorCB.setEnabled(true);

				break;
			}
		} else if (event == cancelButton) {
			this.setVisible(false);
		} else {
			//check if an constraint can be added
			if (nameTF.getText().length() < 1) {
				JOptionPane.showMessageDialog(this, "Please supply a name for the constraint", "Warning", JOptionPane.WARNING_MESSAGE);
			} else if (constraintChooser.getSelectedIndex() < 0) {
				JOptionPane.showMessageDialog(this, "Please select a constraint type", "Warning", JOptionPane.WARNING_MESSAGE);
			} else {
				CB_Constraint constraint = new CB_Constraint(particleList.getParticles(), constraintChooser.getSelectedIndex(), nameTF.getText());
				//add an constraint and set the constraint values
				constraint.setDistance(((Double)distanceF.getValue()).doubleValue());
				constraint.setMaxstep(((Integer)maxstepF.getValue()).intValue());
				constraint.setError(((Double)errorF.getValue()).doubleValue());
				constraint.enableDistance(distanceCB.isSelected());
				constraint.enableMaxstep(maxstepCB.isSelected());
				constraint.enableError(errorCB.isSelected());
				instance.addRelation(CB_Instance.CONSTRAINTS, constraint);
				this.setVisible(false);
			}
		}
	}
}