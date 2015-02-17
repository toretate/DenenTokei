package com.toretate.denentokei;

import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;

public class ClockWidget extends AppWidgetProvider
{
	private static SparseArray<ClockInfo> s_widgetIdToInstance = new SparseArray<ClockInfo>();
	
	@Override
	public void onEnabled( Context context )
	{
		Log.i( "DenenTokei", "onEnabled" );
		super.onEnabled(context);
	}
	
	@Override
	public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )
	{
		Log.i( "DenenTokei", "onUpdate" );
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.i( "DenenTokei", "onDeleted" );
		super.onDeleted(context, appWidgetIds);
	}
	
	@Override
	public void onDisabled(Context context) {
		Log.i( "DenenTokei", "onDisabled" );
		super.onDisabled(context);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i( "DenenTokei", "onRecieve" );
		
		if( AppWidgetManager.ACTION_APPWIDGET_DELETED.equals( intent.getAction()) ) {
			deleteAlarm(context, intent);
		} else if( AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals( intent.getAction() ) ) {
			if( URI_SCHEME.equals( intent.getScheme() ) == false ) {
				setAlarm(context, intent);
			} else {
				int appWidgetId = intent.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, -1 );
				if( appWidgetId != -1 ) {
					// 更新処理
					long now = System.currentTimeMillis();
					
					ClockInfo info = s_widgetIdToInstance.get( appWidgetId );
					if( info == null ) {
						info = new ClockInfo();
						s_widgetIdToInstance.append(appWidgetId, info);
					}
					
					long result = now - info.getSaveTime();
//					result /= 1000 * 60;	// ms->分
					
					RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.activity_clock );
					views.setTextViewText( R.id.charismaClock, info.getCharismaString() + "+" + result );
//					views.setTextViewText( R.id.charismaClock, "hoge" );
					
					ComponentName widget = new ComponentName( context, ClockWidget.class );
					AppWidgetManager mng = AppWidgetManager.getInstance( context );
					mng.updateAppWidget( widget, views );
//					
//					// 時計設定
//					setAlarm( context, intent );
				}
			}
		}
		super.onReceive(context, intent);
	}
	
	private static final String URI_SCHEME = "com.toretate.denentokei";
	private Intent buildAlarmIntent( Context context, int appWidgetId )
	{
		Intent intent = new Intent(context, ClockWidget.class );
		intent.setAction( AppWidgetManager.ACTION_APPWIDGET_UPDATE );
		intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId );
		intent.setData( Uri.parse( URI_SCHEME + "://update/" + appWidgetId) );
		return intent;
	}
	
	private void setAlarm( final Context context, Intent intent )
	{
		for( int appWidgetId : intent.getExtras().getIntArray( AppWidgetManager.EXTRA_APPWIDGET_IDS ) ) {
			
			final long interval = 1000 * 60;	// 1m
			
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, buildAlarmIntent(context, appWidgetId), PendingIntent.FLAG_UPDATE_CURRENT );
			AlarmManager mng = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );
			mng.setRepeating( AlarmManager.RTC, System.currentTimeMillis(), interval, operation);
		}
	}
	
	private void deleteAlarm( Context context, Intent intent )
	{
		int appWidgetId = intent.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID );
		
		if( appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID ) {
			AlarmManager mng = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );
			mng.cancel( PendingIntent.getBroadcast(context, 0, buildAlarmIntent(context, appWidgetId), PendingIntent.FLAG_UPDATE_CURRENT ) );
		}
	}
	
	
	static void updateWidget( Context context, AppWidgetManager mng, int appWidgetId )
	{
		ClockInfo info = ClockInfo.loadValues( context, appWidgetId );
		
		RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.activity_clock );
		views.setTextViewText( R.id.charismaClock, info.getCharismaString() );
		views.setTextViewText( R.id.staminaClock, info.getStaminaString() );

		ClockInfo old = s_widgetIdToInstance.get( appWidgetId );
		if( old == null ) {
			s_widgetIdToInstance.append( appWidgetId, info );
		} else {
			old.copyFrom( info );
		}
		
		ComponentName name = new ComponentName( context, ClockWidget.class );
		mng.updateAppWidget( name, views );
	}
	
}
