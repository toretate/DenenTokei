package com.toretate.denentokei2;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.toretate.denentokei2.dialog.NumberPickerDialog;
import com.toretate.denentokei2.preset.PresetChaStaDefs;

import org.json.JSONException;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;


/**
 * 各ウィジェットを作成する際の初期値となるものを設定する
 */
public class MainActivity extends Activity
{
	@NonNull ClockInfo m_info = new ClockInfo();
	
	@Bind(R.id.princeLvSpinner) Button m_princeLvSpinner;
	
//	@Bind(R.id.showGoogle) Button m_showGoogle;

	@Bind(R.id.adView) AdView m_adView;

	@Bind(R.id.stamina) TextView m_stamina;
	@Bind(R.id.staminaSettingButton) Button m_staminaButton;
	@Bind(R.id.staminaSub) TextView m_staminaSub;
	@Bind(R.id.staminaSubSettingButton) Button m_staminaSubButton;
	@Bind(R.id.charisma) TextView m_charisma;
	@Bind(R.id.charismaSettingButton) Button m_charismaButton;

	private boolean m_isStatusEditMode = false;

	private Handler m_handler;
	private long m_time = System.currentTimeMillis();	// 表示の基本となる時刻

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
											.build();
		m_adView.loadAd(adRequest);

		m_handler = new Handler();
		m_handler.postDelayed(new Runnable() {
								  @Override
								  public void run() {

									  m_time = System.currentTimeMillis();
									  refreshControls();

									  m_handler.postDelayed( this, 1000 * 60 );
								  }
							  }, 1000 * 60
		);


				// 初期値設定
				m_info = ClockInfo.loadWidgetCreateValues(this);
		ClockInfo.saveWidgetCreateValues( this, m_info );

		initControls();

//		m_showGoogle.setOnClickListener( new OnClickListener() {
//			@Override
//			public void onClick( View v ) {
//				Intent intent = new Intent( Intent.ACTION_WEB_SEARCH );
//				intent.putExtra( SearchManager.QUERY, "ホームにウィジェット登録" );
//				startActivity( intent );
//			}
//		} );
		
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

