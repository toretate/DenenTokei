package com.toretate.denentokei;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * 各ウィジェットを作成する際の初期値となるものを設定する
 */
public class MainActivity extends Activity
{
	@NonNull ClockInfo m_info = new ClockInfo();
	
	@InjectView(R.id.princeLvSpinner) Spinner m_princeLvSpinner;
	
	@InjectView(R.id.adView) AdView m_adView;
	

	@Override
	protected void onCreate( @Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject( this );
		
		// Admob
		AdRequest adRequest = new AdRequest.Builder()
											.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
											.addTestDevice("10F2FE9276EAE35819748257A08833AE")
											.build();
		m_adView.loadAd(adRequest);

		// 初期値設定
		m_info = ClockInfo.loadWidgetCreateValues( this );
		ClockInfo.saveWidgetCreateValues( this, m_info );
		
		initPrinceLvListUI();
		
		setResult( RESULT_OK );
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

	/**
	 * 王子LvUIの初期設定
	 */
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
			public void onItemSelected( AdapterView<?> parent, View view, int position, long id ) {
				m_info.setPrinceLv( position );
				
				// prefに保存
				final Context ctx = MainActivity.this;
				ClockInfo.saveWidgetCreateValues( ctx, m_info );
				
				// Toastで保存したことを通知
				final String msg = ctx.getString( R.string.saved );
				Toast.makeText( ctx, msg, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
}
