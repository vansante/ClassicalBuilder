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
import javax.swing.event.ListDataListener;
import java.util.Vector;
import java.util.Arrays;

public class CB_ParticleList extends JList implements MouseListener, MouseMotionListener {
	private final CB_Instance instance;
	private DefaultListModel model;
	private int[] particles;
	private final boolean edit;
	private int startRow = 0;

	public CB_ParticleList(CB_Instance instance, int[] particles) {
		super();
		this.instance = instance;
		this.particles = particles;
		this.edit = true;
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.init();
	}
	public CB_ParticleList(CB_Instance instance) {
		super();
		this.instance = instance;
		this.edit = false;
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.init();
	}
	public void init() {
		this.setCellRenderer(new ParticleListRenderer());
		this.model = new DefaultListModel();
		this.setModel(model);
		this.update();
	}
	public int[] getParticles() {
		return particles;
	}
	public void moveUp(int amount) {
		int[] selection = this.getSelectedIndices();
		if (!Arrays.equals(particles, CB_Tools.moveSelectionInArray(particles, selection, true, amount))) {
			particles = CB_Tools.moveSelectionInArray(particles, selection, true, amount);
		}
		this.update();
		this.setSelectedIndices(CB_Tools.getMoveSelectionArray(selection, true, amount));
	}
	public void moveDown(int amount) {
		int[] selection = this.getSelectedIndices();
		if (!Arrays.equals(particles, CB_Tools.moveSelectionInArray(particles, selection, false, amount))) {
			particles = CB_Tools.moveSelectionInArray(particles, selection, false, amount);
		}
		this.update();
		this.setSelectedIndices(CB_Tools.getMoveSelectionArray(selection, false, amount));
	}
	public void add(int[] newParticles) {
		int[] selection = this.getSelectedIndices();
		for (int i = 0; i < newParticles.length; i++) {
			if (CB_Tools.arraySearch(particles, newParticles[i]) < 0) {
				particles = CB_Tools.arrayAdd(particles, newParticles[i]);
			}
		}
		this.setSelectedIndices(this.getSelectedIndices());
		this.update();
	}
	public void remove() {
		int[] selection = this.getSelectedIndices();
		for (int i = 0; i < selection.length; i++) {
			if (CB_Tools.arraySearch(particles, selection[i]) >= 0) {
				particles = CB_Tools.arrayRemove(particles, selection[i] - i);
			}
		}
		this.update();
		this.clearSelection();
	}
	public String getToolTipText(MouseEvent e) {
		return ((CB_Particle) model.getElementAt(locationToIndex(e.getPoint()))).getTooltip(instance);
	}
	public void update() {
		model.removeAllElements();
		if (edit) {
			for (int i = 0; i < particles.length; i++) {
				model.addElement(instance.getParticle(particles[i]));
			}
		} else {
			for (int i = 0; i < instance.getParticles().size(); i++) {
				model.addElement(instance.getParticle(i));
			}
		}
	}
	public void mousePressed(MouseEvent e) {
		startRow = this.getSelectedIndex();
	}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {
		int endRow = this.getSelectedIndex();
		if (startRow == endRow) {
			return;
		}
		particles = CB_Tools.moveInArray(particles, startRow, endRow);
		this.update();
		startRow = endRow;
	}
	public void mouseMoved(MouseEvent e) {}
	private class ParticleListRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object object, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, object, index, isSelected, cellHasFocus);
			label.setIcon(instance.getColors().getColorIcon(((CB_Particle) object).getColor()));
			return label;
		}
	}
}