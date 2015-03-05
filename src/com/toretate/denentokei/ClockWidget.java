package com.toretate.denentokei;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

public class ClockWidget extends AppWidgetProvider
{
	
	@Override
	public void onEnabled( @Nullable final Context context )
	{
		super.onEnabled(context);
	}
	
	@Override
	public void onUpdate( @Nullable final Context context, @Nullable final AppWidgetManager appWidgetManager, @Nullable final int[] appWidgetIds )
	{
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onDeleted( @Nullable final Context context, @Nullable final int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}
	
	@Override
	public void onDisabled( @Nullable final Context context ) {
		super.onDisabled(context);
	}
	
	@Override
	public void onReceive( @Nullable final Context context, @Nullable final Intent intent) {
		if( intent == null || context == null ) {
			super.onReceive(context, intent);
			return;
		}
		
		final int appWidgetId = intent.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID );
		if( AppWidgetManager.ACTION_APPWIDGET_DELETED.equals( intent.getAction()) ) {
			WidgetAlarmUtils.deleteAlarm(context, appWidgetId);
		} else if( AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals( intent.getAction() ) ) {
			
			if( appWidgetId == -1 || appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) {
				// エラー。何もしない
			} else {
				if( WidgetAlarmUtils.isAlarmSet( intent ) == false ) {
					WidgetAlarmUtils.setAlarm( context, appWidgetId );
					updateWidget( context, appWidgetId );
				} else {
					updateWidget( context, appWidgetId );
				}
			}
		}
		super.onReceive(context, intent);
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
		final RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.widget_2x1 );
		
		// 文字列の更新
		final ClockInfo info;
		{
			info = ClockInfo.loadValues( context, appWidgetId );
			final long now = System.currentTimeMillis();
			views.setTextViewText( R.id.staminaClock, info.getStaminaString( now ) );
			views.setTextViewText( R.id.staminaClockSub, info.getStaminaSubString( now ) );
			views.setTextViewText( R.id.charismaClock, info.getCharismaString( now ) );
		}

		// ボタンハンドラの設定
		{
			final Intent intent = new Intent( context, ClockWidgetSettingsActivity.class );
			intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId );
			final PendingIntent pendingIntent = PendingIntent.getActivity( context, appWidgetId, intent, 0 );
			views.setOnClickPendingIntent( R.id.setting_button, pendingIntent );
		}
		
		mng.updateAppWidget( appWidgetId, views );
	}
	
}
