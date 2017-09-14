package com.candy.sugar.analytics.game;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class GameAgent implements GameAgentInterface {
	protected enum GameAgentType {
		None, // dummy interface
		UMeng, // umeng
		UMengOld // old umeng
	};

	protected static final GameAgentType agentType = GameAgentType.UMengOld;
	private static GameAgentInterface mInstance = null;

	public static GameAgentInterface getInstance() {
		if (mInstance == null) {
			if (agentType == GameAgentType.None) {
				mInstance = new GameAgent();
			} else if (agentType == GameAgentType.UMeng) {
				mInstance = new GameAgent();
			} else if (agentType == GameAgentType.UMengOld) {
				mInstance = new GameAgent();
			}
		}
		return mInstance;
	}

	@Override
	public void init(Context ctx) {
	}

	@Override
	public void onResume(Context ctx) {
	}

	@Override
	public void onPause(Context ctx) {
	}

	@Override
	public void pay(double money, double coin, int source) {
	}

	@Override
	public void pay(double money, String item, int number, double price,
			int source) {
	}

	@Override
	public void setDebugMode(boolean debug) {
	}

	@Override
	public void startLevel(String level) {
	}

	@Override
	public void failLevel(String level) {
	}

	@Override
	public void finishLevel(String level) {
	}

	@Override
	public void onEvent(Context context, String eventId) {
	}

	@Override
	public void onEvent(Context context, String eventId, String param) {
	}

	@Override
	public void onEvent(Context context, String eventId,
			HashMap<String, String> map) {
	}
}
