package com.sqrl.guesswhat.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * 音乐播放类
 * @author sqrl
 *
 */
public class MyPlayer {
	
	//音效文件名
	private static final String[] TONE_NAMES={"enter.mp3","cancel.mp3","coin.mp3"};
	
	//音效下标
	public static final int INDEX_TONE_ENTER=0;
	public static final int INDEX_TONE_CANCEL=1;
	public static final int INDEX_TONE_COIN=2;
	
	//音效播放
	private static MediaPlayer[] mToneMediaPlayer=new MediaPlayer[TONE_NAMES.length];
	
	//歌曲播放
	private static MediaPlayer mMusicMediaPlayer; //不用每次播放都新创建一个对象，所以是static(单例模式)
	
	/**
	 * 播放音效
	 * @param context
	 * @param index
	 */
	public static void playTone(Context context,int index){
		//加载声音文件
		AssetManager assetManager=context.getAssets();
		
		if(mToneMediaPlayer[index]==null){
			mToneMediaPlayer[index]=new MediaPlayer();
			try {
				//不需要换音乐，所以放到这个if里，不用reset
				AssetFileDescriptor fileDescriptor=assetManager.openFd(TONE_NAMES[index]);
				mToneMediaPlayer[index].setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getLength());
				mToneMediaPlayer[index].prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mToneMediaPlayer[index].start();
		
	}
	
	/**
	 * 播放音乐
	 * @param context
	 * @param songFileName
	 */
	public static void playSong(Context context,String songFileName){
		if(mMusicMediaPlayer==null){
			mMusicMediaPlayer=new MediaPlayer();
		}
		
		//强制重置，reset成可播放状态(因为可能要换音乐播放了)
		mMusicMediaPlayer.reset();
		
		//加载声音文件
		AssetManager assetManager=context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor=assetManager.openFd(songFileName);
			mMusicMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getLength());
			mMusicMediaPlayer.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//声音播放
		mMusicMediaPlayer.start();
	}
	
	public static void stopSong(Context context){
		if(mMusicMediaPlayer!=null){
			mMusicMediaPlayer.stop();
		}
	}
}
