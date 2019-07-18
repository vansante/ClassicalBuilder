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
import java.util.Vector;

//this class creates a JPanel which is used in the ItemPanel to show a list of particles
public class CB_ParticlePanel extends JPanel implements TreeSelectionListener, TreeWillExpandListener, MouseListener {

	private final CB_Main main;
	private final JTree particleTree;
	private final ParticleTreeModel treeModel;
	private boolean systemChange = false;

	public CB_ParticlePanel(CB_Main main) {
		this.main = main;
		this.setPreferredSize(new Dimension(150, 550));
		this.setLayout(new GridLayout(1, 1));

		treeModel = new ParticleTreeModel();

		particleTree = new JTree(treeModel) {
			public String getToolTipText(MouseEvent e) {
				TreePath treePath = getPathForLocation(e.getX(), e.getY());
				if (treePath != null && treePath.getPathCount() == 2) {
					return ((CB_Particle) treePath.getPathComponent(1)).getTooltip(getInstance());
				}
				return null;
			}
		};
		particleTree.addTreeSelectionListener(this);
		particleTree.addTreeWillExpandListener(this);
		particleTree.addMouseListener(this);
		particleTree.setEditable(false);
		particleTree.setToolTipText("");
		particleTree.setCellRenderer(new ParticleTreeRenderer());

		Dimension size = particleTree.getPreferredScrollableViewportSize();
		size.width = Short.MAX_VALUE;
		JScrollPane listScroller = new JScrollPane(particleTree);
		listScroller.setMaximumSize(size);
		this.add(listScroller);
	}
	public CB_Instance getInstance() {
		return main.getInstance();
	}
	//update the particles in the list
	public void update() {
		treeModel.fireTreeStructureChanged();
	}
	//update the selected particles in the list
	public void updateSelection() {
		systemChange = true;
		if (main.getInstance().getSelectionType() == CB_Instance.PARTICLES) {
			int[] selection = new int[main.getInstance().getSelectionSize()];
			for (int i = 0; i < selection.length; i++) {
				selection[i] = main.getInstance().getSelection(i) + 1;
			}
			particleTree.setSelectionRows(selection);
		} else {
			particleTree.clearSelection();
		}
		systemChange = false;
	}
	public void valueChanged(TreeSelectionEvent e) {
		if (systemChange) {
			return;
		}
		if (particleTree.isRowSelected(0)) {
			particleTree.removeSelectionRow(0);
		}
		int[] selection = particleTree.getSelectionRows();
		if (selection != null && selection.length > 0) {
			for (int i = 0; i < selection.length; i++) {
				selection[i] -= 1;
			}
			main.getInstance().select(CB_Instance.PARTICLES, selection);
		} else {
			this.updateSelection();
		}
	}
	public void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			TreePath treePath = particleTree.getPathForLocation(e.getX(), e.getY());
			if (treePath != null && treePath.getPathCount() == 2) {
				int index = main.getParticles().indexOf((CB_Particle)treePath.getPathComponent(1));
				if (!main.getInstance().getParticleSelected(index)) {
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
				CB_ParticleMenu particleMenu = new CB_ParticleMenu(main.getInstance());
				particleMenu.show(e.getComponent(), e.getX(), e.getY());
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
	public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
		// Disallow tree expansions:
		throw new ExpandVetoException(e);
	}
	public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
		// Disallow tree collapses:
		throw new ExpandVetoException(e);
	}
	private class ParticleTreeModel implements TreeModel {
		private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();
		private Object root = "Particles";

		public ParticleTreeModel() {
		}
		public Object getChild(Object parent, int index) {
			if (parent.equals(root)) {
				return main.getParticle(index);
			}
			return null;
		}
		public int getChildCount(Object parent) {
			if (parent.equals(root)) {
				return main.getParticles().size();
			}
			return 0;
		}
		public int getIndexOfChild(Object parent, Object child) {
			if (parent.equals(root) && child != null) {
				for (int i = 0; i < main.getParticles().size(); i++) {
					if (main.getParticle(i).equals(child)) {
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
			if (!node.equals(root)) {
				return true;
			}
			return false;
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
	private class ParticleTreeRenderer extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(JTree tree, Object object, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, object, sel, expanded, leaf, row, hasFocus);
			if (leaf) {
				setIcon(getInstance().getColors().getColorIcon(((CB_Particle) object).getColor()));
			}
			return this;
		}
	}
}