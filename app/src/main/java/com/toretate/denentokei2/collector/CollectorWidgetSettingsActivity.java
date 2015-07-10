package com.toretate.denentokei2.collector;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.Bind;

import com.toretate.denentokei.R;

public class CollectorWidgetSettingsActivity extends Activity {

	@Bind( R.id.saveButton )
	Button m_saveButton;

	@Override
	protected void onCreate( @Nullable final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_edit_widget_collector );
		ButterKnife.bind( this );
		
		m_saveButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( @Nullable final View v ) {
				// 保存ボタンの処理

					// 成功を返す
					final Intent result = new Intent( CollectorWidgetSettingsActivity.this, CollectorWidget.class );
					result.setAction( AppWidgetManager.ACTION_APPWIDGET_UPDATE );
//					result.putExtra( AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { m_appWidgetId } );
//					result.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, m_appWidgetId );
					sendBroadcast( result );
					setResult( RESULT_OK, result );
					finish();
			}
		} );
	}

}
