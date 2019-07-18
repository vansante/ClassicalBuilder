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

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.text.ParseException;

//this class imports all the values out of a valid save file.
public class CB_Import {
	// Created objects
	private CB_Box box;
	private CB_Colors colors;
	private CB_Particles particles;
	private CB_Relations interactions;
	private CB_Relations constraints;

	// Input String
	private String[] inputLines;
	private ArrayList<Integer> colorTranslate, particleTranslate;

	// Read values
	private int lastReadInt;
	private double lastReadDouble;
	private double[] lastReadVector;
	private String lastReadString;

	// Various regex patterns:
	private static final Pattern namePattern = Pattern.compile("^[^#]+\\Q#!CB name=\"\\E([^']+)\\Q\"\\E.*$");
	private static final Pattern particlesPattern = Pattern.compile("^[^{}]+\\{ ([\\d\\Qto\\E ]+)\\}[^{}]*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern allParticlesPattern = Pattern.compile("^[^{}]+\\{ *\\Qall\\E\\ *\\}[^{}]*$", Pattern.CASE_INSENSITIVE);

	public CB_Import(String input) throws ParseException {
		this.inputLines = this.processInput(input);
		this.checkDuplicates(input);
		this.readBox();
		this.readColors();
		this.readParticles();
		this.readInteractions();
		this.readConstraints();
	}
	public CB_Box getBox() {
		return box;
	}
	public CB_Colors getColors() {
		return colors;
	}
	public CB_Particles getParticles() {
		return particles;
	}
	public CB_Relations getInteractions() {
		return interactions;
	}
	public CB_Relations getConstraints() {
		return constraints;
	}
	// function that checks if theyre multiple state occurances in the input, and strips all but the last one
	private void checkDuplicates(String input) {
		for (int i = 0; i < inputLines.length; i++) {
			if (inputLines[i].length() > 5 && inputLines[i].substring(0, 5).equalsIgnoreCase("color")) {
				Pattern colorPattern = Pattern.compile("^\\Q" + inputLines[i] + "\\E$", Pattern.MULTILINE);
				Matcher matcher = colorPattern.matcher(input);
				int count = 0;
				int index = 0;
				while (matcher.find()) {
					index = matcher.start();
					count++;
				}
				if (count > 1) {
					input = input.substring(index);
					System.out.println("Import: Found " + count + " state occurances in import string, assuming last one.");
					inputLines = this.processInput(input);
				}
				break;
			}
		}
	}
	private void readColors() throws ParseException {
		colors = new CB_Colors(false);
		colorTranslate = new ArrayList<Integer>(30);
		for (int i = 0; i < inputLines.length; i++) {
			if (inputLines[i].length() > 5 && inputLines[i].substring(0, 5).equalsIgnoreCase("color")) {
				String[] line = inputLines[i].split(" ");
				boolean id = false;
				boolean rgb = false;
				for (int u = 1; u < line.length; u++) {
					if (this.readInt(line[u], "id")) {//read the colorid value
						id = true;
					} else if (this.readVector(line[u], "rgb")) {//read the color value
						rgb = true;
					}
				}
				if (id && rgb) {
					if (colors.size() > 0 && colorTranslate.indexOf(lastReadInt) > 0) {
						throw new ParseException("Duplicate color id found at line " + (i + 1), i + 1);
					}
					String name = "R=" + lastReadVector[0] + " G=" + lastReadVector[1] + " B=" + lastReadVector[2];
					if (this.readNameString(inputLines[i])) {
						name = lastReadString;
					}
					colorTranslate.add(lastReadInt);
					colors.add(new CB_Color(name, (float) lastReadVector[0], (float) lastReadVector[1], (float) lastReadVector[2]));
				} else {
					throw new ParseException("Invalid color found at line " + (i + 1), i + 1);
				}
			}
		}
		if (colors.size() == 0) {
			throw new ParseException("No colors found in file!", 0);
		}
	}
	//read the box properties
	private void readBox() throws ParseException {
		boolean boxFound = false;
		box = new CB_Box();
		for (int i = 0; i < inputLines.length; i++) {
			String[] line = inputLines[i].split(" ");
			if (line.length == 0) {
				// Empty line
			} else if (line[0].equalsIgnoreCase("box")) {
				boxFound = true;
				for (int u = 1; u < line.length; u++) {//read the cell value
					if (this.readVector(line[u], "cell")) {
						box.setDimensions(lastReadVector);
						box.setType(CB_Box.TYPE_CELL);
					} else if (this.readVector(line[u], "periodic")) {//read the periodic value
						box.setDimensions(lastReadVector);
						box.setType(CB_Box.TYPE_PERIODIC);
					} else {
						throw new ParseException("Invalid box statement found at line " + (i + 1), i + 1);
					}
				}
			} else if (line[0].equalsIgnoreCase("conformation")) {
				box.enableConf(true);
				for (int u = 1; u < line.length; u++) {//read the n value
					if (this.readInt(line[u], "n")) {
						box.setConfNSteps(lastReadInt);
					} else if (this.readDouble(line[u], "error")) {//read the error value
						box.setConfError(lastReadDouble);
					} else if (this.readDouble(line[u], "maxstep")) {//read the maxstep value
						box.setConfMaxstep(lastReadDouble);
						box.enableConfMaxstep(true);
					}
				}
			} else if (line[0].equalsIgnoreCase("temperature")) {
				for (int u = 1; u < line.length; u++) {
					box.enableTemp(true);
					if (this.readDouble(line[u], "k")) {
						box.setTempBoltzmann(lastReadDouble);
					} else if (this.readDouble(line[u], "constant")) {//read the constant value
						box.setTempTemperature(lastReadDouble);
						box.setTempType(CB_Box.TEMP_TYPE_CONSTANT);
					} else if (this.readDouble(line[u], "initial")) {//read the initial value
						box.setTempTemperature(lastReadDouble);
						box.setTempType(CB_Box.TEMP_TYPE_INITIAL);
					} else if (this.readDouble(line[u], "tau")) {//read the tau value
						box.setTempTau(lastReadDouble);
						box.enableTempTau(true);
					} else if (this.readDouble(line[u], "gamma")) {//read the gamma value
						box.setTempGamma(lastReadDouble);
						box.setTempAdvancedType(CB_Box.TEMP_ADV_TYPE_GAMMA);
						box.enableTempAdvanced(true);
					} else if (this.readDouble(line[u], "rmf")) {//read the rmd value
						box.setTempRmf(lastReadDouble);
						box.setTempAdvancedType(CB_Box.TEMP_ADV_TYPE_RMF);
						box.enableTempAdvanced(true);
					} else if (this.readDouble(line[u], "cmrf")) {//read the cmrf value
						box.setTempCmrf(lastReadDouble);
						box.enableTempCmrf(true);
					}
				}
			} else if (line[0].equalsIgnoreCase("dynamics")) {
				box.enableTraj(true);
				for (int u = 1; u < line.length; u++) {
					if (this.readDouble(line[u], "dt")) {//read the dt value
						box.setTrajTimestep(lastReadDouble);
					} else if (this.readDouble(line[u], "tend")) {//read the tend value
						box.setTrajEnd(lastReadDouble);
					} else if (this.readDouble(line[u], "t")) {//read the t value
						box.setTrajStart(lastReadDouble);
					} else if (this.readDouble(line[u], "error")) {//read the error value
						box.setTrajError(lastReadDouble);
						box.enableTrajError(true);
					} else if (this.readInt(line[u], "snapshots")) {//read the snapshots value
						box.setTrajSnapshots(lastReadInt);
						box.enableTrajSnapshots(true);
					} else if (this.readInt(line[u], "n")) {//read the n value
						box.setTrajNSteps(lastReadInt);
					}
				}
			}
		}
		if (!boxFound) {
			throw new ParseException("No box statement found in file!", 0);
		}
	}
	//read the particles
	private void readParticles() throws ParseException {
		particles = new CB_Particles(30);
		particleTranslate = new ArrayList<Integer>(40);
		// Scale
		double[] scale = new double[] {1, 1, 1};
		// Translate
		double[] translate = new double[] {0, 0, 0};
		// Rotate
		double[] rotateAxis = new double[] {0, 0, 0};
		double rotateAngle = 0;
		// Particle properties
		int particleId = 0, particleColor = 0;
		double particleMass = 1.0, particleCharge = 1.0, particleRadius = 1.0;
		double[] particlePosition = new double[] {0, 0, 0}, particleMomentum = new double[] {0, 0, 0};
		for (int i = 0; i < inputLines.length; i++) {
			String[] line = inputLines[i].split(" ");
			if (line.length == 0) {
				// Empty line
			} else if (line[0].equalsIgnoreCase("particle")) {
				boolean idSet = false;
				for (int u = 1; u < line.length; u++) {
					if (this.readInt(line[u], "id")) {//read the particle-id value
						particleId = lastReadInt;
						idSet = true;
					} else if (this.readInt(line[u], "c")) {//read the particle-color value
						int temp = colorTranslate.indexOf(lastReadInt);
						if (temp < 0) {
							throw new ParseException("Invalid particle color found at line " + (i + 1), i + 1);
						}
						particleColor = temp;
					} else if (this.readDouble(line[u], "m")) {//read the particle-mass
						particleMass = lastReadDouble;
					} else if (this.readDouble(line[u], "q")) {//read the particle-charge
						particleCharge = lastReadDouble;
					} else if (this.readDouble(line[u], "r")) {//read the particle-radius
						particleRadius = lastReadDouble;
					} else if (this.readVector(line[u], "x")) {//read the particle-position(normal)
						particlePosition = lastReadVector;
						if (rotateAngle != 0.0) {
							particlePosition = this.rotatePosition(lastReadVector, rotateAxis, rotateAngle);
						}
						for (int j = 0; j <= 2; j++) {
							particlePosition[j] = scale[j] * (particlePosition[j] + translate[j]);
						}
					}  else if (this.readVector(line[u], "s")) {//read the particle-position(spherical)
						particlePosition = lastReadVector;
						particlePosition = this.sphericalToCartesian(particlePosition);
						if (rotateAngle != 0.0) {
							particlePosition = this.rotatePosition(lastReadVector, rotateAxis, rotateAngle);
						}
						for (int j = 0; j <= 2; j++) {
							particlePosition[j] = scale[j] * (particlePosition[j] + translate[j]);
						}
					} else if (this.readVector(line[u], "p")) {//read the particle-momentum(normal)
						particleMomentum = lastReadVector;
					} else if (this.readVector(line[u], "ps")) {//read the particle-momentum(spherical)
						particleMomentum = this.sphericalToCartesian(lastReadVector);
					}
				}
				if (!idSet) {
					particleId++;
				}
				if (particles.size() > 0 && particleTranslate.indexOf(particleId) > 0) {
					throw new ParseException("Duplicate particle id found at line " + (i + 1), i + 1);
				}
				String name = "Particle " + (particles.size() + 1);
				if (this.readNameString(inputLines[i])) {
					name = lastReadString;
				}
				particleTranslate.add(particleId);
				particles.add(new CB_Particle(name, particleColor, particleRadius, particleMass, particleCharge, particlePosition, particleMomentum));
			} else if (line[0].equalsIgnoreCase("scale")) {//read the scale values
				for (int u = 1; u < line.length; u++) {
					if (this.readDouble(line[u], "x")) {//read the xscale value
						scale[0] = lastReadDouble;
					} else if (this.readDouble(line[u], "y")) {//read the yscale value
						scale[1] = lastReadDouble;
					} else if (this.readDouble(line[u], "z")) {//read the zscale value
						scale[2] = lastReadDouble;
					}
				}
			} else if (line[0].equalsIgnoreCase("translate")) {//read the translation values
				for (int u = 1; u < line.length; u++) {
					if (this.readDouble(line[u], "x")) {//read the xtranslation value
						translate[0] = lastReadDouble;
					} else if (this.readDouble(line[u], "y")) {//read the ytranslation value
						translate[1] = lastReadDouble;
					} else if (this.readDouble(line[u], "z")) {//read the ztranslation value
						translate[2] = lastReadDouble;
					}
				}
			} else if (line[0].equalsIgnoreCase("rotate")) {//read the rotation values
				for (int u = 1; u < line.length; u++) {
					if (this.readDouble(line[u], "deg")) {//read the rotation angle(degrees)
						rotateAngle = Math.toRadians(lastReadDouble);
					} else if (this.readDouble(line[u], "rad")) {//read the rotation angle(radius)
						rotateAngle = lastReadDouble;
					} else if (this.readVector(line[u], "axis")) {//read the rotation axis
						rotateAxis = lastReadVector;
					}
				}
			}
		}
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).setPosition(this.checkParticlePosition(particles.get(i).getPosition()));
		}
	}
	//read the interactions
	private void readInteractions() throws ParseException {
		interactions = new CB_Relations(CB_Instance.INTERACTIONS);
		for (int i = 0; i < inputLines.length; i++) {
			if (inputLines[i].length() > 11 && inputLines[i].substring(0, 11).equalsIgnoreCase("interaction")) {
				int type = -1;
				String[] line = inputLines[i].split(" ");
				//read the interaction type
				if (line.length <= 1) {
				} else if (line[1].equalsIgnoreCase("gravity")) {
					type = CB_Interaction.GRAVITATIONAL;
				} else if (line[1].equalsIgnoreCase("coulomb")) {
					type = CB_Interaction.COULOMB;
				} else if (line[1].equalsIgnoreCase("lennardjones")) {
					type = CB_Interaction.LENNARD_JONES;
				} else if (line[1].equalsIgnoreCase("morse")) {
					type = CB_Interaction.MORSE;
				} else if (line[1].equalsIgnoreCase("rydberg")) {
					type = CB_Interaction.RYDBERG;
				} else if (line[1].equalsIgnoreCase("harmonic")) {
					type = CB_Interaction.HARMONIC_STRETCH;
				} else if (line[1].equalsIgnoreCase("bending")) {
					type = CB_Interaction.HARMONIC_BENDING;
				} else if (line[1].equalsIgnoreCase("torsional")) {
					type = CB_Interaction.PERIODIC_TORSIONAL;
				}
				if (type == -1) {
					throw new ParseException("No valid interaction type found at line " + (i + 1), i + 1);
				}
				int[] particleIds = this.readParticleIds(inputLines[i]);
				String name = CB_Interaction.getTypeString(type) + " " + (interactions.size() + 1);
				if (this.readNameString(inputLines[i])) {
					name = lastReadString;
				}
				CB_Interaction interaction = new CB_Interaction(particleIds, type, name);
				boolean force = false, forceCos = false, equilibrium = false, exponential = false, a1 = false;
				boolean a2 = false, a3 = false, degree = false, period = false;
				for (int u = 2; u < line.length; u++) {
					if (this.readDouble(line[u], "f")) {//read the interaction-force(normal)
						interaction.setForce(lastReadDouble);
						force = true;
					} else if (this.readDouble(line[u], "fcos")) {//read the interaction-force(fcos)
						interaction.setForce(lastReadDouble);
						forceCos = true;
					} else if (this.readDouble(line[u], "r0")) {//read the equilibrium distance
						interaction.setEquilibrium(lastReadDouble);
						equilibrium = true;
					} else if (this.readDouble(line[u], "exp")) {//read the exponentialParameter
						interaction.setExponential(lastReadDouble);
						exponential = true;
					} else if (this.readDouble(line[u], "a1")) {//read the interaction-a1
						interaction.setA1(lastReadDouble);
						a1 = true;
					} else if (this.readDouble(line[u], "a2")) {//read the interaction-a2
						interaction.setA2(lastReadDouble);
						a2 = true;
					} else if (this.readDouble(line[u], "a3")) {//read the interaction-a3
						interaction.setA3(lastReadDouble);
						a3 = true;
					} else if (this.readDouble(line[u], "n")) {//read the interaction-period
						interaction.setPeriod(lastReadDouble);
						period = true;
					} else if (this.readDouble(line[u], "deg")) {//read the interaction-angle(degree)
						interaction.setDegree(lastReadDouble);
						degree = true;
					} else if (this.readDouble(line[u], "rad")) {//read the interaction-angle(radius)
						interaction.setDegree(Math.toDegrees(lastReadDouble));
						degree = true;
					}
				}
				//convert fcos to f
				if (forceCos) {
					interaction.setForce(Math.sin(Math.toRadians(interaction.getDegree())) * Math.sin(Math.toRadians(interaction.getDegree())) * interaction.getForce());
				}
				//check for invalid input
				switch (type) {
					case CB_Interaction.GRAVITATIONAL:
						if ((!force && !forceCos)) {
							throw new ParseException("Missing interaction parameters at line " + (i + 1), i + 1);
						}
					break;
					case CB_Interaction.COULOMB:
						if ((!force && !forceCos)) {
							throw new ParseException("Missing interaction parameters at line " + (i + 1), i + 1);
						}
					break;
					case CB_Interaction.LENNARD_JONES:
						if ((!force && !forceCos) || !equilibrium) {
							throw new ParseException("Missing interaction parameters at line " + (i + 1), i + 1);
						}
					break;
					case CB_Interaction.MORSE:
						if ((!force && !forceCos) || !equilibrium || !exponential) {
							throw new ParseException("Missing interaction parameters at line " + (i + 1), i + 1);
						}
					break;
					case CB_Interaction.RYDBERG:
						if ((!force && !forceCos) || !equilibrium || !exponential || !a1 || !a2 || !a3) {
							throw new ParseException("Missing interaction parameters at line " + (i + 1), i + 1);
						}
					break;
					case CB_Interaction.HARMONIC_STRETCH:
						if ((!force && !forceCos) || !equilibrium) {
							throw new ParseException("Missing interaction parameters at line " + (i + 1), i + 1);
						}
					break;
					case CB_Interaction.HARMONIC_BENDING:
						if ((!force && !forceCos) || !degree) {
							throw new ParseException("Missing interaction parameters at line " + (i + 1), i + 1);
						}
					break;
					case CB_Interaction.PERIODIC_TORSIONAL:
						if ((!force && !forceCos) || !degree || !period) {
							throw new ParseException("Missing interaction parameters at line " + (i + 1), i + 1);
						}
					break;
				}
				interactions.add(interaction);
			}
		}
	}
	//read the constraints
	private void readConstraints() throws ParseException {
		constraints = new CB_Relations(CB_Instance.CONSTRAINTS);
		for (int i = 0; i < inputLines.length; i++) {
			if (inputLines[i].length() > 9 && inputLines[i].substring(0, 9).equalsIgnoreCase("constrain")) {
				int type = -1;
				String[] line = inputLines[i].split(" ");
				//get selected constraint type
				if (line.length <= 1) {
				} else if (line[1].equalsIgnoreCase("distance")) {
					type = CB_Constraint.DISTANCE;
				}
				if (type == -1) {
					throw new ParseException("No valid constraint type found at line " + (i + 1), i + 1);
				}
				int[] particleIds = this.readParticleIds(inputLines[i]);
				String name = CB_Constraint.getTypeString(type) + " " + (constraints.size() + 1);
				if (this.readNameString(inputLines[i])) {
					name = lastReadString;
				}
				CB_Constraint constraint = new CB_Constraint(particleIds, type, name);
				boolean distance = false, maxstep = false, error = false;
				for (int u = 2; u < line.length; u++) {
					if (this.readDouble(line[u], "r")) {
						constraint.enableDistance(true);
						constraint.setDistance(lastReadDouble);
						distance = true;
					} else if (this.readInt(line[u], "maxstep")) {//read the maxstap
						constraint.enableMaxstep(true);
						constraint.setMaxstep(lastReadInt);
						maxstep = true;
					} else if (this.readDouble(line[u], "error")) {//read the error parameter
						constraint.enableError(true);
						constraint.setError(lastReadDouble);
						error = true;
					}
				}
				switch (type) {
					case CB_Constraint.DISTANCE:
					break;
				}
				constraints.add(constraint);
			}
		}
	}
	//function for checking the particle position
	private double[] checkParticlePosition(double[] position) {
		position[CB_Particle.X] = position[CB_Particle.X] % box.getDimension(CB_Box.WIDTH);
		if (position[CB_Particle.X] > box.getDimension(CB_Box.WIDTH) / 2) {
			position[CB_Particle.X] = position[CB_Particle.X] - box.getDimension(CB_Box.WIDTH);
		} else if (position[CB_Particle.X] < - box.getDimension(CB_Box.WIDTH) / 2) {
			position[CB_Particle.X] = position[CB_Particle.X] + box.getDimension(CB_Box.WIDTH);
		}
		position[CB_Particle.Y] = position[CB_Particle.Y] % box.getDimension(CB_Box.HEIGHT);
		if (position[CB_Particle.Y] > box.getDimension(CB_Box.HEIGHT) / 2) {
			position[CB_Particle.Y] = position[CB_Particle.Y] - box.getDimension(CB_Box.HEIGHT);
		} else if (position[CB_Particle.Y] < - box.getDimension(CB_Box.HEIGHT) / 2) {
			position[CB_Particle.Y] = position[CB_Particle.Y] + box.getDimension(CB_Box.HEIGHT);
		}
		position[CB_Particle.Z] = position[CB_Particle.Z] % box.getDimension(CB_Box.DEPTH);
		if (position[CB_Particle.Z] > box.getDimension(CB_Box.DEPTH) / 2) {
			position[CB_Particle.Z] = position[CB_Particle.Z] - box.getDimension(CB_Box.DEPTH);
		} else if (position[CB_Particle.Z] < - box.getDimension(CB_Box.DEPTH) / 2) {
			position[CB_Particle.Z] = position[CB_Particle.Z] + box.getDimension(CB_Box.DEPTH);
		}
		return position;
	}
	//function for reading the integer values
	private boolean readInt(String input, String fieldName) {
		if (input.length() >= fieldName.length() + 2 && input.substring(0, fieldName.length() + 1).equalsIgnoreCase(fieldName + "=")) {
			try {
				lastReadInt = Integer.parseInt(input.substring(fieldName.length() + 1, input.length()));
				return true;
			} catch (NumberFormatException n) {
				return false;
			}
		}
		return false;
	}
	//function for reading the double values
	private boolean readDouble(String input, String fieldName) {
		if (input.length() >= fieldName.length() + 1 && input.substring(0, fieldName.length() + 1).equalsIgnoreCase(fieldName + "=")) {
			try {
				lastReadDouble = Double.parseDouble(input.substring(fieldName.length() + 1, input.length()));
				return true;
			} catch (NumberFormatException n) {
				return false;
			}
		}
		return false;
	}
	//function for reading the vectors
	private boolean readVector(String input, String fieldName) {
		if (input.length() >= fieldName.length() + 2 && input.substring(0, fieldName.length() + 2).equalsIgnoreCase(fieldName + "=[")) {
			String[] doublesString = input.substring(fieldName.length() + 2, input.length() - 1).split(",");
			if (doublesString.length == 3) {
				double[] doubles = new double[doublesString.length];
				for (int i = 0; i < doublesString.length; i++) {
					try {
						doubles[i] = Double.parseDouble(doublesString[i]);
					} catch (NumberFormatException n) {
						return false;
					}
				}
				lastReadVector = doubles;
				return true;
			}
		}
		return false;
	}
	//function for checking the string values
	private boolean readNameString(String input) {
		Matcher matcher = namePattern.matcher(input);
		if (matcher.find()) {
			lastReadString = matcher.group(1);
			return true;
		}
		return false;
	}
	//function for checking the particle id's
	private int[] readParticleIds(String input) {
		Matcher matcher = particlesPattern.matcher(input);
		if (matcher.find()) {
			String[] idString = matcher.group(1).split(" ");
			ArrayList<Integer> ids = new ArrayList<Integer>(idString.length);
			for (int i = 0; i < idString.length; i++) {
				if (!idString[i].equalsIgnoreCase("to")) {
					try {
						ids.add(Integer.parseInt(idString[i]));
					} catch (NumberFormatException n) {
						return new int[] {};
					}
				} else {
					try {
						int startTo = ids.get(ids.size() - 1);
						int endTo = Integer.parseInt(idString[i + 1]);
						if (startTo == endTo) {
							return new int[] {};
						} else if (startTo < endTo) {
							for (int j = startTo + 1; j < endTo; j++) {
								if (ids.indexOf(j) != -1) {
									ids.add(j);
								}
							}
						} else if (startTo > endTo) {
							for (int j = endTo + 1; j < startTo; j++) {
								if (ids.indexOf(j) != -1) {
									ids.add(j);
								}
							}
						}
					} catch (NumberFormatException n) {
						return new int[] {};
					}
				}
			}
			int[] idsArray = new int[ids.size()];
			for (int i = 0; i < ids.size(); i++) {
				idsArray[i] = particleTranslate.indexOf(ids.get(i));
			}
			return idsArray;
		} else {
			matcher = allParticlesPattern.matcher(input);
			if (matcher.matches()) {
				int[] ids = new int[particles.size()];
				for (int i = 0; i < particles.size(); i++) {
					ids[i] = i;
				}
				return ids;
			}
		}
		return new int[] {};
	}
	//split all data into an array counts the number of colors and particles
	private String[] processInput(String input) {
		String[] lines = input.split("\n");
		for (int i = 0; i < lines.length; i++) {
			// Reduce all whitespace to a single space
			lines[i] = lines[i].replaceAll("[\\s]+", " ");
			lines[i] = this.stripComments(lines[i]);
			lines[i] = lines[i].trim();
		}
		return lines;
	}
	//strips all the comments
	private String stripComments(String line) {
		// Check for comments
		if (line.contains("#")) {
			char[] charArray = line.toCharArray();
			boolean name = false;
			// Find the first occurance of #
			for (int i = 0; i < charArray.length; i++) {
				if (charArray[i] == '#') {
					if (name || (i + 4 < charArray.length && !line.substring(i, i + 4).equals("#!CB"))) {
						// Strip comments
						return line.substring(0, i);
					} else {
						name = true;
					}
				}
			}
		}
		return line;
	}
	//rotate the position of the particles.
	private double[] rotatePosition(double[] pos, double[] rotation, double angle) {
		double sRX = rotation[0] * rotation[0];
		double sRY = rotation[1] * rotation[1];
		double sRZ = rotation[2] * rotation[2];
		double n = (sRX + sRY + sRZ);
		if (n > 0.0) {
			double rootN = Math.sqrt(n);
			double dot = this.vectorDot(pos, rotation);
			double cos = Math.cos(angle);
			double sin = Math.sin(angle);
			double[] newpos = new double[3];
			newpos[0] = rotation[0]*dot+cos*(pos[0]*(sRY+sRZ)-rotation[0]*(rotation[1]*pos[1]+rotation[2]*pos[2]))+sin*rootN*(pos[2]*rotation[1]-rotation[2]*pos[1]);
			newpos[1] = rotation[1]*dot+cos*(pos[1]*(sRX+sRZ)-rotation[1]*(rotation[0]*pos[0]+rotation[2]*pos[2]))+sin*rootN*(pos[0]*rotation[2]-rotation[0]*pos[2]);
			newpos[2] = rotation[2]*dot+cos*(pos[2]*(sRX+sRY)-rotation[2]*(rotation[0]*pos[0]+rotation[1]*pos[1]))+sin*rootN*(pos[1]*rotation[0]-rotation[1]*pos[0]);
			pos = vectorDivide(newpos, n);
		}
		return pos;
	}
	private double vectorDot(double[] vectorA, double[] vectorB) {
		return vectorA[0] * vectorB[0] + vectorA[1] * vectorB[1] + vectorA[2] * vectorB[2];
	}
	// Function that devides the vector
	private double[] vectorDivide(double[] vector, double value) {
		vector[0] = vector[0] / value;
		vector[1] = vector[1] / value;
		vector[2] = vector[2] / value;
		return vector;
	}
	// Converts the spherical to a cartesian coordinate
	private double[] sphericalToCartesian(double[] spherical) {
		double[] cartesian = new double[3];
		cartesian[0] = spherical[0] * Math.sin(spherical[1]) * Math.cos(spherical[2]);
		cartesian[1] = spherical[0] * Math.sin(spherical[1]) * Math.sin(spherical[2]);
		cartesian[2] = spherical[0] * Math.cos(spherical[1]);
		return cartesian;
	}
}