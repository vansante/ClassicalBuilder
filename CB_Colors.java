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
import java.awt.*;
import javax.swing.Icon;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class CB_Colors extends ArrayList<CB_Color> implements Transferable {

	private static final DataFlavor dataFlavor = new DataFlavor(CB_Colors.class, "ClassicalBuilder Colors");

	public static final int ICON_SIZE = 14;

	// Constructor
	public CB_Colors(boolean addDefaults) {
		super(16);

		if (addDefaults) {
			this.add(new CB_Color("Aqua", 0.0f, 1.0f, 1.0f));
			this.add(new CB_Color("Black", 0.0f, 0.0f, 0.0f));
			this.add(new CB_Color("Blue", 0.0f, 0.0f, 1.0f));
			this.add(new CB_Color("Fuschia", 1.0f, 0.0f, 1.0f));
			this.add(new CB_Color("Grey", 0.5f, 0.5f, 0.5f));
			this.add(new CB_Color("Green", 0.0f, 0.5f, 0.0f));
			this.add(new CB_Color("Lime", 0.0f, 1.0f, 0.0f));
			this.add(new CB_Color("Maroon", 0.5f, 0.0f, 0.0f));
			this.add(new CB_Color("Navy", 0.0f, 0.0f, 0.5f));
			this.add(new CB_Color("Olive", 0.5f, 0.5f, 0.0f));
			this.add(new CB_Color("Purple", 0.5f, 0.0f, 0.5f));
			this.add(new CB_Color("Red", 1.0f, 0.0f, 0.0f));
			this.add(new CB_Color("Silver", 0.75f, 0.75f, 0.75f));
			this.add(new CB_Color("Teal", 0.0f, 0.5f, 0.5f));
			this.add(new CB_Color("White", 1.0f, 1.0f, 1.0f));
			this.add(new CB_Color("Yellow", 1.0f, 1.0f, 0.0f));
		}
	}
	// Clones the array and returns the clone
	public CB_Colors cloneColors() {
		CB_Colors colorsCopy = new CB_Colors(false);
		for (int i = 0; i < this.size(); i++) {
			colorsCopy.add(this.get(i).clone());
		}
		return colorsCopy;
	}
	// Replaces the current array with the given one
	public void setColors(CB_Colors newColors) {
		this.clear();
		this.ensureCapacity(newColors.size());
		this.addAll(newColors);
	}
	// Returns a string with the number of colors
	public String toString() {
		return "Colors";
	}
	public Icon getColorIcon(final int id) {
		return new Icon() {
			public int getIconHeight() {
				return ICON_SIZE;
			}
			public int getIconWidth() {
				return ICON_SIZE;
			}
			public void paintIcon(Component c, Graphics g, int x, int y) {
				if (c.isEnabled()) {
					Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.translate(x, y);
					g2d.setColor(get(id).getTransparentColor());
					g2d.fillOval(1, 1, ICON_SIZE - 2, ICON_SIZE - 2);
					g2d.setColor(get(id).getColor());
					g2d.fillOval(1, 1, ICON_SIZE - 3, ICON_SIZE - 3);
					g2d.setColor(Color.white);
					g2d.fillOval(4, 3, 2, 2);
					//Restore graphics object
					g2d.translate(-x, -y);
				}
			}
		};
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
