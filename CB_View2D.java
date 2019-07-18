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
import javax.swing.JPanel;
import javax.swing.JLabel;

//this class shows a 2D view and paints the 2d box, the particles, constraints and interactions
public class CB_View2D extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener {

	private final CB_Main main;
	private final int indexX, indexY;
	private final String label;

	private int yStartDrag, xStartDrag;

	// Some variables to enable the dragging of particles
	private static int dragState = CB_View2D.NO_DRAG;
	private int draggingId = -1;
	private double[] oldVector;

	private int hover = -1;

	private static final Image particleGlowImage = CB_Tools.getImage("particleglow.png");
	private static final AlphaComposite alpha100 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
	private static final AlphaComposite alpha75  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f);
	private static final AlphaComposite alpha50  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f);

	private static final int NO_DRAG = 0;
	private static final int DRAG_PARTICLE = 1;
	private static final int DRAG_VIEW = 2;
	private static final int DRAG_MOMENTUM = 3;

	public CB_View2D(CB_Main main, int indexX, int indexY, String label) {
		super(null);
		this.main = main;
		this.indexX = indexX;
		this.indexY = indexY;
		this.label = label;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.addComponentListener(this);
		this.setBackground(Color.black);
		this.setOpaque(true);
		this.setDoubleBuffered(true);
		this.setToolTipText("");
	}
	public String getToolTipText(MouseEvent e) {
		int particle = selectParticle(e.getX(), e.getY());
		if (particle >= 0) {
			return main.getParticle(particle).getTooltip(main.getInstance());
		}
		return null;
	}
	//function whichi paints all components
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		if (dragState == CB_View2D.NO_DRAG) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		this.drawBox(g2d);
		if (this.getInstance().getSelectionType() != CB_Instance.PARTICLES) {
			this.drawRelations(g2d, this.getInstance().getSelectionType());
		}
		this.drawParticles(g2d);
		g2d.setColor(Color.white);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawString(label, 5, 15);
	}
	// functions that draws a green box on the screen
	public void drawBox(Graphics2D g2d) {
		int left = (int) (this.getSize().width / 2 + this.getInstance().getOffset(indexX)) - (int) (this.getInstance().getScale() * this.getInstance().getBox().getDimension(indexX) / 2);
		int right = (int) (this.getSize().width / 2 + this.getInstance().getOffset(indexX)) + (int) (this.getInstance().getScale() * this.getInstance().getBox().getDimension(indexX) / 2);
		int top = (int) (this.getSize().height / 2 + this.getInstance().getOffset(indexY)) - (int) (this.getInstance().getScale() * this.getInstance().getBox().getDimension(indexY) / 2);
		int bottom = (int) (this.getSize().height / 2 + this.getInstance().getOffset(indexY)) + (int) (this.getInstance().getScale() * this.getInstance().getBox().getDimension(indexY) / 2);
		g2d.setColor(Color.green);
		g2d.drawLine(left, top, right, top);
		g2d.drawLine(right, top, right, bottom);
		g2d.drawLine(right, bottom, left, bottom);
		g2d.drawLine(left, bottom, left, top);
	}
	// Function that draws the particles on the screen
	public void drawParticles(Graphics2D g2d) {
		if (this.getInstance().getSelectionType() == CB_Instance.PARTICLES) {
			// If particles are selected, draw the ones not selected first
			for (int i = 0; i < main.getParticles().size(); i++) {
				if (!this.getInstance().getParticleSelected(i)) {
					this.drawParticle(g2d, i);
				}
			}
			// Now draw the selected ones on top
			for (int i = 0; i < this.getInstance().getSelectionSize(); i++) {
				this.drawParticle(g2d, this.getInstance().getSelection(i));
			}
		} else {
			// Else just draw them in the normal order
			for (int i = 0; i < this.getInstance().getParticles().size(); i++) {
				this.drawParticle(g2d, i);
			}
		}
		g2d.setComposite(alpha100);
	}
	// Function that draws one particle
	public void drawParticle(Graphics2D g2d, int particle) {
		if (dragState == CB_View2D.NO_DRAG) {
			if (this.getInstance().getParticleSelected(particle)) {
				g2d.setComposite(alpha100);
			} else if (hover == particle) {
				g2d.setComposite(alpha75);
			} else {
				g2d.setComposite(alpha50);
			}
			g2d.setColor(main.getColor(main.getParticle(particle).getColor()).getColor());
		} else {
			if (this.getInstance().getParticleSelected(particle)) {
				g2d.setColor(main.getColor(main.getParticle(particle).getColor()).getColor());
			} else {
				g2d.setColor(main.getColor(main.getParticle(particle).getColor()).getTransparentColor());
			}
		}
		g2d.fillOval(
			this.convertX(main.getParticle(particle).getPosIndex(indexX)) - this.convertRadius(main.getParticle(particle).getRadius()),
			this.convertY(main.getParticle(particle).getPosIndex(indexY)) - this.convertRadius(main.getParticle(particle).getRadius()),
			this.convertRadius(main.getParticle(particle).getRadius()) * 2,
			this.convertRadius(main.getParticle(particle).getRadius()) * 2
		);
		// If the color is too dark, draw a white circle around the particle
		if (main.getColor(main.getParticle(particle).getColor()).getBlackness() < 0.1f) {
			g2d.setColor(Color.white);
			g2d.drawOval(
				this.convertX(main.getParticle(particle).getPosIndex(indexX)) - this.convertRadius(main.getParticle(particle).getRadius()),
				this.convertY(main.getParticle(particle).getPosIndex(indexY)) - this.convertRadius(main.getParticle(particle).getRadius()),
				this.convertRadius(main.getParticle(particle).getRadius()) * 2,
				this.convertRadius(main.getParticle(particle).getRadius()) * 2
			);
		}
		if (dragState == CB_View2D.NO_DRAG) {
			// Draw the light effect
			g2d.drawImage(
				particleGlowImage,
				this.convertX(main.getParticle(particle).getPosIndex(indexX)),
				this.convertY(main.getParticle(particle).getPosIndex(indexY)) - (int) (this.convertRadius(main.getParticle(particle).getRadius()) / 1.5),
				(int) (this.getInstance().getScale() * main.getParticle(particle).getRadius() / 1.5),
				(int) (this.getInstance().getScale() * main.getParticle(particle).getRadius() / 1.5),
				this
			);
		}
		// If only this particle is selected
		if (this.getInstance().getSelectionType() == CB_Instance.PARTICLES && this.getInstance().getSelectionSize() == 1 && this.getInstance().getParticleSelected(particle)) {
			// Check if the momentum is bigger than 0
			if (main.getParticle(particle).getMomIndex(indexX) != 0 || main.getParticle(particle).getMomIndex(indexY) != 0) {
				// Draw the momentum
				g2d.setColor(Color.white);
				paintArrow(
					g2d,
					this.convertX(main.getParticle(particle).getPosIndex(indexX)),
					this.convertY(main.getParticle(particle).getPosIndex(indexY)),
					this.convertX(main.getParticle(particle).getPosIndex(indexX) + main.getParticle(particle).getMomIndex(indexX)),
					this.convertY(main.getParticle(particle).getPosIndex(indexY) + main.getParticle(particle).getMomIndex(indexY))
				);
			}
		}
	}
	// Function that draws the relations on the screen
	public void drawRelations(Graphics2D g2d, int type) {
		for (int i = 0; i < this.getInstance().getSelectionSize(); i++) {
			g2d.setColor(Color.white);
			if (main.getRelation(type, this.getInstance().getSelection(i)).hasRelationCenter()) {
				if (main.getRelation(type, this.getInstance().getSelection(i)).getParticlesSize() > 1) {
					double[] center = this.getInstance().getParticlesCenter(main.getRelation(type, this.getInstance().getSelection(i)).getParticles());
					for (int u = 0; u < main.getRelation(type, this.getInstance().getSelection(i)).getParticlesSize(); u++) {
						g2d.drawLine(
							this.convertX(main.getParticle(main.getRelation(type, this.getInstance().getSelection(i)).getParticle(u)).getPosIndex(indexX)),
							this.convertY(main.getParticle(main.getRelation(type, this.getInstance().getSelection(i)).getParticle(u)).getPosIndex(indexY)),
							this.convertX(center[indexX]),
							this.convertY(center[indexY])
						);
					}
					g2d.fillOval((int) convertX(center[indexX]) - 3, (int) convertY(center[indexY]) - 3, 6, 6);
				}
			} else {
				for (int j = 0; j < main.getRelation(type, this.getInstance().getSelection(i)).getParticlesSize() - 1; j++) {
					g2d.drawLine(
						this.convertX(main.getParticle(main.getRelation(type, this.getInstance().getSelection(i)).getParticle(j)).getPosIndex(indexX)),
						this.convertY(main.getParticle(main.getRelation(type, this.getInstance().getSelection(i)).getParticle(j)).getPosIndex(indexY)),
						this.convertX(main.getParticle(main.getRelation(type, this.getInstance().getSelection(i)).getParticle(j + 1)).getPosIndex(indexX)),
						this.convertY(main.getParticle(main.getRelation(type, this.getInstance().getSelection(i)).getParticle(j + 1)).getPosIndex(indexY))
					);
				}
			}
		}
	}
	// Function that paints an arrow on the screen
	public void paintArrow(Graphics g, int x0, int y0, int x1, int y1) {
		int deltaX = x1 - x0;
		int deltaY = y1 - y0;
		double frac = 0.15;
		g.drawLine(x0, y0, x1, y1);
		g.drawLine(
			x0 + (int)((1 - frac) * deltaX + frac * deltaY),
			y0 + (int)((1 - frac) * deltaY - frac * deltaX),
			x1,
			y1
		);
		g.drawLine(
			x0 + (int)((1 - frac) * deltaX - frac * deltaY),
			y0 + (int)((1 - frac) * deltaY + frac * deltaX),
			x1,
			y1
		);
	}
	//function to set the particle position
	public void setParticlePosition(int particleId, double posA, double posB) {
		// Invert Y axis...
		if (indexX == CB_Box.HEIGHT) {
			posA = -posA;
		}
		if (indexY == CB_Box.HEIGHT) {
			posB = -posB;
		}
		main.getParticle(particleId).setPosIndex(indexX, posA);
		main.getParticle(particleId).setPosIndex(indexY, posB);
		this.getInstance().getMain().getParticlePropertiesPanel().updatePosition();
		main.getView().getView3D().updateParticle(particleId);
	}
	//function to set the particle position
	public void setParticleMomentum(int particleId, double momA, double momB) {
		// Invert Y axis...
		if (indexX == CB_Box.HEIGHT) {
			momA = -momA;
		}
		if (indexY == CB_Box.HEIGHT) {
			momB = -momB;
		}
		main.getParticle(particleId).setMomIndex(indexX, momA);
		main.getParticle(particleId).setMomIndex(indexY, momB);
		this.getInstance().getMain().getParticlePropertiesPanel().updateMomentum();
		main.getView().getView3D().updateParticle(particleId);
	}
	public void setOffset(int offsetX, int offsetY) {
		this.getInstance().setOffset(indexX, offsetX);
		this.getInstance().setOffset(indexY, offsetY);
	}
	//function to set everything on its original position
	public void resetView() {
		this.getInstance().setScale(this.getOptimalScale());
		this.getInstance().setOffset(new int[] {0, 0, 0});
	}
	//function which handles the zoom functionality
	public double getOptimalScale() {
		if (this.getSize().width > this.getSize().height) {
			return (this.getSize().height - 20) / this.getInstance().getMaxBoxSize();
		}
		return (this.getSize().width - 20) / this.getInstance().getMaxBoxSize();
	}
	//updates the box properties
	public void updateBox() {
		this.resetView();
		this.repaint();
	}
	//updater the particles, interactions and constraints.
	public void update() {
		this.repaint();
	}
	// Creates a particle move that can be undone
	public void createUndoableMove(int id, double[] oldPosition, double[] newPosition) {
		this.getInstance().addUndoableEdit(
			new CB_UndoableEdit(id, oldPosition, newPosition) {
				public void redo() {
					main.getParticle(getId()).setPosition(newVector);
					getInstance().updateParticle(getId());
					super.redo();
				}
				public void undo() {
					main.getParticle(getId()).setPosition(oldVector);
					getInstance().updateParticle(getId());
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Move Particle";
				}
			}
		);
	}
	// Creates a change in momentum that can be undone
	public void createUndoableMomChange(int id, double[] oldMomentum, double[] newMomentum) {
		this.getInstance().addUndoableEdit(
			new CB_UndoableEdit(id, oldMomentum, newMomentum) {
				public void redo() {
					main.getParticle(getId()).setMomentum(newVector);
					getInstance().updateParticle(getId());
					super.redo();
				}
				public void undo() {
					main.getParticle(getId()).setMomentum(oldVector);
					getInstance().updateParticle(getId());
					super.undo();
				}
				public String getUndoRedoPresentationName() {
					return "Alter Particle Momentum";
				}
			}
		);
	}
	public int selectParticle(int x, int y) {
		Point point = new Point(x, y);
		Point particlePoint;
		for (int i = 0; i < this.getInstance().getParticles().size(); i++) {
			if (x >= this.convertX(main.getParticle(i).getPosIndex(indexX)) - this.convertRadius(main.getParticle(i).getRadius()) &&
					x <= this.convertX(main.getParticle(i).getPosIndex(indexX)) + this.convertRadius(main.getParticle(i).getRadius()) &&
					y >= this.convertY(main.getParticle(i).getPosIndex(indexY)) - this.convertRadius(main.getParticle(i).getRadius()) &&
					y <= this.convertY(main.getParticle(i).getPosIndex(indexY)) + this.convertRadius(main.getParticle(i).getRadius())) {
				// The coordinate is in the particle square
				particlePoint = new Point(this.convertX(main.getParticle(i).getPosIndex(indexX)), this.convertY(main.getParticle(i).getPosIndex(indexY)));
				if (point.distance(particlePoint) <= this.convertRadius(main.getParticle(i).getRadius())) {
					// The coordinate is also in the particle circle!
					return i;
				}
			}
		}
		return -1;
	}
	public int convertX(double x) {
		if (indexX == 1) {
			// Invert Y axis
			return (int) - (x * this.getInstance().getScale()) + (this.getSize().width / 2 + this.getInstance().getOffset(indexX));
		} else {
			return (int) (x * this.getInstance().getScale()) + (this.getSize().width / 2 + this.getInstance().getOffset(indexX));
		}
	}
	public int convertY(double y) {
		if (indexY == 1) {
			// Invert Y axis
			return (int) - (y * this.getInstance().getScale()) + (this.getSize().height / 2 + this.getInstance().getOffset(indexY));
		} else {
			return (int) (y * this.getInstance().getScale()) + (this.getSize().height / 2 + this.getInstance().getOffset(indexY));
		}
	}
	public int convertRadius(double radius) {
		return (int) (this.getInstance().getScale() * radius);
	}
	public double convertViewX(double x) {
		return (x - (this.getSize().width / 2) - this.getInstance().getOffset(indexX)) / this.getInstance().getScale();
	}
	public double convertViewY(double y) {
		return (y - (this.getSize().height / 2) - this.getInstance().getOffset(indexY)) / this.getInstance().getScale();
	}
	public void zoomView(int y) {
		// Right click on empty space
		if (yStartDrag > y) {
			this.getInstance().setScale(this.getInstance().getScale() - (this.getInstance().getScale() / 50));
		} else if (yStartDrag < y) {
			this.getInstance().setScale(this.getInstance().getScale() + (this.getInstance().getScale() / 50));
		}
		if (this.getInstance().getScale() < 0) {
			this.getInstance().setScale(- this.getInstance().getScale());
		} else if (this.getInstance().getScale() > 100000000) {
			this.getInstance().setScale(99999990);
		}
		yStartDrag = y;
	}
	public void moveView(int x, int y) {
		// Left click on empty space
		int newOffsetX = this.getInstance().getOffset(indexX) + (x - xStartDrag);
		int newOffsetY = this.getInstance().getOffset(indexY) + (y - yStartDrag);
		int maxOffsetX = this.getSize().width - (int) (this.getSize().width / 2 - this.getInstance().getScale() * this.getInstance().getBox().getDimension(indexX) / 2) - 120;
		int maxOffsetY = this.getSize().height - (int) (this.getSize().height / 2 - this.getInstance().getScale()* this.getInstance().getBox().getDimension(indexY) / 2) - 120;
		if (newOffsetX > maxOffsetX) {
			newOffsetX = maxOffsetX;
		} else if (newOffsetX < - maxOffsetX) {
			newOffsetX = - maxOffsetX;
		}
		if (newOffsetY > maxOffsetY) {
			newOffsetY = maxOffsetY;
		} else if (newOffsetY < - maxOffsetY) {
			newOffsetY = -maxOffsetY;
		}
		this.setOffset(newOffsetX, newOffsetY);
		xStartDrag = x;
		yStartDrag = y;
	}
	public CB_Instance getInstance() {
		return main.getInstance();
	}
	public CB_View getView() {
		return main.getView();
	}
	// Mouse events
	public void mouseClicked(MouseEvent e) {
		int particle = this.selectParticle(e.getX(), e.getY());
		if (particle >= 0) {
			// Press on a particle
			if (e.isMetaDown()) {
				// Right click, show menu
				if (!main.getInstance().getParticleSelected(particle)) {
					main.getInstance().select(CB_Instance.PARTICLES, new int[] {particle});
				}
				if (main.getInstance().getSelectionSize() == 1) {
					CB_ParticleMenu particleMenu = new CB_ParticleMenu(main.getInstance(), particle);
					particleMenu.show(e.getComponent(), e.getX(), e.getY());
				} else {
					CB_ParticleMenu particleMenu = new CB_ParticleMenu(main.getInstance(), main.getInstance().getSelection());
					particleMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			} else {
				// Select the particle
				this.getInstance().pickParticle(particle, e.isControlDown());
			}
		} else if (e.isMetaDown()) {
			// Show non particle menu
			CB_ParticleMenu particleMenu = new CB_ParticleMenu(main.getInstance());
			particleMenu.show(e.getComponent(), e.getX(), e.getY());
		} else if (this.getInstance().getSelectionType() == CB_Instance.PARTICLES &&
					this.getInstance().getSelectionSize() == 1 && e.isShiftDown()) {
			// If we just clicked with shift, instantly change the particle momentum
			oldVector = new double[] {
				this.getInstance().getParticle(this.getInstance().getSelection(0)).getMomIndex(CB_Particle.X),
				this.getInstance().getParticle(this.getInstance().getSelection(0)).getMomIndex(CB_Particle.Y),
				this.getInstance().getParticle(this.getInstance().getSelection(0)).getMomIndex(CB_Particle.Z)
			};
			// If we're dealing with the Y axis, invert the axis
			if (indexY == CB_Box.HEIGHT) {
				this.setParticleMomentum(
					this.getInstance().getSelection(0),
					this.convertViewX(e.getX()) - this.getInstance().getParticle(this.getInstance().getSelection(0)).getPosIndex(indexX),
					this.convertViewY(e.getY()) + this.getInstance().getParticle(this.getInstance().getSelection(0)).getPosIndex(indexY)
				);
			} else {
				this.setParticleMomentum(
					this.getInstance().getSelection(0),
					this.convertViewX(e.getX()) - this.getInstance().getParticle(this.getInstance().getSelection(0)).getPosIndex(indexX),
					this.convertViewY(e.getY()) - this.getInstance().getParticle(this.getInstance().getSelection(0)).getPosIndex(indexY)
				);
			}
			// Create undoable momentum change
			this.createUndoableMomChange(
				this.getInstance().getSelection(0),
				oldVector,
				this.getInstance().getParticle(this.getInstance().getSelection(0)).getMomentum()
			);
			// Done changing momentum
		} else {
			// No particle was 'hit'
			this.getInstance().deselect();
		}
	}
	public void mousePressed(MouseEvent e) {
		xStartDrag = e.getX();
		yStartDrag = e.getY();
		if (dragState == CB_View2D.NO_DRAG) {
			// Which particle are we dragging, if any
			draggingId = this.selectParticle(e.getX(), e.getY());
			if (draggingId >= 0) {
				// Press on a particle, remember old position
				oldVector = new double[] {
					this.getInstance().getParticle(draggingId).getPosIndex(CB_Particle.X),
					this.getInstance().getParticle(draggingId).getPosIndex(CB_Particle.Y),
					this.getInstance().getParticle(draggingId).getPosIndex(CB_Particle.Z)
				};
				// Change state
				dragState = CB_View2D.DRAG_PARTICLE;
			} else if (this.getInstance().getSelectionType() == CB_Instance.PARTICLES &&
					 this.getInstance().getSelectionSize() == 1 && e.isShiftDown()) {
				// If theres one particle selected and shift is pressed, drag the momentum
				// Remember old momentum
				oldVector = new double[] {
					this.getInstance().getParticle(this.getInstance().getSelection(0)).getMomIndex(CB_Particle.X),
					this.getInstance().getParticle(this.getInstance().getSelection(0)).getMomIndex(CB_Particle.Y),
					this.getInstance().getParticle(this.getInstance().getSelection(0)).getMomIndex(CB_Particle.Z)
				};
				// Change state
				dragState = CB_View2D.DRAG_MOMENTUM;
			}
		}
	}
	public void mouseReleased(MouseEvent e) {
		if (dragState == CB_View2D.DRAG_PARTICLE) {
			// If the particle has been dragged create an undoable move
			this.createUndoableMove(draggingId, oldVector, this.getInstance().getParticle(draggingId).getPosition());
		} else if (dragState == CB_View2D.DRAG_MOMENTUM) {
			// If the momentum has been changed create an undoable edit
			this.createUndoableMomChange(
				this.getInstance().getSelection(0),
				oldVector,
				this.getInstance().getParticle(this.getInstance().getSelection(0)).getMomentum()
			);
		}
		// Reset state
		dragState = CB_View2D.NO_DRAG;
		this.getView().update2D();
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {
		hover = -1;
		this.repaint();
	}
	public void mouseDragged(MouseEvent e) {
		// Dragging the mouse..
		if (dragState == CB_View2D.NO_DRAG) {
			// No particle to drag, so lets set the state to view
			dragState = CB_View2D.DRAG_VIEW;
		} else if (dragState == CB_View2D.DRAG_PARTICLE) {
			this.getInstance().pickParticle(draggingId, e.isControlDown());
			// Set the new position
			this.setParticlePosition(
				draggingId,
				this.getInstance().checkParticlePosition(indexX, this.convertViewX(e.getX())),
				this.getInstance().checkParticlePosition(indexY, this.convertViewY(e.getY()))
			);
		} else if (dragState == CB_View2D.DRAG_VIEW) {
			// We are dragging the view, check if we should zoom or move
			if (e.isMetaDown()) {
				this.zoomView(e.getY());
			} else {
				this.moveView(e.getX(), e.getY());
			}
		} else if (dragState == CB_View2D.DRAG_MOMENTUM) {
			// Set the new momentum
			// If we're dealing with the Y axis, invert the axis
			if (indexY == CB_Box.HEIGHT) {
				this.setParticleMomentum(
					this.getInstance().getSelection(0),
					this.convertViewX(e.getX()) - this.getInstance().getParticle(this.getInstance().getSelection(0)).getPosIndex(indexX),
					this.convertViewY(e.getY()) + this.getInstance().getParticle(this.getInstance().getSelection(0)).getPosIndex(indexY)
				);
			} else {
				this.setParticleMomentum(
					this.getInstance().getSelection(0),
					this.convertViewX(e.getX()) - this.getInstance().getParticle(this.getInstance().getSelection(0)).getPosIndex(indexX),
					this.convertViewY(e.getY()) - this.getInstance().getParticle(this.getInstance().getSelection(0)).getPosIndex(indexY)
				);
			}
		}
		// Update all view2d's
		main.getView().update2D();
	}
	public void mouseMoved(MouseEvent e) {
		if (dragState == CB_View2D.NO_DRAG) {
			int temp = this.selectParticle(e.getX(), e.getY());
			if (temp != hover) {
				hover = temp;
				this.repaint();
			}
		}
	}
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			this.getInstance().setScale(this.getInstance().getScale() - (this.getInstance().getScale() / 10));
		} else if (e.getWheelRotation() > 0) {
			this.getInstance().setScale(this.getInstance().getScale() + (this.getInstance().getScale() / 10));
		}
		main.getView().update2D();
	}
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {
		this.resetView();
		main.getView().update2D();
	}
	public void componentShown(ComponentEvent e) {}
}