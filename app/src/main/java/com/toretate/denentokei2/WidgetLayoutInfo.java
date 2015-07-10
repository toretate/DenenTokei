package com.toretate.denentokei2;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toretate.denentokei2.FontSetting.FontSettingType;

public class WidgetLayoutInfo {
    private static final String PREFS_NAME = "WidgetLayoutInfo";
    public static final @NonNull String DEF_FONT_FAMILY = "monospace";
    public static final int DEF_FONT_SIZE = 24;
	
	public class WidgetTextLayoutInfo {
		public @NonNull String fontName;
		public int fontSize;
		public int textViewMarginL;
		public int textViewMarginT;
		
		private FontSettingType m_type;
		public WidgetTextLayoutInfo( final FontSettingType type ) {
			m_type = type;
			fontName = DEF_FONT_FAMILY;
			load();
		}
		
		private void reset() {
			fontName = DEF_FONT_FAMILY;
			fontSize = DEF_FONT_SIZE;
			textViewMarginL = 0;
			textViewMarginT = 0;
		}
		
		public void save() {
			final Activity parent = m_parent.get();
			if( parent != null ) save( parent );
		}
		
		public void save( @NonNull final Context context ) {
			if( m_appWidgetId == -1 || m_appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) return;
			
			final SharedPreferences.Editor editor = context.getSharedPreferences( PREFS_NAME + m_appWidgetId, 0 ).edit();
			editor.putString(	m_type.typeString + "FontFamily",	this.fontName );
			editor.putInt(		m_type.typeString + "FontSize",		this.fontSize );
			editor.putInt(		m_type.typeString + "TextMarginL",	this.textViewMarginL );
			editor.putInt(		m_type.typeString + "TextMarginT",	this.textViewMarginT );
			editor.commit();
		}
		
		public void load() {
			final Activity parent = m_parent.get();
			if( parent != null ) load( parent );
		}
		
		/**
		 * ウィジェットIDから設定値をロードします
		 * @param context コンテキスト
		 * @param appWidgetId ウィジェットID
		 * @return 設定値
		 */
		public void load( @NonNull final Activity activity )
		{
			if( m_appWidgetId == -1 || m_appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) {
				reset();
			} else {
				final SharedPreferences prefs = activity.getSharedPreferences( PREFS_NAME + m_appWidgetId, 0 );
				if( prefs.contains( m_type.typeString + "FontFamily" ) ) {
					
					String fontName = prefs.getString( m_type.typeString + "FontFamily", DEF_FONT_FAMILY );
					if( fontName == null ) fontName = DEF_FONT_FAMILY;
					this.fontName	= fontName;
					
					fontSize		= prefs.getInt( m_type.typeString + "FontSize", DEF_FONT_SIZE );
					textViewMarginL	= prefs.getInt( m_type.typeString + "TextMarginL", 0 );
					textViewMarginT	= prefs.getInt( m_type.typeString + "TextMarginT", 0 );
				} else {
					reset();
				}
			}
		}
		
		public void resetView( final @NonNull TextView target ) {
			final Activity activity = m_parent.get();
			if( activity == null ) return;
			
			activity.runOnUiThread( new Runnable() {
				@Override
				public void run() {
					final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)target.getLayoutParams();
					params.setMargins( textViewMarginL, textViewMarginT, params.rightMargin, params.bottomMargin );
					target.setLayoutParams( params );
					
					target.setTypeface( Typeface.create( fontName, Typeface.NORMAL ) );
					target.setTextSize( fontSize );
				}
			} );
		}
	}
	
	private WidgetTextLayoutInfo m_staInfo;
	private WidgetTextLayoutInfo m_chaInfo;
	private WidgetTextLayoutInfo m_stasubInfo;
	public @Nullable WidgetTextLayoutInfo getTextLayoutInfo( final @NonNull FontSetting.FontSettingType type ) {
		switch( type ) {
		case Sta:
			return m_staInfo;
		case Cha:
			return m_chaInfo;
		case StaSub:
			return m_stasubInfo;
		default:
			return null;
		}
	}
	
	private WeakReference<Activity> m_parent;
	private int m_appWidgetId;
	
	public WidgetLayoutInfo( final @NonNull Activity parent, final int appWidgetId ) {
		m_parent = new WeakReference<Activity>( parent );
		m_appWidgetId = appWidgetId;
		
		m_staInfo = new WidgetTextLayoutInfo( FontSettingType.Sta );
		m_chaInfo = new WidgetTextLayoutInfo( FontSettingType.Cha );
		m_stasubInfo = new WidgetTextLayoutInfo( FontSettingType.StaSub );
	}
	
}

