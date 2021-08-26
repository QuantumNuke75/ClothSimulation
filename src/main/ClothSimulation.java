package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ClothSimulation extends JPanel implements MouseListener, MouseMotionListener {

	//Instance of the Cloth.
	public Cloth cloth;

	//The number of Junction on each axis.
	public int junctionCountX;
	public int junctionCountY;

	//The Junction being dragged.
	public Junction junctionBeingDragged = null;

	//Whether a Junction is being dragged.
	public boolean isDragging = false;


	/**
	 *
	 * @param junctionCountX
	 * @param junctionCountY
	 */
	public ClothSimulation(int junctionCountX, int junctionCountY) {
		super();
		this.junctionCountX = junctionCountX;
		this.junctionCountY = junctionCountY;
		cloth = new Cloth(junctionCountX, junctionCountY);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 *
	 */
	public void run() {
		long lastLoopTime = System.nanoTime();
		final int FPS = 60;
		final long preferredTime = 1000000000 / FPS;

		while (true) {
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double dT = updateLength / ((double) preferredTime);

			cloth.updateCloth(dT);
			this.repaint();

			try {
				Thread.sleep((lastLoopTime - System.nanoTime() + preferredTime) / 1000000);
			} catch (Exception e) {

			}
		}
	}

	/**
	 *
	 * @param gr
	 */
	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, super.getWidth(), super.getHeight());
		
		for (Connector connector : cloth.connectors) {
				g.setStroke(new BasicStroke(2));


				int red = (int) (255 * (1 - connector.getLength() / 50));
				int green = (int) (255 * (1 - connector.getLength() / 50));

				if (red > 255) {
					red = 255;
				} else if (red < 0) {
					red = 0;
				}

				if (green > 255) {
					green = 255;
				} else if (green < 0) {
					green = 0;
				}

				g.setColor(new Color(255 - red, green, 0));
				g.drawLine((int) connector.getStartJunction().getCurrentX(), (int) connector.getStartJunction().getCurrentY(),
						(int) connector.getEndJunction().getCurrentX(), (int) connector.getEndJunction().getCurrentY());
		}

		//Drawing every Junction.
		for(Junction[] junction1 : cloth.junctions) {
			for(Junction junction2 : junction1) {
				if(junction2 != null){
					g.setColor(Color.WHITE);
					g.fillOval((int)(junction2.getCurrentX() - 2), (int)(junction2.getCurrentY() - 2), 4, 4);
				}
			}
		}

		//Update position of dragged junction based on mouse location.
		if(isDragging) {
			SwingUtilities.convertPointFromScreen(MouseInfo.getPointerInfo().getLocation(), this);
			if (junctionBeingDragged != null) {
				junctionBeingDragged.setCurrentX(MouseInfo.getPointerInfo().getLocation().x - this.getLocationOnScreen().x);
				junctionBeingDragged.setCurrentY(MouseInfo.getPointerInfo().getLocation().y - this.getLocationOnScreen().y);
			}
		}
	}

	/**
	 *
	 * @param e
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		Rectangle selectionBox = new Rectangle(x - 5, y - 5, 10, 10);

			for (int i = 0; i < junctionCountX; i++) {
				for (int j = 0; j < junctionCountY; j++) {
					if (cloth.junctions[i][j] != null && selectionBox.contains(cloth.junctions[i][j].getCurrentX(), cloth.junctions[i][j].getPreviousY())) {
						junctionBeingDragged = cloth.junctions[i][j];
						this.repaint();
						break;
					}
				}
			}
	}

	/**
	 *
	 * @param e
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		isDragging = true;
	}

	/**
	 *
	 * @param e
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		isDragging = false;
		if (junctionBeingDragged != null) {
			junctionBeingDragged.setMovable(true);
			junctionBeingDragged = null;
		}
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1000, 1000);
	}

	//Unused functions.
	public void mouseEntered(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
}