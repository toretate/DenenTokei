package com.toretate.denentokei2;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.toretate.denentokei2.dialog.FontFamilyDialog;
import com.toretate.denentokei2.dialog.NumberPickerDialog;

public class FontSetting {
	
    enum FontSettingType {
    	Sta("Sta"), StaSub("StaSub"), Cha("Cha"), ;
    	final @NonNull String typeString;
    	FontSettingType( final @NonNull String typeString ) { this.typeString = typeString; }
    }
    private final @NonNull FontSettingType m_type;
	
	private final WidgetLayoutInfo.WidgetTextLayoutInfo m_textInfo;	// モデル
	
	private final @NonNull Button m_fontFamilyButton;	//!< フォントファミリを変更するボタン
	private final @NonNull Button m_fontSizeButton;		//!< フォントサイズを変更するボタン
	private final @NonNull TextView m_textView;			//!< テキスト内容
	
	private final @NonNull DandDListener m_listener;	//!< D&Dリスナ
	private final @NonNull GestureDetector m_gesture;	//!< 長押し判定
	
	
	public FontSetting( final @NonNull Button fontFamilyButton, final @NonNull Button fontSizeButton, final @NonNull TextView textView, final Activity parent, final @NonNull FontSettingType type, final WidgetLayoutInfo info ) {
		m_type = type;
		m_textInfo = info.getTextLayoutInfo( m_type );
		
		m_fontFamilyButton = fontFamilyButton;
		m_fontSizeButton = fontSizeButton;
		
		m_textView = textView;
		m_textView.addOnLayoutChangeListener( new OnLayoutChangeListener() {
			// D&Dのdropを監視
			@Override
			public void onLayoutChange( View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom ) {
				if( left == oldLeft && top == oldTop ) return;
				if( m_textView != v ) return;
				
				if( m_textInfo.textViewMarginL == left && m_textInfo.textViewMarginT == top ) return;	// 変化無し

				m_textInfo.textViewMarginL = left;
				m_textInfo.textViewMarginT = top;
				m_textInfo.save();
			}
		} );
		
		m_listener = new DandDListener( textView );
		m_gesture = new GestureDetector( parent, m_listener );
		m_textView.setOnTouchListener( new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return m_gesture.onTouchEvent( event );
			}
		});
		
		setupFontFamilyButton( parent );
		setupFontSizeButton( parent );
	}
	
	public void resetView() {
		m_textInfo.resetView( m_textView );
	}
	
	private void setupFontFamilyButton( final Activity activity ) {
		m_fontFamilyButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final FontFamilyDialog dialog = new FontFamilyDialog( m_textInfo.fontName );
				dialog.setFontFamilyChangedListener( new FontFamilyDialog.FontFamilyChangedListener() {
					@Override
					public void fontFamilyChanged(String family) {
						setFontFamily( family != null ? family : WidgetLayoutInfo.DEF_FONT_FAMILY );
					}
				});
				dialog.show( activity.getFragmentManager(), "fontFamily" );
			}
		});
	}

	private void setupFontSizeButton( final Activity activity ) {
		m_fontSizeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick( final View v ) {
				final NumberPickerDialog dialog = new NumberPickerDialog();

				final Bundle bundle = NumberPickerDialog.createBundle( m_textInfo.fontSize, 1, 54, "フォントサイズ" );
				dialog.setArguments( bundle );

				dialog.setNumberChangedListener(new NumberPickerDialog.NumberChangedListener() {
					@Override
					public void numberChanged(int number) {
						setFontSize(number);
					}

					@Override
					public void dialogClosed(int number) {
						setFontSize(number);
					}
				});
				dialog.show( activity.getFragmentManager(), "FontSizePicker" );
			}
		});
	}
	
	private void setFontFamily( final @NonNull String family ) {
		if( m_textInfo.fontName.equals( family ) == false ) {
			m_textInfo.fontName = family;
			m_textInfo.save();
		}
		m_fontFamilyButton.setText( family );
		m_textView.setTypeface( Typeface.create( family, Typeface.NORMAL ) );
	}
	
	private void setFontSize( final int fontSize ) {
		if( m_textInfo.fontSize != fontSize ) {
			m_textInfo.fontSize = fontSize;
			m_textInfo.save();
		}
		m_fontSizeButton.setText( fontSize + "" );
		m_textView.setTextSize( fontSize );
	}

}
