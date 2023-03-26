package com.example.shucloud.SVO;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FolderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("seqFolder")
    private String seqFolder;
    @SerializedName("folderName")
    private String folderName;
    @SerializedName("folderPath")
    private String folderPath;
    @SerializedName("folderOwner")
    private String folderOwner;
    @SerializedName("folderType")
    private String folderType;
    @SerializedName("folderUsers")
    private String folderUsers;
    @SerializedName("folderCanWrite")
    private int folderCanWrite;
    @SerializedName("folderCanDelete")
    private int folderCanDelete;
    @SerializedName("folderSortMine")
    private String folderSortMine;
    @SerializedName("userCount")
    private int userCount;
    @SerializedName("dealState")
    private String dealState;
    @SerializedName("progressPercentage")
    private String progressPercentage;
    @SerializedName("userCustomProfile")
    private String userCustomProfile;
    @SerializedName("userCustomFolderName")
    private String userCustomFolderName;
    @SerializedName("pageNum")
    private int pageNum;
    @SerializedName("msg")
    private String msg;
    private String folderOwnerName;
    private boolean checked = false;

    public String getFolderName() {
        return folderName;
    }
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    public String getFolderPath() {
        return folderPath;
    }
    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
    public String getFolderOwner() {
        return folderOwner;
    }
    public void setFolderOwner(String folderOwner) {
        this.folderOwner = folderOwner;
    }
    public String getFolderUsers() {
        return folderUsers;
    }
    public void setFolderUsers(String folderUsers) {
        this.folderUsers = folderUsers;
    }
    public String getFolderType() {
        return folderType;
    }
    public void setFolderType(String folderType) {
        this.folderType = folderType;
    }
    public int getFolderCanWrite() {
        return folderCanWrite;
    }
    public void setFolderCanWrite(int folderCanWrite) {
        this.folderCanWrite = folderCanWrite;
    }
    public int getFolderCanDelete() {
        return folderCanDelete;
    }
    public void setFolderCanDelete(int folderCanDelete) {
        this.folderCanDelete = folderCanDelete;
    }
    public String getFolderSortMine() {
        return folderSortMine;
    }
    public void setFolderSortMine(String folderSortMine) {
        this.folderSortMine = folderSortMine;
    }
    public String getSeqFolder() {
        return seqFolder;
    }
    public void setSeqFolder(String seqFolder) {
        this.seqFolder = seqFolder;
    }
    public int getUserCount() {
        return userCount;
    }
    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
    public String getDealState() {
        return dealState;
    }
    public void setDealState(String dealState) {
        this.dealState = dealState;
    }
    public String getProgressPercentage() {
        return progressPercentage;
    }
    public void setProgressPercentage(String progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
    public int getPageNum() {
        return pageNum;
    }
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getUserCustomProfile() {
        return userCustomProfile;
    }
    public void setUserCustomProfile(String userCustomProfile) {
        this.userCustomProfile = userCustomProfile;
    }
    public String getUserCustomFolderName() {
        return userCustomFolderName;
    }
    public void setUserCustomFolderName(String userCustomFolderName) {
        this.userCustomFolderName = userCustomFolderName;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getFolderOwnerName() {
        return folderOwnerName;
    }
    public void setFolderOwnerName(String folderOwnerName) {
        this.folderOwnerName = folderOwnerName;
    }

    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "FolderVO{" +
                "seqFolder='" + seqFolder + '\'' +
                ", folderName='" + folderName + '\'' +
                ", folderPath='" + folderPath + '\'' +
                ", folderOwner='" + folderOwner + '\'' +
                ", folderType='" + folderType + '\'' +
                ", folderUsers='" + folderUsers + '\'' +
                ", folderCanWrite=" + folderCanWrite +
                ", folderCanDelete=" + folderCanDelete +
                ", folderSortMine='" + folderSortMine + '\'' +
                ", userCount=" + userCount +
                ", dealState='" + dealState + '\'' +
                ", progressPercentage='" + progressPercentage + '\'' +
                ", userCustomProfile='" + userCustomProfile + '\'' +
                ", userCustomFolderName='" + userCustomFolderName + '\'' +
                ", pageNum=" + pageNum +
                ", msg='" + msg + '\'' +
                ", folderOwnerName='" + folderOwnerName + '\'' +
                ", checked=" + checked +
                '}';
    }
}
