package com.example.shucloud.Service;



import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.Post;
import com.example.shucloud.SVO.UserVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {



    @FormUrlEncoded
    @POST("/posts")
    Call<Post> PostData(@FieldMap HashMap<String,Object> param);
    /*
    @Part MultipartBody.Part file
    @Part("items") RequestBody items,
    @Part("isAny") RequestBody isAny
    */

/*    @Multipart
    @POST("/uploadFile")
    Call<String> uploadFile(
        @PartMap Map<String, RequestBody> body

    );*/


    @POST("/userCheck")
    Call<UserVO> userCheck(
            @Body UserVO vo
    );
    @POST("/userCheckAsId")
    Call<UserVO> userCheckAsId(
            @Query("userId") String ownerUserId
    );
    @POST("/getFolderUsers")
    Call<ArrayList<UserVO>> getFolderUsers(
            @Body FolderVO vo
    );
    @POST("/getInitFolder")
    Call<FolderVO> getInitFolder(
            @Body FolderVO vo
    );


    @Multipart
    @POST("/uploadFile")
    Call<String> uploadFile(
            @Part ArrayList<MultipartBody.Part> files
            ,@PartMap Map<String, RequestBody> params
    );
    @Multipart
    @POST("/uploadFile")
    Single<String> uploadFile2(
            @Part ArrayList<MultipartBody.Part> files
            ,@PartMap Map<String, RequestBody> params
    );
    @Multipart
    @POST("/uploadFolderProfile")
    Call<String> uploadFolderProfile(
            @Part MultipartBody.Part file
            ,@PartMap Map<String, RequestBody> params
    );
    @POST("/uploadFolderName")
    Call<String> uploadFolderName(
            @Body FolderVO vo
    );
    @POST("/deleteFile")
    Call<String> deleteFile(
            @Body List<ImgViewVO> files
    );
    @POST("/folderDealState")
    Call<String> folderDealState(
            @Body FolderVO vo
    );
    @POST("/getFolderFiles")
    Call<ArrayList<ImgViewVO>> getFolderFiles(
            @Body FolderVO vo
    );

    @POST("/getUserFolders")
    Call <ArrayList<FolderVO>> getUserFolders(
            @Body UserVO vo
    );
    @POST("/addFolder")
    Call<ArrayList<FolderVO>> addFolder(
            @Body FolderVO vo
    );
    @POST("/additionalUserFolders")
    Call<String> additionalUserFolders(
            @Body List<FolderVO> folderVo
            , @Query("userEmail") String userEmail
    );
    @POST("/addedUserConfirm")
    Call<String> addedUserConfirm(
            @Body FolderVO folderVo
            , @Query("confirm") boolean confirm
    );
    @POST("/deleteFolder")
    Call<String> deleteFolder(
            @Body List<FolderVO> folderVo
            , @Query("isCheckFiles") boolean isCheckFiles
    );
    @POST("/setupFolder")
    Call<String> setupFolder(
            @Body FolderVO folderVo
            , @Query("isPersonal") boolean isPersonal
    );


}