	private void initControls() {
		TextView text;
		Button button;

		ImageButton imgButton;

		initPrinceLvListUI();

		// カリスマ、スタミナの設定
		refreshControls();
		{
			// スタミナ

			button = m_staminaButton;
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String title = getString( R.string.stamina );
					title = title != null ? title : "";
					final NumberPickerDialog dialog = new NumberPickerDialog();
					dialog.setArguments( NumberPickerDialog.createBundle( m_info.getStamina(), 0, m_info.getStaminaMax() + 1, title ));
					dialog.setNumberChangedListener( new NumberPickerDialog.NumberChangedListener() {
						@Override
						public void numberChanged( int number ) { setStamina( number, /*needSave*/false ); }
						@Override
						public void dialogClosed( int number ) { setStamina(number, /*needSave*/true); }
					} );
					dialog.show( MainActivity.this.getFragmentManager(), "staminaPicker" );
				}
			});

			// スタミナ(Sub)
			button = (Button)this.findViewById( R.id.staminaSubSettingButton );
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String title = getString( R.string.stamina );
					title = title != null ? title : "";
					final NumberPickerDialog dialog = new NumberPickerDialog();
					dialog.setArguments( NumberPickerDialog.createBundle( m_info.getStaminaSub(), 0, 60, title ) );
					dialog.setNumberChangedListener( new NumberPickerDialog.NumberChangedListener() {
						@Override
						public void numberChanged( int number ) { setStaminaSub(number, /*needSave*/false); }
						@Override
						public void dialogClosed( int number ) { setStaminaSub(number, /*needSave*/true); }
					} );
					dialog.show( MainActivity.this.getFragmentManager(), "staminaSubPicker" );
				}
			});

			// カリスマ
			button = (Button)this.findViewById( R.id.charismaSettingButton );
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String title = getString( R.string.charisma );
					title = title != null ? title : "";
					final NumberPickerDialog dialog = new NumberPickerDialog();
					dialog.setArguments( NumberPickerDialog.createBundle( m_info.getCharisma(), 0, m_info.getCharismaMax() + 1, title ) );
					dialog.setNumberChangedListener( new NumberPickerDialog.NumberChangedListener() {
						@Override
						public void numberChanged( int number ) { setCharisma(number, /*needSave*/false); }
						@Override
						public void dialogClosed( int number ) { setCharisma(number, /*needSave*/true); }
					} );
					dialog.show( MainActivity.this.getFragmentManager(), "charismaPicker" );
				}
			});

		}

		// 歯車UIの設定
		{
			imgButton = (ImageButton)this.findViewById( R.id.setting_status_button );
			imgButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					m_isStatusEditMode = !m_isStatusEditMode;

					int[] textViewIds = {R.id.stamina, R.id.staminaSub, R.id.charisma };
					int[] buttonIds = {R.id.staminaSettingButton, R.id.staminaSubSettingButton, R.id.charismaSettingButton};

					if( m_isStatusEditMode ) {
						// TextViewを非表示、Buttonを表示に変更
						for( int i=0; i<textViewIds.length; i++ ) {
							TextView text = (TextView)findViewById( textViewIds[i] );
							text.setVisibility( View.GONE );
							Button button = (Button)findViewById( buttonIds[i] );
							button.setVisibility( View.VISIBLE );
						}
					} else {
						// 上記の逆
						for( int i=0; i<textViewIds.length; i++ ) {
							TextView text = (TextView)findViewById( textViewIds[i] );
							text.setVisibility( View.VISIBLE );
							Button button = (Button)findViewById( buttonIds[i] );
							button.setVisibility( View.GONE );
						}
					}
				}
			});
		}
	}


	/**
	 * 王子LvUIの初期設定
	 */
	private void initPrinceLvListUI()
	{
		// 王子Lv(表示上1～300)
		final Button button = m_princeLvSpinner;
		button.setText(String.valueOf(m_info.getPrinceLv()));
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = getString(R.string.princeLv);
				title = title != null ? title : "";

				final NumberPickerDialog dialog = new NumberPickerDialog();
				dialog.setArguments( NumberPickerDialog.createBundle(m_info.getPrinceLv(), 1, 300, title));
				dialog.setNumberChangedListener(new NumberPickerDialog.NumberChangedListener() {
					@Override
					public void numberChanged(int number) {
						m_info.setPrinceLv(number);
						button.setText(String.valueOf(number));
					}

					@Override
					public void dialogClosed(int number) {
						// prefに保存
						final Context ctx = MainActivity.this;
						ClockInfo.saveWidgetCreateValues(ctx, m_info);

						// Toastで保存したことを通知
						final String msg = ctx.getString(R.string.saved);
						Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();

						refreshControls();
					}
				});

				dialog.show(MainActivity.this.getFragmentManager(), "priceLvPicker");
			}
		});
	}
	

	@Override
	public boolean onCreateOptionsMenu( final Menu menu ) {
		if( menu == null ) return false;
		return MenuUtils.onCreateOptionsMenu(menu, this);
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item ) {
		return MenuUtils.onOptionsItemSelected(item, this);
	}

	private void setStamina( final int stamina, final boolean needSave ) {
		m_info.setStamina(stamina);
		m_time = System.currentTimeMillis();
		if( needSave ) saveValues(this, m_info, m_time);

		refreshControls();
	}

	private void setStaminaSub( final int staminaSub, final boolean needSave ) {
		m_info.setStaminaSub(staminaSub);
		m_time = System.currentTimeMillis();
		if( needSave ) saveValues(this, m_info, m_time);

		refreshControls();
	}

	private void setCharisma( final int charisma, final boolean needSave ) {
		m_info.setCharisma(charisma);
		m_time = System.currentTimeMillis();
		if( needSave ) saveValues(this, m_info, m_time);

		refreshControls();
	}

	void saveValues( @NonNull final Context context, @NonNull final ClockInfo info, final long time ) {
		info.saveValues(context, time);
	}

	private void refreshControls(){
		m_stamina.setText(m_info.getStaminaString(m_time));
		m_staminaButton.setText( m_stamina.getText() );
		m_staminaSub.setText(m_info.getStaminaSubString(m_time));
		m_staminaSubButton.setText( m_staminaSub.getText() );
		m_charisma.setText(m_info.getCharismaString(m_time));
		m_charismaButton.setText( m_charisma.getText() );
	}


}
