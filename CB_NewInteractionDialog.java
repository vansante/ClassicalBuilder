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
import java.util.Arrays;

// This class creates the Interaction Dialog and saves the input into the main.class

public class CB_NewInteractionDialog extends JDialog implements ActionListener {

	private final CB_Instance instance;
	private final int[] particles;
	//ComboBox
	private final JComboBox interactionChooser;
	//Buttons
	private final JButton upButton, downButton, okButton, cancelButton;
	//List
	private final CB_ParticleList particleList;
	//Textfields
	private final JTextField nameTF;
	private final JSpinner forceF, equilibriumF, exponentialF;
	private final JSpinner a1F, a2F, a3F, degreeF, periodF;
	//Labels
	private final JLabel forceL, equilibriumL, exponentialL, typeL;
	private final JLabel a1L, a2L, a3L, degreeL, periodL, nameL;

	public CB_NewInteractionDialog(CB_Instance instance, int[] particles) {
		super(instance.getMain(), "New Interaction", true);
		this.instance = instance;
		this.particles = particles;
		this.setSize(400, 375);
		this.setResizable(false);
		this.setLocationRelativeTo(instance.getMain());
		this.setLayout(new BorderLayout(2, 2));

		//comboBox
		interactionChooser = new JComboBox(CB_Interaction.NAMES);
		interactionChooser.setSelectedIndex(-1);
		interactionChooser.setPreferredSize(new Dimension(0,23));
		interactionChooser.addActionListener(this);

		//Labels
		forceL = new JLabel("Force:");
		equilibriumL = new JLabel("Equilibrium Distance:");
		exponentialL = new JLabel("Exponential Parameter:");
		a1L = new JLabel("A1:");
		a2L = new JLabel("A2:");
		a3L = new JLabel("A3:");
		degreeL = new JLabel("Degree:");
		periodL = new JLabel("Period:");
		nameL = new JLabel("Interaction Name:");
		typeL = new JLabel("Interaction Type:");

		//Textfields
		forceF = CB_Tools.createDoubleField(1.0);
		equilibriumF = CB_Tools.createDoubleField(0.0);
		exponentialF = CB_Tools.createDoubleField(0.0);
		a1F = CB_Tools.createDoubleField(0.0);
		a2F = CB_Tools.createDoubleField(0.0);
		a3F = CB_Tools.createDoubleField(0.0);
		degreeF = CB_Tools.createDoubleField(0.0);
		periodF = CB_Tools.createDoubleField(0.0);
		nameTF = new JTextField("Interaction " + (instance.getInteractions().size() + 1));
		nameTF.addActionListener(this);

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

		//List
		particleList = new CB_ParticleList(instance, particles);

		particleList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		// Particle list scroller
		JScrollPane particleScroll = new JScrollPane(particleList);
		particleScroll.setPreferredSize(new Dimension(135,0));

		// Right panel
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout(2, 2));

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
		panelNameType.add(interactionChooser);

		rightPanel.add(panelNameType, BorderLayout.NORTH);

		// propertiesPanel
		JPanel propertiesPanel = new JPanel();
		propertiesPanel.setLayout(new GridLayout(8, 2, 2, 2));
		propertiesPanel.setBorder(BorderFactory.createTitledBorder("Properties"));

		propertiesPanel.add(forceL);
		propertiesPanel.add(forceF);
		propertiesPanel.add(equilibriumL);
		propertiesPanel.add(equilibriumF);
		propertiesPanel.add(exponentialL);
		propertiesPanel.add(exponentialF);
		propertiesPanel.add(a1L);
		propertiesPanel.add(a1F);
		propertiesPanel.add(a2L);
		propertiesPanel.add(a2F);
		propertiesPanel.add(a3L);
		propertiesPanel.add(a3F);
		propertiesPanel.add(degreeL);
		propertiesPanel.add(degreeF);
		propertiesPanel.add(periodL);
		propertiesPanel.add(periodF);
		rightPanel.add(propertiesPanel, BorderLayout.CENTER);

