package com.toretate.denentokei2.dialog;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.toretate.denentokei2.R;

public class FontFamilyDialog extends DialogFragment {
	
	public interface FontFamilyChangedListener {
		public void fontFamilyChanged( String family );
	}
	
	private FontFamilyChangedListener m_listener = null;
	public void setFontFamilyChangedListener( FontFamilyChangedListener listener ) { m_listener = listener; }
	
	private String m_family;
	private ListView m_listView;
	
	public FontFamilyDialog( String family ) {
		super();
		m_family = family;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog( getActivity(), R.style.transparent );
		dialog.setTitle("フォント");
		dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
//		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		return dialog;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		LayoutInflater inf = getActivity().getLayoutInflater();
		View view = inf.inflate( R.layout.dialog_fontpicker, null, false );
		
		m_listView = (ListView)view.findViewById( R.id.fontList1 );
		
		String[] fontList = {
				"sans-serif",
				"sans-serif-light",
				"sans-serif-thin",
				"sans-serif-condensed",
				"serif",
				"Droid Sans",
				"monospace"
		};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(), android.R.layout.simple_expandable_list_item_1, fontList );
		m_listView.setAdapter( adapter );
		m_listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView listView = (ListView)parent;
				m_family = (String)listView.getItemAtPosition( position );
				if( m_listener != null ) m_listener.fontFamilyChanged( m_family );
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
