package com.ratodigital.simplesms;

import android.content.Context;
import android.content.SharedPreferences;

public class SimpleSMSLogger {

	
	public static void log(Context context, String text){
		SharedPreferences localData = context.getSharedPreferences(RegisterActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = localData.edit();
		editor.putString(RegisterActivity.PREFS_LOG, localData.getString(RegisterActivity.PREFS_LOG, "")+"\n"+text);
		editor.commit();
	}
	
	
}
