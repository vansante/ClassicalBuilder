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

import java.util.ArrayList;
import java.util.Collection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class CB_Relations extends ArrayList<CB_Relation> implements Transferable {

	private final int type;

	private static final DataFlavor interactionFlavor = new DataFlavor(CB_Interaction.class, "ClassicalBuilder Interaction");
	private static final DataFlavor constraintFlavor = new DataFlavor(CB_Constraint.class, "ClassicalBuilder Constraint");

	public static final String[] NAMES = { "", "Interactions", "Constraints" };
	public static final String[] NAMES_SINGLE = { "", "Interaction", "Constraint" };

	public CB_Relations(int type) {
		super(20);
		this.type = type;
	}
	public CB_Relations(int type, int size) {
		super(size);
		this.type = type;
	}
	// Checks if a removed particle exists in any of the stored relations
	// If one is found, the particle array of the relation is edited to remove the particle
	public void removeRelationsParticles(int particleId) {
		for (int i = 0; i < this.size(); i++) {
			int[] temp = this.get(i).cloneParticles();
			int result = CB_Tools.arraySearch(temp, particleId);
			if (result >= 0) {
				temp = CB_Tools.arrayRemove(temp, result);
			}
			for (int j = 0; j < temp.length; j++) {
				if (temp[j] > particleId) {
					temp[j]--;
				}
			}
			this.get(i).setParticles(temp);
		}
	}
	public CB_Relations clone(boolean stripParticles) {
		CB_Relations relationsCopy = new CB_Relations(type);
		for (int i = 0; i < this.size(); i++) {
			relationsCopy.add(this.get(i).clone(stripParticles));
		}
		return relationsCopy;
	}
	// Replaces the array of relations with the one supplied
	public void setRelations(CB_Relations newRelations) {
		this.clear();
		this.ensureCapacity(newRelations.size());
		this.addAll(newRelations);
	}
	// Methods that enable data transferring
	public Object getTransferData(DataFlavor flavor) {
		return this.clone(true);
	}
	public DataFlavor[] getTransferDataFlavors() {
		if (type == CB_Instance.INTERACTIONS) {
			return new DataFlavor[] {interactionFlavor};
		} else if (type == CB_Instance.CONSTRAINTS) {
			return new DataFlavor[] {constraintFlavor};
		}
		return new DataFlavor[] {};
	}
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (type == CB_Instance.INTERACTIONS && flavor.equals(interactionFlavor)) {
			return true;
		} else if (type == CB_Instance.CONSTRAINTS && flavor.equals(constraintFlavor)) {
			return true;
		}
		return false;
	}
	public static DataFlavor getDataFlavor(int type) {
		if (type == CB_Instance.INTERACTIONS) {
			return interactionFlavor;
		} else if (type == CB_Instance.CONSTRAINTS) {
			return constraintFlavor;
		}
		return null;
	}
	public static String getTypeString(int type) {
		return NAMES[type];
	}
	public static String getTypeStringSingle(int type) {
		return NAMES_SINGLE[type];
	}
	// Override a few not allowed methods
	public boolean add(int index, Object element) { return false; }
	public boolean addAll(int index, Collection c) { return false; }
}