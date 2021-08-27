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
        //Instantiates the first instance of when the loop is run.
        long lastLoopTime = System.nanoTime();

        //The locked FPS of the simulation.
        final int FPS = 60;

        //The preferred time step.
        final long preferredTime = 1000000000 / FPS;

        //The loop for the simulation to run in.
        while (true) {
            //Gets the current time.
            long now = System.nanoTime();

            //Get difference between the last loop run and this loop run.
            long updateLength = now - lastLoopTime;

            //Sets the last run loop time to now.
            lastLoopTime = now;

            //Sets the delta time based on the difference between the last loop time and now,
            //divided by the preferred time step.
            double dT = updateLength / ((double) preferredTime);

            //Updates the Cloth instance.
            cloth.updateCloth(dT);

            //Redraws everything needed to be drawn by the simulation.
            this.repaint();

            try {
                //Delays the program's next loop
                Thread.sleep((lastLoopTime - System.nanoTime() + preferredTime) / 1000000);
            }
            catch (Exception e) {

            }
        }
    }

    /**
     * @param gr
     */
    @Override
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D) gr;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, super.getWidth(), super.getHeight());

        //Draw the border.
		g.setStroke(new BasicStroke(5));
		g.setColor(Color.WHITE);
		g.drawLine(0,0,this.getWidth(),0);
		g.drawLine(this.getWidth(),0,this.getWidth(),this.getHeight());
		g.drawLine(this.getWidth(), this.getHeight(), 0, this.getHeight());
		g.drawLine(0, this.getHeight(), 0, 0);

        //If the option drawConnectors in enabled.
        if (drawConnectors) {
            for (Connector connector : cloth.connectors) {

                //Set the width of the line used to draw Connectors.
                g.setStroke(new BasicStroke(2));

                //If the option showStress is enabled.
                if (showStress) {
                    //Red and green RBG values based on length of the Connector.
                    int red = (int) (255 * (1 - connector.getLength() / 75));
                    int green = (int) (255 * (1 - connector.getLength() / 75));

                    //Validate the bounds of the red and green RBG values.
                    red = validateColorBounds(red);
                    green = validateColorBounds(green);

                    //Set the color to draw of the Connector.
                    g.setColor(new Color(255 - red, green, 0));
                } else {
                    //Sets the color of the Connector to be drawn.
                    g.setColor(connectorColor);
                }
                //The Screen Space coordinates of the start Junction.
                int[] drawCoordinatesStart = convertSimulationCoordsToScreenSpaceCoords((int) connector.getStartJunction().getCurrentX(), (int) connector.getStartJunction().getCurrentY());

                //The Screen Space coordinates of the end Junction.
                int[] drawCoordinatesEnd = convertSimulationCoordsToScreenSpaceCoords((int) connector.getEndJunction().getCurrentX(), (int) connector.getEndJunction().getCurrentY());

                //Draw the Connector with the given coordinates.
                g.drawLine(drawCoordinatesStart[0], drawCoordinatesStart[1], drawCoordinatesEnd[0], drawCoordinatesEnd[1]);
            }
        }

        //Drawing every Junction.
        if (drawJunction) {
            for (Junction junction : cloth.junctionsArrayList) {
            	//Set the color to be drawn to the default color.
                g.setColor(junctionColor);

                //Gets the Screen Space coordinates of the Junction.
                int[] drawCoordinates = convertSimulationCoordsToScreenSpaceCoords((int) junction.getCurrentX(), (int) junction.getCurrentY());

                //Draws the Junction with the given coordinates and width/height.
                g.fillOval(drawCoordinates[0] - 2, drawCoordinates[1] - 2, 4, 4);
            }
        }

        //Update position of dragged junction based on mouse location.
        if (isDragging) {
            //SwingUtilities.convertPointFromScreen(MouseInfo.getPointerInfo().getLocation(), this);
            if (junctionBeingDragged != null) {

				//Get the simulation coordinates from the Screen Space coordinates.
                int[] drawCoordsInSimulationCoords = convertScreenSpaceCoordsToSimulationCoords(MouseInfo.getPointerInfo().getLocation().x - this.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - this.getLocationOnScreen().y);

                //Sets the current dragged Junction to the converted simulation coordinates.
                junctionBeingDragged.setCurrentX(drawCoordsInSimulationCoords[0]);
                junctionBeingDragged.setCurrentY(drawCoordsInSimulationCoords[1]);
            }
        }
    }

    /**
     * @param color
     * @return
     */
    public int validateColorBounds(int color) {
        color = color > 255 ? 255 : color < 0 ? 0 : color;
        return color;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public int[] convertSimulationCoordsToScreenSpaceCoords(int x, int y) {
        double scaleX;
        double preferredWidth = 1000;
        int windowWidth = Start.INSTANCE.clothSimulation.getWidth();
        scaleX = windowWidth / preferredWidth;

        return new int[]{(int) (x * scaleX), (int) (y * scaleX)};
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public int[] convertScreenSpaceCoordsToSimulationCoords(int x, int y) {
        double scaleX;
        double preferredWidth = 1000;
        int windowWidth = Start.INSTANCE.clothSimulation.getWidth();
        scaleX = windowWidth / preferredWidth;

        return new int[]{(int) (x / scaleX), (int) (y / scaleX)};
    }


    /**
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        Rectangle selectionBox = new Rectangle(x - 5, y - 5, 10, 10);

        for (Junction junction : cloth.junctionsArrayList) {
            //Gets the screen coordinates from thr coordinates of the Junction
            int[] drawCoordinates = convertSimulationCoordsToScreenSpaceCoords((int) junction.getCurrentX(), (int) junction.getCurrentY());
            if (selectionBox.contains(drawCoordinates[0], drawCoordinates[1])) {
                junctionBeingDragged = junction;
                this.repaint();
                break;
            }
        }
    }

    /**
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        isDragging = true;
    }

    /**
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
     * @return
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1000, 1000);
    }

    //Unused functions.
    public void mouseEntered(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

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