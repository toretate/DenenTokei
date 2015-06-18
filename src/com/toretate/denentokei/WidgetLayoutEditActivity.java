package com.toretate.denentokei;

import java.util.ArrayList;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.toretate.denentokei.FontSetting.FontSettingType;

public class WidgetLayoutEditActivity extends Activity {
	
	private WidgetLayoutInfo m_info;
	
	private @Nullable FontSetting m_chaFontSetting;
	private @Nullable FontSetting m_staFontSetting;
	private @Nullable FontSetting m_staSubFontSetting;

	// Cha
	@InjectView(R.id.charismaFontListButton1)
	@Nullable Button m_chaFontFamilyButton;
	
	@InjectView(R.id.charismaFontSizeButton)
	@Nullable Button m_chaFontSizeButton;
	
	@InjectView(R.id.chaClock)
	@Nullable TextView m_chaTextView;
	
	// Sta
	@InjectView(R.id.staminaFontListButton1)
	@Nullable Button m_staFontFamilyButton;
	
	@InjectView(R.id.staminaFontSizeButton)
	@Nullable Button m_staFontSizeButton;
	
	@InjectView(R.id.staClock)
	@Nullable TextView m_staTextView;
	
	// StaSub
	@InjectView(R.id.staSubFontListButton1)
	@Nullable Button m_staSubFontFamilyButton;
	
	@InjectView(R.id.staSubFontSizeButton)
	@Nullable Button m_staSubFontSizeButton;
	
	@InjectView(R.id.staSubClock)
	@Nullable TextView m_staSubTextView;
	
	
	// frame, image
	@InjectView(R.id.frameRatingBar1)
	@Nullable RatingBar m_frameBar;
	
	@InjectView(R.id.imageRatingBar1)
	@Nullable RatingBar m_imageBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_widget_layout);
		ButterKnife.inject( this );

		final Intent intent = this.getIntent();
		int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
		if( intent != null ) {
			appWidgetId = intent.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID );
		}
		
		m_chaTextView = (TextView)this.findViewById( R.id.chaClock );
		m_staTextView = (TextView)this.findViewById( R.id.staClock );
		m_staSubTextView = (TextView)this.findViewById( R.id.staSubClock );
		
		m_info = new WidgetLayoutInfo( this, appWidgetId );
		m_info.getTextLayoutInfo( FontSettingType.Cha ).load();
		m_info.getTextLayoutInfo( FontSettingType.Sta ).load();
		m_info.getTextLayoutInfo( FontSettingType.StaSub ).load();

		TextView textView;
		Button fontFamily;
		Button fontSize;
		FontSetting fontSetting;
		
		// カリスマ設定
		fontSetting = null;
		textView = m_chaTextView;
		fontFamily = m_chaFontFamilyButton;
		fontSize = m_chaFontSizeButton;
		if( textView != null && fontFamily != null && fontSize != null ) {
			fontSetting = new FontSetting( fontFamily, fontSize, textView, this, FontSetting.FontSettingType.Cha, m_info );
			fontSetting.resetView();
		}
		m_chaFontSetting = fontSetting;
		
		// スタミナ設定
		fontSetting = null;
		textView = m_staTextView;
		fontFamily = m_staFontFamilyButton;
		fontSize = m_staFontSizeButton;
		if( textView != null && fontFamily != null && fontSize != null ) {
			fontSetting = new FontSetting( fontFamily, fontSize, textView, this, FontSetting.FontSettingType.Sta, m_info );
			fontSetting.resetView();
		}
		m_staFontSetting = fontSetting;
		
		// スタミナ(Sub)設定
		fontSetting = null;
		textView = m_staSubTextView;
		fontFamily = m_staSubFontFamilyButton;
		fontSize = m_staSubFontSizeButton;
		if( textView != null && fontFamily != null && fontSize != null ) {
			fontSetting = new FontSetting( fontFamily, fontSize, textView, this, FontSetting.FontSettingType.StaSub, m_info );
			fontSetting.resetView();
		}
		m_staSubFontSetting = fontSetting;
		
		final ImageView view1 = (ImageView)this.findViewById( R.id.view1 );
		final View frame1 = (View)this.findViewById( R.id.frame1 );
		
		// RatingBarの初期値
		ArrayList<RatingBar> bars = new ArrayList<RatingBar>();
		bars.add( m_frameBar );
		bars.add( m_imageBar );
		
		for( RatingBar bar : bars ) {
			bar.setNumStars( 5 );
			bar.setRating( 1 );
			bar.setStepSize( 1 );
			bar.setOnRatingBarChangeListener( new OnRatingBarChangeListener() {
				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {					
					if( rating <= 1 ) ratingBar.setRating( 1 );	// 最低１保証
					rating = (float)Math.floor( rating );
					
					final int len = 70 * (int)rating - 30;
					final int pixcel = (int)TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, len, getResources().getDisplayMetrics() );
					
					if( ratingBar == m_imageBar ) {
						// 画像サイズ
						LayoutParams params = view1.getLayoutParams();
						params.height = pixcel;
						params.width = pixcel;
						view1.setLayoutParams( params );
					}
					if( ratingBar == m_frameBar ) {
						// frameサイズ
						LayoutParams params = frame1.getLayoutParams();
						params.height = pixcel;
						frame1.setLayoutParams( params );
					}
				}
			});
		}
	}
	
}
