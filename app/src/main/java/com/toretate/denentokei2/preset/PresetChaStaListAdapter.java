package com.toretate.denentokei2.preset;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.toretate.denentokei2.R;

/**
 * PresetChaStaをグリッドで見せるためのアダプタ(とはいうもののListViewで見せるんだが)
 * GridLayoutだと weight 指定できないっぽいし TableLayout だと動的に追加が面倒だし Listでがむばる
 */
public class PresetChaStaListAdapter extends BaseAdapter {

	/** ボタンクリック時のイベント拾うためのリスナ */
	public interface PresetChaStaSelectedListener {
		/** ボタンクリック時に呼ばれます */
		public void onClick( @Nullable final PresetChaSta preset );
	}
	
	private @NonNull Context m_ctx;
	private @NonNull List<PresetChaSta> m_list;

	private @Nullable PresetChaStaSelectedListener m_outerListener = null;
	public void setButtonHandler( @Nullable final PresetChaStaSelectedListener l ) { m_outerListener = l; }
	
	private @NonNull View.OnClickListener m_innerListener = new View.OnClickListener() {
		@Override
		public void onClick( @Nullable final View v ) {
			if( v == null ) return;
			final PresetChaStaSelectedListener l = m_outerListener;
			if( l == null ) return;
			l.onClick( getItem( (Integer)v.getTag() ) );
		}
	};
	
	public PresetChaStaListAdapter( @NonNull final Context ctx, @NonNull final List<PresetChaSta> list ) {
		this.m_ctx = ctx;
		this.m_list = list;
	}

	@Override
	public int getCount() {
		return ( m_list.size() / PresetChaStaAdapterHolder.s_cols ) +1;	// 「＋」の追加が必用なので、丁度でも +1 している
	}

	@Override
	public @Nullable PresetChaSta getItem( final int index ) {
		if( index < m_list.size() ) {
			return m_list.get( index );
		}
		return null;
	}

	@Override
	public long getItemId( final int index ) {
		if( index < m_list.size() ) {
			final PresetChaSta preset = m_list.get( index );
			return preset.id;
		}
		return -1;
	}

	@Override
	public @NonNull View getView( final int position, @Nullable View convertView, @Nullable final ViewGroup parent ) {
		PresetChaStaAdapterHolder holder;
		final int position_real = position * PresetChaStaAdapterHolder.s_cols;

		if( convertView != null ) {
			holder = (PresetChaStaAdapterHolder)convertView.getTag();
		} else {
			convertView = View.inflate( m_ctx, R.layout.preset_cha_sta_listitem, null );
			if( convertView == null ) return new View( m_ctx );
			
			holder = new PresetChaStaAdapterHolder( convertView );
			convertView.setTag( holder );
		}

		boolean showAddButton = false;
		for( int i=0; i<PresetChaStaAdapterHolder.s_cols; i++ ) {
			final PresetChaSta item = getItem( position_real +i );
			
			final Button button = holder.get( i );
			final String text;
			final int visibility;
			if( item != null ) {
				final Spanned spanned = Html.fromHtml( String.format( Locale.getDefault(), "<big>%s</big><br /><small>Sta:%2d Cha:%3d</small>", item.name_short, item.sta, item.cha ) );
				visibility = View.VISIBLE;
				if( button != null ) {
					button.setText( spanned );
				}
			} else {
				if( showAddButton ) {
					text = "";
					visibility = View.INVISIBLE;
				} else {
					text = "＋";
					visibility = View.VISIBLE;
				}
				showAddButton = true;
				
				if( button != null ) {
					button.setText( text );
				}
			}
			
			if( button != null ) {
				button.setVisibility( visibility );
				button.setOnClickListener( m_innerListener );
				button.setTag( position_real +i );
			}
		}
		
		return convertView;
	}
	
	public void replace( final @NonNull List<PresetChaSta> list ) {
		m_list.clear();
		for( PresetChaSta preset : list ) {
			m_list.add( preset );
		}
		this.notifyDataSetChanged();
	}
}
