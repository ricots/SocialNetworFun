package com.sample.socialnetworkfun;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class AppConfig extends Application {


    private static AppConfig appInstance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor sharedPreferencesEditor;
    private static Context mContext;
   /* private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;*/

    @Override
    public void onCreate()
    {
        super.onCreate();
        appInstance = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferencesEditor = sharedPreferences.edit();
        setContext(getApplicationContext());
        //MultiDex.install(this);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mctx) {
        mContext = mctx;
    }

    public static AppConfig getAppInstance() {
        if (appInstance == null)
            throw new IllegalStateException("The application is not created yet!");
        return appInstance;
    }

   /* public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }*/

    /**
     * Application level preference work.
     */
    public static void preferencePutInteger(String key, int value) {
        sharedPreferencesEditor.putInt(key, value);
        sharedPreferencesEditor.commit();
    }

    public static int preferenceGetInteger(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void preferencePutBoolean(String key, boolean value) {
        sharedPreferencesEditor.putBoolean(key, value);
        sharedPreferencesEditor.commit();
    }

    public static boolean preferenceGetBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void preferencePutString(String key, String value) {
        sharedPreferencesEditor.putString(key, value);
        sharedPreferencesEditor.commit();
    }

    public static String preferenceGetString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void preferencePutLong(String key, long value) {
        sharedPreferencesEditor.putLong(key, value);
        sharedPreferencesEditor.commit();
    }

    public static long preferenceGetLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static void preferenceRemoveKey(String key) {
        sharedPreferencesEditor.remove(key);
        sharedPreferencesEditor.commit();
    }

    public static void clearPreference() {
        sharedPreferencesEditor.clear();
        sharedPreferencesEditor.commit();
    }
}


