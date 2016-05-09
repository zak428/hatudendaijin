package com.example.hatudendaijin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * @author Admin
 */
@SuppressLint("NewApi")
public class TutorialActivity extends MainActivity implements OnClickListener,
		DialogListener {
	int i = -1;// ストーリーフラグ制御用変数
	int flag = 0;// フラグ
	private BgmPlayer bgm;
	private BgmPlayer bgm2;
	private SoundPool mSePlayer;
	private final static int MAX_STREAMS = 10;
	private int[] mSound = new int[5];
	String player;

	/**
	 * Called when the activity is first created.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.turorial);

		// intent受け取り
		Intent intent = getIntent();
		player = intent.getStringExtra("PLAYER");// 名前管理用変数

		final ImageButton button2 = (ImageButton) findViewById(R.id.button2);
		button2.setOnClickListener(this);
		final ImageButton finbtn = (ImageButton) findViewById(R.id.imageButton1);
		finbtn.setOnClickListener(this);
		final ImageButton optionbtn = (ImageButton) findViewById(R.id.imageButton2);
		optionbtn.setOnClickListener(this);

		final ImageView souri = (ImageView) findViewById(R.id.imageView1);
		final ImageView daijin = (ImageView) findViewById(R.id.imageView2);
		final ImageView hisho = (ImageView) findViewById(R.id.imageView3);
		final RelativeLayout window = (RelativeLayout) findViewById(R.id.window);
		final FrameLayout feedback = (FrameLayout) findViewById(R.id.feedback);
		souri.setVisibility(View.INVISIBLE);
		daijin.setVisibility(View.INVISIBLE);
		hisho.setVisibility(View.INVISIBLE);
		window.setVisibility(View.INVISIBLE);
		feedback.setVisibility(View.INVISIBLE);

		final TextView textview = (TextView) findViewById(R.id.text);
		textview.setText(getText(R.string.text_001));

		final Animation anime1 = AnimationUtils.loadAnimation(this,
				R.anim.oscillation_button);
		button2.setAnimation(anime1);

		// プレイヤーの初期化
		this.bgm = new BgmPlayer(this, 2);

		// 効果音の初期化
		mSePlayer = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
		mSound[0] = mSePlayer.load(getApplicationContext(), R.raw.click, 1);
		// 再生テスト
		int streamID = 0;
		do {
			// 少し待ち時間を入れる
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
			// ボリュームをゼロにして再生して戻り値をチェック
			streamID = mSePlayer.play(mSound[0], 0.0f, 0.0f, 1, 0, 1.0f);
		} while (streamID == 0);

		// ///グラフ部分//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		final TextView tpara0 = (TextView) findViewById(R.id.para0);// 原子力発電が何%発電しているか,何%まで発電できるかを表示するTextViewです。
		final TextView tpara1 = (TextView) findViewById(R.id.para1);// 石炭発電が何%発電しているかを表示するTextViewです。
		final TextView tpara2 = (TextView) findViewById(R.id.para2);// 天然ガス発電が何%発電しているかを表示するTextViewです。
		final TextView tpara3 = (TextView) findViewById(R.id.para3);// 太陽光発電が何%発電しているか,何%まで発電できるかを表示するTextViewです。
		final TextView tpara = (TextView) findViewById(R.id.tpara);// 各発電の%総和,最大出力増率,減率,コスト,co2排出量を表示するTextViewです。

		final ImageView hint = (ImageView) findViewById(R.id.hint);// ヒント用の画像を表示するImageViewです。invisibleに設定されています。hintButtonをクリックするとvisibleになります。

		tpara0.setText("原子力:" + para[0] + "%,限界:" + nuclearlimit + "%です。");
		tpara3.setText("太陽光:" + para[3] + "%,限界:" + solarlimit + "%です。");
		tpara.setText("合計:" + p + "%\n最大出力増率:" + Sisya(up) + "%\n最大出力減率:"
				+ Sisya(down) + "%\ncost:" + Sisya(cost) + "兆円\nCO2:"
				+ Sisya(co2) + "Mton-C");

		// ImageButton hintButton;//=
		// (ImageButton)findViewById(R.id.hintButton);//右上のhintButtonを押すとhintが表示、非表示になります。AlertDialog版も一応コメントアウトして残しておきます。
		// hintButton.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // AlertDialog.Builder dlg;
		// // dlg = new AlertDialog.Builder(MainActivity.this);
		// // dlg.setTitle("ヒント");
		// // dlg.setMessage("ヒントを表示します！！！");
		// // dlg.show();
		// if (hint.getVisibility() != View.VISIBLE) {
		// hint.setVisibility(View.VISIBLE);
		// } else {
		// hint.setVisibility(View.INVISIBLE);
		// }
		// }
		// });
		//
		//
		// ImageButton submitButton;//=
		// (ImageButton)findViewById(R.id.submitButton);//右下のsubmitButtonを押すと条件によってactivityを遷移させます。
		// submitButton.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if(p==100){//総和が100%じゃないと提出できない
		// if(up<30){//火力発電の出力増率が30%以下じゃないと提出できない
		// if(cost<4){
		// //Intent intent = new
		// Intent(getApplicationContext(),SuccessActivity.class);
		// //startActivity(intent);
		// }else{
		// //Intent intent = new
		// Intent(getApplicationContext(),FailureActivity.class);
		// //startActivity(intent);
		// }
		// }else{
		// AlertDialog.Builder dlg;
		// dlg = new AlertDialog.Builder(GameActivity.this);
		// dlg.setTitle("出力増減率に問題あり！");
		// dlg.setMessage("出力を自由に変えられない原子力発電と太陽光発電の割合を下げて\n火力発電の割合を増やしましょう");
		// dlg.show();
		// }
		// }else{
		// AlertDialog.Builder dlg;
		// dlg = new AlertDialog.Builder(GameActivity.this);
		// dlg.setTitle("100%に達していません！");
		// dlg.setMessage("出力を増やして100%にしてください");
		// dlg.show();
		// }
		// }
		// });

		final SeekBar seekBar0 = (SeekBar) findViewById(R.id.seekBar0);// 原子力発電のシークバーです。
		seekBar0.setMax(nuclearlimit);
		seekBar0.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// ツマミをドラッグしたときに呼ばれる

				para[0] = progress;// シークバーの値をpara[0]に代入します。この時点ではグラフ描画用のviewであるGraphViewにはpara[0]は渡されていません。
				p = para[0] + para[1] + para[2] + para[3];// 各発電の%総和を計算します。

				if (progress > nuclearlimit) {// シークバーの値が原子力の限界%を超えないようにします。
					seekBar0.setProgress(nuclearlimit);
				} else if (p > 100) {// シークバーの値によって%総和が100を超えないようにします。
					seekBar0.setProgress(100 - para[1] - para[2] - para[3]);
				}

				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(getApplication());// グラフ描画のViewである、GraphViewにpara[]を渡すためにpreferencesというデータベースを使っています。
				sp.edit().putInt("Para0", para[0]).commit();// spというデータベースにPara0という名前でpara[0]を保存しています。GraphViewではこれを読み込んでいます。

				limitcalculation();// 原子力発電、太陽光発電の限界発電%を計算します。
				tpara0.setText("原子力:" + para[0] + "%,限界:" + nuclearlimit
						+ "%です。");
				tpara3.setText("太陽光:" + para[3] + "%,限界:" + solarlimit + "%です。");

				calculation();// 各発電の%総和、コスト、co2排出量、最大出力増減率を計算します。
				tpara.setText("合計:" + p + "%\n最大出力増率:" + Sisya(up)
						+ "%\n最大出力減率:" + Sisya(down) + "%\ncost:" + Sisya(cost)
						+ "兆円\nCO2:" + Sisya(co2) + "Mton-C");
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// ツマミを離したときに呼ばれる
				if (i == 16) {
					i = 17;
					textview.setText(getText(R.string.text_6));
				}

			}
		});

		final SeekBar seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		seekBar1.setMax(100);
		seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// ツマミをドラッグしたときに呼ばれる

				para[1] = progress;
				p = para[0] + para[1] + para[2] + para[3];

				if (p > 100) {
					seekBar1.setProgress(100 - para[0] - para[2] - para[3]);

				}

				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(getApplication());
				sp.edit().putInt("Para1", para[1]).commit();

				tpara1.setText("石炭:" + para[1] + "%");

				calculation();
				tpara.setText("合計:" + p + "%\n最大出力増率:" + Sisya(up)
						+ "%\n最大出力減率:" + Sisya(down) + "%\ncost:" + Sisya(cost)
						+ "兆円\nCO2:" + Sisya(co2) + "Mton-C");
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// ツマミを離したときに呼ばれる
				if (i == 16) {
					i = 17;
					textview.setText(getText(R.string.text_6));
				}

			}
		});

		final SeekBar seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		seekBar2.setMax(100);
		seekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// ツマミをドラッグしたときに呼ばれる

				para[2] = progress;
				p = para[0] + para[1] + para[2] + para[3];

				if (p > 100) {
					seekBar2.setProgress(100 - para[0] - para[1] - para[3]);
				}

				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(getApplication());
				sp.edit().putInt("Para2", para[2]).commit();

				tpara2.setText("天然ガス:" + para[2] + "%");

				calculation();
				tpara.setText("合計:" + p + "%" + "\n最大出力増率:" + Sisya(up)
						+ "%\n最大出力減率:" + Sisya(down) + "%\ncost:" + Sisya(cost)
						+ "兆円\nCO2:" + Sisya(co2) + "Mton-C");
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// ツマミを離したときに呼ばれる
				if (i == 16) {
					i = 17;
					textview.setText(getText(R.string.text_6));
				}

			}
		});

		final SeekBar seekBar3 = (SeekBar) findViewById(R.id.seekBar3);
		seekBar3.setMax(solarlimit);
		seekBar3.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// ツマミをドラッグしたときに呼ばれる

				para[3] = progress;
				p = para[0] + para[1] + para[2] + para[3];

				if (progress > solarlimit) {
					seekBar3.setProgress(solarlimit);
				} else if (p > 100) {
					seekBar3.setProgress(100 - para[0] - para[1] - para[2]);
				}

				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(getApplication());
				sp.edit().putInt("Para3", para[3]).commit();

				limitcalculation();
				tpara0.setText("原子力:" + para[0] + "%,限界:" + nuclearlimit
						+ "%です。");
				tpara3.setText("太陽光:" + para[3] + "%,限界:" + solarlimit + "%です。");

				calculation();
				tpara.setText("合計:" + p + "%\n最大出力増率:" + Sisya(up)
						+ "%\n最大出力減率:" + Sisya(down) + "%\ncost:" + Sisya(cost)
						+ "兆円\nCO2:" + Sisya(co2) + "Mton-C");
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// ツマミを離したときに呼ばれる
				if (i == 16) {
					i = 17;
					textview.setText(getText(R.string.text_6));
				}

			}
		});
		// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	}

	@Override
	protected void onResume() {
		super.onResume();
		// BGMの再生
		if (i < 3)
			bgm.start();
		if (i >= 3)
			bgm2.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// BGMの停止
		if (i < 3)
			bgm.stop();
		if (i >= 3)
			bgm2.stop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// SoundPoolの解放
		mSePlayer.release();
	}

	@SuppressLint("NewApi")
	public void showDialog(String title, String message, int type) {
		MyDialogFragment newFragment = MyDialogFragment.newInstance(title,
				message, type);
		// リスナーセット
		newFragment.setDialogListener(this);
		// ここでCancelable(false)をしないと効果が無い
		newFragment.setCancelable(false);
		newFragment.show(getFragmentManager(), "dialog");
	}

	/**
	 * OKボタンをおした時
	 */
	public void doPositiveClick() {
		final TextView textview = (TextView) findViewById(R.id.text);
		if (i == 18) {
			textview.setText(getText(R.string.text_9));
			i = 19;
		}
	}

	/**
	 * NGボタンをおした時
	 */
	public void doNegativeClick() {

	}

	/**
	 * 提出ボタンをおした時
	 */
	public void doFinClick() {
		final TextView textview = (TextView) findViewById(R.id.text);
		final ImageView souri = (ImageView) findViewById(R.id.imageView1);
		final ImageView hisho = (ImageView) findViewById(R.id.imageView3);
		final FrameLayout layout = (FrameLayout) findViewById(R.id.Layout1);
		final RelativeLayout window = (RelativeLayout) findViewById(R.id.window);
		final FrameLayout feedback = (FrameLayout) findViewById(R.id.feedback);
		final Animation anime3 = AnimationUtils.loadAnimation(this,
				R.anim.oscillation_character2);// 秘書、総理用
		if (i == 20 || i == 19) {
			textview.setText(getText(R.string.text_11));
			i = 21;

			// キャラ表示
			hisho.clearAnimation();
			hisho.setVisibility(View.INVISIBLE);
			souri.setAnimation(anime3);
			souri.setVisibility(View.VISIBLE);
			window.setVisibility(View.INVISIBLE);
			feedback.setVisibility(View.VISIBLE);
			// 背景変更
			layout.setBackgroundResource(R.drawable.yakuin);
		}
	}

	/**
	 * リトライボタンをおした時
	 */
	public void doRetryClick() {

	}

	/**
	 * あきらめるボタンをおした時
	 */
	public void doGameoverClick() {

	}

	// 戻るボタン
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				// ダイアログ表示など特定の処理を行いたい場合はここに記述

				// 親クラスのdispatchKeyEvent()を呼び出さずにtrueを返す
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	public void onClick(View v) {
		final ImageButton button2 = (ImageButton) findViewById(R.id.button2);
		final ImageButton finbtn = (ImageButton) findViewById(R.id.imageButton1);
		final ImageButton optionbtn = (ImageButton) findViewById(R.id.imageButton2);
		final TextView textview = (TextView) findViewById(R.id.text);
		final FrameLayout layout = (FrameLayout) findViewById(R.id.Layout1);
		final ImageView souri = (ImageView) findViewById(R.id.imageView1);
		final ImageView daijin = (ImageView) findViewById(R.id.imageView2);
		final ImageView hisho = (ImageView) findViewById(R.id.imageView3);
		final RelativeLayout window = (RelativeLayout) findViewById(R.id.window);
		final LinearLayout seekbars = (LinearLayout) findViewById(R.id.seekbars);
		final FrameLayout feedback = (FrameLayout) findViewById(R.id.feedback);

		final Animation anime2 = AnimationUtils.loadAnimation(this,
				R.anim.oscillation_character1);// 大臣用
		final Animation anime3 = AnimationUtils.loadAnimation(this,
				R.anim.oscillation_character2);// 秘書、総理用

		feedback.setVisibility(View.INVISIBLE);
		if (v == button2) {
			if (i == -1) {
				textview.setText(getText(R.string.text_001)+player+getText(R.string.text_001_));
				i = 0;
			} else if (i == 0) {
				textview.setText(getText(R.string.text_01));
				i = 1;
			} else if (i == 1) {
				textview.setText(getText(R.string.text_1));
				i = 2;
				// 背景変更
				layout.setBackgroundResource(R.drawable.city);

			} else if (i == 2) {
				textview.setText(getText(R.string.text_2));
				i = 3;

			} else if (i == 3) {
				textview.setText(getText(R.string.text_2_1) + player
						+ getText(R.string.text_2_1_));
				i = 4;

			} else if (i == 4) {
				textview.setText(getText(R.string.text_2_2));
				i = 5;

			} else if (i == 5) {
				textview.setText(getText(R.string.text_2_3));
				i = 6;

				// キャラ表示
				souri.clearAnimation();
				souri.setVisibility(View.INVISIBLE);
				hisho.setAnimation(anime3);
				hisho.setVisibility(View.VISIBLE);

			} else if (i == 6) {
				textview.setText(getText(R.string.text_2_4));
				i = 7;

				// 背景変更
				layout.setBackgroundResource(R.drawable.black);

				// キャラ表示
				hisho.clearAnimation();
				hisho.setVisibility(View.INVISIBLE);

			} else if (i == 7) {
				textview.setText(getText(R.string.text_2_5));
				i = 8;

			} else if (i == 8) {
				textview.setText(getText(R.string.text_2_6));
				i = 9;

			} else if (i == 9) {
				textview.setText(getText(R.string.text_2_7) + player
						+ getText(R.string.text_2_7_));
				i = 10;

				// BGM変更
				bgm.stop();
				this.bgm2 = new BgmPlayer(this, 3);
				bgm2.start();

				// キャラ表示
				anime2.setFillAfter(true);
				daijin.setAnimation(anime2);
				daijin.setVisibility(View.VISIBLE);
				souri.setAnimation(anime3);
				souri.setVisibility(View.VISIBLE);

				// 背景変更
				layout.setBackgroundResource(R.drawable.yakuin);

			} else if (i == 10) {
				textview.setText(getText(R.string.text_3));
				// キャラ表示
				souri.clearAnimation();
				souri.setVisibility(View.INVISIBLE);

				// 背景変更
				layout.setBackgroundResource(R.drawable.business);

				i = 11;
			} else if (i == 11) {
				textview.setText(getText(R.string.text_3_1) + player
						+ getText(R.string.text_3_1_));

				// キャラ表示
				hisho.setAnimation(anime3);
				hisho.setVisibility(View.VISIBLE);

				i = 12;
			} else if (i == 12) {
				textview.setText(getText(R.string.text_3_2));

				i =13;
			}else if (i == 13) {
				textview.setText(getText(R.string.text_3_3));

				i = 14;
			}else if (i == 14) {
				textview.setText(getText(R.string.text_3_4));

				i = 15;
			}else if (i == 15) {
				textview.setText(getText(R.string.text_4));

				i = 16;
				flag = 0;
			}else if (i == 16) {

				if (flag == 0) {
					textview.setText(getText(R.string.text_5));
					flag++;
					//startMeasure(seekbars);
				} else if (flag == 1) {
					textview.setText(getText(R.string.text_5_1));
				}
				// 操作ウィンドウの表示
				window.setVisibility(View.VISIBLE);

				// スクロールバーを動かすとi=17へ
			} else if (i == 17) {
				textview.setText(getText(R.string.text_7));
				i = 18;
				flag = 0;
			} else if (i == 18) {

				// オプションボタンを押すとi=19へ
				if (flag == 0) {
					textview.setText(getText(R.string.text_8));
					flag++;
					//startMeasure(optionbtn);
				} else if (flag == 1) {
					textview.setText(getText(R.string.text_8_1));
				}
			} else if (i == 19) {
				//startMeasure(finbtn);
				textview.setText(getText(R.string.text_9));
				i = 20;
				// 提出ボタンを押すとi=21へ
			} else if (i == 20) {

				textview.setText(getText(R.string.text_10));
				// 提出ボタンを押すとi=21へ
			} else if (i == 21) {
				textview.setText(getText(R.string.text_12));
				i = 22;

				// キャラ表示
				souri.clearAnimation();
				souri.setVisibility(View.INVISIBLE);
				hisho.setAnimation(anime3);
				hisho.setVisibility(View.VISIBLE);

				// 背景変更
				layout.setBackgroundResource(R.drawable.business);

			} else if (i == 22) {
				textview.setText(getText(R.string.text_13));
				i = 23;
			} else if (i == 23) {
				textview.setText(getText(R.string.text_14));
				i = 24;
			} else if (i == 24) {
				textview.setText(getText(R.string.text_15));
				i = 25;

				// キャラ表示
				hisho.clearAnimation();
				hisho.setVisibility(View.INVISIBLE);
				souri.setAnimation(anime3);
				souri.setVisibility(View.VISIBLE);

				// 背景変更
				layout.setBackgroundResource(R.drawable.yakuin);
			} else if (i == 25) {
				textview.setText(getText(R.string.text_16));
				Intent intent = new Intent(getApplicationContext(),
						com.example.hatudendaijin.StartGameActivity.class);
				intent.putExtra("FLAG", 1);
				intent.putExtra("PLAYER", player);
				startActivity(intent);// チュートリアル終わり
			}

			mSePlayer.play(mSound[0], 2.0f, 2.0f, 0, 0, 1.0f);
			// デバッグ用
			//Toast.makeText(this, String.valueOf(i), Toast.LENGTH_SHORT).show();

		} else if (v == finbtn) {
			showDialog(getString(R.string.fintitle),
					getString(R.string.finmessage), 1);
		} else if (v == optionbtn) {
			showDialog(getString(R.string.optiontitle),
					getString(R.string.optionmessage), 3);
		}
	}

	//点滅アニメーションのコード
	private Handler mHandler = new Handler();
	private ScheduledExecutorService mScheduledExecutor;
	private int time=2;
int t=0;

	private void startMeasure(final View v) {

		t=0;
	    /**
	     * 第一引数: 繰り返し実行したい処理
	     * 第二引数: 指定時間後に第一引数の処理を開始
	     * 第三引数: 第一引数の処理完了後、指定時間後に再実行
	     * 第四引数: 第二、第三引数の単位
	     *
	     * new Runnable（無名オブジェクト）をすぐに（0秒後に）実行し、完了後1700ミリ秒ごとに繰り返す。
	     * （ただしアニメーションの完了からではない。Handler#postが即時実行だから？？）
	     */
	    mScheduledExecutor = Executors.newScheduledThreadPool(2);

	    mScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
	        @Override
	        public void run() {
	            mHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                    v.setVisibility(View.VISIBLE);
	    	            if(t!=time) t++;

	                    // HONEYCOMBより前のAndroid SDKがProperty Animation非対応のため
	                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	                       animateAlpha();
	                    }
	                }
	            });
	        }


	        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	        private void animateAlpha() {

	            // 実行するAnimatorのリスト
	            List<Animator> animatorList = new ArrayList<Animator>();

	            // alpha値を0から1へ1000ミリ秒かけて変化させる。
	            ObjectAnimator animeFadeIn = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
	            animeFadeIn.setDuration(1000);

	            // alpha値を1から0へ600ミリ秒かけて変化させる。
	            ObjectAnimator animeFadeOut = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f);
	            animeFadeOut.setDuration(600);

	            // 実行対象Animatorリストに追加。
	            animatorList.add(animeFadeIn);
	            animatorList.add(animeFadeOut);

	            final AnimatorSet animatorSet = new AnimatorSet();

	            // リストの順番に実行
	            animatorSet.playSequentially(animatorList);

	            animatorSet.start();
	            if(t==time)mScheduledExecutor.shutdown();
	        }
	    }, 0, 1700, TimeUnit.MILLISECONDS);

	}


}
