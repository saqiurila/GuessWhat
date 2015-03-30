package com.sqrl.guesswhat.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * ���ֲ�����
 * @author sqrl
 *
 */
public class MyPlayer {
	
	//��Ч�ļ���
	private static final String[] TONE_NAMES={"enter.mp3","cancel.mp3","coin.mp3"};
	
	//��Ч�±�
	public static final int INDEX_TONE_ENTER=0;
	public static final int INDEX_TONE_CANCEL=1;
	public static final int INDEX_TONE_COIN=2;
	
	//��Ч����
	private static MediaPlayer[] mToneMediaPlayer=new MediaPlayer[TONE_NAMES.length];
	
	//��������
	private static MediaPlayer mMusicMediaPlayer; //����ÿ�β��Ŷ��´���һ������������static(����ģʽ)
	
	/**
	 * ������Ч
	 * @param context
	 * @param index
	 */
	public static void playTone(Context context,int index){
		//���������ļ�
		AssetManager assetManager=context.getAssets();
		
		if(mToneMediaPlayer[index]==null){
			mToneMediaPlayer[index]=new MediaPlayer();
			try {
				//����Ҫ�����֣����Էŵ����if�����reset
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
	 * ��������
	 * @param context
	 * @param songFileName
	 */
	public static void playSong(Context context,String songFileName){
		if(mMusicMediaPlayer==null){
			mMusicMediaPlayer=new MediaPlayer();
		}
		
		//ǿ�����ã�reset�ɿɲ���״̬(��Ϊ����Ҫ�����ֲ�����)
		mMusicMediaPlayer.reset();
		
		//���������ļ�
		AssetManager assetManager=context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor=assetManager.openFd(songFileName);
			mMusicMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getLength());
			mMusicMediaPlayer.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//��������
		mMusicMediaPlayer.start();
	}
	
	public static void stopSong(Context context){
		if(mMusicMediaPlayer!=null){
			mMusicMediaPlayer.stop();
		}
	}
}
