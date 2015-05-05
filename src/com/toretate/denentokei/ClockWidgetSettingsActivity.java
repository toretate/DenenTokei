package com.toretate.denentokei;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.toretate.denentokei.dialog.NumberPickerDialog;

/**
 * 各ウィジェットを設定するためのアクティビティ
 */
public class ClockWidgetSettingsActivity extends Activity
{
	@NonNull ClockInfo m_info = new ClockInfo();
	
	int m_appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;	//!< ウィジェットのIDの基
	boolean m_fromWidgetSettingButton;							//!< ウィジェットの設定ボタンから来たかどうか

	@InjectView(R.id.princeLvSpinner) Button m_princeLvSpinner;
	
	@InjectView(R.id.charismaMax) TextView m_charismaMax;
	@InjectView(R.id.charisma) Button m_charismaSpinner;
	
	@InjectView(R.id.staminaMax) TextView m_staminaMax;
	@InjectView(R.id.stamina) Button m_staminaSpinner;
	@InjectView(R.id.staminaSub) Button m_staminaSubSpinner;
	
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
			
			this.m_fromWidgetSettingButton = extras.getBoolean( ClockWidget.s_fromWidgetSettingButton, Boolean.FALSE );
		}
		// ウィジェットIDの習得失敗したら goto fail
		if( this.m_appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) {
			Log.i("Widget", "FAILED:GET WIDGET ID");
			finish();
			return;
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
				final Activity activity = ClockWidgetSettingsActivity.this;
				saveValues( activity, m_appWidgetId, m_info );
	
				if( ClockWidgetSettingsActivity.this.m_fromWidgetSettingButton ) {
					// 単に終了
					// 成功を返す
					final Intent result = new Intent( ClockWidgetSettingsActivity.this, ClockWidget.class );
					result.setAction( AppWidgetManager.ACTION_APPWIDGET_UPDATE );
					result.putExtra( AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { m_appWidgetId } );
					result.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, m_appWidgetId );
					sendBroadcast( result );
					finish();
				} else {
					// 成功を返す
					final Intent result = new Intent( ClockWidgetSettingsActivity.this, ClockWidget.class );
					result.setAction( AppWidgetManager.ACTION_APPWIDGET_UPDATE );
					result.putExtra( AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { m_appWidgetId } );
					result.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, m_appWidgetId );
					sendBroadcast( result );
					setResult( RESULT_OK, result );
					finish();
				}
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
		final Button button = m_princeLvSpinner;
		button.setText( String.valueOf( m_info.getPrinceLv() ) );
		button.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = getString( R.string.princeLv );
				title = title != null ? title : "";
				final NumberPickerDialog dialog = new NumberPickerDialog(m_info.getPrinceLv(), 1, 300, title );
				dialog.setNumberChangedListener( new NumberPickerDialog.NumberChangedListener() {
					@Override
					public void numberChanged( int number ) {
						m_info.setPrinceLv( number );
						button.setText( String.valueOf(number) );
					}

					@Override
					public void dialogClosed(int number) {
						initCharismaUI();
						initStaminaUI();
					}
				});
				dialog.show( ClockWidgetSettingsActivity.this.getFragmentManager(), "priceLvPicker" );
			}
		});
		
		initCharismaUI();
		initStaminaUI();
	}
	
	static void saveValues( @NonNull final Context context, int appWidgetId, @NonNull final ClockInfo info )
	{
		info.saveValues( context, appWidgetId, System.currentTimeMillis() );
	}
	
	/** カリスマ設定 */
	private void initCharismaUI()
	{
		m_charismaMax.setText( "/" + m_info.getCharismaMaxString() );
		
		final Button button = m_charismaSpinner;
		button.setText( String.valueOf( m_info.getCharisma() ) );
		button.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = getString( R.string.charisma );
				title = title != null ? title : "";
				final NumberPickerDialog dialog = new NumberPickerDialog( m_info.getCharisma(), 0, m_info.getCharismaMax() +1, title );
				dialog.setNumberChangedListener( new NumberPickerDialog.NumberChangedListener() {
					@Override
					public void numberChanged( int number ) {
						m_info.setCharisma( number );
						button.setText( String.valueOf( m_info.getCharisma() ) );
					}

					@Override
					public void dialogClosed(int number) {
					}
				});
				dialog.show( ClockWidgetSettingsActivity.this.getFragmentManager(), "charismaPicker" );
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
			final Button button = m_staminaSpinner;
			button.setText( String.valueOf( m_info.getStamina() ) );
			button.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					String title = getString( R.string.stamina );
					title = title != null ? title : "";
					final NumberPickerDialog dialog = new NumberPickerDialog( m_info.getStamina(), 0, m_info.getStaminaMax() +1, title );
					dialog.setNumberChangedListener( new NumberPickerDialog.NumberChangedListener() {
						@Override
						public void numberChanged( int number ) {
							m_info.setStamina( number );
							button.setText( String.valueOf( m_info.getStamina() ) );
						}

						@Override
						public void dialogClosed(int number) {
						}
					});
					dialog.show( ClockWidgetSettingsActivity.this.getFragmentManager(), "staminaPicker" );
				}
			});
			
		}
		
		// スタミナ回復まで設定
		{
			// 現在値Spinnerを 0～60に変更
			final Button button = m_staminaSubSpinner;
			button.setText( String.valueOf( m_info.getStaminaSub() ) );
			button.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					String title = getString( R.string.next );
					title = title != null ? title : "";
					final NumberPickerDialog dialog = new NumberPickerDialog( m_info.getStaminaSub(), 0, 60, title );
					dialog.setNumberChangedListener( new NumberPickerDialog.NumberChangedListener() {
						@Override
						public void numberChanged( int number ) {
							m_info.setStaminaSub( number );
							button.setText( String.valueOf( m_info.getStaminaSub() ) );
						}

						@Override
						public void dialogClosed(int number) {
						}
					});
					dialog.show( ClockWidgetSettingsActivity.this.getFragmentManager(), "staminaSubPicker" );
				}
			});
		}
		
	}
}
