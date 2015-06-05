package com.toretate.denentokei;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class WidgetLayoutEditActivity extends Activity {
	
	FontSetting m_chaFontSeting;
	FontSetting m_staFontSeting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// カリスマ設定
		m_chaFontSeting = new FontSetting( R.id.charismaFontListButton1, R.id.charismaFontSizeButton, R.id.chaClock, this);
		// スタミナ設定
		m_staFontSeting = new FontSetting( R.id.staminaFontListButton1, R.id.staminaFontSizeButton, R.id.staClock, this);
		
		final ImageView view1 = (ImageView)this.findViewById( R.id.view1 );
		final View frame1 = (View)this.findViewById( R.id.frame1 );
		
		// RatingBarの初期値
		ArrayList<RatingBar> bars = new ArrayList<RatingBar>();
		final RatingBar frameBar = (RatingBar)this.findViewById(R.id.frameRatingBar1);
		bars.add( frameBar );
		final RatingBar imageBar = (RatingBar)this.findViewById(R.id.imageRatingBar1);
		bars.add( imageBar );
		
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
					
					if( ratingBar == imageBar ) {
						// 画像サイズ
						LayoutParams params = view1.getLayoutParams();
						params.height = pixcel;
						params.width = pixcel;
						view1.setLayoutParams( params );
					}
					if( ratingBar == frameBar ) {
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
