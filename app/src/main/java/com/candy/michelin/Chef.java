package com.candy.michelin;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public abstract class Chef {
	protected Activity mActivity;
	
	abstract public int getId();

	abstract public boolean isAvailable();

	abstract public void order(int index, Michelin.Callback callback);

	abstract protected void serve(int result, String message);

	public void onCreateApplication(Application application) {
	}

	public void attachBaseContext(Context base) {
	}

	public void onCreateActivity(Activity activity) {
		this.mActivity = activity;
	}

	public void onDestroy(Activity activity) {
	}

	public void onResume(Activity activity) {
	}

	public void onPause(Activity activity) {
	}

	public void requestExit(Activity activity, final Michelin.ExitCallback callback) {
		callback.onConfirmExit();
	}
	
	public String getUserId() {
		return null;
	}
}
