package com.candy.michelin;

import java.util.ArrayList;
import java.util.HashMap;

import com.candy.michelin.Michelin.Callback;
import com.candy.sugar.analytics.game.GameAgent;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

public class ChefManager {
	public static final String TAG = "michelin.chefmanager";
	// chefs
	public static int ChefNone = 0;
	public static int ChefYiJie = 1;
	public static int ChefLeTu = 2;
	public static int ChefJinYou = 3;
	public static int ChefSky = 4;
	public static int ChefZZF = 5;
	public static int ChefMiFei = 6;
	public static int ChefUC = 7;
	public static int ChefNumber = 8;

	// single instance
	private static ChefManager instance = null;

	// members
	private ArrayList<Chef> chefs = new ArrayList<Chef>();
	private int currentChefIndex = 0;
	private Michelin.ExitCallback mExitCallback;
	private int mExitNumber;
	private boolean mExitFlag;
	private Michelin.ExitCallback mCustomerExitCallback;
	private final String classes[] = { // 把类名写在下在面
	"com.candy.michelin.ChefMiFei", // 米飞
			"com.candy.michelin.ChefUC", // UC
			"com.candy.michelin.ChefYiJie", // 易接
			"com.candy.michelin.ChefLeTu", // 乐途
			"com.candy.michelin.ChefJinYou", // 近游
			"com.candy.michelin.ChefSky", // 斯凯
	};
	private Application mApplication;
	private Activity mActivity;

	public static ChefManager getInstance() {
		if (instance == null) {
			instance = new ChefManager();
		}
		return instance;
	}

	private ChefManager() {
		Chef chef = null;

		for (int i = 0; i < classes.length; i++) {
			try {
				Class<?> cls = Class.forName(classes[i]);
				chef = (Chef) cls.newInstance();
				if (chef.isAvailable()) {
					this.chefs.add(chef);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		this.mExitCallback = new Michelin.ExitCallback() {
			@Override
			public void onConfirmExit() {
				this.getOne();
			}

			@Override
			public void onCancelExit() {
				// 有一个不cancel就取消退出
				ChefManager.this.mExitFlag = false;

				this.getOne();
			}

			private void getOne() {
				ChefManager.this.mExitNumber++;

				if (ChefManager.this.mExitNumber == ChefManager.this.chefs
						.size()) {
					if (ChefManager.this.mExitFlag) {
						ChefManager.this.mCustomerExitCallback.onConfirmExit();
					} else {
						ChefManager.this.mCustomerExitCallback.onCancelExit();
					}
				}
			}
		};
	}

	private void resetCurrentChefIndex() {
		this.currentChefIndex = 0;
		Log.v(TAG, "current: " + this.currentChefIndex);
	}

	private Chef getCurrentChef() {
		if (this.chefs.size() == 0) {
			return null;
		} else {
			resetCurrentChefIndex();

			return this.chefs.get(this.currentChefIndex);
		}
	}

	public int getCurrentChefId() {
		if (this.chefs.size() == 0) {
			return ChefNone;
		} else {
			resetCurrentChefIndex();

			return this.chefs.get(this.currentChefIndex).getId();
		}
	}

	public void onCreateApplication(Application application) {
		for (Chef chef : this.chefs) {
			chef.onCreateApplication(application);
		}
		mApplication = application;
	}

	public void attachBaseContext(Context base) {
		for (Chef chef : this.chefs) {
			chef.attachBaseContext(base);
		}
	}

	public void onCreateActivity(Activity activity) {
		mActivity = activity;

		GameAgent.getInstance().setDebugMode(false); // 设置输出运行时日志
		GameAgent.getInstance().init(activity);

		for (Chef chef : this.chefs) {
			chef.onCreateActivity(activity);
		}
	}

	public void onDestroy(Activity activity) {
		for (Chef chef : this.chefs) {
			chef.onDestroy(activity);
		}
	}

	public void onResume(Activity activity) {
		GameAgent.getInstance().onResume(activity);

		for (Chef chef : this.chefs) {
			chef.onResume(activity);
		}
	}

	public void onPause(Activity activity) {
		GameAgent.getInstance().onPause(activity);

		for (Chef chef : this.chefs) {
			chef.onPause(activity);
		}
	}

	public void order(final int index, final Michelin.Callback callback) {
		final Chef chef = this.getCurrentChef();
		final int chef_id = chef == null ? 0 : chef.getId();

		// 添加一个计费请求
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", chef_id + "");
		map.put("index", index + "");
		GameAgent.getInstance().onEvent(mActivity, "PayStart", map);

		if (chef == null) {
			GameAgent.getInstance().onEvent(mActivity, "PaySuccess", map);
			callback.serve(0, "没有计费");
		} else {
			chef.order(index, new Callback() {
				@Override
				public void serve(int result, String message) {
					// 如果计费成功，添加一个计费成功
					if (result == Michelin.ResultSuccess) {
						GameAgent.getInstance().onEvent(mActivity,
								"PaySuccess", map);
					} else {
						GameAgent.getInstance().onEvent(mActivity, "PayFail",
								map);
					}

					Log.d(TAG, result + ", " + message);
					callback.serve(result, message);
				}
			});
		}
	}

	public void requestExit(Activity activity,
			final Michelin.ExitCallback callback) {
		this.mCustomerExitCallback = callback;
		this.mExitNumber = 0;
		this.mExitFlag = true;
		for (Chef chef : this.chefs) {
			chef.requestExit(activity, this.mExitCallback);
		}
	}

	public String getUserId() {
		Chef chef = this.getCurrentChef();
		if (chef == null) {
			return "";
		} else {
			return chef.getUserId();
		}
	}

	private int getChefIndex(int chef_id) {
		int index = -1;
		for (Chef chef : this.chefs) {
			index++;
			if (chef.getId() == chef_id) {
				break;
			}
		}
		return index;
	}
}
