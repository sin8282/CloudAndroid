package com.example.shucloud;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.shucloud.Adapter.DriveStorageAdapter;
import com.example.shucloud.Adapter.UserListAdapter;
import com.example.shucloud.Custom.DrawerToggleModuler;
import com.example.shucloud.Custom.PreferenceManager;
import com.example.shucloud.Custom.RetrofitCustomCallBack;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.Service.RetrofitInterface;
import com.example.shucloud.api.ApiClient;
import com.example.shucloud.databinding.ActivityDriveStorageBinding;
import com.example.shucloud.databinding.DrawerFoldermenunaviBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriveStorageView extends Fragment {

    private final String TAG = this.getClass().getName();
    private static UserVO USER ;
    public static final int LOAD_MORE_CNT = 200;
    public static ViewPagerGallery viewPagerGallery;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    public ActivityDriveStorageBinding driveBinding;
    public DrawerFoldermenunaviBinding menuNaviBinding;
    private static DriveStorageAdapter driveStorageAdapter;
    private UserListAdapter userListAdapter;
    private RequestManager glide;

    private Disposable reloadDisposable;

    public static FolderVO selectedFolder;
    private List<ImgViewVO> imgList;
    private List<ImgViewVO> newList;
    private static int pageNum;

    protected Handler handler;
    private boolean isComplete;
    private boolean isCanLoadMore;
    private boolean isUpdate[];
    private Toast mToast = null;

    private static final RetrofitInterface menuRequestRetrofit = ApiClient.getApiService("menuRequestRetrofit",4030);


    public static DriveStorageView newInstance(){
        DriveStorageView driveStorageView = new DriveStorageView();
        return driveStorageView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate"+"my log station ::");
        this.viewPagerGallery = (ViewPagerGallery) getActivity();
        this.imgList = new ArrayList<>();
        this.pageNum = 1;
        getInitFolder();
        USER = viewPagerGallery.managingService.getUSER();
        mToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        isUpdate = new boolean[2]; //[0] 유무, [1] 방장껀지 개인껀지
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            if(reloadDisposable != null && !reloadDisposable.isDisposed()) return;
            if(viewPagerGallery.managingService.isLongClick() == false){
                Log.i(TAG, "show");
                isCanLoadMore = true;
                isComplete = false;
                driveStorageAdapter.setLoaded();
                reloadDisposable = getObservable(selectedFolder);
            }
        }else{
            Log.i(TAG, "hidden");
            isComplete = true;
            if(reloadDisposable == null) return;
            reloadDisposable.dispose();
            savePreferenceManager();
            if(drawerLayout!= null && drawerLayout.isDrawerOpen(Gravity.RIGHT)){
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        if (viewPagerGallery.managingService.isLongClick() == false) {
            Log.i(TAG, "Resume"+"my log station ::");
            isCanLoadMore = true;
            isComplete = false;
            driveStorageAdapter.setLoaded();
            reloadDisposable = getObservable(selectedFolder);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Pause"+"my log station ::");

        isComplete = true;
        if (reloadDisposable== null) return;
        reloadDisposable.dispose();
        savePreferenceManager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        inflater = getLayoutInflater();
        driveBinding = ActivityDriveStorageBinding.inflate(inflater);
        menuNaviBinding = driveBinding.folderMenuNaviView;
        View view = driveBinding.drawerLayout;
        drawerLayout = driveBinding.drawerLayout;
        recyclerView = driveBinding.imgRv;
        recyclerView.setHasFixedSize(true);
        glide = Glide.with(view.getContext());

        driveStorageAdapter = new DriveStorageAdapter(
                viewPagerGallery
                , this
                , recyclerView
                , imgList
                , glide);
        recyclerView.setAdapter(driveStorageAdapter);

        // setListener ------------------------------------------------------------------
        handler = new Handler();
        viewPagerGallery.binding.driveDeleteBtn.setOnClickListener(v -> {
            showDeleteFilesDialog();
        });
        driveBinding.folderMenuNaviBtn.setOnClickListener(v -> {
            if(selectedFolder != null){
                getFolderMenuNaviInfo(selectedFolder);
            }else{
                mToast.setText("선택하신 자료가 없습니다.");
                mToast.show();
            }
        });
        driveStorageAdapter.setOnLoadMoreListener(new DriveStorageAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (isCanLoadMore){
                    if(viewPagerGallery.managingService.isLongClick()){
                        mToast.setText("다운로드 창을 띄워놓은 상태에서는 로딩이 불가능 합니다.");
                        mToast.show();
                        return;
                    }
                    imgList.add(null);
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            driveStorageAdapter.notifyItemInserted(imgList.size() - 1);
                        }
                    });

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imgList.remove(imgList.size() - 1);
                            driveStorageAdapter.notifyItemRemoved(imgList.size());
                            getMoreFiles(selectedFolder);

                        }
                    }, 2000);
                }
            }
        });
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {}
            @Override
            public void onDrawerStateChanged(int newState) {}
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if(isUpdate[0]){
                    menuRequestRetrofit.setupFolder(selectedFolder,isUpdate[1]).enqueue(new RetrofitCustomCallBack<String>() {
                        @Override
                        public void onFinalResponse(Call<String> call, Response<String> response) {
                            Log.i(TAG, "code :: " +response.code());
                            if(isUpdate[1]){
                                getFiles(selectedFolder);
                            }
                            isUpdate[0] =false; isUpdate[1] =false;
                        }
                        @Override
                        public void onFinalFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
            }
        });

        return view;
    }

    public void showDownLoadDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_download);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPagerGallery.startDownloadSV();
                mToast.setText( "다운로드를 진행합니다.");
                mToast.show();
                viewPagerGallery.setonLongClick(driveStorageAdapter, false);
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDeleteFolderDialog(List<FolderVO> selectedFolder){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_deletefiles);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView msg1 = dialog.findViewById(R.id.textMessage1);
        msg1.setText("해당 유저를 삭제 하시겠습니까?");
        TextView msg2 = dialog.findViewById(R.id.textMessage2);
        msg2.setText("* 유저가 남긴 자료는 폴더에 남아있습니다.");
        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFolder(selectedFolder);
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void deleteFolder(List<FolderVO> selectedFolder){
        Call<String> call = menuRequestRetrofit.deleteFolder(selectedFolder, false);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "ERROR :: "+response.code());
                    Toast.makeText(getContext(), "ERROR :: "+response.code() , Toast.LENGTH_SHORT).show();
                    return;
                }
                Call getUsers = menuRequestRetrofit.getFolderUsers(selectedFolder.get(0));
                getUsers.enqueue(new RetrofitCustomCallBack() {
                    @Override
                    public void onFinalResponse(Call call, Response response) {
                        List<UserVO> result = (List<UserVO>) response.body();
                        userListAdapter.setUserListRefresh(result);
                        if (result.toString().indexOf(USER.toString()) == -1 ){
                            viewPagerGallery.mainNavi.setSelectedItemId(R.id.mainNavi_3);
                        }
                    }
                    @Override
                    public void onFinalFailure(Call call, Throwable t) {

                    }
                });
                Toast.makeText(getContext(), response.body() , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void showAddUserDialog(List<FolderVO> selectedFolder){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_adduser);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);

        EditText userNameEt = dialog.findViewById(R.id.userNameEt);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = String.valueOf(userNameEt.getText());
                additionalUserFolders(selectedFolder, userEmail);
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void additionalUserFolders(List<FolderVO> selectedFolder, String userEmail){
        Call<String> call = menuRequestRetrofit.additionalUserFolders(selectedFolder, userEmail);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "ERROR :: "+response.code());
                    Toast.makeText(getContext(), response.code() , Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!"success".equals(response.body())){
                    Toast.makeText(getContext(), "ERROR :: "+response.body() , Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private void setupFolder(FolderVO selectedFolder){
        // 방장영역
        menuNaviBinding.writeAuthRg.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == menuNaviBinding.ownerRb.getId()){
                selectedFolder.setFolderCanWrite(1);
            }else{
                selectedFolder.setFolderCanWrite(2);
            }
            isUpdate[0] = true;
            isUpdate[1] = false;
        });
        menuNaviBinding.deleteAuthRg.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == menuNaviBinding.psnRb.getId()){
                selectedFolder.setFolderCanDelete(1);
            }else{
                selectedFolder.setFolderCanDelete(2);
            }
            isUpdate[0] = true;
            isUpdate[1] = false;
        });
        // 개인영역
        menuNaviBinding.sortMineCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                selectedFolder.setFolderSortMine("Y");
            }else{
                selectedFolder.setFolderSortMine("N");
            }
            isUpdate[0] = true;
            isUpdate[1] = true;
        });

    }
    public void getFolderMenuNaviInfo(FolderVO selectedFolder) {
        Call userCheck = menuRequestRetrofit.userCheckAsId(selectedFolder.getFolderOwner());
        userCheck.enqueue(new RetrofitCustomCallBack() {
            @Override
            public void onFinalResponse(Call userCheck, Response response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "code :: " +response.code());
                    return;
                }
                UserVO userOwner = (UserVO) response.body();
                selectedFolder.setFolderOwnerName(userOwner.getName());
                menuNaviBinding.folderNameTv.setText(selectedFolder.getFolderName());
                menuNaviBinding.folderOwnerTv.setText(selectedFolder.getFolderOwnerName());
                if (selectedFolder.getFolderCanWrite() == 2){
                    menuNaviBinding.usersRb.setChecked(true);
                }
                if (selectedFolder.getFolderCanDelete() == 2){
                    menuNaviBinding.evyRb.setChecked(true);
                }
                if("Y".equals(selectedFolder.getFolderSortMine())){
                    menuNaviBinding.sortMineCb.setChecked(true);
                }
                if(USER.getUserId().equals(userOwner.getUserId())){
                    menuNaviBinding.ownerRb.setEnabled(true);
                    menuNaviBinding.usersRb.setEnabled(true);
                    menuNaviBinding.psnRb.setEnabled(true);
                    menuNaviBinding.evyRb.setEnabled(true);
                    setupFolder(selectedFolder);
                }

                Call getUsers = menuRequestRetrofit.getFolderUsers(selectedFolder);
                getUsers.enqueue(new RetrofitCustomCallBack() {
                    @Override
                    public void onFinalResponse(Call getUsers, Response response) {
                        if(!response.isSuccessful()){
                            Log.i(TAG, "code :: " +response.code());
                            return;
                        }
                        List<UserVO> userList = (List<UserVO>) response.body();

                        userList.add(0,new UserVO(null,null,"친구 추가","N"));
                        userListAdapter = new UserListAdapter(
                                viewPagerGallery
                                , DriveStorageView.this
                                , recyclerView
                                , selectedFolder
                                , userList
                                , glide);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        menuNaviBinding.userListView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                        menuNaviBinding.userListView.setAdapter(userListAdapter);

                        if (userList.toString().indexOf(USER.toString()) >0 ){
                            drawerLayout.openDrawer(menuNaviBinding.drawerView);
                        }else{
                            mToast.setText("선택된 자료가 없습니다.");
                            mToast.show();
                        }

                    }
                    @Override
                    public void onFinalFailure(Call getUsers, Throwable t) {

                    }
                });
            }
            @Override
            public void onFinalFailure(Call userCheck, Throwable t) {

            }
        });

    }
    public void showDeleteFilesDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_deletefiles);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List deleteList = driveStorageAdapter.getSelectedList();
                if(!driveStorageAdapter.getIsCanDelete()){
                    mToast.setText("이 폴더는 개인이 올렸던 자료만 삭제 가능합니다.");
                    mToast.show();
                    return;
                }
                if (deleteList.size() > 0) {
                    deleteFile(deleteList);
                    imgList.removeAll(deleteList);
                    viewPagerGallery.setonLongClick(driveStorageAdapter, false);
                    dialog.dismiss();
                }else{
                    mToast.setText("선택하신 자료가 없습니다.");
                    mToast.show();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
    public void getInitFolder(){
        String driveSeqFolder = PreferenceManager.getString(this.getContext(), "driveSeqFolder");
        Log.i(TAG ,driveSeqFolder+" ->> getInitFolder");
        if(!"".equals(driveSeqFolder)) {
            FolderVO vo = new FolderVO();
            vo.setSeqFolder(driveSeqFolder);
            menuRequestRetrofit.getInitFolder(vo).enqueue(new RetrofitCustomCallBack<FolderVO>() {
                @Override
                public void onFinalResponse(Call<FolderVO> call, Response<FolderVO> response) {
                    selectedFolder = response.body();
                }
                @Override
                public void onFinalFailure(Call<FolderVO> call, Throwable t) {

                }
            });
        }
    }

    public void getFiles(FolderVO vo){
        Call<ArrayList<ImgViewVO>> call = menuRequestRetrofit.getFolderFiles(vo);
        call.enqueue(new RetrofitCustomCallBack<ArrayList<ImgViewVO>>() {
            @Override
            public void onFinalResponse(Call<ArrayList<ImgViewVO>> call, Response<ArrayList<ImgViewVO>> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "code :: " +response.code());
                    return;
                }
                imgList = response.body();
                isComplete = true;
                driveStorageAdapter.setFileList(imgList);
                Log.i(TAG, "사이즈 :: " +imgList.size()+"");

                if (imgList.size() < LOAD_MORE_CNT ){
                    isCanLoadMore = false;
                }else {
                    pageNum++;
                }
                if (imgList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    driveBinding.emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    driveBinding.emptyView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFinalFailure(Call<ArrayList<ImgViewVO>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public void getMoreFiles(FolderVO vo) {
        Log.i(TAG ,"새로 받아오고 있습니다. ::" + vo.getSeqFolder());
        vo.setPageNum(pageNum);
        Call<ArrayList<ImgViewVO>> call = menuRequestRetrofit.getFolderFiles(vo);
        call.enqueue(new RetrofitCustomCallBack<ArrayList<ImgViewVO>>() {
            @Override
            public void onFinalResponse(Call<ArrayList<ImgViewVO>> call, Response<ArrayList<ImgViewVO>> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "code :: " +response.code());
                    return;
                }
                newList = response.body();
                int newListSize = newList.size();
                imgList.addAll(newList);
                driveStorageAdapter.notifyItemInserted(newListSize);
                driveStorageAdapter.setLoaded();
                Log.i(TAG, "new added 사이즈 :: " +newListSize);
                if (newListSize < LOAD_MORE_CNT ){
                    isCanLoadMore = false;
                }else {
                    pageNum++;
                }
            }
            @Override
            public void onFinalFailure(Call<ArrayList<ImgViewVO>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public void deleteFile(List<ImgViewVO> files){
        Call<String> call = menuRequestRetrofit.deleteFile(files);
        call.enqueue(new RetrofitCustomCallBack<String>() {
            @Override
            public void onFinalResponse(Call<String> call, Response<String> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "code :: " +response.code());
                }
                response.body();
            }

            @Override
            public void onFinalFailure(Call<String> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public void setInitFolder(FolderVO vo){
        this.pageNum = 1;
        this.selectedFolder = vo;
        Log.i(TAG, "setInitFolder"+vo.toString());
    }

    public void savePreferenceManager(){
        if(selectedFolder != null){
            PreferenceManager.setString(this.getContext(), "driveSeqFolder", selectedFolder.getSeqFolder());;
        }
    }

    public Disposable getObservable(FolderVO vo) {
        if(pageNum == 1 && vo!= null){
            Log.i(TAG ,"getObservable 시작" +  vo.getSeqFolder());
            vo.setPageNum(1);
            getFiles(vo);
            return Observable.interval(5000L, TimeUnit.MILLISECONDS).take(1)
                    .repeatUntil(() ->isComplete)
                    .doOnNext(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Throwable {
                            if(imgList.size() == 0){
                                getFiles(vo);
                                Log.i(TAG, "불러오는 중입니다...");
                            }
                        }
                    }).subscribeOn(Schedulers.io())
                    .subscribe();
        }
        return null;
    }
}