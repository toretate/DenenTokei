package com.toretate.denentokei;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

/** ウィジェットを再更新するためのユーティリティクラス */
public class WidgetAlarmUtils {
	
//	private static long s_interval = 1000;								//!< 1秒毎
	private static long s_interval = 1000 * 60;							//!< 1分毎	
	private static final String URI_SCHEME = "com.toretate.denentokei";
	
	/**
	 * アラーム用のIntentを作成します
	 * @param context		コンテキスト
	 * @param appWidgetId	アラームの対象となるウィジェットID
	 * @return				アラーム用のIntent
	 */
	private static @NonNull Intent buildAlarmIntent( @NonNull final Context context, final int appWidgetId )
	{
		final Intent intent = new Intent(context, ClockWidget.class );
		intent.setAction( AppWidgetManager.ACTION_APPWIDGET_UPDATE );
		intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId );
		intent.setData( Uri.parse( URI_SCHEME + "://update/" + appWidgetId) );
		return intent;
	}
	
	/**
	 * 定期アラームを設置します
	 * @param context		コンテキスト
	 * @param appWidgetIds	定期アラームを設定するウィジェットID群
	 */
	static void setAlarm( @NonNull final Context context, @NonNull final int[] appWidgetIds )
	{
		for( int appWidgetId : appWidgetIds ) setAlarm( context, appWidgetId );
	}
	
	/**
	 * 定期アラームを設置します
	 * @param context		コンテキスト
	 * @param appWidgetId	定期アラームを設定するウィジェットID
	 */
	static void setAlarm( @NonNull final Context context, final int appWidgetId )
	{
		if( appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) return;
		
		final AlarmManager mng = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );
		final Intent alarmIntent = buildAlarmIntent( context, appWidgetId );
		final PendingIntent operation = PendingIntent.getBroadcast(context, appWidgetId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT );
		mng.setRepeating( AlarmManager.RTC, System.currentTimeMillis(), s_interval, operation);
	}
	
	/**
	 * 定期アラームを削除します
	 * @param context		コンテキスト
	 * @param appWidgetId	定期アラームを削除するウィジェットID
	 */
	static void deleteAlarm( @NonNull final Context context, final int appWidgetId )
	{
		if( appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) return;
		
		final AlarmManager mng = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );
		if( appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID ) {
			final Intent alarmIntent = buildAlarmIntent( context, appWidgetId );
			mng.cancel( PendingIntent.getBroadcast(context, appWidgetId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT ) );
		}
	}
	
	/**
	 * インテントにアラームが設置済みかどうかを取得します
	 * @param intent インテント
	 * @return true:アラーム設置済み
	 */
	static boolean isAlarmSet( @NonNull final Intent intent )
	{
		return URI_SCHEME.equals( intent.getScheme() );
	}

}
