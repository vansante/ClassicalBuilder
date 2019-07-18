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

// Class that stores an array of particles and has functions to add, remove and clone them
public class CB_Particles extends ArrayList<CB_Particle> implements Transferable {

	private static final DataFlavor dataFlavor = new DataFlavor(CB_Particles.class, "ClassicalBuilder Particles");

	public CB_Particles() {
		super(30);
	}
	public CB_Particles(int size) {
		super(size);
	}
	public CB_Particles clone() {
		CB_Particles thisCopy = new CB_Particles(this.size());
		for (int i = 0; i < this.size(); i++) {
			thisCopy.add(this.get(i).clone());
		}
		return thisCopy;
	}
	// Replaces the current array with the given one
	public void setParticles(CB_Particles newParticles) {
		this.clear();
		this.ensureCapacity(newParticles.size());
		this.addAll(newParticles);
	}
	// Returns a string with the number of this
	public String toString() {
		return "Particles";
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
	// Override a few not allowed methods
	public boolean add(int index, Object element) { return false; }
	public boolean addAll(int index, Collection c) { return false; }
}