package com.toretate.denentokei;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * 各ウィジェットを設定するためのアクティビティ
 */
public class ClockWidgetSettingsActivity extends Activity
{
	@NonNull ClockInfo m_info = new ClockInfo();
	
	int m_appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;	//!< ウィジェットのIDの基

	@InjectView(R.id.princeLvSpinner) Spinner m_princeLvSpinner;
	
	@InjectView(R.id.charismaMax) TextView m_charismaMax;
	@InjectView(R.id.charisma) Spinner m_charismaSpinner;
	
	@InjectView(R.id.staminaMax) TextView m_staminaMax;
	@InjectView(R.id.stamina) Spinner m_staminaSpinner;
	@InjectView(R.id.staminaSub) Spinner m_staminaSubSpinner;
	
	@InjectView(R.id.saveButton) Button m_saveButton;
	
	@InjectView(R.id.adView) AdView m_adView;
	

	@Override
	protected void onCreate( @Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.widget_settings);
		ButterKnife.inject( this );
		
		// Admob
		AdRequest adRequest = new AdRequest.Builder()
											.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
											.addTestDevice("10F2FE9276EAE35819748257A08833AE")
											.build();
		m_adView.loadAd(adRequest);

		// 標準をキャンセルにしておく -> Backボタンなどで閉じられた場合にウィジェットが配置されないように
		setResult( RESULT_CANCELED );

		final Intent intent = this.getIntent();
		final Bundle extras = intent.getExtras();
		if( extras != null ) {
			// ウィジェットIDの取得
			this.m_appWidgetId = extras.getInt( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID );
		}
		// ウィジェットIDの習得失敗したら goto fail
		if( this.m_appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) {
			finish();
		}
		
		// 最低値設定
		m_info = ClockInfo.loadValues( this, this.m_appWidgetId );
		m_info.saveCurrent( this, this.m_appWidgetId, System.currentTimeMillis() );
		
		initPrinceLvListUI();
		
		m_saveButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( @Nullable final View v) {
				// 保存ボタンの処理
				
				// prefに保存
				final Context ctx = ClockWidgetSettingsActivity.this;
				saveValues( ctx, m_appWidgetId, m_info );
				
				// 成功を返す
				final Intent result = new Intent( ClockWidgetSettingsActivity.this, ClockWidget.class );
				result.setAction( AppWidgetManager.ACTION_APPWIDGET_UPDATE );
				result.putExtra( AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { m_appWidgetId } );
				result.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, m_appWidgetId );
				sendBroadcast( result );
				setResult( RESULT_OK, result );
				finish();
			}
		});;
	}
	
	@Override
	protected void onPause()
	{
		m_adView.pause();
		super.onPause();
	};
	
	@Override
	protected void onResume() {
		m_adView.resume();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		m_adView.destroy();
		super.onDestroy();
	}

	private void initPrinceLvListUI()
	{
		// 王子Lv(表示上1～300)
		final Spinner spinner = m_princeLvSpinner;
		spinner.setOnItemSelectedListener( null );

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_spinner_item );
		for( int i=0; i<300; i++ ) {
			adapter.add( ( i +1 ) +"" );
		}
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_item );
		spinner.setAdapter( adapter );
		
		spinner.setSelection( m_info.getPrinceLv() );
		spinner.setOnItemSelectedListener( new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				m_info.setPrinceLv( position );
				
				initCharismaUI();
				initStaminaUI();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	static void saveValues( @NonNull final Context context, int appWidgetId, @NonNull final ClockInfo info )
	{
		info.saveValues( context, appWidgetId, System.currentTimeMillis() );
	}
	
	/** カリスマ設定 */
	private void initCharismaUI()
	{
		m_charismaMax.setText( "/" + m_info.getCharismaMaxString() );
		
		// 現在値Spinnerを 0～m_charismaMaxに変更
		final Spinner spinner = m_charismaSpinner;
		spinner.setOnItemSelectedListener( null );
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		for( int i=0; i<=m_info.getCharismaMax() +1; i++ ) {
			adapter.add( String.valueOf( i ) );
		}
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_item );
		spinner.setAdapter( adapter );

		// カリスマ値の設定
		spinner.setSelection( m_info.getCharisma() );
		spinner.setOnItemSelectedListener( new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				m_info.setCharisma( position );
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	/** スタミナ設定 */
	private void initStaminaUI()
	{
		// 王子Lvから算出
		final TextView textView = m_staminaMax;
		textView.setText( "/" + m_info.getStaminaMaxString() );
		
		// スタミナ現在値設定
		{
			// 現在値Spinnerを 0～m_staminaMaxに変更
			final Spinner spinner = m_staminaSpinner;
			spinner.setOnItemSelectedListener( null );
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
			for( int i=0; i<=m_info.getStaminaMax() +1; i++ ) {
				adapter.add( String.valueOf( i ) );
			}
			adapter.setDropDownViewResource( android.R.layout.simple_spinner_item );
			spinner.setAdapter( adapter );

			// スタミナ値設定
			spinner.setSelection( m_info.getStamina() );
			spinner.setOnItemSelectedListener( new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					m_info.setStamina( position );
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
		}
		
		// スタミナ回復まで設定
		{
			// 現在値Spinnerを 0～60に変更
			final Spinner spinner = m_staminaSubSpinner;
			spinner.setOnItemSelectedListener( null );
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
			for( int i=0; i<=60; i++ ) {
				adapter.add( String.valueOf( i ) );
			}
			adapter.setDropDownViewResource( android.R.layout.simple_spinner_item );
			spinner.setAdapter( adapter );

			// スタミナ値設定
			spinner.setSelection( m_info.getStaminaSub() );
			spinner.setOnItemSelectedListener( new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					m_info.setStaminaSub( position );
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
		}
		
	}
}
