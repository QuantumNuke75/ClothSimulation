package main;

import java.util.ArrayList;

public class Junction {

    //The total stress of the Junction.
    public float totalStress;

    //Position related variables.
    public Vector2D currentPos;
    public Vector2D previousPos;

    //Acceleration Related Variables
    public Vector2D acceleration;

    public Vector2D velocity;

    //The current state of the particle
    public JunctionState junctionState;

    //An ArrayList of all the connected Connectors.
    public ArrayList<Connector> relatedConnectors = new ArrayList<>();

    //Mass of the particle
    public float mass = 1f;

    public int row;
    public int col;

    /**
     * @param x             - The given x position of the Junction.
     * @param y             - The given y position of the Junction.
     * @param junctionState - The given state of the particle.
     */
    public Junction(double x, double y, JunctionState junctionState) {
        this.currentPos = new Vector2D(x, y);
        this.previousPos = new Vector2D(x, y);
        this.acceleration = new Vector2D(0, -Variables.gravityStrength);
        this.velocity = new Vector2D();
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

            // Vertlet-like with dampening
            acceleration = new Vector2D(Variables.newWindStrengthX, Variables.gravityStrength);
            Vector2D tempPos = currentPos;
            currentPos = currentPos.getAdded( (currentPos.getSubtracted(previousPos)).getMultiplied(Variables.dampeningCoeff)
                    .getAdded(acceleration.getMultiplied(dT * dT)) );
            previousPos = tempPos;


            // Vertlet integration
/*            acceleration = new Vector2D(Variables.newWindStrengthX, Variables.gravityStrength);
            Vector2D prev = new Vector2D(this.currentPos.x, this.currentPos.y);
            currentPos.x = currentPos.x * 2 - this.previousPos.x + acceleration.x * (dT * dT);
            currentPos.y = currentPos.y * 2 - this.previousPos.y + acceleration.y * (dT * dT);

            this.previousPos.x = prev.x;
            this.previousPos.y = prev.y;
*/


            // Spring Mass System
/*            ArrayList<Junction> connectedNeighbors = new ArrayList<>();
            for(Connector connector : relatedConnectors){
                if(connector.junctionA != this){
                    connectedNeighbors.add(connector.junctionA);
                }
                else{
                    connectedNeighbors.add(connector.junctionB);
                }
            }

            acceleration = new Vector2D();
            Vector2D tempPos = currentPos;
            for(Junction neighbor : connectedNeighbors){
                Vector2D dX = neighbor.currentPos.getSubtracted(this.currentPos);
                acceleration.add(dX.getMultiplied(Variables.springConstant/mass).getMultiplied(1 - (20/dX.getLength())));
            }
            acceleration.add(Variables.newWindStrengthX, Variables.gravityStrength);
            velocity.add(acceleration.getMultiplied(dT));
            currentPos.add(velocity.getMultiplied(dT));
            previousPos = tempPos;
*/


            // Euler
/*          acceleration = new Vector2D(Variables.newWindStrengthX, Variables.gravityStrength);
          velocity.add(acceleration.getMultiplied(dT));
          currentPos.add(velocity.getMultiplied(dT));
*/

            //Check for out of bounds on the y-axis.
            if (this.currentPos.y > Variables.SIMULATION_HEIGHT) {
                this.currentPos.y = Variables.SIMULATION_HEIGHT;
            } else if (this.currentPos.y < 0) {
                this.currentPos.y = 0;
            }

            //Check for out of bounds on the x-axis.
            if (this.currentPos.x > Variables.SIMULATION_WIDTH) {
                this.currentPos.x = Variables.SIMULATION_WIDTH;
            } else if (this.currentPos.x < 0) {
                this.currentPos.x = 0;
            }

            //resets total stress in order to recalculate
            this.totalStress = 0;
            //calculates total stress
            for (Connector connector : this.relatedConnectors) {
                this.totalStress += connector.length;
            }
        }
    }

    public String toString() {
        return "(" + this.col + ", " + this.row + ")";
    }

}