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

	public Cloth cloth;
	public int particleCountX;
	public int particleCountY;
	public Junction junctionBeingDragged = null;
	public boolean isDragging = false;
	
	

	public ClothSimulation(int particleCountX, int particleCountY) {
		super();
		this.particleCountX = particleCountX;
		this.particleCountY = particleCountY;
		cloth = new Cloth(particleCountX, particleCountY);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void run() {
		long lastLoopTime = System.nanoTime();
		final int FPS = 60;
		final long OPTIMAL_TIME = 1000000000 / FPS;

		while (true) {
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double dT = updateLength / ((double) OPTIMAL_TIME);

			cloth.updateCloth(dT);
			this.repaint();

			try {
				Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
			} catch (Exception e) {

			}
		}
	}

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

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		Rectangle selectionBox = new Rectangle(x - 5, y - 5, 10, 10);

			for (int i = 0; i < particleCountX; i++) {
				for (int j = 0; j < particleCountY; j++) {
					if (cloth.junctions[i][j] != null && selectionBox.contains(cloth.junctions[i][j].getCurrentX(), cloth.junctions[i][j].getPreviousY())) {
						junctionBeingDragged = cloth.junctions[i][j];
						this.repaint();
						break;
					}
				}
			}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		isDragging = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isDragging = false;
		if (junctionBeingDragged != null) {
			junctionBeingDragged.setMovable(true);
			junctionBeingDragged = null;
		}
	}

	//Unused functions.
	public void mouseEntered(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1000, 1000);
	}
}