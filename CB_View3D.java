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
import java.awt.event.*;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Box;

public class CB_View3D extends Canvas3D implements MouseListener, MouseMotionListener {

	private final CB_Main main;

	private final SimpleUniverse universe;
	private final Locale locale;
	private final PickCanvas pickCanvas;

	private final BranchGroup rootGroup, lightGroup, labelGroup, boxGroup, particleGroup;
	private BranchGroup relationGroup;
	private final TransformGroup transformGroup;

	private final MouseRotate mouseRotate;
	private final MouseZoom mouseZoom;
	private final MouseWheelZoom mouseWheelZoom;

	private Shape3D[] boxLines = new Shape3D[12];
	private final ArrayList<Particle> particles;

	private final Appearance relationAppearance;
	private int rotation, currentX, currentY;
	private boolean dragStarted = false;

	private static final LineAttributes lineAttributes = new LineAttributes(1.0f, LineAttributes.PATTERN_SOLID, true);

	private static final double ROT_FACTOR = 0.005;

	public static final int ROT_NORMAL = 0;
	public static final int ROT_LOCK_X = 1;
	public static final int ROT_LOCK_Y = 2;
	public static final int ROT_SEMILOCK = 3;
	public static final int ROT_LOCK = 4;

	public CB_View3D(CB_Main cmain) {
		super(SimpleUniverse.getPreferredConfiguration());
		this.main = cmain;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		universe = new SimpleUniverse(this);

		// Java3D Crash handling, comment to achieve lower java3D version compatibility
		/*
		universe.addRenderingErrorListener(
			new RenderingErrorListener() {
				public void errorOccurred(RenderingError e) {
					System.out.println("View3D: View3D has just crashed!");
					JOptionPane.showMessageDialog(main, "There was an error with the 3D renderer.\n3D View will be disabled until the next start of the program.", "View3D Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		);
		*/

		rootGroup = new BranchGroup();

		transformGroup = new TransformGroup();
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		transformGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		transformGroup.setCapability(Group.ALLOW_CHILDREN_READ);
		transformGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);