		// buttonPanel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

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
	//disable all the textfields and labels
	public void disableAll() {
		forceF.setEnabled(false);
		forceL.setEnabled(false);
		equilibriumF.setEnabled(false);
		equilibriumL.setEnabled(false);
		exponentialF.setEnabled(false);
		exponentialL.setEnabled(false);
		a1F.setEnabled(false);
		a1L.setEnabled(false);
		a2F.setEnabled(false);
		a2L.setEnabled(false);
		a3F.setEnabled(false);
		a3L.setEnabled(false);
		degreeF.setEnabled(false);
		degreeL.setEnabled(false);
		periodF.setEnabled(false);
		periodL.setEnabled(false);
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
		} else if (event == interactionChooser) {
			this.disableAll();
			//enable textfields and labels depending on which interaction is selected
			switch (interactionChooser.getSelectedIndex()) {
				case CB_Interaction.GRAVITATIONAL:
					forceF.setEnabled(true);
					forceL.setEnabled(true);
				break;
				case CB_Interaction.COULOMB:
					forceF.setEnabled(true);
					forceL.setEnabled(true);
				break;
				case CB_Interaction.LENNARD_JONES:
					forceF.setEnabled(true);
					forceL.setEnabled(true);
					equilibriumF.setEnabled(true);
					equilibriumL.setEnabled(true);
				break;
				case CB_Interaction.MORSE:
					forceF.setEnabled(true);
					forceL.setEnabled(true);
					equilibriumF.setEnabled(true);
					equilibriumL.setEnabled(true);
					exponentialF.setEnabled(true);
					exponentialL.setEnabled(true);
				break;
				case CB_Interaction.RYDBERG:
					forceF.setEnabled(true);
					forceL.setEnabled(true);
					equilibriumF.setEnabled(true);
					equilibriumL.setEnabled(true);
					exponentialF.setEnabled(true);
					exponentialL.setEnabled(true);
					a1F.setEnabled(true);
					a1L.setEnabled(true);
					a2F.setEnabled(true);
					a2L.setEnabled(true);
					a3F.setEnabled(true);
					a3L.setEnabled(true);
				break;
				case CB_Interaction.HARMONIC_STRETCH:
					forceF.setEnabled(true);
					forceL.setEnabled(true);
					equilibriumF.setEnabled(true);
					equilibriumL.setEnabled(true);
				break;
				case CB_Interaction.HARMONIC_BENDING:
					forceF.setEnabled(true);
					forceL.setEnabled(true);
					degreeF.setEnabled(true);
					degreeL.setEnabled(true);
				break;
				case CB_Interaction.PERIODIC_TORSIONAL:
					forceF.setEnabled(true);
					forceL.setEnabled(true);
					degreeF.setEnabled(true);
					degreeL.setEnabled(true);
					periodF.setEnabled(true);
					periodL.setEnabled(true);
				break;
			}
		} else if (event == cancelButton) {
			this.setVisible(false);
		} else {
			//check if an interaction can be added
			if (nameTF.getText().length() < 1) {
				JOptionPane.showMessageDialog(this, "Please supply a name for the interaction", "Warning", JOptionPane.WARNING_MESSAGE);
			} else if (interactionChooser.getSelectedIndex() < 0) {
				JOptionPane.showMessageDialog(this, "Please select an interaction type", "Warning", JOptionPane.WARNING_MESSAGE);
			} else {
				CB_Interaction interaction = new CB_Interaction(particleList.getParticles(), interactionChooser.getSelectedIndex(), nameTF.getText());
				//add an interaction and set the interaction values
				interaction.setForce(((Double)forceF.getValue()).doubleValue());
				interaction.setEquilibrium(((Double)equilibriumF.getValue()).doubleValue());
				interaction.setExponential(((Double)exponentialF.getValue()).doubleValue());
				interaction.setA1(((Double)a1F.getValue()).doubleValue());
				interaction.setA2(((Double)a2F.getValue()).doubleValue());
				interaction.setA3(((Double)a3F.getValue()).doubleValue());
				interaction.setDegree(((Double)degreeF.getValue()).doubleValue());
				interaction.setPeriod(((Double)periodF.getValue()).doubleValue());
				instance.addRelation(CB_Instance.INTERACTIONS, interaction);
				this.setVisible(false);
			}
		}
	}
}