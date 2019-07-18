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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class CB_Box implements Transferable {

	// Box general
	private int type = TYPE_CELL;
	private double[] dimensions; // [0] = Width dimension, [1] = Height dimension, [2] = Depth dimension

	// Temperature
	private int tempType = TEMP_TYPE_INITIAL;
	private int tempAdvancedType = TEMP_ADV_TYPE_RMF;
	private double tempRmf;
	private double tempGamma;
	private double tempCmrf;
	private double tempTemperature = 293;
	private double tempBoltzmann = 1.0;
	private double tempTau;
	private boolean tempEnabled = false;
	private boolean tempTauEnabled = false;
	private boolean tempAdvancedEnabled = false;
	private boolean tempCmrfEnabled = false;

	// Trajectory
	private int trajNSteps;
	private int trajType = TRAJ_TYPE_END;
	private int trajSnapshots;
	private double trajTimestep;
	private double trajError;
	private double trajStart;
	private double trajEnd;
	private boolean trajEnabled = false;
	private boolean trajErrorEnabled = false;
	private boolean trajSnapshotsEnabled = false;

	// Conformation
	private int confNSteps = 10000;
	private double confError;
	private double confMaxstep;
	private boolean confMaxstepEnabled;
	private boolean confEnabled = false;

	// Dimensions
	public static final int WIDTH = 0;
	public static final int HEIGHT = 1;
	public static final int DEPTH = 2;

	// Box types
	public static final int TYPE_CELL = 0;
	public static final int TYPE_PERIODIC = 1;

	// Temperature types
	public static final int TEMP_TYPE_INITIAL = 0;
	public static final int TEMP_TYPE_CONSTANT = 1;

	// Temperature advanced types
	public static final int TEMP_ADV_TYPE_RMF = 0;
	public static final int TEMP_ADV_TYPE_GAMMA = 1;

	// Trajectory types
	public static final int TRAJ_TYPE_END = 0;
	public static final int TRAJ_TYPE_N = 1;

	private static final DataFlavor dataFlavor = new DataFlavor(CB_Box.class, "ClassicalBuilder Box Properties");

	// Box constructor with dimensions
	public CB_Box(int type, double[] dimensions) {
		this.type = type;
		this.dimensions = dimensions;
	}
	public CB_Box() {
		this(TYPE_PERIODIC, new double[] {10.0, 10.0, 10.0});
	}
	// Box Setters
	public void setType(int type) {
		this.type = type;
	}
	public void setDimensions(double[] dimensions) {
		this.dimensions = dimensions;
	}
	public void setDimension(int index, double dimension) {
		this.dimensions[index] = dimension;
	}
	// Temperature Enablers
	public void enableTemp(boolean enable) {
		this.tempEnabled = enable;
	}
	public void enableTempTau(boolean enable) {
		this.tempTauEnabled = enable;
	}
	public void enableTempAdvanced(boolean enable) {
		this.tempAdvancedEnabled = enable;
	}
	public void enableTempCmrf(boolean enable) {
		this.tempCmrfEnabled = enable;
	}
	// Temperature Setters
	public void setTempType(int type) {
		this.tempType = type;
	}
	public void setTempTemperature(double temperature) {
		this.tempTemperature = temperature;
	}
	public void setTempBoltzmann(double boltzmann) {
		this.tempBoltzmann = boltzmann;
	}
	public void setTempTau(double tau) {
		this.tempTau = tau;
	}
	public void setTempAdvancedType(int type) {
		this.tempAdvancedType = type;
	}
	public void setTempRmf(double Rmf) {
		this.tempRmf = Rmf;
	}
	public void setTempGamma(double gamma) {
		this.tempGamma = gamma;
	}
	public void setTempCmrf(double Cmrf) {
		this.tempCmrf = Cmrf;
	}
	// Trajectory Enablers
	public void enableTraj(boolean enable) {
		this.trajEnabled = enable;
	}
	public void enableTrajError(boolean enable) {
		this.trajErrorEnabled = enable;
	}
	public void enableTrajSnapshots(boolean enable) {
		this.trajSnapshotsEnabled = enable;
	}
	// Trajectory Setters
	public void setTrajStart(double start) {
		this.trajStart= start;
	}
	public void setTrajEnd(double end) {
		this.trajEnd = end;
	}
	public void setTrajNSteps(int n) {
		this.trajNSteps = n;
	}
	public void setTrajType(int type) {
		this.trajType = type;
	}
	public void setTrajTimestep(double timestep) {
		this.trajTimestep = timestep;
	}
	public void setTrajError(double error) {
		this.trajError = error;
	}
	public void setTrajSnapshots(int snapshots) {
		this.trajSnapshots = snapshots;
	}
	// Conformation Enablers
	public void enableConf(boolean enable) {
		this.confEnabled = enable;
	}
	public void enableConfMaxstep(boolean enable) {
		this.confMaxstepEnabled = enable;
	}
	// Conformation Setters
	public void setConfNSteps(int n) {
		this.confNSteps = n;
	}
	public void setConfError(double error) {
		this.confError = error;
	}
	public void setConfMaxstep(double maxstep) {
		this.confMaxstep = maxstep;
	}
	// Box getters
	public int getType() {
		return type;
	}
	public double[] getDimensions() {
		return dimensions;
	}
	public double getDimension(int index) {
		return dimensions[index];
	}
	// Temperature isEnabled-ers
	public boolean isTempEnabled() {
		return tempEnabled;
	}
	public boolean isTempTauEnabled() {
		return tempTauEnabled;
	}
	public boolean isTempAdvancedEnabled() {
		return tempAdvancedEnabled;
	}
	public boolean isTempCmrfEnabled() {
		return tempCmrfEnabled;
	}
	// Temperature getters
	public int getTempType() {
		return tempType;
	}
	public double getTempTemperature() {
		return tempTemperature;
	}
	public double getTempBoltzmann() {
		return tempBoltzmann;
	}
	public double getTempTau() {
		return tempTau;
	}
	public int getTempAdvancedType() {
		return tempAdvancedType;
	}
	public double getTempRmf() {
		return tempRmf;
	}
	public double getTempGamma() {
		return tempGamma;
	}
	public double getTempCmrf() {
		return tempCmrf;
	}
	// Trajectory isEnabled-ers
	public boolean isTrajEnabled() {
		return trajEnabled;
	}
	public boolean isTrajErrorEnabled() {
		return trajErrorEnabled;
	}
	public boolean isTrajSnapshotsEnabled() {
		return trajSnapshotsEnabled;
	}
	// Trajectory getters
	public double getTrajStart() {
		return trajStart;
	}
	public double getTrajEnd() {
		return trajEnd;
	}
	public int getTrajNSteps() {
		return trajNSteps;
	}
	public int getTrajType() {
		return trajType;
	}
	public double getTrajTimestep() {
		return trajTimestep;
	}
	public double getTrajError() {
		return trajError;
	}
	public int getTrajSnapshots() {
		return trajSnapshots;
	}
	// Conformation isEnabled-ers
	public boolean isConfEnabled() {
		return confEnabled;
	}
	public boolean isConfMaxstepEnabled() {
		return confMaxstepEnabled;
	}
	// Conformation getters
	public int getConfNSteps() {
		return confNSteps;
	}
	public double getConfError() {
		return confError;
	}
	public double getConfMaxstep() {
		return confMaxstep;
	}
	// Clone
	public CB_Box clone() {
		// Box
		CB_Box box = new CB_Box(type, new double[] { dimensions[0], dimensions[1], dimensions[2] });
		// Set temperature properties
		box.enableTemp(tempEnabled);
		box.setTempType(tempType);
		box.setTempTemperature(tempTemperature);
		box.setTempBoltzmann(tempBoltzmann);
		box.setTempTau(tempTau);
		box.enableTempTau(tempTauEnabled);
		box.enableTempAdvanced(tempAdvancedEnabled);
		box.setTempAdvancedType(tempAdvancedType);
		box.setTempGamma(tempGamma);
		box.setTempRmf(tempRmf);
		box.setTempCmrf(tempCmrf);
		box.enableTempCmrf(tempCmrfEnabled);
		// Set trajectory properties
		box.enableTraj(trajEnabled);
		box.setTrajStart(trajStart);
		box.setTrajEnd(trajEnd);
		box.setTrajNSteps(trajNSteps);
		box.setTrajType(trajType);
		box.setTrajTimestep(trajTimestep);
		box.setTrajError(trajError);
		box.enableTrajError(trajErrorEnabled);
		box.setTrajSnapshots(trajSnapshots);
		box.enableTrajSnapshots(trajSnapshotsEnabled);
		// Set conformation properties
		box.enableConf(confEnabled);
		box.setConfNSteps(confNSteps);
		box.setConfError(confError);
		box.setConfMaxstep(confMaxstep);
		box.enableConfMaxstep(confMaxstepEnabled);

		return box;
	}
	// Methods that enable data transferring
	public Object getTransferData(DataFlavor flavor) {
		return this.clone();
	}
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {dataFlavor};
	}
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (flavor.equals(dataFlavor)) {
			return true;
		}
		return false;
	}
	public static DataFlavor getDataFlavor() {
		return dataFlavor;
	}
}