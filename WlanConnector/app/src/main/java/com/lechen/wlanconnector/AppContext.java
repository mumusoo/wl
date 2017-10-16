package com.lechen.wlanconnector;

import android.app.Application;
import android.app.Dialog;

public class AppContext extends Application {
    public static final String TAG = "AppContext";
    long                       address;
    private static Dialog dialog;//
    private static AppContext mApp;

    public AppContext() {
        address = 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public static Dialog getDialog() {
        return dialog;
    }

    public static void setDialog(Dialog dialog) {
        AppContext.dialog = dialog;
    }

    public static AppContext getmApp() {
        return mApp;
    }

    public static void setmApp(AppContext mApp) {
        AppContext.mApp = mApp;
    }
}
