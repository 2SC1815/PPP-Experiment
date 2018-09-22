package com.murabi10.fe;

import java.util.ArrayList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

/**
 *
 * プレイヤーの移動を予測し、弾を撃ちます。
 * @author 2SC1815
 *
 */

public class Main extends BasicGame {

	public Main() {
		super("プレイヤーの移動を予測して弾を撃ちたい");
	}

	public static void main(String[] args) throws SlickException {

		AppGameContainer app = new AppGameContainer(new Main());

		app.setDisplayMode(ScrX, ScrY, false); // ディスプレイサイズを指定します。
		app.setShowFPS(true); /// FPS を表示します
		app.setTargetFrameRate(120 - 1); // FPS を 120 に維持する設定をします
		app.setMaximumLogicUpdateInterval(2); // update() が呼ばれる間隔を設定します。
		app.setMinimumLogicUpdateInterval(2);

		app.start(); // 処理開始
	}


	// 定数

	private static final int ScrX = 500, ScrY = 500; // 画面サイズ

	private static final double ACCELERATION_FACTOR = 0.0005; 	// 加速係数。大きければプレイヤーの加速が早くなる。
	private static final double DECELERATION_FACTOR = 0.0001; 	// 減速係数。大きければプレイヤーの減速が早くなる。
	private static final double MAX_ACCELERATION = 1.9; 		// プレイヤーの最大加速度。

	private static final int SHOOTING_INTERVAL = 200;			// 弾を撃つ間隔（ミリ秒）
	private static final int TURRET_X = ScrX / 2, TURRET_Y = ScrY / 4; // 弾を撃ってくる砲座の座標

	private static final double BULLET_SPEED = 0.67; 				// 弾の速度。1Tickで進む距離。
	// プレイヤーが弾の速度を超えて、砲座から離れる動きをするとどうやっても命中しません。
	// かといって、弾の速度を上げると予測する意味がなくなりますから、調整は必須です。


	// 変数

	private Player player = new Player(new Velocity(0, 0), ScrX / 2, (ScrY / 3) * 2);	// プレイヤーのインスタンス。

	private ArrayList<Bullet> bulletList = new ArrayList<Bullet>(); 	// 画面に存在する弾の処理リスト。
	private int shootingTimer = SHOOTING_INTERVAL;	// 射撃タイマー。

	private float pvx, pvy;	// デバッグ表示用の変数。実動作には不要。

	@Override
	public void init(GameContainer arg0) throws SlickException {}
	// 初期化処理は現状不要。

