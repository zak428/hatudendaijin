package com.example.hatudendaijin;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class FailureActivity extends Activity {
	
	float cost = 0;
	float co2 = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_failure);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		cost = sp.getFloat("Cost",0);
		co2 = sp.getFloat("Co2",0);
		TextView textview2 = (TextView)findViewById(R.id.textView2);
		textview2.setText("今年のコストは"+cost+"兆円でした。CO2排出量は"+co2+"Mton-Cでした。");
	}
}
