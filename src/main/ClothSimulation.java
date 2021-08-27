package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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

	//Settings
	private boolean drawJunction = false;
	private boolean drawConnectors = true;
	private boolean showStress = true;

	private Color junctionColor = Color.BLUE;
	private Color connectorColor = Color.GREEN;


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

		if(drawConnectors){
			for (Connector connector : cloth.connectors) {
					g.setStroke(new BasicStroke(2));

					if(showStress) {

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
					}
					else{
						g.setColor(connectorColor);
					}

					g.drawLine((int) connector.getStartJunction().getCurrentX(), (int) connector.getStartJunction().getCurrentY(), (int) connector.getEndJunction().getCurrentX(), (int) connector.getEndJunction().getCurrentY());
			}
		}

		//Drawing every Junction.
		if(drawJunction) {
			for(Junction junction : cloth.junctionsArrayList){
				g.setColor(junctionColor);
				g.fillOval((int) (junction.getCurrentX() - 2), (int) (junction.getCurrentY() - 2), 4, 4);
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

		for(Junction junction : cloth.junctionsArrayList){
			if(selectionBox.contains(junction.getCurrentX(), junction.getCurrentY())){
				junctionBeingDragged = junction;
				this.repaint();
				break;
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

	public boolean isDrawJunction() {
		return drawJunction;
	}

	public void setDrawJunction(boolean drawJunction) {
		this.drawJunction = drawJunction;
	}

	public boolean isDrawConnectors() {
		return drawConnectors;
	}

	public void setDrawConnectors(boolean drawConnectors) {
		this.drawConnectors = drawConnectors;
	}

	public boolean isShowStress() {
		return showStress;
	}

	public void setShowStress(boolean showStress) {
		this.showStress = showStress;
	}

	public Color getJunctionColor() {
		return junctionColor;
	}

	public void setJunctionColor(Color junctionColor) {
		this.junctionColor = junctionColor;
	}

	public Color getConnectorColor() {
		return connectorColor;
	}

	public void setConnectorColor(Color connectorColor) {
		this.connectorColor = connectorColor;
	}
}