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

// Class that stores all properties of an interaction

public class CB_Interaction extends CB_Relation {
	// Interaction properties
	private double force;
	private double equilibrium;
	private double exponential;
	private double a1;
	private double a2;
	private double a3;
	private double degree;
	private double period;

	public static final int GRAVITATIONAL = 0;
	public static final int COULOMB = 1;
	public static final int LENNARD_JONES = 2;
	public static final int MORSE = 3;
	public static final int RYDBERG = 4;
	public static final int HARMONIC_STRETCH = 5;
	public static final int HARMONIC_BENDING = 6;
	public static final int PERIODIC_TORSIONAL = 7;

	public static final String[] NAMES = {"Gravitational", "Coulomb", "Lennard-Jones", "Morse", "Rydberg", "Harmonic Stretch", "Harmonic Bending", "Periodic Torsional"};

	public CB_Interaction(int[] particles, int type, String name) {
		super(particles, type, name);
	}
	public void setForce(double force) {
		this.force = force;
	}
	public void setEquilibrium(double equilibrium) {
		this.equilibrium = equilibrium;
	}
	public void setExponential(double exponential) {
		this.exponential = exponential;
	}
	public void setA1(double a1) {
		this.a1 = a1;
	}
	public void setA2(double a2) {
		this.a2 = a2;
	}
	public void setA3(double a3) {
		this.a3 = a3;
	}
	public void setDegree(double degree) {
		this.degree = degree;
	}
	public void setPeriod(double period) {
		this.period = period;
	}
	public double getForce() {
		return force;
	}
	public double getEquilibrium() {
		return equilibrium;
	}
	public double getExponential() {
		return exponential;
	}
	public double getA1() {
		return a1;
	}
	public double getA2() {
		return a2;
	}
	public double getA3() {
		return a3;
	}
	public double getDegree() {
		return degree;
	}
	public double getPeriod() {
		return period;
	}
	// Returns a string that describes the type of interaction
	public String getTypeString() {
		return CB_Interaction.NAMES[super.getType()];
	}
	public String getTooltip() {
		return this.getTooltip(null);
	}
	// Returns a tooltip string describing the interaction
	public String getTooltip(CB_Instance instance) {
		StringBuilder tooltip = new StringBuilder();
		tooltip.append("<html><h3>" + super.getName() + "</h3><h4>" + this.getTypeString() + "</h4>");
		if (instance != null) {
			tooltip.append("<table valign='top'>");
			tooltip.append("<tr><td>");
		}
		tooltip.append("<strong>Properties</strong>");
		tooltip.append("<table>");
		tooltip.append("<tr><td>Force:</td><td>" + force + "</td></tr>");
		switch (super.getType()) {
			case CB_Interaction.LENNARD_JONES:
				tooltip.append("<tr><td>Equilibrium Distance:</td><td>" + equilibrium + "</td></tr>");
			break;
			case CB_Interaction.MORSE:
				tooltip.append("<tr><td>Equilibrium Distance:</td><td>" + equilibrium + "</td></tr>");
				tooltip.append("<tr><td>Exponential Parameter:</td><td>" + exponential + "</td></tr>");
			break;
			case CB_Interaction.RYDBERG:
				tooltip.append("<tr><td>Equilibrium Distance:</td><td>" + equilibrium + "</td></tr>");
				tooltip.append("<tr><td>Exponential Parameter:</td><td>" + exponential + "</td></tr>");
				tooltip.append("<tr><td>A1:</td><td>" + a1 + "</td></tr>");
				tooltip.append("<tr><td>A2:</td><td>" + a2 + "</td></tr>");
				tooltip.append("<tr><td>A3:</td><td>" + a3 + "</td></tr>");
			break;
			case CB_Interaction.HARMONIC_STRETCH:
				tooltip.append("<tr><td>Equilibrium Distance:</td><td>" + equilibrium + "</td></tr>");
			break;
			case CB_Interaction.HARMONIC_BENDING:
				tooltip.append("<tr><td>Degree:</td><td>" + degree + "</td></tr>");
			break;
			case CB_Interaction.PERIODIC_TORSIONAL:
				tooltip.append("<tr><td>Degree:</td><td>" + degree + "</td></tr>");
				tooltip.append("<tr><td>Period:</td><td>" + period + "</td></tr>");
			break;
		}
		tooltip.append("</table>");
		if (instance != null) {
			int interaction = instance.getInteractions().indexOf(this);
			// Build the interaction column(s)
			tooltip.append("</td><td>");
			int count = 0;
			StringBuilder particles = new StringBuilder();
			for (int i = 0; i < instance.getParticles().size(); i++) {
				if (instance.getInteraction(interaction).getParticleIndex(i) >= 0) {
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
	// Clones this interaction
	public CB_Interaction clone(boolean stripParticles) {
		CB_Interaction interaction;
		if (stripParticles) {
			interaction = new CB_Interaction(new int[] {}, super.getType(), super.getName());
		} else {
			interaction = new CB_Interaction(super.cloneParticles(), super.getType(), super.getName());
		}
		interaction.setForce(force);
		interaction.setEquilibrium(equilibrium);
		interaction.setExponential(exponential);
		interaction.setA1(a1);
		interaction.setA2(a2);
		interaction.setA3(a3);
		interaction.setDegree(degree);
		interaction.setPeriod(period);
		return interaction;
	}
	public int getRelationType() {
		return CB_Instance.INTERACTIONS;
	}
	public boolean hasRelationCenter() {
		if (super.getType() == HARMONIC_BENDING || super.getType() == PERIODIC_TORSIONAL) {
			return false;
		}
		return true;
	}
	public static String getTypeString(int type) {
		return NAMES[type];
	}
}