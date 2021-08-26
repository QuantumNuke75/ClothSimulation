package main;

public class Connector {

	//The instance of the cloth.
	private Cloth cloth;

	Junction startJunction;
	Junction endJunction;
	Double length;
	Double normalLength = 10.0;

	/**
	 *
	 * @param cloth {@link Cloth}
	 * @param start
	 * @param end
	 */
	public Connector(Cloth cloth, Junction start, Junction end) {
		this.cloth = cloth;
		this.startJunction = start;
		this.endJunction = end;
	}

	/**
	 *
	 * @return
	 */
	public Double getLength() {
		this.length = Math.pow(Math.pow(endJunction.getCurrentX() - startJunction.getCurrentX(), 2)
				+ Math.pow(endJunction.getCurrentY() - startJunction.getCurrentY(), 2), 0.5);
		return length;
	}

	/**
	 *
	 */
	public void update() {
		double differenceX = startJunction.getCurrentX() - endJunction.getCurrentX();
		double differenceY = startJunction.getCurrentY() - endJunction.getCurrentY();

		this.getLength();
		double diffS = (normalLength - this.length) / this.length;
		
		// changes material properties    
		// 0.05   0.5 normal
		// 1 1 glitch
		double translationX = differenceX * 0.5 * diffS * 0.5;
		double translationY = differenceY * 0.5 * diffS * 0.5;

			if (startJunction.isMovable()) {
				this.startJunction.setCurrentX(this.startJunction.getCurrentX() + translationX);
				this.startJunction.setCurrentY(this.startJunction.getCurrentY() + translationY);
			}

			if (endJunction.isMovable()) {
				this.endJunction.setCurrentX(this.endJunction.getCurrentX() - translationX);
				this.endJunction.setCurrentY(this.endJunction.getCurrentY() - translationY);
			}
	}

	public Junction getStartJunction() {
		return startJunction;
	}

	public void setStartJunction(Junction startJunction) {
		this.startJunction = startJunction;
	}

	public Junction getEndJunction() {
		return endJunction;
	}

	public void setEndJunction(Junction endJunction) {
		this.endJunction = endJunction;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public Double getNormalLength() {
		return normalLength;
	}

	public void setNormalLength(Double normalLength) {
		this.normalLength = normalLength;
	}
}
