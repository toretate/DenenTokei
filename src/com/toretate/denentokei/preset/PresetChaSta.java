package com.toretate.denentokei.preset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.annotation.NonNull;

public class PresetChaSta implements Serializable {
	private static final long serialVersionUID = -8099837994540173489L;
	
	/** @serial */
	public final int id;
	
	/** @serial */
	@NonNull public final String name_long;		//!< 名前
	
	/** @serial */
	@NonNull public final String name_short;	//!< 名前(短縮)
	
	/** @serial */
	public final int cha;						//!< カリスマ
	
	/** @serial */
	public final int sta;						//!< スタミナ
	
	/** @serial */
	public boolean isSelected = false;			//!< 選択状態かどうか
	
	@NonNull public final List<PresetChaSta> children;
	
	public PresetChaSta( final int id, @NonNull final String name, final int cha, final int sta ) {
		this.id = id;
		this.name_long = name;
		this.name_short = name;
		this.cha = cha;
		this.sta = sta;
		this.children = new ArrayList<PresetChaSta>();
	}
	
	public PresetChaSta( final @NonNull JSONArray entry, final @NonNull Set<Integer> selectedIds ) throws JSONException {
		int i = 0;
		
		this.id = entry.getInt(i);
		i++;
		
		String nl = entry.getString(i);
		if( nl == null ) nl = "";
		this.name_long = nl;
		i++;
		
		String ns = entry.getString(i);
		if( ns == null ) ns = "";
		this.name_short = ns;
		i++;
		
		this.cha = entry.getInt(i);
		i++;
		
		this.sta = entry.getInt(i);
		i++;
		
		this.children = new ArrayList<PresetChaSta>();
		
		if( selectedIds.contains( this.id ) ) {
			this.isSelected = true;
		}
	}
	
	public PresetChaSta( final @NonNull JSONObject obj, final @NonNull String name, final @NonNull Set<Integer> selectedIds ) throws JSONException {
		this.id = -1;
		this.name_long = name;
		this.name_short = name;
		this.cha = 0;
		this.sta = 0;
		
		final JSONObject child = obj.optJSONObject( name );
		if( child == null ) throw new JSONException( "child:" +name +" is not found" );
			
		final String type = child.optString( "type" );
		
		final ArrayList<PresetChaSta> children = new ArrayList<PresetChaSta>();
		if( type != null && type.equals( "folder" ) ) {
			@SuppressWarnings( "unchecked" )
			Iterator<String> it = child.keys();
			while( it.hasNext() ) {
				final String key = it.next();
				if( key == null ) continue;
				if( key.equals( "type" ) ) continue;
				
				final JSONArray array = child.getJSONArray( key );
				if( array == null ) continue;
				
				for( int i=0; i<array.length(); i++ ) {
					final JSONArray childArray = array.getJSONArray(i);
					if( childArray != null ) children.add( new PresetChaSta( childArray, selectedIds ) );
				}
			}
		} else if( type != null && type.equals( "array" ) ) {
			final JSONArray array = child.getJSONArray( "array" );
			for( int i=0; i<array.length(); i++ ) {
				final JSONArray childArray = array.getJSONArray(i);
				if( childArray != null ) children.add( new PresetChaSta( childArray, selectedIds ) );
			}
		}
		this.children = children;
		
	}
}
