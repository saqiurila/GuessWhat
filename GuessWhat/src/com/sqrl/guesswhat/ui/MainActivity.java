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
	
	// ��״̬
	public static final int STATUS_ANSWER_RIGHT=1;
	public static final int STATUS_ANSWER_WRONG=2;
	public static final int STATUS_ANSWER_LACK=3; //������
	
	//������˸����
	public static final int SPARK_TIMES=5;
	
	//dialog
	public static final int ID_DIALOG_DELETE_WORD=1;
	public static final int ID_DIALOG_TIP_ANSWER=2;
	public static final int ID_DIALOG_LACK_COINS=3;
	
	//��Ƭ��ض���
	private Animation mPanAnim;
	private LinearInterpolator mPanLin; //����
	
	private Animation mStickInAnim;
	private LinearInterpolator mStickInLin;
	
	private Animation mStickOutAnim;
	private LinearInterpolator mStickOutLin;
	
	private ImageView mViewPan;
	private ImageView mViewStick;
	
	//Play��ť�¼�
	private ImageButton mBtnPlayStart;
	private boolean isPlaying=false;
	
	//���ֿ�����
	private ArrayList<WordButton> mAllWords;
	private MyGridView mMyGridView;
	
	private ArrayList<WordButton> mSelectedWords;
	
	//��ѡ�����ֿ�UI����
	private LinearLayout mViewSelectedWordsContainer;
	
	//���ؽ���
	private View mPassView;
	
	//��ǰ����
	private Song mCurrentSong;
	private TextView mCurrentSongNamePassView;
	
	//��ǰ�ص�����
	private int mCurrentStageIndex=-1;
	private TextView mCurrentStageView;
	private TextView mCurrentStagePassView;
	
	//��ǰ�Ľ������
	private int mCurrentCoins=Const.TOTAL_COINS;
	
	//���View
	private TextView mViewCurrentCoins;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//��ȡ��Ϸ����
		int[] datas=Util.loadData(this);
		mCurrentStageIndex=datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins=datas[Const.INDEX_LOAD_DATA_COINS];
		
		//��ʼ���ؼ�
		mViewPan=(ImageView) findViewById(R.id.pan);
		mViewStick=(ImageView) findViewById(R.id.stick);
		mBtnPlayStart=(ImageButton) findViewById(R.id.playBtn);
		
		mMyGridView=(MyGridView) findViewById(R.id.myGridView);
		
		mViewCurrentCoins=(TextView)findViewById(R.id.coinNum);
		mViewCurrentCoins.setText(mCurrentCoins+"");
		
		//ע�����
		mMyGridView.registOnWordButtonClick(this);
		
		mViewSelectedWordsContainer=(LinearLayout)findViewById(R.id.word_selected_container);
		
		//��ʼ������
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
		mStickInAnim.setFillAfter(true); //���������������Ǹ�λ��
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
		
		//Play��ť�¼�
		mBtnPlayStart=(ImageButton)findViewById(R.id.playBtn);
		mBtnPlayStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Toast.makeText(MainActivity.this,"Hello",Toast.LENGTH_LONG).show();
				handlePlayButton();
			}
		});
		
		//��ʼ����Ϸ����
		initCurrentStageData();
		
		//
		handleDeleteWord();
		handleTipAnswer();
		
	}
	
	/**
	 * ���������ְ�ť
	 */
	public void handlePlayButton(){
		if(mViewStick!=null){
			if(!isPlaying){
				isPlaying=true;
				mViewStick.startAnimation(mStickInAnim); //�˿�ʼת��
				mBtnPlayStart.setVisibility(View.INVISIBLE); //���ز��Ű�ť
				
				//��������
				MyPlayer.playSong(MainActivity.this, mCurrentSong.getFile());
			}
		}
	}

	@Override
	public void onWordButtonClick(WordButton wordButton) {
		//Toast.makeText(MainActivity.this,wordButton.getIndex()+"",Toast.LENGTH_SHORT).show();
		//������Ч
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_ENTER);
		
		setSelectWord(wordButton);
		
		//��ô�״̬
		int checkResult=checkAnswer();
		
		//����
		if(checkResult==STATUS_ANSWER_RIGHT){
			//��ý���������
			handlePassEvent();
		}else if(checkResult==STATUS_ANSWER_WRONG){
			//������ʾ����˸���֣���ʾ�û���
			sparkWords();
		}else if(checkResult==STATUS_ANSWER_LACK){
			//������������Ϊ��ɫ
			for(int i=0;i<mSelectedWords.size();i++){
				mSelectedWords.get(i).getViewbutton().setTextColor(Color.WHITE);
			}
		}
		
	}
	
	/**
	 * ������ؽ��漰�¼�
	 */
	private void handlePassEvent(){
		mPassView=(LinearLayout)findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);
		
		//ֹͣδ��ɵĶ���
		mViewPan.clearAnimation();
		
		//ֹͣ��������
		MyPlayer.stopSong(MainActivity.this);
		
		//���Ź�����Ч����ң�
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_COIN);
		
		//��ʾ��ǰ������
		mCurrentStagePassView=(TextView)findViewById(R.id.text_current_stage_pass);
		if(mCurrentStagePassView!=null){
			mCurrentStagePassView.setText((mCurrentStageIndex+1)+"");
		}
		
		//��ʾ��ǰ������
		mCurrentSongNamePassView=(TextView)findViewById(R.id.text_current_song_name_pass);
		if(mCurrentSongNamePassView!=null){
			mCurrentSongNamePassView.setText(mCurrentSong.getName());
		}
		
		//��һ�ذ�������
		ImageButton btnPass=(ImageButton)findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(judgeAppPassed()){
					//����ͨ�ؽ��� 
					Util.startActivity(MainActivity.this, AppPassView.class);
				}
				else{
					//������һ��
					mPassView.setVisibility(View.INVISIBLE);
					//���عؿ�����
					initCurrentStageData();
				}
			}
		});
	}
	
	/**
	 * �ж��Ƿ�ͨ��
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
	 * ���ô�
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton){
		for(int i=0;i<mSelectedWords.size();i++){
			if(mSelectedWords.get(i).getWordString().length()==0){
				//���ô����ֿ����ݼ��ɼ���
				mSelectedWords.get(i).getViewbutton().setText(wordButton.getWordString());
				mSelectedWords.get(i).setIsVisible(true);
				mSelectedWords.get(i).setWordString(wordButton.getWordString());
				//��¼����
				mSelectedWords.get(i).setIndex(wordButton.getIndex());
				
				//log
				MyLog.d(TAG, mSelectedWords.get(i).getIndex()+"");
				
				//���ô�ѡ��Ŀɼ���
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
	public void onPause(){ //Ӧ���л�����̨ʱ
		//������Ϸ����
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
	 * ���ص�ǰ�ؿ�������
	 */
	private void initCurrentStageData(){
		//��ȡ��ǰ�ؿ���������Ϣ
		mCurrentSong=loadStageSongInfo(++mCurrentStageIndex);
		
		//��ʼ����ѡ���
		mSelectedWords=initSelectWord();
		
		LayoutParams params=new LayoutParams(140,140);	
		
		//�����һ�صĴ𰸿�
		mViewSelectedWordsContainer.removeAllViews();
		
		//�����µĴ𰸿�
		for(int i=0;i<mSelectedWords.size();i++){
			mViewSelectedWordsContainer.addView(mSelectedWords.get(i).getViewbutton(),params);
		}
		
		//��ʾ��ǰ�ص�����
		mCurrentStageView=(TextView)findViewById(R.id.text_current_stage);
		if(mCurrentStageView!=null){
			mCurrentStageView.setText((mCurrentStageIndex+1)+"");
		}
		
		//�������
		mAllWords=initAllWord();
		//��������-myGridView
		mMyGridView.updateData(mAllWords);	
		
		//�Զ���������
		handlePlayButton();
	}
	
	/**
	 * ��ʼ����ѡ���ֿ�
	 * @return
	 */
	private ArrayList<WordButton> initAllWord(){
		ArrayList<WordButton> data=new ArrayList<WordButton>();
		
		//������д�ѡ����
		String[] words=generateWords();
		
		//�����ָ���buttons
		for(int i=0;i<MyGridView.COUNT_WORDS;i++){
			WordButton button=new WordButton();
			
			button.setWordString(words[i]);
			
			data.add(button);
		}
		
		return data;
	}
	
	/**
	 * ��ʼ����ѡ���ֿ�
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
	 * �������еĴ�ѡ����
	 * @return
	 */
	public String[] generateWords(){
		String[] words=new String[MyGridView.COUNT_WORDS];
		Random random=new Random();
		
		//�������
		for(int i=0;i<mCurrentSong.getNameLength();i++){
			words[i]=mCurrentSong.getNameCharacters()[i]+"";
		}
		
		//����������ɵ�����
		for(int i=mCurrentSong.getNameLength();i<MyGridView.COUNT_WORDS;i++){
			words[i]=getRandomChar()+"";
		}
		
		//��������˳��1. ������Ԫ�������ѡȡһ�������һ��Ԫ�ؽ��н���
		//2. �ӵڶ���֮���Ԫ����ѡһ������ڶ�������
		//�����ܹ���֤��ÿ��Ԫ����ÿ��λ�ó��ֵĸ��ʶ���1/n
		for(int i=MyGridView.COUNT_WORDS-1;i>=0;i--){
			int index=random.nextInt(i+1);
			
			String buf=words[index];
			words[index]=words[i];
			words[i]=buf;
		}
		
		return words;
	}
	
	/**
	 * �����������
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
	 * ����״̬
	 * @return
	 */
	private int checkAnswer(){
		//��鳤��
		for(int i=0;i<mSelectedWords.size();i++){
			if(mSelectedWords.get(i).getWordString().length()==0){
				return STATUS_ANSWER_LACK;
			}
		}
		//������ȷ��
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<mSelectedWords.size();i++){
			sb.append(mSelectedWords.get(i).getWordString());
		}
		
		return sb.toString().equals(mCurrentSong.getName())?STATUS_ANSWER_RIGHT:STATUS_ANSWER_WRONG;
	}
	
	/**
	 * ��˸����
	 */
	private void sparkWords(){
		//��ʱ��
		TimerTask task=new TimerTask(){
			boolean mChange=false;
			int mSparkTimes=0;
			
			@Override
			public void run() {
				//Timer���½�һ���̣߳�����׿�����жԽ�����޸ı���������Activity��UI�̣߳���
				//��׿�ṩ��һЩ��������֤�����޸�����UI�߳���ִ��
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						if(mSparkTimes>SPARK_TIMES){
							return;
						}
						//ִ����˸�߼���������ʾΪ��ɫ�Ͱ�ɫ����
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
	 * �Զ�ѡ��һ����
	 */
	private void tipAnswer(){		
		boolean tipWord=false;
		for(int i=0;i<mSelectedWords.size();i++){
			if(mSelectedWords.get(i).getWordString().length()==0){
				onWordButtonClick(findAAnswerWord(i));				
				tipWord=true;
				
				//���ٽ��
				if(!handleCoins(-getTipAnswerCoins())){
					//��Ҳ�������ʾ��ʾ�Ի���
					showConfirmDialog(ID_DIALOG_LACK_COINS);
					return;
				}
				break;
			}
		}
		//��ѡ���ֿ���û�пյ�
		if(!tipWord){
			sparkWords();
		}
	}
	
	/**
	 * ɾ������
	 */
	private void deleteOneWord(){
		//���ٽ��
		if(!handleCoins(-getDeleteWordCoins())){
			//��Ҳ�������ʾ��ʾ�Ի���
			return;
		}
		//�����������Ӧ��WordButton��Ϊ���ɼ�
		setButtonVisibility(findNotAnswerWord(),View.INVISIBLE);
	}
	
	/**
	 * �ҵ�һ�����Ǵ𰸵����֣����ҵ�ǰ�ǿɼ���
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
	 * �ҵ�һ��������
	 * @param position ��ǰ��Ҫ����𰸵Ŀ������
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
	 * �ж�ĳ�������Ƿ�Ϊ��
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
	 * ���ӻ����ָ�������Ľ��
	 */
	private boolean handleCoins(int num){
		//�жϵ�ǰ��������Ƿ�ɱ�����
		if(mCurrentCoins+num>=0){
			mCurrentCoins+=num;
			//�޸���ʾ�Ľ������
			mViewCurrentCoins.setText(mCurrentCoins+"");
			return true;
		}
		else{
			//��Ҳ���
			return false;
		}
	}
	
	/**
	 * �������ļ��л�ȡɾ����ѡ�����¼������ѵĽ������
	 * @return
	 */
	private int getDeleteWordCoins(){
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}
	
	/**
	 * ����ɾ����ѡ�����¼�
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
	 * �������ļ��л�ȡ��ʾ���¼������ѵĽ������
	 * @return
	 */
	private int getTipAnswerCoins(){
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}
	
	/**
	 * ������ʾ��ť�¼�
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
	
	//�Զ���dialog���¼���Ӧ
	//1.ɾ�������
	private IAlertDialogButtonListener mBtnOkDeleteWordListener=new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// ִ���¼�
			deleteOneWord();
		}		
	};
	//2.����ʾ
	private IAlertDialogButtonListener mBtnOkTipAnswerListener=new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// ִ���¼�
			tipAnswer();
		}		
	};
	//3.��Ҳ���
	private IAlertDialogButtonListener mBtnOkLackCoinsListener=new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// ִ���¼�
			
		}		
	};
	
	/**
	 * ��ʾ�Ի���
	 * @param id
	 */
	private void showConfirmDialog(int id){
		switch (id) {
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this, "ȷ�ϻ���"+getDeleteWordCoins()+"�����ȥ��һ������𰸣�", mBtnOkDeleteWordListener);
			break;
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this, "ȷ�ϻ���"+getTipAnswerCoins()+"����һ��һ��������ʾ��", mBtnOkTipAnswerListener);
			break;	
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this, "��Ҳ��㣬ȥ�̵겹��", mBtnOkLackCoinsListener);
			break;
		default:
			break;
		}
	}
}
