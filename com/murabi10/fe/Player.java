package com.murabi10.fe;

/**
 *
 * プレイヤーのクラスです。可変加速度と座標のフィールドを持っています。
 *
 * @author 2SC1815
 *
 */
public class Player {

	public Player(Velocity v, double x, double y) {
		this.velocity = v;
		this.x = x;
		this.y = y;
	}

	public Velocity velocity;
	public double x, y;

}
