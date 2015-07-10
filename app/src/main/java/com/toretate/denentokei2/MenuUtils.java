package com.toretate.denentokei2;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

public class MenuUtils {

	@NonNull public static final String PKG_NAME_AIGIS_A = "jp.co.dmmlabo.aigisall";
	@NonNull public static final String PKG_NAME_AIGIS_R = "jp.co.dmmlabo.aigis";
	@NonNull public static final String TWITTER_AIGIS_A = "https://twitter.com/Aigis1000/";
	@NonNull public static final String TWITTER_AIGIS_R = "https://twitter.com/search?q=%23%E5%8D%83%E5%B9%B4%E6%88%A6%E4%BA%89%E3%82%A2%E3%82%A4%E3%82%AE%E3%82%B9&src=hash";
	@NonNull public static final String TWITTER_TORETATE = "https://twitter.com/toretate/";
	@NonNull public static final String WIKI_GCWIKI = "http://aigis.gcwiki.info/";
	@NonNull public static final String WIKI_SEESAA = "http://seesaawiki.jp/aigis/";

	enum MenuItemDefs {
		MENU_SELECT_RUN_AIGIS_A(0, false),			//!< アイギス起動
		MENU_SELECT_RUN_AIGIS_R(1, false),			//!< アイギスR起動
		MENU_SELECT_TWITTER(2, false),				//!< @aigis1000(twitter)
		MENU_SELECT_TWITTER_HASHTAG(3, false),		//!< #千年戦争アイギス(twitter)
		MENU_SELECT_PRESET(4, false),				//!< カリスタプリセット
		MENU_SELECT_NOTIFY(5, true),					//!< Alarm
		MENU_TEST_CRASHLYTICS(6, true),				//!< Crashriticsのテスト
		MENU_SELECT_TWITTER_TORETATE(7, false),	//!< @toretate(twitter)
		MENU_SELECT_WIKI_1(8),						//!< アイギスWiki(gcwiki)
		MENU_SELECT_WIKI_2(9),						//!< アイギスWiki(seesaa)
		MENU_SELECT_AIGIS_PUBLIC(10),				//!< DMMのアイギスフォーラムのお知らせページ
		UNDEFINED(1000, false),;

		int value;
		boolean isDebugModelMenu;

		MenuItemDefs(final int value) {
			this(value, false);
		}

		MenuItemDefs(final int value, final boolean isDebugModeMenu) {
			this.value = value;
			this.isDebugModelMenu = isDebugModeMenu;
		}

		static MenuItemDefs create(final int value) {
			for (MenuItemDefs def : MenuItemDefs.values()) {
				if (value == def.value) return def;
			}
			return UNDEFINED;
		}
	}

