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
import java.util.ArrayList;

public class CB_ParticleMenu extends JPopupMenu implements ActionListener {

	private final CB_Instance instance;
	private int particle;
	private int[] particles;

	private JMenuItem  mCopy, mCut, mDelete, mSelectAll, mDeselect;
	private JMenu addToInteraction, removeFromInteraction, selectInteraction;
	private JMenu addToConstraint, removeFromConstraint, selectConstraint;
	private JMenuItem[] interactionParticleList, interactionSelectionList;
	private JMenuItem[] constraintParticleList, constraintSelectionList;
	private JMenuItem mInteractionAll, mConstraintAll;

	private static final int ACTION_ADD_INT = 0;
	private static final int ACTION_REMOVE_INT = 1;
	private static final int ACTION_ADD_CON = 2;
	private static final int ACTION_REMOVE_CON = 3;
	private static final int ACTION_SELECT_INT = 4;
	private static final int ACTION_SELECT_CON = 5;

	public CB_ParticleMenu(CB_Instance instance, int particle) {
		this.instance = instance;
		this.particle = particle;
		// First the easy actions
		mCut = CB_Tools.createMenuItem(this, "Cut", "cut.png", this);
		mCopy = CB_Tools.createMenuItem(this, "Copy", "copy.png", this);
		mDelete = CB_Tools.createMenuItem(this, "Delete", "delete.png", this);
		this.addSeparator();
		// The select interaction actions
		selectInteraction = CB_Tools.createSubMenu(this, "Select Particle Interaction", "interaction.png");
		mInteractionAll = CB_Tools.createSubItem(selectInteraction, "Select All", this, CB_Tools.getIcon("select.png"));
		selectInteraction.addSeparator();
		// The select constraint actions
		selectConstraint = CB_Tools.createSubMenu(this, "Select Particle Constraint", "constraint.png");
		mConstraintAll = CB_Tools.createSubItem(selectConstraint, "Select All", this, CB_Tools.getIcon("select.png"));
		selectConstraint.addSeparator();
		this.addSeparator();
		// Now the add to and remove from interactions
		addToInteraction = CB_Tools.createSubMenu(this, "Add to Interaction", "newinteraction.png");
		removeFromInteraction = CB_Tools.createSubMenu(this, "Remove from Interaction", "delinteraction.png");
		this.addSeparator();
		// Now the add to and remove from constraints
		addToConstraint = CB_Tools.createSubMenu(this, "Add to Constraint", "newconstraint.png");
		removeFromConstraint = CB_Tools.createSubMenu(this, "Remove from Constraint", "delconstraint.png");

		interactionParticleList = new JMenuItem[instance.getInteractions().size()];
		interactionSelectionList = new JMenuItem[instance.getInteractions().size()];
		constraintParticleList = new JMenuItem[instance.getConstraints().size()];
		constraintSelectionList = new JMenuItem[instance.getConstraints().size()];

		Icon interactionIcon = CB_Tools.getIcon("interaction.png");
		Icon constraintIcon = CB_Tools.getIcon("constraint.png");

		for (int i = 0; i < instance.getInteractions().size(); i++) {
			if (instance.getInteraction(i).getParticleIndex(particle) == -1) {
				interactionParticleList[i] = CB_Tools.createSubItem(
					addToInteraction,
					instance.getInteraction(i).getName(),
					new ParticleAction(i, ACTION_ADD_INT),
					interactionIcon
				);
			} else {
				interactionParticleList[i] = CB_Tools.createSubItem(
					removeFromInteraction,
					instance.getInteraction(i).getName(),
					new ParticleAction(i, ACTION_REMOVE_INT),
					interactionIcon
				);
				interactionSelectionList[i] = CB_Tools.createSubItem(
					selectInteraction,
					instance.getInteraction(i).getName(),
					new ParticleAction(i, ACTION_SELECT_INT),
					interactionIcon
				);
			}
		}
		for (int i = 0; i < instance.getConstraints().size(); i++) {
			if (instance.getConstraint(i).getParticleIndex(particle) == -1) {
				constraintParticleList[i] = CB_Tools.createSubItem(
					addToConstraint,
					instance.getConstraint(i).getName(),
					new ParticleAction(i, ACTION_ADD_CON),
					constraintIcon
				);
			} else {
				constraintParticleList[i] = CB_Tools.createSubItem(
					removeFromConstraint,
					instance.getConstraint(i).getName(),
					new ParticleAction(i, ACTION_REMOVE_CON),
					constraintIcon
				);
				constraintSelectionList[i] = CB_Tools.createSubItem(
					selectConstraint,
					instance.getConstraint(i).getName(),
					new ParticleAction(i, ACTION_SELECT_CON),
					constraintIcon
				);
			}
		}
	}
	public CB_ParticleMenu(CB_Instance instance, int[] particles) {
		this.instance = instance;
		this.particles = particles;
		mCut = CB_Tools.createMenuItem(this, "Cut", "cut.png", this);
		mCopy = CB_Tools.createMenuItem(this, "Copy", "copy.png", this);
		mDelete = CB_Tools.createMenuItem(this, "Delete", "delete.png", this);
	}
	public CB_ParticleMenu(CB_Instance instance) {
		this.instance = instance;
		mSelectAll = CB_Tools.createMenuItem(this, "Select All", "select.png", this);
		mDeselect = CB_Tools.createMenuItem(this, "Deselect", "deselect.png", this);
	}
	public void actionPerformed(ActionEvent e) {
		// Get the source menu item
		Object event = e.getSource();
		// Do the action for the menu item
		if (event == mCut) {
			instance.getMain().cut();
		} else if (event == mCopy) {
			instance.getMain().copy();
		} else if (event == mDelete) {
			instance.removeSelectedItems();
		} else if (event == mSelectAll) {
			instance.selectAll(CB_Instance.PARTICLES);
		} else if (event == mDeselect) {
			instance.deselect();
		} else if (event == mInteractionAll) {
			ArrayList<Integer> newSelection = new ArrayList<Integer>();
			for (int i = 0; i < instance.getInteractions().size(); i++) {
				if (instance.getInteraction(i).getParticleIndex(particle) > -1) {
					newSelection.add(i);
				}
			}
			int[] selection = new int[newSelection.size()];
			for (int i = 0; i < newSelection.size(); i++) {
				selection[i] = newSelection.get(i);
			}
			instance.select(CB_Instance.INTERACTIONS, selection);
		} else if (event == mConstraintAll) {
			ArrayList<Integer> newSelection = new ArrayList<Integer>();
			for (int i = 0; i < instance.getConstraints().size(); i++) {
				if (instance.getConstraint(i).getParticleIndex(particle) > -1) {
					newSelection.add(i);
				}
			}
			int[] selection = new int[newSelection.size()];
			for (int i = 0; i < newSelection.size(); i++) {
				selection[i] = newSelection.get(i);
			}
			instance.select(CB_Instance.CONSTRAINTS, selection);
		}
	}
	public class ParticleAction implements ActionListener {
		private int relation;
		private int action;

		public ParticleAction(int relation, int action) {
			this.relation = relation;
			this.action = action;
		}
		public void actionPerformed(ActionEvent e) {
			switch (action) {
				case ACTION_ADD_INT:
					instance.addRelationParticle(CB_Instance.INTERACTIONS, relation, particle);
				break;
				case ACTION_REMOVE_INT:
					instance.removeRelationParticle(CB_Instance.INTERACTIONS, relation, particle);
				break;
				case ACTION_ADD_CON:
					instance.addRelationParticle(CB_Instance.CONSTRAINTS, relation, particle);
				break;
				case ACTION_REMOVE_CON:
					instance.removeRelationParticle(CB_Instance.CONSTRAINTS, relation, particle);
				break;
				case ACTION_SELECT_INT:
					instance.select(CB_Instance.INTERACTIONS, new int[] {relation});
				break;
				case ACTION_SELECT_CON:
					instance.select(CB_Instance.CONSTRAINTS, new int[] {relation});
				break;
			}
		}
	}
}