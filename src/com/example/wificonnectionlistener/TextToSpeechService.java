package com.example.wificonnectionlistener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class TextToSpeechService extends Service{

	private TextToSpeech mTextToSpeech;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mTextToSpeech = new TextToSpeech(this, mOnInitListener);
		final String message = intent.getStringExtra("message");
		new Thread(new Runnable() {
			
			public void run() {
				try {
					Thread.sleep(1000);
					mTextToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	OnInitListener mOnInitListener = new OnInitListener() {
		
		public void onInit(int status) {
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mTextToSpeech != null){
			mTextToSpeech.stop();
			mTextToSpeech.shutdown();
		}
	}
}