	@Override
	public void update(GameContainer arg0, int delta) throws SlickException {
		// delta は 最後に update() が呼ばれてからの経過時間（ミリ秒）です。

		// 射撃タイマーが0になったらタイマーをリセットし、弾を撃つ処理を開始します。

		if (shootingTimer <= 0) {

			shootingTimer = SHOOTING_INTERVAL; // タイマーリセット

			/*
			 ************************************
			 * 以下から弾を撃つ処理です。
			 ************************************
			 */

			double vx = player.x - TURRET_X; // プレイヤーと砲座の相対距離を計算します。
			double vy = player.y - TURRET_Y;

			int LandingRemain = 0; // 着弾までかかる秒数（ミリ秒）保持用の変数

			for (int i=0; i<1; i++) {
				// 着弾までの時間を計算します。
				// 砲座とプレイヤーの直線距離を計算し、砲弾速度で割ると算出できます。
				// ２回目は、着弾にかかる時間でプレイヤーが動いた距離を補正して計算します。
				LandingRemain = (int) ( Math.sqrt((vx*vx) + (vy*vy)) / BULLET_SPEED);
				// 算出した時間を使い、着弾までにかかる時間でプレイヤーがどこまで動くか計算します。
				// ２回目は、補正した時間でもう一回計算し、予想座標を算出します。
				vx = (player.x + (player.velocity.getX() *  LandingRemain)) - TURRET_X;
				vy = (player.y + (player.velocity.getY() *  LandingRemain)) - TURRET_Y;
			}

			// この処理は、予想座標からベクタ方向に変換しています。
			double length = Math.sqrt((vx*vx) + (vy*vy));
			vx /= length;
			vy /= length;

			pvx = (float) vx; // デバッグ用にベクタ方向変数をコピー。実動作には不要。
			pvy = (float) vy;

			// 弾速を掛けて、計算とおりの速度で動くようにします。
			vx *= BULLET_SPEED;
			vy *= BULLET_SPEED;

			// 加速度インスタンスを生成します。
			ConstantVelocity velocity = new ConstantVelocity(vx, vy);

			// 加速度を弾に適用し、砲座座標におきます。
			Bullet bullet = new Bullet(velocity, TURRET_X, TURRET_Y);

			// 弾を処理リストに追加します。
			bulletList.add(bullet);

			/*
			 ************************************
			 * 弾を撃つ処理おわり
			 ************************************
			 */

		}

		shootingTimer -= delta; // 射撃タイマーを減算します。


		/*
		 ****************************************
		 * 以下から画面をはみ出した弾の処理です。
		 ****************************************
		 */

		ArrayList<Bullet> destroyQueue = new ArrayList<Bullet>();
		// 弾の削除キュー リスト です。
		// リストをイテレートしているループ内でそのリストの要素を削除すると、例外が発生するため。

		for (Bullet bullet : bulletList) {

			bullet.x += bullet.velocity.getX();	// 弾を加速度に基づいて移動させます。
			bullet.y += bullet.velocity.getY();

			if ((bullet.x > ScrX || bullet.x < 0) || (bullet.y > ScrY || bullet.y < 0)) {
				destroyQueue.add(bullet);
				// 弾が画面端に到達したら、削除キューに追加します。
			}

		}

		// 実際に弾を削除します。
		for (Bullet bullet : destroyQueue) {
			bulletList.remove(bullet);
		}

		/*
		 ************************************
		 * 画面をはみ出した弾の処理おわり
		 ************************************
		 */

		/*
		 ************************************
		 * 以下からプレイヤーの移動処理です。
		 ************************************
		 */

		Input input = arg0.getInput(); // キー入力を取得するインスタンスを取得します

		final double acceleration = delta * ACCELERATION_FACTOR; // 加速係数の計算
		final double deceleration = delta * DECELERATION_FACTOR; // 減速係数の計算

		double oldx = player.velocity.getX(), oldy = player.velocity.getY(); // 加速されたかを確認するための変数です。

		if (input.isKeyDown(Input.KEY_UP))
			player.velocity.addY(-acceleration);	// プレイヤーの加速度に加速係数を足す。

		if (input.isKeyDown(Input.KEY_DOWN))
			player.velocity.addY(acceleration);		// 同上

		if (input.isKeyDown(Input.KEY_LEFT))
			player.velocity.addX(-acceleration);	// 同上

		if (input.isKeyDown(Input.KEY_RIGHT))
			player.velocity.addX(acceleration);		// 同上

		if (player.velocity.getX() == oldx) // X方向に加速されなかった場合、X方向減速します。
			player.velocity.addX(Math.signum(player.velocity.getX()) * -deceleration);

		if (player.velocity.getY() == oldy) // Y方向に加速されなかった場合、Y方向減速します。
			player.velocity.addY(Math.signum(player.velocity.getY()) * -deceleration);

		// 最大加速度より大きな加速度にならないようにします。
		player.velocity.setX(Math.abs(player.velocity.getX()) > MAX_ACCELERATION ? Math.signum(player.velocity.getX()) * MAX_ACCELERATION : player.velocity.getX());
		player.velocity.setY(Math.abs(player.velocity.getY()) > MAX_ACCELERATION ? Math.signum(player.velocity.getY()) * MAX_ACCELERATION : player.velocity.getY());

		player.x += player.velocity.getX();	// 加速度に基づいて移動させます。
		player.y += player.velocity.getY();

		// プレイヤーが画面の外に行かないようにします。また、画面端で跳ね返るようにします。
		if (player.x > ScrX || player.x < 0) {
			player.x = (player.x < 0) ? 0 : ScrX;
			//player.velocity.setX(0);
			player.velocity.setX(-player.velocity.getX());
		}

		if (player.y > ScrY || player.y < 0) {
			player.y = (player.y < 0) ? 0 : ScrY;
			//player.velocity.setY(0);
			player.velocity.setY(-player.velocity.getY());
		}


		/*
		 ************************************
		 * プレイヤーの移動処理おわり
		 ************************************
		 */
	}

	@Override
	public void render(GameContainer arg0, Graphics g) throws SlickException {
		/*
		 ************************************
		 * 以下から画面表示処理です。
		 ************************************
		 */

		g.drawString("x="+Math.round(player.x)+" y="+Math.round(player.y), 10, 30); // プレイヤー位置の表示
		g.drawString("vx="+pvx+" vy="+pvy, 10, 50); // 弾丸射出方向の表示

		g.fill(new Rectangle(TURRET_X - 10, TURRET_Y - 10, 20f, 20f)); // 砲座の表示

		g.fill(new Rectangle((float)player.x, (float)player.y, 10f, 10f)); // プレイヤーの表示

		for (Bullet bullet : bulletList) { // 弾のイテレート

			g.fill(new Rectangle((float)bullet.x, (float)bullet.y, 5f, 5f)); // 弾の表示

		}

		/*
		 ************************************
		 * 画面表示処理おわり
		 ************************************
		 */

	}

}
