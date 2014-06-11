package com.xylon.tipjar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RadioButton;

public class SharedPreferencesUtils {

	public static void SavePreferences(Context context, String key, String value) {
		//String key = context.getResources().getResourceName(resid);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"MY_SHARED_PREF", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	// getResources().getResourceName(int resid);
	/**
	 * 
	 * Fetch the value corresponding to the key from SharedPreferences
	 * and set the text of the RadioButton rd
	 * @param context
	 * @param key input the key to fetch the value
	 * @param rd  Radiobutton which will be set with the value corresponding to the key
	 */
	public static void LoadRadioPreferences(final Context context, final String key,
			final RadioButton rd) {
		//String resName = context.getResources().getResourceName(resId);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"MY_SHARED_PREF", Context.MODE_PRIVATE);
		String radioTextName = sharedPreferences.getString(key, "");
		if ( radioTextName != null && !radioTextName.equals(""))
			rd.setText(radioTextName +"%");
	}
	
	
	public static String LoadRadioPreferences(final Context context, final String key ) {
		//String resName = context.getResources().getResourceName(resId);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"MY_SHARED_PREF", Context.MODE_PRIVATE);
		String value = sharedPreferences.getString(key, "");
		return value;
	}
}
