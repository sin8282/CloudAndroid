package com.example.shucloud.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.shucloud.DriveStorageView;
import com.example.shucloud.FolderProfileActivity;
import com.example.shucloud.ManagingView;
import com.example.shucloud.R;
import com.example.shucloud.SVO.FolderVO;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.UploadingActivity;
import com.example.shucloud.ViewPagerGallery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ManagingViewAdapter extends RecyclerView.Adapter <ManagingViewAdapter.CustomViewHolder> {

    private final String TAG = getClass().getName();
    private UserVO USER;
    private static final String BASE_URL_API = "";
    private ViewPagerGallery viewPagerGallery;
    private RecyclerView recyclerView;
    private ManagingView managingView;
    public List<FolderVO> userFolder;
    private List<FolderVO> selectedFolder;
    private RequestManager glide;

    public boolean isDealState = false;
    private boolean isSelected = false;
    private Toast mToast = null;


    public ManagingViewAdapter(ViewPagerGallery viewPagerGallery, RecyclerView recyclerView, ManagingView managingView, List folder, UserVO USER, RequestManager glide) {
        this.viewPagerGallery = viewPagerGallery;
        this.recyclerView = recyclerView;
        this.managingView = managingView;
        this.userFolder = folder;
        this.USER = USER;
        this.selectedFolder = new ArrayList<>();
        this.glide = glide;
    }

    public void initListener() {
        mToast = Toast.makeText(managingView.getContext(), "", Toast.LENGTH_SHORT);
        CheckBox allItemCb = viewPagerGallery.binding.allItemCb;
        CardView bottomSubNavi = viewPagerGallery.binding.bottomSubNavi;
        TextView uploadCntTv = viewPagerGallery.binding.uploadCntTv;
        bottomSubNavi.setOnClickListener(v -> {});
        allItemCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isSelected){

            }else {
                if (isChecked){
                    for (FolderVO vo : userFolder) {
                        vo.setChecked(true);
                    }
                    selectedFolder = new ArrayList<>(userFolder);
                }else{
                    for(FolderVO vo:userFolder){
                        vo.setChecked(false);
                    }
                    selectedFolder.clear();
                }
            }
            uploadCntTv.setText(String.valueOf(selectedFolder.size()));
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_folderlist,parent,false);
        initListener();
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

            FolderVO vo = userFolder.get(position);
            if (vo.getDealState().equals("N")){
                holder.folderCV.setBackgroundColor(Color.GRAY);
                holder.folderProgressTv.setText(vo.getProgressPercentage()+"%");
                isDealState = true;
            }else if(vo.getDealState().equals("Q")){
                holder.folderCV.setBackgroundColor(Color.YELLOW);
                holder.folderProgressTv.setText("초대 상태입니다. 클릭하여 승인");
            }else{
                holder.folderCV.setBackgroundColor(Color.WHITE);
                holder.folderProgressTv.setText("");
                List<FolderVO> progressFolder= viewPagerGallery.managingService.getUpFolder();

                if (progressFolder.contains(vo.getSeqFolder())){
                    //viewPagerGallery.myVibrate();
                    Toast.makeText(viewPagerGallery, vo.getFolderName()+" 폴더 업로드가 완료되었습니다.", Toast.LENGTH_LONG).show();
                    progressFolder.remove(vo.getSeqFolder());
                }
            }

        if(vo.getUserCustomFolderName() != null){
            holder.folderName.setText(vo.getUserCustomFolderName());
        }else {
            holder.folderName.setText(vo.getFolderName());
        }

        if (vo.getUserCustomProfile() != null){
           glide.load(BASE_URL_API+vo.getUserCustomProfile()).into(holder.folderIamge);
           holder.folderIamge.setBackground(AppCompatResources.getDrawable(managingView.getContext(),R.drawable.imgview_radius_background));
        }else if (vo.getFolderType().equals("F")){
            holder.folderIamge.setImageResource(R.drawable.ic_folder);
            holder.folderIamge.setBackground(null);
        }else{
            holder.folderIamge.setImageResource(R.drawable.ic_gallery);
            holder.folderIamge.setBackground(null);
        }

        if(vo.getUserCount() != 1){
            holder.UserType.setImageResource(R.drawable.ic_users);
        }else{
            holder.UserType.setImageResource(R.drawable.ic_user);
        }

        if(viewPagerGallery.managingService.isLongClick()){
                //if (USER.getUserId().equals(vo.getFolderOwner())){
                holder.UserType.setVisibility(View.GONE);
                holder.checkbox.setVisibility(View.VISIBLE);

                holder.folderCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.checkbox.setChecked(holder.checkbox.isChecked()?false:true);
                    }
                });
                holder.checkbox.setVisibility(View.VISIBLE);
                holder.checkbox.setOnCheckedChangeListener(null);
                holder.checkbox.setChecked(vo.isChecked());
                holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        CheckBox allItemCb = viewPagerGallery.viewPagerRoot.findViewById(R.id.allItemCb);
                        TextView uploadCntTv =  buttonView.getRootView().findViewById(R.id.uploadCntTv);
                        //set your object's last status
                        vo.setChecked(isChecked);
                        if(isChecked){
                            selectedFolder.add(vo);
                        }else{
                            selectedFolder.remove(vo);
                        }
                        Log.i(TAG,isChecked+" 유저사이즈 >"+ userFolder.size() +"셀렉 >"+selectedFolder.size() );

                        if(selectedFolder.size()>0 && userFolder.size() == selectedFolder.size()){
                            isSelected = true;
                            allItemCb.setChecked(true);
                            isSelected = false;
                        }else if (selectedFolder.size()>0 && userFolder.size() != selectedFolder.size()){
                            isSelected = true;
                            allItemCb.setChecked(false);
                            isSelected = false;
                        }else if (selectedFolder.size() == 0 ){
                            isSelected = true;
                            allItemCb.setChecked(false);
                            isSelected = false;
                        }
                        uploadCntTv.setText(String.valueOf(selectedFolder.size()));
                    }
                });
                holder.folderIamge.setOnClickListener(v -> {
                    Intent intent = new Intent(viewPagerGallery, FolderProfileActivity.class);
                    intent.putExtra("folderVo",vo );
                    viewPagerGallery.startActivity(intent);
                });
                holder.folderName.setOnClickListener(v -> {
                    managingView.showUpdateFolderNameDialog(vo);
                });
                viewPagerGallery.binding.driveDeleteBtn.setOnClickListener(v -> {
                    managingView.showDeleteFolderDialog(selectedFolder);
                });
            }else{
                holder.checkbox.setVisibility(View.GONE);
                holder.UserType.setVisibility(View.VISIBLE);
                holder.folderIamge.setOnClickListener(null);
                holder.folderName.setOnClickListener(null);
                holder.folderCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(vo.getDealState().equals("Q")) {
                            managingView.getUserEmail(vo);
                        }else{
                            DriveStorageView.newInstance().setInitFolder(userFolder.get(holder.getAdapterPosition()));
                            viewPagerGallery.mainNavi.setSelectedItemId(R.id.mainNavi_2);
                        }
                    }
                });
            }
    }

    @Override
    public int getItemCount() {
        return (null != userFolder ? userFolder.size(): 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected CardView folderCV;
        protected ImageView folderIamge;
        protected TextView folderName;
        protected ImageView UserType;
        final protected CheckBox checkbox;
        protected TextView folderProgressTv;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            folderCV = itemView.findViewById(R.id.folderCV);
            folderIamge = itemView.findViewById(R.id.folderIamge);
            folderName = itemView.findViewById(R.id.folderNameTv);
            UserType = itemView.findViewById(R.id.userTypeIv);
            checkbox = itemView.findViewById(R.id.checkbox);
            folderProgressTv = itemView.findViewById(R.id.folderProgressTv);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    viewPagerGallery.setonLongClick(ManagingViewAdapter.this, null);
                    return true;
                }
            });
        }
    }

    public List<FolderVO> getSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(List<FolderVO> selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public void setFolder(List<FolderVO> folder) {
        this.userFolder = folder;
        this.isDealState = false;
        notifyDataSetChanged();
    }


}
