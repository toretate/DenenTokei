package com.toretate.denentokei2;


import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Bind;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.toretate.denentokei2.dialog.NumberPickerDialog;
import com.toretate.denentokei2.preset.PresetChaStaDefs;

/**
 * 各ウィジェットを作成する際の初期値となるものを設定する
 */
public class MainActivity extends Activity
{
	@NonNull ClockInfo m_info = new ClockInfo();
	
	@Bind(R.id.princeLvSpinner) Button m_princeLvSpinner;
	
	@Bind(R.id.showGoogle) Button m_showGoogle;
	
	@Bind(R.id.adView) AdView m_adView;
	

	@Override
	protected void onCreate( @Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		
		try {
			PresetChaStaDefs.load( this );
		} catch( IOException e ) {
			e.printStackTrace();
		} catch( JSONException e ) {
			e.printStackTrace();
		}
		
		// menu
		getWindow().requestFeature( Window.FEATURE_ACTION_BAR | Window.FEATURE_ACTION_BAR_OVERLAY );
		
		setContentView(R.layout.activity_main);
		ButterKnife.bind( this );
		
		// Admob
		AdRequest adRequest = new AdRequest.Builder()
											.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
											.addTestDevice("10F2FE9276EAE35819748257A08833AE")
											.addTestDevice("66F8796DEDCDF99B7B8EFED91381216C")
											.build();
		m_adView.loadAd(adRequest);
		
		// 初期値設定
		m_info = ClockInfo.loadWidgetCreateValues( this );
		ClockInfo.saveWidgetCreateValues( this, m_info );
		
		initPrinceLvListUI();
		
		m_showGoogle.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				Intent intent = new Intent( Intent.ACTION_WEB_SEARCH );
				intent.putExtra( SearchManager.QUERY, "ホームにウィジェット登録" );
				startActivity( intent );
			}
		} );
		
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
		final Button button = m_princeLvSpinner;
		button.setText( String.valueOf( m_info.getPrinceLv() ));
		button.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = getString( R.string.princeLv );
				title = title != null ? title : "";
				
				final NumberPickerDialog dialog = new NumberPickerDialog( m_info.getPrinceLv(), 1, 300, title );
				dialog.setNumberChangedListener( new NumberPickerDialog.NumberChangedListener() {
					@Override
					public void numberChanged( int number ) {
						m_info.setPrinceLv( number );
						button.setText( String.valueOf(number) );
					}

					@Override
					public void dialogClosed(int number) {
						// prefに保存
						final Context ctx = MainActivity.this;
						ClockInfo.saveWidgetCreateValues( ctx, m_info );
						
						// Toastで保存したことを通知
						final String msg = ctx.getString( R.string.saved );
						Toast.makeText( ctx, msg, Toast.LENGTH_SHORT).show();
					}
				});
				
				dialog.show( MainActivity.this.getFragmentManager(), "priceLvPicker" );
			}
		});
	}
	

	@Override
	public boolean onCreateOptionsMenu( final Menu menu ) {
		if( menu == null ) return false;
		return MenuUtils.onCreateOptionsMenu( menu, this );
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item ) {
		return MenuUtils.onOptionsItemSelected( item, this );
	}
}