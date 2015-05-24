package com.toretate.denentokei.preset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.toretate.denentokei.R;

public class PresetChaStaExpandableListAdapter extends BaseExpandableListAdapter {

	private @NonNull List<PresetChaSta> m_presets = new ArrayList<PresetChaSta>();
	private @NonNull Set<Integer> m_selectedPresetIds = new HashSet<Integer>();
	
	private @NonNull Context m_ctx;
	private @Nullable LayoutInflater m_inf;
	
	public PresetChaStaExpandableListAdapter( final @NonNull Activity ctx ) {
		m_ctx = ctx;
		m_inf = ctx.getLayoutInflater();
		
		final PresetChaSta[] presets = PresetChaStaDefs.getPresets( ctx );
		if( presets != null ) {
			final List<PresetChaSta> tmp = Arrays.asList( presets );
			if( tmp != null ) m_presets = tmp;
		}
	}
	

	@Override
	public PresetChaSta getChild(int groupPosition, int childPosition) {
		final PresetChaSta preset = getGroup( groupPosition );
		if( preset != null && childPosition < preset.children.size() ) {
			return preset.children.get( childPosition );
		} else {
			return null;
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		PresetChaSta preset = getChild( groupPosition, childPosition );
		return preset != null ? preset.id : -1;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		PresetChaStaAdapterHolder holder;
		final int position_real = childPosition * PresetChaStaAdapterHolder.s_cols;

		if( convertView != null ) {
			holder = (PresetChaStaAdapterHolder)convertView.getTag();
		} else {
			convertView = m_inf.inflate( R.layout.preset_cha_sta_listitem_toggle, null );
			if( convertView == null ) return new View( m_ctx );
			
			holder = new PresetChaStaAdapterHolder( convertView );
			convertView.setTag( holder );
		}
		
		PresetChaSta group = getGroup( groupPosition );
		if( group == null ) return convertView;
		
		for( int i=0; i<PresetChaStaAdapterHolder.s_cols; i++ ) {
			final PresetChaSta item = getChild( groupPosition, position_real +i );
			
			final ToggleButton button = (ToggleButton)holder.get( i );
			final String text;
			final int visibility;
			if( item != null ) {
				text = item.name_short;
				visibility = View.VISIBLE;
				button.setChecked( item.isSelected );
			} else {
				text = "";
				visibility = View.INVISIBLE;
				button.setChecked( false );
			}
			
			button.setVisibility( visibility );
			button.setText( text );
			button.setTextOff( text );
			button.setTextOn( text );
			button.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick( View v ) {
					final PresetChaSta preset = (PresetChaSta)v.getTag();
					if( preset.isSelected ) {
						m_selectedPresetIds.remove( preset.id );
						preset.isSelected = false;
					} else {
						m_selectedPresetIds.add( preset.id );
						preset.isSelected = true;
					}
					((ToggleButton)v).setChecked( preset.isSelected );
					
					PresetChaStaDefs.save( m_ctx, m_selectedPresetIds );
				}
			} );
			button.setTag( item );
		}
		
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		PresetChaSta preset = getGroup( groupPosition );
		int result = 0;
		if( preset != null ) {
			int size = preset.children.size();
			result = size / PresetChaStaAdapterHolder.s_cols;
			if( size % PresetChaStaAdapterHolder.s_cols != 0 ) {
				result += 1;
			}
		}

		return result;
	}

	@Override
	public PresetChaSta getGroup(int groupPosition) {
		if( groupPosition < m_presets.size() ) {
			return m_presets.get( groupPosition );
		} else {
			return null;
		}
	}

	@Override
	public int getGroupCount() {
		return m_presets.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		PresetChaSta preset = getGroup( groupPosition );
		return preset != null ? groupPosition : -1;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if( convertView == null ) {
			convertView = m_inf.inflate( R.layout.edit_preset_cha_sta_list_group_item, null );
		}
		
		PresetChaSta group = getGroup( groupPosition );
		View text = convertView.findViewById( R.id.groupItemText );
		if( group != null && text instanceof TextView ) {
			((TextView)text).setText( group.name_short );
		}
		
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

}
