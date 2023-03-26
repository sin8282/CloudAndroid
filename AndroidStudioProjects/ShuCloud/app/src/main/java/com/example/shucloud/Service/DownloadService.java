package com.example.shucloud.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.shucloud.MainActivity;
import com.example.shucloud.R;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.InstanceVO;
import com.example.shucloud.SVO.UserVO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class DownloadService extends Service {
    private final String TAG = this.getClass().getName();
    private static final String BASE_URL_API = "";
    private static String systemLocalPath;
    List<ImgViewVO> downloadList;
    private FolderVO pushFolder = new FolderVO();
    private UserVO USER ;

    private NotificationManager notificationManager;
    private Notification notification;

    public DownloadService() {
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
            try {
                getDownloadDir();
                InstanceVO instanceVO = new InstanceVO();
                String instanceUploadList = instanceVO.getInstanceVO(getApplicationContext(),"downloadList",true);
                ImgViewVO[] tempArray = InstanceVO.gson.fromJson(instanceUploadList, ImgViewVO[].class);
                downloadList = Arrays.asList(tempArray);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel downloadChannel =
                    new NotificationChannel("SHUCLOUD_DOWN", "알림 설정 모드 타이틀", NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(downloadChannel);
        }


        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent =  PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_MUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(getApplicationContext(), "SHUCLOUD_DOWN")
                    .setContentTitle("ShuCloud")
                    .setContentText("다운로드 중이어유~")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setTicker("쓥슈?")
                    .build();
        }

        startForeground(1, notification);
        startDownloadSV();

        return super.onStartCommand(intent, flags, startId);
    }

    public void  getDownloadDir(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            systemLocalPath = Environment.getExternalStorageDirectory()+"/Pictures/ShuCloud";
            File dir = new File(systemLocalPath);
            if(!dir.exists()) dir.mkdir();
        }
    }

     public void startDownloadSV(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "들어옴");
                    URL url;
                    int read;
                    try{
                        HttpURLConnection conn = null;
                        InputStream is = null;
                        FileOutputStream fos = null;


                        for (ImgViewVO vo :downloadList) {
                            File file = new File(systemLocalPath+"/"+vo.getName());
                            url = new URL(BASE_URL_API+vo.getUri());
                            conn = (HttpURLConnection) url.openConnection();
                            byte[] tmpByte = new byte[conn.getContentLength()];
                            is = conn.getInputStream();
                            fos = new FileOutputStream(file);
                            for(;;){
                                read = is.read(tmpByte);
                                if(read <= 0){
                                    break;
                                }
                                fos.write(tmpByte, 0, read);
                            }
                        }
                        is.close();
                        fos.close();
                        conn.disconnect();


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            stopForeground(STOP_FOREGROUND_DETACH);
                            Intent notificationIntent = new Intent();
                            PendingIntent pendingIntent =  PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
                            notification = new Notification.Builder(getApplicationContext(), "SHUCLOUD_DOWN")
                                    .setContentText("다운로드가 완료 되었습니다.")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                                    .build();

                            notificationManager.notify(1,notification);
                        }

                    }catch(Exception e){
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }

                }
            }).start();
        }


     /*public void startDownloadSV(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try{
                    for (ImgViewVO vo :downloadList) {
                        bitmap = BitmapFactory.decodeStream(new URL(BASE_URL_API+vo.getUri()).openStream());
                        File file = new File(systemLocalPath+"/"+vo.getName());
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                        fileOutputStream.close();
                    }
                    bitmap.recycle();
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopForeground(STOP_FOREGROUND_DETACH);
                    Intent notificationIntent = new Intent();
                    PendingIntent pendingIntent =  PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
                    notification = new Notification.Builder(getApplicationContext(), "SHUCLOUD_DOWN")
                            .setContentText("다운로드가 완료 되었습니다.")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();

                    notificationManager.notify(1,notification);
                }
            }
        }).start();
    }*/





}

