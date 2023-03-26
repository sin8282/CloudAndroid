package com.android.svo;

import java.io.InputStream;
import java.io.Serializable;

public class FileVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private  String uri;
    private  String name;
    private  String date;
    private  String size;
    private boolean checked = false;
    
    private  String seqFolder;
    private  String seqFile;
    private  String fileOwner;
    private  String fileUploadedName;
    private  String fileOriginalName;
    private  String filePath;
    private  String fileSize;
    private  String fileType;
	
	public String getUri() { return uri; }
	  
	public void setUri(String uri) { this.uri = uri; }
	 
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    
    
	public String getSeqFolder() {
		return seqFolder;
	}

	public void setSeqFolder(String seqFolder) {
		this.seqFolder = seqFolder;
	}

	public String getSeqFile() {
		return seqFile;
	}

	public void setSeqFile(String seqFile) {
		this.seqFile = seqFile;
	}

	public String getFileOwner() {
		return fileOwner;
	}

	public void setFileOwner(String fileOwner) {
		this.fileOwner = fileOwner;
	}

	public String getFileUploadedName() {
		return fileUploadedName;
	}

	public void setFileUploadedName(String fileUploadedName) {
		this.fileUploadedName = fileUploadedName;
	}

	public String getFileOriginalName() {
		return fileOriginalName;
	}

	public void setFileOriginalName(String fileOriginalName) {
		this.fileOriginalName = fileOriginalName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@Override
	public String toString() {
		
		return getName()+getDate()+getSize();
	}
    
}
