package com.candy.michelin;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class Michelin {
	public static final String version = "0.0.1";

	public static final int ResultSuccess = 0;
	public static final int ResultFail = 1;
	public static final int ResultCancel = 2;
	public static final int ResultOvertime = 3;

	public static int getCurrentChef() {
		return ChefManager.getInstance().getCurrentChefId();
	}

	/**
	 * 请求计费
	 * 
	 * @param index
	 *            计费点的编号，从1开始
	 * @param callback
	 *            回调函数
	 */
	public static void order(int index, final Callback callback) {
		ChefManager.getInstance().order(index, callback);
	}

	public static void onCreateApplication(Application application) {
		ChefManager.getInstance().onCreateApplication(application);
	}

	public static void attachBaseContext(Context base) {
		ChefManager.getInstance().attachBaseContext(base);
	}

	public static void onCreateActivity(Activity activity) {
		ChefManager.getInstance().onCreateActivity(activity);
	}

	public static void onStart(Activity activity) {
	}

	public static void onResume(Activity activity) {
		ChefManager.getInstance().onResume(activity);
	}

	public static void onPause(Activity activity) {
		ChefManager.getInstance().onPause(activity);
	}

	public static void onDestroy(Activity activity) {
		ChefManager.getInstance().onDestroy(activity);
	}

	public static void requestExit(Activity activity,
			final ExitCallback callback) {
		ChefManager.getInstance().requestExit(activity, callback);
	}

	public static String getUserId() {
		return ChefManager.getInstance().getUserId();
	}

	public interface Callback {
		void serve(int result, String message);
	}

	public interface ExitCallback {
		void onConfirmExit();

		void onCancelExit();
	}
}
