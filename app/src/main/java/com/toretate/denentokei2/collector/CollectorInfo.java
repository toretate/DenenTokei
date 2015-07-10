package com.toretate.denentokei2.collector;

import java.util.Calendar;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class CollectorInfo {
    private static final String PREFS_NAME = "CollectorInfo";
    private static final String PREF_PREFIX_KEY = "prefix_";

	public int current;				//!< 現在個数
	public int goal;				//!< 目標個数
	public long terminationTime;	//!< イベント終了期限
	
	private CollectorInfo() {
		this.current = 0;
		this.goal = 1500;

		// 2015年6月25日9時59分をゴール時間とする
		Calendar cal = Calendar.getInstance();
		cal.set( 2015, 6, 25, 9, 59);
		this.terminationTime = cal.getTimeInMillis();
	}
	
	public static CollectorInfo load( @NonNull final Context context, final int appWidgetId ) {
		CollectorInfo info = new CollectorInfo();
		if( appWidgetId == -1 || appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) {
			return info;
		}
		
		final SharedPreferences prefs = context.getSharedPreferences( PREFS_NAME + appWidgetId, 0 );
		if( prefs.contains( PREF_PREFIX_KEY + "PrinceLv" ) ) {
			info.current		= prefs.getInt( "Current", 0 );
			info.goal			= prefs.getInt( "Goal", 1500 );
		}
		
		return info;
	}
}