		//create the rotate function
		mouseRotate = new MouseRotate();
		mouseRotate.setFactor(ROT_FACTOR);
		mouseRotate.setTransformGroup(transformGroup);
		mouseRotate.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), main.getInstance().getMaxBoxSize() * 2));

		//create the zoom function
		mouseZoom = new MouseZoom();
		mouseZoom.setTransformGroup(transformGroup);
		mouseZoom.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), main.getInstance().getMaxBoxSize() * 2));

		//create the zoom function
		mouseWheelZoom = new MouseWheelZoom();
		mouseWheelZoom.setTransformGroup(transformGroup);
		mouseWheelZoom.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), main.getInstance().getMaxBoxSize() * 2));

		lightGroup = new BranchGroup();
		labelGroup = new BranchGroup();
		boxGroup = new BranchGroup();

		particleGroup = new BranchGroup();
		particleGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		particleGroup.setCapability(Group.ALLOW_CHILDREN_READ);
		particleGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		particleGroup.setCapability(BranchGroup.ALLOW_DETACH);
		transformGroup.addChild(particleGroup);

		pickCanvas = new PickCanvas(this, particleGroup);
		pickCanvas.setMode(PickCanvas.BOUNDS);

		relationGroup = new BranchGroup();
		relationGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		relationGroup.setCapability(Group.ALLOW_CHILDREN_READ);
		relationGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		relationGroup.setCapability(BranchGroup.ALLOW_DETACH);

		rootGroup.addChild(transformGroup);
		rootGroup.addChild(mouseRotate);
		rootGroup.addChild(mouseZoom);
		rootGroup.addChild(mouseWheelZoom);

		universe.getViewingPlatform().setNominalViewingTransform();
		universe.getViewer().getView().setBackClipDistance(1000000.0);
		universe.getViewer().getView().setFrontClipDistance(0.01);
		universe.getViewer().getView().setDepthBufferFreezeTransparent(false);

		locale = new Locale(universe);
		locale.addBranchGraph(rootGroup);

		this.createBox();
		this.createLights();

		particles = new ArrayList<Particle>();

		relationAppearance = new Appearance();
		Material material = new Material();
		material.setDiffuseColor(new Color3f(Color.white));
		material.setAmbientColor(new Color3f(Color.white));
		material.setShininess(10.0f);
		relationAppearance.setMaterial(material);
		relationAppearance.setLineAttributes(lineAttributes);

		this.zoomReset();
		this.setRotation(ROT_SEMILOCK);
	}
	public void zoomIn() {
		this.setScale(this.getScale() + (this.getScale() / 8));
	}
	public void zoomOut() {
		this.setScale(this.getScale() - (this.getScale() / 8));
	}
	//set the zoom, translation and rotation at their original values
	public void zoomReset() {
		double scale = 1.32 / main.getInstance().getMaxBoxSize();
		Transform3D transform = new Transform3D();
		transform.setScale(scale);
		transformGroup.setTransform(transform);
	}
	public void setTransform3D(float[] transform) {
		Transform3D transform3D = new Transform3D(transform);
		transformGroup.setTransform(transform3D);
	}
	public float[] getTransform3D() {
		float[] transform = new float[16];
		Transform3D transform3D = new Transform3D();
		transformGroup.getTransform(transform3D);
		transform3D.get(transform);
		return transform;
	}
	//update the particle properties
	public void updateParticles() {
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).update();
		}
	}
	public void updateParticle(int id) {
		particles.get(id).update();
	}
	public void resetParticles() {
		particleGroup.removeAllChildren();
		particles.clear();
		for (int i = 0; i < main.getParticles().size(); i++) {
			Particle particle = new Particle(i);
			particles.add(particle);
			particleGroup.addChild(particle);
		}
	}
	//update the selected particles
	public void updateSelection() {
		if (relationGroup.getParent() != null) {
			transformGroup.removeChild(relationGroup);
		}
		this.updateParticles();
		if (main.getInstance().getSelectionType() != CB_Instance.PARTICLES) {
			this.showRelations(main.getInstance().getSelectionType());
		}
	}
	// When a relation is selected, show the relation
	public void showRelations(int type) {
		relationGroup = new BranchGroup();
		relationGroup.setCapability(BranchGroup.ALLOW_DETACH);
		relationGroup.removeAllChildren();
		int count = 0;
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			count += main.getRelation(type, main.getInstance().getSelection(i)).getParticlesSize();
		}
		Shape3D[] relationLines = new Shape3D[count];
		count = 0;
		for (int i = 0; i < main.getInstance().getSelectionSize(); i++) {
			if (main.getRelation(type, main.getInstance().getSelection(i)).hasRelationCenter()) {
				double[] interactionCenter = main.getInstance().getParticlesCenter(main.getRelation(type, main.getInstance().getSelection(i)).getParticles());
				for (int u = 0; u < main.getRelation(type, main.getInstance().getSelection(i)).getParticlesSize(); u++) {
					relationLines[count] = new Shape3D(
						this.createLine(
							main.getParticle(main.getRelation(type, main.getInstance().getSelection(i)).getParticle(u)).getPosition(),
							interactionCenter,
							new Color3f(1.0f, 1.0f, 1.0f)
						)
					);
					relationLines[count].setAppearance(relationAppearance);
					relationGroup.addChild(relationLines[count]);
					count++;
				}
				if (main.getRelation(type, main.getInstance().getSelection(i)).getParticlesSize() > 1) {
					// Add sphere in the relation center
					TransformGroup transformGroupSphere = new TransformGroup();
					transformGroupSphere.addChild(
						new Sphere(
							(float) main.getInstance().getMaxBoxSize() / 100,
							Sphere.GENERATE_NORMALS,
							10,
							relationAppearance
						)
					);
					Transform3D transform3DSphere = new Transform3D();
					transform3DSphere.setTranslation(new Vector3d(interactionCenter));
					transformGroupSphere.setTransform(transform3DSphere);
					relationGroup.addChild(transformGroupSphere);
				}
			} else {
				for (int j = 0; j < main.getRelation(type, main.getInstance().getSelection(i)).getParticlesSize() - 1; j++) {
					relationLines[count] = new Shape3D(
						this.createLine(
							main.getParticle(main.getRelation(type, main.getInstance().getSelection(i)).getParticle(j)).getPosition(),
							main.getParticle(main.getRelation(type, main.getInstance().getSelection(i)).getParticle(j + 1)).getPosition(),
							new Color3f(1.0f, 1.0f, 1.0f)
						)
					);
					relationLines[count].setAppearance(relationAppearance);
					relationGroup.addChild(relationLines[count]);
					count++;
				}
			}
		}
		relationGroup.compile();
		transformGroup.addChild(relationGroup);
	}
	//function which creates the light effects on the spheres
	public void createLights() {
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100000000.0f);
		DirectionalLight directionalLight = new DirectionalLight(new Color3f(Color.white), new Vector3f(-1.0f, -1.0f, 0.0f));
		directionalLight.setInfluencingBounds(bounds);
		directionalLight.setEnable(true);
		lightGroup.addChild(directionalLight);
		AmbientLight ambientLight = new AmbientLight(true, new Color3f(0.25f, 0.25f, 0.25f));
		ambientLight.setInfluencingBounds(bounds);
		lightGroup.addChild(ambientLight);
		lightGroup.compile();
		transformGroup.addChild(lightGroup);
	}
	//update the properties of the box
	public void updateBox() {
		LineArray[] boxLineArray = this.getBoxLines();
		for (int i = 0; i < boxLines.length; i++) {
			boxLines[i].setGeometry(boxLineArray[i]);
		}
		mouseRotate.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), main.getInstance().getMaxBoxSize() * 2));
		mouseWheelZoom.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), main.getInstance().getMaxBoxSize() * 2));
		mouseZoom.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), main.getInstance().getMaxBoxSize() * 2));
	}
	//function returns the coordinates of several lines
	public LineArray[] getBoxLines() {
		double left = - main.getInstance().getBox().getDimension(CB_Box.WIDTH) / 2;
		double right = main.getInstance().getBox().getDimension(CB_Box.WIDTH) / 2;
		double top = main.getInstance().getBox().getDimension(CB_Box.HEIGHT) / 2;
		double bottom = - main.getInstance().getBox().getDimension(CB_Box.HEIGHT) / 2;
		double front = main.getInstance().getBox().getDimension(CB_Box.DEPTH) / 2;
		double back = - main.getInstance().getBox().getDimension(CB_Box.DEPTH) / 2;

		LineArray[] lineCoordinates = new LineArray[12];
		for (int i = 0; i < lineCoordinates.length; i++) {
			lineCoordinates[i] = new LineArray(2, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		}
		lineCoordinates[0].setCoordinate(0, new double[] {left, top, front});
		lineCoordinates[0].setCoordinate(1, new double[] {right, top, front});
		lineCoordinates[1].setCoordinate(0, new double[] {right, top, front});
		lineCoordinates[1].setCoordinate(1, new double[] {right, bottom, front});
		lineCoordinates[2].setCoordinate(0, new double[] {right, bottom, front});
		lineCoordinates[2].setCoordinate(1, new double[] {left, bottom, front});
		lineCoordinates[3].setCoordinate(0, new double[] {left, bottom, front});
		lineCoordinates[3].setCoordinate(1, new double[] {left, top, front});
		lineCoordinates[4].setCoordinate(0, new double[] {left, top, back});
		lineCoordinates[4].setCoordinate(1, new double[] {right, top, back});
		lineCoordinates[5].setCoordinate(0, new double[] {right, top, back});
		lineCoordinates[5].setCoordinate(1, new double[] {right, bottom, back});
		lineCoordinates[6].setCoordinate(0, new double[] {right, bottom, back});
		lineCoordinates[6].setCoordinate(1, new double[] {left, bottom, back});
		lineCoordinates[7].setCoordinate(0, new double[] {left, bottom, back});
		lineCoordinates[7].setCoordinate(1, new double[] {left, top, back});
		lineCoordinates[8].setCoordinate(0, new double[] {left, top, back});
		lineCoordinates[8].setCoordinate(1, new double[] {left, top, front});
		lineCoordinates[9].setCoordinate(0, new double[] {right, top, back});
		lineCoordinates[9].setCoordinate(1, new double[] {right, top, front});
		lineCoordinates[10].setCoordinate(0, new double[] {left, bottom, back});
		lineCoordinates[10].setCoordinate(1, new double[] {left, bottom, front});
		lineCoordinates[11].setCoordinate(0, new double[] {right, bottom, back});
		lineCoordinates[11].setCoordinate(1, new double[] {right, bottom, front});
		Color3f boxColor = new Color3f(0.1f, 0.9f, 0.0f);
		for (int i = 0; i < lineCoordinates.length; i++) {
			for(int u = 0; u < 2; u++) {
				lineCoordinates[i].setColor(u, boxColor);
			}
		}
		return lineCoordinates;
	}
	public void createBox() {
		// Create the BoxLines
		LineArray[] boxLineArray = this.getBoxLines();
		// Create Box appearance
		LineAttributes lineAttributes = new LineAttributes(1.0f, LineAttributes.PATTERN_SOLID, true);
		Appearance appearance = new Appearance();
		appearance.setLineAttributes(lineAttributes);
		// Set geometry
		for (int i = 0; i < boxLines.length; i++) {
			boxLines[i] = new Shape3D(boxLineArray[i]);
			boxLines[i].setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
			boxLines[i].setAppearance(appearance);
			boxGroup.addChild(boxLines[i]);
		}
		boxGroup.compile();
		transformGroup.addChild(boxGroup);
	}
	public LineArray createLine(double[] coordinate1, double[] coordinate2, Color3f color) {
		LineArray line = new LineArray(2, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		line.setCoordinate(0, coordinate1);
		line.setCoordinate(1, coordinate2);
		line.setColor(0, color);
		line.setColor(1, color);
		return line;
	}
	public Shape3D createLineShape(LineArray line, Appearance appearance) {
		Shape3D lineShape = new Shape3D(line);
		appearance.setLineAttributes(lineAttributes);
		lineShape.setAppearance(appearance);
		return lineShape;
	}
	public void setRotation(int rotation) {
		this.rotation = rotation;
		this.updateRotation();
	}
	public void updateRotation() {
		if (rotation == ROT_NORMAL || rotation == ROT_SEMILOCK) {
			mouseRotate.setFactor(ROT_FACTOR);
		} else if (rotation == ROT_LOCK_X) {
			mouseRotate.setFactor(0, ROT_FACTOR);
		} else if (rotation == ROT_LOCK_Y) {
			mouseRotate.setFactor(ROT_FACTOR, 0);
		} else if (rotation == ROT_LOCK) {
			mouseRotate.setFactor(0, 0);
		}
	}
	public void setScale(double scale) {
		Transform3D transform = new Transform3D();
		transformGroup.getTransform(transform);
		transform.setScale(scale);
		transformGroup.setTransform(transform);
	}
	public double getScale() {
		Transform3D transform = new Transform3D();
		transformGroup.getTransform(transform);
		return transform.getScale();
	}
	public CB_Instance getInstance() {
		return main.getInstance();
	}
	public void mouseClicked(MouseEvent e) {
		pickCanvas.setShapeLocation(e);
		PickResult result = pickCanvas.pickClosest();
		if (result == null) {
			main.getInstance().deselect();
		} else if (result.getNode(PickResult.PRIMITIVE) != null && result.getNode(PickResult.PRIMITIVE).getParent() != null && result.getNode(PickResult.PRIMITIVE).getParent().getParent() != null) {
			Particle particle = (Particle) result.getNode(PickResult.PRIMITIVE).getParent().getParent();
			if (particle != null) {
				for (int i = 0; i < particles.size(); i++) {
					if (particle.equals(particles.get(i))) {
						main.getInstance().pickParticle(i, e.isControlDown());
						return;
					}
				}
			}
		}
	}
	public void mousePressed(MouseEvent e) {
		if (rotation == ROT_SEMILOCK) {
			currentX = e.getX();
			currentY = e.getY();
		}
	}
	public void mouseReleased(MouseEvent e) {
		if (rotation == ROT_SEMILOCK) {
			this.updateRotation();
			dragStarted = false;
		}
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {
		if (rotation == ROT_SEMILOCK && !dragStarted) {
			if (currentX != e.getX()) {
				mouseRotate.setFactor(ROT_FACTOR, 0);
				dragStarted = true;
			} else if (currentY != e.getY()) {
				mouseRotate.setFactor(0, ROT_FACTOR);
				dragStarted = true;
			}
		}
	}
	public void mouseMoved(MouseEvent e) {}
	private class Particle extends BranchGroup {
		private int id;
		private TransformGroup particleTransform;
		private Sphere sphere;
		private Shape3D momentumLine;

		public Particle(int id) {
			super();
			this.id = id;

			this.setCapability(BranchGroup.ALLOW_DETACH);
			this.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
			this.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
			this.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

			particleTransform = new TransformGroup();
			particleTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			int sphereDetail = 30 - (getInstance().getParticles().size() / 20);
			if (sphereDetail < 8) {
				sphereDetail = 8;
			}
			sphere = new Sphere(1.0f, Sphere.GENERATE_NORMALS | Sphere.ENABLE_APPEARANCE_MODIFY, sphereDetail);
			momentumLine = createLineShape(createLine( new double[] {0, 0, 0}, new double[] {0, 0, 0}, new Color3f(1.0f, 1.0f, 1.0f)), new Appearance());
			momentumLine.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
			this.update();
			particleTransform.addChild(sphere);
			this.addChild(particleTransform);
			this.addChild(momentumLine);
			this.compile();
		}
		public void update() {
			Transform3D transform3D = new Transform3D();
			transform3D.setTranslation(new Vector3d(main.getParticle(id).getX(), main.getParticle(id).getY(), main.getParticle(id).getZ()));
			transform3D.setScale(main.getParticle(id).getRadius());
			particleTransform.setTransform(transform3D);
			this.setAppearance();
			if (main.getInstance().getSelectionType() == CB_Instance.PARTICLES && main.getInstance().getSelectionSize() == 1 && main.getInstance().getParticleSelected(id)) {
				momentumLine.setGeometry(
					createLine(
						main.getParticle(id).getPosition(),
						new double[] {
							main.getParticle(id).getX() + main.getParticle(id).getMomIndex(CB_Particle.X),
							main.getParticle(id).getY() + main.getParticle(id).getMomIndex(CB_Particle.Y),
							main.getParticle(id).getZ() + main.getParticle(id).getMomIndex(CB_Particle.Z)
						},
						new Color3f(1.0f, 1.0f, 1.0f)
					)
				);
			} else {
				momentumLine.setGeometry(
					new LineArray(2, GeometryArray.COORDINATES | GeometryArray.COLOR_3)
				);
			}
		}
		//set the appearance of the particles (color, light effects and material)
		public void setAppearance() {
			Appearance appearance = new Appearance();
			Material material = new Material();
			material.setColorTarget(Material.DIFFUSE);
			material.setShininess(120.0f);
			Color3f color = new Color3f(getInstance().getColor(main.getParticle(id).getColor()).getColorFloat());
			material.setSpecularColor(new Color3f(Color.white));
			material.setDiffuseColor(color);
			material.setAmbientColor(color);
			material.setLightingEnable(true);
			appearance.setMaterial(material);
			appearance.setColoringAttributes(
				new ColoringAttributes(
					new Color3f(color),
					ColoringAttributes.FASTEST
				)
			);
			if (!getInstance().getParticleSelected(id)) {
				appearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.5f));
			}
			sphere.setAppearance(appearance);
		}
	}
}