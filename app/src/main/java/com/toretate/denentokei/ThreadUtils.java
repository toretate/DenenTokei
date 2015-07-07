package com.toretate.denentokei;

import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;

public class ThreadUtils {
	
	public interface Functor<Param, Result> {
		Result apply( Param param );
	}
	
	public final static boolean isUIThread() {
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}
	
	public final static boolean isBackgroundTherad() {
		return !isUIThread();
	}
	
	public static void runOnBackground( final @NonNull Runnable run ) {
		final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				run.run();
				return null;
			}
		};
		task.execute();
	}

}
