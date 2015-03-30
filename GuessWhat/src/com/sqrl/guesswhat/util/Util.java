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
	
	//��������ķ��������óɾ�̬�ģ�����ÿ���õ�ʱ����new������Ϳ���ʹ������ķ�����
	public static View getView(Context context,int layoutId){
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout=inflater.inflate(layoutId, null);
		return layout;
	}
	
	/**
	 * ������ת
	 */
	public static void startActivity(Context context,Class destination){
		Intent intent=new Intent();
		intent.setClass(context, destination);
		context.startActivity(intent);
		
		//�رյ�ǰ��activity
		((Activity) context).finish();
	}
	
	/**
	 * ��ʾ�Զ���Ի���
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
				//�رնԻ���
				if (mAlertDialog!=null) {
					mAlertDialog.cancel();
				}
				//�¼��ص�
				if(listener!=null){
					listener.onClick();
				}
				//������Ч
				MyPlayer.playTone(context,MyPlayer.INDEX_TONE_ENTER);
			}
		});
		
		btnCancelView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//�رնԻ���
				if (mAlertDialog!=null) {
					mAlertDialog.cancel();
				}
				//������Ч
				MyPlayer.playTone(context,MyPlayer.INDEX_TONE_CANCEL);
			}
		});
		
		//Ϊdialog����view
		builder.setView(dialogView);
		mAlertDialog=builder.create();
		
		//��ʾ�Ի���
		mAlertDialog.show();
	}
	
	/**
	 * ��Ϸ���ݱ���
	 * @param context
	 * @param stageIndex
	 * @param coins
	 */
	public static void saveData(Context context,int stageIndex,int coins){
		FileOutputStream fis=null;
		//�ļ���д���������·��try���д��catch��ץȡ�쳣��finally��ر���
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
	 * ��Ϸ���ݶ�ȡ
	 * @param context
	 * @return
	 */
	public static int[] loadData(Context context){
		FileInputStream fis=null;
		int[] datas={-1,Const.TOTAL_COINS};//��ʼֵ
		
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
