package com.toretate.denentokei.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.toretate.denentokei.R;

public class FontSizeDialog extends DialogFragment {
	
	public interface FontSizeChangedListener {
		public void fontSizeChanged( int size );
	}
	
	private FontSizeChangedListener m_listener = null;
	public void setFontSizeChangedListener( FontSizeChangedListener listener ) { m_listener = listener; }
	
	private int m_fontSize;
	
	public FontSizeDialog( int fontSize ) {
		super();
		m_fontSize = fontSize;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog( getActivity(), R.style.transparent );
		dialog.setTitle("フォントサイズ");
		dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
//		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		return dialog;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		LayoutInflater inf = getActivity().getLayoutInflater();
		View view = inf.inflate( R.layout.dialog_numberpicker, null, false );
		
		final NumberPickerDialog picker = new NumberPickerDialog( m_fontSize, 1, 54, "フォントサイズ");
		picker.setNumberChangedListener( new NumberPickerDialog.NumberChangedListener() {
			@Override
			public void numberChanged(int number) {
				if( m_listener != null ) m_listener.fontSizeChanged( number );
			}

			@Override
			public void dialogClosed(int number) {
				if( m_listener != null ) m_listener.fontSizeChanged( number );
			}
		});
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Dialog dialog = getDialog();
		
		// 場所を下に
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.gravity = Gravity.BOTTOM;
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes( params );
	}
	
}
