package com.example.shucloud;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.shucloud.Adapter.UploadingViewAdapter;
import com.example.shucloud.Custom.AutoFitViewModuler;
import com.example.shucloud.Custom.RetrofitCustomCallBack;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.InstanceVO;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.Service.BackgroundSV;
import com.example.shucloud.Service.ManagingService;
import com.example.shucloud.Service.ServiceConn;
import com.example.shucloud.Service.UploadService;
import com.example.shucloud.Service.RetrofitInterface;
import com.example.shucloud.api.ApiClient;
import com.example.shucloud.databinding.ActivityUploadingBinding;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UploadingActivity extends AppCompatActivity implements BackgroundSV {

    private final String TAG = this.getClass().getName();
    private static UserVO USER;
    private static  ServiceConn conn;
    public ManagingService managingService;
    private ActivityUploadingBinding binding;
    private AutoFitViewModuler recyclerView;
    private UploadingViewAdapter uploadingViewAdapter;

    List<ImgViewVO> uploadList;
    List<FolderVO> userFolder;

    RetrofitInterface retrofit = ApiClient.getApiService("menuRequestRetrofit", 4030);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadingBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        conn = ServiceConn.newInstance();
        Intent managingIt = new Intent(this, ManagingService.class);
        bindService(managingIt, conn, Context.BIND_AUTO_CREATE);
        managingService = conn.getManagingService();


        USER = managingService.getUSER();
        InstanceVO instanceVO = new InstanceVO();
        try {
            String instanceUploadList = instanceVO.getInstanceVO(v.getContext(),"uploadList",false);
            ImgViewVO[] tempArray = InstanceVO.gson.fromJson(instanceUploadList, ImgViewVO[].class);
            uploadList = Arrays.asList(tempArray);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        recyclerView = binding.imgRv;
        recyclerView.setHasFixedSize(true);
        recyclerView.mScaleDetector = null;
        uploadingViewAdapter = new UploadingViewAdapter(
                null
                , recyclerView
                , userFolder
                , uploadList
                , this);
        recyclerView.setAdapter(uploadingViewAdapter);

        getUserFolders(USER);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getUserFolders(UserVO vo){
        Call <ArrayList<FolderVO>> call = retrofit.getUserFolders(vo);
        call.enqueue(new Callback<ArrayList<FolderVO>>() {
            @Override
            public void onResponse(Call<ArrayList<FolderVO>> call, Response<ArrayList<FolderVO>> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "ERROR :: "+response.code());
                    Toast.makeText(getApplicationContext(), "ERROR :: "+response.code() , Toast.LENGTH_SHORT).show();
                    return;
                }
                userFolder =  response.body();
                uploadingViewAdapter.setFolder(userFolder);

            }
            @Override
            public void onFailure(Call<ArrayList<FolderVO>> call, Throwable t) {
            }
        });
    }

    @Override
    public void startUploadSV(FolderVO pushFolder) {
        pushFolder.setDealState("N");
        pushFolder.setProgressPercentage("0");
        callFolderDealState(pushFolder);
    }

    @Override
    public void startDownloadSV() {

    }

    private void callFolderDealState(FolderVO pushFolder){
        Call<String> call = retrofit.folderDealState(pushFolder);
        call.enqueue(new RetrofitCustomCallBack<String>() {
            @Override
            public void onFinalResponse(Call<String> call, Response<String> response){
                if (!response.isSuccessful()) {
                    Log.i(TAG,"code: " + response.code());
                    return;
                }
                if("success".equals(response.body())){
                    Intent bgIt = new Intent(UploadingActivity.this, UploadService.class);
                    bgIt.putExtra("pushFolder",pushFolder);
                    bgIt.putExtra("USER",USER);
                    //startService(it);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(bgIt);
                    }
                    managingService.addUpFolder(pushFolder.getSeqFolder());
                    setResult(RESULT_OK);
                    finish();
                }
            }
            @Override
            public void onFinalFailure(Call<String> call, Throwable t) {
                Log.e(TAG,"code: " + t.getMessage());
            }
        });

    }
    //서비스 작동 중 인지 확인하기
   /* private boolean isServiceRunning(String serviceName){
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = i
                    .next();

            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                serviceRunning = true;

                if(runningServiceInfo.foreground)
                {
                    //service run in foreground
                }
            }
        }
        return serviceRunning;
    }*/

}

