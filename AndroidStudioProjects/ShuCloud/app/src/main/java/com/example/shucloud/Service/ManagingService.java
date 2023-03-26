package com.example.shucloud.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.shucloud.Adapter.ManagingViewAdapter;
import com.example.shucloud.MainActivity;
import com.example.shucloud.ManagingView;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.ViewPagerGallery;
import com.example.shucloud.api.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagingService extends Service {
    private final String TAG = this.getClass().getName();
    private UserVO USER;
    private List upFolder;
    private List downFolder;
    private boolean isLongClick;


    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public ManagingService getService() {
            return ManagingService.this;
        }
    }
    public ManagingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        upFolder = new ArrayList();
        downFolder = new ArrayList();
        isLongClick = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (USER == null){
            USER = (UserVO) intent.getSerializableExtra("USER");
        }
        if (upFolder == null){
            USER = (UserVO) intent.getSerializableExtra("USER");
        }
        Log.d(TAG, USER.getUserEmail());
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public UserVO getUSER() {
        return USER;
    }

    public List getUpFolder() {
        return upFolder;
    }
    public void addUpFolder(Object oj) {
        this.upFolder.add(oj);
    }

    public List getDownFolder() {
        return downFolder;
    }

    public boolean isLongClick() {
        return isLongClick;
    }

    public void setLongClick(Boolean value) {
        this.isLongClick = value;
    }
    public boolean setLongClick() {
        return isLongClick = isLongClick == true ? false : true;
    }


    public void initialAllValue(){}
}