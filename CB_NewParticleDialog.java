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

//this class creates a new dialog in which you can add a new particle
public class CB_NewParticleDialog extends JDialog implements ActionListener {

	private final CB_Instance instance;

	private final JTextField nameTF;
	private final JSpinner posXF, posYF, posZF;
	private final JSpinner massF, chargeF, radiusF;
	private final JSpinner momXF, momYF, momZF;
	private final JSpinner amountF;

	private final JButton okButton, cancelButton;
	private final JComboBox colorChooser;

	public CB_NewParticleDialog(CB_Instance instance) {
		super(instance.getMain(), "New Particle", true);
		this.instance = instance;
		this.setSize(400, 235);
		this.setResizable(false);
		this.setLocationRelativeTo(instance.getMain());
		this.getContentPane().setLayout(new BorderLayout());

		// Labels
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setPreferredSize(new Dimension(40, 24));
		JLabel colorLabel = new JLabel("Color:");
		colorLabel.setPreferredSize(new Dimension(40, 20));

		//TextFields
		nameTF = this.createTextField("Particle " + (instance.getParticles().size() + 1));

		massF = CB_Tools.createDoubleField(1.0, 0.0, Double.MAX_VALUE);
		chargeF = CB_Tools.createDoubleField(1.0);
		radiusF = CB_Tools.createDoubleField(0.5, 0.0, Double.MAX_VALUE);

		posXF = CB_Tools.createDoubleField(0.0, - (instance.getBox().getDimension(CB_Box.WIDTH) / 2), (instance.getBox().getDimension(CB_Box.WIDTH) / 2));
		posYF = CB_Tools.createDoubleField(0.0, - (instance.getBox().getDimension(CB_Box.HEIGHT) / 2), (instance.getBox().getDimension(CB_Box.HEIGHT) / 2));
		posZF = CB_Tools.createDoubleField(0.0, - (instance.getBox().getDimension(CB_Box.DEPTH) / 2), (instance.getBox().getDimension(CB_Box.DEPTH) / 2));

		momXF = CB_Tools.createDoubleField(0.0);
		momYF = CB_Tools.createDoubleField(0.0);
		momZF = CB_Tools.createDoubleField(0.0);

		// Color selector
		colorChooser = new JComboBox();
		colorChooser.setPreferredSize(new Dimension(120, 23));

		for ( int i = 0; i < instance.getColors().size(); i++ ) {
			colorChooser.addItem(instance.getColor(i).getName());
		}

		amountF = CB_Tools.createIntField(1, 1, 25);

		// Buttons
		okButton = new JButton("Ok");
		okButton.setPreferredSize(new Dimension(50, 25));
		okButton.addActionListener(this);
		okButton.setMnemonic(KeyEvent.VK_O);
		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(50, 25));
		cancelButton.addActionListener(this);
		cancelButton.setMnemonic(KeyEvent.VK_C);

		// Amount of the particles to be added panel
		JPanel amountPanel = new JPanel(new BorderLayout(2, 2));
		amountPanel.add(new JLabel("Amount:"), BorderLayout.WEST);
		amountPanel.add(amountF, BorderLayout.EAST);

		//PanelNorth
		//panel to place the labels and textfields in the center
		JPanel genericInfoPanel = new JPanel();
		genericInfoPanel.setLayout(new GridLayout(2, 3, 10, 5));
		genericInfoPanel.setPreferredSize(new Dimension(350, 50));
		genericInfoPanel.add(nameLabel);
		genericInfoPanel.add(nameTF);
		genericInfoPanel.add(amountPanel);
		genericInfoPanel.add(colorLabel);
		genericInfoPanel.add(colorChooser);

		JPanel northPanel = new JPanel();
		northPanel.setBorder(BorderFactory.createTitledBorder(""));
		northPanel.add(genericInfoPanel);

		//Properties
		JPanel propertiesLabelPanel = new JPanel();
		propertiesLabelPanel.setLayout(new GridLayout(3,1,2,2));
		propertiesLabelPanel.add(new JLabel("Mass:"));
		propertiesLabelPanel.add(new JLabel("Charge:"));
		propertiesLabelPanel.add(new JLabel("Radius:"));

		//PropertiesTextFields
		JPanel propertiesTextPanel = new JPanel();
		propertiesTextPanel.setLayout(new GridLayout(3,1,2,2));
		propertiesTextPanel.add(massF);
		propertiesTextPanel.add(chargeF);
		propertiesTextPanel.add(radiusF);

