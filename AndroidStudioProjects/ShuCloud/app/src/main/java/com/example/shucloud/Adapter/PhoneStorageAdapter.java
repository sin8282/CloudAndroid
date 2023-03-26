package com.example.shucloud.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.RequestManager;
import com.example.shucloud.Custom.LayoutAnimater;
import com.example.shucloud.PhoneStorageView;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.ViewPagerFolder;
import com.example.shucloud.R;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.InstanceVO;
import com.example.shucloud.UploadingActivity;
import com.example.shucloud.ViewPagerGallery;

import java.util.ArrayList;
import java.util.List;

public class PhoneStorageAdapter extends RecyclerView.Adapter <PhoneStorageAdapter.CustomViewHolder>{

    private final ViewPagerGallery viewPagerGallery;
    private RecyclerView recyclerView;
    private PhoneStorageView phoneStorageView;
    public List<ImgViewVO> imgList;
    public List<ImgViewVO> uploadList;

    private final RequestManager glide;
    private static final Handler handler = new Handler();

    private boolean isSelected = false;
    private Toast mToast = null;


    public PhoneStorageAdapter(ViewPagerGallery viewPagerGallery, PhoneStorageView phoneStorageView, RecyclerView recyclerView, List list, RequestManager glide){
        this.viewPagerGallery = viewPagerGallery;
        this.phoneStorageView = phoneStorageView;
        this.recyclerView = recyclerView;
        this.imgList = list;
        this.glide = glide;
        uploadList = new ArrayList<>();
        viewPagerGallery.managingService.setLongClick(false);

    }

    public void initListener() {
        mToast = Toast.makeText(phoneStorageView.getContext(), "", Toast.LENGTH_SHORT);
        CheckBox allItemCb = viewPagerGallery.binding.allItemCb;
        CardView bottomSubNavi = viewPagerGallery.binding.bottomSubNavi;
        TextView uploadCntTv = viewPagerGallery.binding.uploadCntTv;
        ImageView storageFolderIv = phoneStorageView.binding.storageFolderIv;
        allItemCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isSelected){

            }else {
                if (isChecked){
                    for (ImgViewVO vo : imgList) {
                        vo.setChecked(true);
                    }
                    uploadList = new ArrayList<>(imgList);
                }else{
                    for(ImgViewVO vo:imgList){
                        vo.setChecked(false);
                    }
                    uploadList.clear();
                }
            }
            uploadCntTv.setText(String.valueOf(uploadList.size()));
            notifyDataSetChanged();
        });

        bottomSubNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if (uploadList.size() > 0) {
                    InstanceVO instanceVO = new InstanceVO();
                    instanceVO.setInstanceVO(context,"uploadList",uploadList);
                    viewPagerGallery.doUploading();
                }else{
                    mToast.setText("선택하신 자료가 없습니다.");
                    mToast.show();
                }
            }
        });
        storageFolderIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ViewPagerFolder.class);
                ((Activity) context).startActivityForResult(intent,1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPagerGallery.setonLongClick(PhoneStorageAdapter.this, false);
                    }
                },500);

            }
        });

    }



    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_imgitemlist, parent, false);
        initListener();
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        ImgViewVO vo = imgList.get(position);
        glide.load(vo.getUri()).into(holder.iv);
        holder.name.setText(vo.getName());
        holder.date.setText(vo.getDate()+"");
        holder.size.setText(vo.getSize()+"");

        if(viewPagerGallery.managingService.isLongClick()) {
            holder.iv.setOnClickListener(v -> holder.checkbox.setChecked(holder.checkbox.isChecked()?false:true));
            holder.iv.setOnLongClickListener(v -> {
                viewPagerGallery.setonLongClick(PhoneStorageAdapter.this, false);
                return false;
            });
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setOnCheckedChangeListener(null);
            holder.checkbox.setChecked(vo.getChecked());
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    CheckBox allItemCb = viewPagerGallery.binding.allItemCb;
                    TextView uploadCntTv =  viewPagerGallery.binding.uploadCntTv;
                    //set your object's last status
                    vo.setChecked(isChecked);
                    if(isChecked){
                        uploadList.add(vo);
                    }else{
                        uploadList.remove(vo);
                    }
                    if(uploadList.size()>0 && imgList.size() == uploadList.size()){
                        isSelected = true;
                        allItemCb.setChecked(true);
                        isSelected = false;
                    }else if (uploadList.size()>0 && imgList.size() != uploadList.size()){
                        isSelected = true;
                        allItemCb.setChecked(false);
                        isSelected = false;
                    }else if (uploadList.size() == 0 ){
                        isSelected = true;
                        allItemCb.setChecked(false);
                        isSelected = false;
                    }
                    uploadCntTv.setText(String.valueOf(uploadList.size()));
                }
            });
        }else {
            holder.checkbox.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return (null != imgList ? imgList.size(): 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView iv;
        protected TextView name;
        protected TextView date;
        protected TextView size;
        final protected CheckBox checkbox;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            iv =  itemView.findViewById(R.id.iv);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            size = itemView.findViewById(R.id.size);
            checkbox = itemView.findViewById(R.id.checkbox);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    viewPagerGallery.setonLongClick(PhoneStorageAdapter.this, null);
                    return true;
                }
            });

        }
    }

    public void setLongClickDataClear(){
        CheckBox allItemCb = viewPagerGallery.binding.allItemCb;
        TextView uploadCntTv = viewPagerGallery.binding.uploadCntTv;

        uploadList.clear();
        allItemCb.setChecked(false);
        uploadCntTv.setText("0");
        for(ImgViewVO vo:imgList){
            vo.setChecked(false);
        }
    }


}
