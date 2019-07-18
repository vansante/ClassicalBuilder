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

import java.awt.*;
import javax.swing.*;

//this class adds all the view into one screen.
public class CB_View extends JPanel {

	private final CB_Main main;
	private final CB_View2D viewFront, viewTop, viewLeft;
	private final CB_View3D view3D;
	private int mode = -1;

	private final CardLayout layout;
	private final JPanel panelFront, panelTop, panelLeft, panel3D, panel2D, panelAll;

	public static final int V_FRONT = 0;
	public static final int V_TOP = 1;
	public static final int V_LEFT = 2;
	public static final int V_3D = 3;
	public static final int V_2D = 4;
	public static final int V_ALL = 5;

	public final static String[] PANELS = { "Front", "Top", "Left", "3D", "2D", "All" };

	public CB_View(CB_Main main, int mode) {
		this.main = main;

		this.layout = new CardLayout();
		this.setLayout(layout);

		viewFront = new CB_View2D(main, CB_Particle.X, CB_Particle.Y, "Front");
		viewTop = new CB_View2D(main, CB_Particle.X, CB_Particle.Z, "Top");
		viewLeft = new CB_View2D(main, CB_Particle.Z, CB_Particle.Y, "Left");
		view3D = new CB_View3D(main);

		panelFront = new JPanel(new GridLayout(1, 1, 0, 0));
		panelFront.add(viewFront);
		panelTop = new JPanel(new GridLayout(1, 1, 0, 0));
		panelTop.add(viewTop);
		panelLeft = new JPanel(new GridLayout(1, 1, 0, 0));
		panelLeft.add(viewLeft);
		panel3D = new JPanel(new GridLayout(1, 1, 0, 0));
		panel3D.add(view3D);
		panel2D = new JPanel(new GridLayout(2, 2, 2, 2));
		panel2D.add(viewFront);
		panel2D.add(viewTop);
		panel2D.add(viewLeft);
		panelAll = new JPanel(new GridLayout(2, 2, 2, 2));
		panelAll.add(viewFront);
		panelAll.add(viewTop);
		panelAll.add(viewLeft);
		panelAll.add(view3D);

		this.add(panelFront, PANELS[V_FRONT]);
		this.add(panelTop, PANELS[V_TOP]);
		this.add(panelLeft, PANELS[V_LEFT]);
		this.add(panel3D, PANELS[V_3D]);
		this.add(panel2D, PANELS[V_2D]);
		this.add(panelAll, PANELS[V_ALL]);

		this.setViewMode(mode);
	}
	//function which sets the selected view mode
	public void setViewMode(int mode) {
		if (mode != this.mode) {
			this.mode = mode;
			switch (this.mode) {
				case V_FRONT:
					panelFront.add(viewFront);
				break;
				case V_TOP:
					panelTop.add(viewTop);
				break;
				case V_LEFT:
					panelLeft.add(viewLeft);
				break;
				case V_3D:
					panel3D.add(view3D);
				break;
				case V_2D:
					panel2D.add(viewFront);
					panel2D.add(viewTop);
					panel2D.add(viewLeft);
				break;
				case V_ALL:
					panelAll.add(viewFront);
					panelAll.add(viewTop);
					panelAll.add(viewLeft);
					panelAll.add(view3D);
				break;
			}
			layout.show(this, PANELS[mode]);
			viewFront.updateBox();
			viewTop.updateBox();
			viewLeft.updateBox();
		}
	}
	//update particle properties in all four views
	public void updateParticle(int id) {
		viewFront.update();
		viewTop.update();
		viewLeft.update();
		view3D.updateParticle(id);
	}
	//update particle properties in all four views
	public void updateParticles() {
		viewFront.update();
		viewTop.update();
		viewLeft.update();
		view3D.updateParticles();
	}
	//reset particle properties in all four views
	public void resetParticles() {
		viewFront.update();
		viewTop.update();
		viewLeft.update();
		view3D.resetParticles();
	}
	//update box properties in all four views
	public void updateBox() {
		viewFront.updateBox();
		viewTop.updateBox();
		viewLeft.updateBox();
		view3D.updateBox();
	}
	//update interactions and constraints in all four views
	public void updateSelection() {
		viewFront.update();
		viewTop.update();
		viewLeft.update();
		view3D.updateSelection();
	}
	//reset zoom in the different views
	public void zoomReset() {
		viewTop.resetView();
		viewTop.setOffset(0, 0);
		viewLeft.resetView();
		viewLeft.setOffset(0, 0);
		viewFront.resetView();
		viewFront.setOffset(0, 0);
		view3D.zoomReset();
		this.update2D();
	}
	public void zoomIn() {
		main.getInstance().setScale(main.getInstance().getScale() + (main.getInstance().getScale() / 8));
		this.update2D();
		view3D.zoomIn();
	}
	public void zoomOut() {
		main.getInstance().setScale(main.getInstance().getScale() - (main.getInstance().getScale() / 8));
		this.update2D();
		view3D.zoomOut();
	}
	//updates particles in 2dViews only
	public void update2D() {
		viewFront.update();
		viewTop.update();
		viewLeft.update();
	}
	public CB_View3D getView3D() {
		return view3D;
	}
}