	public static boolean onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final Context ctx) {
		return onCreateOptionsMenu(menu, ctx, AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	public static boolean onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final Context ctx, final int appWidgetId) {
		final PackageManager pm = ctx.getPackageManager();

		MenuItem actionItem;
		ApplicationInfo info;

		boolean existAigisA = false;
		boolean existAigisR = false;

		try {
			existAigisA = pm.getApplicationInfo(MenuUtils.PKG_NAME_AIGIS_A, PackageManager.GET_META_DATA) != null;
			if ( existAigisA ) {
				actionItem = menu.add(0, MenuItemDefs.MENU_SELECT_RUN_AIGIS_A.value, 0, "アイギスを起動");
				actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			}
		} catch (NameNotFoundException e) {
			// インストールされていない
		}

		try {
			existAigisR = pm.getApplicationInfo(MenuUtils.PKG_NAME_AIGIS_R, PackageManager.GET_META_DATA) != null;
			if ( existAigisR ) {
				actionItem = menu.add(0, MenuItemDefs.MENU_SELECT_RUN_AIGIS_R.value, 0, "アイギス\"R\"を起動");
				actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			}
		} catch (NameNotFoundException e) {
			// インストールされていない
		}

		boolean existAigis = existAigisA || existAigisR;
		if( existAigis ) {    // twitter
			actionItem = menu.add(0, MenuItemDefs.MENU_SELECT_TWITTER.value, 0, "twitter-運営");
			actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

			actionItem = menu.add(0, MenuItemDefs.MENU_SELECT_TWITTER_HASHTAG.value, 0, "twitter-#千年戦争アイギス");
			actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		}

		{
			// PresetChasta
			actionItem = menu.add(0, MenuItemDefs.MENU_SELECT_PRESET.value, 0, "カリスタ一覧設定");
			actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		}
		
		if( BuildConfig.DEBUG) {
			actionItem = menu.add( 0, MenuItemDefs.MENU_SELECT_NOTIFY.value, 0, "通知設定" );
			actionItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
		}

		if( BuildConfig.DEBUG ) {
			// CrashLytics
			actionItem = menu.add( 0, MenuItemDefs.MENU_TEST_CRASHLYTICS.value, 0, "＊Test ClashLytics" );
			actionItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
		}

		{
			actionItem = menu.add(0, MenuItemDefs.MENU_SELECT_WIKI_1.value, 0, "Wiki(gcwiki)");
			actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

			actionItem = menu.add(0, MenuItemDefs.MENU_SELECT_WIKI_2.value, 0, "Wiki(seesaa)");
			actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

			actionItem = menu.add(0, MenuItemDefs.MENU_SELECT_TWITTER_TORETATE.value, 0, "twitter-作者(@toretate)");
			actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		}

		return true;
	}

	public static boolean onOptionsItemSelected(final MenuItem item, final Context ctx) {
		final MenuItemDefs defs = MenuItemDefs.create(item.getItemId());
		Intent intent;

		switch (defs) {
		case MENU_SELECT_RUN_AIGIS_A: {
			final PackageManager pm = ctx.getPackageManager();
			intent = pm.getLaunchIntentForPackage(PKG_NAME_AIGIS_A);
			ctx.startActivity(intent);
			break;
		}
		case MENU_SELECT_RUN_AIGIS_R: {
			final PackageManager pm = ctx.getPackageManager();
			intent = pm.getLaunchIntentForPackage(PKG_NAME_AIGIS_R);
			ctx.startActivity(intent);
			break;
		}
		case MENU_SELECT_TWITTER: {
			final Uri uri = Uri.parse(TWITTER_AIGIS_A);
			intent = new Intent(Intent.ACTION_VIEW, uri);
			ctx.startActivity(intent);
			break;
		}
		case MENU_SELECT_TWITTER_HASHTAG: {
			final Uri uri = Uri.parse(TWITTER_AIGIS_R);
			intent = new Intent(Intent.ACTION_VIEW, uri);
			ctx.startActivity(intent);
			break;
		}
		case MENU_SELECT_PRESET: {
			intent = new Intent(ctx, EditPresetChaStaListActivity.class);
			ctx.startActivity(intent);
			break;
		}
		case MENU_SELECT_NOTIFY: {
//			intent = new Intent(ctx, SetNotifierActivity.class);
//			ctx.startActivity(intent);
			break;
		}
		case MENU_TEST_CRASHLYTICS:
			Crashlytics.getInstance().crash();
			break;

		case MENU_SELECT_WIKI_1: {
			final Uri uri = Uri.parse(WIKI_GCWIKI);
			intent = new Intent(Intent.ACTION_VIEW, uri);
			ctx.startActivity(intent);
			break;
		}

		case MENU_SELECT_WIKI_2: {
			final Uri uri = Uri.parse(WIKI_SEESAA);
			intent = new Intent(Intent.ACTION_VIEW, uri);
			ctx.startActivity(intent);
			break;
		}

		case MENU_SELECT_TWITTER_TORETATE: {
			final Uri uri = Uri.parse(TWITTER_TORETATE);
			intent = new Intent(Intent.ACTION_VIEW, uri);
			ctx.startActivity(intent);
			break;
		}

		case UNDEFINED:
		default:
			break;
		}
		return true;
	}
}
