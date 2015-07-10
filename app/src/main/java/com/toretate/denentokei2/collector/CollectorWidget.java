package com.toretate.denentokei2.collector;

import java.util.Calendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.toretate.denentokei.R;
import com.toretate.denentokei2.WidgetAlarmUtils;

public class CollectorWidget extends AppWidgetProvider {
	public static String s_fromWidgetSettingButton 	= "fromWidgetSettingButton";
	
	private int m_goal = 1600;		//!< 目標個数
	private int m_current = 100;	//!< 現在値
	private long m_goal_time;		//!< 目標時間(ms)
	
	@Override
	public void onEnabled( @Nullable final Context context )
	{
		super.onEnabled(context);
	}
	
	@Override
	public void onUpdate( @Nullable final Context context, @Nullable final AppWidgetManager appWidgetManager, @Nullable final int[] appWidgetIds )
	{
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		if( appWidgetIds != null && context != null && appWidgetManager != null ) {
			for( int appWidgetId : appWidgetIds ) {
				updateWidget(context, appWidgetId, appWidgetManager);
			}
		}
	}	
	
	@Override
	public void onDeleted( @Nullable final Context context, @Nullable final int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}
	
	@Override
	public void onDisabled( @Nullable final Context context ) {
		Log.i("Widget", "onDisabled");
		super.onDisabled(context);
	}
	
	@Override
	public void onReceive( @Nullable final Context context, @Nullable final Intent intent) {
		if( intent == null || context == null ) {
			super.onReceive(context, intent);
			return;
		}
		final String action = intent.getAction();
		
		// 更新チェック
		if( Intent.ACTION_MY_PACKAGE_REPLACED.equals( action ) 
				|| Intent.ACTION_BOOT_COMPLETED.equals( action ) 
				|| Intent.ACTION_TIME_CHANGED.equals( action )
				|| Intent.ACTION_DATE_CHANGED.equals( action )
				|| Intent.ACTION_TIMEZONE_CHANGED.equals( action )
				) {
			updateAllWidget( context );
			super.onReceive( context, intent );
			return;
		}
		
		final int appWidgetId = intent.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID );
		
		Log.i("Widget", "action:" +intent.getAction() + " ID:" +appWidgetId);
		if( appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) return;
		
		if( AppWidgetManager.ACTION_APPWIDGET_DELETED.equals( action ) ) {
			WidgetAlarmUtils.deleteAlarm(context, appWidgetId, CollectorWidget.class );
		} else if(
				AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals( action )
				|| action.equals("android.appwidget.action.APPWIDGET_UPDATE_OPTIONS")
				|| Intent.ACTION_MY_PACKAGE_REPLACED.equals( action )
				) {
			
			if( appWidgetId == -1 || appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) {
				// エラー。何もしない
			} else {
				if( WidgetAlarmUtils.isAlarmSet( intent ) == false ) {
					Log.i("Widget", "　alarm set");
					WidgetAlarmUtils.setAlarm( context, appWidgetId, CollectorWidget.class );
					updateWidget( context, appWidgetId );
				} else {
					updateWidget( context, appWidgetId );
				}
			}
		}
		super.onReceive(context, intent);
	}
	
	static void updateAllWidget( @NonNull final Context context )
	{
		final AppWidgetManager mng = AppWidgetManager.getInstance( context );
		if( mng == null ) return;
		
		int[] appWidgetIds = mng.getAppWidgetIds( new ComponentName( context, CollectorWidget.class ) );
		if( appWidgetIds == null ) return;
		for( int appWidgetId : appWidgetIds ) {
			WidgetAlarmUtils.deleteAlarm(context, appWidgetId, CollectorWidget.class );
			WidgetAlarmUtils.setAlarm( context, appWidgetId, CollectorWidget.class );
			updateWidget( context, appWidgetId, mng );
		}
	}
	
	/** ウィジェットの更新を行います */
	static void updateWidget( @NonNull final Context context, final int appWidgetId )
	{
		final AppWidgetManager mng = AppWidgetManager.getInstance( context );
		if( mng != null ) updateWidget( context, appWidgetId, mng );
	}
	
	/** ウィジェットの更新を行います */
	static void updateWidget( @NonNull final Context context, final int appWidgetId, @NonNull final AppWidgetManager mng )
	{
		final RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.widget_collector );
		
		int goal = 1600;
		int current = 524;
		int remain = goal - current;
		final Calendar goalTimeCalendar = Calendar.getInstance();
		goalTimeCalendar.set( 2015, 6, 25, 9, 0);					// 終了日時：(仮に2015年6月25日(木) 9時)
		final long goalTime = goalTimeCalendar.getTimeInMillis();
		
		// 現在時刻
		final long currentTime = System.currentTimeMillis();
		
		final long delta_ms = goalTime - currentTime;
		if( delta_ms < 0 ) {
			views.setTextViewText( R.id.collector_percent, "率:----個/h");
		} else {
			final double delta_h = delta_ms / ( 1000.0f * 60 * 60 );
			final double percent = remain / delta_h;
			String format;
			if( 1000 < percent ) {
				format = "率:%4f個/h";
			} else if( 100 < percent ) {
				format = "率:%3.1f個/h";
			} else {
				format = "率:%02.2f個/h";
			}
			views.setTextViewText( R.id.collector_percent, String.format(format, percent) );
		}
		
		views.setTextViewText( R.id.collector_goal, String.format("目標:%4d個", goal));
		views.setTextViewText( R.id.collector_current, String.format("現在:%4d個", current) );
		views.setTextViewText( R.id.collector_remain, String.format("残:%4d個", remain) );
		
		views.setInt( R.id.collector_progress, "setMax", goal );
		views.setInt( R.id.collector_progress, "setProgress", current );
		
		// ボタンハンドラの設定
		{
			final Intent intent = new Intent( context, CollectorWidgetSettingsActivity.class );
			intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId );
			intent.putExtra( s_fromWidgetSettingButton, Boolean.TRUE );
			final PendingIntent pendingIntent = PendingIntent.getActivity( context, appWidgetId, intent, 0 );
			views.setOnClickPendingIntent( R.id.collector_setting_button, pendingIntent );
		}
		
		mng.updateAppWidget( appWidgetId, views );
	}
	
	

}
