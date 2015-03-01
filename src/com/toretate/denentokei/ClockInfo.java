package com.toretate.denentokei;

import java.util.Locale;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.SparseArray;
 
public class ClockInfo
{
	private static SparseArray<ClockInfo> s_widgetIdToInstance = new SparseArray<ClockInfo>();
	
    private static final String PREFS_NAME = "com.toretate.denentokei.ClockInfo";
    private static final String PREF_PREFIX_KEY = "prefix_";

    private static final long s_interval_charisma = 1000 * 60 * 3;	// 3分毎に更新
    private static final long s_interval_stamina = 1000 * 60 * 60;	// 1時間毎に更新
    private static final long s_interval_stamina_sub = 1000 * 60;	// 1分毎に更新
//    private static final long s_interval_charisma = 1000 * 3;	// 3秒毎に更新
//    private static final long s_interval_stamina = 1000 * 60;	// 1分毎に更新
    
    private int m_princeLv;
    
	private int m_charisma;		//!< 現在のカリスマ
	private int m_charismaMax;		//!< 現レベルでの最大カリスマ
	private int m_charismaSub;		//!< カリスマが１回復するまでの分数(まぁ使わないでしょ…）
	
	private int m_stamina;			//!< 現在のスタミナ
	private int m_staminaMax;		//!< 現レベルでの最大スタミナ
	private int m_staminaSub;		//!< スタミナが１回復するまでの分数
	
	private long m_saveTime;		//!< データを保存した時刻

	// getter/setter
	public int getPrinceLv() { return m_princeLv; }
	public void setPrinceLv( final int lv )
	{ 
		m_princeLv = lv;
		resetCharisma();
		resetStamina();
	}
	
	public int getCharisma() { return m_charisma; }
	public String getCharismaString() { return String.format( Locale.getDefault(), "%03d", m_charisma + 1 ); }
	public void setCharisma( final int charisma ) { this.m_charisma = Math.min( charisma, this.m_charismaMax +1 ); }
	
	public int getCharismaMax() { return m_charismaMax; }
	public String getCharismaMaxString() { return String.format( Locale.getDefault(), "%03d", m_charismaMax + 1 ); }
	
	public int getCharismaSub() { return m_charismaSub; }
	public String getCharismaSubString() { return String.format( Locale.getDefault(), "後%01d分", m_charismaSub); }
	public void setCharismaSub( final int charismaSub ) { this.m_charismaSub = Math.max( charismaSub, 0 ); }
	
	
	
	public int getStamina() { return m_stamina; }
	public String getStaminaString() { return String.format( Locale.getDefault(), "%02d", (m_stamina + 1) ); }
	public void setStamina( final int stamina ) { this.m_stamina = Math.min( stamina, this.m_staminaMax +1 ); }
	
	public int getStaminaMax() { return m_staminaMax; }
	public String getStaminaMaxString() { return String.format( Locale.getDefault(), "%02d", (m_staminaMax + 1) ); }

	public int getStaminaSub() { return m_staminaSub; }
	public String getCStaminaSubString() { return String.format( Locale.getDefault(), "後%02d分", m_staminaSub); }
	public void setStaminaSub( final int staminaSub ) { this.m_staminaSub = Math.max( staminaSub, 0 ); }

	public long getSaveTime() { return m_saveTime; }
	
	// 表示用getter
	
	/** 
	 * 現在時刻を指定して、現在のカリスマ値を取得します
	 * @param currentTimeMillis {@link System#currentTimeMillis()}
	 * @return 現在のカリスマ値
	 */
	public int getCharisma( final long currentTimeMillis ) {
		final long now = ( currentTimeMillis - m_saveTime ) / s_interval_charisma;
		if( now <= Integer.MAX_VALUE ) {
			return m_charisma + (int)now;
		} else {
			return 0;
		}
	}
	
