package com.example.shucloud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shucloud.Adapter.ManagingViewAdapter;
import com.example.shucloud.Custom.AutoFitViewModuler;
import com.example.shucloud.Custom.RetrofitCustomCallBack;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.UserVO;

import com.example.shucloud.Service.RetrofitInterface;
import com.example.shucloud.api.ApiClient;
import com.example.shucloud.databinding.ActivityManagingViewBinding;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagingView extends Fragment {

    private final String TAG = this.getClass().getName();
    private static UserVO USER ;
    public static ViewPagerGallery viewPagerGallery;
    private AutoFitViewModuler recyclerView;
    public ActivityManagingViewBinding binding;
    private static ManagingViewAdapter managingViewAdapter;
    private List<FolderVO> userFolder = new ArrayList();
    Disposable reloadDisposable;

    private boolean isOpenedMenu = false;
    private Toast mToast = null;

    private static final RetrofitInterface menuRequestRetrofit = ApiClient.getApiService("menuRequestRetrofit",4030);



    public static ManagingView newInstance(){
        ManagingView managingView = new ManagingView();
        return managingView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewPagerGallery = (ViewPagerGallery) getActivity();
        USER = viewPagerGallery.managingService.getUSER();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
            if(!hidden){
                if(reloadDisposable != null && !reloadDisposable.isDisposed()) return;
                reloadDisposable= startObservable();
                Log.i(TAG, "Show 재개");
            }else{
                if(reloadDisposable == null) return;
                reloadDisposable.dispose();
                Log.i(TAG, "Hidden 컷!");
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadDisposable= startObservable();
        Log.i(TAG, "reloadObserable 재개");
    }

    @Override
    public void onPause() {
        super.onPause();
        reloadDisposable.dispose();
        Log.i(TAG, "reloadObserable 컷!");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = getLayoutInflater();
        binding = ActivityManagingViewBinding.inflate(inflater);
        View view = binding.getRoot();

        recyclerView = binding.imgRv;
        recyclerView.setHasFixedSize(true);
        recyclerView.mScaleDetector = null;

        managingViewAdapter = new ManagingViewAdapter(
                viewPagerGallery
                , recyclerView
                , this
                , userFolder
                , USER
                , Glide.with(view.getContext())
        );
        recyclerView.setAdapter(managingViewAdapter);

        // setListener ------------------------------------------------------------------
        // todo search 기능 만들기
        binding.folderSearchBtn.setOnClickListener(v -> { });
        binding.folderAddBtn.setOnClickListener(v -> { showAddFolderDialog(); });

        // todo user 버튼 다른페이지로 넘기기
        viewPagerGallery.viewPagerRoot.findViewById(R.id.userAddBtn).setOnClickListener(v -> {
            List selectedFolder = managingViewAdapter.getSelectedFolder();
            if(selectedFolder.size()>0){
                //showAddUserDialog(selectedFolder);
            }else {
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
                else{
                    mToast = Toast.makeText(v.getContext(), "선택하신 자료가 없습니다.", Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        });
        viewPagerGallery.viewPagerRoot.findViewById(R.id.deleteBtn).setOnClickListener(v ->{
            List selectedFolder = managingViewAdapter.getSelectedFolder();
            if(selectedFolder.size()>0){
                showDeleteFolderDialog(selectedFolder);
            }else {
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
                else{
                    mToast = Toast.makeText(v.getContext(), "선택하신 자료가 없습니다.", Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        });

        return view;
    }


    public boolean isOpenedMenu() {
        return isOpenedMenu;
    }

    public void showUpdateFolderNameDialog(FolderVO selectedFolder){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_foldername);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView msg1 = dialog.findViewById(R.id.textMessage1);
        if(selectedFolder.getUserCustomFolderName() != null){
            msg1.setText(selectedFolder.getUserCustomFolderName());
        }else{
            msg1.setText(selectedFolder.getFolderName());
        }
        EditText et = dialog.findViewById(R.id.newFolderEt);

        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFolder.setUserCustomFolderName(String.valueOf(et.getText()));
                menuRequestRetrofit.uploadFolderName(selectedFolder)
                    .enqueue(new RetrofitCustomCallBack<String>() {
                        @Override
                        public void onFinalResponse(Call<String> call, Response<String> response) {
                                if ("success".equals(response.body())){
                                    managingViewAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                        }
                        @Override
                        public void onFinalFailure(Call<String> call, Throwable t) {

                        }
                    });
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
        Log.i(TAG,selectedFolder.toString());
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_deletefiles);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView msg1 = dialog.findViewById(R.id.textMessage1);
        msg1.setText("선택하신 폴더들을 삭제 하시겠습니까?");
        TextView msg2 = dialog.findViewById(R.id.textMessage2);
        msg2.setText("*본인이 등록한 사진을 전부 삭제해야 폴더 삭제가 가능합니다.");
        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFolder(selectedFolder);
                viewPagerGallery.setonLongClick(managingViewAdapter, null);
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
        Call<String> call = menuRequestRetrofit.deleteFolder(selectedFolder, true);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "ERROR :: "+response.code());
                    Toast.makeText(getContext(), "ERROR :: "+response.code() , Toast.LENGTH_SHORT).show();
                    return;
                }
                getUserFolders();
                Toast.makeText(getContext(), response.body() , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void showAddedUserConfirmDialog(FolderVO folderVo, String ownerUserId){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_addeduserconfirm);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);

        TextView msg1= dialog.findViewById(R.id.textMessage1);
        msg1.setText(ownerUserId);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addedUserConfirm(folderVo,true);
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addedUserConfirm(folderVo,false);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void getUserEmail(FolderVO folderVo){
        Call<UserVO> call = menuRequestRetrofit.userCheckAsId(folderVo.getFolderOwner());
        call.enqueue(new Callback<UserVO>() {
            @Override
            public void onResponse(Call<UserVO> call, Response<UserVO> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "ERROR :: "+response.code());
                    Toast.makeText(getContext(), "ERROR :: "+response.code() , Toast.LENGTH_SHORT).show();
                    return;
                }
                UserVO getFolderUser = response.body();
                showAddedUserConfirmDialog(folderVo,getFolderUser.getUserEmail());
            }
            @Override
            public void onFailure(Call<UserVO> call, Throwable t) {
            }
        });
    }
    public void addedUserConfirm(FolderVO folderVo,boolean confirm){
        Call<String> call = menuRequestRetrofit.addedUserConfirm(folderVo, confirm);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "ERROR :: "+response.code());
                    Toast.makeText(getContext(), "ERROR :: "+response.code() , Toast.LENGTH_SHORT).show();
                    return;
                }
                String result = response.body();
                Toast.makeText(getContext(), result+" 완료했습니다." , Toast.LENGTH_SHORT).show();
                getUserFolders();
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }


    private void showAddFolderDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_addfolder);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);

        RadioGroup fileTypeGroup = dialog.findViewById(R.id.fileTypeGroup);
        EditText FolderNameEt = dialog.findViewById(R.id.FolderNameEt);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderVO vo = new FolderVO();
                vo.setFolderName(String.valueOf(FolderNameEt.getText()));
                RadioButton rd = dialog.findViewById(fileTypeGroup.getCheckedRadioButtonId());
                vo.setFolderType(rd.getText().equals("갤러리")? "G" : "F");
                vo.setFolderOwner(USER.getUserId());
                vo.setFolderUsers(USER.getUserId());

                addFolder(vo, dialog);
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
    private void addFolder(FolderVO vo, Dialog dialog){
        Call<ArrayList<FolderVO>> call = menuRequestRetrofit.addFolder(vo);
        call.enqueue(new Callback<ArrayList<FolderVO>>() {
            @Override
            public void onResponse(Call<ArrayList<FolderVO>> call, Response<ArrayList<FolderVO>> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "ERROR :: "+response.code());
                    Toast.makeText(getContext(), "ERROR :: "+response.code() , Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<FolderVO> result =  response.body();

                if (result.get(0).getMsg() == null){
                    Toast.makeText(getContext(), "성공적으로 생성되었습니다." , Toast.LENGTH_SHORT).show();
                    userFolder = result;
                    managingViewAdapter.setFolder(result);
                    dialog.dismiss();

                }else{
                    Toast.makeText(getContext(), "ERROR :: 관리자 문의"+result.get(0).getMsg() , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FolderVO>> call, Throwable t) {
                Log.e(TAG, "ERROR :: "+t.getMessage());
            }
        });
    }


    public void getUserFolders(){
        Call<ArrayList<FolderVO>> call = menuRequestRetrofit.getUserFolders(USER);
        call.enqueue(new Callback<ArrayList<FolderVO>>() {
            @Override
            public void onResponse(Call<ArrayList<FolderVO>> call, Response<ArrayList<FolderVO>> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "ERROR :: "+response.code());
                    Toast.makeText(getContext(), "ERROR :: "+response.code() , Toast.LENGTH_SHORT).show();
                    return;
                }
                userFolder = response.body();
                managingViewAdapter.setFolder(userFolder);
            }
            @Override
            public void onFailure(Call<ArrayList<FolderVO>> call, Throwable t) {
            }
        });
    }


    public Disposable startObservable(){
        getUserFolders();
        return Observable
                .interval(5000L, TimeUnit.MILLISECONDS).take(1)
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Throwable {
                        if (managingViewAdapter.isDealState) {
                            getUserFolders();
                            Log.d(TAG, "다시불러옴");
                        }

                    }
                })
                .repeatUntil(() -> !managingViewAdapter.isDealState)
                .subscribe();
    }


}