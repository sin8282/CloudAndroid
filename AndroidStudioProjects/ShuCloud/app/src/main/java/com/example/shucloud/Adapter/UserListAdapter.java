package com.example.shucloud.Adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.shucloud.DriveStorageView;
import com.example.shucloud.R;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.ViewPagerGallery;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListCustomHolder>{
    private static UserVO USER;
    private ViewPagerGallery viewPagerGallery;
    private DriveStorageView driveStorageView;
    private RecyclerView recyclerView;
    private FolderVO selectedFolder;
    private List<UserVO> userList;
    private RequestManager glide;

    public UserListAdapter(ViewPagerGallery viewPagerGallery, DriveStorageView driveStorageView, RecyclerView recyclerView, FolderVO selectedFolder, List userList, RequestManager glide){
        this.viewPagerGallery = viewPagerGallery;
        this.driveStorageView = driveStorageView;
        this.recyclerView = recyclerView;
        this.selectedFolder = selectedFolder;
        this.userList = userList;
        this.glide = glide;
    }
    @NonNull
    @Override
    public UserListCustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_userlist,parent,false);
        USER = viewPagerGallery.managingService.getUSER();
        return new UserListCustomHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull UserListCustomHolder holder, int position) {
        UserVO vo =  userList.get(position);
        holder.userNameTv.setText(vo.getName());

        glide.load(vo.getUserProfile()).into(holder.userIv);
        if (holder.getAdapterPosition() == 0) {
            glide.load(R.drawable.ic_add_circle).into(holder.userIv);
            holder.userNameTv.setTextColor(Color.GRAY);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<FolderVO> folderList = new ArrayList<>();
                    folderList.add(selectedFolder);
                    driveStorageView.showAddUserDialog(folderList);
                }
            });
        }else if(vo.getUserId().equals(selectedFolder.getFolderOwner())){
            holder.userinfo.setVisibility(View.VISIBLE);
            holder.userinfo.setColorFilter(Color.parseColor("#FFE100"));
            holder.userinfo.setImageResource(R.drawable.ic_crown);
        }else{
            if(USER.getUserId().equals(selectedFolder.getFolderOwner())){
                holder.userinfo.setVisibility(View.VISIBLE);
                holder.userinfo.setImageResource(R.drawable.ic_delete_user);
                holder.userinfo.setColorFilter(Color.parseColor("#79a8a9"));
                holder.userinfo.setOnClickListener(v -> {
                    List<FolderVO> deleteOneUser = new ArrayList();
                    selectedFolder.setFolderUsers(vo.getUserId());
                    Log.i("my log station ::",  selectedFolder.toString());
                    deleteOneUser.add(selectedFolder);
                    driveStorageView.showDeleteFolderDialog(deleteOneUser);
                });
            }else {
                holder.userinfo.setVisibility(View.GONE);
                if(USER.getUserId().equals(vo.getUserId())) {
                    holder.userinfo.setVisibility(View.VISIBLE);
                    holder.userinfo.setImageResource(R.drawable.ic_delete_user);
                    holder.userinfo.setColorFilter(Color.parseColor("#79a8a9"));
                    holder.userinfo.setOnClickListener(v -> {
                        List<FolderVO> deleteOneUser = new ArrayList();
                        selectedFolder.setFolderUsers(vo.getUserId());
                        Log.i("my log station ::", selectedFolder.toString());
                        deleteOneUser.add(selectedFolder);
                        driveStorageView.showDeleteFolderDialog(deleteOneUser);
                    });
                }
            }
        }





/*        else if(vo.getUserId().equals(selectedFolder.getFolderOwner())){
            holder.userinfo.setVisibility(View.VISIBLE);
        }else{
            if(USER.getUserId().equals(selectedFolder.getFolderOwner())){
                holder.userinfo.setVisibility(View.VISIBLE);
                holder.userinfo.setImageResource(R.drawable.ic_delete_user);
                holder.userinfo.setColorFilter(Color.parseColor("#79a8a9"));
                holder.userinfo.setOnClickListener(v -> {
                    List<FolderVO> deleteOneUser = new ArrayList();
                    selectedFolder.setFolderUsers(vo.getUserId());
                    Log.i("my log station ::",  selectedFolder.toString());
                    deleteOneUser.add(selectedFolder);
                    driveStorageView.showDeleteFolderDialog(deleteOneUser);
                });
            }else if(USER.getUserId().equals(selectedFolder.getFolderUsers())) {
                holder.userinfo.setVisibility(View.VISIBLE);
                holder.userinfo.setImageResource(R.drawable.ic_delete_user);
                holder.userinfo.setColorFilter(Color.parseColor("#79a8a9"));
                holder.userinfo.setOnClickListener(v -> {
                    List<FolderVO> deleteOneUser = new ArrayList();
                    selectedFolder.setFolderUsers(vo.getUserId());
                    Log.i("my log station ::",  selectedFolder.toString());
                    deleteOneUser.add(selectedFolder);
                    driveStorageView.showDeleteFolderDialog(deleteOneUser);
                });
            } else {
                holder.userinfo.setVisibility(View.GONE);
            }

        }*/


    }

    @Override
    public int getItemCount() {
        return (null != userList ? userList.size(): 0);
    }

    public class UserListCustomHolder extends RecyclerView.ViewHolder{
        protected ImageView userIv;
        protected TextView userNameTv;
        protected ImageView userinfo;

        public UserListCustomHolder(@NonNull View itemView) {
            super(itemView);
            userIv = itemView.findViewById(R.id.userIv);
            userNameTv = itemView.findViewById(R.id.userNameTv);
            userinfo = itemView.findViewById(R.id.userinfo);
        }
    }

    public void setUserListRefresh(List<UserVO> userList){
        this.userList = userList;
        userList.add(0,new UserVO(null,null,"친구 추가","N"));
        Log.i("my log station " ,userList.toString());
        notifyDataSetChanged();
    }
}
