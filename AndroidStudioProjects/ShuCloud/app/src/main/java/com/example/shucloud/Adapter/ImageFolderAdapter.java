package com.example.shucloud.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.shucloud.Custom.AutoFitViewModuler;
import com.example.shucloud.R;
import com.example.shucloud.SVO.StroageBucketVO;

import java.util.List;

public class ImageFolderAdapter extends RecyclerView.Adapter<ImageFolderAdapter.CustomViewHolder> {

    private View viewPagerView;
    private AutoFitViewModuler recyclerView;
    private List<StroageBucketVO> bucketList;
    private final RequestManager glide;

    public ImageFolderAdapter(View viewPagerView, AutoFitViewModuler recyclerView, List<StroageBucketVO> bucketList, RequestManager glide) {
        this.viewPagerView = viewPagerView;
        this.recyclerView = recyclerView;
        this.bucketList = bucketList;
        this.glide = glide;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bucketlist, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        StroageBucketVO vo = bucketList.get(position);
        glide.load(vo.getThumbnailUri()).into(holder.iv);
        holder.nameTv.setText(vo.getBucketName());
        holder.countTv.setText(String.valueOf(vo.getCount()));

        if("전체사진".equals(vo.getBucketName())){
            holder.bookmarkIv.setVisibility(View.VISIBLE);
        }

        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity a = (Activity)recyclerView.getContext();
                Intent intent = new Intent();
                intent.putExtra("mediaType",1);
                intent.putExtra("selection", "bucket_display_name=?");
                intent.putExtra("selectionArgs", new String[]{vo.getBucketName()});
                a.setResult(Activity.RESULT_OK,intent);
                a.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bucketList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected ImageView iv;
        protected ImageView bookmarkIv;
        protected TextView nameTv;
        protected TextView countTv;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            nameTv = itemView.findViewById(R.id.nameTv);
            countTv = itemView.findViewById(R.id.countTv);
            bookmarkIv = itemView.findViewById(R.id.bookmarkIv);

        }

    }
}
