package com.murabi10.fe;

/**
 *
 * 可変加速度を表すクラスです。
 * @author 2SC1815
 *
 */

public class Velocity extends ConstantVelocity {

	public Velocity(double x, double y) {
		super(x, y);
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void addX(double x) {
		this.x += x;
	}

	public void addY(double y) {
		this.y += y;
	}


	@Override
	public Velocity clone() {
		return new Velocity(this.x, this.y);
	}

}
