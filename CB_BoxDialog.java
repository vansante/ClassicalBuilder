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

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

//creates the dialog containing all the box properties
public class CB_BoxDialog extends JDialog implements ActionListener {
	private CB_Box box;
	private final CB_Instance instance;
	private final JLabel trajTimestepL;
	private final JTabbedPane boxPanel;
	private final JLabel confNStepsL, confErrorL, trajStartL;
	private final JCheckBox tempTauCB, trajErrorCB, trajSnapshotsCB, tempCmrfCB;
	private final JCheckBox tempCB, confCB, confMaxstepCB, trajCB, tempAdvancedCB;
	private final JLabel widthL, heightL, depthL, tempBoltzmannL, tempTemperatureL;

	private final JButton okButton, cancelButton;
	private final JSpinner trajSnapshotsF, trajNStepsF;
	private final JSpinner tempRmfF, tempCmrfF, tempGammaF;
	private final JSpinner widthF, heightF, depthF, tempBoltzmannF;
	private final JSpinner trajStartF, trajEndF, trajTimestepF, trajErrorF;
	private final JSpinner tempTemperatureF, tempTauF, confNStepsF, confErrorF, confMaxstepF;
	private final JRadioButton tempConstantRB, tempInitialRB, typeCellRB, typePeriodicRB, trajNStepsRB, trajEndRB, tempGammaRB, tempRmfRB;



	public CB_BoxDialog(CB_Instance instance, CB_Box box) {
		super(instance.getMain(), "Box Properties", true);
		this.instance = instance;
		this.box = box;
		this.setSize(310, 335);
		this.setResizable(false);
		this.setLocationRelativeTo(instance.getMain());
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		boxPanel = new JTabbedPane();
		boxPanel.setPreferredSize(new Dimension(290,425));

		//new objects
		confNStepsL = this.createLabel(" Subsequent steps", 150);
		confErrorL = this.createLabel(" Error parameter", 150);
		trajStartL = this.createLabel(" Start time", 150);
		trajTimestepL = this.createLabel(" Maximum timestep", 150);
		widthL = this.createLabel("Width", 50);
		heightL = this.createLabel("Height", 50);
		depthL = this.createLabel("Depth", 50);
		tempTemperatureL = this.createLabel(" Temperature", 100);
		tempBoltzmannL = this.createLabel(" Boltzmann constant", 100);

		// Create checkboxes
		tempTauCB = this.createCheckbox("Tau");
		trajErrorCB = this.createCheckbox("Error parameter");
		trajSnapshotsCB = this.createCheckbox("Snapshots");
		tempCB = this.createCheckbox("Enable");
		trajCB = this.createCheckbox("Enable");
		confCB = this.createCheckbox("Enable");
		confMaxstepCB = this.createCheckbox("Max steps");
		tempAdvancedCB = this.createCheckbox("Enable");
		tempCmrfCB = this.createCheckbox("CMRF");

		// Create radiobuttons
		typeCellRB = this.createRadiobutton("Cell", false);
		typePeriodicRB = this.createRadiobutton("Periodic", false);
		tempInitialRB = this.createRadiobutton("Initial", true);
		tempConstantRB = this.createRadiobutton("Constant", false);
		trajEndRB = this.createRadiobutton("End time", true);
		trajNStepsRB = this.createRadiobutton("N", false);
		tempGammaRB = this.createRadiobutton("Gamma", true);
		tempRmfRB = this.createRadiobutton("RMF", false);

		// Create textfields
		widthF = CB_Tools.createDoubleField(0, 0);
		heightF = CB_Tools.createDoubleField(0, 0);
		depthF = CB_Tools.createDoubleField(0, 0);
		tempTemperatureF = CB_Tools.createDoubleField(0, 0);
		tempTauF = CB_Tools.createDoubleField(0);
		tempRmfF = CB_Tools.createDoubleField(0);
		tempCmrfF = CB_Tools.createDoubleField(0);
		tempGammaF = CB_Tools.createDoubleField(0);
		tempBoltzmannF = CB_Tools.createDoubleField(0);
		trajStartF = CB_Tools.createDoubleField(0);
		trajEndF = CB_Tools.createDoubleField(0);
		trajTimestepF = CB_Tools.createDoubleField(0);
		trajErrorF = CB_Tools.createDoubleField(0);
		trajSnapshotsF = CB_Tools.createIntField(0, 0);
		trajNStepsF = CB_Tools.createIntField(0, 0);
		confNStepsF = CB_Tools.createIntField(0, 0);
		confErrorF = CB_Tools.createDoubleField(0);
		confMaxstepF = CB_Tools.createDoubleField(0);

		//Declaration of all the buttons
		okButton = new JButton("Ok");
		okButton.setPreferredSize(new Dimension(50,25));
		okButton.addActionListener(this);
		okButton.setMnemonic(KeyEvent.VK_O);
		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(50,25));
		cancelButton.addActionListener(this);
		cancelButton.setMnemonic(KeyEvent.VK_C);

