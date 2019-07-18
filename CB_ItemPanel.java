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

//this class creates a JPanel containing the tabs: particles, interactions and constraints
public class CB_ItemPanel extends JPanel {
	private final JTabbedPane itemPanel;

	public CB_ItemPanel(CB_ParticlePanel particlePanel, CB_RelationPanel interactionPanel, CB_RelationPanel constraintPanel) {
		this.setPreferredSize(new Dimension(202, 0));
		this.setLayout(new GridLayout(1,1));

		//creates a JTabbedPane and adds the tabs
		itemPanel = new JTabbedPane();
		itemPanel.addTab("Particles", particlePanel);
		itemPanel.addTab("Interactions", interactionPanel);
		itemPanel.addTab("Constraints", constraintPanel);

		this.add(itemPanel);
	}
	//sets the selected tab
	public void setIndex(int index) {
		itemPanel.setSelectedIndex(index);
	}
	//gets the selected tab
	public int getIndex() {
		return itemPanel.getSelectedIndex();
	}
	public static JMenuItem createMenuItem(String title, JPopupMenu menu, ActionListener listener) {
		JMenuItem menuItem = new JMenuItem(title);
		menuItem.addActionListener(listener);
		menu.add(menuItem);
		return menuItem;
	}
}