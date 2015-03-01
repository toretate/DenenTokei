package com.toretate.denentokei.inapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
import com.toretate.denentokei.ThreadUtils;

public class InAppPurchaseManager {

	/** シングルトンインスタンス */
	private static @Nullable InAppPurchaseManager s_instance;

	private static int s_inAppPurchaseVersion = 3;
	
	/** InAppPurchaseサービス */
	private @Nullable IInAppBillingService m_Service;  
	
	
	/** InAppPurcaseサービスコネクタ */
	final @NonNull ServiceConnection m_ServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected( final ComponentName name ) {
			m_Service = null;
		}

		@Override
		public void onServiceConnected( final ComponentName name, final IBinder service ) {
			m_Service = IInAppBillingService.Stub.asInterface( service );
		}
	};
	
	/** シングルトンのためprivateにしておく */
	private InAppPurchaseManager() {}

	/**
	 * シングルトンインスタンスを取得
	 * @return
	 */
	public static @NonNull InAppPurchaseManager getInstance() {
		InAppPurchaseManager result = s_instance;
		if( result == null ) {
			result = new InAppPurchaseManager();
			s_instance = result;
		}
		return result;
	}

	/**
	 * InAppPurchaseサービスをバインドする
	 * @param context
	 */
	public void bind( @NonNull final Context context )
	{
		// In App Purchase を bind
		context.bindService(
				new Intent("com.android.vending.billing.InAppBillingService.BIND")
				, m_ServiceConn
				, Context.BIND_AUTO_CREATE
				);
	}
	
	/**
	 * バインドしているInAppPurchaseサービスを解除
	 * @param context
	 */
	public void destroy( @NonNull final Context context )
	{
		if( m_ServiceConn != null ) context.unbindService( m_ServiceConn );
	}

	/**
	 * 商品一覧を取得
	 * @return 商品一覧
	 * @throws Exception InAppBilingServiceがnull
	 * @throws Exception UIThreadから呼び出した場合
	 */
	public @Nullable List<SkuDetail> getItemList( @NonNull final Context context ) throws Exception
	{
		final IInAppBillingService service = m_Service;
		if( service == null ) {
			throw new Exception( "IInAppBillingService is null" );
		}
		
		// getSkuDetailsがバックグラウンドスレッドを要求するのでメソッド全体をバックグランド保障する
		if( ThreadUtils.isUIThread() ) {
			throw new Exception( "getItemList must call on BackgroundThread" );
		}
		
		final ArrayList<String> skuList = new ArrayList<String>();
		skuList.add( "giveSinseiKessyou" );
		
		final Bundle querySkus = new Bundle();
		querySkus.putStringArrayList( "ITEM_ID_LIST", skuList );

		// Googleへ問い合わせ
	    try {
	    	final String purchaseType = "inapp";
			final Bundle skuDetails = service.getSkuDetails(
													s_inAppPurchaseVersion
													, context.getPackageName()
													, purchaseType
													, querySkus);
			
			final int code = skuDetails.getInt("RESPONSE_CODE");
			switch( ResponseCode.create( code ) ) {
			case ResultOk:
				// 商品リストを取得する
				ArrayList<String> response_list = skuDetails.getStringArrayList("DETAILS_LIST");
				ArrayList<SkuDetail> result = new ArrayList<SkuDetail>();

				for( String row : response_list ) {
					// JSONオブジェクトへ変換する
					final JSONObject object = new JSONObject( row );
					final SkuDetail sku = new SkuDetail( object );
					Log.v("DenenTokei", sku.toString());
					result.add( sku );
				}
				return result;
				
			case ResultError:
			default:
				break;
			}
		} catch ( final RemoteException e ) {
			return null;
		}  
	    
	    return null;
	}
}
