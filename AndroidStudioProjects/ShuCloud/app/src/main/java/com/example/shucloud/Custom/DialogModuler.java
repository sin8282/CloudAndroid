package com.example.shucloud.Custom;

import android.app.Dialog;
import android.content.Context;

public class DialogModuler extends Dialog {

    private DialogModuler(Context context){
        super(context);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }
}
