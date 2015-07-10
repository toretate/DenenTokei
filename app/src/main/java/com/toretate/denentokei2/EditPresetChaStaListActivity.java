package com.toretate.denentokei2;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.toretate.denentokei2.preset.PresetChaStaExpandableListAdapter;

public class EditPresetChaStaListActivity extends Activity {
	
	private BaseExpandableListAdapter m_adapter;	
	private ExpandableListView m_editList;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_edit_preset_cha_sta_list );
		
		ActionBar bar = this.getActionBar();
		bar.setDisplayHomeAsUpEnabled( true );	// 戻るボタン
		
		m_adapter = new PresetChaStaExpandableListAdapter( this );
		
		m_editList = (ExpandableListView)this.findViewById(R.id.editPresetChaStaList);
		m_editList.setAdapter( m_adapter );
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item ) {
		if ( android.R.id.home == item.getItemId() ) {
			this.setResult( Activity.RESULT_OK );
			this.finish();
			return true;
		}
		
		return MenuUtils.onOptionsItemSelected( item, this );
	}
}
