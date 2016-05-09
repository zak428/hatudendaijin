package com.example.hatudendaijin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.TextView;

public class StartGameActivity extends Activity {

	private BgmPlayer bgm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startgame);

		// intent受け取り
		Intent i = getIntent();
		final int flag = i.getIntExtra("FLAG", 1);
		final String player = i.getStringExtra("PLAYER");// 名前管理用変数

		final TextView textview = (TextView) findViewById(R.id.chapter);

		if (flag == 1) {
			// プレイヤーの初期化
			this.bgm = new BgmPlayer(this, 7);
			textview.setText(getText(R.string.chap1));
		} else if (flag == 2) {
			// プレイヤーの初期化
			this.bgm = new BgmPlayer(this, 7);
			textview.setText(getText(R.string.chap2));
		} else if (flag == 3) {
			// プレイヤーの初期化
			this.bgm = new BgmPlayer(this, 7);
			textview.setText(getText(R.string.chap3));
		} else if (flag == 0) {
			// プレイヤーの初期化
			this.bgm = new BgmPlayer(this, 7);
			textview.setText(getText(R.string.chap0));
		}

		// スプラッシュ画面から、2秒（2000ミリ秒)後に遷移する。
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// BGMの停止
				bgm.stop();
				Intent intent = new Intent(StartGameActivity.this,
						GameActivity.class);

				if (flag == 1) {
					intent.putExtra("FLAG", 1);
				} else if (flag == 2) {
					intent.putExtra("FLAG", 2);
				} else if (flag == 3) {
					intent.putExtra("FLAG", 3);
				} else if (flag == 0) {
					//intent.putExtra("FLAG", 0);
					intent = new Intent(StartGameActivity.this,
							TutorialActivity.class);
				}
				intent.putExtra("PLAYER", player);
				startActivity(intent);

				// アクティビティを終了させることで、スプラッシュ画面に戻ることを防ぐ。
				// MainActivity.this.finish();
			}
		}, 4500);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// BGMの再生
		bgm.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// BGMの停止
		bgm.stop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}