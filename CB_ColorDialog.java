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

//this class creates a dialog in which you can edit or add colors.
public class CB_ColorDialog extends JDialog implements ActionListener, ListSelectionListener {

	private CB_Instance instance;
	private CB_Colors colors;
	private JTextField editNameF, newNameF;
	private JList colorList;
	private DefaultListModel colorListModel;
	private Color editColor, newColor;
	private JPanel editColorPreviewP, newColorPreviewP;
	private JButton editChooseColorB, editDeleteB, newColorB, newChooseColorB, okButton, cancelButton;

	public CB_ColorDialog(CB_Instance instance, CB_Colors colors) {
		super(instance.getMain(), "Colors", true);
        this.colors = colors;
		this.instance = instance;
		this.setSize(350, 400);
		this.setResizable(false);
		this.setLocationRelativeTo(instance.getMain());
		this.getContentPane().setLayout(new BorderLayout(5, 5));

		colorListModel = new DefaultListModel();
		colorList = new JList(colorListModel) {
			public String getToolTipText(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				String item = null;
				if (index > -1) {
					item = "" + getModel().getElementAt(index);
				}
				return item;
			}
		};
		colorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		colorList.setLayoutOrientation(JList.VERTICAL);
		colorList.addListSelectionListener(this);

		JScrollPane listScroller = new JScrollPane(colorList);
		listScroller.setBorder(BorderFactory.createTitledBorder("Colors"));
		listScroller.setPreferredSize(new Dimension(150,0));

		editColorPreviewP = new JPanel();
		editColorPreviewP.setPreferredSize(new Dimension(30, 30));
		editColorPreviewP.setOpaque(true);

		newColorPreviewP = new JPanel();
		newColorPreviewP.setOpaque(true);

		editNameF = new JTextField("");
		editNameF.addActionListener(this);

		newNameF = new JTextField("");
		newNameF.addActionListener(this);

		editChooseColorB = new JButton("Edit Color");
		editChooseColorB.setPreferredSize(new Dimension(100,25));
		editChooseColorB.addActionListener(this);

		editDeleteB = new JButton("Delete Color");
		editDeleteB.setPreferredSize(new Dimension(100,25));
		editDeleteB.addActionListener(this);

		newColorB = new JButton("Add Color To List");
		newColorB.setPreferredSize(new Dimension(100,25));
		newColorB.addActionListener(this);

		newChooseColorB = new JButton("Select Color");
		newChooseColorB.setPreferredSize(new Dimension(100,25));
		newChooseColorB.addActionListener(this );

		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(50,25));
		cancelButton.addActionListener(this);
		cancelButton.setMnemonic(KeyEvent.VK_C);

		okButton = new JButton("Ok");
		okButton.setPreferredSize(new Dimension(50,25));
		okButton.addActionListener(this);
		okButton.setMnemonic(KeyEvent.VK_O);

		// The new color Panel

