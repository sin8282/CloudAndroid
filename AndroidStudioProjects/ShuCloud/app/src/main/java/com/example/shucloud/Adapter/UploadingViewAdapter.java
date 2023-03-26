package com.example.shucloud.Adapter;

import static com.google.gson.internal.bind.util.ISO8601Utils.format;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shucloud.R;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.Service.BackgroundSV;
import com.example.shucloud.UploadingActivity;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

public class UploadingViewAdapter extends RecyclerView.Adapter <UploadingViewAdapter.CustomViewHolder> {

    private final String TAG = getClass().getName();
    private static UserVO USER;
    private UploadingActivity uploadingActivity;
    private View viewPagerView;
    private RecyclerView recyclerView;

    private List<ImgViewVO> uploadList;
    private List<MultipartBody.Part> failureList;
    private List<FolderVO> userfolder;
    private Toast mToast = null;

    private BackgroundSV backgroundSV;

    public UploadingViewAdapter(View viewPagerView, RecyclerView recyclerView, List folderList, List uploadList, UploadingActivity uploadingActivity) {
        this.viewPagerView = viewPagerView;
        this.recyclerView = recyclerView;
        this.userfolder = folderList;
        this.uploadList = uploadList;
        this.uploadingActivity = uploadingActivity;
        this.backgroundSV = (BackgroundSV) uploadingActivity;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_folderlist,parent,false);
        failureList = new ArrayList<>();
        USER = uploadingActivity.managingService.getUSER();
        mToast = Toast.makeText(v.getContext(), "", Toast.LENGTH_SHORT);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        FolderVO vo =userfolder.get(position);
        if (vo.getFolderType().equals("F")){
            holder.folderIamge.setImageResource(R.drawable.ic_folder);
        }
        if(vo.getFolderUsers() != null){
            holder.UserType.setImageResource(R.drawable.ic_users);
        }
        holder.folderName.setText(vo.getFolderName());

        holder.folderCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int isCanWrite = vo.getFolderCanWrite();
                Log.i(TAG, isCanWrite +"    ::     "+ USER.getUserId()+",,"+vo.getFolderOwner());
                if(isCanWrite == 1 && !USER.getUserId().equals(vo.getFolderOwner())){
                    mToast.setText("이 폴더는 공유된 폴더이며, 방장만 업로드 할 수 있습니다. ");
                    mToast.show();
                    return;
                }

                holder.folderCV.setBackgroundColor(Color.GRAY);
                backgroundSV.startUploadSV(userfolder.get(holder.getAdapterPosition()));

            }
        });
    }

    @Override
    public int getItemCount() {
        return userfolder == null ? 0 : userfolder.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected CardView folderCV;
        protected ImageView folderIamge;
        protected TextView folderName;
        protected ImageView UserType;
        protected CheckBox checkBox;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            folderCV = itemView.findViewById(R.id.folderCV);
            folderIamge = itemView.findViewById(R.id.folderIamge);
            folderName = itemView.findViewById(R.id.folderNameTv);
            UserType = itemView.findViewById(R.id.userTypeIv);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }

    public void setFolder(List<FolderVO> folder) {
        this.userfolder = folder;
        notifyItemInserted(folder.size());
    }
}
