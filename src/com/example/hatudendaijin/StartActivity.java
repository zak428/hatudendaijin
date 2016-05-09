package com.example.hatudendaijin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends Activity implements OnClickListener {

	private BgmPlayer bgm;
    private SoundPool mSePlayer;
    private final static int MAX_STREAMS = 10;
    private int mSound;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(this);

        final Animation anime1 = AnimationUtils.loadAnimation(this, R.anim.oscillation_button);
        button1.setAnimation(anime1);

		// プレイヤーの初期化
		this.bgm = new BgmPlayer(this, 1);

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

        //名前入力
        final EditText editView = new EditText(StartActivity.this);
        new AlertDialog.Builder(StartActivity.this)
            .setIcon(android.R.drawable.ic_dialog_info)
            .setTitle("名前を入れてください(あなたの電源構成を表示するときに使います)")
            //setViewにてビューを設定します。
            .setView(editView)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //入力した文字をトースト出力する
                    //Toast.makeText(StartActivity.this,
                      //      editView.getText().toString(),
                        //    Toast.LENGTH_LONG).show();

                    //入力されるとスタート。
            		Intent intent = new Intent(getApplicationContext(),
            				com.example.hatudendaijin.StartGameActivity.class);
            		intent.putExtra("PLAYER",editView.getText().toString());
            		intent.putExtra("FLAG",0);
            		startActivity(intent);

                }
            })
            .show();

	}
}