		//PropertiesPanel
		JPanel propertiesPanel = new JPanel();
		propertiesPanel.setBorder(BorderFactory.createTitledBorder("Properties"));
		propertiesPanel.setLayout(new BorderLayout(2,2));
		propertiesPanel.add(propertiesLabelPanel, BorderLayout.WEST);
		propertiesPanel.add(propertiesTextPanel, BorderLayout.CENTER);

		//PositionLabels
		JPanel positionLabelPanel = new JPanel();
		positionLabelPanel.setLayout(new GridLayout(3,1,2,2));
		positionLabelPanel.add(new JLabel("X:"));
		positionLabelPanel.add(new JLabel("Y:"));
		positionLabelPanel.add(new JLabel("Z:"));

		JPanel positionTextPanel = new JPanel();
		positionTextPanel.setLayout(new GridLayout(3,1,2,2));
		positionTextPanel.add(posXF);
		positionTextPanel.add(posYF);
		positionTextPanel.add(posZF);

		JPanel positionPanel = new JPanel();
		positionPanel.setBorder(BorderFactory.createTitledBorder("Position"));
		positionPanel.setLayout(new BorderLayout(2,2));
		positionPanel.add(positionLabelPanel, BorderLayout.WEST);
		positionPanel.add(positionTextPanel, BorderLayout.CENTER);

		// Momentum
		JPanel momentumLabelPanel = new JPanel();
		momentumLabelPanel.setLayout(new GridLayout(3,1,2,2));
		momentumLabelPanel.add(new JLabel("X:"));
		momentumLabelPanel.add(new JLabel("Y:"));
		momentumLabelPanel.add(new JLabel("Z:"));

		JPanel momentumTextPanel = new JPanel();
		momentumTextPanel.setLayout(new GridLayout(3,1,2,2));
		momentumTextPanel.add(momXF);
		momentumTextPanel.add(momYF);
		momentumTextPanel.add(momZF);

		JPanel momentumPanel = new JPanel();
		momentumPanel.setBorder(BorderFactory.createTitledBorder("Momentum"));
		momentumPanel.setLayout(new BorderLayout(2,2));
		momentumPanel.add(momentumLabelPanel, BorderLayout.WEST);
		momentumPanel.add(momentumTextPanel, BorderLayout.CENTER);

		//final panelcenter
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(1,3));
		centerPanel.add(propertiesPanel);
		centerPanel.add(positionPanel);
		centerPanel.add(momentumPanel);

		//Panel South
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout());
		southPanel.add(okButton);
		southPanel.add(cancelButton);

		//add all the panels to the dialog
		this.add(northPanel,BorderLayout.NORTH);
		this.add(centerPanel,BorderLayout.CENTER);
		this.add(southPanel,BorderLayout.SOUTH);

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				okButton.requestFocusInWindow();
			}
		});
	}
	// Create and return a textfield
	public JTextField createTextField(String value) {
		JTextField textField = new JTextField(value);
		textField.setPreferredSize(new Dimension(0, 20));
		textField.addActionListener(this);
		return textField;
	}
	// The actionlistener for the buttons
	public void actionPerformed(ActionEvent e) {
		// Get the source button
		Object event = e.getSource();
		// Do the action that for the button
		if (event == cancelButton) {
			this.setVisible(false);
		} else {
			if (nameTF.getText().length() > 0) {
				int amount = ((Integer)amountF.getValue()).intValue();
				CB_Particles newParticles = new CB_Particles(amount);
				CB_Particle newParticle;
				for (int i = 0; i < amount; i++) {
					newParticle = new CB_Particle();
					newParticle.setName(nameTF.getText());
					newParticle.setColor(colorChooser.getSelectedIndex());
					newParticle.setPosIndex(0, ((Double)posXF.getValue()).doubleValue());
					newParticle.setPosIndex(1, ((Double)posYF.getValue()).doubleValue());
					newParticle.setPosIndex(2, ((Double)posZF.getValue()).doubleValue());
					newParticle.setMomIndex(0, ((Double)momXF.getValue()).doubleValue());
					newParticle.setMomIndex(1, ((Double)momYF.getValue()).doubleValue());
					newParticle.setMomIndex(2, ((Double)momZF.getValue()).doubleValue());
					newParticle.setMass(((Double)massF.getValue()).doubleValue());
					newParticle.setCharge(((Double)chargeF.getValue()).doubleValue());
					newParticle.setRadius(((Double)radiusF.getValue()).doubleValue());
					newParticles.add(newParticle);
				}
				instance.addParticles(newParticles);
				this.setVisible(false);
			} else {
				JOptionPane.showMessageDialog(this, "Please supply a name for the particle.", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
}