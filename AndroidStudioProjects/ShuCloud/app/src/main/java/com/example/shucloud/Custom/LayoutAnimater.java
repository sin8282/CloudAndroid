package com.example.shucloud.Custom;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

import com.example.shucloud.R;

public class LayoutAnimater implements Animation.AnimationListener {

    boolean isOpen = false;
    private View view;

    public LayoutAnimater(View v, boolean isOpen){
        this.isOpen = isOpen;
        this.view = v;
    }
    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(isOpen){
            view.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.GONE);
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
