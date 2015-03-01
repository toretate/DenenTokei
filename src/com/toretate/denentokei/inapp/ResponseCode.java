package com.toretate.denentokei.inapp;

public enum ResponseCode {
	ResultOk(0),					//!< 成功
	ResultUserCancelled(1),			//!< ダイアログキャンセルか戻るボタン
	ResultServiceUnavailable(2),	//!< ネットワークDown
	ResultBillingUnavaiable(3),		//!< 課金APIのバージョンサポート外
	ResultItemUnavailable(4),		//!< リクエストした商品が購入できない
	ResultDeveloperError(5),		//!< 引数が無効、もしくは署名が不正、課金設定がGooglePlayで不備、AndroidManifestに権限の記述無し。
	ResultError(6),					//!< 処理中に致命的なエラーが発生
	ResultItemAlreadyOwned(7),		//!< 所有していない商品に対して消費行動を行おうとして失敗
	;
	
	private int m_code;
	public int getCode() { return m_code; }
	
	ResponseCode( final int code ) {
		m_code = code;
	}
	
	static ResponseCode create( final int code ) {
		switch( code ) {
		case 0: 		return ResultOk;
		case 1: 		return ResultUserCancelled;
		case 2: 		return ResultServiceUnavailable;
		case 3: 		return ResultBillingUnavaiable;
		case 4: 		return ResultItemUnavailable;
		case 5: 		return ResultDeveloperError;
		case 6: 		return ResultError;
		case 7: 		return ResultItemAlreadyOwned;
		default:	return ResultError;
		}
	}
}
