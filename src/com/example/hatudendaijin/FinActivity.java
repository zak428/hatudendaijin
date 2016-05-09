package com.example.hatudendaijin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FinActivity extends Activity implements OnClickListener {

	private BgmPlayer bgm;
    private SoundPool mSePlayer;
    private final static int MAX_STREAMS = 10;
    private int mSound;
    private ViewPager _viewPager = null;


	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fin);

		// intent受け取り
		Intent i = getIntent();
		String player = i.getStringExtra("PLAYER");// 名前管理用変数
		String file = i.getStringExtra("FILE");// 画像ファイル受け取り用管理用変数

		final Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(this);

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		_viewPager = (ViewPager) findViewById(R.id.viewpager1);
		PagerAdapter mPagerAdapter = new CustomPagerAdapter(this);
		_viewPager.setAdapter(mPagerAdapter);

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


		// プレイヤーの初期化
		this.bgm = new BgmPlayer(this, 5);

		//効果音の初期化
        mSePlayer = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        mSound = mSePlayer.load(getApplicationContext(), R.raw.start, 1);
        //再生テスト
        int streamID = 0;
        do {
         //少し待ち時間を入れる
         try {
          Thread.sleep(10);
         } catch (InterruptedException e) {
         }
         //ボリュームをゼロにして再生して戻り値をチェック
         streamID = mSePlayer.play(mSound, 0.0f, 0.0f, 1, 0, 1.0f);
        } while(streamID == 0);


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

	  //SoundPoolの解放
	  mSePlayer.release();
	 }

	public void onClick(View v) {
        mSePlayer.play(mSound, 2.0f, 2.0f, 0, 0, 1.0f);
		Intent intent = new Intent(getApplicationContext(),
				com.example.hatudendaijin.EDActivity.class);
		startActivity(intent);
	}
}