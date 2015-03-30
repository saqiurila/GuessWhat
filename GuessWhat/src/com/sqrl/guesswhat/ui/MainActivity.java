package com.sqrl.guesswhat.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.sqrl.guesswhat.R;
import com.sqrl.guesswhat.data.Const;
import com.sqrl.guesswhat.model.IAlertDialogButtonListener;
import com.sqrl.guesswhat.model.IWordButtonClickListener;
import com.sqrl.guesswhat.model.Song;
import com.sqrl.guesswhat.model.WordButton;
import com.sqrl.guesswhat.myui.MyGridView;
import com.sqrl.guesswhat.util.MyLog;
import com.sqrl.guesswhat.util.MyPlayer;
import com.sqrl.guesswhat.util.Util;

public class MainActivity extends Activity implements IWordButtonClickListener{
	
	public static final String TAG="MainActivity";
	
	// 答案状态
	public static final int STATUS_ANSWER_RIGHT=1;
	public static final int STATUS_ANSWER_WRONG=2;
	public static final int STATUS_ANSWER_LACK=3; //不完整
	
	//文字闪烁次数
	public static final int SPARK_TIMES=5;
	
	//dialog
	public static final int ID_DIALOG_DELETE_WORD=1;
	public static final int ID_DIALOG_TIP_ANSWER=2;
	public static final int ID_DIALOG_LACK_COINS=3;
	
	//唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanLin; //匀速
	
	private Animation mStickInAnim;
	private LinearInterpolator mStickInLin;
	
	private Animation mStickOutAnim;
	private LinearInterpolator mStickOutLin;
	
	private ImageView mViewPan;
	private ImageView mViewStick;
	
	//Play按钮事件
	private ImageButton mBtnPlayStart;
	private boolean isPlaying=false;
	
	//文字框容器
	private ArrayList<WordButton> mAllWords;
	private MyGridView mMyGridView;
	
	private ArrayList<WordButton> mSelectedWords;
	
	//已选择文字框UI容器
	private LinearLayout mViewSelectedWordsContainer;
	
	//过关界面
	private View mPassView;
	
	//当前歌曲
	private Song mCurrentSong;
	private TextView mCurrentSongNamePassView;
	
	//当前关的索引
	private int mCurrentStageIndex=-1;
	private TextView mCurrentStageView;
	private TextView mCurrentStagePassView;
	
	//当前的金币数量
	private int mCurrentCoins=Const.TOTAL_COINS;
	
	//金币View
	private TextView mViewCurrentCoins;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//读取游戏数据
		int[] datas=Util.loadData(this);
		mCurrentStageIndex=datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins=datas[Const.INDEX_LOAD_DATA_COINS];
		
		//初始化控件
		mViewPan=(ImageView) findViewById(R.id.pan);
		mViewStick=(ImageView) findViewById(R.id.stick);
		mBtnPlayStart=(ImageButton) findViewById(R.id.playBtn);
		
		mMyGridView=(MyGridView) findViewById(R.id.myGridView);
		
		mViewCurrentCoins=(TextView)findViewById(R.id.coinNum);
		mViewCurrentCoins.setText(mCurrentCoins+"");
		
		//注册监听
		mMyGridView.registOnWordButtonClick(this);
		
		mViewSelectedWordsContainer=(LinearLayout)findViewById(R.id.word_selected_container);
		
