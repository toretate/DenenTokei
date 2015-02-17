package com.toretate.denentokei;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity
{
	ClockInfo m_info;
	
	int m_appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;	//!< ウィジェットのIDの基

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 標準をキャンセルにしておく -> Backボタンなどで閉じられた場合にウィジェットが配置されないように
		setResult( RESULT_CANCELED );
		
		setContentView(R.layout.activity_main);

		// 最低値設定
		m_info = new ClockInfo();
		
		initPrinceLvList();
		
		this.findViewById( R.id.saveButton ).setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 保存ボタンの処理
				
//				// ウィジェットの更新
//				Context ctx = MainActivity.this;
//				AppWidgetManager mng = AppWidgetManager.getInstance( ctx );
//				ClockWidget.updateWidget( ctx, mng, m_appWidgetId );
				
				// 成功を返す
				Intent result = new Intent( MainActivity.this, ClockWidget.class );
//				Intent result = new Intent();
				result.setAction( AppWidgetManager.ACTION_APPWIDGET_UPDATE );
//				result.putExtra( AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { m_appWidgetId } );
				result.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, m_appWidgetId );
//				sendBroadcast( result );
				setResult( RESULT_OK, result );
				finish();
			}
		});;
		
		Intent intent = this.getIntent();
		Bundle extras = intent.getExtras();
		if( extras != null ) {
			// ウィジェットIDの取得
			this.m_appWidgetId = extras.getInt( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID );
		}
		
		// ウィジェットIDの習得失敗したら goto fail
		if( this.m_appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initPrinceLvList()
	{
		// 王子Lv(表示上1～200)
		Spinner spinner = (Spinner)findViewById( R.id.princeLvSpinner );
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_spinner_item );
		for( int i=0; i<200; i++ ) {
			adapter.add( ( i +1 ) +"" );
		}
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_item );
		spinner.setAdapter( adapter );
		spinner.setOnItemSelectedListener( new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				m_info.setPrinceLv( position );
				
				resetCharismaUI();
				resetStaminaUI();
				
				// Prefsに保存
				Context ctx = MainActivity.this;
				saveValues( ctx, m_appWidgetId, m_info );
				
				AppWidgetManager mng = AppWidgetManager.getInstance( ctx );
				ClockWidget.updateWidget( ctx, mng, m_appWidgetId );
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	static void saveValues( Context context, int appWidgetId, ClockInfo info )
	{
		info.saveValues(context, appWidgetId);
	}
	
	/** カリスマ最大量 */
	private void resetCharismaUI()
	{
		TextView textView = (TextView)this.findViewById( R.id.charismaMax );
		textView.setText( "/" + m_info.getCharismaMax() );
		
		// 現在値Spinnerを 30～m_charismaMaxに変更
		Spinner spinner = (Spinner)findViewById( R.id.charisma );
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		for( int i=0; i<m_info.getCharismaMax(); i++ ) {
			adapter.add( ( i +1 ) +"" );
		}
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_item );
		spinner.setAdapter( adapter );
		
		// カリスマ値の補正
		final int charisma = m_info.getCharisma();
		spinner.setSelection( charisma - 1 );
	}
	
	/** スタミナ最大量 */
	private void resetStaminaUI()
	{
		// 王子Lvから算出
		TextView textView = (TextView)this.findViewById( R.id.staminaMax );
		textView.setText( "/" + m_info.getStaminaMax() );
		
		// 現在値Spinnerを 1～m_staminaMaxに変更
		Spinner spinner = (Spinner)findViewById( R.id.stamina );
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		for( int i=0; i<m_info.getStaminaMax(); i++ ) {
			adapter.add( ( i +1 ) +"" );
		}
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_item );
		spinner.setAdapter( adapter );
		
		// スタミナ値
		spinner.setSelection( m_info.getStamina() -1 );
	}
}
