package com.candy.sugar3;

import com.candy.michelin.Michelin;

import android.app.Application;
import android.content.Context;

public class application extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Michelin.onCreateApplication(this);



	}

	@Override
	protected void attachBaseContext(Context base) {
		Michelin.attachBaseContext(base);

		super.attachBaseContext(base);
	}
}
