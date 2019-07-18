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
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;

public class CB_HelpDialog extends JFrame implements ActionListener, HyperlinkListener {

	private JEditorPane helpPane;
	private final JScrollPane scroll;
	private final JButton closeButton;

	// Constructs a new JFrame with a JEditorPane to display the html help pages in.
	//
	public CB_HelpDialog(CB_Main main) {
		super(CB_Main.TITLE + " - Help");
		this.setSize(750, 550);
		this.setResizable(true);
		this.setLocationRelativeTo(main);

		// Set an icon
		this.setIconImage(CB_Tools.getImage("help.png"));
		this.getContentPane().setLayout(new BorderLayout(2, 2));

		// Load the html page
		try {
			java.net.URL url = this.getClass().getClassLoader().getResource("ClassicalBuilder/help/index.html");
			helpPane = new JEditorPane(url);
		} catch (IOException ex) {
			helpPane = new JEditorPane();
			JOptionPane.showMessageDialog(this, "There was an error loading the help file.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		helpPane.setContentType("text/html");
		helpPane.setEditable(false);
		helpPane.addHyperlinkListener(this);
		scroll = new JScrollPane(helpPane);
		this.getContentPane().add(scroll, BorderLayout.CENTER);
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		closeButton.setPreferredSize(new Dimension(50,25));
		closeButton.setMnemonic(KeyEvent.VK_C);
		JPanel okPanel = new JPanel();
		okPanel.add(closeButton);
		this.getContentPane().add(okPanel, BorderLayout.SOUTH);

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				closeButton.requestFocusInWindow();
			}
		});
	}
	// Action handler that closes the window if the closebutton is pressed
	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}
	// Hyperlink action handler that updates the JEditorPane with the new page
	// if a hyperlink is pressed
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				helpPane.setPage(e.getURL());
			} catch (Exception error) {}
		}
	}
}