/*
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

import java.io.Serializable;

public class CB_Particle implements Serializable {
	// The properties of the particle
	private String name;
	private int colorId;
	private double radius;
	private double mass;
	private double charge;
	private double[] position; // Position array [0] = X, [1] = Y, [2] = Z
	private double[] momentum; // Momentum array [0] = X, [1] = Y, [2] = Z

	public static final int X = 0;
	public static final int Y = 1;
	public static final int Z = 2;

	// Constructor that sets all properties
	public CB_Particle(String name, int colorId, double radius, double mass, double charge, double[] position, double[] momentum) {
		this.name = name;
		this.colorId = colorId;
		this.radius = radius;
		this.mass = mass;
		this.charge = charge;
		this.position = position;
		this.momentum = momentum;
	}
	// Constructor that creates a particle with default values
	public CB_Particle() {
		this("New Particle", 0, 1.0, 1.0, 1.0, new double[] {0.0, 0.0, 0.0}, new double[] {0.0, 0.0, 0.0} );
	}
	// Method to set all particle properties in one go
	public void setProperties(String name, int colorId, double radius, double mass, double charge, double[] position, double[] momentum) {
		this.name = name;
		this.colorId = colorId;
		this.radius = radius;
		this.mass = mass;
		this.charge = charge;
		this.position = position;
		this.momentum = momentum;
	}
	// Various set methods
	public void setName(String name) {
		this.name = name;
	}
	public void setRadius(double radius) {
		this.radius	= radius;
	}
	public void setMass(double mass) {
		this.mass	= mass;
	}
	public void setCharge(double charge) {
		this.charge	= charge;
	}
	public void setColor(int colorId) {
		this.colorId = colorId;
	}
	public void setPosition(double[] position) {
		this.position = position;
	}
	public void setPosIndex(int index, double position) {
		this.position[index] = position;
	}
	public void setMomentum(double[] momentum) {
		this.momentum = momentum;
	}
	public void setMomIndex(int index, double momentum) {
		this.momentum[index] = momentum;
	}
	// Various get methods
	public String getName() {
		return name;
	}
	public int getColor() {
		return colorId;
	}
	public double getMass() {
		return mass;
	}
	public double getRadius() {
		return radius;
	}
	public double getCharge() {
		return charge;
	}
	public double[] getPosition() {
		return position;
	}
	public double getPosIndex(int index) {
		return position[index];
	}
	public double getX() {
		return position[X];
	}
	public double getY() {
		return position[Y];
	}
	public double getZ() {
		return position[Z];
	}
	public double[] getMomentum() {
		return momentum;
	}
	public double getMomIndex(int index) {
		return momentum[index];
	}
	public String getTooltip() {
		return this.getTooltip(null);
	}
	// Returns a tooltip string describing the particle properties, instance can be null
	public String getTooltip(CB_Instance instance) {
		StringBuilder tooltip = new StringBuilder();
		tooltip.append("<html>");
		tooltip.append("<h3>" + name + "</h3>");
		// If instance is set, start a multi column tooltip
		if (instance != null) {
			tooltip.append("<table valign='top'>");
			tooltip.append("<tr><td>");
		}
		tooltip.append("<table>");
		tooltip.append("<tr><td colspan=2><strong>Properties</strong></td></tr>");
		tooltip.append("<tr><td>Mass:</td><td>" + mass + "</td></tr>");
		tooltip.append("<tr><td>Radius:</td><td>" + radius + "</td></tr>");
		tooltip.append("<tr><td>Charge:</td><td>" + charge + "</td></tr>");
		tooltip.append("<tr><td colspan=2><strong>Position</strong></td></tr>");
		tooltip.append("<tr><td>[X]:</td><td>" + position[0] + "</td></tr>");
		tooltip.append("<tr><td>[Y]:</td><td>" + position[1] + "</td></tr>");
		tooltip.append("<tr><td>[Z]:</td><td>" + position[2] + "</td></tr>");
		tooltip.append("<tr><td colspan=2><strong>Momentum</strong></td></tr>");
		tooltip.append("<tr><td>[X]:</td><td>" + momentum[0] + "</td></tr>");
		tooltip.append("<tr><td>[Y]:</td><td>" + momentum[1] + "</td></tr>");
		tooltip.append("<tr><td>[Z]:</td><td>" + momentum[2] + "</td></tr>");
		tooltip.append("</table>");

		if (instance != null) {
			int particle = instance.getParticles().indexOf(this);
			// Build the interaction column(s)
			int count = 0;
			StringBuilder relations = new StringBuilder();
			for (int i = 0; i < instance.getInteractions().size(); i++) {
				if (instance.getInteraction(i).getParticleIndex(particle) >= 0) {
					relations.append(instance.getInteraction(i).getName() + "<br/>");
					count++;
					// After 40 start a new column
					if (count % 40 == 0) {
						relations.append("</td><td><br/><br/>");
					}
				}
			}
			// Only display if there are any
			if (count > 0) {
				tooltip.append("</td><td>");
				tooltip.append("<strong>Interactions (" + count + ")</strong><br/><br/>");
				tooltip.append(relations);
			}

			// Build the constraint column(s)
			count = 0;
			relations = new StringBuilder();
			for (int i = 0; i < instance.getConstraints().size(); i++) {
				if (instance.getConstraint(i).getParticleIndex(particle) >= 0) {
					relations.append(instance.getConstraint(i).getName() + "<br/>");
					count++;
					// After 40 start a new column
					if (count % 40 == 0) {
						relations.append("</td><td><br/><br/>");
					}
				}
			}
			// Only display if there are any
			if (count > 0) {
				tooltip.append("</td><td>");
				tooltip.append("<strong>Constraints (" + count + ")</strong><br/><br/>");
				tooltip.append(relations);
			}
			tooltip.append("</td></tr>");
			tooltip.append("</table>");
		}
		tooltip.append("</html>");
		return tooltip.toString();
	}
	// Returns a string describing the particle properties
	public String toString() {
		return name;
	}
	// Clones this particle
	public CB_Particle clone() {
		return new CB_Particle(name, colorId, radius, mass, charge, new double[] {position[0], position[1], position[2]}, new double[] {momentum[0], momentum[1], momentum[2]});
	}
}