		JPanel newPanel = new JPanel();
		newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.PAGE_AXIS));
		newPanel.setBorder(BorderFactory.createTitledBorder("Add New Color"));

		JPanel newNamePanel = new JPanel(new GridLayout(1, 2, 2, 2));
		newNamePanel.setMaximumSize(new Dimension(240, 20));
		newNamePanel.add(new JLabel("Color name:"));
		newNamePanel.add(newNameF);

		JPanel newColorBoxPanel = new JPanel(new GridLayout(1, 1, 1, 1));
		newColorBoxPanel.setMaximumSize(new Dimension(60, 60));
		newColorBoxPanel.setBorder(BorderFactory.createTitledBorder("Color"));
		newColorBoxPanel.add(newColorPreviewP);

		JPanel newCenterPanel = new JPanel(new BorderLayout(2, 2));
		newCenterPanel.add(newChooseColorB, BorderLayout.NORTH);
		newCenterPanel.add(newColorBoxPanel, BorderLayout.CENTER);
		newCenterPanel.add(newColorB, BorderLayout.SOUTH);

		newPanel.add(newNamePanel);
		newPanel.add(Box.createRigidArea(new Dimension(5, 5)));
		newPanel.add(newCenterPanel);

		// The edit color Panel

		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.PAGE_AXIS));
		editPanel.setBorder(BorderFactory.createTitledBorder("Edit Color"));

		JPanel editNamePanel = new JPanel(new GridLayout(1, 2, 2, 2));
		editNamePanel.setMaximumSize(new Dimension(240, 20));
		editNamePanel.add(new JLabel("Color name:"));
		editNamePanel.add(editNameF);

		JPanel editColorBoxPanel = new JPanel(new GridLayout(1, 1, 1, 1));
		editColorBoxPanel.setMaximumSize(new Dimension(60, 60));
		editColorBoxPanel.setBorder(BorderFactory.createTitledBorder("Color"));
		editColorBoxPanel.add(editColorPreviewP);

		JPanel editCenterPanel = new JPanel(new BorderLayout(2, 2));
		editCenterPanel.add(editChooseColorB, BorderLayout.NORTH);
		editCenterPanel.add(editColorBoxPanel, BorderLayout.CENTER);
		editCenterPanel.add(editDeleteB, BorderLayout.SOUTH);

		editPanel.add(editNamePanel);
		editPanel.add(Box.createRigidArea(new Dimension(5, 5)));
		editPanel.add(editCenterPanel);


		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(new GridLayout(2,1));
		panelCenter.add(newPanel);
		panelCenter.add(editPanel);

		JPanel panelSouth = new JPanel();
		panelSouth.add(okButton);
		panelSouth.add(cancelButton);

		this.add(listScroller, BorderLayout.WEST);
		this.add(panelCenter, BorderLayout.CENTER);
		this.add(panelSouth, BorderLayout.SOUTH);
		this.updateColors();
		colorList.setSelectedIndex(0);

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				okButton.requestFocusInWindow();
			}
		});
	}
	//return the color selected in the colorlist
	public Color getSelectedColor(){
		return colors.get(colorList.getSelectedIndex()).getColor();
	}
	//update the colors in the colorlist
	public void updateColors() {
		colorListModel.clear();
		for (int i = 0; i < colors.size(); i++) {
			colorListModel.addElement(colors.get(i).getName());
		}
	}
	public void actionPerformed(ActionEvent e){
		Object event = e.getSource();
		if (event == newChooseColorB) {
			newColor = JColorChooser.showDialog(this, "Choose Color", newColor);
        	newColorPreviewP.setBackground(newColor);
		} else if (event == newColorB){
			//check if a new color can be added
			if (newColor == null) {
				JOptionPane.showMessageDialog(this, "Please select a new color first", "Warning", JOptionPane.WARNING_MESSAGE);
			} else if (newNameF.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Please supply a name for the new color", "Warning", JOptionPane.WARNING_MESSAGE);
			}else if (newNameF.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Please supply a name for the new color", "Warning", JOptionPane.WARNING_MESSAGE);
			} else if (newNameF.getText().length() > 50) {
				JOptionPane.showMessageDialog(this, "The name you've created \ncontains too many symbols", "Warning", JOptionPane.WARNING_MESSAGE);
			} else {
				//add a new color
				colors.add(new CB_Color(newNameF.getText(), newColor));
				this.updateColors();
				newNameF.setText("");
				colorList.setSelectedIndex(colors.size() - 1);
			}
		} else if (event == editDeleteB) {
			//delete a selected color if there are more than two colors in the list.
			if (colors.size() > 1) {
				if (colorList.getSelectedIndices().length == 1) {
					int colorId = colorList.getSelectedIndex();
					colors.remove(colorId);
					this.updateColors();
					colorList.setSelectedIndex(colorId - 1);
				}
			} else {
				JOptionPane.showMessageDialog(this, "You can't delete all colors. \nYou must have at least one color remaining", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		} else if (event == editNameF) {
			int colorId = colorList.getSelectedIndex();
			colors.get(colorId).setName(editNameF.getText());
			this.updateColors();
			colorList.setSelectedIndex(colorId);
		} else if(event == editChooseColorB) {
			editColor = JColorChooser.showDialog(this, "Choose New Color", this.getSelectedColor());
			editColorPreviewP.setBackground(editColor);
			if (editColor == null) {
				JOptionPane.showMessageDialog(this, "Please select a new color", "Warning", JOptionPane.WARNING_MESSAGE);
				editColorPreviewP.setBackground(this.getSelectedColor());
			} else if (editNameF.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Please supply a name for the color", "Warning", JOptionPane.WARNING_MESSAGE);
			} else if ( editNameF.getText().length() > 50 ) {
				JOptionPane.showMessageDialog(this, "The name you have supplied has too many symbols", "Warning", JOptionPane.WARNING_MESSAGE);
			} else {
				//edit a color.
				int colorId = colorList.getSelectedIndex();
				colors.get(colorId).setName(editNameF.getText());
				colors.get(colorId).setColor(editColor);
				this.updateColors();
				editNameF.setText("");
				colorList.setSelectedIndex(colorId);
			}
		} else if (event == cancelButton) {
			this.setVisible(false);
		} else if (event == okButton) {
			instance.setNewColors(colors);
			this.setVisible(false);
		}
	}
	//set a new color or name for the edit color.
	public void valueChanged(ListSelectionEvent e) {
		if (colorListModel.getSize() > 0 && colorList.getSelectedIndex() >= 0) {
			editColorPreviewP.setBackground(this.getSelectedColor());
			editNameF.setText(colors.get(colorList.getSelectedIndex()).getName());
		}
	}
}