package com.example.shucloud.api;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class ContentUriRequestBody extends RequestBody {

    ContentResolver contentResolver;
    private Uri uri;
    private long size = -1L;
    private String name;
    private String mediaType;
    private boolean thumbNail;

    public ContentUriRequestBody(Uri uri, ContentResolver contentResolver){
        this.uri=uri;
        this.contentResolver = contentResolver;
        try(Cursor cursor=contentResolver.query(uri, null, null, null, null)){
            cursor.moveToFirst();
            //size=cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE));
            name=cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        }

    }
    public ContentUriRequestBody(Uri uri, ContentResolver contentResolver, boolean thumbNail){
        this.uri=uri;
        this.contentResolver = contentResolver;
        try(Cursor cursor=contentResolver.query(uri, null, null, null, null)){
            cursor.moveToFirst();
            //size=cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE));
            name=cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        }

    }

    @Override
    public long contentLength() throws IOException {
        return size;
    }

    @Override
    public MediaType contentType(){
        //Log.i("mylog ::", String.valueOf(MediaType.get(contentResolver.getType(uri))));
        mediaType = String.valueOf(MediaType.get(contentResolver.getType(uri)));
        return MediaType.get(contentResolver.getType(uri));
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
       /*
       이방식을 사용하면 jpg일때만 사진이 돌아가는 현상이 있어서 bitmap도 추가로 선별해서 작업한다.
       이때 size = -1L로 해야 size체크 없이 진행한다.
       (이상하게 사이즈가 안맞는다... 아마 가로 세로 변화 때문일듯?)
       */
        if(mediaType.indexOf("jpeg") == -1){
            try(Source source=Okio.source(contentResolver.openInputStream(uri))){
                sink.writeAll(source);
            }
        }else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                Bitmap bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri));
                if(!thumbNail) bitmap.compress(Bitmap.CompressFormat.JPEG, 80, sink.outputStream());
                else {
                    int height=bitmap.getHeight();
                    int width=bitmap.getWidth();
                    bitmap.createScaledBitmap(bitmap, 160, height/(width/160), true);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, sink.outputStream());
                }

                bitmap.recycle();
            }
        }
    }

    public String getFileName(){
        return name;
    }
}