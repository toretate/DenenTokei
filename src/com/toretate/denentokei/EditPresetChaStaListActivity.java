package com.toretate.denentokei;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.toretate.denentokei.preset.PresetChaStaExpandableListAdapter;

public class EditPresetChaStaListActivity extends Activity {
	
	private BaseExpandableListAdapter m_adapter;	
	private ExpandableListView m_editList;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_edit_preset_cha_sta_list );
		
		m_adapter = new PresetChaStaExpandableListAdapter( this );
		
		m_editList = (ExpandableListView)this.findViewById(R.id.editPresetChaStaList);
		m_editList.setAdapter( m_adapter );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.edit_preset_cha_sta_list, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if( id == R.id.action_settings ) { return true; }
		return super.onOptionsItemSelected( item );
	}
}
