package com.example.shucloud.SVO;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class InstanceVO {

    public static final Gson gson = new Gson();

    public void setInstanceVO(Context context, String key, Object value){
        try(FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE)){
            fos.write(gson.toJson(value).getBytes(StandardCharsets.UTF_8));
        }catch(Exception e){
            Log.i("InstanceVO", e.getMessage());
        }
    }

    public String getInstanceVO(Context context, String key, boolean wantDelete) throws FileNotFoundException {

        FileInputStream fis = context.openFileInput(key);
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        } finally {
            if(wantDelete){
                File file = new File(context.getFilesDir(), key);
                file.delete();
            }
            return stringBuilder.toString();
        }
    }
/*    ImgViewVO[] mcArray = gson.fromJson(stringBuilder.toString(), ImgViewVO[].class);
    List<ImgViewVO> mcList = Arrays.asList(mcArray);*/

}
