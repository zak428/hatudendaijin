package com.example.hatudendaijin;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class EDActivity extends Activity{

	private BgmPlayer bgm;
    private SoundPool mSePlayer;
    private final static int MAX_STREAMS = 10;
    private int mSound;
	//final LinearLayout endroll = (LinearLayout) findViewById(R.id.Layout1);
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed);

		
		
		final TextView textview = (TextView) findViewById(R.id.endroll);
		final Animation anime = AnimationUtils.loadAnimation(this,
				R.anim.endroll);
		textview.setAnimation(anime);
		textview.setVisibility(View.VISIBLE);
		// プレイヤーの初期化
		this.bgm = new BgmPlayer(this, 6);
		
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
	


}