/****************************************************************************
Copyright (c) 2010-2011 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/
package com.candy.sugar3;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.candy.michelin.Michelin;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.qwert.poiuy.sugar.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.game.UMGameAgent;

public class activity extends Cocos2dxActivity implements RewardedVideoAdListener{

	static {
		System.loadLibrary("game");
	}

	
	public static native void serveto(int result, String message);
	
	public static final int REQUEST_TYPE = 2014;// 自定义值

	public static final int MSG_TYPE_QUIT = 3;
	public static final int MSG_TYPE_UPDATE_ONLINE_CONFIG = 4;
	public static final int MSG_TYPE_LEVEL_STATISTIC = 101;
	public static final int MSG_TYPE_TOAST = 1999;
	public static final int MSG_TYPE_ORDER = 200;

	public static final int MSG_AD_SPOT = 3000;
	public static final int MSG_AD_VEDIO = 3001;

	private static Handler mHandler = null;
	private screenLockReceiver sOnBroadcastReciver = null;

	private InterstitialAd mInterstitialAd;
	private RewardedVideoAd mAd;
	
	public static activity actInstance;
	public static Object getSugar(){
		
		return actInstance;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actInstance = this;
		
		voidFirstResumeError();
		// create a handler to make sure that C++ functions run on UI thread.
		this.createHandler();

		// 注册监听屏幕开关的Receiver
		sOnBroadcastReciver = new screenLockReceiver();
		IntentFilter recevierFilter = new IntentFilter();
		recevierFilter.addAction(Intent.ACTION_SCREEN_ON);
		recevierFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(sOnBroadcastReciver, recevierFilter);
		Michelin.onCreateActivity(this);

		initAd();

		MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_GAME);
		UMGameAgent.init( this );
	}

	private void exitGame() {
		Michelin.requestExit(this, new Michelin.ExitCallback() {
			@Override
			public void onConfirmExit() {
				activity.this.finish();
			}

			@Override
			public void onCancelExit() {
			}
		});
	}


	
	@Override
	protected void onResume() {
		super.onResume();
		Michelin.onResume(this);
		mAd.resume(this);
		UMGameAgent.onResume(this);
	}

	private void voidFirstResumeError() {
		/**
		 * @第0步，防止SD卡安装第一次打开出现Activity按home键支付流程中断 初始化SDK
		 */
		if (!this.isTaskRoot()) {
			Intent mainIntent = getIntent();
			String action = mainIntent.getAction();
			if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER)
					&& action.equals(Intent.ACTION_MAIN)) {
				finish();
				return;// finish()之后该活动会继续执行后面的代码
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Michelin.onPause(this);
		mAd.pause(this);
		UMGameAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Michelin.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Cocos2dxHelper.setAppIsBackground(true);
		
		
	}

	@Override
	protected void onDestroy() {
		if (sOnBroadcastReciver != null) {
			unregisterReceiver(sOnBroadcastReciver);
		}
		Michelin.onDestroy(this);
		super.onDestroy();
		mAd.destroy(this);
	}

	@Override
	public Cocos2dxGLSurfaceView onCreateView() {
		Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
		// main should create stencil buffer
		glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);
		return glSurfaceView;
	}

	public static void callStatisticsAPI(int type, int info) {
		if (mHandler != null) {
			Message msg = mHandler.obtainMessage(MSG_TYPE_LEVEL_STATISTIC);
			msg.arg1 = type;
			msg.arg2 = info;
			mHandler.sendMessage(msg);
		}
	}

	public static void makeToast(int toast) {
		Message msg = mHandler.obtainMessage(MSG_TYPE_TOAST);
		msg.arg1 = toast;
		mHandler.sendMessage(msg);
	}

	public static void placeOrder(int course) {
		Message msg = mHandler.obtainMessage(MSG_TYPE_ORDER);
		msg.arg1 = course;
		mHandler.sendMessage(msg);
	}

	public static void quit() {
		Message msg = mHandler.obtainMessage(MSG_TYPE_QUIT);
		mHandler.sendMessage(msg);
	}

	public static void sendSpot() {
		Message msg = mHandler.obtainMessage(MSG_AD_SPOT);
		mHandler.sendMessage(msg);
	}

	public static void sendVedio() {
		Message msg = mHandler.obtainMessage(MSG_AD_VEDIO);
		mHandler.sendMessage(msg);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static String getOnlineConfigValue(String key) {
		return null;
	}

	public static String getDefaultConfigValue(String key) {
		return "";
	}

	
	public  String getIMSI(){
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId();
		
		return imsi;

	}
	
	public static String getPayid(int key) {
		String myid = "";
		if(key == 8){
			myid = "001";
		}else if(key==9){
			myid="002";
		}else if(key==10){
			myid="003";
		}else if(key==11){
			myid="004";
		}else if(key==12){
			myid="005";
		}else if(key==13){
			myid="006";
		}else if(key==17){
			myid="008";
		}else if(key==18){
			myid="009";
		}else if(key==20){
			myid="010";
		}else if(key==19){
			myid="011";
		}else if(key==1){
			myid="012";
		}else if(key==21){
			myid="013";
		}else if(key==2){
			myid="014";
		}else if(key==14){
			myid="015";
		}
		return myid;
	}

	private void createHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {
				case MSG_TYPE_UPDATE_ONLINE_CONFIG: {
				}
					break;
				case MSG_TYPE_LEVEL_STATISTIC: {
					// 上传关卡的事件信息
					// 1: 开始某关
					// 2: 完成某关
					// 3: 在某关失败
					// 4-8: 在某关使用道具1-5
					// 9: 点击帮助
				}
					break;
				case MSG_TYPE_ORDER: {

					break;
				}
				case MSG_TYPE_QUIT: {
					exitGame();
				}
					break;
				case MSG_TYPE_TOAST: {
					String texts[] = {
							activity.this.getResources().getString(
									R.string.pay_success),
							activity.this.getResources().getString(
									R.string.pay_fail),
							activity.this.getResources().getString(
									R.string.pay_cancel),
							activity.this.getResources().getString(
									R.string.pay_overtime) };
					if (msg.arg1 < texts.length) {
						Toast.makeText(activity.this, texts[msg.arg1],
								Toast.LENGTH_LONG).show();
					}
				}
					break;
				case MSG_AD_SPOT: {
					spot();
				}
				break;

				case MSG_AD_VEDIO: {
					vedio();
				}
				break;

				default:
					break;
				}
			}
		};
	}

	public class screenLockReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null)
				return;
			String action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				// Cocos2dxHelper.setScreenIsOff(false);
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				Cocos2dxHelper.setScreenIsOff(true);
			}
		}
	}


	public void initAd()
	{
		MobileAds.initialize(this, "ca-app-pub-3264772490175149~3337555262");
		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId("ca-app-pub-3264772490175149/9156858151");
		mInterstitialAd.setAdListener(new AdListener(){
			@Override
			public void onAdLoaded() {
				Log.e("-----------", "onAdLoaded");
			}
			@Override
			public void onAdFailedToLoad(int errorCode) {
				Log.e("-----------", "onAdFailedToLoad");
			}
			@Override
			public void onAdOpened() {
				Log.e("-----------", "onAdOpened");
			}
			@Override
			public void onAdLeftApplication() {
				Log.e("-----------", "onAdLeftApplication");
			}
			@Override
			public void onAdClosed() {
				mInterstitialAd.loadAd(new AdRequest.Builder().build());
				Log.e("-----------", "onAdClosed");
			}
		});
		mInterstitialAd.loadAd(new AdRequest.Builder().build());

		mAd = MobileAds.getRewardedVideoAdInstance(this);
		mAd.setRewardedVideoAdListener(this);
	}

	public void spot()
	{
		if(mInterstitialAd.isLoaded())
		{
			mInterstitialAd.show();
		}
		else
		{
			mInterstitialAd.loadAd(new AdRequest.Builder().build());
		}
	}

	public void vedio()
	{
		mAd.loadAd("ca-app-pub-3264772490175149/5713147603", new AdRequest.Builder().build());
	}
	private boolean isAward = false;
	@Override
	public void onRewardedVideoAdLoaded() {
		mAd.show();
		isAward = false;
		Log.e("-----------", "onRewardedVideoAdLoaded");
	}
	@Override
	public void onRewardedVideoAdOpened() {
		Log.e("-----------", "onRewardedVideoAdOpened");
	}
	@Override
	public void onRewardedVideoStarted() {
		Log.e("-----------", "onRewardedVideoStarted");
	}
	@Override
	public void onRewardedVideoAdClosed() {
		if(isAward)
		{
			actInstance.runOnGLThread(new Runnable() {
				@Override
				public void run() {
					Cocos2dxHelper.serve(0,"");
				}
			});
			makeToast(3);
		}
		else
		{
			actInstance.runOnGLThread(new Runnable() {
				@Override
				public void run() {
					Cocos2dxHelper.serve(2,"");
				}
			});
			makeToast(2);
		}

		Log.e("-----------", "onRewardedVideoAdClosed");
	}
	@Override
	public void onRewarded(RewardItem rewardItem) {
		isAward = true;
		Log.e("-----------", "onRewarded");
	}
	@Override
	public void onRewardedVideoAdLeftApplication() {
		Log.e("-----------", "onRewardedVideoAdLeftApplication");
	}
	@Override
	public void onRewardedVideoAdFailedToLoad(int i) {
		actInstance.runOnGLThread(new Runnable() {
			@Override
			public void run() {
				Cocos2dxHelper.serve(1,"");
			}
		});
		makeToast(1);
		Log.e("-----------", "onRewardedVideoAdFailedToLoad"+i);
	}
}
