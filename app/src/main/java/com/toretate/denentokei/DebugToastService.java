package com.toretate.denentokei;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class DebugToastService extends Service {
	
	public static String s_toastMsg;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String msg = (String)intent.getCharSequenceExtra( s_toastMsg );
		
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		
		stopSelf();
		
		return super.onStartCommand(intent, flags, startId);
	}

}
