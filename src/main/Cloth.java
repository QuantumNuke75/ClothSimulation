package main;

import java.util.ArrayList;
import java.util.Arrays;

public class Cloth {
    //The starting coordinate of each axis of Junction.
    public int startJunctionX;
    public int startJunctionY = 50; //implement fix for vertical scaling

    //List of all Connectors.
    ArrayList<Connector> connectorArrayList = new ArrayList<>();
    //List of all Junctions.
    ArrayList<Junction> junctionsArrayList = new ArrayList<>();

    /*

    [
    [0,0,0,0,0]
    [0,0,0,0,0]
    [0,0,0,0,0]
    [0,0,0,0,0]
    [0,0,0,0,0]
    ]


     */

    /**
     * Initialization of the Cloth.
     */
    public Cloth() {
        this.startJunctionX = Variables.SIMULATION_WIDTH / 2 - (Variables.JUNCTION_COUNT_X * Variables.JUNCTION_DISTANCE) / 2;
        createJunctions();
        createConnectors();
    }

    /**
     * Updates all the cloth physics, including Connectors, Junctions, and ripping.
     */
    public void updateCloth() {
        //Update the wind
        if (Variables.windStrengthX > 0) {
            Variables.newWindStrengthX = (float) ((Variables.windStrengthX + 0.05f * Math.sin(.001 * System.currentTimeMillis())) + 0.05);
        } else if (Variables.windStrengthX < 0) {
            Variables.newWindStrengthX = (float) ((Variables.windStrengthX - 0.05f * Math.sin(.001 * System.currentTimeMillis())) + 0.05);
        } else {
            Variables.newWindStrengthX = 0;
        }

        //Update all the connectors in order
        for (Connector connector : this.connectorArrayList)  {
            connector.update(Variables.dT);
        }

        //Update all the junctions in order
        for (Junction junction : this.junctionsArrayList) {
            //Only if the JunctionState is normal.
            if (junction != null && junction.junctionState == JunctionState.NORMAL) {
                junction.update(Variables.dT);
            }
        }

        //Methods for breaking the cloth
        removeBrokenConnectors(); //still a bit broken for shading mode
    }

    /**
     * Initializes all the initial Junctions.
     */
    public void createJunctions() {
        // col/x
        for(int i = 0; i < Variables.JUNCTION_COUNT_Y; i++){
            //row/y
            for(int j = 0; j < Variables.JUNCTION_COUNT_X; j++){
                Junction junction = new Junction(this.startJunctionX + (Variables.JUNCTION_DISTANCE * j), this.startJunctionY + (Variables.JUNCTION_DISTANCE * i), (i == 0 && (j == 0 || j == Variables.JUNCTION_COUNT_X - 1 || j == Variables.JUNCTION_COUNT_X / 2)) || (i == Variables.JUNCTION_COUNT_Y - 1 && (j == 0 || j == Variables.JUNCTION_COUNT_X - 1)) ? JunctionState.ANCHOR : JunctionState.NORMAL);
                this.junctionsArrayList.add(junction);
                junction.row = j;
                junction.col = i;
            }
        }
    }

    /**
     * Calculates the row from a given index.
     *
     * @param num - The given index.
     * @returns the row.
     */
    public int getRowFromNumber(int num) {return (int) Math.floor( num / Variables.JUNCTION_COUNT_Y);}

    /**
     * Calculates the column from a given index.
     *
     * @param num - The given index.
     * @returns the column.
     */
    public int getColFromNumber(int num) {
        return num % Variables.JUNCTION_COUNT_X;
    }

