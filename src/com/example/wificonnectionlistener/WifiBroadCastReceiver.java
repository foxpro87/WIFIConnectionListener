package com.example.wificonnectionlistener;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class WifiBroadCastReceiver extends BroadcastReceiver {

	private WifiManager mWifiManager;
	private Handler mHandler = new Handler();
	private String message;
	@Override
	public void onReceive(final Context context, final Intent intent) {

		new Thread(new Runnable() {

			public void run() {

				// wait for a second.
				try {
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) { 
					ConnectivityManager manager = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
					if(manager != null){
						if(isNetworkAvailable(manager)){
							NetworkInfo info = manager.getActiveNetworkInfo();
			                String typeName = info.getTypeName();  
			                
			                message = "Network Type is "+typeName;
							UtterText(context, message);
							 
							mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
							int state = mWifiManager.getWifiState();

							switch (state) {
							case WifiManager.WIFI_STATE_DISABLED:
								message = "Wifi connection lost.";
								UtterText(context, message);
								break;

							case WifiManager.WIFI_STATE_ENABLED:
								if(typeName.equalsIgnoreCase("WIFI")){
									getAvailableWifiScanResults(context);
									getConnectedNetWorkInformation(context);
								}
								break;
							case WifiManager.WIFI_STATE_DISABLING:
								message = "Wifi Disabling...";
								UtterText(context, message);
								break;
							case WifiManager.WIFI_STATE_ENABLING:
								message = "Wifi Enabling...";
								UtterText(context, message);
								break;
							case WifiManager.WIFI_STATE_UNKNOWN:
								message = "Wifi state unknown.";
								UtterText(context, message);
								break;
							}
						}
						else{
							 message = "Data Connection Lost.";
							 UtterText(context, message);
						}
					}
				}
			}
		}).start();
	}
	
	/***
	 * Checks Data Connection.
	 * 
	 * @param manager
	 * @return true if there is data connection else returns false.
	 */
	private boolean isNetworkAvailable(ConnectivityManager manager) {
		NetworkInfo info = manager.getActiveNetworkInfo();
		if(info != null){
			return info.isConnectedOrConnecting();
		}
		return false;
	}
	
	/***
	 * 
	 *Scans List of Available Wifi Networks 
	 * 
	 **/
	private void getAvailableWifiScanResults(Context mContext) {
		List<ScanResult> mScanResults = mWifiManager.getScanResults();
		ScanResult bestResult = null;
		
		if(mScanResults != null){
			for(ScanResult results : mScanResults){
				Log.d("Available Networks", results.SSID);
				if(bestResult == null || WifiManager.compareSignalLevel(bestResult.level, results.level) < 0){
					bestResult = results;
				}
			}
			
			if(mScanResults.size() > 0 && bestResult != null){
				String message = String.format("%s networks found. %s is the strongest.", mScanResults.size(), bestResult.SSID);
				UtterText(mContext, message);
			}
		}
	}
	
	/***
	 * 
	 *gets the currently connected Wifi Networks Information.
	 * 
	 **/
	private void getConnectedNetWorkInformation(Context mContext) {
		WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
		String message;
		if(mWifiInfo != null){
			String ssid = mWifiInfo.getSSID();
			int wifiSignalStrength = WifiManager.calculateSignalLevel(mWifiInfo.getRssi(), 4);
			int link_speed = mWifiInfo.getLinkSpeed();
			message = String.format("connected to %s with signal strength %s and link speed of %s", ssid, wifiSignalStrength, link_speed);
		}
		else{
			message = "Not Conntected to Any Wifi Network";
		}
		UtterText(mContext, message);		
	}
	
	/***
	 * 
	 *sends the text to TextToSpeech Service
	 * 
	 **/
	private void UtterText(Context mContext, String message) {
		 mHandler.post(new MyRunnable(message, mContext));
		Intent tts_intent = new Intent(mContext, TextToSpeechService.class);
		tts_intent.putExtra("message", message);
		mContext.startService(tts_intent);
	}
	
	class MyRunnable implements Runnable
	{
		String data;
		Context mContext;
		
		public MyRunnable(String data, Context mContext) {
			this.data = data;
			this.mContext = mContext;
		}
		
		public void run() {
			Toast.makeText(mContext, data, Toast.LENGTH_LONG).show();
		}
	}
}