		//buttongroups
		ButtonGroup tempBG = new ButtonGroup();
		tempBG.add(tempInitialRB);
		tempBG.add(tempConstantRB);

		ButtonGroup typeBG = new ButtonGroup();
		typeBG.add(typeCellRB);
		typeBG.add(typePeriodicRB);

		ButtonGroup tempAdvBG = new ButtonGroup();
		tempAdvBG.add(tempRmfRB);
		tempAdvBG.add(tempGammaRB);

		ButtonGroup trajTypeBG = new ButtonGroup();
		trajTypeBG.add(trajEndRB);
		trajTypeBG.add(trajNStepsRB);

		//Type panel
		JPanel typeGroup = new JPanel();
		typeGroup.setPreferredSize(new Dimension(300,80));
		typeGroup.setBorder(BorderFactory.createTitledBorder("Type"));
		typeGroup.add(typeCellRB);
		typeGroup.add(typePeriodicRB);

		//Dimension panel incliding the typeGroup
		JPanel dimensionsGroup = new JPanel();
		dimensionsGroup.setBorder(BorderFactory.createTitledBorder("Dimensions"));
		dimensionsGroup.setPreferredSize(new Dimension(300,93));
		dimensionsGroup.setLayout(new GridLayout(3, 3, 2, 2));
		dimensionsGroup.add(widthL);
		dimensionsGroup.add(widthF);
		dimensionsGroup.add(new JPanel());
		dimensionsGroup.add(heightL);
		dimensionsGroup.add(heightF);
		dimensionsGroup.add(new JPanel());
		dimensionsGroup.add(depthL);
		dimensionsGroup.add(depthF);
		dimensionsGroup.add(new JPanel());

		//Panel containing the checkbox to enable/disable temperatureGroup
		//Also containing radioButtons to select temperature type
		JPanel tempGroupCheck = new JPanel();
		tempGroupCheck.setLayout(new GridLayout(1,4,2,2));
		tempGroupCheck.add(tempCB);
		tempGroupCheck.add(new JPanel());
		tempGroupCheck.add(tempInitialRB);
		tempGroupCheck.add(tempConstantRB);


		JPanel kOptionPanel = new JPanel();
		kOptionPanel.setLayout(new GridLayout(1, 2));
		kOptionPanel.add(tempBoltzmannL);
		kOptionPanel.add(tempBoltzmannF);

		JPanel tempOptionPanel = new JPanel();
		tempOptionPanel.setLayout(new GridLayout(1, 2));
		tempOptionPanel.add(tempTemperatureL);
		tempOptionPanel.add(tempTemperatureF);

		JPanel tauOptionPanel = new JPanel();
		tauOptionPanel.setLayout(new GridLayout(1, 2));
		tauOptionPanel.add(tempTauCB);
		tauOptionPanel.add(tempTauF);

		//Panel containing the advanced temperature textfields + their labels.
		//Panel has the title "advanced".
		JPanel advancedTemp = new JPanel();
		advancedTemp.setBorder(BorderFactory.createTitledBorder("Advanced"));
		advancedTemp.setLayout(new GridLayout(4,2,2,2));
		advancedTemp.setPreferredSize(new Dimension(300,125));
		advancedTemp.add(tempAdvancedCB);
		advancedTemp.add(new JPanel());
		advancedTemp.add(tempRmfRB);
		advancedTemp.add(tempRmfF);
		advancedTemp.add(tempGammaRB);
		advancedTemp.add(tempGammaF);
		advancedTemp.add(tempCmrfCB);
		advancedTemp.add(tempCmrfF);

