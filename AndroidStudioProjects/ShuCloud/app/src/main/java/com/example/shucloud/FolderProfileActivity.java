package com.example.shucloud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shucloud.Adapter.FolderProfileAdapter;
import com.example.shucloud.Adapter.PhoneStorageAdapter;
import com.example.shucloud.Custom.AutoFitViewModuler;
import com.example.shucloud.Custom.RetrofitCustomCallBack;
import com.example.shucloud.SVO.ContentResolverQueryVO;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.Service.ManagingService;
import com.example.shucloud.Service.RetrofitInterface;
import com.example.shucloud.Service.ServiceConn;
import com.example.shucloud.api.ApiClient;
import com.example.shucloud.databinding.ActivityFolderProfileBinding;
import com.example.shucloud.databinding.ActivityPhoneStorageBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class FolderProfileActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();
    public UserVO USER;
    private static ServiceConn conn;
    private ManagingService managingService;
    private List<ImgViewVO> imgList;
    private ContentResolverQueryVO queryVO;
    private ActivityFolderProfileBinding binding;
    private AutoFitViewModuler recyclerView;
    private FolderProfileAdapter folderProfileAdapter;

    private RetrofitInterface menuRequestRetrofit = ApiClient.getApiService("menuRequestRetrofit",4030);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FolderVO selectedFolder = (FolderVO) getIntent().getSerializableExtra("folderVo");

        conn = ServiceConn.newInstance();
        Intent managingIt = new Intent(this, ManagingService.class);
        bindService(managingIt, conn, Context.BIND_AUTO_CREATE);
        managingService = conn.getManagingService();
        USER = managingService.getUSER();

        imgList = new ArrayList<>();
        queryVO = new ContentResolverQueryVO(
                1
                ,null
                ,null
                ,null
                ,null
        );
        callQuery();

        binding = ActivityFolderProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        recyclerView = binding.imgRv;
        recyclerView.setHasFixedSize(true);

        folderProfileAdapter= new FolderProfileAdapter(
                this
                , recyclerView
                , selectedFolder
                , imgList
                , Glide.with(view.getContext()));

        recyclerView.setAdapter(folderProfileAdapter);
        setContentView(view);
    }


    private void callQuery(){
        Uri collection;
        if(queryVO != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = queryVO.getNewCollection();
            } else {
                collection = queryVO.getOldCollection();
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }
        }


        String[] defultProjection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.SIZE,
        };
        // 조회조건
        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        try (Cursor cursor = getApplicationContext().getContentResolver().query(
                collection,
                queryVO.getProjection(),
                queryVO.getSelection(),
                queryVO.getSelectionArgs(),
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int dateColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

            while (cursor.moveToNext()) {
                // Get values of columns for a given Images.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int date = cursor.getInt(dateColumn);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        queryVO.getOldCollection(), id); //contentUri는 구버전 Uri를 가져온다
                // Stores column values and the contentUri in a local object
                // that represents the media file.
                imgList.add(new ImgViewVO(String.valueOf(contentUri), name, date, size));
            }
            Log.i(TAG, imgList.size() + "");
        }
    }

    public void uploadFolderProfile(MultipartBody.Part multiBody, HashMap<String, RequestBody> body){
        menuRequestRetrofit.uploadFolderProfile(multiBody, body).enqueue(new RetrofitCustomCallBack<String>() {
            @Override
            public void onFinalResponse(Call<String> call, Response<String> response) {
                if ("success".equals(response.body())){
                    Toast.makeText(getApplicationContext(), "프로필이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                    FolderProfileActivity.this.finish();
                }

            }
            @Override
            public void onFinalFailure(Call<String> call, Throwable t) {

            }
        });
    }
}