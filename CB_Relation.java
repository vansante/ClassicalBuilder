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

// Class that stores all properties of an Relation

public abstract class CB_Relation {
	// Relation properties
	private int[] particles; // Array with the IDs of the involved particles
	private int relationType;
	private int type;
	private String name;

	// Constructor that sets the particles, type and name
	public CB_Relation(int[] particles, int type, String name) {
		this.particles = particles;
		this.type = type;
		this.name = name;
	}
	// Functions to set all properties
	public void setType(int type) {
		this.type = type;
	}
	public void setParticles(int[] particles) {
		this.particles = particles;
	}
	public void setParticle(int index, int particle) {
		this.particles[index] = particle;
	}
	public void setName(String name) {
		this.name = name;
	}
	// Functions to get all properties
	public int getType() {
		return type;
	}
	public int[] getParticles() {
		return particles;
	}
	public int getParticle(int index) {
		return particles[index];
	}
	public int getParticlesSize() {
		return particles.length;
	}
	public String getName() {
		return name;
	}
	public String toString() {
		return name;
	}
	public int getParticleIndex(int particle) {
		for (int i = 0; i < particles.length; i++) {
			if (particles[i] == particle) {
				return i;
			}
		}
		return -1;
	}
	public void addParticle(int particle) {
		int[] newParticles = new int[particles.length + 1];
		for (int i = 0; i < particles.length; i++) {
			newParticles[i] = particles[i];
		}
		newParticles[particles.length] = particle;
		particles = newParticles;
	}
	public void removeParticle(int particle) {
		if (particles.length > 0) {
			boolean found = false;
			int[] newParticles = new int[particles.length - 1];
			for (int i = 0; i < particles.length; i++) {
				if (found) {
					newParticles[i - 1] = particles[i];
				} else if (particles[i] != particle) {
					newParticles[i] = particles[i];
				} else {
					found = true;
				}
			}
			if (found) {
				particles = newParticles;
			}
		}
	}
	public void checkParticles(int amount) {
		for (int i = 0; i < particles.length; i++) {
			if (particles[i] >= amount) {
				int[] temp = new int[particles.length - 1];
				for (int u = 0; u < particles.length; u++) {
					if (i < u) {
						temp[i] = particles[i];
					} else if (i > u) {
						temp[i - 1] = particles[i];
					}
				}
				particles = temp;
				this.checkParticles(amount);
			}
		}
	}
	// Returns a string with all the particle IDs, seperated by spaces
	public String getParticlesString() {
		StringBuilder particlesString = new StringBuilder();
		for (int i = 0; i < particles.length; i++) {
			particlesString.append(particles[i]);
			if (i < particles.length - 1) {
				particlesString.append(" ");
			}
		}
		return particlesString.toString();
	}
	// Clones the relations particles
	public int[] cloneParticles() {
		int[] particlesCopy = new int[particles.length];
		for (int i = 0; i < particles.length; i++) {
			particlesCopy[i] = particles[i];
		}
		return particlesCopy;
	}
	public abstract String getTypeString();
	public abstract String getTooltip(CB_Instance instance);
	public abstract CB_Relation clone(boolean stripParticles);
	public abstract int getRelationType();
	public abstract boolean hasRelationCenter();
}