		JPanel normalTemp = new JPanel();
		normalTemp.setLayout(new GridLayout(3,2,2,2));
		normalTemp.add(tempBoltzmannL);
		normalTemp.add(tempBoltzmannF);
		normalTemp.add(tempTemperatureL);
		normalTemp.add(tempTemperatureF);
		normalTemp.add(tempTauCB);
		normalTemp.add(tempTauF);

		//Main temperature group with "temperature control" as title;
		JPanel temperatureGroup = new JPanel();
		temperatureGroup.setBorder(BorderFactory.createTitledBorder("Temperature control"));
		temperatureGroup.setLayout(new BorderLayout());
		temperatureGroup.setPreferredSize(new Dimension(300,245));

		temperatureGroup.add(tempGroupCheck,BorderLayout.NORTH);
		temperatureGroup.add(normalTemp,BorderLayout.CENTER);
		temperatureGroup.add(advancedTemp,BorderLayout.SOUTH);

		//Trajectory group panel parts
		//Panel containing the checkbox which enables/disables the trajectory group
		JPanel trajGroupCheckPanel = new JPanel();
		trajGroupCheckPanel.setLayout(new BorderLayout(2,2));
		trajGroupCheckPanel.add(trajCB, BorderLayout.WEST);

		//Panel containing al the textfields + their labels
		JPanel trajGroup = new JPanel();
		trajGroup.setLayout(new GridLayout(6, 2, 2, 2));
		trajGroup.add(trajStartL);
		trajGroup.add(trajStartF);
		trajGroup.add(trajEndRB);
		trajGroup.add(trajEndF);
		trajGroup.add(trajNStepsRB);
		trajGroup.add(trajNStepsF);
		trajGroup.add(trajTimestepL);
		trajGroup.add(trajTimestepF);
		trajGroup.add(trajErrorCB);
		trajGroup.add(trajErrorF);
		trajGroup.add(trajSnapshotsCB);
		trajGroup.add(trajSnapshotsF);

		//Final trajectory group
		JPanel trajectoryPanel = new JPanel();
		trajectoryPanel.setLayout(new BorderLayout());
		trajectoryPanel.setBorder(BorderFactory.createTitledBorder("Trajectory "));
		trajectoryPanel.setPreferredSize(new Dimension(300,200));
		trajectoryPanel.add(trajGroupCheckPanel,BorderLayout.NORTH);
		trajectoryPanel.add(trajGroup,BorderLayout.CENTER);

		//Conformation group panel parts
		//Panel containing the checkbox which enables/disables the conformation group
		JPanel confCB2 = new JPanel();
		confCB2.setLayout(new BorderLayout(2,2));
		confCB2.add(confCB, BorderLayout.WEST);

		//Panel containing al the textfields + their labels
		JPanel confGroup = new JPanel();
		confGroup.setLayout(new GridLayout(3, 2, 2, 2));
		confGroup.add(confNStepsL);
		confGroup.add(confNStepsF);
		confGroup.add(confErrorL);
		confGroup.add(confErrorF);
		confGroup.add(confMaxstepCB);
		confGroup.add(confMaxstepF);

		//Final confirmation Group
		JPanel conformationPanel = new JPanel();
		conformationPanel.setLayout(new BorderLayout());
		conformationPanel.setBorder(BorderFactory.createTitledBorder("Conformation searcher"));
		conformationPanel.setPreferredSize(new Dimension(300,120));
		conformationPanel.add(confCB2,BorderLayout.NORTH);
		conformationPanel.add(confGroup,BorderLayout.CENTER);

		//ok cancel button
		JPanel okGroup = new JPanel();
		okGroup.add(okButton);
		okGroup.add(cancelButton);

		//dynamicstab
		JPanel dynamicsGroup = new JPanel();
		dynamicsGroup.add(temperatureGroup);
		dynamicsGroup.add(trajectoryPanel);