		//初始化动画
		mPanAnim=AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin=new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		mPanAnim.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationStart(Animation animation){
				
			}
			@Override
			public void onAnimationEnd(Animation animation){
				mViewStick.startAnimation(mStickOutAnim);
			}
			@Override
			public void onAnimationRepeat(Animation animation){
				
			}
		});
		
		mStickInAnim=AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mStickInLin=new LinearInterpolator();
		mStickInAnim.setFillAfter(true); //动画结束后留在那个位置
		mStickInAnim.setInterpolator(mStickInLin);
		mStickInAnim.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationStart(Animation animation){
				
			}
			@Override
			public void onAnimationEnd(Animation animation){
				mViewPan.startAnimation(mPanAnim);
			}
			@Override
			public void onAnimationRepeat(Animation animation){
				
			}
		});
		
		mStickOutAnim=AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		mStickOutLin=new LinearInterpolator();
		mStickOutAnim.setFillAfter(true);
		mStickOutAnim.setInterpolator(mStickOutLin);
		mStickOutAnim.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationStart(Animation animation){
				
			}
			@Override
			public void onAnimationEnd(Animation animation){
				isPlaying=false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation animation){
				
			}
		});
		
		//Play按钮事件
		mBtnPlayStart=(ImageButton)findViewById(R.id.playBtn);
		mBtnPlayStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Toast.makeText(MainActivity.this,"Hello",Toast.LENGTH_LONG).show();
				handlePlayButton();
			}
		});
		
		//初始化游戏数据
		initCurrentStageData();
		
		//
		handleDeleteWord();
		handleTipAnswer();
		
	}
	
	/**
	 * 处理播放音乐按钮
	 */
	public void handlePlayButton(){
		if(mViewStick!=null){
			if(!isPlaying){
				isPlaying=true;
				mViewStick.startAnimation(mStickInAnim); //杆开始转动
				mBtnPlayStart.setVisibility(View.INVISIBLE); //隐藏播放按钮
				
				//播放音乐
				MyPlayer.playSong(MainActivity.this, mCurrentSong.getFile());
			}
		}
	}

	@Override
	public void onWordButtonClick(WordButton wordButton) {
		//Toast.makeText(MainActivity.this,wordButton.getIndex()+"",Toast.LENGTH_SHORT).show();
		//播放音效
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_ENTER);
		
		setSelectWord(wordButton);
		
		//获得答案状态
		int checkResult=checkAnswer();
		
		//检查答案
		if(checkResult==STATUS_ANSWER_RIGHT){
			//获得奖励，过关
			handlePassEvent();
		}else if(checkResult==STATUS_ANSWER_WRONG){
			//错误提示（闪烁文字，提示用户）
			sparkWords();
		}else if(checkResult==STATUS_ANSWER_LACK){
			//将答案文字设置为白色
			for(int i=0;i<mSelectedWords.size();i++){
				mSelectedWords.get(i).getViewbutton().setTextColor(Color.WHITE);
			}
		}
		
	}
	
	/**
	 * 处理过关界面及事件
	 */
	private void handlePassEvent(){
		mPassView=(LinearLayout)findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);
		
		//停止未完成的动画
		mViewPan.clearAnimation();
		
		//停止播放音乐
		MyPlayer.stopSong(MainActivity.this);
		
		//播放过关音效（金币）
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_COIN);
		
		//显示当前关索引
		mCurrentStagePassView=(TextView)findViewById(R.id.text_current_stage_pass);
		if(mCurrentStagePassView!=null){
			mCurrentStagePassView.setText((mCurrentStageIndex+1)+"");
		}
		
		//显示当前歌曲名
		mCurrentSongNamePassView=(TextView)findViewById(R.id.text_current_song_name_pass);
		if(mCurrentSongNamePassView!=null){
			mCurrentSongNamePassView.setText(mCurrentSong.getName());
		}
		
		//下一关按键处理
		ImageButton btnPass=(ImageButton)findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(judgeAppPassed()){
					//进入通关界面 
					Util.startActivity(MainActivity.this, AppPassView.class);
				}
				else{
					//进入下一关
					mPassView.setVisibility(View.INVISIBLE);
					//加载关卡数据
					initCurrentStageData();
				}
			}
		});
	}
	
	/**
	 * 判断是否通关
	 * @return
	 */
	private boolean judgeAppPassed(){
		return mCurrentStageIndex==Const.SONG_INFO.length-1;
	}
	
	private void clearAnwser(WordButton button){
		button.getViewbutton().setText("");
		button.setWordString("");
		button.setIsVisible(false);
		
		setButtonVisibility(mAllWords.get(button.getIndex()),View.VISIBLE);
	}
	
	/**
	 * 设置答案
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton){
		for(int i=0;i<mSelectedWords.size();i++){
			if(mSelectedWords.get(i).getWordString().length()==0){
				//设置答案文字框内容及可见性
				mSelectedWords.get(i).getViewbutton().setText(wordButton.getWordString());
				mSelectedWords.get(i).setIsVisible(true);
				mSelectedWords.get(i).setWordString(wordButton.getWordString());
				//记录索引
				mSelectedWords.get(i).setIndex(wordButton.getIndex());
				
				//log
				MyLog.d(TAG, mSelectedWords.get(i).getIndex()+"");
				
				//设置待选框的可见性
				setButtonVisibility(wordButton,View.INVISIBLE);
				
				break;
			}
		}
	}
	
	private void setButtonVisibility(WordButton button,int visibility){
		button.getViewbutton().setVisibility(visibility);
		button.setIsVisible(visibility==View.VISIBLE?true:false);
		
		//log
		MyLog.d(TAG, button.getIsVisible()+"");
	}
	
	@Override
	public void onPause(){ //应用切换到后台时
		//保存游戏数据
		Util.saveData(MainActivity.this, mCurrentStageIndex-1, mCurrentCoins);
		
		mViewPan.clearAnimation();
		
		MyPlayer.stopSong(MainActivity.this);
		
		super.onPause();
	}
	
	private Song loadStageSongInfo(int stageIndex){
		Song song=new Song();
		
		String[] stageSongInfo=Const.SONG_INFO[stageIndex];
		song.setFile(stageSongInfo[Const.INDEX_FILE_NAME]);
		song.setName(stageSongInfo[Const.INDEX_SONG_NAME]);
		
		return song;
	}
	
	/**
	 * 加载当前关卡的数据
	 */
	private void initCurrentStageData(){
		//读取当前关卡歌曲的信息
		mCurrentSong=loadStageSongInfo(++mCurrentStageIndex);
		
		//初始化已选择框
		mSelectedWords=initSelectWord();
		
		LayoutParams params=new LayoutParams(140,140);	
		
		//清空上一关的答案框
		mViewSelectedWordsContainer.removeAllViews();
		
		//增加新的答案框
		for(int i=0;i<mSelectedWords.size();i++){
			mViewSelectedWordsContainer.addView(mSelectedWords.get(i).getViewbutton(),params);
		}
		
		//显示当前关的索引
		mCurrentStageView=(TextView)findViewById(R.id.text_current_stage);
		if(mCurrentStageView!=null){
			mCurrentStageView.setText((mCurrentStageIndex+1)+"");
		}
		
		//获得数据
		mAllWords=initAllWord();
		//更新数据-myGridView
		mMyGridView.updateData(mAllWords);	
		
		//自动播放音乐
		handlePlayButton();
	}
	
	/**
	 * 初始化待选文字框
	 * @return
	 */
	private ArrayList<WordButton> initAllWord(){
		ArrayList<WordButton> data=new ArrayList<WordButton>();
		
		//获得所有待选文字
		String[] words=generateWords();
		
		//将文字赋给buttons
		for(int i=0;i<MyGridView.COUNT_WORDS;i++){
			WordButton button=new WordButton();
			
			button.setWordString(words[i]);
			
			data.add(button);
		}
		
		return data;
	}
	
	/**
	 * 初始化已选文字框
	 * @return
	 */
	private ArrayList<WordButton> initSelectWord(){
		ArrayList<WordButton> data=new ArrayList<WordButton>();
		
		for(int i=0;i<mCurrentSong.getNameLength();i++){
			View view=Util.getView(MainActivity.this, R.layout.myui_gridview_item);
			
			final WordButton holder=new WordButton();
			
			holder.setViewbutton((Button) view.findViewById(R.id.itemBtn));
			holder.getViewbutton().setTextColor(Color.WHITE);
			holder.getViewbutton().setText("");
			holder.setIsVisible(false);
			
			holder.getViewbutton().setBackgroundResource(R.drawable.game_wordblank);
			
			holder.getViewbutton().setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					clearAnwser(holder);
					MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_CANCEL);
				}
			});
			
			data.add(holder);
		}
		
		return data;
	}
	
	/**
	 * 生成所有的待选文字
	 * @return
	 */
	public String[] generateWords(){
		String[] words=new String[MyGridView.COUNT_WORDS];
		Random random=new Random();
		
		//存入歌名
		for(int i=0;i<mCurrentSong.getNameLength();i++){
			words[i]=mCurrentSong.getNameCharacters()[i]+"";
		}
		
		//存入随机生成的文字
		for(int i=mCurrentSong.getNameLength();i<MyGridView.COUNT_WORDS;i++){
			words[i]=getRandomChar()+"";
		}
		
		//打乱文字顺序：1. 从所有元素中随机选取一个，与第一个元素进行交换
		//2. 从第二个之后的元素中选一个，与第二个交换
		//这样能够保证，每个元素在每个位置出现的概率都是1/n
		for(int i=MyGridView.COUNT_WORDS-1;i>=0;i--){
			int index=random.nextInt(i+1);
			
			String buf=words[index];
			words[index]=words[i];
			words[i]=buf;
		}
		
		return words;
	}
	
	/**
	 * 生成随机汉字
	 * @return
	 */
	public char getRandomChar(){
		String str="";
		int highPos;
		int lowPos;
		
		Random random=new Random();
		
		highPos=176+Math.abs(random.nextInt(39));
		lowPos=161+Math.abs(random.nextInt(93));
		
		byte[] b=new byte[2];
		b[0]=(Integer.valueOf(highPos)).byteValue();
		b[1]=(Integer.valueOf(lowPos)).byteValue();
		
		try {
			str=new String(b,"GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return str.charAt(0);
	}
	
	/**
	 * 检查答案状态
	 * @return
	 */
	private int checkAnswer(){
		//检查长度
		for(int i=0;i<mSelectedWords.size();i++){
			if(mSelectedWords.get(i).getWordString().length()==0){
				return STATUS_ANSWER_LACK;
			}
		}
		//检查答案正确性
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<mSelectedWords.size();i++){
			sb.append(mSelectedWords.get(i).getWordString());
		}
		
		return sb.toString().equals(mCurrentSong.getName())?STATUS_ANSWER_RIGHT:STATUS_ANSWER_WRONG;
	}
	
	/**
	 * 闪烁文字
	 */
	private void sparkWords(){
		//定时器
		TimerTask task=new TimerTask(){
			boolean mChange=false;
			int mSparkTimes=0;
			
			@Override
			public void run() {
				//Timer会新建一个线程，但安卓开发中对界面的修改必须在主的Activity（UI线程）中
				//安卓提供了一些方法，保证界面修改是在UI线程中执行
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						if(mSparkTimes>SPARK_TIMES){
							return;
						}
						//执行闪烁逻辑：交替显示为红色和白色文字
						for(int i=0;i<mSelectedWords.size();i++){
							mSelectedWords.get(i).getViewbutton().setTextColor(mChange?Color.RED:Color.WHITE);
						}
						mChange=!mChange;
						mSparkTimes++;
					}					
				});
			}
		};
		
		Timer timer=new Timer();
		timer.schedule(task,1,150);
	}
	
	/**
	 * 自动选择一个答案
	 */
	private void tipAnswer(){		
		boolean tipWord=false;
		for(int i=0;i<mSelectedWords.size();i++){
			if(mSelectedWords.get(i).getWordString().length()==0){
				onWordButtonClick(findAAnswerWord(i));				
				tipWord=true;
				
				//减少金币
				if(!handleCoins(-getTipAnswerCoins())){
					//金币不够，显示提示对话框
					showConfirmDialog(ID_DIALOG_LACK_COINS);
					return;
				}
				break;
			}
		}
		//已选文字框中没有空的
		if(!tipWord){
			sparkWords();
		}
	}
	
	/**
	 * 删除文字
	 */
	private void deleteOneWord(){
		//减少金币
		if(!handleCoins(-getDeleteWordCoins())){
			//金币不够，显示提示对话框
			return;
		}
		//将这个索引对应的WordButton设为不可见
		setButtonVisibility(findNotAnswerWord(),View.INVISIBLE);
	}
	
	/**
	 * 找到一个不是答案的文字，并且当前是可见的
	 * @return
	 */
	private WordButton findNotAnswerWord(){
		Random random=new Random();
		WordButton temp=null;
		
		while (true) {
			int index=random.nextInt(MyGridView.COUNT_WORDS);
			
			temp=mAllWords.get(index);
			
			if(temp.getIsVisible()&&!isAAnswerWord(temp)){
				return temp;
			}
		}	
	}
	
	/**
	 * 找到一个答案文字
	 * @param position 当前需要填入答案的框的索引
	 * @return
	 */
	private WordButton findAAnswerWord(int position){
		WordButton temp=null;
		for(int i=0;i<MyGridView.COUNT_WORDS;i++){
			temp=mAllWords.get(i);
			if(temp.getWordString().equals(mCurrentSong.getNameCharacters()[position]+"")){
				return temp;
			}
		}
		return null;
	}
	
	/**
	 * 判断某个文字是否为答案
	 * @param button
	 * @return
	 */
	private boolean isAAnswerWord(WordButton button){
		boolean result=false;
		
		for(int i=0;i<mCurrentSong.getNameLength();i++){
			if(button.getWordString().equals(mCurrentSong.getNameCharacters()[i]+"")){
				result=true;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * 增加或减少指定数量的金币
	 */
	private boolean handleCoins(int num){
		//判断当前金币数量是否可被减少
		if(mCurrentCoins+num>=0){
			mCurrentCoins+=num;
			//修改显示的金币数量
			mViewCurrentCoins.setText(mCurrentCoins+"");
			return true;
		}
		else{
			//金币不够
			return false;
		}
	}
	
	/**
	 * 从配置文件中获取删除待选文字事件所花费的金币数量
	 * @return
	 */
	private int getDeleteWordCoins(){
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}
	
	/**
	 * 处理删除待选文字事件
	 */
	private void handleDeleteWord(){
		ImageButton button=(ImageButton)findViewById(R.id.btn_delete_word);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//deleteOneWord();
				showConfirmDialog(ID_DIALOG_DELETE_WORD);
			}
		});
	}	

	/**
	 * 从配置文件中获取提示答案事件所花费的金币数量
	 * @return
	 */
	private int getTipAnswerCoins(){
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}
	
	/**
	 * 处理提示按钮事件
	 */
	private void handleTipAnswer(){
		ImageButton button=(ImageButton)findViewById(R.id.btn_tip_answer);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//tipAnswer();
				showConfirmDialog(ID_DIALOG_TIP_ANSWER);
			}
		});
	}
	
	//自定义dialog的事件响应
	//1.删除错误答案
	private IAlertDialogButtonListener mBtnOkDeleteWordListener=new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// 执行事件
			deleteOneWord();
		}		
	};
	//2.答案提示
	private IAlertDialogButtonListener mBtnOkTipAnswerListener=new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// 执行事件
			tipAnswer();
		}		
	};
	//3.金币不足
	private IAlertDialogButtonListener mBtnOkLackCoinsListener=new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// 执行事件
			
		}		
	};
	
	/**
	 * 显示对话框
	 * @param id
	 */
	private void showConfirmDialog(int id){
		switch (id) {
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this, "确认花掉"+getDeleteWordCoins()+"个金币去掉一个错误答案？", mBtnOkDeleteWordListener);
			break;
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this, "确认花掉"+getTipAnswerCoins()+"个金币获得一个文字提示？", mBtnOkTipAnswerListener);
			break;	
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this, "金币不足，去商店补充", mBtnOkLackCoinsListener);
			break;
		default:
			break;
		}
	}
}
