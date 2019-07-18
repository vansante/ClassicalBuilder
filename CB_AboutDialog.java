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
import java.io.*;
import javax.swing.*;

// Creates a new dialog containing the about information

public class CB_AboutDialog extends JDialog implements ActionListener {
	private final JButton closeButton, linkButton;

	public CB_AboutDialog(CB_Main main) {
		super(main, "About", true);
		this.setSize(408, 365);
		this.setResizable(false);
		this.setLocationRelativeTo(main);
		this.setLayout(new BorderLayout());

		//Buttons
		closeButton = new JButton("Close");
		closeButton.setPreferredSize(new Dimension(50, 25));
		closeButton.addActionListener(this);
		closeButton.setMnemonic(KeyEvent.VK_C);
		linkButton = new JButton(CB_Main.GNU_LICENSE_WEBSITE);
		linkButton.addActionListener(this);
		linkButton.setBorderPainted(false);

		JLabel labelVersion = new JLabel(CB_Main.TITLE, JLabel.CENTER);
		labelVersion.setFont(new Font("Verdana", Font.BOLD, 12));
		JLabel versionLabel =  new JLabel("Version " + CB_Main.VERSION + " Revision " + CB_Main.REVISION);
		JLabel creatorsLabel = new JLabel("Created by: ");
		JLabel creatorNames = new JLabel("Erik Kerkvliet & Paul van Santen");
		creatorsLabel.setFont(new Font("Verdana", Font.BOLD, 11));
		JLabel licenceLabel = new JLabel("This program is licensed according to the GPL");
		licenceLabel.setFont(new Font("Verdana", Font.BOLD, 11));

		JPanel linkPanel = new JPanel();
		linkPanel.setLayout(new FlowLayout());
		linkPanel.add(linkButton);

		JLabel northPanel = new JLabel(CB_Tools.getIcon("aboutlogo.jpg"));
		northPanel.setBorder(BorderFactory.createLoweredBevelBorder());

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setBorder(BorderFactory.createTitledBorder(""));
		centerPanel.add(this.createPanel(labelVersion));
		centerPanel.add(this.createPanel(versionLabel));
		centerPanel.add(this.createPanel(creatorsLabel));
		centerPanel.add(this.createPanel(creatorNames));
		centerPanel.add(this.createPanel(licenceLabel));
		centerPanel.add(linkPanel);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout());
		southPanel.add(closeButton);

		this.add(northPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				closeButton.requestFocusInWindow();
			}
		});
	}
	public JPanel createPanel(JLabel label) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(label);
		return panel;
	}
	// Action listener
	public void actionPerformed(ActionEvent e) {
		Object event = e.getSource();
		if (event == closeButton) {
			// Close window
			this.setVisible(false);
		} else if (event == linkButton) {
			// Open browser with GPL information
			CB_Tools.openURL(CB_Main.GNU_LICENSE_WEBSITE);
		}
	}
}