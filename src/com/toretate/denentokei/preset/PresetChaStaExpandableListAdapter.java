package com.toretate.denentokei.preset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.toretate.denentokei.R;

public class PresetChaStaExpandableListAdapter extends BaseExpandableListAdapter {

	private @NonNull List<PresetChaSta> m_presets = new ArrayList<PresetChaSta>();
	private @NonNull Set<Integer> m_selectedPresetIds = new HashSet<Integer>();
	
	private @NonNull Context m_ctx;
	
	public PresetChaStaExpandableListAdapter( final @NonNull Activity ctx ) {
		m_ctx = ctx;
		
		final PresetChaSta[] presets = PresetChaStaDefs.getPresets( ctx );
		if( presets != null ) {
			final List<PresetChaSta> tmp = Arrays.asList( presets );
			if( tmp != null ) {
				m_presets = tmp;
				
				// m_selectedPresetIdsを初期更新
				for( int groupPosition=0; groupPosition<this.getGroupCount(); groupPosition++ ) {
					final PresetChaSta preset = getGroup( groupPosition );
					for( int childPosition = 0; childPosition < preset.children.size(); childPosition++ ) {
						final PresetChaSta child = preset.children.get( childPosition );
						if( child != null && child.isSelected && child.isActive ) {
							m_selectedPresetIds.add( child.id );
						}
					}
				}
			}
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
	public View getChildView( final int groupPosition, final int childPosition, final boolean isLastChild, @Nullable View convertView, final @Nullable ViewGroup parent) {
		PresetChaStaAdapterHolder holder;
		final int position_real = childPosition * PresetChaStaAdapterHolder.s_cols;

		if( convertView != null ) {
			holder = (PresetChaStaAdapterHolder)convertView.getTag();
		} else {
			convertView = View.inflate( m_ctx, R.layout.preset_cha_sta_listitem_toggle, null );
			if( convertView == null ) return new View( m_ctx );
			
			holder = new PresetChaStaAdapterHolder( convertView );
			convertView.setTag( holder );
		}
		
		final PresetChaSta group = getGroup( groupPosition );
		if( group == null ) return convertView;
		
		for( int i=0; i<PresetChaStaAdapterHolder.s_cols; i++ ) {
			final PresetChaSta item = getChild( groupPosition, position_real +i );
			
			final CheckBox button = (CheckBox)holder.get( i );
			final String text;
			final int visibility;
			if( item != null ) {
				text = item.name_short;
				visibility = View.VISIBLE;
				final Spanned spanned = Html.fromHtml( String.format( Locale.getDefault(), "<big>%s</big><br /><small>Sta:%2d Cha:%3d</small>", item.name_short, item.sta, item.cha ) );
				if( button != null ) {
					button.setChecked( item.isSelected );
					button.setText( spanned );
				}
			} else {
				text = "";
				visibility = View.GONE;
				if( button != null ) {
					button.setChecked( false );
					button.setText( text );
				}
			}
			
			if( button != null ) {
				button.setVisibility( visibility );
				button.setOnClickListener( new OnClickListener() {
					@Override
					public void onClick( final @Nullable View v ) {
						if( v == null
							|| ( v instanceof CheckBox ) == false
							|| ( v.getTag() instanceof PresetChaSta == false )
							) return;
						
						final PresetChaSta preset = (PresetChaSta)(v.getTag());
						if( preset.isSelected ) {
							m_selectedPresetIds.remove( preset.id );
							preset.isSelected = false;
						} else {
							m_selectedPresetIds.add( preset.id );
							preset.isSelected = true;
						}
						((CheckBox)v).setChecked( preset.isSelected );
						
						PresetChaStaDefs.save( m_ctx, m_selectedPresetIds );
					}
				} );
				button.setTag( item );
			}
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
			convertView = View.inflate( m_ctx, R.layout.edit_preset_cha_sta_list_group_item, null );
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
