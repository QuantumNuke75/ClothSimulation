package main;

import java.util.ArrayList;
import java.util.HashMap;
//
public class Cloth {
	private int particleDistance = 10;
	private int particleCountX;
	private int particleCountY;

	private int startParticleX;
	private int startParticleY = 100;

	private float windStrengthX = 0.0f;
	private float gravityStrength = 0.3f;
	private float dampeningCoeff = 0.99f;



	double dT;

	Junction[][] junctions;
	ArrayList<Connector> connectors = new ArrayList<Connector>();


	public Cloth(int particleCountX, int particleCountY) {
		this.particleCountX = particleCountX;
		this.particleCountY = particleCountY;
		startParticleX = 500 - (particleCountX * particleDistance) / 2;
		junctions = new Junction[particleCountX][particleCountY];
		createJunctions();
		createConnectors();
	}

	public void updateCloth(double dT) {
		for (Connector connector : this.connectors) {
				connector.update();
		}

		this.dT = dT;
		for (int i = 0; i < particleCountX; i++) {
			for (int j = 0; j < particleCountY; j++) {
				if (this.junctions[i][j] != null && this.junctions[i][j].isMovable()) {
					this.junctions[i][j].updatePosition(dT);
				}
			}
		}

		removeBrokenConnectors();

	}

	public void createJunctions() {
		for (int i = 0; i < particleCountX; i++) {
			for (int j = 0; j < particleCountY; j++) {
				this.junctions[i][j] = new Junction(this,startParticleX + (particleDistance * j), startParticleY + (particleDistance * i), (i == 0 && (j == 0 || j == particleCountY - 1 || j == particleCountY / 2)) || (i == particleCountX - 1 && (j == 0 || j == particleCountY - 1)) ? false : true);
			}
		}
	}

	public void createConnectors() {
		for (int i = 0; i < particleCountX; i++) {
			for (int j = 0; j < particleCountY; j++) {

				Junction currentJunction = junctions[i][j];


				if (j != particleCountY - 1) {
					Connector con = new Connector(this, currentJunction, junctions[i][j + 1]);
					connectors.add(con);
				}

				if (i != particleCountX - 1) {
					Connector con = new Connector(this, currentJunction, junctions[i + 1][j]);
					connectors.add(con);
				}
			}
		}
	}

	//check if the junction HAS NO CONNECTORS

	public void removeBrokenConnectors(){
		for(Object o : this.connectors.toArray()) {

			Connector connector = (Connector)o;

			if (connector.getLength() > 100) {
				connectors.remove(connector);

			}
		}
	}

	public int getParticleDistance() {
		return particleDistance;
	}

	public void setParticleDistance(int particleDistance) {
		this.particleDistance = particleDistance;
	}

	public int getParticleCountX() {
		return particleCountX;
	}

	public void setParticleCountX(int particleCountX) {
		this.particleCountX = particleCountX;
	}

	public int getParticleCountY() {
		return particleCountY;
	}

	public void setParticleCountY(int particleCountY) {
		this.particleCountY = particleCountY;
	}

	public int getStartParticleX() {
		return startParticleX;
	}

	public void setStartParticleX(int startParticleX) {
		this.startParticleX = startParticleX;
	}

	public int getStartParticleY() {
		return startParticleY;
	}

	public void setStartParticleY(int startParticleY) {
		this.startParticleY = startParticleY;
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