		//extratab
		JPanel extraGroup = new JPanel();
		extraGroup.add(conformationPanel);

		//boxTab
		JPanel boxTab = new JPanel();
		boxTab.add(typeGroup);
		boxTab.add(dimensionsGroup);

		//TrajectoryTab
		JPanel trajectoryTab = new JPanel();
		trajectoryTab.add(trajectoryPanel);

		//add tabs to boxpanel
		boxPanel.addTab("Box", boxTab);
		boxPanel.addTab("Temperature", dynamicsGroup);
		boxPanel.addTab("Trajectory",trajectoryTab);
		boxPanel.addTab("Conformation", extraGroup);

		//add panels to dialog
		this.add(boxPanel);
		this.add(okGroup);

		// Fill in the box data
		this.updateBoxTab();
		this.updateTempTab();
		this.updateTrajTab();
		this.updateConfTab();

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				okButton.requestFocusInWindow();
			}
		});
	}
	public JLabel createLabel(String value, int width) {
		JLabel label = new JLabel(value);
		label.setPreferredSize(new Dimension(width, 20));
		return label;
	}
	public JCheckBox createCheckbox(String text) {
		JCheckBox checkbox = new JCheckBox(text);
		checkbox.addActionListener(this);
		return checkbox;
	}
	public JRadioButton createRadiobutton(String value, boolean checked) {
		JRadioButton radiobutton = new JRadioButton(value, checked);
		radiobutton.addActionListener(this);
		return radiobutton;
	}
	//update all the values
	public void updateBoxTab() {
		// Update first tab: box
		typeCellRB.setSelected(box.getType() == CB_Box.TYPE_CELL);
		typePeriodicRB.setSelected(box.getType() == CB_Box.TYPE_PERIODIC);
		widthF.setValue(box.getDimension(CB_Box.WIDTH));
		heightF.setValue(box.getDimension(CB_Box.HEIGHT));
		depthF.setValue(box.getDimension(CB_Box.DEPTH));
	}
	public void updateTempTab() {
		// Update second tab: temperature
		// First enable and disable the right stuff
		boolean enabled = box.isTempEnabled();

		tempCB.setSelected(enabled);
		tempInitialRB.setEnabled(enabled);
		tempConstantRB.setEnabled(enabled);
		tempBoltzmannL.setEnabled(enabled);
		tempBoltzmannF.setEnabled(enabled);
		tempTemperatureL.setEnabled(enabled);
		tempTemperatureF.setEnabled(enabled);
		tempTauCB.setEnabled(enabled);

		enabled = (box.isTempEnabled() && box.getTempType() == CB_Box.TEMP_TYPE_INITIAL);

		tempInitialRB.setSelected(enabled);

		enabled = (box.isTempEnabled() && box.getTempType() == CB_Box.TEMP_TYPE_CONSTANT);

		tempConstantRB.setSelected(enabled);
		tempAdvancedCB.setEnabled(enabled);
		tempTauCB.setEnabled(enabled);

		enabled = (box.isTempEnabled() && box.getTempType() == CB_Box.TEMP_TYPE_CONSTANT && box.isTempTauEnabled());

		tempTauCB.setSelected(enabled);
		tempTauF.setEnabled(enabled);

		enabled = (box.isTempEnabled() && box.getTempType() == CB_Box.TEMP_TYPE_CONSTANT && box.isTempAdvancedEnabled());

		tempAdvancedCB.setSelected(enabled);
		tempRmfRB.setEnabled(enabled);
		tempGammaRB.setEnabled(enabled);
		tempCmrfCB.setEnabled(enabled);

		enabled = (box.isTempEnabled() && box.getTempType() == CB_Box.TEMP_TYPE_CONSTANT && box.isTempAdvancedEnabled() && box.getTempAdvancedType() == CB_Box.TEMP_ADV_TYPE_RMF);

		tempRmfF.setEnabled(enabled);
		tempRmfRB.setSelected(enabled);

		enabled = (box.isTempEnabled() && box.getTempType() == CB_Box.TEMP_TYPE_CONSTANT && box.isTempAdvancedEnabled() && box.getTempAdvancedType() == CB_Box.TEMP_ADV_TYPE_GAMMA);

		tempGammaF.setEnabled(enabled);
		tempGammaRB.setSelected(enabled);

		enabled = (box.isTempEnabled() && box.getTempType() == CB_Box.TEMP_TYPE_CONSTANT && box.isTempAdvancedEnabled() && box.isTempCmrfEnabled());

		tempCmrfF.setEnabled(enabled);
		tempCmrfCB.setSelected(enabled);

		// Now update the textfields
		tempBoltzmannF.setValue(box.getTempBoltzmann());
		tempTemperatureF.setValue(box.getTempTemperature());
		tempTauF.setValue(box.getTempTau());
		tempRmfF.setValue(box.getTempRmf());
		tempGammaF.setValue(box.getTempGamma());
		tempCmrfF.setValue(box.getTempCmrf());
	}
	public void updateTrajTab() {
		// Update third tab: Trajectory
		// First enable and disable the right stuff
		boolean enabled = box.isTrajEnabled();

		trajCB.setSelected(enabled);
		trajStartL.setEnabled(enabled);
		trajStartF.setEnabled(enabled);
		trajEndRB.setEnabled(enabled);
		trajNStepsRB.setEnabled(enabled);
		trajTimestepL.setEnabled(enabled);
		trajTimestepF.setEnabled(enabled);
		trajErrorCB.setEnabled(enabled);
		trajSnapshotsCB.setEnabled(enabled);

		enabled = (box.isTrajEnabled() && box.getTrajType() == CB_Box.TRAJ_TYPE_END);

		trajEndF.setEnabled(enabled);
		trajEndRB.setSelected(enabled);

		enabled = (box.isTrajEnabled() && box.getTrajType() == CB_Box.TRAJ_TYPE_N);

		trajNStepsF.setEnabled(enabled);
		trajNStepsRB.setSelected(enabled);

		enabled = (box.isTrajEnabled() && box.isTrajErrorEnabled());

		trajErrorCB.setSelected(enabled);
		trajErrorF.setEnabled(enabled);

		enabled = (box.isTrajEnabled() && box.isTrajSnapshotsEnabled());

		trajSnapshotsCB.setSelected(enabled);
		trajSnapshotsF.setEnabled(enabled);

		// Now update the textfields
		trajStartF.setValue(box.getTrajStart());
		trajEndF.setValue(box.getTrajEnd());
		trajNStepsF.setValue(box.getTrajNSteps());
		trajTimestepF.setValue(box.getTrajTimestep());
		trajErrorF.setValue(box.getTrajError());
		trajSnapshotsF.setValue(box.getTrajSnapshots());
	}
	public void updateConfTab() {
		// Update third tab: Trajectory
		// First enable and disable the right stuff
		boolean enabled = box.isConfEnabled();

		confCB.setSelected(enabled);
		confNStepsL.setEnabled(enabled);
		confNStepsF.setEnabled(enabled);
		confErrorL.setEnabled(enabled);
		confErrorF.setEnabled(enabled);
		confMaxstepCB.setEnabled(enabled);

		enabled = (box.isConfEnabled() && box.isConfMaxstepEnabled());

		confMaxstepCB.setSelected(enabled);
		confMaxstepF.setEnabled(enabled);

		// Now update the textfields
		confNStepsF.setValue(box.getConfNSteps());
		confErrorF.setValue(box.getConfError());
		confMaxstepF.setValue(box.getConfMaxstep());
	}
	public void setBoxValues() {
		box.setDimension(CB_Box.WIDTH, ((Double) widthF.getValue()).doubleValue());
		box.setDimension(CB_Box.HEIGHT, ((Double) heightF.getValue()).doubleValue());
		box.setDimension(CB_Box.DEPTH, ((Double) depthF.getValue()).doubleValue());
	}
	public void setTempValues() {
		box.setTempBoltzmann(((Double) tempBoltzmannF.getValue()).doubleValue());
		box.setTempTemperature(((Double) tempTemperatureF.getValue()).doubleValue());
		box.setTempTau(((Double) tempTauF.getValue()).doubleValue());
		box.setTempRmf(((Double) tempRmfF.getValue()).doubleValue());
		box.setTempGamma(((Double) tempGammaF.getValue()).doubleValue());
		box.setTempCmrf(((Double) tempCmrfF.getValue()).doubleValue());
	}
	public void setTrajValues() {
		box.setTrajStart(((Double) trajStartF.getValue()).doubleValue());
		box.setTrajEnd(((Double) trajEndF.getValue()).doubleValue());
		box.setTrajNSteps(((Integer) trajNStepsF.getValue()).intValue());
		box.setTrajTimestep(((Double) trajTimestepF.getValue()).doubleValue());
		box.setTrajError(((Double) trajErrorF.getValue()).doubleValue());
		box.setTrajSnapshots(((Integer) trajSnapshotsF.getValue()).intValue());
	}
	public void setConfValues() {
		box.setConfNSteps(((Integer) confNStepsF.getValue()).intValue());
		box.setConfError(((Double) confErrorF.getValue()).doubleValue());
		box.setConfMaxstep(((Double) confMaxstepF.getValue()).doubleValue());
	}
	public void actionPerformed( ActionEvent e ) {
		Object event = e.getSource();
		if (event == typeCellRB) {
			box.setType(CB_Box.TYPE_CELL);
			this.setBoxValues();
			this.updateBoxTab();
		} else if (event == typePeriodicRB) {
			box.setType(CB_Box.TYPE_PERIODIC);
			this.setBoxValues();
			this.updateBoxTab();
		} else if (event == tempCB) {
			box.enableTemp(tempCB.isSelected());
			this.setTempValues();
			this.updateTempTab();
		} else if (event == tempInitialRB) {
			box.setTempType(CB_Box.TEMP_TYPE_INITIAL);
			this.setTempValues();
			this.updateTempTab();
		} else if (event == tempConstantRB) {
			box.setTempType(CB_Box.TEMP_TYPE_CONSTANT);
			this.setTempValues();
			this.updateTempTab();
		} else if (event == tempTauCB) {
			box.enableTempTau(tempTauCB.isSelected());
			this.setTempValues();
			this.updateTempTab();
		} else if (event == tempAdvancedCB) {
			box.enableTempAdvanced(tempAdvancedCB.isSelected());
			this.setTempValues();
			this.updateTempTab();
		} else if (event == tempRmfRB) {
			box.setTempAdvancedType(CB_Box.TEMP_ADV_TYPE_RMF);
			this.setTempValues();
			this.updateTempTab();
		} else if (event == tempGammaRB) {
			box.setTempAdvancedType(CB_Box.TEMP_ADV_TYPE_GAMMA);
			this.setTempValues();
			this.updateTempTab();
		} else if (event == tempCmrfCB) {
			box.enableTempCmrf(tempCmrfCB.isSelected());
			this.setTempValues();
			this.updateTempTab();
		} else if (event == trajCB) {
			box.enableTraj(trajCB.isSelected());
			this.setTrajValues();
			this.updateTrajTab();
		} else if (event == trajEndRB) {
			box.setTrajType(CB_Box.TRAJ_TYPE_END);
			this.setTrajValues();
			this.updateTrajTab();
		} else if (event == trajNStepsRB) {
			box.setTrajType(CB_Box.TRAJ_TYPE_N);
			this.setTrajValues();
			this.updateTrajTab();
		} else if (event == trajErrorCB) {
			box.enableTrajError(trajErrorCB.isSelected());
			this.setTrajValues();
			this.updateTrajTab();
		} else if (event == trajSnapshotsCB) {
			box.enableTrajSnapshots(trajSnapshotsCB.isSelected());
			this.setTrajValues();
			this.updateTrajTab();
		} else if (event == confCB) {
			box.enableConf(confCB.isSelected());
			this.setConfValues();
			this.updateConfTab();
		} else if (event == confMaxstepCB) {
			box.enableConfMaxstep(confMaxstepCB.isSelected());
			this.setConfValues();
			this.updateConfTab();
		} else if (event == okButton) {
			this.setBoxValues();
			this.setTempValues();
			this.setTrajValues();
			this.setConfValues();
			instance.setNewBox(box);
			this.setVisible(false);
		} else if (event == cancelButton) {
			this.setVisible(false);
		}
	}
}