    /**
     * Initialized all the initial Connectors.
     */
    public void createConnectors() {

        Junction[][] juncArr = new Junction[Variables.JUNCTION_COUNT_Y][Variables.JUNCTION_COUNT_X];
        int row = 0;
        int col = 0;
        for(int i = 0; i < Variables.JUNCTION_COUNT_Y * Variables.JUNCTION_COUNT_X; i++){

            juncArr[col][row] = junctionsArrayList.get(i);

            row++;
            if(row >= Variables.JUNCTION_COUNT_X){
                col++;
                row = 0;
            }
        }

        for(int i = 0; i < juncArr.length; i++){ // col
            for(int j = 0; j < juncArr[i].length; j++){ //row
                if(i < juncArr.length - 1){
                    Connector connector = new Connector(juncArr[i][j], juncArr[i+1][j]);
                    juncArr[i][j].relatedConnectors.add(connector);
                    juncArr[i+1][j].relatedConnectors.add(connector);
                    connectorArrayList.add(connector);
                }
                if(j < juncArr[i].length - 1){
                    Connector connector = new Connector(juncArr[i][j], juncArr[i][j+1]);
                    juncArr[i][j].relatedConnectors.add(connector);
                    juncArr[i][j+1].relatedConnectors.add(connector);
                    connectorArrayList.add(connector);
                }
            }
        }


        /*
        for (int i = 0; i < Variables.JUNCTION_COUNT_X * Variables.JUNCTION_COUNT_Y; i++) {
            Junction currentJunction = (Junction) this.junctionsArrayList.toArray()[i];

            if (getRowFromNumber(i) != Variables.JUNCTION_COUNT_Y - 1) {
                    Connector connector = new Connector(currentJunction, (Junction) this.junctionsArrayList.toArray()[i + Variables.JUNCTION_COUNT_Y]);
                    this.connectorArrayList.add(connector);
                    currentJunction.relatedConnectors.add(connector);
                    ((Junction) this.junctionsArrayList.toArray()[i + Variables.JUNCTION_COUNT_Y]).relatedConnectors.add(connector);
            }
            if (getColFromNumber(i) != Variables.JUNCTION_COUNT_X - 1) {
                    Connector connector = new Connector(currentJunction, (Junction) this.junctionsArrayList.toArray()[i + 1]);
                    this.connectorArrayList.add(connector);
                    currentJunction.relatedConnectors.add(connector);
                    ((Junction) this.junctionsArrayList.toArray()[i + 1]).relatedConnectors.add(connector);
            }
        }*/
    }

    /**
     * Method to break all Junctions where the stress value is too high.
     */
    public void removeBrokenJunctions() {

        //Loop through all Junction without running into a {@link java.util.ConcurrentModificationException}
        for (Object o : this.junctionsArrayList.toArray()) {
            Junction junction = (Junction) o;

            //If the Junction's stress is greater that the permitted stress.
            if (junction.totalStress > Variables.maxStress) {

                //For every related Connector to the Junction.
                for (Connector relatedConnector : junction.relatedConnectors) {

                    //Create a replacement Junction for every related Connector.
                    Junction replacementJunction = new Junction(junction.currentPos.x, junction.currentPos.y, JunctionState.NORMAL);

                    //Replace the end Junction if the Junctions are equal.
                    if (relatedConnector.junctionB.equals(junction)) {
                        relatedConnector.junctionB = replacementJunction;
                        this.junctionsArrayList.add(replacementJunction);
                    }
                    //Replace the start Junction if the Junctions are equal.
                    else {
                        relatedConnector.junctionA = replacementJunction;
                        this.junctionsArrayList.add(replacementJunction);
                    }
                }
                //Remove stressed Junction from still being where it was.
                junction.relatedConnectors.clear();
            }
        }
    }

    /**
     * Method to break all Connectors where the stress value is too high.
     */
    public void removeBrokenConnectors() {

        //Loop through all Junction without running into a {@link java.util.ConcurrentModificationException}
        for (Object o : this.connectorArrayList.toArray()) {
            Connector connector = (Connector) o;

            if (connector.length > Variables.maxStress) {
                this.connectorArrayList.remove(connector);

                //Get midpoint of broken connector
                double midX = (connector.junctionA.currentPos.x + connector.junctionB.currentPos.x) / 2;
                double midY = (connector.junctionA.currentPos.y + connector.junctionB.currentPos.y) / 2;

                //Create two new junction to add as an endpoint for the replacement connectors and sets
                //the default length to half the length of the original connector
                Junction junction1 = new Junction(midX, midY, JunctionState.NORMAL);
                Junction junction2 = new Junction(midX, midY, JunctionState.NORMAL);

                //Adds the new junctions to the junction @link{ArrayList}
                this.junctionsArrayList.add(junction1);
                this.junctionsArrayList.add(junction2);

                //Creates two new connectors as replacements for the broken connector
                //Also sets the length of the new connectors to half that of the broken connector
                // in order to simulate tearing
                Connector connector1 = new Connector(connector.junctionA, junction1);
                connector1.normalLength = connector1.normalLength / 2;
                connector1.length = connector1.normalLength;

                Connector connector2 = new Connector(connector.junctionB, junction2);
                connector2.normalLength = connector2.normalLength / 2;
                connector2.length = connector2.normalLength;

                //Adds the new connectors to the connector @link{ArrayList}
                this.connectorArrayList.add(connector1);
                this.connectorArrayList.add(connector2);

                connector.junctionA.relatedConnectors.remove(connector);
                connector.junctionB.relatedConnectors.remove(connector);
            }
        }
    }
}