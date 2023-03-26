package com.example.shucloud.SVO;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.InputStream;
import java.io.Serializable;

public class ImgViewVO implements Serializable, Parcelable {

    private static final long serialVersionUID = 1L;

    @SerializedName("uri")
    private  String uri;
    @SerializedName("name")
    private  String name;
    @SerializedName("date")
    private  int date;
    @SerializedName("size")
    private  int size;
    @SerializedName("seqFile")
    private int seqFile;
    @SerializedName("fileType")
    private String fileType;
    @SerializedName("fileOwner")
    private String fileOwner;
    private boolean checked = false;

    public ImgViewVO(String uri, String name, int date, int size){
        this.uri = uri;
        this.name = name;
        this.date = date;
        this.size = size;
    }


    protected ImgViewVO(Parcel in) {
        uri = in.readString();
        name = in.readString();
        date = in.readInt();
        size = in.readInt();
        checked = in.readByte() != 0;
    }

    public static final Creator<ImgViewVO> CREATOR = new Creator<ImgViewVO>() {
        @Override
        public ImgViewVO createFromParcel(Parcel in) {
            return new ImgViewVO(in);
        }

        @Override
        public ImgViewVO[] newArray(int size) {
            return new ImgViewVO[size];
        }
    };

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getSeqFile() {
        return seqFile;
    }

    public void setSeqFile(int seqFile) {
        this.seqFile = seqFile;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileOwner() {
        return fileOwner;
    }

    public void setFileOwner(String fileOwner) {
        this.fileOwner = fileOwner;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeString(name);
        dest.writeInt(date);
        dest.writeInt(size);
        dest.writeByte((byte) (checked ? 1 : 0));
    }
}
