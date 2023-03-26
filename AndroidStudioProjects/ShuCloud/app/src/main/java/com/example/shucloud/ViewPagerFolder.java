package com.example.shucloud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.shucloud.Adapter.ViewPagerFolderAdapter;
import com.example.shucloud.Custom.AutoFitViewModuler;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.Service.ManagingService;
import com.example.shucloud.Service.ServiceConn;
import com.example.shucloud.databinding.ViewpagerFolderBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ViewPagerFolder extends AppCompatActivity {

    private final String TAG = this.getClass().getName();
    private static UserVO USER;
    private static ServiceConn conn;
    public ManagingService managingService;
    private ViewpagerFolderBinding binding;
    private AutoFitViewModuler recyclerView;
    private ViewPager2 folderViewPager;
    private ViewPagerFolderAdapter viewPagerFolderAdapter;
    private String[] tabTitle={"사진", "비디오"};
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TEST
        conn = ServiceConn.newInstance();
        Intent managingIt = new Intent(this, ManagingService.class);
        bindService(managingIt, conn, Context.BIND_AUTO_CREATE);
        managingService = conn.getManagingService();
        USER = managingService.getUSER();
        //TEST--

        binding = ViewpagerFolderBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        folderViewPager = binding.viewPager;
        TabLayout tabLayout = binding.tabLayout;

        // call SharedPreferences ------------------------------------------------------
        Intent intent = getIntent();
        if(intent.getExtras() != null){
            String bucketName = intent.getExtras().getString("bucketName"); // 초기 미디어 타입 선택
            if (!bucketName.isEmpty()){
                String[] selectionArgs = new String[] {bucketName};

            }
        }


        // setView ------------------------------------------------------------------
        viewPagerFolderAdapter = new ViewPagerFolderAdapter(view, fragmentManager, getLifecycle());
        folderViewPager.setAdapter(viewPagerFolderAdapter);
        new TabLayoutMediator(tabLayout, folderViewPager,
                (tab, position) -> tab.setText(tabTitle[position])
        ).attach();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}