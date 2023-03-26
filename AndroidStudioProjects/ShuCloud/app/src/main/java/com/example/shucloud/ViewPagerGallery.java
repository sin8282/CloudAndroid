package com.example.shucloud;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.shucloud.Adapter.DriveStorageAdapter;
import com.example.shucloud.Adapter.ManagingViewAdapter;
import com.example.shucloud.Adapter.PhoneStorageAdapter;
import com.example.shucloud.Adapter.ViewPagerAdapter;
import com.example.shucloud.Custom.LayoutAnimater;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.Service.BackgroundSV;
import com.example.shucloud.Service.DownloadService;
import com.example.shucloud.Service.ManagingService;
import com.example.shucloud.Service.ServiceConn;
import com.example.shucloud.Service.UploadService;
import com.example.shucloud.databinding.ViewpagerGalleyBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ViewPagerGallery extends AppCompatActivity implements BackgroundSV {

    private final String TAG = this.getClass().getName();
    private static UserVO USER;
    private static  ServiceConn conn;
    public ManagingService managingService;

    public ViewpagerGalleyBinding binding;
    public View viewPagerRoot;
    private FrameLayout mainFrame;
    public BottomNavigationView mainNavi;
    private String[] tabTitle = {"사진","공유","관리"};
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction ;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume"+"my log station ::");


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate"+"my log station ::");

        conn = ServiceConn.newInstance();
        Intent managingIt = new Intent(this, ManagingService.class);
        bindService(managingIt, conn, Context.BIND_AUTO_CREATE);
        managingService = conn.getManagingService();
        USER = managingService.getUSER();

        setFragment();
        setFrag(1);
        LayoutInflater inflater = getLayoutInflater();
        binding = ViewpagerGalleyBinding.inflate(inflater);
        viewPagerRoot = binding.getRoot();
        setContentView(viewPagerRoot);

        mainNavi = binding.mainNavi;
        mainNavi.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mainNavi_1:
                        setFrag(1);
                        break;
                    case R.id.mainNavi_2:
                        setFrag(2);
                        break;
                    case R.id.mainNavi_3:
                        setFrag(3);
                        break;
                }
                return true;
            }
        });

        // call SharedPreferences ------------------------------------------------------
        Intent intent = getIntent();
        if(intent.getExtras() != null){
            int mediaType = intent.getIntExtra("mediaType",0); // 미디어 타입선택
            String selection = intent.getStringExtra("selection");
            String[] selectionArgs = intent.getStringArrayExtra("selectionArgs");
            PhoneStorageView.newInstance().selectBucketList(mediaType, selection, selectionArgs, 0);

        }

        // setListener ------------------------------------------------------------------

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"onDestroy"+"my log station ::");
        super.onDestroy();
        managingService.setLongClick(false);
        if(conn!= null) unbindService(conn); // 서비스 종료
    }

    private void  setFragment(){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainFrame1,PhoneStorageView.newInstance(),"phoneStorageView");
        fragmentTransaction.add(R.id.mainFrame2,DriveStorageView.newInstance(),"driveStorageView");
        fragmentTransaction.add(R.id.mainFrame3,ManagingView.newInstance(),"managingView");
        fragmentTransaction.commitNow();
    }
    private void setFrag(int n) {
        fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fm :fragmentManager.getFragments()){
            fragmentTransaction.hide (fm);
        }
        switch (n) {
            case 1:
                fragmentTransaction.show(fragmentManager.findFragmentByTag("phoneStorageView"));
                fragmentTransaction.commit();
                break;
            case 2:
                fragmentTransaction.show(fragmentManager.findFragmentByTag("driveStorageView"));
                fragmentTransaction.commit();
                break;
            case 3:
                fragmentTransaction.show(fragmentManager.findFragmentByTag("managingView"));
                fragmentTransaction.commit();
                break;
        }
    }

    public void myVibrate(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(1000);
        }

    }

    public void setonLongClick(RecyclerView.Adapter applyAdapter, Boolean setLongClick){
        Animation SLIDE_UP;
        Animation SLIDE_DOWN;
        SLIDE_UP = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slideup);
        SLIDE_DOWN = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slidedown);
        Log.i("my log station ::" ,managingService.isLongClick()+"");

        if (setLongClick == null )managingService.setLongClick();
        else managingService.setLongClick(setLongClick);

        if (managingService.isLongClick()) {
            SLIDE_DOWN.setAnimationListener(new LayoutAnimater(binding.topSubNavi,true));
            SLIDE_UP.setAnimationListener(new LayoutAnimater(binding.bottomSubNavi,true));
            binding.bottomSubNavi.startAnimation(SLIDE_UP);
            binding.topSubNavi.setVisibility(View.VISIBLE);
            //binding.topMainNavi.setVisibility(View.GONE);
            if (applyAdapter instanceof PhoneStorageAdapter){
                ((PhoneStorageAdapter) applyAdapter).initListener();
            }else if (applyAdapter instanceof DriveStorageAdapter) {
                binding.driveDeleteBtn.setVisibility(View.VISIBLE);
                ((DriveStorageAdapter) applyAdapter).initListener();
            }else if (applyAdapter instanceof ManagingViewAdapter) {
                binding.driveDeleteBtn.setVisibility(View.VISIBLE);
                ((ManagingViewAdapter) applyAdapter).initListener();
            }

        }else {
            SLIDE_DOWN.setAnimationListener(new LayoutAnimater(binding.bottomSubNavi,false));
            binding.bottomSubNavi.startAnimation(SLIDE_DOWN);
            //binding.topMainNavi.setVisibility(View.VISIBLE);
            binding.topSubNavi.setVisibility(View.GONE);
            binding.allItemCb.setChecked(false);
            binding.uploadCntTv.setText("0");
            if (applyAdapter instanceof PhoneStorageAdapter){
                ((PhoneStorageAdapter) applyAdapter).uploadList.clear();
                for(ImgViewVO vo: ((PhoneStorageAdapter) applyAdapter).imgList){
                    vo.setChecked(false);
                }
            }else if (applyAdapter instanceof DriveStorageAdapter) {
                binding.driveDeleteBtn.setVisibility(View.GONE);
                ((DriveStorageAdapter) applyAdapter).setLoaded();
                ((DriveStorageAdapter) applyAdapter).getSelectedList().clear();
                for (ImgViewVO vo : ((DriveStorageAdapter) applyAdapter).imgList) {
                    vo.setChecked(false);
                }
            }else if (applyAdapter instanceof ManagingViewAdapter) {
                binding.driveDeleteBtn.setVisibility(View.GONE);
                ((ManagingViewAdapter) applyAdapter).getSelectedFolder().clear();
                for (FolderVO vo : ((ManagingViewAdapter) applyAdapter).userFolder) {
                    vo.setChecked(false);
                }
            }
        }
        applyAdapter.notifyDataSetChanged();
    }


    public void doUploading (){
        Intent intent = new Intent(ViewPagerGallery.this, UploadingActivity.class);
        onUploadingResult.launch(intent);
    }

    private void  setMenuIcon(boolean isOk){
        FrameLayout fl = binding.menuFl;
        Log.i(this.getClass().getName(), String.valueOf(isOk));
        if(isOk){
            fl.setVisibility(View.VISIBLE);
        }else{
            fl.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){

            int mediaType = data.getIntExtra("mediaType",0);
            String selection = data.getStringExtra("selection");
            String[] selectionArgs = data.getStringArrayExtra("selectionArgs");
            String bucketName = selectionArgs[0];

            if(!(bucketName.equals("전체사진") || bucketName.equals("전체영상"))){
                PhoneStorageView.newInstance().selectBucketList(mediaType, selection, selectionArgs, resultCode);
            }else{
                if(bucketName.equals("전체사진"))
                    PhoneStorageView.newInstance().selectBucketList(1, null, null, resultCode);
                else
                    PhoneStorageView.newInstance().selectBucketList(2, null, null, resultCode);
            }

            if(mediaType == 1){
                tabTitle = new String[]{"사진", "공유", "관리"};
            }else{
                tabTitle = new String[]{"동영상", "공유", "관리"};
            }

            Toast.makeText(this, bucketName+"로 이동했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private final ActivityResultLauncher<Intent> onUploadingResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // 서브화면에서 넘어오는 결과값을 확인
                if(result.getResultCode() == RESULT_OK){
                    Toast.makeText(this, "업로드를 시작했습니다.", Toast.LENGTH_LONG).show();
                    setonLongClick(PhoneStorageView.newInstance().phoneStorageAdapter, false);
                    mainNavi.setSelectedItemId(R.id.mainNavi_3);
                }
            }
    );


    @Override
    public void startUploadSV(FolderVO pushFolder) {

    }

    @Override
    public void startDownloadSV() {
        Intent bgIt = new Intent(ViewPagerGallery.this, DownloadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(bgIt);
        }
        //managingService.addUpFolder(pushFolder.getSeqFolder());
        setResult(RESULT_OK);
    }
}