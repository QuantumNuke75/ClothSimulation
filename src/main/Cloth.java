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
	private int startJunctionY = 100;

    //Acceleration altering variables.
	private float windStrengthX = 0.0f;
	private float gravityStrength = 0.3f;
	private float dampeningCoeff = 0.99f;

	//Delta Time
	double dT;

	//Other
	private double maxLength = 75;

	Junction[][] junctions;
	ArrayList<Connector> connectors = new ArrayList<Connector>();

    /**
     *
     * @param junctionCountX
     * @param junctionCountY
     */
	public Cloth(int junctionCountX, int junctionCountY) {
		this.junctionCountX = junctionCountX;
		this.junctionCountY = junctionCountY;
		startJunctionX = 500 - (junctionCountX * junctionDistance) / 2;
		junctions = new Junction[junctionCountX][junctionCountY];
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
		for (int i = 0; i < junctionCountX; i++) {
			for (int j = 0; j < junctionCountY; j++) {
				if (this.junctions[i][j] != null && this.junctions[i][j].isMovable()) {
					this.junctions[i][j].updatePosition(dT);
				}
			}
		}
		removeBrokenConnectors();
	}

    /**
     *
     */
	public void createJunctions() {
		for (int i = 0; i < junctionCountX; i++) {
			for (int j = 0; j < junctionCountY; j++) {
				this.junctions[i][j] = new Junction(this, startJunctionX + (junctionDistance * j), startJunctionY + (junctionDistance * i), j, i, (i == 0 && (j == 0 || j == junctionCountY - 1 || j == junctionCountY / 2)) || (i == junctionCountX - 1 && (j == 0 || j == junctionCountY - 1)) ? false : true);
			}
		}
	}

    /**
     *
     */
	public void createConnectors() {
		for (int i = 0; i < junctionCountX; i++) {
			for (int j = 0; j < junctionCountY; j++) {

				Junction currentJunction = junctions[i][j];

				if (j != junctionCountY - 1) {
					Connector connector = new Connector(this, currentJunction, junctions[i][j + 1]);
					connectors.add(connector);
					currentJunction.getRelatedConnectors().add(connector);
					junctions[i][j + 1].getRelatedConnectors().add(connector);
				}
				if (i != junctionCountX - 1) {
					Connector connector = new Connector(this, currentJunction, junctions[i + 1][j]);
					connectors.add(connector);
					currentJunction.getRelatedConnectors().add(connector);
					junctions[i + 1][j].getRelatedConnectors().add(connector);
				}
			}
		}
	}

    /**
     *
     */
	public void removeBrokenConnectors(){
		for(Object o : this.connectors.toArray()) {
			Connector connector = (Connector)o;
			if (connector.getLength() > maxLength) {

				connectors.remove(connector);

				Junction newJunction = new Junction(this, connector.endJunction.getCurrentX(), connector.endJunction.getCurrentY(), connector.endJunction.getArrayPosX(), connector.endJunction.getArrayPosY(), true);
				this.junctions[connector.endJunction.getArrayPosX()][connector.endJunction.getArrayPosY()] = newJunction;
				connector.endJunction = newJunction;
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

	public Junction[][] getJunctions() {
		return junctions;
	}

	public void setJunctions(Junction[][] junctions) {
		this.junctions = junctions;
	}

	public ArrayList<Connector> getConnectors() {
		return connectors;
	}

	public void setConnectors(ArrayList<Connector> connectors) {
		this.connectors = connectors;
	}
}