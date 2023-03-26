package com.example.shucloud.Service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ServiceConn implements ServiceConnection {

    private static ManagingService managingService;
    boolean isService = false; // 서비스 중인 확인용

    public static ServiceConn newInstance() {
        return new ServiceConn();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // 서비스와 연결되었을 때 호출되는 메서드
        // 서비스 객체를 전역변수로 저장
        ManagingService.LocalBinder mb = (ManagingService.LocalBinder) service;
        managingService = mb.getService(); // 서비스가 제공하는 메소드 호출하여
        // 서비스쪽 객체를 전달받을수 있슴
        isService = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // 서비스와 연결이 끊겼을 때 호출되는 메서드

        isService = false;
    }

    public ManagingService getManagingService() {
        return managingService;
    }
}
