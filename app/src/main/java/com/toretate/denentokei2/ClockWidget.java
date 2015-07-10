package com.toretate.denentokei2;

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

public class ClockWidget extends AppWidgetProvider
{
	public static String s_fromWidgetSettingButton 	= "fromWidgetSettingButton";
	
	@Override
	public void onEnabled( @Nullable final Context context )
	{
		Log.i("Widget", "onEnabled");
			
		super.onEnabled(context);
	}
	
	@Override
	public void onUpdate( @Nullable final Context context, @Nullable final AppWidgetManager appWidgetManager, @Nullable final int[] appWidgetIds )
	{
		if( appWidgetIds != null ) {
			for( int id : appWidgetIds ) {
				Log.i("Widget", "onUpdate ID:" + id);
			}
		}
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		if( appWidgetIds != null && context != null && appWidgetManager != null ) {
			for( int appWidgetId : appWidgetIds ) {
				updateWidget(context, appWidgetId, appWidgetManager);
			}
		}
	}	
	
	@Override
	public void onDeleted( @Nullable final Context context, @Nullable final int[] appWidgetIds) {
		if( appWidgetIds != null ) {
			for( int id : appWidgetIds ) {
				Log.i("Widget", "onDeleted ID:" + id);
			}
		}
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
			WidgetAlarmUtils.deleteAlarm(context, appWidgetId, ClockWidget.class);
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
					WidgetAlarmUtils.setAlarm( context, appWidgetId, ClockWidget.class );
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
		
		int[] appWidgetIds = mng.getAppWidgetIds( new ComponentName( context, ClockWidget.class ) );
		if( appWidgetIds == null ) return;
		for( int appWidgetId : appWidgetIds ) {
			WidgetAlarmUtils.deleteAlarm(context, appWidgetId, ClockWidget.class );
			WidgetAlarmUtils.setAlarm( context, appWidgetId, ClockWidget.class );
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
		final String pkgName = context.getPackageName();
		final RemoteViews views = new RemoteViews( pkgName, R.layout.widget_2x1 );
		
		// 文字列の更新
		final ClockInfo info = ClockInfo.loadValues( context, appWidgetId );
		Log.i("Widget", "　　updateWidget:" + " ID:" +appWidgetId);
		{
			final long now = System.currentTimeMillis();
			views.setTextViewText( R.id.staminaClock, info.getStaminaString( now ) );
			views.setTextViewText( R.id.staminaClockSub, info.getStaminaSubString( now ) );
			views.setTextViewText( R.id.charismaClock, info.getCharismaString( now ) );
		}
		
//		if( BuildConfig.DEBUG ) {
//			String msg = "sta:" +info.getStamina() + ", " +info.getStaminaString(System.currentTimeMillis());
//			
//			final Intent intent = new Intent( context, DebugToastService.class );
//			intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId );
//			intent.putExtra( DebugToastService.s_toastMsg, msg );
//			
//			final PendingIntent pendingIntent = PendingIntent.getService( context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT );
//			views.setOnClickPendingIntent( R.id.widget_surface, pendingIntent);
//		}
		
		// ボタンハンドラの設定
		{
			final Intent intent = new Intent( context, ClockWidgetSettingsActivity.class );
			intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId );
			intent.putExtra( s_fromWidgetSettingButton, Boolean.TRUE );
			final PendingIntent pendingIntent = PendingIntent.getActivity( context, appWidgetId, intent, 0 );
			views.setOnClickPendingIntent( R.id.setting_button, pendingIntent );
		}
		
		mng.updateAppWidget( appWidgetId, views );
	}
	
	
}
