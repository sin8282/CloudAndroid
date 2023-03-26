package com.example.shucloud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.shucloud.Adapter.PhoneStorageAdapter;
import com.example.shucloud.SVO.ContentResolverQueryVO;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.databinding.ActivityPhoneStorageBinding;

import java.util.ArrayList;
import java.util.List;

public class PhoneStorageView extends Fragment {
    private final String TAG = this.getClass().getName();
    private UserVO USER ;
    public static ViewPagerGallery viewPagerGallery;
    private RecyclerView recyclerView;
    public ActivityPhoneStorageBinding binding;
    public static PhoneStorageAdapter phoneStorageAdapter;

    private List<ImgViewVO> imgList;
    private static ContentResolverQueryVO queryVO;


    public static PhoneStorageView newInstance(){
        PhoneStorageView phoneStorageView = new PhoneStorageView();
        return phoneStorageView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        this.viewPagerGallery = (ViewPagerGallery) getActivity();
        imgList = new ArrayList<ImgViewVO>();
        queryVO = new ContentResolverQueryVO (
                1
                ,null
                ,null
                ,null
                ,null
        );
        callQuery();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (queryVO != null && queryVO.getResultCode() == -1){
            Log.i(TAG, "Resume");
            queryVO.setResultCode(0);
            callQuery();
            phoneStorageAdapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = getLayoutInflater();
        binding = ActivityPhoneStorageBinding.inflate(inflater);
        View view = binding.getRoot();

        recyclerView = binding.imgRv;
        recyclerView.setHasFixedSize(true);

        phoneStorageAdapter= new PhoneStorageAdapter(
                 viewPagerGallery
                , this
                , recyclerView
                , imgList
                , Glide.with(view.getContext()));
        recyclerView.setAdapter(phoneStorageAdapter);
        return view;

    }

    private void callQuery(){
        imgList.clear();
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

        try (Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
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

    public void selectBucketList (int mediaType, String selection, String[] selectionArgs, int resultCode){
        queryVO = new ContentResolverQueryVO(
                mediaType
                ,null
                , selectionArgs
                , selection
                , null
        );
        queryVO.setResultCode(resultCode);
    }

}