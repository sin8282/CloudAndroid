package com.example.shucloud.SVO;

import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class ContentResolverQueryVO {
    private Uri newCollection;
    private Uri oldCollection;
    private String[] projection , selectionArgs;
    private String selection;
    private String sortOrder;
    private int mediaType;
    private int resultCode;


    public ContentResolverQueryVO(int mediaType, String[] projection, String[] selectionArgs, String selection, String sortOrder) {
        this.mediaType = mediaType;
        this.selectionArgs = selectionArgs;
        this.selection = selection;
        this.sortOrder = sortOrder;
        setCollection(mediaType);
        setProjection(mediaType);
        this.projection = projection;
    }

    public Uri getNewCollection() {
        return newCollection;
    }

    public Uri getOldCollection() {
        return oldCollection;
    }

    public int getMediaType() {
        return mediaType;
    }

    public String[] getProjection() {
        return projection;
    }

    public void setNewCollection(Uri newCollection) {
        this.newCollection = newCollection;
    }

    public void setOldCollection(Uri oldCollection) {
        this.oldCollection = oldCollection;
    }

    public void setProjection(String[] projection) { this.projection = projection; }


    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setCollection(int mediaType) {
        this.mediaType = mediaType;
        if (mediaType == 1){
            this.newCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            this.oldCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            this.projection = new String[] {
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.SIZE,
            };
        }else if (mediaType == 2){
            this.newCollection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            this.oldCollection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            this.projection = new String[] {
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DATE_TAKEN,
                    MediaStore.Video.Media.SIZE,
            };
        }

    }

    public void setProjection(int mediaType) {
        if (mediaType == 1){
            this.projection = new String[] {
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.SIZE,
            };
        }else if (mediaType == 2){
             this.projection = new String[] {
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DATE_TAKEN,
                    MediaStore.Video.Media.SIZE,
            };
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "mediaType : " +mediaType +
               ", newCollection : " + newCollection +
               ", oldCollection : " + oldCollection +
               ", projection : " + Arrays.toString(projection) +
               ", selectionArgs : " + Arrays.toString(selectionArgs) +
               ", selection : " + selection +
               ", sortOrder : " + sortOrder ;
    }

}
