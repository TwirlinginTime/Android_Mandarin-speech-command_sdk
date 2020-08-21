package com.twirling.sdk;

import android.util.Log;


public class wakeup{
    public static String TAG = "WAKEUP";

    static {
        try{
            System.loadLibrary("TwirlingWakeupNN");
        }catch(Exception e)
        {
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
        }
        Log.d(TAG,"LoadLibrary ok");
    }

    public long WakeupInit(int stride_len, String keyword_file, String model_file,
                           String appid, String app_passwd)
    {
        Log.d(TAG,"wakeup init");
        return wakeupInit(stride_len,keyword_file,model_file,appid,app_passwd);
    }

    public int WakeupProcess(long obj,float[] data)
    {
        Log.d(TAG,"wakeup process");
        return wakeupProcess(obj,data);
    }

    public void WakeupRelease(long obj)
    {
        Log.d(TAG,"wakeup release");
        wakeupRelease(obj);
    }

    private static native long wakeupInit(int stride_len,String keyword_file,
                                          String model_file, String appid,
                                          String app_passwd);
    private static native int wakeupProcess(long obj, float[] data);
    private static native void wakeupRelease(long obj);
}