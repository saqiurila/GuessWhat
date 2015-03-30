package com.sqrl.guesswhat.model;

public class Song {
	private String mName;
	private String mFile;
	private int mNameLength;
	
	public char[] getNameCharacters(){
		return mName.toCharArray();
	}
	
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
		this.mNameLength=name.length();
	}
	public String getFile() {
		return mFile;
	}
	public void setFile(String file) {
		this.mFile = file;
	}
	public int getNameLength() {
		return mNameLength;
	}
}
