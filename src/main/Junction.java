package main;

import java.util.ArrayList;

public class Junction {

    //The total stress of the Junction.
    public float totalStress;

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

    //An ArrayList of all the connected Connectors.
    private ArrayList<Connector> relatedConnectors = new ArrayList<>();

    /**
     * @param x             - The given x position of the Junction.
     * @param y             - The given y position of the Junction.
     * @param junctionState - The given state of the particle.
     */
    public Junction(double x, double y, JunctionState junctionState) {
        this.currentX = x;
        this.previousX = x;
        this.currentY = y;
        this.previousY = y;
        this.ax = 0;
        this.ay = Variables.gravityStrength;
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
            //Calculates new position. (Current pos + dampening * (displacement + forces*dT))
            double tempX = this.currentX + Variables.dampeningCoeff * ((this.currentX - this.previousX) + /*0.5 **/ Variables.newWindStrengthX * Math.pow(dT, 2));
            double tempY = this.currentY + Variables.dampeningCoeff * ((this.currentY - this.previousY) + /*0.5 **/ Variables.gravityStrength * Math.pow(dT, 2));

            //Set previous coordinates.
            this.previousX = this.currentX;
            this.previousY = this.currentY;

            //Setting current position to calculated position.
            this.currentX = tempX;
            this.currentY = tempY;

            //Check for out of bounds on the y-axis.
            if (this.currentY > Variables.SIMULATION_HEIGHT) {
                this.currentY = Variables.SIMULATION_HEIGHT;
            }
            else if (this.currentY < 0) {
                this.currentY = 0;
            }

            //Check for out of bounds on the x-axis.
            if (this.currentX > Variables.SIMULATION_WIDTH) {
                this.currentX = Variables.SIMULATION_WIDTH;
            }
            else if (this.currentX < 0) {
                this.currentX = 0;
            }

            //resets total stress in order to recalculate
            this.totalStress = 0;
            //calculates total stress
            for (Connector connector : this.relatedConnectors) {
                this.totalStress += connector.length;
            }
        }
    }

    /**
     * Getters and Setters
     */
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