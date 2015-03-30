package com.sqrl.guesswhat.util;

import android.util.Log;

public class MyLog {
	public static final boolean DEBUG=true; //log等级越高，打印出的信息越少
	
	public static void d(String tag,String message){ //debug
		if(DEBUG){
			Log.d(tag, message);
		}
	}
	
	public static void w(String tag,String message){ //warning
		if(DEBUG){
			Log.w(tag, message);
		}
	}
	
	public static void e(String tag,String message){ //error
		if(DEBUG){
			Log.e(tag, message);
		}
	}
	
	public static void i(String tag,String message){ //info
		if(DEBUG){
			Log.i(tag, message);
		}
	}
}
