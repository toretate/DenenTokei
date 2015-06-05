package com.toretate.denentokei;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.toretate.denentokei.dialog.FontFamilyDialog;
import com.toretate.denentokei.dialog.FontFamilyDialog.FontFamilyChangedListener;
import com.toretate.denentokei.dialog.FontSizeDialog;
import com.toretate.denentokei.dialog.FontSizeDialog.FontSizeChangedListener;

public class FontSetting {
	
	Button m_fontFamilyButton;
	String m_fontFamily;
	
	Button m_fontSizeButton;
	int m_fontSize;
	
	TextView m_clock;
	DandDListener m_listener;
	GestureDetector m_gesture;
	
	public FontSetting( int fontFamilyButton, int fontSizeButton, final int clock, final Activity parent ) {
		m_fontFamilyButton = (Button)parent.findViewById( fontFamilyButton );
		m_fontFamily = "serif";
		
		m_fontSizeButton = (Button)parent.findViewById( fontSizeButton );
		m_fontSize = 10;	// 10sp
		
		m_clock = (TextView)parent.findViewById( clock );
		m_listener = new DandDListener( m_clock );
		m_gesture = new GestureDetector( parent, m_listener );
		m_clock.setOnTouchListener( new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				m_gesture.onTouchEvent( event );
				return false;
			}
		});
		
		setupFontFamilyButton( parent );
		setupFontSizeButton( parent );
	}
	
	private void setupFontFamilyButton( final Activity activity ) {
		m_fontFamilyButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				FontFamilyDialog dialog = new FontFamilyDialog( m_fontFamily );
				dialog.setFontFamilyChangedListener( new FontFamilyChangedListener() {
					@Override
					public void fontFamilyChanged(String family) {
						setFontFamily( family );
					}
				});
				dialog.show( activity.getFragmentManager(), "fontFamily" );
			}
		});
	}

	private void setupFontSizeButton( final Activity activity ) {
		m_fontSizeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FontSizeDialog dialog = new FontSizeDialog( m_fontSize );
				dialog.setFontSizeChangedListener( new FontSizeChangedListener() {
					@Override
					public void fontSizeChanged(int size) {
						setFontSize( size );
					}
				});
				dialog.show( activity.getFragmentManager(), "fontSizePicker" );
			}
		});
	}
	
	private void setFontFamily( final String family ) {
		m_fontFamily = family;
		m_fontFamilyButton.setText( family );
		
		m_clock.setTypeface( Typeface.create( family, Typeface.NORMAL ) );
	}
	
	private void setFontSize( final int fontSize ) {
		m_fontSize = fontSize;
		m_fontSizeButton.setText( fontSize + "" );
		
		m_clock.setTextSize( fontSize );
	}

}
