package com.sqrl.guesswhat.model;

public interface IWordButtonClickListener {//观察者模式的桥梁
	void onWordButtonClick(WordButton wordButton);//里面的方法是想要通过这个桥梁实现的内容	
}
