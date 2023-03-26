package com.example.shucloud;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shucloud.Custom.PreferenceManager;
import com.example.shucloud.Custom.RetrofitCustomCallBack;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.Service.ManagingService;
import com.example.shucloud.Service.RetrofitInterface;
import com.example.shucloud.Service.ServiceConn;
import com.example.shucloud.api.ApiClient;
import com.example.shucloud.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.gun0912.tedpermission.PermissionListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ManagingService managingService;
    private ServiceConnection conn;

    private ActivityMainBinding binding;
    private SignInButton loginbtn;
    private GoogleSignInClient googleSignInClient;
    private UserVO USER_INFO;

    private RetrofitInterface menuRequestRetrofit = ApiClient.getApiService("menuRequestRetrofit",4030);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        LayoutInflater inflater = getLayoutInflater();
        binding = binding.inflate(inflater);
        View view = binding.getRoot();
        setContentView(view);

        // SharedPreferences 호출 ------------------------------------------------------------------
        //String USER = PreferenceManager.getString(this, "USER");
        //String selectedFolderSeq = PreferenceManager.getString(this, "selectedFolder");

        // Authorization Permission 확인 ------------------------------------------------------------
        String[] permissionList = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.FOREGROUND_SERVICE
        };

        if (ContextCompat.checkSelfPermission(this, permissionList[0]) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, permissionList[1]) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, permissionList[2]) == PackageManager.PERMISSION_DENIED) {

            // 마쉬멜로우 이상버전부터 권한을 물어본다.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(permissionList, 1000);
            }
        }
        // 로그인 ------------------------------------------------------------
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.googleLoginId))
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        loginbtn= binding.loginbtn;
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                getGoogleResult.launch(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            // 권한 체크에 동의를 하지 않으면 안드로이드 종료
            if (check_result == true) {
            } else {
                Toast.makeText(this, "권한 여부를 승인하셔야 앱을 이용가능 합니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }



    private final ActivityResultLauncher<Intent> getGoogleResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
                try {
                    if (result.getResultCode() == RESULT_OK){
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        GoogleSignInAccount account = task.getResult();
                        if(account != null){
                            USER_INFO = new UserVO(
                                    account.getId()
                                    , account.getEmail()
                                    , account.getDisplayName()
                                    , String.valueOf(account.getPhotoUrl())
                            );
                            callUserLogin(USER_INFO);
                        }
                    }else {
                        //Toast.makeText(this, "로그인 실패 :: "+ result.getResultCode(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.i(getClass().getName(), e.getMessage());
                }
            }
    );

    public void callUserLogin(UserVO setVo){

        Call<UserVO> call = menuRequestRetrofit.userCheck(setVo);
        call.enqueue(new RetrofitCustomCallBack<UserVO>() {
            @Override
            public void onFinalResponse(Call<UserVO> call, Response<UserVO> response) {
                if(!response.isSuccessful()){
                    Log.i(getClass().getName(), "code ::"+response.code());
                    return;
                }
                Log.i("my log station :: ", response.body().isMsg()+"");
                Intent mainIt = new Intent(MainActivity.this, ViewPagerGallery.class);
                //mainIt.putExtra("USER", response.body());


                conn = ServiceConn.newInstance();
                Intent managingIt = new Intent(MainActivity.this, ManagingService.class);
                managingIt.putExtra("USER", response.body());
                bindService(managingIt, conn, Context.BIND_AUTO_CREATE);

                startActivity(mainIt);
            }
            @Override
            public void onFinalFailure(Call<UserVO> call, Throwable t) {
                Log.i("my log station :: ", t.getMessage());
                Toast.makeText(MainActivity.this, "서버연결 실패 ::", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(conn!= null)
            unbindService(conn); // 서비스 종료

    }
}