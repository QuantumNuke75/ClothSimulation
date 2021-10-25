package main;

public class Connector {

    //The instance of the cloth.
    private final Cloth cloth;
    //First junction
    Junction junctionA;
    //End junction
    Junction junctionB;
    //Current length
    Double length;
    //Normal length
    Double normalLength = 20.0;

    /**
     * The constructor for a Connector.
     *
     * @param cloth     {@link Cloth} - The Cloth instance.
     * @param junctionA - The associated first Junction.
     * @param junctionB - The associated last Junction.
     */
    public Connector(Cloth cloth, Junction junctionA, Junction junctionB) {
        this.cloth = cloth;
        this.junctionA = junctionA;
        this.junctionB = junctionB;
    }

    /**
     * Calculates the new length of the Connector based on the distance between the associated Junctions.
     */
    public double recalculateLength() {
        //Sets this length to the calculated length between the two Junctions.
        this.length = Math.pow(Math.pow(junctionB.getCurrentX() - junctionA.getCurrentX(), 2) + Math.pow(junctionB.getCurrentY() - junctionA.getCurrentY(), 2), 0.5);
        return this.length;
    }

    /**
     * The update function. Sets the Connector's position based on the tension caused by the associated {@link Junction}.
     */
    public void update(double dT) {

        //get difference between two junctions
        double differenceX = this.junctionA.getCurrentX() - this.junctionB.getCurrentX();
        double differenceY = this.junctionA.getCurrentY() - this.junctionB.getCurrentY();

        //recalculate length
        this.recalculateLength();

        //percent difference from normal length
        double difference = (this.normalLength - this.length) / this.length;

        //calculate translation amount for junctions
        double translationX = differenceX * difference * Math.pow(0.5, 2);
        double translationY = differenceY * difference * Math.pow(0.5, 2);

        //update the coordinates for one of the connected junctions if the junction is permitted to move
        if (this.junctionA.getJunctionState() == JunctionState.NORMAL) {
            this.junctionA.setCurrentX(this.junctionA.getCurrentX() + translationX);
            this.junctionA.setCurrentY(this.junctionA.getCurrentY() + translationY);
        }

        //update the coordinates for one of the connected junctions if the junction is permitted to move
        if (this.junctionB.getJunctionState() == JunctionState.NORMAL) {
            this.junctionB.setCurrentX(this.junctionB.getCurrentX() - translationX);
            this.junctionB.setCurrentY(this.junctionB.getCurrentY() - translationY);
        }
    }


    /**
     * Getters and Setters
     */
    public Junction getJunctionA() {
        return this.junctionA;
    }

    public void setJunctionA(Junction junctionA) {
        this.junctionA = junctionA;
    }

    public Junction getJunctionB() {
        return this.junctionB;
    }

    public void setJunctionB(Junction junctionB) {
        this.junctionB = junctionB;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getNormalLength() {
        return this.normalLength;
    }

    public void setNormalLength(Double normalLength) {
        this.normalLength = normalLength;
    }
}
