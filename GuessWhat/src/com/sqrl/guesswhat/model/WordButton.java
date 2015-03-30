package com.sqrl.guesswhat.model;

import android.widget.Button;

/**
 * ÎÄ×Ö°´Å¥
 * 
 * @author sqrl
 *
 */
public class WordButton {
	private int mIndex;
	private boolean mIsVisible;
	private String mWordString;
	
	private Button mViewbutton;
	
	public  WordButton() {
		mIsVisible=true;
		mWordString="";
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int index) {
		this.mIndex = index;
	}

	public boolean isIsVisible() {
		return mIsVisible;
	}

	public void setIsVisible(boolean isVisible) {
		this.mIsVisible = isVisible;
	}
	
	public boolean getIsVisible() {
		return mIsVisible;
	}

	public String getWordString() {
		return mWordString;
	}

	public void setWordString(String wordString) {
		this.mWordString = wordString;
	}

	public Button getViewbutton() {
		return mViewbutton;
	}

	public void setViewbutton(Button viewbutton) {
		this.mViewbutton = viewbutton;
	}
}

