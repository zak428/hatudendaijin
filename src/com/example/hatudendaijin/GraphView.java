package com.example.hatudendaijin;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GraphView extends View{

	//各変数の意味はMainActivity参照してください。
	int[] para = {25,25,25,25};
	int[] needs = getResources().getIntArray(R.array.needs);
	int[] sact = getResources().getIntArray(R.array.sact);
	double[] solar = new double[145];
	double[] nuclear = new double[145]; 
	double[] coal = new double[145];
	double[] gas = new double[145]; 

	int tsact = 25518;
	double tLoad = 342478.13;
	double tdLoad = 6693;
	double mLoad = 65;
	double minload = 27;


	public GraphView(Context context, AttributeSet attrs){
		super(context,attrs);
		setFocusable(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.invalidate();
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		//canvas.drawColor(Color.WHITE);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());//MainActivityからpara[]を受け取るためにpreferencesというデータベースを使っています。
		para[0] = sp.getInt("Para0",0);//spというデータベースからPara0という名前の値を読み込み、para[0]に代入しています。
		para[1] = sp.getInt("Para1",50);
		para[2] = sp.getInt("Para2",50);
		para[3] = sp.getInt("Para3",0);
		
		nuclearp();//原子力発電の各時間での発電量を計算しています。
		coalp();//石炭発電の各時間での発電量を計算しています。
		gasp();//天然ガス発電の各時間での発電量を計算しています。
		solarp();//太陽光発電の各時間での発電量を計算しています。


		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);

		Paint paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setColor(Color.BLUE);

		Paint paint3 = new Paint();
		paint3.setAntiAlias(true);
		paint3.setColor(Color.YELLOW);

		Paint paint4 = new Paint();
		paint4.setAntiAlias(true);
		paint4.setColor(Color.GREEN);

		Paint paint5 = new Paint();
		paint5.setAntiAlias(true);
		paint5.setColor(Color.BLACK);

		//以下、グラフ描画用の座標設定です。
		//グラフ域の座標は左上を(0,0)として、右向きをx座標正、下向きをy座標正(注意！)となっているようです。
		//以下の設定はGraphViewがandroid:layout_width="550dp",android:layout_height="500dp"のときいい感じの設定です。
		//レイアウトを変更したときは、下のパラメータを調節するだけでうまく表示できると思います。
		int x = 8;//いちばん左の柱の左端x座標です。
		int dx = 2;//dx-dx2が柱と柱の間隔になります。
		int dx2 = 1;//柱の太さです。
		int y = 155;//各柱の下端のy座標です。大きくすると下の方から柱が始まり、小さくすると上の方から柱が始まるようになります。
		int dy = 2;//発電量に対する柱の高さの比例係数です。大きくすると背が高くなって、小さくすると背が低くなります。
		//
		//
		

		for(int i=0;i<145;i++){	//上の行から順に原子力、石炭、天然ガス、太陽光の各時間の発電量を描画しています。
			canvas.drawRect(x+dx*i, y-Math.round(nuclear[i]*dy), (x+dx2)+dx*i, y, paint);
			canvas.drawRect(x+dx*i, y-Math.round((nuclear[i]+coal[i])*dy), (x+dx2)+dx*i, y-Math.round(nuclear[i]*dy), paint2);
			canvas.drawRect(x+dx*i, y-Math.round((nuclear[i]+coal[i]+gas[i])*dy), (x+dx2)+dx*i, y-Math.round((nuclear[i]+coal[i])*dy), paint3);
			canvas.drawRect(x+dx*i, y-Math.round((nuclear[i]+coal[i]+gas[i]+solar[i])*dy), (x+dx2)+dx*i, y-Math.round((nuclear[i]+coal[i]+gas[i])*dy), paint4);
		}

		for(int i=0;i<144;i++){	//需要曲線を描画しています。
			canvas.drawLine(x+dx*i, y-needs[i]*dy, x+dx*(i+1), y-needs[i+1]*dy, paint5);
		}

		invalidate();

	}

	public void nuclearp(){
		for(int i=0;i<145;i++){
			nuclear[i] = para[0]*tdLoad/14500;
		}
	}

	public void solarp(){
		for(int i=0;i<145;i++){
			solar[i] = para[3]*tdLoad*sact[i]/(100*tsact);
		}
	}

	public void coalp(){
		for(int i=0;i<145;i++){
			coal[i] = para[1]*(needs[i]-solar[i]-nuclear[i])/(100-para[0]-para[3]);
		}
	}

	public void gasp(){
		for(int i=0;i<145;i++){
			gas[i] = para[2]*(needs[i]-solar[i]-nuclear[i])/(100-para[0]-para[3]);
		}
	}



}
