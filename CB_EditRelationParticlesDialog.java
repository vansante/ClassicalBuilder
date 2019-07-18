/**
 * Copyright (C) 2006-2007 Paul van Santen & Erik Kerkvliet
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
import java.util.Arrays;

//this class creates a dialog in which you can edit an interaction or constraint
public class CB_EditRelationParticlesDialog extends JDialog implements ActionListener {

	private final CB_Instance instance;
	private final CB_ParticleList particleList, relationParticleList;
	private final JButton upButton, downButton, addButton, removeButton, okButton, cancelButton;
	private final int type;
	private final int relationId;

	public CB_EditRelationParticlesDialog(CB_Instance instance, int type, int relationId) {
		super(instance.getMain(), "Edit Relation Particles", true);
		this.instance = instance;
		this.type = type;
		this.relationId = relationId;
		this.setSize(400, 400);
		this.setLocationRelativeTo(instance.getMain());
		this.setLayout(new BorderLayout(2, 2));

		// Buttons
		upButton = new JButton("Up");
		upButton.addActionListener(this);
		downButton = new JButton("Down");
		downButton.addActionListener(this);
		addButton = new JButton(">>");
		addButton.setPreferredSize(new Dimension(40,25));
		addButton.addActionListener(this);
		removeButton = new JButton("<<");
		removeButton.setPreferredSize(new Dimension(40,25));
		removeButton.addActionListener(this);
		okButton = new JButton("Ok");
		okButton.setPreferredSize(new Dimension(50,25));
		okButton.addActionListener(this);
		okButton.setMnemonic(KeyEvent.VK_O);
		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(50,25));
		cancelButton.addActionListener(this);
		cancelButton.setMnemonic(KeyEvent.VK_C);

		// List with all available particles
		particleList = new CB_ParticleList(instance);
		JScrollPane particleListScroll = new JScrollPane(particleList);

		JPanel particleListPanel = new JPanel();
		particleListPanel.setLayout(new BorderLayout(2, 2));
		particleListPanel.setPreferredSize(new Dimension(150, 0));
		particleListPanel.setBorder(BorderFactory.createTitledBorder("All Particles"));
		particleListPanel.add(particleListScroll, BorderLayout.CENTER);

		// Get the array with the relation particles
		int[] relationParticles = null;
		if (type == CB_Instance.INTERACTIONS) {
			relationParticles = new int[instance.getInteraction(relationId).getParticlesSize()];
			for (int i = 0; i < instance.getInteraction(relationId).getParticlesSize(); i++) {
				relationParticles[i] = instance.getInteraction(relationId).getParticle(i);
			}
		} else if (type == CB_Instance.CONSTRAINTS) {
			relationParticles = new int[instance.getConstraint(relationId).getParticlesSize()];
			for (int i = 0; i < instance.getConstraint(relationId).getParticlesSize(); i++) {
				relationParticles[i] = instance.getConstraint(relationId).getParticle(i);
			}
		}

		// List with the current relation particles
		relationParticleList = new CB_ParticleList(instance, relationParticles);
		JScrollPane relationParticleListScroll = new JScrollPane(relationParticleList);

		JPanel particleRelationListPanel = new JPanel();
		particleRelationListPanel.setLayout(new BorderLayout(2, 2));
		particleRelationListPanel.setBorder(BorderFactory.createTitledBorder("Relation Particles"));
		particleRelationListPanel.add(relationParticleListScroll, BorderLayout.CENTER);
		JPanel movePanel = new JPanel();
		movePanel.setLayout(new GridLayout(1, 2, 2, 2));
		movePanel.add(upButton);
		movePanel.add(downButton);
		particleRelationListPanel.add(movePanel, BorderLayout.SOUTH);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.add(Box.createVerticalGlue());
		JPanel centerButtonPanel = new JPanel();
		centerButtonPanel.add(addButton);
		centerButtonPanel.add(removeButton);
		centerPanel.add(centerButtonPanel);
		centerPanel.add(Box.createVerticalGlue());

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1, 3, 2, 2));
		topPanel.add(particleListPanel);
		topPanel.add(centerPanel);
		topPanel.add(particleRelationListPanel);

		JPanel okCancelPanel = new JPanel();
		okCancelPanel.add(okButton);
		okCancelPanel.add(cancelButton);

		this.add(topPanel, BorderLayout.CENTER);
		this.add(okCancelPanel, BorderLayout.SOUTH);

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				okButton.requestFocusInWindow();
			}
		});
	}
	//action events
	public void actionPerformed(ActionEvent e) {
		Object event = e.getSource();
		if (event == upButton) {
			//if upbutton is pressed move the selected particle one place up in the list
			relationParticleList.moveUp(1);
		} else if (event == downButton) {
			//if downbutton is pressed move the selected particle one place down in the list
			relationParticleList.moveDown(1);
		} else if (event == addButton) {
			//if addbutton is pressed move the selected particle into the list containing the particles inside the relation
			relationParticleList.add(particleList.getSelectedIndices());
		} else if (event == removeButton) {
			//if removebutton is pressed move the selected particle out of the list containing the particles inside the relation
			relationParticleList.remove();
		} else if (event == cancelButton) {
			this.setVisible(false);
		} else if (event == okButton) {//if okbutton is pressed save all the changes
			instance.editRelationParticles(type, relationId, relationParticleList.getParticles());
			this.setVisible(false);
		}
	}
}