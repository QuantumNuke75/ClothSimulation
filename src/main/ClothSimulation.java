package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

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
    private boolean showShading = false;

    //Default Color Settings
    private Color junctionColor = Color.BLUE;
    private Color connectorColor = Color.GREEN;

    //Image printing
    String folder = UUID.randomUUID().toString();
    int image_num = 0;

    //Threads
    Thread paintThread;
    Thread calculationThread;
    boolean isPaused = true;

    /**
     * The constructor. Creates a new Cloth instance and sets up listeners.
     *
     * @param junctionCountX - The number of horizontal Junctions.
     * @param junctionCountY - The number of vertical Junctions.
     */
    public ClothSimulation(int junctionCountX, int junctionCountY) {
        super();
        this.junctionCountX = junctionCountX;
        this.junctionCountY = junctionCountY;
        this.cloth = new Cloth(junctionCountX, junctionCountY);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * The simulation's loop.
     */
    public void run() {
        //Create image directory
        File file = new File("C:\\\\Users\\\\ethan\\\\OneDrive\\\\Desktop\\\\Cloth Simulation Images\\" + this.folder);
        file.mkdir();

        //Create and start a new PaintThread
        paintThread = new PaintThread();
        paintThread.start();

        //Create and start and new CalculationThread
        calculationThread = new CalculationThread();
        calculationThread.start();
    }

    public void togglePause(){
        this.isPaused = !this.isPaused;
    }

    /**
     * Runs everytime the graphics are redrawn. Draws the border, Connectors, and Junctions. Also handles the dragging of Junctions.
     *
     * @param gr - The given Graphics instance.
     */
    @Override
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D) gr;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, super.getWidth(), super.getHeight());

        //The coordinates of the maximum dimensions of the simulation converted to the Screen Space coordinates.
        int[] thousandCoordinates = convertSimulationCoordsToScreenSpaceCoords(1000, 1000);

        //Draw the border.
        g.setStroke(new BasicStroke(5));
        g.setColor(Color.WHITE);
        g.drawLine(0 + (this.getWidth() - thousandCoordinates[0]) / 2, 0, thousandCoordinates[0] + (this.getWidth() - thousandCoordinates[0]) / 2, 0);
        g.drawLine(thousandCoordinates[0] + (this.getWidth() - thousandCoordinates[0]) / 2, 0, thousandCoordinates[0] + (this.getWidth() - thousandCoordinates[0]) / 2, thousandCoordinates[1]);
        g.drawLine(thousandCoordinates[0] + (this.getWidth() - thousandCoordinates[0]) / 2, thousandCoordinates[1], 0 + (this.getWidth() - thousandCoordinates[0]) / 2, thousandCoordinates[1]);
        g.drawLine(0 + (this.getWidth() - thousandCoordinates[0]) / 2, thousandCoordinates[1], (this.getWidth() - thousandCoordinates[0]) / 2, 0);

        if (showShading) {
            colorAreas(g);
        }

        //If the option drawConnectors in enabled.
        if (this.drawConnectors) {
            for (Object o : this.cloth.connectorArrayList.toArray()) {
                Connector connector = (Connector) o;

                //Set the width of the line used to draw Connectors.
                g.setStroke(new BasicStroke(2));

                //If the option showStress is enabled.
                if (this.showStress) {
                    //Red and green RBG values based on length of the Connector.
                    int red = (int) (255 * (1 - connector.recalculateLength() / 75));
                    int green = (int) (200 * (1 - connector.recalculateLength() / 75));

                    //Validate the bounds of the red and green RBG values.
                    red = validateColorBounds(red);
                    green = validateColorBounds(green);

                    //Set the color to draw of the Connector.
                    g.setColor(new Color(255 - red, green, 0));
                } else {
                    //Sets the color of the Connector to be drawn.
                    g.setColor(this.connectorColor);
                }
                //The Screen Space coordinates of the start Junction.
                int[] drawCoordinatesStart = convertSimulationCoordsToScreenSpaceCoords((int) connector.getJunctionA().getCurrentX(), (int) connector.getJunctionA().getCurrentY());

                //The Screen Space coordinates of the end Junction.
                int[] drawCoordinatesEnd = convertSimulationCoordsToScreenSpaceCoords((int) connector.getJunctionB().getCurrentX(), (int) connector.getJunctionB().getCurrentY());

                //Draw the Connector with the given coordinates.
                g.drawLine(drawCoordinatesStart[0] + (this.getWidth() - thousandCoordinates[0]) / 2, drawCoordinatesStart[1] + (this.getHeight() - thousandCoordinates[1]) / 2, drawCoordinatesEnd[0] + (this.getWidth() - thousandCoordinates[0]) / 2, drawCoordinatesEnd[1] + (this.getHeight() - thousandCoordinates[1]) / 2);
            }
        }

        //Drawing every Junction.
        if (this.drawJunction) {
            for (Object o : this.cloth.junctionsArrayList.toArray()) {
                Junction junction = (Junction) o;

                //Check if the junction is null, if so, continue
                if (junction == null) {
                    continue;
                }

                //Set the color to be drawn to the default color.
                g.setColor(this.junctionColor);

                //Gets the Screen Space coordinates of the Junction.
                int[] drawCoordinates = convertSimulationCoordsToScreenSpaceCoords((int) junction.getCurrentX(), (int) junction.getCurrentY());

                //Draws the Junction with the given coordinates and width/height.
                g.fillOval(drawCoordinates[0] - 2 + (this.getWidth() - thousandCoordinates[0]) / 2, drawCoordinates[1] - 2 + (this.getHeight() - thousandCoordinates[1]) / 2, 4, 4);
            }
        }

        //Update position of dragged junction based on mouse location.
        if (this.isDragging) {
            //SwingUtilities.convertPointFromScreen(MouseInfo.getPointerInfo().getLocation(), this);
            if (this.junctionBeingDragged != null) {
                //Get the simulation coordinates from the Screen Space coordinates.
                int[] drawCoordsInSimulationCoords = convertScreenSpaceCoordsToSimulationCoords(MouseInfo.getPointerInfo().getLocation().x - this.getLocationOnScreen().x - (this.getWidth() - thousandCoordinates[0]) / 2, MouseInfo.getPointerInfo().getLocation().y - this.getLocationOnScreen().y - (this.getHeight() - thousandCoordinates[1]) / 2);
                this.junctionBeingDragged.setCurrentX(drawCoordsInSimulationCoords[0]);
                this.junctionBeingDragged.setCurrentY(drawCoordsInSimulationCoords[1]);
            }
        }
    }

    /**
     * Colors in all the polygons contained within the Cloth.
     *
     * @param g - The given Graphics2D object.
     */
    public void colorAreas(Graphics2D g) {

        for (int i = 0; i < this.junctionCountX * this.junctionCountY; i++) {

            //If selected junction is not last row or column
            if (getRowFromNumber(i) < this.junctionCountY - 1 && getColFromNumber(i) < this.junctionCountX - 1) {

                Junction junction1 = this.cloth.junctionsArrayList.get(i);
                Junction junction2 = this.cloth.junctionsArrayList.get(i + 1);
                Junction junction3 = this.cloth.junctionsArrayList.get(i + junctionCountY + 1);
                Junction junction4 = this.cloth.junctionsArrayList.get(i + junctionCountY);

                //If all Junctions form a valid area.
                if (junctionConnectedToJunction(junction1, junction2) && junctionConnectedToJunction(junction2, junction3) && junctionConnectedToJunction(junction3, junction4) && junctionConnectedToJunction(junction4, junction1)) {

                    //The coordinates of the maximum dimensions of the simulation converted to the Screen Space coordinates.
                    int[] thousandCoordinates = convertSimulationCoordsToScreenSpaceCoords(1000, 1000);

                    //The coordinates of all the Junctions converted to Screen Space coordinates.
                    int[] junction1SS = convertSimulationCoordsToScreenSpaceCoords((int) junction1.getCurrentX(), (int) junction1.getCurrentY());
                    int[] junction2SS = convertSimulationCoordsToScreenSpaceCoords((int) junction2.getCurrentX(), (int) junction2.getCurrentY());
                    int[] junction3SS = convertSimulationCoordsToScreenSpaceCoords((int) junction3.getCurrentX(), (int) junction3.getCurrentY());
                    int[] junction4SS = convertSimulationCoordsToScreenSpaceCoords((int) junction4.getCurrentX(), (int) junction4.getCurrentY());

                    //The coordinates of all the vertices used for area calculation.
                    int[] allXCalc = new int[]{(int) junction1.getCurrentX(), (int) junction2.getCurrentX(), (int) junction3.getCurrentX(), (int) junction4.getCurrentX()};
                    int[] allYCalc = new int[]{(int) junction1.getCurrentY(), (int) junction2.getCurrentY(), (int) junction3.getCurrentY(), (int) junction4.getCurrentY()};

                    //The coordinates of all the vertices used to draw the polygon.
                    int[] allX = new int[]{junction1SS[0] + (this.getWidth() - thousandCoordinates[0]) / 2, junction2SS[0] + (this.getWidth() - thousandCoordinates[0]) / 2, junction3SS[0] + (this.getWidth() - thousandCoordinates[0]) / 2, junction4SS[0] + (this.getWidth() - thousandCoordinates[0]) / 2};
                    int[] allY = new int[]{junction1SS[1] + (this.getHeight() - thousandCoordinates[1]) / 2, junction2SS[1] + (this.getHeight() - thousandCoordinates[1]) / 2, junction3SS[1] + (this.getHeight() - thousandCoordinates[1]) / 2, junction4SS[1] + (this.getHeight() - thousandCoordinates[1]) / 2};

                    //Calculate area of polygon between given coordinates.
                    double area = polygonArea(allXCalc, allYCalc, 4);

                    int color = (int) (255 * (area / 1000));

                    //Validate color bounds
                    color = validateColorBounds(color);

                    //Sets grayscale color
                    g.setColor(new Color(color, color, color));

                    //Draws polygon
                    g.fillPolygon(allX, allY, 4);
                }
            }
        }
    }

    /**
     * Calculates and returns the area of a polygon given the coordinates of all the vertices.
     *
     * @param x           - The x coordinates of all the vertices.
     * @param y           - The y coordinates of all the vertices.
     * @param numVertices - The number of vertices in the polygon.
     * @returns the area of the polygon.
     */
    public double polygonArea(int[] x, int[] y, int numVertices) {
        double area = 0;

        int j = numVertices - 1;
        for (int i = 0; i < numVertices; i++) {
            area += (x[j] + x[i]) * (y[j] - y[i]);
            j = i;
        }
        return Math.abs(area / 2.0);
    }

    /**
     * Checks whether two junctions are directly connected to eachother.
     */
    public boolean junctionConnectedToJunction(Junction junction1, Junction junction2) {
        if (junction1 == null || junction2 == null) {
            return false;
        }
        for (Connector connector : junction1.getRelatedConnectors()) {
            if (connector.getJunctionA().equals(junction2) || connector.getJunctionB().equals(junction2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the row from a given index.
     *
     * @param num - The given index.
     * @returns the row.
     */
    public int getRowFromNumber(int num) {
        return (int) Math.floor(num / this.junctionCountX);
    }

    /**
     * Calculates the column from a given index.
     *
     * @param num - The given index.
     * @returns the column.
     */
    public int getColFromNumber(int num) {
        return num % this.junctionCountY;
    }

    /**
     * Validates to check if an RBG value is within bounds.
     *
     * @param color - The integer of the color.
     * @returns the validates color.
     */
    public int validateColorBounds(int color) {
        color = color > 255 ? 255 : color < 0 ? 0 : color;
        return color;
    }

    /**
     * Method to convert the relative simulation coordinates to the coordinates used by the drawing system.
     *
     * @param x - The x coordinate in simulation coordinates.
     * @param y - The y coordinate in simulation coordinates.
     * @returns an integer Array containing the converted coordinates.
     */
    public int[] convertSimulationCoordsToScreenSpaceCoords(int x, int y) {
        double preferredSize = 1000;
        int windowWidth = Start.INSTANCE.clothSimulation.getWidth();
        int windowHeight = Start.INSTANCE.clothSimulation.getHeight();
        double scale = windowWidth >= windowHeight ? windowHeight / preferredSize : windowWidth / preferredSize;

        return new int[]{(int) (x * scale), (int) (y * scale)};
    }

    /**
     * Method used to convert the relative drawing coordinates to the simulation coordinates.
     *
     * @param x - The x coordinate in Screen Space coordinates.
     * @param y - The y coordinate in Screen Space coordinates.
     * @returns an integer Array containing the converted coordinates.
     */
    public int[] convertScreenSpaceCoordsToSimulationCoords(int x, int y) {
        double preferredSize = 1000;
        int windowWidth = Start.INSTANCE.clothSimulation.getWidth();
        int windowHeight = Start.INSTANCE.clothSimulation.getHeight();
        double scale = windowWidth >= windowHeight ? windowHeight / preferredSize : windowWidth / preferredSize;

        return new int[]{(int) (x / scale), (int) (y / scale)};
    }

    /**
     * The overridden mousePressed method. Run whenever the mouse is pressed.
     *
     * @param e - The given {@link MouseEvent}.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

//        int[] ssClick = new int[]{x, y};
//        //When converting to sim coords, must subtract relative xoffset
//        int[] ssClicktoSim = convertScreenSpaceCoordsToSimulationCoords(ssClick[0] - (this.getWidth()-thousandCoords[0])/2,ssClick[1]- (this.getHeight()-thousandCoords[1])/2);
//        int[] simtoSS = convertSimulationCoordsToScreenSpaceCoords(ssClicktoSim[0], ssClicktoSim[1]);
//        System.out.println("SS Click: " + ssClick[0] + ", " + ssClick[1]);
//        System.out.println("SS Click to Sim: " + ssClicktoSim[0] + ", " + ssClicktoSim[1]);
//        //When converting to ss coords, must add relative xoffset
//        System.out.println("Sim to SS: " + simtoSS[0] + (this.getWidth() - thousandCoords[0])/2 + ", " + simtoSS[1] + (this.getHeight() - thousandCoords[1])/2);

        //Create a Rectangle for use in determining whether the mouse is dragging an object or not.
        Rectangle selectionBoxSimulation = new Rectangle(x - 5, y - 5, 10, 10);
        for (Object o : this.cloth.junctionsArrayList.toArray()) {
            Junction junction = (Junction) o;

            //Check if the junction is null, if so, continue
            if (junction == null) {
                continue;
            }

            //The coordinates of the Junction converted from simulation coordinates to the Screen Space coordinates.
            int[] simulationCoords = convertSimulationCoordsToScreenSpaceCoords((int) junction.getCurrentX(), (int) junction.getCurrentY());

            //The maximum coordinates of the simulation converted into Screen Space coordinates.
            int[] thousandCoords = convertSimulationCoordsToScreenSpaceCoords(1000, 1000);

            //If the current Junction is within the bounds of the defined Rectangle.
            if (selectionBoxSimulation.contains(simulationCoords[0] + (this.getWidth() - thousandCoords[0]) / 2, simulationCoords[1] + (this.getHeight() - thousandCoords[1]) / 2)) {
                this.junctionBeingDragged = junction;
                this.repaint();
                break;
            }
        }
    }

    /**
     * The overridden mouseDragged method. Run whenever the mouse is dragged.
     *
     * @param e - The given {@link MouseEvent}
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!e.isShiftDown()) {
            this.isDragging = true;
        } else {
            //slice through connectors
        }
    }

    /**
     * The overridden mouseReleased event. Run whenever the mouse button is released.
     *
     * @param e - The given {@link MouseEvent}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        this.isDragging = false;
        if (this.junctionBeingDragged != null) {
            this.junctionBeingDragged = null;
        }
    }

    /**
     * The overridden mouseClicked event. Runs when any mouse button is clicked.
     *
     * @param e - The given {@link MouseEvent}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        Rectangle selectionBoxSimulation = new Rectangle(x - 5, y - 5, 10, 10);
        for (Object o : this.cloth.junctionsArrayList.toArray()) {
            Junction junction = (Junction) o;

            //Check if the junction is null, if so, continue
            if (junction == null) {
                continue;
            }

            //The coordinates of the Junction converted from simulation coordinates to the Screen Space coordinates.
            int[] simulationCoords = convertSimulationCoordsToScreenSpaceCoords((int) junction.getCurrentX(), (int) junction.getCurrentY());

            //The maximum coordinates of the simulation converted into Screen Space coordinates.
            int[] thousandCoords = convertSimulationCoordsToScreenSpaceCoords(1000, 1000);

            //If the current Junction is within the bounds of the defined Rectangle.
            if (selectionBoxSimulation.contains(simulationCoords[0] + (this.getWidth() - thousandCoords[0]) / 2, simulationCoords[1] + (this.getHeight() - thousandCoords[1]) / 2)) {
                if (junction.getJunctionState() != JunctionState.NORMAL) {
                    junction.setJunctionState(JunctionState.NORMAL);
                } else {
                    junction.setJunctionState(JunctionState.ANCHOR);
                }
                break;
            }
        }
    }

    /**
     * Save the image.
     */
    public void captureAndSaveImage(){
        BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        this.print(g);
        g.dispose();
        try {
            int[] thousandCoords = convertSimulationCoordsToScreenSpaceCoords(1000, 1000);
            bi = bi.getSubimage((this.getWidth() - thousandCoords[0]) / 2, (this.getHeight() - thousandCoords[1]) / 2, thousandCoords[0], thousandCoords[1]);
            ImageIO.write(bi, "png", new File("C:\\Users\\ethan\\OneDrive\\Desktop\\Cloth Simulation Images\\" + this.folder + "\\" + this.image_num + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        image_num++;
    }

    /**
     * The overridden getPreferredSize method.
     *
     * @returns the preferred Dimension of this JPanel.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1000, 1000);
    }

    /**
     * Unused functions.
     */
    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Getters and Setters
     */
    public boolean isDrawJunction() {
        return this.drawJunction;
    }

    public void setDrawJunction(boolean drawJunction) {
        this.drawJunction = drawJunction;
    }

    public boolean isDrawConnectors() {
        return this.drawConnectors;
    }

    public void setDrawConnectors(boolean drawConnectors) {
        this.drawConnectors = drawConnectors;
    }

    public boolean isShowStress() {
        return this.showStress;
    }

    public void setShowStress(boolean showStress) {
        this.showStress = showStress;
    }

    public Color getJunctionColor() {
        return this.junctionColor;
    }

    public void setJunctionColor(Color junctionColor) {
        this.junctionColor = junctionColor;
    }

    public Color getConnectorColor() {
        return this.connectorColor;
    }

    public void setConnectorColor(Color connectorColor) {
        this.connectorColor = connectorColor;
    }

    public boolean isShowShading() {
        return this.showShading;
    }

    public void setShowShading(boolean showShading) {this.showShading = showShading;}

    //Inner class for purposes of splitting drawing onto a different thread for timing purposes
    class PaintThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if(!Start.INSTANCE.clothSimulation.isPaused) {
                    long firstTime = System.currentTimeMillis();
                    Start.INSTANCE.clothSimulation.repaint();
                    captureAndSaveImage();
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    long secondTime = System.currentTimeMillis();
                    //System.out.println("Draw took: " + (secondTime - firstTime) + " ms");
                }
                else{
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //Inner class for purposes of splitting calculations onto a different thread for timing purposes
    class CalculationThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if(!Start.INSTANCE.clothSimulation.isPaused) {
                    long firstTime = System.currentTimeMillis();
                    Start.INSTANCE.clothSimulation.cloth.updateCloth(0.98);
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    long secondTime = System.currentTimeMillis();
                    System.out.println("Step took: " + (secondTime - firstTime) + " ms");
                    System.out.println("Calculation per Second: " + (int) (1000.0 / (secondTime - firstTime)));
                }
                else{
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}