	/** 
	 * 現在時刻を指定して、カリスマの表示用文字列を取得します
	 * @param currentTimeMillis {@link System#currentTimeMillis()}
	 * @return "${現在カリスマ}/${カリスマ最大値}"
	 */
	public String getCharismaString( final long currentTimeMillis )
	{
		final long now = getCharisma( currentTimeMillis );
		return String.format( Locale.getDefault(), "%03d/%s", now, getCharismaMaxString() );
	}
	
	/** 
	 * 現在時刻を指定して、現在のスタミナ値を取得します
	 * @param currentTimeMillis {@link System#currentTimeMillis()}
	 * @return 現在のスタミナ値
	 */
	public int getStamina( final long currentTimeMillis ) {
		long now = currentTimeMillis - m_saveTime; 							// 経過ミリ秒
		long sub = ( 60 - getStaminaSub( currentTimeMillis ) ) * 60 * 1000;	// ”後x分”のミリ秒
		now = ( now + sub ) / s_interval_stamina;							// ミリ秒 -> h
		
		if( now <= Integer.MAX_VALUE ) {
			return m_stamina + (int)now;
		} else {
			return 0;
		}
	}
	
	/** 
	 * 現在時刻を指定して、スタミナの表示用文字列を取得します
	 * @param currentTimeMillis {@link System#currentTimeMillis()}
	 * @return "${現在スタミナ}/${スタミナ最大値}"
	 */
	public String getStaminaString( final long currentTimeMillis ) {
		final long now = getStamina( currentTimeMillis );
		return String.format( Locale.getDefault(), "%02d/%s", now, getStaminaMaxString() );
	}
	
	/** 
	 * 現在時刻を指定して、現在のスタミナが１回復するまでの分数を取得します
	 * @param currentTimeMillis {@link System#currentTimeMillis()}
	 * @return 現在のスタミナが１回復するまでの分数
	 */
	public int getStaminaSub( final long currentTimeMillis ) {
		long now = currentTimeMillis - m_saveTime;	// 経過ミリ秒数
		now /= s_interval_stamina_sub;					// 経過分数
		now = m_staminaSub - now;
		if( now < 0 ) {
			// 時刻の繰り下がりが発生
			now = 60 - ( Math.abs( now ) % 60 );	// 60分からの経過時刻に変更
		}
		if( now <= Integer.MAX_VALUE ) {
			return (int)now;
		} else {
			return 0;
		}
	}
	
	/** 
	 * 現在時刻を指定して、スタミナが１回復するまでの表示用文字列を取得します
	 * @param currentTimeMillis {@link System#currentTimeMillis()}
	 * @return "後${スタミナが１回復するまでの分数}分"
	 */
	public String getStaminaSubString( final long currentTimeMillis ) {
		final long now = getStaminaSub( currentTimeMillis );
		return String.format( Locale.getDefault(), "後%02d分", now );
	}
	
	/** 初期化 */
	ClockInfo()
	{
		this.m_princeLv = 0;
		this.m_charisma = 0;
		this.m_charismaMax = 0;
		this.m_charismaSub = 0;
		this.m_stamina = 0;
		this.m_staminaMax = 0;
		this.m_staminaSub = 0;
		this.m_saveTime = System.currentTimeMillis();
	}
	
	/** カリスマを再計算 */
	void resetCharisma() {
		// 王子Lvから算出
		if( m_princeLv < 99 ) {
			m_charismaMax = 29 + m_princeLv * 3;
		} else if( m_princeLv < 200 ) {
			m_charismaMax = 326 + m_princeLv - 99;
		} else if( m_princeLv < 300 ) {
			m_charismaMax = 427;
		}
		
		m_charisma = Math.min( m_charismaMax +1, m_charisma );
	}

	/** スタミナを再計算 */
	void resetStamina() {
		// 王子Lvから算出
		if( m_princeLv < 100 ) {
			m_staminaMax = 11;
		} else if( m_princeLv < 119 ) {
			m_staminaMax = 12;
		} else if( m_princeLv < 139 ) {
			m_staminaMax = 13;
		} else if( m_princeLv < 159 ) {
			m_staminaMax = 14;
		} else if( m_princeLv < 179 ) {
			m_staminaMax = 15;
		} else if( m_princeLv < 199 ) {
			m_staminaMax = 16;
		} else if( m_princeLv < 200 ) {
			m_staminaMax = 17;
		} else if( m_princeLv < 300 ) {
			m_staminaMax = 17;
		}
		
		m_stamina = Math.min( m_staminaMax +1, m_stamina );
	}

