package com.sqrl.guesswhat.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sqrl.guesswhat.R;
import com.sqrl.guesswhat.data.Const;
import com.sqrl.guesswhat.model.IAlertDialogButtonListener;

public class Util {
	
	private static AlertDialog mAlertDialog;
	
	//工具类里的方法都设置成静态的，这样每次用的时候不用new工具类就可以使用里面的方法了
	public static View getView(Context context,int layoutId){
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout=inflater.inflate(layoutId, null);
		return layout;
	}
	
	/**
	 * 界面跳转
	 */
	public static void startActivity(Context context,Class destination){
		Intent intent=new Intent();
		intent.setClass(context, destination);
		context.startActivity(intent);
		
		//关闭当前的activity
		((Activity) context).finish();
	}
	
	/**
	 * 显示自定义对话框
	 * @param context
	 * @param message
	 * @param listener
	 */
	public static void showDialog(final Context context,String message,final IAlertDialogButtonListener listener){
		View dialogView=null;
		
		dialogView=getView(context, R.layout.dialog_view);
		
		AlertDialog.Builder builder=new AlertDialog.Builder(context,R.style.Theme_Transparent);
			
		ImageButton btnOkView=(ImageButton)dialogView.findViewById(R.id.btn_dialog_yes);
		ImageButton btnCancelView=(ImageButton)dialogView.findViewById(R.id.btn_dialog_no);		
		TextView textMessageView=(TextView)dialogView.findViewById(R.id.text_dialog_message);
		
		textMessageView.setText(message);
		
		btnOkView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//关闭对话框
				if (mAlertDialog!=null) {
					mAlertDialog.cancel();
				}
				//事件回调
				if(listener!=null){
					listener.onClick();
				}
				//播放音效
				MyPlayer.playTone(context,MyPlayer.INDEX_TONE_ENTER);
			}
		});
		
		btnCancelView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//关闭对话框
				if (mAlertDialog!=null) {
					mAlertDialog.cancel();
				}
				//播放音效
				MyPlayer.playTone(context,MyPlayer.INDEX_TONE_CANCEL);
			}
		});
		
		//为dialog设置view
		builder.setView(dialogView);
		mAlertDialog=builder.create();
		
		//显示对话框
		mAlertDialog.show();
	}
	
	/**
	 * 游戏数据保存
	 * @param context
	 * @param stageIndex
	 * @param coins
	 */
	public static void saveData(Context context,int stageIndex,int coins){
		FileOutputStream fis=null;
		//文件读写都是这个套路，try里读写，catch里抓取异常，finally里关闭流
		try {
			fis=context.openFileOutput(Const.FILE_NAME_DATA, Context.MODE_PRIVATE);
			DataOutputStream dos=new DataOutputStream(fis);
			
			dos.writeInt(stageIndex);
			dos.writeInt(coins);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 游戏数据读取
	 * @param context
	 * @return
	 */
	public static int[] loadData(Context context){
		FileInputStream fis=null;
		int[] datas={-1,Const.TOTAL_COINS};//初始值
		
		try {
			fis=context.openFileInput(Const.FILE_NAME_DATA);
			DataInputStream dis=new DataInputStream(fis);
			
			datas[Const.INDEX_LOAD_DATA_STAGE]=dis.readInt();
			datas[Const.INDEX_LOAD_DATA_COINS]=dis.readInt();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return datas;
	}
}
