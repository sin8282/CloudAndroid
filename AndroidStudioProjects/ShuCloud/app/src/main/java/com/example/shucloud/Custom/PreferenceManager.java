package com.example.shucloud.Custom;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

//getSharedPreferences("파일이름",'모드')
//모드 => 0 (읽기,쓰기가능)
//모드 => MODE_PRIVATE (이 앱에서만 사용가능)
public class PreferenceManager {
    public static final String PREFERENCE_NAME = "ShuCloudPreference";

    private static final String DEFAULT_VALUE_STRING = "";

    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;


    private static SharedPreferences getInstance(Context context){
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

    }

    public static String getString(Context context, String key){
        SharedPreferences pref = getInstance(context);
        return pref.getString(key, DEFAULT_VALUE_STRING);
    }

    public static void setString(Context context, String key, String value){
        SharedPreferences pref = getInstance(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static Boolean getBoolean(Context context, String key){
        SharedPreferences pref = getInstance(context);
        return pref.getBoolean(key, DEFAULT_VALUE_BOOLEAN);
    }

    public static void setBoolean(Context context, String key, boolean value){
        SharedPreferences pref = getInstance(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static int getInt(Context context, String key){
        SharedPreferences pref = getInstance(context);
        return pref.getInt(key, DEFAULT_VALUE_INT);
    }

    public static void setInt(Context context, String key, int value){
        SharedPreferences pref = getInstance(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }


}
