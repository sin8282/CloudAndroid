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
import com.example.shucloud.Adapter.ImageFolderAdapter;
import com.example.shucloud.Custom.AutoFitViewModuler;
import com.example.shucloud.SVO.StroageBucketVO;
import com.example.shucloud.databinding.FragmentImageFolderBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentImageFolder extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ViewPagerFolder viewPagerFolder;
    private FragmentImageFolderBinding binding;
    private AutoFitViewModuler recyclerView;
    private ImageFolderAdapter imageFolderAdapter;


    public FragmentImageFolder() {}

    public static FragmentImageFolder newInstance() {
        FragmentImageFolder fragment = new FragmentImageFolder();
/*        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewPagerFolder = (ViewPagerFolder) getActivity();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImageFolderBinding.inflate(inflater);
        View view = binding.getRoot();

        List<StroageBucketVO> bucketList = new ArrayList<>();
        List<String> checkBucketName = new ArrayList<>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        };
        // 조회조건은 따로 없는 전체이므로 사용하지 않는다.
        /*String selection = MediaStore.Images.Media.DURATION +
                " >= ?";
        String[] selectionArgs = new String[] {
                String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));
        };*/
        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        try (Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                collection,
                projection,
                null,
                null,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()) {
                // Get values of columns for a given Images.
                long id = cursor.getLong(idColumn);

                String buckName = cursor.getString(bucketNameColumn);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

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
                vo.setBucketName("전체사진");
                vo.setThumbnailUri(bucketList.get(0).getThumbnailUri());
                bucketList.add(0,vo);
            }

            Log.i("폴더 ::", bucketList.toString());
        }

        recyclerView = binding.imgRv;
        recyclerView.setHasFixedSize(true);
        recyclerView.mScaleDetector = null;
        imageFolderAdapter = new ImageFolderAdapter(null, recyclerView, bucketList, Glide.with(view.getContext()));
        recyclerView.setAdapter(imageFolderAdapter);

        return view;
    }

    private int getCountFolder(Uri collection, String buckName) {
        try(Cursor cursor = getActivity().getContentResolver().query(
                collection
                ,null
                ,MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "= ?"
                , new String[] {buckName}
                , null)){
            return  ((cursor == null) || (cursor.moveToFirst() == false)) ? 0 : cursor.getCount();
        }
    }
}