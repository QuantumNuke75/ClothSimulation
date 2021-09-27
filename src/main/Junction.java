package main;

import java.util.ArrayList;

public class Junction {

    //The total stress of the Junction.
    public float totalStress;
    //The instance of the cloth.
    private Cloth cloth;
    //Position related variables.
    private double currentX;
    private double currentY;
    private double previousX;
    private double previousY;
    //Acceleration Related Variables
    private double ax;
    private double ay;
    //The current state of the particle
    private JunctionState junctionState;

    //An {@link ArrayList} of all the connected Connectors.
    private ArrayList<Connector> relatedConnectors = new ArrayList<>();

    /**
     * @param cloth         {@link Cloth}
     * @param x             - The given x position of the Junction.
     * @param y             - The given y position of the Junction.
     * @param junctionState - The given state of the particle.
     */
    public Junction(Cloth cloth, double x, double y, JunctionState junctionState) {
        this.cloth = cloth;
        this.currentX = x;
        this.previousX = x;
        this.currentY = y;
        this.previousY = y;
        this.ax = 0;
        this.ay = this.cloth.getGravityStrength();
        this.junctionState = junctionState;
    }

    /**
     * The update function. Calculates the new position based off the gravitational and wind acceleration. Also, calculates the collision for
     * the bounds of the simulation. The totalStress is calculated off of the total lengths of all associated Connectors.
     *
     * @param dT - The delta time.
     */
    public void update(double dT) {
        if (this.junctionState == JunctionState.NORMAL) {
            //Calculates new position.
            double tempX = this.currentX + this.cloth.getDampeningCoeff() * ((this.currentX - this.previousX) + 0.5 * this.cloth.getNewWindStrengthX() * dT * dT);
            double tempY = this.currentY + this.cloth.getDampeningCoeff() * ((this.currentY - this.previousY) + 0.5 * this.cloth.getGravityStrength() * dT * dT);

            //Set previous coordinates.
            this.previousX = this.currentX;
            this.previousY = this.currentY;

            //Setting current position to calculated position.
            this.currentX = tempX;
            this.currentY = tempY;

            //Check for out of bounds on the y-axis.
            if (this.currentY > 1000) {
                this.currentY = 1000;
            } else if (this.currentY < 0) {
                this.currentY = 0;
            }

            //Check for out of bounds on the x-axis.
            if (this.currentX > 1000) {
                this.currentX = 1000;
            } else if (this.currentX < 0) {
                this.currentX = 0;
            }

            this.totalStress = 0;
            for (Connector connector : this.relatedConnectors) {
                this.totalStress += connector.length;
            }
        }
    }

    /**
     * Getters and Setters
     */
    public Cloth getCloth() {
        return this.cloth;
    }

    public void setCloth(Cloth cloth) {
        this.cloth = cloth;
    }

    public double getCurrentX() {
        return this.currentX;
    }

    public void setCurrentX(double currentX) {
        this.currentX = currentX;
    }

    public double getCurrentY() {
        return this.currentY;
    }

    public void setCurrentY(double currentY) {
        this.currentY = currentY;
    }

    public double getPreviousX() {
        return this.previousX;
    }

    public void setPreviousX(double previousX) {
        this.previousX = previousX;
    }

    public double getPreviousY() {
        return this.previousY;
    }

    public void setPreviousY(double previousY) {
        this.previousY = previousY;
    }

    public double getAx() {
        return this.ax;
    }

    public void setAx(double ax) {
        this.ax = ax;
    }

    public double getAy() {
        return this.ay;
    }

    public void setAy(double ay) {
        this.ay = ay;
    }

    public JunctionState getJunctionState() {
        return this.junctionState;
    }

    public void setJunctionState(JunctionState junctionState) {
        this.junctionState = junctionState;
    }

    public ArrayList<Connector> getRelatedConnectors() {
        return this.relatedConnectors;
    }

    public void setRelatedConnectors(ArrayList<Connector> relatedConnectors) {
        this.relatedConnectors = relatedConnectors;
    }

    public void addToRelatedConnectors(Connector connector) {
        this.relatedConnectors.add(connector);
    }
}