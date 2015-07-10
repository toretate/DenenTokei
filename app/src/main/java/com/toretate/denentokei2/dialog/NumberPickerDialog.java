package com.toretate.denentokei2.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import butterknife.ButterKnife;
import butterknife.Bind;

import com.toretate.denentokei.R;

public class NumberPickerDialog extends DialogFragment {
	
	public interface NumberChangedListener {
		public void numberChanged( final int number );	//!< 数値が変更された時に呼ばれます
		public void dialogClosed( final int number );	//!< ダイアログが閉じられたときに呼ばれます
	}

	@Nullable
	private NumberChangedListener m_listener = null;
	public void setNumberChangedListener( final NumberChangedListener listener ) { m_listener = listener; }
	
	private int m_initNumber;
	private int m_min;
	private int m_max;
	
	@NonNull
	private String m_title;
	
	@Bind(R.id.numberPicker1) NumberPicker m_numberPicker1;
	@Bind(R.id.numberPicker10) NumberPicker m_numberPicker10;
	@Bind(R.id.numberPicker100) NumberPicker m_numberPicker100;
	
	public NumberPickerDialog( final int initNumber, final int min, final int max, final @NonNull String title ) {
		super();
		m_initNumber = initNumber;
		m_min = min;
		m_max = max;
		m_title = title;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog( getActivity(), R.style.transparent );
		dialog.setTitle( m_title );
		dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
//		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		
		return dialog;
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		
		final int num = getNumber();
		// イベント発火
		final NumberChangedListener l = m_listener;
		if( l != null ) { l.dialogClosed( num ); }
	}
	
	private int getNumber() {
		int num = m_numberPicker1.getValue();
		num += m_numberPicker10.getValue() * 10;
		num += m_numberPicker100.getValue() * 100;
		return num;
	}
	
	private void numberChanged( NumberPicker picker, int newVal ) {
		int num = getNumber();
		
		int num1 = num % 10;
		int num10 = ( num / 10 ) % 10;
		int num100 = ( num / 100 ) % 10;
		
		if( picker == m_numberPicker1 ) {
			num = num100 * 100 + num10 * 10 + newVal * 1;
		} else if( picker == m_numberPicker10 ) {
			num = num100 * 100 + newVal * 10 + num1 * 1;
		} else if( picker == m_numberPicker100) {
			num = newVal * 100 + num10 * 10 + num1 * 1;
		}
		
		// 補正
		num = Math.max( num, m_min );
		num = Math.min( num, m_max );
		
		// イベント発火
		final NumberChangedListener l = m_listener;
		if( l != null ) l.numberChanged( num );
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		LayoutInflater inf = getActivity().getLayoutInflater();
		View view = inf.inflate( R.layout.dialog_numberpicker, null, false );
		ButterKnife.bind( this, view );

		if( 0 < m_max ) {
			m_numberPicker1.setVisibility( View.VISIBLE );
			m_numberPicker1.setMinValue( 0 );
			m_numberPicker1.setDescendantFocusability( NumberPicker.FOCUS_BLOCK_DESCENDANTS );
			if( m_max < 10 ) {
				m_numberPicker1.setMaxValue( m_max );
			} else {
				m_numberPicker1.setMaxValue( 9 );
			}
			m_numberPicker1.setValue( ( m_initNumber / 1 ) % 10 );
			m_numberPicker1.setOnValueChangedListener( new OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					numberChanged( picker, newVal );
				}
			});
		}
		if( 10 <= m_max ) {
			m_numberPicker10.setVisibility( View.VISIBLE );
			m_numberPicker10.setMinValue( 0 );
			m_numberPicker10.setDescendantFocusability( NumberPicker.FOCUS_BLOCK_DESCENDANTS );
			if( m_max < 100 ) {
				m_numberPicker10.setMaxValue( ( m_max / 10 ) % 10 );
			} else {
				m_numberPicker10.setMaxValue( 9 );
			}
			m_numberPicker10.setValue( ( m_initNumber / 10 ) % 10 );
			m_numberPicker10.setOnValueChangedListener( new OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					numberChanged( picker, newVal );
				}
			});
		}
		if( 100 <= m_max ) {
			m_numberPicker100.setVisibility( View.VISIBLE );
			m_numberPicker100.setMinValue( 0 );
			m_numberPicker100.setDescendantFocusability( NumberPicker.FOCUS_BLOCK_DESCENDANTS );
			if( m_max < 1000 ) {
				m_numberPicker100.setMaxValue( ( m_max / 100 ) % 100 );
			} else {
				m_numberPicker100.setMaxValue( 9 );
			}
			m_numberPicker100.setValue( ( m_initNumber / 100 ) % 10 );
			m_numberPicker100.setOnValueChangedListener( new OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					numberChanged( picker, newVal );
				}
			});
		}
		
		
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
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
