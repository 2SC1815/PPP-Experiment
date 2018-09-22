package com.murabi10.fe;

/**
 * 弾のクラスです。固定加速度と座標のフィールドを持っています。
 *
 * @author 2SC1815
 *
 */

public class Bullet {

	public Bullet(ConstantVelocity v, double x, double y) {
		this.velocity = v;
		this.x = x;
		this.y = y;
	}

	public ConstantVelocity velocity;
	public double x, y;

}
