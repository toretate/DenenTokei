package com.toretate.denentokei;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

public class MenuUtils {
	
	public static final String AIGIS_PKG_NAME = "jp.co.dmmlabo.aigisall";
	public static final String AIGIS_R_PKG_NAME = "jp.co.dmmlabo.aigis";

	enum MenuItemDefs {
		MENU_SELECT_RUN_IGIS( 0 ), MENU_SELECT_RUN_IGIS_R( 1 ), MENU_SELECT_TWITTER( 2 ), MENU_SELECT_TWITTER_HASHTAG( 3 ), UNDEFINED( 1000 ), ;

		int value;

		MenuItemDefs( int value ) {
			this.value = value;
		}

		static MenuItemDefs create( int value ) {
			switch( value ) {
			case 0:
				return MENU_SELECT_RUN_IGIS;
			case 1:
				return MENU_SELECT_RUN_IGIS_R;
			case 2:
				return MENU_SELECT_TWITTER;
			case 3:
				return MENU_SELECT_TWITTER_HASHTAG;
			default:
				return UNDEFINED;
			}
		}
	}

	public static boolean onCreateOptionsMenu( final Menu menu, final Context ctx ) {
		PackageManager pm = ctx.getPackageManager();

		MenuItem actionItem;
		ApplicationInfo info;

		try {
			info = pm.getApplicationInfo( MenuUtils.AIGIS_PKG_NAME, PackageManager.GET_META_DATA );
			if( info != null ) {
				info = null;
				actionItem = menu.add( 0, MenuItemDefs.MENU_SELECT_RUN_IGIS.value, 0, "アイギスを起動" );
				actionItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
			}
		} catch( NameNotFoundException e ) {
			// インストールされていない
		}

		try {
			info = pm.getApplicationInfo( MenuUtils.AIGIS_R_PKG_NAME, PackageManager.GET_META_DATA );
			if( info != null ) {
				info = null;
				actionItem = menu.add( 0, MenuItemDefs.MENU_SELECT_RUN_IGIS_R.value, 0, "アイギス\"R\"を起動" );
				actionItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
			}
		} catch( NameNotFoundException e ) {
			// インストールされていない
		}

		{	// twitter
			actionItem = menu.add( 0, MenuItemDefs.MENU_SELECT_TWITTER.value, 0, "twitter-運営" );
			actionItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
			
			actionItem = menu.add( 0, MenuItemDefs.MENU_SELECT_TWITTER_HASHTAG.value, 0, "twitter-#千年戦争アイギス" );
			actionItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
		}
		
		return true;
	}

	public static boolean onOptionsItemSelected( final MenuItem item, final Context ctx ) {
		MenuItemDefs defs = MenuItemDefs.create( item.getItemId() );
		Intent intent;

		switch( defs ) {
		case MENU_SELECT_RUN_IGIS: {
			PackageManager pm = ctx.getPackageManager();
			intent = pm.getLaunchIntentForPackage( AIGIS_PKG_NAME );
			ctx.startActivity( intent );
			break;
		}
		case MENU_SELECT_RUN_IGIS_R: {
			PackageManager pm = ctx.getPackageManager();
			intent = pm.getLaunchIntentForPackage( AIGIS_R_PKG_NAME );
			ctx.startActivity( intent );
			break;
		}
		case MENU_SELECT_TWITTER:
		{
			Uri uri = Uri.parse( "https://twitter.com/Aigis1000/" );
			intent = new Intent( Intent.ACTION_VIEW, uri );
			ctx.startActivity( intent );
			break;
		}
		case MENU_SELECT_TWITTER_HASHTAG:
		{
			Uri uri = Uri.parse( "https://twitter.com/search?q=%23%E5%8D%83%E5%B9%B4%E6%88%A6%E4%BA%89%E3%82%A2%E3%82%A4%E3%82%AE%E3%82%B9&src=hash" );
			intent = new Intent( Intent.ACTION_VIEW, uri );
			ctx.startActivity( intent );
			break;
		}
		case UNDEFINED:
		default:
			break;
		}
		return true;
	}
}
