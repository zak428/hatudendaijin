package com.example.hatudendaijin;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CustomPagerAdapter extends PagerAdapter {
	public final static int N = 5;
	private LayoutInflater _inflater = null;

	public CustomPagerAdapter(Context c) {
		super();
		_inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		LinearLayout layout = (LinearLayout) _inflater.inflate(R.layout.page, null);
		File dir = new File(Environment.getExternalStorageDirectory().getPath()+ "/hatsudendaijin/");
		//指定されたディレクトリのファイル名（ディレクトリ名）を取得
		final File[] filelist = dir.listFiles();
		int brt = 255*position/N;
		layout.setBackgroundColor(Color.rgb(brt,brt,brt));//適当に色をセット(しなくていい)
		ImageView img = (ImageView) layout.findViewById(R.id.img_scroll);





		//int rsrc[] = { R.drawable.city, R.drawable.business, R.drawable.button, R.drawable.hukidasi, R.drawable.hisho };

		img.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+ "/hatsudendaijin/"+filelist[position].getName()));
			    //decodeSampledBitmapFromFile(Environment.getExternalStorageDirectory().getPath()+ "/hatsudendaijin/2015326182130.png", img.getMeasuredWidth(), img.getMeasuredHeight()));

		//img.setImageResource(rsrc[position]);
		container.addView(layout);
		return layout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public int getCount() {
		return N;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {

	    // inJustDecodeBounds=true で画像のサイズをチェック
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(filePath, options);

	    // inSampleSize を計算
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // inSampleSize をセットしてデコード
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(filePath, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

	    // 画像の元サイズ
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        } else {
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        }
	    }
	    return inSampleSize;
	}
}