package com.example.shucloud.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.shucloud.DriveStorageView;
import com.example.shucloud.SVO.UserVO;
import com.example.shucloud.Service.BackgroundSV;
import com.example.shucloud.R;
import com.example.shucloud.SVO.ImgViewVO;
import com.example.shucloud.SVO.InstanceVO;
import com.example.shucloud.ViewPagerGallery;

import java.util.ArrayList;
import java.util.List;

public class DriveStorageAdapter extends RecyclerView.Adapter{

    private final String TAG = getClass().getName();
    private static UserVO USER;
    private static final String BASE_URL_API = "";

    private final ViewPagerGallery viewPagerGallery;
    private final DriveStorageView driveStorageView;
    private RecyclerView recyclerView;
    public List<ImgViewVO> imgList;
    public List<ImgViewVO> selectedList; // 다운로드 or 삭제
    private final RequestManager glide;

    private boolean loading;
    private boolean isSelected = false;
    private boolean isCanDelete = true;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCnt;
    private OnLoadMoreListener onLoadMoreListener;
    private BackgroundSV backgroundSV;
    private Toast mToast = null;

    public DriveStorageAdapter(ViewPagerGallery viewPagerGallery, DriveStorageView driveStorageView, RecyclerView recyclerView, List imgList, RequestManager glide){
        this.recyclerView = recyclerView;
        this.viewPagerGallery = viewPagerGallery;
        this.driveStorageView = driveStorageView;
        this.imgList = imgList;
        this.glide = glide;
        this.selectedList = new ArrayList<>();
        this.backgroundSV = viewPagerGallery;

    }


    public void initListener() {
        USER =viewPagerGallery.managingService.getUSER();
        mToast = Toast.makeText(driveStorageView.getContext(), "", Toast.LENGTH_SHORT);
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCnt = gridLayoutManager.getItemCount();
                    lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                    /*loding 을 이용하여 현재 로딩중인지 파악한다.
                     * totalItemCnt는 현재 화면에 itemView list갯수이고, lastVisiableItem은 화면 오른쪽 하단에 보이는 item에 index위치이다.
                     * visibleThreshold를 통해 어느타이밍에 새로 자료를 불러올지 설정할 수 있다. 1로 설정하면 화면 끝까지 다내려야 로딩이 된다.
                     *  예 : 총 50 <= 현재위치 50번째 + 1 이 되므로 로딩이 된다. 0 으로 해도되겠군... */
/*                    Log.d(TAG, loading
                            +" : "+ totalItemCnt
                            +" : "+ lastVisibleItem
                            +" : "+ visibleThreshold);*/

                    if (!loading){
                        if(totalItemCnt>= driveStorageView.LOAD_MORE_CNT &&totalItemCnt <=(lastVisibleItem+visibleThreshold)){
                            if (onLoadMoreListener != null){
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }

                }
            });
        }
        CheckBox allItemCb = viewPagerGallery.binding.allItemCb;
        CardView bottomSubNavi = viewPagerGallery.binding.bottomSubNavi;
        TextView uploadCntTv = viewPagerGallery.binding.uploadCntTv;
        allItemCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isSelected){

            }else {
                if (isChecked){
                    for (ImgViewVO vo : imgList) {
                        vo.setChecked(true);
                    }
                    selectedList = new ArrayList<>(imgList);
                }else{
                    for(ImgViewVO vo:imgList){
                        vo.setChecked(false);
                    }
                    selectedList.clear();
                }
            }
            uploadCntTv.setText(String.valueOf(selectedList.size()));
            notifyDataSetChanged();
            Log.i("my log station ::","sdsdsd");
        });

        bottomSubNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if (selectedList.size() > 0) {
                    InstanceVO instanceVO = new InstanceVO();
                    instanceVO.setInstanceVO(context, "downloadList", selectedList);
                    driveStorageView.showDownLoadDialog();
                }else{
                    mToast.setText("선택하신 자료가 없습니다.");
                    mToast.show();
                }
            }
        });
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }
    public void setLoaded(){
        loading = false;
    }

    @Override
    public int getItemViewType(int position) {
        return imgList.get(position) != null ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return (null != imgList ? imgList.size(): 0);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        initListener();
        if(viewType == 1){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_imgitemlist, parent, false);
            vh = new CustomViewHolder(v);
        }else{ // 로딩창
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar,parent,false);
            vh = new ProgressViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ImgViewVO vo = imgList.get(position);
        int getFolderCanDelete = driveStorageView.selectedFolder.getFolderCanDelete();
        if(holder instanceof CustomViewHolder){
            CustomViewHolder ch = (CustomViewHolder)holder;
            glide.load(BASE_URL_API+imgList.get(position).getUri()).into(ch.iv);
            if(vo.getFileType().indexOf("video")>=0){
                ch.fileType.setVisibility(View.VISIBLE);
            }else{
                ch.fileType.setVisibility(View.GONE);
            }
            if(viewPagerGallery.managingService.isLongClick()) {
                ch.iv.setOnClickListener(v -> ch.checkbox.setChecked(ch.checkbox.isChecked()?false:true));
                ch.iv.setOnLongClickListener(v -> {
                    viewPagerGallery.setonLongClick(DriveStorageAdapter.this, false);
                    return false;
                });
                ch.checkbox.setVisibility(View.VISIBLE);
                ch.checkbox.setOnCheckedChangeListener(null);
                ch.checkbox.setChecked(vo.getChecked());
                ch.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        CheckBox allItemCb = viewPagerGallery.viewPagerRoot.findViewById(R.id.allItemCb);
                        TextView uploadCntTv =  buttonView.getRootView().findViewById(R.id.uploadCntTv);
                        //set your object's last status
                        vo.setChecked(isChecked);
                        if(isChecked){
                            //todo 일단 넣어봄
                            if(getFolderCanDelete == 1 && !USER.getUserId().equals(vo.getFileOwner())){
                                isCanDelete = false;
                            }
                            selectedList.add(vo);
                        }else{
                            selectedList.remove(vo);
                        }
                        if(selectedList.size()>0 && imgList.size() == selectedList.size()){
                            isSelected = true;
                            allItemCb.setChecked(true);
                            isSelected = false;
                        }else if (selectedList.size()>0 && imgList.size() != selectedList.size()){
                            isSelected = true;
                            allItemCb.setChecked(false);
                            isSelected = false;
                        }else if (selectedList.size() == 0 ){
                            isSelected = true;
                            allItemCb.setChecked(false);
                            isSelected = false;
                        }
                        uploadCntTv.setText(String.valueOf(selectedList.size()));
                    }
                });
            //ch.tv_name.setText(imgList.get(position).getFileName());
            }else{
                ch.checkbox.setVisibility(View.GONE);
            }
        }else{
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        protected ImageView iv;
        protected ImageView fileType;
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
            fileType = itemView.findViewById(R.id.fileType);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    viewPagerGallery.setonLongClick(DriveStorageAdapter.this, null);
                    return true;
                }
            });
        }


    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder{

        protected ProgressBar progressBar;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar1);
        }
    }

    public void setFileList(List<ImgViewVO> list) {
        this.imgList = list;
        notifyDataSetChanged();
    }
    public List getSelectedList() {
        return selectedList;
    }

    public boolean getIsCanDelete() {
        return isCanDelete;
    }
}
