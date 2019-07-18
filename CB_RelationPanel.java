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
import javax.swing.tree.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

public class CB_RelationPanel extends JPanel implements TreeSelectionListener, TreeExpansionListener, TreeWillExpandListener, MouseListener {
	private final CB_Main main;
	private final int type;
	private final JTree relationTree;
	private final RelationTreeModel treeModel;
	private static final ImageIcon interactionIcon = CB_Tools.getIcon("interaction.png");
	private static final ImageIcon constraintIcon = CB_Tools.getIcon("constraint.png");
	private boolean systemChange = false;

    public CB_RelationPanel(CB_Main main, int type) {
    	this.main = main;
    	this.type = type;
		this.setPreferredSize(new Dimension(150, 550));
		this.setLayout(new GridLayout(1, 1));

		treeModel = new RelationTreeModel();
		relationTree = new JTree(treeModel) {
			public String getToolTipText(MouseEvent e) {
				TreePath treePath = getPathForLocation(e.getX(), e.getY());
				if (treePath != null && treePath.getPathCount() == 2) {
					return ((CB_Relation) treePath.getPathComponent(1)).getTooltip(getInstance());
				} else if (treePath != null && treePath.getPathCount() == 3) {
					return ((CB_Particle) treePath.getPathComponent(2)).getTooltip(getInstance());
				}
				return null;
			}
		};
		relationTree.addTreeSelectionListener(this);
		relationTree.addTreeExpansionListener(this);
		relationTree.addTreeWillExpandListener(this);
		relationTree.addMouseListener(this);
		relationTree.setEditable(false);
		relationTree.setExpandsSelectedPaths(true);
		relationTree.setToolTipText("");
		relationTree.setCellRenderer(new RelationTreeRenderer());

		Dimension size = relationTree.getPreferredScrollableViewportSize();
		size.width = Short.MAX_VALUE;
		JScrollPane listScroller = new JScrollPane(relationTree);
		listScroller.setMaximumSize(size);
		this.add(listScroller);
	}
	public CB_Instance getInstance() {
		return main.getInstance();
	}
	public void update() {
		treeModel.fireTreeStructureChanged();
	}
	public void updateSelection() {
		systemChange = true;
		relationTree.clearSelection();
		if (main.getInstance().getSelectionType() == CB_Instance.PARTICLES && main.getInstance().getSelectionSize() > 0) {
			for (int j = 0; j < main.getRelations(type).size(); j++) {
				if (relationTree.isExpanded(new TreePath(new Object[] {treeModel.getRoot(), main.getRelation(type, j)}))) {
					this.selectParticlesInRelation(j);
				}
			}
		} else if (main.getInstance().getSelectionType() == type && main.getInstance().getSelectionSize() > 0) {
			for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
				relationTree.addSelectionPath(
					new TreePath(
						new Object[] {
							treeModel.getRoot(),
							main.getRelation(type, main.getInstance().getSelection(i))
						}
					)
				);
			}
		}
		systemChange = false;
	}
	public void selectParticlesInRelation(int index) {
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			int arrayPos = CB_Tools.arraySearch(main.getRelation(type, index).getParticles(), main.getInstance().getSelection(i));
			if (arrayPos >= 0) {
				relationTree.addSelectionPath(
					new TreePath(
						new Object[] {
							treeModel.getRoot(),
							main.getRelation(type, index),
							main.getParticle(main.getRelation(type, index).getParticle(arrayPos))
						}
					)
				);
			}
		}
	}
	public void valueChanged(TreeSelectionEvent e) {
		if (systemChange) {
			return;
		}
		if (relationTree.isRowSelected(0)) {
			relationTree.removeSelectionRow(0);
		}
		TreePath[] treePaths = relationTree.getSelectionPaths();
		// Check if there are both relations and particles selected
		boolean particle = false, relation = false;
		if (treePaths != null) {
			for (int i = 0; i < treePaths.length; i++) {
				if (treePaths[i].getPathCount() == 2) {
					relation = true;
				} else if (treePaths[i].getPathCount() == 3) {
					particle = true;
				}
			}
		}
		if (relation && !particle) {
			int[] newSelection = new int[treePaths.length];
			int count = 0;
			for (int i = 0; i < treePaths.length; i++) {
				if (treePaths[i].getPathCount() == 2) {
					newSelection[count] = main.getRelations(type).indexOf((CB_Relation) treePaths[i].getPathComponent(1));
					count++;
				}
			}
			main.getInstance().select(type, newSelection);
		} else if (particle && !relation) {
			// Filter out duplicate particleIds by creating a HashSet
			HashSet<Integer> particleSet = new HashSet<Integer>(treePaths.length);
			for (int i = 0; i < treePaths.length; i++) {
				int index = main.getRelations(type).indexOf((CB_Relation) treePaths[i].getPathComponent(1));
				for (int u = 0; u < main.getRelation(type, index).getParticlesSize(); u++) {
					if (treePaths[i].getPathComponent(2).equals(main.getParticle(main.getRelation(type, index).getParticle(u)))) {
						particleSet.add(main.getRelation(type, index).getParticle(u));
					}
				}
			}
			Integer[] integers = particleSet.toArray(new Integer[particleSet.size()]);
			int[] newSelection = new int[integers.length];
			for (int i = 0; i < integers.length; i++) {
				newSelection[i] = integers[i].intValue();
			}
			if (!main.getInstance().select(CB_Instance.PARTICLES, newSelection)) {
				this.updateSelection();
			}
		} else {
			this.updateSelection();
		}
	}
	public void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			TreePath treePath = relationTree.getPathForLocation(e.getX(), e.getY());
			if (treePath != null && treePath.getPathCount() == 2) {
				// Rightclick on an relation
				int index = main.getRelations(type).indexOf((CB_Relation)treePath.getPathComponent(1));
				if (main.getInstance().getSelectionType() != type || !relationTree.isPathSelected(treePath)) {
					main.getInstance().select(type, new int[] {index});
				}
				if (main.getInstance().getSelectionSize() == 1) {
					CB_RelationMenu relationMenu = new CB_RelationMenu(main.getInstance(), type, index);
					relationMenu.show(e.getComponent(), e.getX(), e.getY());
				} else {
					CB_RelationMenu relationMenu = new CB_RelationMenu(main.getInstance(), type, main.getInstance().getSelection());
					relationMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			} else if (treePath != null && treePath.getPathCount() == 3) {
				// Rightclick on a particle
				int index = main.getParticles().indexOf((CB_Particle)treePath.getPathComponent(2));
				if (main.getInstance().getSelectionType() != CB_Instance.PARTICLES || !relationTree.isPathSelected(treePath) || !main.getInstance().getParticleSelected(index)) {
					main.getInstance().select(CB_Instance.PARTICLES, new int[] {index});
				}
				if (main.getInstance().getSelectionSize() == 1) {
					CB_ParticleMenu particleMenu = new CB_ParticleMenu(main.getInstance(), index);
					particleMenu.show(e.getComponent(), e.getX(), e.getY());
				} else {
					CB_ParticleMenu particleMenu = new CB_ParticleMenu(main.getInstance(), main.getInstance().getSelection());
					particleMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			} else {
				// Rightclick on nothing
				CB_RelationMenu relationMenu = new CB_RelationMenu(main.getInstance(), type);
				relationMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	public void mousePressed(MouseEvent e) {
		this.maybeShowPopup(e);
	}
	public void mouseReleased(MouseEvent e) {
		this.maybeShowPopup(e);
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void treeExpanded(TreeExpansionEvent e) {
		systemChange = true;
		if (main.getInstance().getSelectionType() == CB_Instance.PARTICLES && main.getInstance().getSelectionSize() > 0) {
			TreePath path = e.getPath();
			if (path.getPathCount() == 2 && path.getPathComponent(0).equals(treeModel.getRoot())) {
				this.selectParticlesInRelation(treeModel.getIndexOfChild(treeModel.getRoot(), path.getPathComponent(1)));
			}
		}
		systemChange = false;
	}
	public void treeCollapsed(TreeExpansionEvent e) {}
	public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
		// Disallow tree expansions except for relations:
		if (e.getPath().getPathCount() != 2 || !e.getPath().getPathComponent(0).equals(treeModel.getRoot())) {
			throw new ExpandVetoException(e);
		}
	}
	public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
		// Disallow tree collapses except for relations:
		if (e.getPath().getPathCount() != 2 || !e.getPath().getPathComponent(0).equals(treeModel.getRoot())) {
			throw new ExpandVetoException(e);
		}
	}
	private class RelationTreeModel implements TreeModel {
		private final Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();
		private final Object root = CB_Relations.getTypeString(type);

		public Object getChild(Object parent, int index) {
			if (parent.equals(root)) {
				return main.getRelation(type, index);
			}
			return main.getParticle(((CB_Relation) parent).getParticle(index));
		}
		public int getChildCount(Object parent) {
			if (parent.equals(root)) {
				return main.getRelations(type).size();
			}
			return ((CB_Relation) parent).getParticlesSize();
		}
		public int getIndexOfChild(Object parent, Object child) {
			if (parent.equals(root) && child != null) {
				return main.getRelations(type).indexOf(child);
			} else if (parent != null && child != null) {
				CB_Relation relation = (CB_Relation) parent;
				for (int i = 0; i < relation.getParticlesSize(); i++) {
					if (main.getParticle(relation.getParticle(i)).equals(child)) {
						return i;
					}
				}
			}
			return -1;
		}
		public Object getRoot() {
			return root;
		}
		public boolean isLeaf(Object node) {
			if (node.equals(root) || (treeModel.getIndexOfChild(root, node) >= 0 && ((CB_Relation)node).getParticlesSize() > 0)) {
				return false;
			}
			return true;
		}
		public void addTreeModelListener(TreeModelListener l) {
			treeModelListeners.addElement(l);
		}
		public void removeTreeModelListener(TreeModelListener l) {
			treeModelListeners.removeElement(l);
		}
		protected void fireTreeStructureChanged() {
			TreeModelEvent e = new TreeModelEvent(this, new Object[] {root});
			for (TreeModelListener tml : treeModelListeners) {
				tml.treeStructureChanged(e);
			}
		}
		public void valueForPathChanged(TreePath path, Object newValue) {}
	}
	private class RelationTreeRenderer extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(JTree tree, Object object, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, object, sel, expanded, leaf, row, hasFocus);
			if (treeModel.getIndexOfChild(treeModel.getRoot(), object) >= 0) {
				if (type == CB_Instance.INTERACTIONS) {
					setIcon(interactionIcon);
				} else if (type == CB_Instance.CONSTRAINTS) {
					setIcon(constraintIcon);
				}
			} else if (!treeModel.getRoot().equals(object)) {
				setIcon(getInstance().getColors().getColorIcon(((CB_Particle) object).getColor()));
			}
			return this;
		}
	}
}