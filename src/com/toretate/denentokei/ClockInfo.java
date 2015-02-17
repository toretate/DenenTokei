package com.toretate.denentokei;

import android.content.Context;
import android.content.SharedPreferences;

public class ClockInfo
{
	
    private static final String PREFS_NAME = "com.toretate.denentokei.ClockInfo";
    private static final String PREF_PREFIX_KEY = "prefix_";
    
    private int m_princeLv;
	private int m_charisma;
	private int m_charismaMax;
	private int m_stamina;
	private int m_staminaMax;
	private long m_saveTime;
	
	public void setPrinceLv( int lv )
	{ 
		m_princeLv = lv;
		resetCharismaMax();
		resetStaminaMax();
	}
	public int getPrinceLv() { return m_princeLv; }
	public int getCharisma() { return m_charisma; }
	public int getCharismaMax() { return m_charismaMax; }
	public int getStamina() { return m_stamina; }
	public int getStaminaMax() { return m_staminaMax; }
	public long getSaveTime() { return m_saveTime; }
	
	public String getCharismaString() { return m_charisma + "/" + m_charismaMax; }
	public String getStaminaString() { return m_stamina + "/" + m_staminaMax; }
	
	ClockInfo()
	{
		this.m_princeLv = 1;
		this.m_charisma = 1;
		this.m_charismaMax = 1;
		this.m_stamina = 1;
		this.m_staminaMax = 1;
		this.m_saveTime = System.currentTimeMillis();
	}
	
	void copyFrom( ClockInfo from )
	{
		this.m_princeLv = from.getPrinceLv();
		this.m_charisma = from.getCharisma();
		this.m_charismaMax = from.getCharismaMax();
		this.m_stamina = from.getStamina();
		this.m_staminaMax = from.getStaminaMax();
	}
	
	/** カリスマ最大量 */
	void resetCharismaMax()
	{
		// 王子Lvから算出
		if( m_princeLv < 99 ) {
			m_charismaMax = 30 + m_princeLv * 3;
		} else if( m_princeLv < 200 ) {
			m_charismaMax = 327 + m_princeLv - 99;
		}
		
		m_charisma = Math.max( m_charismaMax, m_charisma );
	}

	/** スタミナ最大量 */
	void resetStaminaMax()
	{
		// 王子Lvから算出
		if( m_princeLv < 100 ) {
			m_staminaMax = 12;
		} else if( m_princeLv < 119 ) {
			m_staminaMax = 13;
		} else if( m_princeLv < 139 ) {
			m_staminaMax = 14;
		} else if( m_princeLv < 159 ) {
			m_staminaMax = 15;
		} else if( m_princeLv < 179 ) {
			m_staminaMax = 16;
		} else if( m_princeLv < 199 ) {
			m_staminaMax = 17;
		} else if( m_princeLv < 200 ) {
			m_staminaMax = 18;
		}
		
		m_stamina = Math.max( m_stamina, m_staminaMax );
	}

	void saveValues( Context context, int appWidgetId )
	{
		SharedPreferences.Editor editor = context.getSharedPreferences( PREFS_NAME + appWidgetId, 0 ).edit();
		editor.putInt( PREF_PREFIX_KEY + "PrinceLv", this.m_princeLv );
		editor.putInt( PREF_PREFIX_KEY + "Charisma", this.m_charisma );
		editor.putInt( PREF_PREFIX_KEY + "CharismaMax", this.m_charismaMax );
		editor.putInt( PREF_PREFIX_KEY + "Stamina", this.m_stamina );
		editor.putInt( PREF_PREFIX_KEY + "StaminaMax", this.m_staminaMax );
		editor.putLong( PREF_PREFIX_KEY + "SAVETIME", System.currentTimeMillis() );
		editor.commit();
	}
	
	static ClockInfo loadValues( Context context, int appWidgetId )
	{
		ClockInfo info = new ClockInfo();
		
		SharedPreferences prefs = context.getSharedPreferences( PREFS_NAME + appWidgetId, 0 );
		info.m_princeLv 	= prefs.getInt( PREF_PREFIX_KEY + "PrinceLv", 1 );
		info.m_charisma 	= prefs.getInt( PREF_PREFIX_KEY + "Charisma", 1 );
		info.m_charismaMax 	= prefs.getInt( PREF_PREFIX_KEY + "CharismaMax", 1 );
		info.m_stamina 		= prefs.getInt( PREF_PREFIX_KEY + "Stamina", 1 );
		info.m_staminaMax 	= prefs.getInt( PREF_PREFIX_KEY + "StaminaMax", 1 );
		info.m_saveTime		= prefs.getLong( PREF_PREFIX_KEY + "SAVETIME", System.currentTimeMillis() );
		
		return info;
	}

}
