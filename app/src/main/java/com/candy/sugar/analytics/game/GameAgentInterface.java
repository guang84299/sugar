package com.candy.sugar.analytics.game;

import java.util.HashMap;

import android.content.Context;

public interface GameAgentInterface {
	// life cycle
	public void init(Context context);

	public void onResume(Context context);

	public void onPause(Context context);

	// pay
	public void pay(double money, double coin, int source);

	public void pay(double money, String item, int number, double price,
			int source);

	// debug
	public void setDebugMode(boolean debug);

	// level
	public void startLevel(String level);

	public void failLevel(String level);

	public void finishLevel(String level);

	// event
	public void onEvent(Context context, String eventId);
	
	public void onEvent(Context context, String eventId, String param);

	public void onEvent(Context context, String eventId,
			HashMap<String, String> map);
}
