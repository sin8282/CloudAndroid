package com.example.shucloud.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.shucloud.Custom.AutoFitViewModuler;
import com.example.shucloud.FolderProfileActivity;
import com.example.shucloud.R;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.api.ContentUriRequestBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FolderProfileAdapter  extends RecyclerView.Adapter <FolderProfileAdapter.CustomViewHolder> {
    private FolderProfileActivity folderProfile;
    private AutoFitViewModuler recyclerView;
    private FolderVO selectedFolder;
    private List<ImgViewVO> imgList;
    private RequestManager glide;
    private LinearLayout loading;

    public FolderProfileAdapter(FolderProfileActivity folderProfile, AutoFitViewModuler recyclerView, FolderVO selectedFolder, List imgList, RequestManager glide) {
        this.folderProfile = folderProfile;
        this.recyclerView = recyclerView;
        this.selectedFolder = selectedFolder;
        this.imgList = imgList;
        this.glide = glide;
    }

    @NonNull
    @Override
    public FolderProfileAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_imgitemlist, parent, false);
        loading = folderProfile.findViewById(R.id.loading);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        ImgViewVO vo = imgList.get(position);
        glide.load(vo.getUri()).into(holder.iv);
        holder.name.setText(vo.getName());
        holder.date.setText(vo.getDate()+"");
        holder.size.setText(vo.getSize()+"");


        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                String requestName = getFileName(folderProfile.USER); // todo실제 서버파일 이름
                ContentUriRequestBody contentUriRequestBody = new ContentUriRequestBody(Uri.parse(vo.getUri()),folderProfile.getContentResolver(), true);
                MultipartBody.Part multiPartBody = MultipartBody.Part.createFormData(requestName, contentUriRequestBody.getFileName(), contentUriRequestBody);
                HashMap<String, RequestBody> params = new HashMap<>();
                params.put("seqFolder",setParam(selectedFolder.getSeqFolder()));
                folderProfile.uploadFolderProfile(multiPartBody, params);
            }
        });
    }

    @Override
    public int getItemCount() {return (null != imgList ? imgList.size(): 0);}


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView iv;
        protected TextView name;
        protected TextView date;
        protected TextView size;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            size = itemView.findViewById(R.id.size);
        }
    }
    public String getFileName(UserVO user){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    private RequestBody setParam(String value){
        return RequestBody.create(value, MediaType.parse("text/plain"));
    }
}
