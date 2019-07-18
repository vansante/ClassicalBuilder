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
import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;

//this class creates a JPanel which containts the information about the particles and relations
public class CB_PropertiesPanel extends JPanel {

	private final CardLayout layout;

	public final static String[] PANELS = { "ParticlePropertiesPanel", "InteractionPropertiesPanel", "ConstraintPropertiesPanel" };

	public CB_PropertiesPanel(CB_ParticlePropertiesPanel particlePropertiesPanel,
			CB_InteractionPropertiesPanel interactionPropertiesPanel, CB_ConstraintPropertiesPanel constraintPropertiesPanel) {

		this.layout = new CardLayout();
		this.setLayout(layout);
		this.setPreferredSize(new Dimension(150, 0));
		this.setBorder(BorderFactory.createRaisedBevelBorder());

		this.add(particlePropertiesPanel, PANELS[CB_Instance.PARTICLES]);
		this.add(interactionPropertiesPanel, PANELS[CB_Instance.INTERACTIONS]);
		this.add(constraintPropertiesPanel, PANELS[CB_Instance.CONSTRAINTS]);
	}
	//set which properties must be viewed
	public void setPanel(int type) {
		layout.show(this, PANELS[type]);
	}
	//create one label and textfield
	public static JPanel createPropertyPanel(String title, JTextField textField) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder(title));
		panel.setPreferredSize(new Dimension(142, 48));
		panel.add(textField);
		return panel;
	}
	//create one label and textfield which are optional, so containing a checkbox
	public static JPanel createOptionalPropertyPanel(String title, JCheckBox checkbox, JTextField textField) {
		JPanel panel = new JPanel(new BorderLayout(2, 2));
		panel.setBorder(BorderFactory.createTitledBorder(title));
		panel.setPreferredSize(new Dimension(142, 52));
		panel.add(checkbox, BorderLayout.WEST);
		panel.add(textField, BorderLayout.CENTER);
		return panel;
	}
	//create one label and a combobox
	public static JPanel createChooserPanel(String title, JComboBox chooser) {
		JPanel panel = new JPanel();
		panel.add(chooser);
		panel.setBorder(BorderFactory.createTitledBorder(title));
		panel.setPreferredSize(new Dimension(142, 54));
		return panel;
	}
	//create three labels and textfields in one group
	public static JPanel createGroupPanel(String title, String label1, String label2, String label3, JTextField textField1, JTextField textField2, JTextField textField3) {
		JLabel label1l = new JLabel(label1);
		JLabel label2l = new JLabel(label2);
		JLabel label3l = new JLabel(label3);

		JPanel panel1 = new JPanel(new BorderLayout(2, 6));
		panel1.add(label1l, BorderLayout.WEST);
		panel1.add(textField1, BorderLayout.CENTER);

		JPanel panel2 = new JPanel(new BorderLayout(2, 6));
		panel2.add(label2l, BorderLayout.WEST);
		panel2.add(textField2, BorderLayout.CENTER);

		JPanel panel3 = new JPanel(new BorderLayout(2, 6));
		panel3.add(label3l, BorderLayout.WEST);
		panel3.add(textField3, BorderLayout.CENTER);

		JPanel panel = new JPanel(new GridLayout(3,1,2,2));
		panel.setBorder(BorderFactory.createTitledBorder(title));
		panel.setPreferredSize(new Dimension(142, 100));
		panel.add(panel1);
		panel.add(panel2);
		panel.add(panel3);
		return panel;
	}
	public static JFormattedTextField createDoubleField(ActionListener listener) {
		NumberFormat format = NumberFormat.getInstance();
		format.setMinimumFractionDigits(1);
		format.setMaximumFractionDigits(12);
		JFormattedTextField textField = new JFormattedTextField(new NumberFormatter(format));
		textField.addActionListener(listener);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		return textField;
	}
	public static JFormattedTextField createIntField(ActionListener listener) {
		NumberFormat format = NumberFormat.getIntegerInstance();
		JFormattedTextField textField = new JFormattedTextField(new NumberFormatter(format));
		textField.addActionListener(listener);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		return textField;
	}
}
