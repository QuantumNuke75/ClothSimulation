package main;

import java.util.ArrayList;

public class Cloth {
    //The distance between each Junction upon creation.
	private int junctionDistance = 15;

    //The number of Junction on each axis.
	private int junctionCountX;
	private int junctionCountY;

    //The starting coordinate of each axis of Junction.
	private int startJunctionX;
	private int startJunctionY = 50;

    //Acceleration altering variables.
	private float windStrengthX = 0.0f;
	private float gravityStrength = 0.3f;
	private float dampeningCoeff = 0.99f;

	//Delta Time
	double dT;

	//Other
	private double maxStress = 175;

	ArrayList<Connector> connectors = new ArrayList<Connector>();
	ArrayList<Junction> junctionsArrayList = new ArrayList<Junction>();

    /**
     *
     * @param junctionCountX
     * @param junctionCountY
     */
	public Cloth(int junctionCountX, int junctionCountY) {
		this.junctionCountX = junctionCountX;
		this.junctionCountY = junctionCountY;
		startJunctionX = 500 - (junctionCountX * junctionDistance) / 2;
		createJunctions();
		createConnectors();
	}

    /**
     *
     * @param dT
     */
	public void updateCloth(double dT) {
		for (Connector connector : this.connectors) {
			connector.update();
		}

		this.dT = dT;
		for(Junction junction : junctionsArrayList){
			if(junction.isMovable()) {
				junction.updatePosition(dT);
			}
		}
		removeBrokenConnectors();
	}

    /**
     *
     */
	public void createJunctions() {
		for(int i = 0; i < junctionCountX * junctionCountY; i++){
			int row = getRowFromNumber(i);
			int col = getColFromNumber(i);
			this.junctionsArrayList.add(new Junction(this, startJunctionX + (junctionDistance * row), startJunctionY + (junctionDistance * col),
					(col == 0 && (row == 0 || row == junctionCountY - 1 || row == junctionCountY / 2)) || (col == junctionCountX - 1 && (row == 0 || row == junctionCountY - 1)) ? false : true));
		}
	}

	public int getRowFromNumber(int num){
		return (int)Math.floor(num/junctionCountX);
	}

	public int getColFromNumber(int num){
		return num%junctionCountY;
	}

    /**
     *
     */
	public void createConnectors() {

		for(int i = 0; i < junctionCountX * junctionCountY; i++){

			Junction currentJunction = (Junction) junctionsArrayList.toArray()[i];

			if (getRowFromNumber(i) != junctionCountY - 1) {
				Connector connector = new Connector(this, currentJunction, (Junction) junctionsArrayList.toArray()[i+junctionCountY]);
				connectors.add(connector);
				currentJunction.getRelatedConnectors().add(connector);
				((Junction) junctionsArrayList.toArray()[i+junctionCountY]).getRelatedConnectors().add(connector);
			}
			if (getColFromNumber(i) != junctionCountX - 1) {
				Connector connector = new Connector(this, currentJunction, (Junction) junctionsArrayList.toArray()[i+1]);
				connectors.add(connector);
				currentJunction.getRelatedConnectors().add(connector);
				((Junction) junctionsArrayList.toArray()[i+1]).getRelatedConnectors().add(connector);
			}
		}
	}

    /**
     * Method to break all Junctions where the stress value is too high.
     */


//if the stress junction has any connectors that have a junction not connected with any other connectors
//assign that connector and keep it
	public void removeBrokenConnectors(){

		for(Object o : this.junctionsArrayList.toArray()) {
			Junction junction = (Junction) o;
			if (junction.totalStress > maxStress) {
				for(Connector relatedConnector : junction.getRelatedConnectors()){
					//Create a replacement Junction for every related Connector.
					Junction replacementJunction = new Junction(this, junction.getCurrentX(), junction.getCurrentY(), true);
					if(relatedConnector.getEndJunction().equals(junction)){
						relatedConnector.setEndJunction(replacementJunction);
						junctionsArrayList.add(replacementJunction);
					}
					else{
						relatedConnector.setStartJunction(replacementJunction);
						junctionsArrayList.add(replacementJunction);
					}
				}
				//Remove stressed Junction from existence
				junctionsArrayList.remove(junction);
			}
		}
	}

	public int getJunctionDistance() {
		return junctionDistance;
	}

	public void setJunctionDistance(int junctionDistance) {
		this.junctionDistance = junctionDistance;
	}

	public int getJunctionCountX() {
		return junctionCountX;
	}

	public void setJunctionCountX(int junctionCountX) {
		this.junctionCountX = junctionCountX;
	}

	public int getJunctionCountY() {
		return junctionCountY;
	}

	public void setJunctionCountY(int junctionCountY) {
		this.junctionCountY = junctionCountY;
	}

	public int getStartJunctionX() {
		return startJunctionX;
	}

	public void setStartJunctionX(int startJunctionX) {
		this.startJunctionX = startJunctionX;
	}

	public int getStartJunctionY() {
		return startJunctionY;
	}

	public void setStartJunctionY(int startJunctionY) {
		this.startJunctionY = startJunctionY;
	}

	public float getWindStrengthX() {
		return windStrengthX;
	}

	public void setWindStrengthX(float windStrengthX) {
		this.windStrengthX = windStrengthX;
	}

	public float getGravityStrength() {
		return gravityStrength;
	}

	public void setGravityStrength(float gravityStrength) {
		this.gravityStrength = gravityStrength;
	}

	public float getDampeningCoeff() {
		return dampeningCoeff;
	}

	public void setDampeningCoeff(float dampeningCoeff) {
		this.dampeningCoeff = dampeningCoeff;
	}

	public double getdT() {
		return dT;
	}

	public void setdT(double dT) {
		this.dT = dT;
	}

	public ArrayList<Connector> getConnectors() {
		return connectors;
	}

	public void setConnectors(ArrayList<Connector> connectors) {
		this.connectors = connectors;
	}
}