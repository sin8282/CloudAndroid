package com.example.shucloud.Service;

import android.view.View;

import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.UserVO;

public interface BackgroundSV {
    void startUploadSV(FolderVO pushFolder);
    void startDownloadSV();
}
