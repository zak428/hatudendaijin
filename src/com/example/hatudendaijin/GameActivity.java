package com.example.hatudendaijin;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
public class GameActivity extends MainActivity implements OnClickListener,
		DialogListener {
	int i = 1;// ストーリーフラグ制御用変数
	int f = 1;// チャプターフラグ管理用変数
	int a_ = 0;// フィードバック分岐用変数
	int HP = 3;// 体力ゲージ
	int flag = 0;// フラグ
	int retry[] = { 1, 1 };// リトライ用iとf格納用
	private BgmPlayer bgm, bgm2, bgm3, gameover;
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
		setContentView(R.layout.game);

		// intent受け取り
		Intent i = getIntent();
		f = i.getIntExtra("FLAG", 1);// チャプターフラグ管理用変数
		player = i.getStringExtra("PLAYER");// 名前管理用変数

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
		final TextView textview2 = (TextView) findViewById(R.id.textView2);
		final FrameLayout feedback = (FrameLayout) findViewById(R.id.feedback);
		souri.setVisibility(View.INVISIBLE);
		daijin.setVisibility(View.INVISIBLE);
		hisho.setVisibility(View.INVISIBLE);
		window.setVisibility(View.INVISIBLE);
		textview2.setVisibility(View.INVISIBLE);
		feedback.setVisibility(View.INVISIBLE);

		// キャラ表示
		final Animation anime2 = AnimationUtils.loadAnimation(this,
				R.anim.oscillation_character1);// 大臣用
		final Animation anime3 = AnimationUtils.loadAnimation(this,
				R.anim.oscillation_character2);// 秘書、総理用

		hisho.setAnimation(anime3);
		daijin.setAnimation(anime2);

		final TextView textview = (TextView) findViewById(R.id.text);
		textview.setText(getText(R.string.text_20));

		final Animation anime1 = AnimationUtils.loadAnimation(this,
				R.anim.oscillation_button);
		button2.setAnimation(anime1);

		// ライフゲージ
		// final ProgressBar bar = (ProgressBar)
		// findViewById(R.id.progressBar1);
		// bar.setMax(3);
		// bar.setProgress(HP);

		if (f == 1) {
			hisho.setVisibility(View.VISIBLE);
			daijin.setVisibility(View.VISIBLE);
		}

		// プレイヤーの初期化
		this.bgm = new BgmPlayer(this, 4);
		this.bgm2 = new BgmPlayer(this, 4);
		this.bgm3 = new BgmPlayer(this, 4);
		this.gameover = new BgmPlayer(this, 4);

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

		//final ImageView hint = (ImageView) findViewById(R.id.hint);// ヒント用の画像を表示するImageViewです。invisibleに設定されています。hintButtonをクリックするとvisibleになります。

		final ProgressBar pb1=(ProgressBar) findViewById(R.id.progressBar1);//金
		final ProgressBar pb2=(ProgressBar) findViewById(R.id.progressBar2);//co2

		final TextView tv1 = (TextView) findViewById(R.id.cost);
		final TextView tv2 = (TextView) findViewById(R.id.co2);

		// 水平プログレスバーの最大値を設定します
        pb1.setMax(100);
        pb2.setMax(30);
        // 水平プログレスバーの値を設定します
        pb1.setProgress((int)(Sisya(cost)*10));
        pb2.setProgress((int)Sisya(co2));
        // 水平プログレスバーのセカンダリ値を設定します
        pb1.setSecondaryProgress(40);
        pb2.setSecondaryProgress(30);


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
				// 水平プログレスバーの値を設定します
		        pb1.setProgress((int)(Sisya(cost)*10));
		        pb2.setProgress((int)Sisya(co2));

		        if(pb1.getProgress()>pb1.getSecondaryProgress()){
		        tv1.setTextColor(Color.RED);
		        }else{
		        	tv1.setTextColor(Color.WHITE);
		        }

		        if(pb2.getProgress()>pb2.getSecondaryProgress()){
		        	tv2.setTextColor(Color.RED);
		        }else{
		        	tv2.setTextColor(Color.WHITE);
		        }

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
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
				// 水平プログレスバーの値を設定します
		        pb1.setProgress((int)(Sisya(cost)*10));
		        pb2.setProgress((int)Sisya(co2));

		        if(pb1.getProgress()>pb1.getSecondaryProgress()){
		        	tv1.setTextColor(Color.RED);
		        }else{
		        	tv1.setTextColor(Color.WHITE);
		        }


		        if(pb2.getProgress()>pb2.getSecondaryProgress()){
		        	tv2.setTextColor(Color.RED);
		        }else{
		        	tv2.setTextColor(Color.WHITE);
		        }

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
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
				// 水平プログレスバーの値を設定します
		        pb1.setProgress((int)(Sisya(cost)*10));
		        pb2.setProgress((int)Sisya(co2));

		        if(pb1.getProgress()>pb1.getSecondaryProgress()){
		        	tv1.setTextColor(Color.RED);
		        }else{
		        	tv1.setTextColor(Color.WHITE);
		        }


		        if(pb2.getProgress()>pb2.getSecondaryProgress()){
		          tv2.setTextColor(Color.RED);
		        }else{
		        	tv2.setTextColor(Color.WHITE);
		        }

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
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
				// 水平プログレスバーの値を設定します
		        pb1.setProgress((int)(Sisya(cost)*10));
		        pb2.setProgress((int)Sisya(co2));

		        if(pb1.getProgress()>pb1.getSecondaryProgress()){
		        	tv1.setTextColor(Color.RED);
		        }else{
		        	tv1.setTextColor(Color.WHITE);
		        }


		        if(pb2.getProgress()>pb2.getSecondaryProgress()){
		        	tv2.setTextColor(Color.RED);
		        }else{
		        	tv2.setTextColor(Color.WHITE);
		        }

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		if (f == 1) {// 最初は原子力と太陽光表示しない。
			seekBar0.setVisibility(View.INVISIBLE);
			seekBar3.setVisibility(View.INVISIBLE);
			tpara0.setVisibility(View.INVISIBLE);
			tpara3.setVisibility(View.INVISIBLE);
		}
		// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
		final ImageView hisho = (ImageView) findViewById(R.id.imageView3);
		final RelativeLayout window = (RelativeLayout) findViewById(R.id.window);

		final Animation anime3 = AnimationUtils.loadAnimation(this,
				R.anim.oscillation_character2);// 秘書、総理用

		if (f == 1) {
			if (i == 3) {

				i = 4;
			} else if (i == 6) {

				i = 7;
			} else if (i == 9) {

				i = 10;
			} else if (i == 12) {

				i = 13;
			}

		} else if (f == 2) {
			if (i == 4) {

				i = 5;
			} else if (i == 8) {

				i = 9;
			}

		} else if (f == 3) {
			if (i == 3) {

				i = 4;
			} else if (i == 6) {

				i = 7;
			}

		}

		// キャラ表示
		hisho.setAnimation(anime3);
		hisho.setVisibility(View.VISIBLE);
		window.setVisibility(View.VISIBLE);
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
		double cost = 0;
		double co2 = 0;
		double tcost = 0;
		double tco2 = 0;

		final TextView textview = (TextView) findViewById(R.id.text);
		final RelativeLayout window = (RelativeLayout) findViewById(R.id.window);
		final FrameLayout layout = (FrameLayout) findViewById(R.id.Layout1);
		final ImageView souri = (ImageView) findViewById(R.id.imageView1);
		final ImageView hisho = (ImageView) findViewById(R.id.imageView3);
		final FrameLayout feedback = (FrameLayout) findViewById(R.id.feedback);
		final TextView fbtext = (TextView) findViewById(R.id.feedbacktext);
		final TextView fbrank = (TextView) findViewById(R.id.feedbackRank);
		// HP
		final ImageView h1 = (ImageView) findViewById(R.id.imageView4);
		final ImageView h2 = (ImageView) findViewById(R.id.imageView5);
		final ImageView h3 = (ImageView) findViewById(R.id.imageView6);

		final Animation anime3 = AnimationUtils.loadAnimation(this,
				R.anim.oscillation_character2);// 秘書、総理用

		boolean a = false;// 成功、失敗判定用変数

		final SeekBar seekBar0 = (SeekBar) findViewById(R.id.seekBar0);// 原子力のシークバー
		final SeekBar seekBar3 = (SeekBar) findViewById(R.id.seekBar3);// 太陽光のシークバー
		final TextView tpara0 = (TextView) findViewById(R.id.para0);// 原子力発電が何%発電しているか,何%まで発電できるかを表示するTextViewです。
		final TextView tpara3 = (TextView) findViewById(R.id.para3);// 太陽光発電が何%発電しているか,何%まで発電できるかを表示するTextViewです。

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		cost = Sisya(sp.getFloat("Cost", 0));
		co2 = Sisya(sp.getFloat("Co2", 0));

		if (p == 100) {// 総和が100%じゃないと提出できない
			if (up < 30) {// 火力発電の出力増率が30%以下じゃないと提出できない
				if (seekBar3.getProgress() > 10 || i != 13) {// 太陽光発電の出力増率が１０以上じゃないと提出できない
					if (f == 1) {
						if (i == 4) {
							textview.setText(getText(R.string.text_21));
							i = 5;
							tcost = 4;
							a_ = 1;
							if (cost <= 4) {
								a = true;
								a_ = 2;
							}
						} else if (i == 7) {
							textview.setText(getText(R.string.text_24));
							i = 8;
							tco2 = 21;
							a_ = 3;
							if (co2 <= 21) {
								a = true;
								a_ = 4;
								seekBar0.setVisibility(View.VISIBLE);
								tpara0.setVisibility(View.VISIBLE);
							}
						} else if (i == 10) {
							textview.setText(getText(R.string.text_27));
							i = 11;
							tcost = 4;
							tco2 = 21;
							a_ = 5;
							if (co2 > 21)
								a_ = 6;
							if (co2 <= 21 && cost <= 4) {
								a = true;
								a_ = 7;
								seekBar3.setVisibility(View.VISIBLE);
								tpara3.setVisibility(View.VISIBLE);
							}
						} else if (i == 13) {
							textview.setText(getText(R.string.text_30));
							i = 14;
							tcost = 6;
							tco2 = 18;
							a_ = 8;
							if (co2 > 18)
								a_ = 9;
							if (co2 <= 18 && cost <= 6) {
								a = true;
								a_ = 10;
							}
						}
					} else if (f == 2) {

						if (i == 5) {
							textview.setText(getText(R.string.text_37));
							i = 6;
							tcost = 5.4;
							tco2 = 18;
							a_ = 11;
							if (co2 > 18)
								a_ = 12;
							if (co2 <= 18 && cost <= 5.4) {
								a = true;
								a_ = 13;
							}

						} else if (i == 9) {
							textview.setText(getText(R.string.text_41));
							i = 10;
							tco2 = 16;
							tcost = 5.4;
							a_ = 14;
							if (co2 > 16)
								a_ = 15;
							if (co2 <= 16 && cost <= 5.4) {
								a = true;
								a_ = 16;
							}

						}

					} else if (f == 3) {

						if (i == 4) {
							textview.setText(getText(R.string.text_45));
							i = 5;
							tco2 = 16;
							tcost = 7;
							a_ = 17;
							if (co2 > 16)
								a_ = 18;
							if (co2 <= 16 && cost <= 7) {
								a = true;
								a_ = 19;
							}

						} else if (i == 7) {
							textview.setText(getText(R.string.text_48));
							i = 8;
							// 制限なし
							a = true;
							a_ = 20;

						}

					}

					// Intent intent = new Intent(getApplicationContext(),
					// com.example.hatudendaijin.FinActivity.class);
					// startActivity(intent);

					// キャラ表示
					hisho.clearAnimation();
					hisho.setVisibility(View.INVISIBLE);
					souri.setAnimation(anime3);
					souri.setVisibility(View.VISIBLE);
					layout.setBackgroundResource(R.drawable.yakuin);
					feedback.setVisibility(View.VISIBLE);
					window.setVisibility(View.INVISIBLE);

					if (a) {
						// Intent intent = new
						// Intent(getApplicationContext(),SuccessActivity.class);
						// startActivity(intent);
						fbtext.setBackgroundResource(R.drawable.feedback);
						fbtext.setText("目標コスト:" + fb1(tcost) + "目標CO2:" + fb2(tco2)
								+ "達成コスト:" + cost + "兆円\n達成CO2:" + co2
								+ "Mton-C\n差分コスト:" + Math.abs(Sisya(tcost - cost))
								+ "兆円\n差分CO2:" + Math.abs(Sisya(tco2 - co2)) + "Mton-C");
						fbrank.setText("Rank:A");

					} else {
						// Intent intent = new
						// Intent(getApplicationContext(),FailureActivity.class);
						// startActivity(intent);
						HP--;
						fbtext.setBackgroundResource(R.drawable.feedback2);
						fbtext.setText("目標コスト:" + fb1(tcost) + "目標CO2:" + fb2(tco2)
								+ "達成コスト:" + cost + "兆円\n達成CO2:" + co2
								+ "Mton-C\n差分コスト:" + Math.abs(Sisya(tcost - cost))
								+ "兆円\n差分CO2:" + Math.abs(Sisya(tco2 - co2)) + "Mton-C");
						fbrank.setText("Rank:E");

						// HP;
						if (HP == 2) {
							h3.setVisibility(View.INVISIBLE);
							i--;
							textview.setText(getText(R.string.text_998));
						} else if (HP == 1) {
							h2.setVisibility(View.INVISIBLE);
							i--;
							textview.setText(getText(R.string.text_998));
						} else if (HP == 0) {
							h1.setVisibility(View.INVISIBLE);
							layout.setBackgroundResource(R.drawable.gameover);
							retry[0] = i - 1;
							retry[1] = f;
							textview.setText(getText(R.string.text_999));
							f = 999;
							i = 0;
							bgm.stop();
						}
					}
				} else {
					AlertDialog.Builder dlg;
					dlg = new AlertDialog.Builder(GameActivity.this);
					dlg.setTitle("太陽光の発電割合が少ないです！");
					dlg.setMessage("太陽光の発電割合を増やして10%以上にしてください");
					dlg.show();

				}
			} else {
				AlertDialog.Builder dlg;
				dlg = new AlertDialog.Builder(GameActivity.this);
				dlg.setTitle("出力増減率に問題あり！");
				dlg.setMessage("出力を自由に変えられない原子力発電と太陽光発電の割合を下げて\n火力発電の割合を増やしましょう");
				dlg.show();
			}
		} else {
			AlertDialog.Builder dlg;
			dlg = new AlertDialog.Builder(GameActivity.this);
			dlg.setTitle("100%に達していません！");
			dlg.setMessage("出力を増やして100%にしてください");
			dlg.show();
		}

	}



	public String fb1(double d){
		String s;
		s=d+"兆円\n";
		if(d==0){
			s="制限なし\n";
		}

		return s;
	}
	public String fb2(double d){
		String s;
		s=d+"M-tonC\n";
		if(d==0){
			s="制限なし\n";
		}

		return s;
	}





	/**
	 * リトライボタンをおした時
	 */
	public void doRetryClick() {
		final TextView textview = (TextView) findViewById(R.id.text);
		final RelativeLayout window = (RelativeLayout) findViewById(R.id.window);
		final FrameLayout layout = (FrameLayout) findViewById(R.id.Layout1);
		final ImageView souri = (ImageView) findViewById(R.id.imageView1);
		final ImageView hisho = (ImageView) findViewById(R.id.imageView3);
		final Animation anime3 = AnimationUtils.loadAnimation(this,
				R.anim.oscillation_character2);// 秘書、総理用

		// HP
		final ImageView h1 = (ImageView) findViewById(R.id.imageView4);
		final ImageView h2 = (ImageView) findViewById(R.id.imageView5);
		final ImageView h3 = (ImageView) findViewById(R.id.imageView6);

		// 復活
		i = 0;
		f = 1000;
		HP = 3;
		h3.setVisibility(View.VISIBLE);
		h2.setVisibility(View.VISIBLE);
		h1.setVisibility(View.VISIBLE);

		// キャラ表示
		hisho.clearAnimation();
		hisho.setVisibility(View.INVISIBLE);
		souri.setAnimation(anime3);
		souri.setVisibility(View.VISIBLE);

		textview.setText(getText(R.string.text_1001));
	}

	/**
	 * あきらめるボタンをおした時
	 */
	public void doGameoverClick() {
		final TextView textview = (TextView) findViewById(R.id.text);

		Intent intent = new Intent(GameActivity.this, FinActivity.class);

		//startActivity(intent);
		textview.setText(getText(R.string.text_9999));
		f=1;
		i=16;
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
		final TextView textview2 = (TextView) findViewById(R.id.textView2);
		final FrameLayout layout = (FrameLayout) findViewById(R.id.Layout1);
		final ImageView souri = (ImageView) findViewById(R.id.imageView1);
		final ImageView daijin = (ImageView) findViewById(R.id.imageView2);
		final ImageView hisho = (ImageView) findViewById(R.id.imageView3);
		final RelativeLayout window = (RelativeLayout) findViewById(R.id.window);
		final FrameLayout feedback = (FrameLayout) findViewById(R.id.feedback);

		final Animation anime2 = AnimationUtils.loadAnimation(this,
				R.anim.oscillation_character1);// 大臣用
		final Animation anime3 = AnimationUtils.loadAnimation(this,
				R.anim.oscillation_character2);// 秘書、総理用
		anime2.setFillAfter(true);
		anime3.setFillAfter(true);

		AlphaAnimation fadein = new AlphaAnimation(0.0f, 1.0f);
		fadein.setInterpolator(new AccelerateInterpolator());
		fadein.setDuration(1000);

		AlphaAnimation fadeout = new AlphaAnimation(1.0f, 0.0f);
		fadeout.setInterpolator(new AccelerateInterpolator());
		fadeout.setDuration(1000);
		fadeout.setStartOffset(1000);

		AnimationSet set = new AnimationSet(false);
		AnimationSet set2 = new AnimationSet(false);
		set.setFillEnabled(true);
		set.addAnimation(fadein);
		set2.setFillAfter(true);
		set2.addAnimation(fadeout);

		final Calendar calendar = Calendar.getInstance();

		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DAY_OF_MONTH);
		final int hour = calendar.get(Calendar.HOUR_OF_DAY);
		final int minute = calendar.get(Calendar.MINUTE);
		final int second = calendar.get(Calendar.SECOND);
		// final int ms = calendar.get(Calendar.MILLISECOND);

		final ProgressBar pb1=(ProgressBar) findViewById(R.id.progressBar1);//金
		final ProgressBar pb2=(ProgressBar) findViewById(R.id.progressBar2);//co2

		if (a_ != 0) {// フィードバック画面に遷移
			// キャラ表示
			souri.clearAnimation();
			souri.setVisibility(View.INVISIBLE);
			hisho.setAnimation(anime3);
			hisho.setVisibility(View.VISIBLE);

			if (a_ == 1) {
				textview.setText(getText(R.string.text_21_1));
			} else if (a_ == 2) {

				textview.setText(getText(R.string.text_21_));
				// 水平プログレスバーのセカンダリ値を設定します
		        pb1.setSecondaryProgress(100);
		        pb2.setSecondaryProgress(21);
			} else if (a_ == 3) {

				textview.setText(getText(R.string.text_24_1));
			} else if (a_ == 4) {

				textview.setText(getText(R.string.text_24_));
				// 水平プログレスバーのセカンダリ値を設定します
		        pb1.setSecondaryProgress(40);
		        pb2.setSecondaryProgress(21);
			} else if (a_ == 5) {

				textview.setText(getText(R.string.text_27_1));
			} else if (a_ == 6) {

				textview.setText(getText(R.string.text_27_2));
			} else if (a_ == 7) {

				textview.setText(getText(R.string.text_27_));
				// 水平プログレスバーのセカンダリ値を設定します
		        pb1.setSecondaryProgress(60);
		        pb2.setSecondaryProgress(18);
			} else if (a_ == 8) {

				textview.setText(getText(R.string.text_30_1));
			} else if (a_ == 9) {

				textview.setText(getText(R.string.text_30_2));
			} else if (a_ == 10) {

				textview.setText(getText(R.string.text_30_));
				// 水平プログレスバーのセカンダリ値を設定します
		        pb1.setSecondaryProgress(54);
		        pb2.setSecondaryProgress(18);
			} else if (a_ == 11) {// この↓はまだ未実装

				textview.setText(getText(R.string.text_17));
			} else if (a_ == 12) {

				textview.setText(getText(R.string.text_17));
			} else if (a_ == 13) {

				textview.setText(getText(R.string.text_17));
			} else if (a_ == 14) {

				textview.setText(getText(R.string.text_17));
			} else if (a_ == 15) {

				textview.setText(getText(R.string.text_17));
			} else if (a_ == 16) {

				textview.setText(getText(R.string.text_17));
			} else if (a_ == 17) {

				textview.setText(getText(R.string.text_17));
			} else if (a_ == 18) {

				textview.setText(getText(R.string.text_17));
			} else if (a_ == 19) {

				textview.setText(getText(R.string.text_17));
			} else if (a_ == 20) {

				textview.setText(getText(R.string.text_17));
			}
			a_ = 0;
		} else {
			feedback.setVisibility(View.INVISIBLE);

			if (f == 1) {// チャプター１

				if (v == button2) {

					if (i == 1) {// Mission1
						textview.setText(getText(R.string.text_17));
						i = 2;
						// キャラ表示
						hisho.clearAnimation();
						hisho.setVisibility(View.INVISIBLE);
						souri.setAnimation(anime3);
						souri.setVisibility(View.VISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.yakuin);

						// ハードル１
						// textview2.setText(getText(R.string.optiontitle1));
						// textview2.setVisibility(View.VISIBLE);
						// textview2.startAnimation( set );

					} else if (i == 2) {

						textview.setText(getText(R.string.text_18));
						i = 3;

						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);
						// textview2.startAnimation( set2 );

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);

					} else if (i == 3) {
						textview.setText(getText(R.string.text_19));
						showDialog(getString(R.string.optiontitle1),
								getString(R.string.optionmessage1), 3);

						// オプションボタンでの確認。
					} else if (i == 4) {
						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);
						hisho.setAnimation(anime3);
						hisho.setVisibility(View.VISIBLE);
						window.setVisibility(View.VISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);
						if (flag == 0) {// 表示内容がループします。
							textview.setText(getText(R.string.text_20_1));
							flag++;
						} else if (flag == 1) {
							textview.setText(getText(R.string.text_20_2));
							flag++;
						} else if (flag == 2) {
							textview.setText(getText(R.string.text_20_3));
							flag = 0;
						}
						// 提出ボタンを押したらi=5へ
					} else if (i == 5) {
						// キャラ表示
						hisho.clearAnimation();
						hisho.setVisibility(View.INVISIBLE);
						souri.setAnimation(anime3);
						souri.setVisibility(View.VISIBLE);

						textview.setText(getText(R.string.text_22));
						i = 6;
					} else if (i == 6) {// Mission2
						textview.setText(getText(R.string.text_23));

						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);

						flag = 0;
						showDialog(getString(R.string.optiontitle2),
								getString(R.string.optionmessage2), 3);
					} else if (i == 7) {
						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);
						hisho.setAnimation(anime3);
						hisho.setVisibility(View.VISIBLE);
						window.setVisibility(View.VISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);
						if (flag == 0) {// 表示内容がループします。
							textview.setText(getText(R.string.text_20_1));
							flag++;
						} else if (flag == 1) {
							textview.setText(getText(R.string.text_20_2));
							flag++;
						} else if (flag == 2) {
							textview.setText(getText(R.string.text_20_3));
							flag = 0;
						}
						// 提出ボタンを押したらi=8へ
					} else if (i == 8) {
						// キャラ表示
						hisho.clearAnimation();
						hisho.setVisibility(View.INVISIBLE);
						souri.setAnimation(anime3);
						souri.setVisibility(View.VISIBLE);

						textview.setText(getText(R.string.text_25));
						i = 9;

					} else if (i == 9) {// Mission3
						textview.setText(getText(R.string.text_26));

						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);
						flag = 0;

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);

						showDialog(getString(R.string.optiontitle3),
								getString(R.string.optionmessage3), 3);
					} else if (i == 10) {
						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);
						hisho.setAnimation(anime3);
						hisho.setVisibility(View.VISIBLE);
						window.setVisibility(View.VISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);
						if (flag == 0) {// 表示内容がループします。
							textview.setText(getText(R.string.text_20_1));
							flag++;
						} else if (flag == 1) {
							textview.setText(getText(R.string.text_20_2));
							flag++;
						} else if (flag == 2) {
							textview.setText(getText(R.string.text_20_3));
							flag = 0;
						}
						// 提出ボタンを押すとi=11へ
					} else if (i == 11) {
						// キャラ表示
						hisho.clearAnimation();
						hisho.setVisibility(View.INVISIBLE);
						souri.setAnimation(anime3);
						souri.setVisibility(View.VISIBLE);

						textview.setText(getText(R.string.text_28));
						i = 12;

					} else if (i == 12) {// Mission4
						textview.setText(getText(R.string.text_29));

						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);
						flag = 0;

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);
						showDialog(getString(R.string.optiontitle4),
								getString(R.string.optionmessage4), 3);

					} else if (i == 13) {
						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);
						hisho.setAnimation(anime3);
						hisho.setVisibility(View.VISIBLE);
						window.setVisibility(View.VISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);
						if (flag == 0) {// 表示内容がループします。
							textview.setText(getText(R.string.text_20_1));
							flag++;
						} else if (flag == 1) {
							textview.setText(getText(R.string.text_20_2));
							flag++;
						} else if (flag == 2) {
							textview.setText(getText(R.string.text_20_3));
							flag = 0;
						}
						// 提出ボタンを押すとi=14へ
					} else if (i == 14) {
						// キャラ表示
						hisho.clearAnimation();
						hisho.setVisibility(View.INVISIBLE);
						souri.setAnimation(anime3);
						souri.setVisibility(View.VISIBLE);

						textview.setText(getText(R.string.text_31));
						i = 15;
					} else if (i == 15) {

						Intent intent = new Intent(GameActivity.this,
								StartGameActivity.class);

						intent.putExtra("FLAG", 2);
						intent.putExtra("PLAYER", player);

						// startActivity(intent);
						textview.setText(getText(R.string.text_9999));
						i = 16;
					} else if (i == 16) {

						this.moveTaskToBack(true);

						i = 16;
					}
					mSePlayer.play(mSound[0], 2.0f, 2.0f, 0, 0, 1.0f);
					// デバッグ用
					// Toast.makeText(this, String.valueOf(i),
					// Toast.LENGTH_SHORT)
					// .show();

				} else if (v == finbtn) {
					showDialog(getString(R.string.fintitle),
							getString(R.string.finmessage), 1);
				} else if (v == optionbtn) {

					if (i == 4) {
						showDialog(getString(R.string.optiontitle1),
								getString(R.string.optionmessage1), 3);
					} else if (i == 7) {
						showDialog(getString(R.string.optiontitle2),
								getString(R.string.optionmessage2), 3);
					} else if (i == 10) {
						showDialog(getString(R.string.optiontitle3),
								getString(R.string.optionmessage3), 3);
					} else if (i == 13) {
						showDialog(getString(R.string.optiontitle4),
								getString(R.string.optionmessage4), 3);
					}
				}
			}

			if (f == 2) {// チャプター２
				if (v == button2) {

					if (i == 1) {
						textview.setText(getText(R.string.text_33));
						i = 2;
						// キャラ表示
						hisho.clearAnimation();
						hisho.setVisibility(View.INVISIBLE);
						daijin.clearAnimation();
						daijin.setVisibility(View.INVISIBLE);
						souri.setAnimation(anime3);
						souri.setVisibility(View.VISIBLE);
						// ←ほかの大臣表示？？

						// 背景変更
						layout.setBackgroundResource(R.drawable.yakuin);

						// ハードル5
						// textview2.setText(getText(R.string.optiontitle5));
						// textview2.setVisibility(View.VISIBLE);
						// textview2.startAnimation( set );

					} else if (i == 2) {

						// キャラ表示
						// ←ほかの大臣消去
						textview.setText(getText(R.string.text_34));
						i = 3;

					} else if (i == 3) {

						textview.setText(getText(R.string.text_35));
						i = 4;

						// キャラ表示
						daijin.setAnimation(anime2);
						daijin.setVisibility(View.VISIBLE);

					} else if (i == 4) {// ハードル5

						textview.setText(getText(R.string.text_36));

						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);

						showDialog(getString(R.string.optiontitle5),
								getString(R.string.optionmessage5), 3);
						// オプションボタンで確認

						flag = 0;

					} else if (i == 5) {
						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);
						hisho.setAnimation(anime3);
						hisho.setVisibility(View.VISIBLE);
						window.setVisibility(View.VISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);
						if (flag == 0) {// 表示内容がループします。
							textview.setText(getText(R.string.text_20_1));
							flag++;
						} else if (flag == 1) {
							textview.setText(getText(R.string.text_20_2));
							flag++;
						} else if (flag == 2) {
							textview.setText(getText(R.string.text_20_3));
							flag = 0;
						}
						// 提出ボタンを押したらi=6へ

					} else if (i == 6) {
						textview.setText(getText(R.string.text_38));

						// キャラ表示
						hisho.clearAnimation();
						hisho.setVisibility(View.INVISIBLE);
						daijin.clearAnimation();
						daijin.setVisibility(View.INVISIBLE);
						souri.setAnimation(anime3);
						souri.setVisibility(View.VISIBLE);
						// ←異国の総理表示？？

						// 背景変更←異国風のなにか
						layout.setBackgroundResource(R.drawable.yakuin);

						i = 7;
					} else if (i == 7) {
						textview.setText(getText(R.string.text_39));

						// キャラ表示
						daijin.setAnimation(anime2);
						daijin.setVisibility(View.VISIBLE);
						souri.setAnimation(anime3);
						souri.setVisibility(View.VISIBLE);
						// ←異国の総理消去

						// 背景変更
						layout.setBackgroundResource(R.drawable.yakuin);
						i = 8;

					} else if (i == 8) {// ハードル6
						textview.setText(getText(R.string.text_40));

						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);

						flag = 0;
						showDialog(getString(R.string.optiontitle6),
								getString(R.string.optionmessage6), 3);
					} else if (i == 9) {
						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);
						hisho.setAnimation(anime3);
						hisho.setVisibility(View.VISIBLE);
						window.setVisibility(View.VISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);
						if (flag == 0) {// 表示内容がループします。
							textview.setText(getText(R.string.text_20_1));
							flag++;
						} else if (flag == 1) {
							textview.setText(getText(R.string.text_20_2));
							flag++;
						} else if (flag == 2) {
							textview.setText(getText(R.string.text_20_3));
							flag = 0;
						}
						// 提出ボタンを押したらi=10へ
					} else if (i == 10) {

						Intent intent = new Intent(GameActivity.this,
								StartGameActivity.class);

						intent.putExtra("FLAG", 3);
						intent.putExtra("PLAYER", player);
						startActivity(intent);
					}
					mSePlayer.play(mSound[0], 2.0f, 2.0f, 0, 0, 1.0f);
					// デバッグ用
					// Toast.makeText(this, String.valueOf(i),
					// Toast.LENGTH_SHORT)
					// .show();

				} else if (v == finbtn) {
					showDialog(getString(R.string.fintitle),
							getString(R.string.finmessage), 1);
				} else if (v == optionbtn) {

					if (i == 5) {
						showDialog(getString(R.string.optiontitle5),
								getString(R.string.optionmessage5), 3);
					} else if (i == 9) {
						showDialog(getString(R.string.optiontitle6),
								getString(R.string.optionmessage6), 3);
					}
				}

			} else if (f == 3) {
				if (v == button2) {
					if (i == 1) {
						textview.setText(getText(R.string.text_42));
						i = 2;
						// キャラ表示
						hisho.clearAnimation();
						hisho.setVisibility(View.INVISIBLE);
						daijin.clearAnimation();
						daijin.setVisibility(View.INVISIBLE);
						// ←デモ隊

						// 背景変更←デモ風景
						layout.setBackgroundResource(R.drawable.yakuin);

					} else if (i == 2) {

						// キャラ表示
						daijin.setAnimation(anime2);
						daijin.setVisibility(View.VISIBLE);
						souri.setAnimation(anime3);
						souri.setVisibility(View.VISIBLE);
						// ←デモ隊消去

						// 背景変更
						layout.setBackgroundResource(R.drawable.yakuin);

						textview.setText(getText(R.string.text_43));
						i = 3;

					} else if (i == 3) {// ハードル5

						textview.setText(getText(R.string.text_44));

						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);

						showDialog(getString(R.string.optiontitle7),
								getString(R.string.optionmessage7), 3);
						// オプションボタンで確認

						flag = 0;

					} else if (i == 4) {
						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);
						hisho.setAnimation(anime3);
						hisho.setVisibility(View.VISIBLE);
						window.setVisibility(View.VISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);
						if (flag == 0) {// 表示内容がループします。
							textview.setText(getText(R.string.text_20_1));
							flag++;
						} else if (flag == 1) {
							textview.setText(getText(R.string.text_20_2));
							flag++;
						} else if (flag == 2) {
							textview.setText(getText(R.string.text_20_3));
							flag = 0;
						}
						// 提出ボタンを押したらi=5へ

					} else if (i == 5) {
						// キャラ表示
						hisho.clearAnimation();
						hisho.setVisibility(View.INVISIBLE);
						souri.setAnimation(anime3);
						souri.setVisibility(View.VISIBLE);

						textview.setText(getText(R.string.text_46));

						i = 6;
					} else if (i == 6) {
						textview.setText(getText(R.string.text_47));

						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);

						flag = 0;
						showDialog(getString(R.string.optiontitle8),
								getString(R.string.optionmessage8), 3);

					} else if (i == 7) {
						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);
						hisho.setAnimation(anime3);
						hisho.setVisibility(View.VISIBLE);
						window.setVisibility(View.VISIBLE);

						// 背景変更
						layout.setBackgroundResource(R.drawable.business);
						if (flag == 0) {// 表示内容がループします。
							textview.setText(getText(R.string.text_20_1));
							flag++;
						} else if (flag == 1) {
							textview.setText(getText(R.string.text_20_2));
							flag++;
						} else if (flag == 2) {
							textview.setText(getText(R.string.text_20_3));
							flag = 0;
						}
						// 提出ボタンを押したらi=8へ
					} else if (i == 8) {

						Intent intent = new Intent(GameActivity.this,
								FinActivity.class);
						intent.putExtra("PLAYER", player);
						intent.putExtra("nuclear", para[0]);
						intent.putExtra("colar", para[1]);
						intent.putExtra("gas", para[2]);
						intent.putExtra("solar", para[3]);
						intent.putExtra("FILE", year + (month + 1) + day + hour
								+ minute + second + ".png");
						getViewBitmap(window);

						startActivity(intent);
					}
					mSePlayer.play(mSound[0], 2.0f, 2.0f, 0, 0, 1.0f);
					// デバッグ用
					// Toast.makeText(this, String.valueOf(i),
					// Toast.LENGTH_SHORT)
					// .show();

				} else if (v == finbtn) {
					showDialog(getString(R.string.fintitle),
							getString(R.string.finmessage), 1);
				} else if (v == optionbtn) {

					if (i == 4) {
						showDialog(getString(R.string.optiontitle7),
								getString(R.string.optionmessage7), 3);
					} else if (i == 7) {
						showDialog(getString(R.string.optiontitle8),
								getString(R.string.optionmessage8), 3);
					}
				}

			} else if (f == 999) {
				if (v == button2) {
					if (i == 0) {
						textview.setText(getText(R.string.text_1000));
						i = 1;
					} else if (i == 1) {
						showDialog(getString(R.string.gameover),
								getString(R.string.gameover2), 2);
					}
				}
			} else if (f == 1000) {
				if (v == button2) {
					if (i == 0) {
						i = 1;
						textview.setText(getText(R.string.text_1002));
					} else if (i == 1) {
						i = retry[0];
						f = retry[1];
						textview.setText(getText(R.string.text_1003));

						// キャラ表示
						souri.clearAnimation();
						souri.setVisibility(View.INVISIBLE);
						hisho.setAnimation(anime3);
						hisho.setVisibility(View.VISIBLE);
						layout.setBackgroundResource(R.drawable.business);
						window.setVisibility(View.VISIBLE);
						bgm.start();
					}
				}

			}
		}
	}

}