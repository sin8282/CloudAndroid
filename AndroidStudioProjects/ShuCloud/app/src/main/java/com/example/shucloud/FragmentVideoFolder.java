package com.example.shucloud;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.shucloud.Adapter.VideoFolderAdapter;
import com.example.shucloud.Custom.AutoFitViewModuler;
import com.example.shucloud.SVO.StroageBucketVO;
import com.example.shucloud.databinding.FragmentVideoFolderBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentVideoFolder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentVideoFolder extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ViewPagerFolder viewPagerFolder;
    private FragmentVideoFolderBinding binding;
    private AutoFitViewModuler recyclerView;
    private VideoFolderAdapter videoFolderAdapter;

    public FragmentVideoFolder() {
        // Required empty public constructor
    }

    public static FragmentVideoFolder newInstance() {
        FragmentVideoFolder fragment = new FragmentVideoFolder();
/*        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.viewPagerFolder = (ViewPagerFolder) getActivity();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVideoFolderBinding.inflate(inflater);
        View view = binding.getRoot();

        List<StroageBucketVO> bucketList = new ArrayList<>();
        List<String> checkBucketName = new ArrayList<>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        };
        // 조회조건은 따로 없는 전체이므로 사용하지 않는다.
        /*String selection = MediaStore.Video.Media.DURATION +
                " >= ?";
        String[] selectionArgs = new String[] {
                String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));
        };*/
        String sortOrder = MediaStore.Video.Media.DATE_TAKEN + " DESC";

        try (Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                collection,
                projection,
                null,
                null,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()) {
                // Get values of columns for a given Video.
                long id = cursor.getLong(idColumn);

                String buckName = cursor.getString(bucketNameColumn);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                if (!checkBucketName.contains(buckName)){
                    checkBucketName.add(buckName);
                    StroageBucketVO vo = new StroageBucketVO();
                    vo.setCount(getCountFolder(collection,buckName));
                    vo.setBucketName(buckName);
                    vo.setThumbnailUri(String.valueOf(contentUri));
                    bucketList.add(vo);
                }
            }
            if(!(cursor == null || cursor.moveToFirst() == false)){
                StroageBucketVO vo = new StroageBucketVO();
                vo.setCount(cursor.getCount());
                vo.setBucketName("전체영상");
                vo.setThumbnailUri(bucketList.get(0).getThumbnailUri());
                bucketList.add(0,vo);
            }

            Log.i("폴더 ::", bucketList.toString());
        }

        recyclerView = binding.imgRv;
        recyclerView.setHasFixedSize(true);
        recyclerView.mScaleDetector = null;
        videoFolderAdapter = new VideoFolderAdapter(null, recyclerView, bucketList, Glide.with(view.getContext()));
        recyclerView.setAdapter(videoFolderAdapter);

        return view;
    }

    private int getCountFolder(Uri collection, String buckName) {
        try(Cursor cursor = getActivity().getContentResolver().query(
                collection
                ,null
                ,MediaStore.Video.Media.BUCKET_DISPLAY_NAME + "= ?"
                , new String[] {buckName}
                , null)){
            return  ((cursor == null) || (cursor.moveToFirst() == false)) ? 0 : cursor.getCount();
        }
    }
}