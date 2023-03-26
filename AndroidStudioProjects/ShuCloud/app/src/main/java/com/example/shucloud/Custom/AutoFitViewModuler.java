package com.example.shucloud.Custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ScaleGestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AutoFitViewModuler extends RecyclerView {

    public ScaleGestureDetector mScaleDetector ;

    private float mScaleFactor = 1.f;
    private GridLayoutManager manager;

    //private int[] winSpec = {0,0};
    private int columnWidth = -1;

    private OnTouchListener onTouchListener;

    public AutoFitViewModuler(@NonNull Context context) {
        super(context);
        onScaleGestureListener(context);
        initialization(context, null);
    }

    public AutoFitViewModuler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onScaleGestureListener(context);
        initialization(context, attrs);
    }

    public AutoFitViewModuler(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onScaleGestureListener(context);
        initialization(context, attrs);
    }

    private void initialization(Context context, AttributeSet attrs) {
        try {
            if (attrs != null) {
                // list the attributes we want to fetch

                int[] attrsArray = {
                        android.R.attr.columnWidth
                };

                @SuppressLint("ResourceType") TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
                //retrieve the value of the 0 index, which is columnWidth
                columnWidth = array.getDimensionPixelSize(0, 150);
                array.recycle();
            }
            manager = new GridLayoutManager(context, 1);
            setLayoutManager(manager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onScaleGestureListener(Context context){
        ScaleGestureDetector.OnScaleGestureListener scaleListener =
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        mScaleFactor *= detector.getScaleFactor();
                        // Don't let the object get too small or too large.
                        mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 6.0f)); // 1.0 <= x <=6.0

                        columnWidth *= detector.getScaleFactor();
                        columnWidth = columnWidth <= 170 ? 170 : columnWidth;
                        columnWidth = columnWidth >= 560 ? 560 : columnWidth;
                        //Log.i("my log Station ::", mScaleDetector.getScaleFactor()+"저장되는 스케일 ::" + mScaleFactor +"columnWidth " + columnWidth);
                        onChangeScale();
                        //invalidate();
                        return true;
                    }
                };
        mScaleDetector = new ScaleGestureDetector(context, scaleListener);
    }

    public void onChangeScale(){
        int spanCount = Math.min(Math.max(1, getMeasuredWidth()/ columnWidth),6);
        manager.setSpanCount(spanCount);
        setLayoutManager(manager);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (onTouchListener != null) {                      // 해당화면에서 터치관련 리스너가 존재하면
            return onTouchListener.onTouch(this, event); // 해당 이벤트 리스너 매서드 를 실행한다. 파라미터를 뷰와 이벤트 두가지를 받는다.
        } else if(mScaleDetector!= null && event.getPointerCount()>1){ //그게아니라 손가락이 두개 얹어져있으면...
            mScaleDetector.onTouchEvent(event); // 스케일 제스터는 터치 모션에 등록되어있지 않다.(event.getActionMasked()) gePointer로 손가락 두개 얹었을때 반응하게 했다.
            return true;
        }else {
            return super.onTouchEvent(event); // view가 가지고 있는 기본적인 터치 매서드를 실행한다.
        }

    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        //winSpec[0] = MeasureSpec.getSize(widthSpec); winSpec[1] =MeasureSpec.getSize(heightSpec);
        try {
            if(columnWidth > 0 ){
                int spanCount = Math.min(Math.max(1, getMeasuredWidth()/ columnWidth),6);
                manager.setSpanCount(spanCount);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
