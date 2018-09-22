package com.murabi10.fe;

/**
 * 固定され、変更できない加速度を表すクラスです。
 * @author 2SC1815
 *
 */

public class ConstantVelocity {

	protected double x, y;

	public ConstantVelocity(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	@Override
	public ConstantVelocity clone() {
		return new ConstantVelocity(this.x, this.y);
	}

}
