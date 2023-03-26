package com.android.svo;

import java.io.Serializable;

public class ParamsVO implements Serializable{


	private static final long serialVersionUID = 1L;
	
	private int totalCnt;
	private int presentCnt;
	private String userId;
	private String folderOwner;
	private String folderName;
	private String seqFolder;

	
	public int getTotalCnt() {
		return totalCnt;
	}
	public void setTotalCnt(int totalCnt) {
		this.totalCnt = totalCnt;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFolderOwner() {
		return folderOwner;
	}
	public void setFolderOwner(String folderOwner) {
		this.folderOwner = folderOwner;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public int getPresentCnt() {
		return presentCnt;
	}
	public void setPresentCnt(int presentCnt) {
		this.presentCnt = presentCnt;
	}
	
	public String getSeqFolder() {
		return seqFolder;
	}
	public void setSeqFolder(String seqFolder) {
		this.seqFolder = seqFolder;
	}
	@Override
	public String toString() {
		return totalCnt+userId+folderName;
	}

}
