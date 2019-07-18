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

import java.awt.Color;

public class CB_Color {

	private String name; // User specified color name
	private float red; // Float with the amount of red
	private float green; // Float with the amount of green
	private float blue; // Float with the amount of blue

	// Constructors
	public CB_Color(String name, float red, float green, float blue) {
		this.name = name;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	public CB_Color(String name, Color color) {
		this.name = name;
		this.red = (float) color.getRed() / 255;
		this.green = (float) color.getGreen() / 255;
		this.blue = (float) color.getBlue() / 255;
	}
	public CB_Color() {
		this("White", 1.0f, 1.0f, 1.0f);
	}
	// Set methods
	public void setName(String name) {
		this.name = name;
	}
	public void setColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	public void setColor(float[] color) {
		this.red = color[0];
		this.green = color[1];
		this.blue = color[2];
	}
	public void setColor(Color color) {
		this.red = (float) color.getRed() / 255;
		this.green = (float) color.getGreen() / 255;
		this.blue = (float) color.getBlue() / 255;
	}
	public void setRed(float red) {
		this.red = red;
	}
	public void setGreen(float green) {
		this.green = green;
	}
	public void setBlue(float blue) {
		this.blue = blue;
	}
	// Get methods
	public String getName() {
		return name;
	}
	public Color getColor() {
		return new Color(red, green, blue);
	}
	public Color getTransparentColor() {
		return new Color(red * 0.5f, green * 0.5f, blue * 0.5f);
	}
	public float[] getColorFloat() {
		return new float[] {red, green, blue};
	}
	public float getRed() {
		return red;
	}
	public float getGreen() {
		return green;
	}
	public float getBlue() {
		return blue;
	}
	public float getBlackness() {
		return (red + green + blue) / 3.0f;
	}
	public CB_Color clone() {
		return new CB_Color(name, red, green, blue);
	}
	public String toString() {
		return name;
	}
}
