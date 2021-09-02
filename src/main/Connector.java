package main;

public class Connector {

	//The instance of the cloth.
	private Cloth cloth;

	Junction startJunction;
	Junction endJunction;
	Double length;
	Double normalLength = 10.0;

	/**
	 * The construtor for a Connector.
	 *
	 * @param cloth {@link Cloth} - The Cloth instance.
	 * @param start - The associated "starting" Junction.
	 * @param end - The associated "ending" Junction.
	 */
	public Connector(Cloth cloth, Junction start, Junction end) {
		this.cloth = cloth;
		this.startJunction = start;
		this.endJunction = end;
	}

	/**
	 * Calculates the new length of the Connector based on the distance between the associated Junctions.
	 */
	public double recalculateLength() {
		//Sets this length to the calculated length between the two Junctions.
		this.length = Math.pow(Math.pow(endJunction.getCurrentX() - startJunction.getCurrentX(), 2) + Math.pow(endJunction.getCurrentY() - startJunction.getCurrentY(), 2), 0.5);
		return this.length;
	}

	/**
	 * The update function. Sets the Connector's position based on the tension caused by the associated {@link Junction}.
	 */
	public void update() {

		double differenceX = this.startJunction.getCurrentX() - this.endJunction.getCurrentX();
		double differenceY = this.startJunction.getCurrentY() - this.endJunction.getCurrentY();

		this.recalculateLength();
		double difference = (this.normalLength - this.length) / this.length;

		double translationX = differenceX * difference * 0.5 * 0.5;
		double translationY = differenceY * difference * 0.5 * 0.5;

		if (this.startJunction.getJunctionState() == JunctionState.NORMAL) {
			this.startJunction.setCurrentX(this.startJunction.getCurrentX() + translationX);
			this.startJunction.setCurrentY(this.startJunction.getCurrentY() + translationY);
		}

		if (this.endJunction.getJunctionState() == JunctionState.NORMAL) {
			this.endJunction.setCurrentX(this.endJunction.getCurrentX() - translationX);
			this.endJunction.setCurrentY(this.endJunction.getCurrentY() - translationY);
		}
	}

	public Junction getStartJunction() {
		return this.startJunction;
	}

	public void setStartJunction(Junction startJunction) {
		this.startJunction = startJunction;
	}

	public Junction getEndJunction() {
		return this.endJunction;
	}

	public void setEndJunction(Junction endJunction) {
		this.endJunction = endJunction;
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
