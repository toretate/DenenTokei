package com.toretate.denentokei.preset;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * カリスタのプリセット定義
 */
public class PresetChaStaDefs {
	
	public static PresetChaSta s_story;
	public static PresetChaSta s_challenge;
	public static PresetChaSta s_mission;
	
	private static List<PresetChaSta> s_list = new ArrayList<PresetChaSta>();
	public static List<PresetChaSta> getList() { return s_list; }
	
	public static void load( final Context ctx ) throws IOException, JSONException {
		AssetManager as = ctx.getResources().getAssets();
		InputStream in = as.open( "preset_cha_sta.json" );
		int size = in.available();
		byte[] buffer = new byte[size];
		in.read(buffer);
		in.close();
		
		String str = new String( buffer );
		JSONObject json = new JSONObject( str );

		s_story = new PresetChaSta( json, "story" );
		s_challenge = new PresetChaSta( json, "challenge" );
		s_mission = new PresetChaSta( json, "mission" );
	}
	
}
