package com.toretate.denentokei;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
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
	private static @NonNull Intent buildAlarmIntent( @NonNull final Context context, final int appWidgetId, @NonNull final Class<? extends AppWidgetProvider> targetClass )
	{
		final Intent intent = new Intent(context, targetClass );
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
	public static void setAlarm( @NonNull final Context context, @NonNull final int[] appWidgetIds, final @NonNull Class<? extends AppWidgetProvider> widgetClass )
	{
		for( int appWidgetId : appWidgetIds ) setAlarm( context, appWidgetId, widgetClass );
	}
	
	/**
	 * 定期アラームを設置します
	 * @param context		コンテキスト
	 * @param appWidgetId	定期アラームを設定するウィジェットID
	 */
	public static void setAlarm( @NonNull final Context context, final int appWidgetId, final @NonNull Class<? extends AppWidgetProvider> widgetClass )
	{
		if( appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) return;
		
		final AlarmManager mng = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );
		final Intent alarmIntent = buildAlarmIntent( context, appWidgetId, widgetClass );
		final PendingIntent operation = PendingIntent.getBroadcast(context, appWidgetId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT );
		mng.setRepeating( AlarmManager.RTC, System.currentTimeMillis(), s_interval, operation);
	}
	
	/**
	 * 定期アラームを削除します
	 * @param context		コンテキスト
	 * @param appWidgetId	定期アラームを削除するウィジェットID
	 */
	public static void deleteAlarm( @NonNull final Context context, final int appWidgetId, final @NonNull Class<? extends AppWidgetProvider> widgetClass )
	{
		if( appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) return;
		
		final AlarmManager mng = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );
		if( appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID ) {
			final Intent alarmIntent = buildAlarmIntent( context, appWidgetId, widgetClass );
			mng.cancel( PendingIntent.getBroadcast(context, appWidgetId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT ) );
		}
	}
	
	/**
	 * インテントにアラームが設置済みかどうかを取得します
	 * @param intent インテント
	 * @return true:アラーム設置済み
	 */
	public static boolean isAlarmSet( @NonNull final Intent intent )
	{
		return URI_SCHEME.equals( intent.getScheme() );
	}
	
	/**
	 * 通知機能
	 */
	/*
	static void setAlarm( Context context )
	{
		Calendar triggerTime = Calendar.getInstance();
		triggerTime.add( Calendar.SECOND, 5 );	// 今から5秒後
		
		Intent intent = new Intent( context, Notifier.class );
		PendingIntent sender = PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
		
		AlarmManager mng = (AlarmManager)context.getSystemService( Activity.ALARM_SERVICE );
		mng.set( AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), sender );
	}
	*/

}
