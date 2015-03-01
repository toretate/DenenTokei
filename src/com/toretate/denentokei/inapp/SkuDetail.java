package com.toretate.denentokei.inapp;

import org.json.JSONException;
import org.json.JSONObject;

public class SkuDetail {
	private String m_productId;
	private String m_type;
	private String m_price;
	private String m_priceAmountMicros;
	private String m_priceCurrencyCode;
	private String m_title;
	private String m_description;
	
	public SkuDetail( JSONObject json ) throws JSONException {
		m_productId = json.getString( "productId" );
		m_type = json.getString( "type" );
		m_price = json.getString( "price" );
		m_priceAmountMicros = json.getString( "price_amount_micros" );
		m_priceCurrencyCode = json.getString( "price_currency_code" );
		m_title = json.getString( "title" );
		m_description = json.getString( "description" );
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put( "productId", m_productId );
		obj.put( "type", m_type );
		obj.put( "price", m_price );
		obj.put( "price_amount_micros", m_priceAmountMicros );
		obj.put( "price_currency_code", m_priceCurrencyCode );
		obj.put( "title", m_title );
		obj.put( "description", m_description );
		return obj;
	}
	
	@Override
	public String toString() {
		try {
			return toJSON().toString();
		} catch( JSONException ex ) {
			return ex.getMessage();
		}
	}
}
