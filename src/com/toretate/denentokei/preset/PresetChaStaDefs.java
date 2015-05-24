package com.toretate.denentokei.preset;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * カリスタのプリセット定義
 */
public class PresetChaStaDefs {
    private static final String PREFS_NAME = "com.toretate.denentokei.preset.PresetChaStaDefs";
	
	private static PresetChaStaDefs s_instance;
	public static PresetChaStaDefs getInstance( final @NonNull Context ctx ) {
		if( s_instance == null ) {
			s_instance = new PresetChaStaDefs();
			try {
				load( ctx );
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return s_instance;
	}
	
	private static @Nullable PresetChaSta s_story = null;		//!< ストーリーミッション
	private static @Nullable PresetChaSta s_challenge = null;	//!< チャレンジクエスト
	private static @Nullable PresetChaSta s_daily = null;		//!< 曜日ミッション
	private static @Nullable PresetChaSta s_awaikening = null;	//!< 覚醒ミッション
	private static @Nullable PresetChaSta s_urgency = null;		//!< 緊急ミッション
	private static @Nullable PresetChaSta s_expedition = null;	//!< 大討伐ミッション
	private static @Nullable PresetChaSta s_reprint = null;		//!< 復刻ミッション
	
	private static @Nullable PresetChaSta[] s_presets;
	
	public static @Nullable PresetChaSta[] getPresets( final @NonNull Context ctx ) {
		getInstance( ctx );
		return s_presets;
	}
	
	private static @NonNull List<PresetChaSta> s_list = new ArrayList<PresetChaSta>();
	public static @NonNull List<PresetChaSta> getList() { return s_list; }
	
	public static void load( @NonNull final Context ctx ) throws IOException, JSONException {
		final AssetManager as = ctx.getResources().getAssets();
		final InputStream in = as.open( "preset_cha_sta.json" );
		final int size = in.available();
		final byte[] buffer = new byte[size];
		in.read(buffer);
		in.close();
		
		final String str = new String( buffer );
		final JSONObject json = new JSONObject( str );

		// SharedPreferences からデータを取ってくる
		Set<Integer> selectedIds = new HashSet<Integer>();
		final SharedPreferences prefs = ctx.getSharedPreferences( PREFS_NAME, 0 );
		if( prefs.contains( "presets" ) ) {
			String jsonData = prefs.getString( "presets", null );
			if( jsonData != null ) {
				JSONArray jsonArray = new JSONArray( jsonData );
				for( int i=0; i<jsonArray.length(); i++ ) {
					int id = jsonArray.getInt( i );
					selectedIds.add( id );
				}
			}
		}
		
		s_story = new PresetChaSta( json, "ストーリーミッション", selectedIds );
		s_challenge = new PresetChaSta( json, "チャレンジ", selectedIds );
		s_daily = new PresetChaSta( json, "曜日ミッション", selectedIds );
		s_awaikening = new PresetChaSta( json, "覚醒ミッション", selectedIds );
		s_urgency = new PresetChaSta( json, "緊急ミッション", selectedIds );
		s_expedition = new PresetChaSta( json, "大討伐", selectedIds );
		s_reprint = new PresetChaSta( json, "復刻", selectedIds );
		
		s_presets = new PresetChaSta[]{ s_story, s_challenge, s_daily, s_awaikening, s_urgency, s_expedition, s_reprint };
	}
	
	public static void save( @NonNull final Context ctx, final @NonNull Set<Integer> selectedPresetIds ) {
		JSONArray jsonArray = new JSONArray();
		for( Integer id : selectedPresetIds ) {
			jsonArray.put( id.intValue() );
		}
		String json = jsonArray.toString();
		final SharedPreferences.Editor editor = ctx.getSharedPreferences( PREFS_NAME, 0 ).edit();
		editor.putString( "presets", json );
		editor.commit();
	}
	
}
