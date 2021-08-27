package main;

import java.util.ArrayList;

public class Junction {

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

	//Whether the Junction is immovable or not
	private boolean isMovable;

	public float totalStress;

	private ArrayList<Connector> relatedConnectors = new ArrayList<>();

	/**
	 *
	 * @param cloth {@link Cloth}
	 * @param posX
	 * @param posY
	 * @param isMovable
	 */
	public Junction(Cloth cloth, double posX, double posY, boolean isMovable) {
		this.cloth = cloth;
		this.currentX = posX;
		this.previousX = posX;
		this.currentY = posY;
		this.previousY = posY;
		this.ax = 0;
		this.ay = this.cloth.getGravityStrength();
		this.isMovable = isMovable;
	}

	/**
	 *
	 * @param dT
	 */
	public void updatePosition(double dT) {
		if (this.isMovable) {
			//Calculates new position.
			double tempX = this.currentX + cloth.getDampeningCoeff() * ((this.currentX - this.previousX) + 0.5 * this.ax * dT * dT);
			double tempY = this.currentY + cloth.getDampeningCoeff() * ((this.currentY - this.previousY) + 0.5 * cloth.getGravityStrength() * dT * dT);

			//Set previous coordinates.
			this.previousX = this.currentX;
			this.previousY = this.currentY;

			//Setting current position to calculated position.
			this.currentX = tempX;
			this.currentY = tempY;


			//Check for out of bounds on the y-axis.
			if (this.currentY > Start.INSTANCE.clothSimulation.getHeight()) {
				this.currentY = Start.INSTANCE.clothSimulation.getHeight();
			}			
			else if (this.currentY < 0) {
				this.currentY = 0;
			}

			//Check for out of bounds on the x-axis.
			if (this.currentX > Start.INSTANCE.clothSimulation.getWidth()) {
				this.currentX = Start.INSTANCE.clothSimulation.getWidth();
			}			
			else if (this.currentX < 0) {
				this.currentX = 0;
			}

			totalStress = 0;
			for(Connector connector: relatedConnectors){
				totalStress += connector.length;
			}

		}
	}

	public Cloth getCloth() {
		return cloth;
	}

	public void setCloth(Cloth cloth) {
		this.cloth = cloth;
	}

	public double getCurrentX() {
		return currentX;
	}

	public void setCurrentX(double currentX) {
		this.currentX = currentX;
	}

	public double getCurrentY() {
		return currentY;
	}

	public void setCurrentY(double currentY) {
		this.currentY = currentY;
	}

	public double getPreviousX() {
		return previousX;
	}

	public void setPreviousX(double previousX) {
		this.previousX = previousX;
	}

	public double getPreviousY() {
		return previousY;
	}

	public void setPreviousY(double previousY) {
		this.previousY = previousY;
	}

	public double getAx() {
		return ax;
	}

	public void setAx(double ax) {
		this.ax = ax;
	}

	public double getAy() {
		return ay;
	}

	public void setAy(double ay) {
		this.ay = ay;
	}

	public boolean isMovable() {
		return isMovable;
	}

	public void setMovable(boolean movable) {
		isMovable = movable;
	}

	public ArrayList<Connector> getRelatedConnectors() {
		return relatedConnectors;
	}

	public void setRelatedConnectors(ArrayList<Connector> relatedConnectors) {
		this.relatedConnectors = relatedConnectors;
	}
}