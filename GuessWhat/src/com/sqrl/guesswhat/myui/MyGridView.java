package com.sqrl.guesswhat.myui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.sqrl.guesswhat.R;
import com.sqrl.guesswhat.model.IWordButtonClickListener;
import com.sqrl.guesswhat.model.WordButton;
import com.sqrl.guesswhat.util.Util;

public class MyGridView extends GridView{
	public final static int COUNT_WORDS=24;
	
	private ArrayList<WordButton> mWordButtons=new ArrayList<WordButton>();
	private MyGridAdapter mAdapter;
	
	private Context mContext;
	
	private Animation mScaleAnimation;
	
	private IWordButtonClickListener mWordButtonClickListener;
	
	public MyGridView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		
		mContext=context;
		
		mAdapter=new MyGridAdapter();
		this.setAdapter(mAdapter);
	}
	
	public void updateData(ArrayList<WordButton> wordBtns){
		mWordButtons=wordBtns;
		
		//重新设置数据源
		setAdapter(mAdapter);
	}
	
	class MyGridAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mWordButtons.size();
		}

		@Override
		public Object getItem(int position) {
			return mWordButtons.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final WordButton holder;
			
			if(convertView==null){
				convertView=Util.getView(mContext, R.layout.myui_gridview_item);
				
				holder=mWordButtons.get(position);
				
				//加载动画
				mScaleAnimation=AnimationUtils.loadAnimation(mContext, R.anim.scale);
				//设置动画延迟时间，实现每个按钮依次逐个出现
				mScaleAnimation.setStartOffset(position*100);
				
				holder.setIndex(position);
				holder.setViewbutton((Button)convertView.findViewById(R.id.itemBtn));
				
				holder.getViewbutton().setOnClickListener(new View.OnClickListener() { //是个内部类，内部类用的外部类的变量
																						//应该是final的,所以holder设置成final					
					@Override
					public void onClick(View v) {
						mWordButtonClickListener.onWordButtonClick(holder);	
					}
				});
				
				convertView.setTag(holder);
				
			}
			else{
				holder=(WordButton) convertView.getTag();
			}
			
			holder.getViewbutton().setText(holder.getWordString());
			
			//播放动画
			convertView.startAnimation(mScaleAnimation);
			
			return convertView;
		}		
	}
	
	/**
	 * 注册监听接口
	 * @param listener
	 */
	public void registOnWordButtonClick(IWordButtonClickListener listener){
		mWordButtonClickListener=listener;
	}
}
