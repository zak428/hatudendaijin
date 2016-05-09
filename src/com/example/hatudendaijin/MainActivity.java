package com.example.hatudendaijin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {



	int[] para = {0,50,50,0};//各発電施設の%です。順に{原子力,石炭,天然ガス,太陽光}です。
	double cost = 0;//コストです。
	double co2 = 0;//co2排出量です。

	int[] needs = new int[145];//各時間での電力需要です。
	int[] sact = new int[145];//各自間での太陽光発電の稼働率です。
	double[] nuclear = new double[145]; //各時間での原子力発電の発電量です。
	double[] coal = new double[145];//各時間での石炭発電の発電量です。
	double[] gas = new double[145]; //各時間での天然ガス発電の発電量です。
	double[] solar = new double[145];//各時間での太陽光発電の発電量です。

	int nuclearlimit = 100;//原子力発電の発電できる限界の%です。
	int solarlimit = 100;//太陽光発電の発電できる限界の%です。

	double tLoad = 342478.13;//電力需要の総和です。
	int tsact = 25518;//太陽光発電の稼働率の総和です。
	double tdLoad = 6693;//8月17日(グラフの日)の電力需要の総和です。
	double mLoad = 65;//8月17日(グラフの日)の最大電力需要
	double minload = 27;//8月17日(グラフの日)の最小電力需要

	double[] pf ={50000,44000,40000,60000};//各発電の建設費です。
	double[] pv ={1.1,1.4,5.5,0};//各発電の燃料費です。
	double[] car ={0,0.000085,0.0000485,0};//各発電のco2排出係数です。

	public int p =  para[0]+para[1]+para[2]+para[3];//各発電の%総和です。
	double up = 0;//火力発電の最大出力増率です。
	double down = 0;//火力発電の最大出力減率です。



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		needs = getResources().getIntArray(R.array.needs);//xmlから各時間での電力需要を読み込みます。
		sact = getResources().getIntArray(R.array.sact);//xmlから各時間での電力需要を読み込みます。


		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplication());//グラフ描画のViewである、GraphViewにpara[]を渡すためにpreferencesというデータベースを使っています。
		for(int i=0;i<4;i++){
			sp.edit().putInt("Para"+i, para[i]).commit();//spというデータベースにPara0(1,2,3)という名前でpara[0(1,2,3)]を保存しています。GraphViewではこれを読み込んでいます。
		}

		limitcalculation();//原子力発電、太陽光発電の限界発電%を計算します。

		calculation();//各発電の%総和、コスト、co2排出量、最大出力増減率を計算します。




	}


	//@Override
	//public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.main, menu);
	//	return true;
	//}

	//@Override
	//public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
	//	int id = item.getItemId();
	//	if (id == R.id.action_settings) {
	//		return true;
	//	}
	//	return super.onOptionsItemSelected(item);
	//}

	public void costcal(){	//コスト計算のメソッドです。
		cost = 0;
		for(int i=0;i<4;i++){
			if(i==3){
				cost += (max(solar)/0.6)*pf[i]/1000000;
			}else{
				cost += para[i]*(tLoad*pv[i]+mLoad*pf[i])/100000000;
			}
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplication());
		sp.edit().putFloat("Cost", (float)cost).commit();
	}

	public void co2cal(){	//co2排出量計算のメソッドです。
		co2 = 0;
		for(int i=0;i<4;i++){
			co2 += para[i]*(tLoad*car[i])/100;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplication());
		sp.edit().putFloat("Co2", (float)co2).commit();
	}

	public void nuclearp(){	//原子力発電の各時間での発電量を計算するメソッドです。
		for(int i=0;i<145;i++){
			nuclear[i] = para[0]*tdLoad/14500;
		}
	}

	public void solarp(){	//太陽光発電の各時間での発電量を計算するメソッドです。
		for(int i=0;i<145;i++){
			solar[i] = para[3]*tdLoad*sact[i]/(100*tsact);
		}
	}

	public void coalp(){	//石炭発電の各時間での発電量を計算するメソッドです。
		for(int i=0;i<145;i++){
			coal[i] = para[1]*(needs[i]-solar[i]-nuclear[i])/(100-para[0]-para[3]);
		}
	}

	public void gasp(){	//天然ガス発電の各時間での発電量を計算するメソッドです。
		for(int i=0;i<145;i++){
			gas[i] = para[2]*(needs[i]-solar[i]-nuclear[i])/(100-para[0]-para[3]);
		}
	}

	public void nuclearlimit(){	//原子力の発電限界%を計算するメソッドです。
		double[]ds = new double[145];//配列ds:Load-太陽光発電量
		for(int i=0;i<145;i++){
			ds[i] = needs[i] - solar[i];
		}
		double limit = Math.min(minload,min(ds));
		nuclearlimit = (int)(100*(limit*145/tdLoad)); //原子力発電の限界%
	}

	public void solarlimit(){	//太陽光の発電限界%を計算するメソッドです。
		double[]dn = new double[145];//配列dn:Load-原子力発電量
		for(int i=0;i<145;i++){
			dn[i] = needs[i] - nuclear[i];
			dn[i] = dn[i]/((sact[i]+0.00001)/tsact);
		}
		double limit = min(dn);
		solarlimit = (int)(100*(limit/tdLoad));//太陽光発電の限界%
	}


	public void updowncal(){	//火力発電の最大出力増減率を計算します。
		double[] fire = new double[145];
		for(int i=0;i<145;i++){
			fire[i] = needs[i] - solar[i] - nuclear[i];
		}
		double[] check = new double[144];
		for(int i=0;i<144;i++){
			check[i] = 100*(fire[i+1] - fire[i])/fire[i];
		}
		Arrays.sort(check);
		up = check[143];
		down = check[0];
	}

	public void calculation(){	//各発電の%総和、コスト、co2排出量、最大出力増減率を計算します。
		p = para[0]+para[1]+para[2]+para[3];
		costcal();
		co2cal();
		updowncal();
	}

	public void limitcalculation(){	//原子力発電、太陽光発電の限界発電%を計算します。
		nuclearp();
		solarp();
		nuclearlimit();
		solarlimit();
	}




	public static double min(double[] a) {
		double[]b = new double[a.length];
		for(int i=0;i<a.length;i++){
			b[i]=a[i];
		}
		Arrays.sort(b);
		double min = b[0];
		return min;
	}

	public static double max(double[] a) {
		double[]b = new double[a.length];
		for(int i=0;i<a.length;i++){
			b[i]=a[i];
		}
		Arrays.sort(b);
		double max = b[a.length-1];
		return max;
	}

	public static int minnum(double[] a) {
		double[]b = new double[a.length];
		for(int i=0;i<a.length;i++){
			b[i]=a[i];
		}
		double min = b[0];
		int h = 0;
		for(int i=1;i<a.length;i++){
			if(b[i]<min){
				min = b[i];
				h = i;
			}
		}
		return h;
	}

	public static int maxnum(double[] a) {
		double[]b = new double[a.length];
		for(int i=0;i<a.length;i++){
			b[i]=a[i];
		}
		double max = b[0];
		int h = 0;
		for(int i=1;i<a.length;i++){
			if(b[i]>max){
				max = b[i];
				h = i;
			}
		}
		return h;
	}

	public static double Sisya(double a){	//表示を見やすくするための四捨五入のメソッドです。
		BigDecimal bi = new BigDecimal(String.valueOf(a));
		double b = bi.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		return b;
	}

	public void getViewBitmap(View view){//画面キャプチャ

		final Calendar calendar = Calendar.getInstance();

		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DAY_OF_MONTH);
		final int hour = calendar.get(Calendar.HOUR_OF_DAY);
		final int minute = calendar.get(Calendar.MINUTE);
		final int second = calendar.get(Calendar.SECOND);
		//final int ms = calendar.get(Calendar.MILLISECOND);c

		// キャッシュを有効化
		view.setDrawingCacheEnabled(false);
		view.setDrawingCacheEnabled(true);

		FileOutputStream out = null;

		try {

		    // ファイル生成
		    String path = Environment.getExternalStorageDirectory().getPath()+ "/hatsudendaijin/";
		    File dirs = new File(path);
		    if (!dirs.exists()) {
		        dirs.mkdirs();    //make folders
		    }
		    out = new FileOutputStream(new File(path + year + (month + 1) + day +
				    hour + minute + second +".png"));

		    // ファイルへ出力
		    Bitmap saveBitmap = null;
		    saveBitmap = Bitmap.createBitmap(view.getDrawingCache());
            saveBitmap.compress(CompressFormat.PNG, 100, out);

		    Toast.makeText(this, "スクリーンショットを作成しました",Toast.LENGTH_LONG).show();

		} catch (FileNotFoundException e) {
			Toast.makeText(this, "スクリーンショットを作成できませんでした",Toast.LENGTH_LONG).show();
		} finally {

		    try {
		        if (out != null) {
		            out.close();
		        }
			} catch (IOException e) {
		    }
		}


	}


}
