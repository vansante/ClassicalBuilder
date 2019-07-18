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

public class CB_RelationMenu extends JPopupMenu implements ActionListener {

	private final CB_Instance instance;
	private final int type;
	private int relation;
	private int[] relations;

	private JMenuItem  mCopy, mCut, mDelete, mSelectAll, mDeselect, mEditParticles;
	private JMenu addParticle, removeParticle, selectRelation;
	private JMenuItem[] relationParticleList, relationSelectionList;
	private JMenuItem mRelationAll;

	private static final int ACTION_ADD_PART = 0;
	private static final int ACTION_REMOVE_PART = 1;
	private static final int ACTION_SELECT_PART = 2;

	public CB_RelationMenu(CB_Instance instance, int type, int relation) {
		this.instance = instance;
		this.type = type;
		this.relation = relation;
		// First the easy actions
		mEditParticles = CB_Tools.createMenuItem(this, "Edit Particles", "editrelation.png", this);
		mCut = CB_Tools.createMenuItem(this, "Cut", "cut.png", this);
		mCopy = CB_Tools.createMenuItem(this, "Copy", "copy.png", this);
		mDelete = CB_Tools.createMenuItem(this, "Delete", "delete.png", this);
		this.addSeparator();
		// The select particle actions
		selectRelation = CB_Tools.createSubMenu(this, "Select Relation Particle", "particle.png");
		mRelationAll = CB_Tools.createSubItem(selectRelation, "Select All", this, CB_Tools.getIcon("select.png"));
		selectRelation.addSeparator();
		this.addSeparator();
		// Now the add to and remove from relations
		addParticle = CB_Tools.createSubMenu(this, "Add Particle", "newparticle.png");
		removeParticle = CB_Tools.createSubMenu(this, "Remove Particle", "delparticle.png");

		relationParticleList = new JMenuItem[instance.getParticles().size()];
		relationSelectionList = new JMenuItem[instance.getParticles().size()];

		for (int i = 0; i < instance.getParticles().size(); i++) {
			if (this.getRelations().get(relation).getParticleIndex(i) == -1) {
				relationParticleList[i] = CB_Tools.createSubItem(
					addParticle,
					instance.getParticle(i).getName(),
					new RelationAction(i, ACTION_ADD_PART),
					instance.getColors().getColorIcon(instance.getParticle(i).getColor())
				);
			} else {
				relationParticleList[i] = CB_Tools.createSubItem(
					removeParticle,
					instance.getParticle(i).getName(),
					new RelationAction(i, ACTION_REMOVE_PART),
					instance.getColors().getColorIcon(instance.getParticle(i).getColor())
				);
				relationSelectionList[i] = CB_Tools.createSubItem(
					selectRelation,
					instance.getParticle(i).getName(),
					new RelationAction(i, ACTION_SELECT_PART),
					instance.getColors().getColorIcon(instance.getParticle(i).getColor())
				);
			}
		}
	}
	public CB_Relations getRelations() {
		if (type == CB_Instance.INTERACTIONS) {
			return instance.getInteractions();
		} else if (type == CB_Instance.CONSTRAINTS) {
			return instance.getConstraints();
		}
		return null;
	}
	public CB_RelationMenu(CB_Instance instance, int type, int[] relations) {
		this.instance = instance;
		this.type = type;
		this.relations = relations;
		mCut = CB_Tools.createMenuItem(this, "Cut", "cut.png", this);
		mCopy = CB_Tools.createMenuItem(this, "Copy", "copy.png", this);
		mDelete = CB_Tools.createMenuItem(this, "Delete", "delete.png", this);
	}
	public CB_RelationMenu(CB_Instance instance, int type) {
		this.instance = instance;
		this.type = type;
		mSelectAll = CB_Tools.createMenuItem(this, "Select All", "select.png", this);
		mDeselect = CB_Tools.createMenuItem(this, "Deselect", "deselect.png", this);
	}
	public void actionPerformed(ActionEvent e) {
		// Get the source menu item
		Object event = e.getSource();
		// Do the action for the menu item
		if (event == mEditParticles) {
			instance.showRelationParticlesDialog();
		} else if (event == mCut) {
			instance.getMain().cut();
		} else if (event == mCopy) {
			instance.getMain().copy();
		} else if (event == mDelete) {
			instance.removeSelectedItems();
		} else if (event == mSelectAll) {
			instance.selectAll(type);
		} else if (event == mDeselect) {
			instance.deselect();
		} else if (event == mRelationAll) {
			instance.select(CB_Instance.PARTICLES, this.getRelations().get(relation).cloneParticles());
		}
	}
	public class RelationAction implements ActionListener {
		private int particle;
		private int action;

		public RelationAction(int particle, int action) {
			this.particle = particle;
			this.action = action;
		}
		public void actionPerformed(ActionEvent e) {
			switch (action) {
				case ACTION_ADD_PART:
					instance.addRelationParticle(type, relation, particle);
				break;
				case ACTION_REMOVE_PART:
					instance.removeRelationParticle(type, relation, particle);
				break;
				case ACTION_SELECT_PART:
					instance.select(CB_Instance.PARTICLES, new int[] {particle});
				break;
			}
		}
	}
}