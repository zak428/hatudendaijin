package com.example.hatudendaijin;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

public class EndingActivity extends Activity {

	final LinearLayout layout = (LinearLayout) findViewById(R.id.Layout1);


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ed);

		/*		final VideoView videoView = (VideoView) findViewById(R.id.video);
		videoView.setVideoURI(Uri.parse("android.resource://"
				+ this.getPackageName() + "/" + R.raw.edmovie));
		// 準備が完了したら再生開始
		videoView.setOnPreparedListener(new OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				videoView.start();
			}
		});
*/
/*
		  final TextView textview = (TextView) findViewById(R.id.endroll);
		  final Animation anime = AnimationUtils.loadAnimation(this,
		  R.anim.endroll); textview.setAnimation(anime);
		  textview.setVisibility(View.VISIBLE);
		  
		  // プレイヤーの初期化 this.bgm = new BgmPlayer(this, 6);
		  
		  //効果音の初期化 mSePlayer = new SoundPool(MAX_STREAMS,
		  AudioManager.STREAM_MUSIC, 0); mSound =
		  mSePlayer.load(getApplicationContext(), R.raw.start, 1); //再生テスト int
		  streamID = 0; do { //少し待ち時間を入れる try { Thread.sleep(10); } catch
		  (InterruptedException e) { } //ボリュームをゼロにして再生して戻り値をチェック streamID =
		  mSePlayer.play(mSound, 0.0f, 0.0f, 1, 0, 1.0f); } while(streamID ==
		  0);
		 */

	}

}