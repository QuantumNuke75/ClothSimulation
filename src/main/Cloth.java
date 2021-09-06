package main;

import java.util.ArrayList;

public class Cloth {
    //The distance between each Junction upon creation.
	private int junctionDistance = 20;

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
	private double dT;

	//The maximum stress of a given Junction.
	private double maxStress = 1000;

	//List of all Connectors.
	ArrayList<Connector> connectors = new ArrayList<Connector>();

	//List of all Junctions.
	ArrayList<Junction> junctionsArrayList = new ArrayList<Junction>();

    /**
     * Initialization of the Cloth.
	 *
     * @param junctionCountX - The number of horizontal Junctions.
     * @param junctionCountY - The number of vertical Junctions.
     */
	public Cloth(int junctionCountX, int junctionCountY) {
		this.junctionCountX = junctionCountX;
		this.junctionCountY = junctionCountY;
		startJunctionX = 500 - (junctionCountX * junctionDistance) / 2;
		createJunctions();
		createConnectors();
	}

    /**
	 * Updates all the cloth physics, including Connectors, Junctions, and ripping.
     *
     * @param dT - The given delta time.
     */
	public void updateCloth(double dT) {
		this.dT = dT;

		for (Connector connector : this.connectors) {
			connector.update();
		}

		for(Junction junction : this.junctionsArrayList){

			//Only if the JunctionState is normal.
			if(junction.getJunctionState() == JunctionState.NORMAL) {
				junction.update(dT);
			}
		}
		removeBrokenConnectors();
	}

    /**
     * Initializes all the initial Junctions.
     */
	public void createJunctions() {
		for(int i = 0; i < this.junctionCountX * this.junctionCountY; i++){
			int row = getRowFromNumber(i);
			int col = getColFromNumber(i);
			this.junctionsArrayList.add(new Junction(this, this.startJunctionX + (this.junctionDistance * row), this.startJunctionY + (this.junctionDistance * col), i,
					(col == 0 && (row == 0 || row == this.junctionCountY - 1 || row == this.junctionCountY / 2)) || (col == this.junctionCountX - 1 && (row == 0 || row == this.junctionCountY - 1)) ? JunctionState.ANCHOR : JunctionState.NORMAL));
		}
	}

	/**
	 * Calculates the row from a given index.
	 *
	 * @param num - The given index.
	 * @returns the row.
	 */
	public int getRowFromNumber(int num){
		return (int)Math.floor(num/this.junctionCountX);
	}

	/**
	 * Calculates the column from a given index.
	 *
	 * @param num - The given index.
	 * @returns the column.
	 */
	public int getColFromNumber(int num){
		return num%this.junctionCountY;
	}

    /**
     * Initialized all the initial Connectors.
     */
	public void createConnectors() {

		for(int i = 0; i < this.junctionCountX * this.junctionCountY; i++){

			Junction currentJunction = (Junction) this.junctionsArrayList.toArray()[i];

			if (getRowFromNumber(i) != this.junctionCountY - 1) {
				Connector connector = new Connector(this, currentJunction, (Junction) this.junctionsArrayList.toArray()[i+this.junctionCountY]);
				this.connectors.add(connector);
				currentJunction.getRelatedConnectors().add(connector);
				((Junction) this.junctionsArrayList.toArray()[i+this.junctionCountY]).getRelatedConnectors().add(connector);
			}
			if (getColFromNumber(i) != this.junctionCountX - 1) {
				Connector connector = new Connector(this, currentJunction, (Junction) this.junctionsArrayList.toArray()[i+1]);
				this.connectors.add(connector);
				currentJunction.getRelatedConnectors().add(connector);
				((Junction) this.junctionsArrayList.toArray()[i+1]).getRelatedConnectors().add(connector);
			}
		}
	}




//if the stress junction has any connectors that have a junction not connected with any other connectors
//assign that connector and keep it
	/**
	 * Method to break all Junctions where the stress value is too high.
	 */
	public void removeBrokenJunctions(){

		//Loop through all Junction without running into a {@link java.util.ConcurrentModificationException}
		for(Object o : this.junctionsArrayList.toArray()) {
			Junction junction = (Junction) o;

			//If the Junction's stress is greater that the permitted stress.
			if (junction.totalStress > this.maxStress) {

				//For every related Connector to the Junction.
				for(Connector relatedConnector : junction.getRelatedConnectors()){

					//Create a replacement Junction for every related Connector.
					Junction replacementJunction = new Junction(this, junction.getCurrentX(), junction.getCurrentY(), this.junctionsArrayList.size(), JunctionState.NORMAL);

					//Replace the end Junction if the Junctions are equal.
					if(relatedConnector.getEndJunction().equals(junction)){
						relatedConnector.setEndJunction(replacementJunction);
						this.junctionsArrayList.add(replacementJunction);
					}
					//Replace the start Junction if the Junctions are equal.
					else{
						relatedConnector.setStartJunction(replacementJunction);
						this.junctionsArrayList.add(replacementJunction);
					}
				}
				//Remove stressed Junction from still being where it was.
				junction.getRelatedConnectors().clear();;
			}
		}
	}

	/**
	 * Method to break all Connectors where the stress value is too high.
	 */
	public void removeBrokenConnectors(){

		//Loop through all Junction without running into a {@link java.util.ConcurrentModificationException}
		for(Object o : this.connectors.toArray()) {
			Connector connector = (Connector) o;

			if(connector.length > maxStress){
				connectors.remove(connector);
			}
		}
	}

	public int getJunctionDistance() {
		return this.junctionDistance;
	}

	public void setJunctionDistance(int junctionDistance) {
		this.junctionDistance = junctionDistance;
	}

	public int getJunctionCountX() {
		return this.junctionCountX;
	}

	public void setJunctionCountX(int junctionCountX) {
		this.junctionCountX = junctionCountX;
	}

	public int getJunctionCountY() {
		return this.junctionCountY;
	}

	public void setJunctionCountY(int junctionCountY) {
		this.junctionCountY = junctionCountY;
	}

	public int getStartJunctionX() {
		return this.startJunctionX;
	}

	public void setStartJunctionX(int startJunctionX) {
		this.startJunctionX = startJunctionX;
	}

	public int getStartJunctionY() {
		return this.startJunctionY;
	}

	public void setStartJunctionY(int startJunctionY) {
		this.startJunctionY = startJunctionY;
	}

	public float getWindStrengthX() {
		return this.windStrengthX;
	}

	public void setWindStrengthX(float windStrengthX) {
		this.windStrengthX = windStrengthX;
	}

	public float getGravityStrength() {
		return this.gravityStrength;
	}

	public void setGravityStrength(float gravityStrength) {
		this.gravityStrength = gravityStrength;
	}

	public float getDampeningCoeff() {
		return this.dampeningCoeff;
	}

	public void setDampeningCoeff(float dampeningCoeff) {
		this.dampeningCoeff = dampeningCoeff;
	}

	public double getdT() {
		return this.dT;
	}

	public void setdT(double dT) {
		this.dT = dT;
	}

	public ArrayList<Connector> getConnectors() {
		return this.connectors;
	}

	public void setConnectors(ArrayList<Connector> connectors) {
		this.connectors = connectors;
	}

	public double getMaxStress() {
		return this.maxStress;
	}

	public void setMaxStress(double maxStress) {
		this.maxStress = maxStress;
	}
}