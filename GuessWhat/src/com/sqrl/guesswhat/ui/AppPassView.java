package com.sqrl.guesswhat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.sqrl.guesswhat.R;

/**
 * 通关界面
 * @author sqrl
 *
 */
public class AppPassView extends Activity{
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.app_passed);
		
		//隐藏titlebar右边的金币显示
		FrameLayout view=(FrameLayout) findViewById(R.id.layout_bar_coin);
		view.setVisibility(View.INVISIBLE);
	}
}
