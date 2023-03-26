package com.example.shucloud.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.shucloud.Custom.RetrofitCustomCallBack;
import com.example.shucloud.MainActivity;
import com.example.shucloud.ManagingView;
import com.example.shucloud.R;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.InstanceVO;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.ViewPagerGallery;
import com.example.shucloud.api.ApiClient;
import com.example.shucloud.api.ContentUriRequestBody;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.BooleanSupplier;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadService extends Service {

    private final String TAG = this.getClass().getName();
    private List<ImgViewVO> uploadList;
    private FolderVO pushFolder = new FolderVO();
    private UserVO USER ;
    private int progress = 0;
    public double percent = 0;

    private NotificationManager notificationManager;
    private Notification notification;

    private static final int UPLOADCNT = 10;
    int fromIndex;
    boolean isComplete;
    private int errCnt = 0;

    private static final RetrofitInterface retrofit = ApiClient.getApiService("bulkRequestRetrofit");

    public UploadService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent == null){
            return START_STICKY;
        }else{
            setInitValue();
            pushFolder = (FolderVO) intent.getSerializableExtra("pushFolder");
            USER = (UserVO) intent.getSerializableExtra("USER");
            try {
                InstanceVO instanceVO = new InstanceVO();
                String instanceUploadList = instanceVO.getInstanceVO(getApplicationContext(),"uploadList",true);
                ImgViewVO[] tempArray = InstanceVO.gson.fromJson(instanceUploadList, ImgViewVO[].class);
                uploadList = Arrays.asList(tempArray);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    "CHANNEL_1_ID",
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is channel 1");

            NotificationChannel channel2 = new NotificationChannel(
                    "CHANNEL_1_ID",
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel uploadChannel =
                    new NotificationChannel("SHUCLOUD_UP", "알림 설정 모드 타이틀", NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(uploadChannel);
        }


        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent =  PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_MUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(getApplicationContext(), "SHUCLOUD_UP")
                    .setContentTitle("ShuCloud")
                    .setContentText("업로드 중이어유~")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setTicker("쓥슈!")
                    .build();
        }
        // Notification ID cannot be 0.
        startForeground(1, notification);
        uploadFileRxVer(USER, pushFolder);

        return super.onStartCommand(intent, flags, startId);
    }
    private void uploadFileSV (UserVO user, FolderVO folder){
        progress = 0;
        callFolderDealState(folder);

        ArrayList<MultipartBody.Part> multiPartFilesList = new ArrayList<>();
        HashMap<String, RequestBody> params = new HashMap<>();
        int totalCnt = uploadList.size();
        params.put("seqFolder",setParam(folder.getSeqFolder()));
        params.put("userId",setParam(USER.getUserId()));
        params.put("totalCnt",setParam(totalCnt));
        params.put("folderName",setParam(folder.getFolderName()));

        for (int i=0;i<totalCnt;i++) {
            int Cnt = i+1;
            ImgViewVO vo = uploadList.get(i);
            String requestName = getFileName(user, folder)+i; // 실제 서버파일 이름
            ContentUriRequestBody contentUriRequestBody = new ContentUriRequestBody(Uri.parse(vo.getUri()),getContentResolver());
            MultipartBody.Part multiPartBody = MultipartBody.Part.createFormData(requestName, contentUriRequestBody.getFileName(), contentUriRequestBody);
            multiPartFilesList.add(multiPartBody);

            /*Descripte
             *업로드는 10개단위로 한다.
             *업로드한 다음에는 list를 비워주고 다음 10개단위를 기다린다.
             * else를 통해 나머지도 올린다.
             */
            if(Cnt == totalCnt){
                //Log.i(getClass().getName(),"업 : code:1");
                callUploadFile(multiPartFilesList, params);
                multiPartFilesList.clear();
            }else if (totalCnt >= 10 && (Cnt) % 10 == 0){
                //Log.i(getClass().getName(),"업 : code:2");
                callUploadFile(multiPartFilesList, params);
                multiPartFilesList.clear();
            }

        }

    }

    public void uploadFileRxVer(UserVO user, FolderVO folder){
        progress = 0;
        callFolderDealState(folder);
        ArrayList<MultipartBody.Part> multiPartFilesList = new ArrayList<>();
        HashMap<String, RequestBody> params = new HashMap<>();
        int totalCnt = uploadList.size();
        params.put("seqFolder",setParam(folder.getSeqFolder()));
        params.put("userId",setParam(USER.getUserId()));
        params.put("folderName",setParam(folder.getFolderName()));
        params.put("folderOwner",setParam(folder.getFolderOwner()));
        params.put("totalCnt",setParam(totalCnt));

        new Thread(new Runnable()
        {
            public void run()
            {
                for (int i=0;i<totalCnt;i++) {
                    ImgViewVO vo = uploadList.get(i);
                    String requestName = getFileName(user, folder)+i; // 실제 서버파일 이름
                    ContentUriRequestBody contentUriRequestBody = new ContentUriRequestBody(Uri.parse(vo.getUri()),getContentResolver());
                    MultipartBody.Part multiPartBody = MultipartBody.Part.createFormData(requestName, contentUriRequestBody.getFileName(), contentUriRequestBody);
                    multiPartFilesList.add(multiPartBody);
                }
                callUploadFile (multiPartFilesList, params);
            }
        }).start();

    }


    private void callFolderDealState(FolderVO vo){

        Call<String> call = retrofit.folderDealState(vo);
        call.enqueue(new RetrofitCustomCallBack<String>() {
            @Override
            public void onFinalResponse(Call<String> call, Response<String> response){
                if (!response.isSuccessful()) {
                    Log.i(getClass().getName(),"code: " + response.code());
                    return;
                }
            }
            @Override
            public void onFinalFailure(Call<String> call, Throwable t) {
                Log.e(getClass().getName(),"code: " + t.getMessage());
            }
        });

    }

    public void callUploadFile(ArrayList list, Map params){
        if(!list.isEmpty()){
            Call<String> call = retrofit.uploadFile(list,params);
            call.enqueue(new RetrofitCustomCallBack<String>() {
                @Override
                public void onFinalResponse(Call<String> call, Response<String> response){
                    if (!response.isSuccessful()) {
                        Log.i(getClass().getName(),"code: " + response.code());
                        return;
                    }
                    String result = response.body();
                    Log.i("progress ::" , result);

                }
                @Override
                public void onFinalFailure(Call<String> call, Throwable t) {

                }
            });
        }
    }
    public void callUploadFile (ArrayList<MultipartBody.Part> multiPartFilesList,  HashMap<String, RequestBody> params){
        int multiPartFilesSize = multiPartFilesList.size();
        int toIndex = 0;
        if (fromIndex + UPLOADCNT >= multiPartFilesSize){
            toIndex = multiPartFilesSize;
            isComplete = true;
        }else{
            toIndex = fromIndex + UPLOADCNT;
        }
        ArrayList<MultipartBody.Part> tempList =
                new ArrayList( multiPartFilesList
                        .subList(fromIndex, toIndex));
        Log.i("my log station :: ", "testcnt  "+fromIndex+ "tempSize  "+multiPartFilesSize);

        retrofit.uploadFile2(tempList, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull String s) {
                        if (!isComplete){
                            fromIndex = fromIndex + UPLOADCNT;
                            callUploadFile(multiPartFilesList,params);
                        }else{
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                stopForeground(STOP_FOREGROUND_DETACH);
                                Intent notificationIntent = new Intent();
                                PendingIntent pendingIntent =  PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
                                notification = new Notification.Builder(getApplicationContext(), "SHUCLOUD_UP")
                                        .setContentText("업로드가 완료 되었습니다.")
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true)
                                        .build();

                                notificationManager.notify(1,notification);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (errCnt <3){
                            Log.i(TAG, "updoadERR :: "+e.getMessage() +"errCnt :: "+ errCnt);
                            callUploadFile(multiPartFilesList,params);
                            errCnt ++;
                        }else {
                            errCnt = 0;
                        }

                    }
                });

    }


    public String getFileName(UserVO user, FolderVO folder){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return user.getUserId()+folder.getSeqFolder()+sdf.format(new Date());
    }

    private RequestBody setParam(String value){
        return RequestBody.create(value, MediaType.parse("text/plain"));
    }
    private RequestBody setParam(int value){
        return RequestBody.create(String.valueOf(value), MediaType.parse("text/plain"));
    }

    public void setInitValue(){
        this.fromIndex = 0;
        this.isComplete = false;
    }

    public void setStopService(){
        if(isComplete)
            stopSelf();
    }

}