	/**
	 *  設定値を保存
	 *  @param context コンテキスト
	 *  @param appWidgetId 設定値を保存するウィジェットID
	 */
	void saveValues( @NonNull final Context context, final int appWidgetId, final long currentTimeMillis )
	{
		if( appWidgetId == -1 || appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) return;
		this.m_saveTime = currentTimeMillis;
		
		final SharedPreferences.Editor editor = context.getSharedPreferences( PREFS_NAME + appWidgetId, 0 ).edit();
		editor.putInt( PREF_PREFIX_KEY + "PrinceLv", this.m_princeLv );
		editor.putInt( PREF_PREFIX_KEY + "Charisma", this.m_charisma );
		editor.putInt( PREF_PREFIX_KEY + "CharismaMax", this.m_charismaMax );
		editor.putInt( PREF_PREFIX_KEY + "CharismaSub", this.m_charismaSub );
		editor.putInt( PREF_PREFIX_KEY + "Stamina", this.m_stamina );
		editor.putInt( PREF_PREFIX_KEY + "StaminaMax", this.m_staminaMax );
		editor.putInt( PREF_PREFIX_KEY + "StaminaSub", this.m_staminaSub );
		editor.putLong( PREF_PREFIX_KEY + "SAVETIME", this.m_saveTime );
		editor.commit();
		
		s_widgetIdToInstance.append( appWidgetId, this );
	}
	
	/**
	 * charisma, charismaSub, stamina, staminaSub を現在の時刻で再設定する
	 */
	void saveCurrent( @NonNull final Context context, final int appWidgetId,  final long currentTimeMillis )
	{
		if( appWidgetId == -1 || appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) return;
		
		m_charisma = this.getCharisma( currentTimeMillis );
		m_stamina = this.getStamina( currentTimeMillis );
		m_staminaSub = this.getStaminaSub( currentTimeMillis );
		m_saveTime = currentTimeMillis;
		saveValues( context, appWidgetId, currentTimeMillis );
	}
	
	/**
	 * ウィジェットIDから設定値をロードします
	 * @param context コンテキスト
	 * @param appWidgetId ウィジェットID
	 * @return 設定値
	 */
	static @NonNull ClockInfo loadValues( @NonNull final Context context, final int appWidgetId )
	{
		if( appWidgetId == -1 || appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) {
			return new ClockInfo();
		}
		
		ClockInfo info = s_widgetIdToInstance.get( appWidgetId );
		if( info == null ) {
			info = new ClockInfo();
			s_widgetIdToInstance.append( appWidgetId, info );
		}
		
		final SharedPreferences prefs = context.getSharedPreferences( PREFS_NAME + appWidgetId, 0 );
		info.m_princeLv 	= prefs.getInt( PREF_PREFIX_KEY + "PrinceLv", 0 );
		info.m_charisma 	= prefs.getInt( PREF_PREFIX_KEY + "Charisma", 0 );
		info.m_charismaMax 	= prefs.getInt( PREF_PREFIX_KEY + "CharismaMax", 0 );
		info.m_charismaSub 	= prefs.getInt( PREF_PREFIX_KEY + "CharismaSub", 0 );
		info.m_stamina 		= prefs.getInt( PREF_PREFIX_KEY + "Stamina", 0 );
		info.m_staminaMax 	= prefs.getInt( PREF_PREFIX_KEY + "StaminaMax", 0 );
		info.m_staminaSub 	= prefs.getInt( PREF_PREFIX_KEY + "StaminaSub", 0 );
		info.m_saveTime		= prefs.getLong( PREF_PREFIX_KEY + "SAVETIME", System.currentTimeMillis() );
		
		return info;
	}

}
