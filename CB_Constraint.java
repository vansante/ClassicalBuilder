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

// Class that stores all the constraint properties

public class CB_Constraint extends CB_Relation {
	// Constraint properties
	private double distance;
	private boolean distanceEnabled;
	private int maxstep = 1000;
	private boolean maxstepEnabled;
	private double error = 1e-012;
	private boolean errorEnabled;

	public static final int DISTANCE = 0;

	public static final String[] NAMES = {"Distance"};

	// Constructor that sets the particles, type and name
	public CB_Constraint(int[] particles, int type, String name) {
		super(particles, type, name);
		distanceEnabled = false;
		maxstepEnabled = false;
		errorEnabled = false;
	}
	// Functions to set all properties
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public void setMaxstep(int maxstep) {
		this.maxstep = maxstep;
	}
	public void setError(double error) {
		this.error = error;
	}
	public void enableDistance(boolean enable) {
		this.distanceEnabled = enable;
	}
	public void enableMaxstep(boolean enable) {
		this.maxstepEnabled = enable;
	}
	public void enableError(boolean enable) {
		this.errorEnabled = enable;
	}
	// Functions to get all properties
	public boolean isDistanceEnabled() {
		return distanceEnabled;
	}
	public boolean isMaxstepEnabled() {
		return maxstepEnabled;
	}
	public boolean isErrorEnabled() {
		return errorEnabled;
	}
	public double getDistance() {
		return distance;
	}
	public int getMaxstep() {
		return maxstep;
	}
	public double getError() {
		return error;
	}
	// Returns a string that describes the type of constraint
	public String getTypeString() {
		return CB_Interaction.NAMES[super.getType()];
	}
	public String getTooltip() {
		return this.getTooltip(null);
	}
	// Returns a tooltip string describing the constraint
	public String getTooltip(CB_Instance instance) {
		StringBuilder tooltip = new StringBuilder();
		tooltip.append("<html><h3>" + super.getName() + "</h3><h4>" + this.getTypeString() + "</h4>");
		if (instance != null) {
			tooltip.append("<table valign='top'>");
			tooltip.append("<tr><td>");
		}
		tooltip.append("<strong>Properties</strong>");
		tooltip.append("<table>");
		switch (super.getType()) {
			case CB_Constraint.DISTANCE:
				if (distanceEnabled) {
					tooltip.append("<tr><td>Distance:</td><td>" + distance + "</td></tr>");
				}
				if (maxstepEnabled) {
					tooltip.append("<tr><td>Maxstep:</td><td>" + maxstep + "</td></tr>");
				}
				if (errorEnabled) {
					tooltip.append("<tr><td>Error:</td><td>" + error + "</td></tr>");
				}
			break;
		}
		tooltip.append("</table>");
		if (instance != null) {
			int constraint = instance.getConstraints().indexOf(this);
			// Build the interaction column(s)
			tooltip.append("</td><td>");
			int count = 0;
			StringBuilder particles = new StringBuilder();
			for (int i = 0; i < instance.getParticles().size(); i++) {
				if (instance.getConstraint(constraint).getParticleIndex(i) >= 0) {
					particles.append(instance.getParticle(i).getName() + "<br/>");
					count++;
					// After 30 start a new column
					if (count % 30 == 0) {
						particles.append("</td><td><br/><br/>");
					}
				}
			}
			tooltip.append("<strong>Particles (" + count + ")</strong><br/><br/>");
			tooltip.append(particles);
			tooltip.append("</td></tr>");
			tooltip.append("</table>");
		}
		tooltip.append("</html>");
		return tooltip.toString();
	}
	// Clones this constraint
	public CB_Constraint clone(boolean stripParticles) {
		CB_Constraint constraint;
		if (stripParticles) {
			constraint = new CB_Constraint(new int[] {}, super.getType(), super.getName());
		} else {
			constraint = new CB_Constraint(super.cloneParticles(), super.getType(), super.getName());
		}
		constraint.setDistance(distance);
		constraint.setMaxstep(maxstep);
		constraint.setError(error);

		constraint.enableDistance(distanceEnabled);
		constraint.enableMaxstep(maxstepEnabled);
		constraint.enableError(errorEnabled);
		return constraint;
	}
	public int getRelationType() {
		return CB_Instance.CONSTRAINTS;
	}
	public boolean hasRelationCenter() {
		if (super.getType() == DISTANCE) {
			return false;
		}
		return true;
	}
	public static String getTypeString(int type) {
		return NAMES[type];
	}
}