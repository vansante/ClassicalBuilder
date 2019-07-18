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

import javax.swing.undo.AbstractUndoableEdit;

public abstract class CB_UndoableEdit extends AbstractUndoableEdit {
	protected CB_Box oldBox, newBox;
	protected CB_Colors oldColors, newColors;
	protected CB_Particle oldParticle, newParticle;
	protected CB_Particles oldParticles, newParticles;
	protected CB_Interaction oldInteraction, newInteraction;
	protected CB_Constraint oldConstraint, newConstraint;
	protected CB_Relations oldRelations, newRelations;
	protected CB_Relations oldInteractions, newInteractions, oldConstraints, newConstraints;
	protected int[] oldArray, newArray;
	protected double[] oldVector, newVector;

	private int id;
	private int[] ids;

	public CB_UndoableEdit(int id, int[] oldArray, int[] newArray) {
		super();
		this.id = id;
		this.oldArray = oldArray;
		this.newArray = newArray;
	}
	public CB_UndoableEdit(CB_Box oldBox, CB_Box newBox, CB_Particles oldParticles, CB_Particles newParticles) {
		super();
		this.oldBox = oldBox;
		this.newBox = newBox;
		this.oldParticles = oldParticles;
		this.newParticles = newParticles;
	}
	public CB_UndoableEdit(CB_Colors oldColors, CB_Colors newColors, CB_Particles oldParticles, CB_Particles newParticles) {
		super();
		this.oldColors = oldColors;
		this.newColors = newColors;
		this.oldParticles = oldParticles;
		this.newParticles = newParticles;
	}
	public CB_UndoableEdit(int id, double[] oldVector, double[] newVector) {
		super();
		this.id = id;
		this.oldVector = oldVector;
		this.newVector = newVector;
	}
	public CB_UndoableEdit(int id, CB_Particle oldParticle, CB_Particle newParticle) {
		super();
		this.id = id;
		this.oldParticle = oldParticle;
		this.newParticle = newParticle;
	}
	public CB_UndoableEdit(int[] ids, CB_Particles oldParticles, CB_Particles newParticles) {
		super();
		this.ids = ids;
		this.oldParticles = oldParticles;
		this.newParticles = newParticles;
	}
	public CB_UndoableEdit(int[] ids, CB_Particles oldParticles, CB_Particles newParticles,
				CB_Relations oldInteractions, CB_Relations newInteractions,
				CB_Relations oldConstraints, CB_Relations newConstraints) {
		super();
		this.ids = ids;
		this.oldParticles = oldParticles;
		this.newParticles = newParticles;
		this.oldInteractions = oldInteractions;
		this.newInteractions = newInteractions;
		this.oldConstraints = oldConstraints;
		this.newConstraints = newConstraints;
	}
	public CB_UndoableEdit(CB_Particles oldParticles, CB_Particles newParticles) {
		super();
		this.oldParticles = oldParticles;
		this.newParticles = newParticles;
	}
	public CB_UndoableEdit(int id, CB_Interaction oldInteraction, CB_Interaction newInteraction) {
		super();
		this.id = id;
		this.oldInteraction = oldInteraction;
		this.newInteraction = newInteraction;
	}
	public CB_UndoableEdit(int id, CB_Constraint oldConstraint, CB_Constraint newConstraint) {
		super();
		this.id = id;
		this.oldConstraint = oldConstraint;
		this.newConstraint = newConstraint;
	}
	public CB_UndoableEdit(int[] ids, CB_Relations oldRelations, CB_Relations newRelations) {
		super();
		this.ids = ids;
		this.oldRelations = oldRelations;
		this.newRelations = newRelations;
	}
	public CB_UndoableEdit(CB_Relations oldRelations, CB_Relations newRelations) {
		super();
		this.oldRelations = oldRelations;
		this.newRelations = newRelations;
	}
	public int getId() {
		return id;
	}
	public int[] getIds() {
		return ids;
	}
	public int getId(int index) {
		return ids[index];
	}
	public String getRedoPresentationName() {
		return "Redo " + getUndoRedoPresentationName();
	}
	public String getUndoPresentationName() {
		return "Undo " + getUndoRedoPresentationName();
	}
	public abstract String getUndoRedoPresentationName();
}