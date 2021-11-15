package main;

public class Connector {

    //First junction
    public Junction junctionA;

    //Second junction
    public Junction junctionB;

    //Current length
    public Double length;

    //Normal length
    public Double normalLength = 20.0;

    /**
     * The constructor for a Connector.
     *
     * @param junctionA - The associated first Junction.
     * @param junctionB - The associated last Junction.
     */
    public Connector(Junction junctionA, Junction junctionB) {
        this.junctionA = junctionA;
        this.junctionB = junctionB;
    }

    /**
     * Calculates the new length of the Connector based on the distance between the associated Junctions.
     */
    public double recalculateLength() {
        //Sets this length to the calculated length between the two Junctions.
        this.length = Math.pow(Math.pow(junctionB.currentPos.x - junctionA.currentPos.x, 2) + Math.pow(junctionB.currentPos.y - junctionA.currentPos.y, 2), 0.5);
        return this.length;
    }

    /**
     * The update function. Sets the Connector's position based on the tension caused by the associated {@link Junction}.
     */
    public void update(double dT) {


        Vector2D differenceVec = this.junctionA.currentPos.getSubtracted(junctionB.currentPos).getNormalized();

        junctionA.velocity.dot(differenceVec);
        junctionB.velocity.dot(differenceVec);

        double fsd = -Variables.springConstant*(normalLength-length)-Variables.dampeningFactor*(junctionA.velocity.getLength() - junctionB.velocity.getLength());

        junctionA.acceleration = differenceVec.getMultiplied(-fsd);
        junctionB.acceleration = differenceVec.getMultiplied(fsd);


        if (this.junctionA.junctionState == JunctionState.NORMAL) {
            junctionA.velocity.add(junctionA.acceleration.getMultiplied(dT));
            junctionA.currentPos.add(junctionA.velocity.getMultiplied(dT));
        }

        if (this.junctionB.junctionState == JunctionState.NORMAL) {
            junctionB.velocity.add(junctionB.acceleration.getMultiplied(dT));
            junctionB.currentPos.add(junctionB.velocity.getMultiplied(dT));
        }

//        Vector2D differenceVec = this.junctionA.currentPos.getSubtracted(junctionB.currentPos);
//
//        //recalculate length
//        this.recalculateLength();
//
//        //percent difference from normal length
//        double difference = (this.normalLength - this.length) / this.length;
//        Vector2D translationVec = new Vector2D(differenceVec.x * difference * 0.5, differenceVec.y * difference * 0.5);

        //current connector constraint calculation
//        this.recalculateLength();
//        Vector2D vectorBetween = junctionB.currentPos.getSubtracted(junctionA.currentPos);
//        Vector2D additionVector = vectorBetween.getMultiplied(1 - normalLength / length);
//        Vector2D additionVectorHalf = additionVector.getMultiplied(0.5);
//
//
//        //update the coordinates for one of the connected junctions if the junction is permitted to move
//        if (this.junctionA.junctionState == JunctionState.NORMAL) {
//            this.junctionA.currentPos.add(additionVectorHalf);
//        }
//
//        //update the coordinates for one of the connected junctions if the junction is permitted to move
//        if (this.junctionB.junctionState == JunctionState.NORMAL) {
//            this.junctionB.currentPos.subtract(additionVectorHalf);
//        }

    }
}
