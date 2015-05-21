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
		MENU_SELECT_RUN_IGIS( 0 ),
		MENU_SELECT_RUN_IGIS_R( 1 ), 
		MENU_SELECT_TWITTER( 2 ), 
		MENU_SELECT_TWITTER_HASHTAG( 3 ), 
		MENU_SELECT_NOTIFY( 4 ), 
		MENU_PICK_WIDGET( 5 ),
		UNDEFINED( 1000 ),
		;

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
			case 4:
				return MENU_SELECT_NOTIFY;
			case 5:
				return MENU_PICK_WIDGET;
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
		
		/*
		{	// ウィジェット
			actionItem = menu.add( 0, MenuItemDefs.MENU_PICK_WIDGET.value, 0, "ウィジェット選択" );
			actionItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
		}
		*/
		
		/*if( ctx instanceof ClockWidgetSettingsActivity ) {
			// 通知(通知設定はウィジェット毎に持つ)
			actionItem = menu.add( 0, MenuItemDefs.MENU_SELECT_NOTIFY.value, 0, "通知設定" );
			actionItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
		}
		*/
		
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
		/*
		case MENU_SELECT_NOTIFY:
		{
			intent = new Intent( ctx, SetNotifierActivity.class );
			ctx.startActivity( intent );
			break;
		}
		case MENU_PICK_WIDGET:
		{
			PackageManager pkgMng = ctx.getPackageManager();
			intent = new Intent( AppWidgetManager.ACTION_APPWIDGET_PICK );
			intent.addCategory( Intent.CATEGORY_HOME );
			
			List<ResolveInfo> appList = pkgMng.queryIntentActivities( intent, 0 );
			List<ActivityInfo> activityInfoList = new ArrayList<ActivityInfo>();
			if( appList != null ) {
				for( ResolveInfo info : appList ) {
					activityInfoList.add( info.activityInfo );
				}
			}
			
			ActivityInfo activityInfo = activityInfoList.get( 1 );
			
			AppWidgetHost host = new AppWidgetHost( ctx, 8823 );
			int appWidgetId = host.allocateAppWidgetId();
			
			ArrayList<AppWidgetProviderInfo> infoList = new ArrayList<AppWidgetProviderInfo>();
			ArrayList<Bundle> bundleList = new ArrayList<Bundle>();
			intent = new Intent( AppWidgetManager.ACTION_APPWIDGET_PICK );
//			intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId );
//			intent.putParcelableArrayListExtra( AppWidgetManager.EXTRA_CUSTOM_INFO, infoList );
//			intent.putParcelableArrayListExtra( AppWidgetManager.EXTRA_CUSTOM_EXTRAS, bundleList );
			
			intent.setPackage( activityInfo.packageName );
			intent.setClassName( activityInfo.packageName, activityInfo.name );
			ctx.startActivity( intent );
			
//			if( ctx instanceof Activity ) {
//				Activity activity = (Activity)ctx;
//				activity.startActivityForResult( intent, 8823 );
//			}
			break;
		}
		*/
		case UNDEFINED:
		default:
			break;
		}
		return true;
	}
}
