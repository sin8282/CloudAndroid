package com.example.shucloud.api;


import androidx.annotation.NonNull;

import com.example.shucloud.Service.RetrofitInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ApiClient {
    private static Retrofit bulkRequestRetrofit;
    private static Retrofit menuRequestRetrofit;
    private static final String BASE_URL_API = "";

    private static final Gson gson = new GsonBuilder().setLenient().create();

    //static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

    public static Retrofit getInstance(String type, int port) {
        //interceptor.level(HttpLoggingInterceptor.Level.BODY);
        Retrofit retrofit;
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder();
                        builder.addHeader("Content-Type","application/json; charset=utf-8");
                        builder.addHeader("Accept","application/json; charset=utf-8");

                        builder.method(original.method(), original.body());
                        Request request = builder.build();
                        Response response = chain.proceed(request);
                        return response.newBuilder().build();
                    }
                })
                //.addInterceptor(interceptor)
                //.retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5,TimeUnit.MINUTES).build();
        switch (type){
            case "bulkRequestRetrofit" :
                retrofit = bulkRequestRetrofit;
                break;
            case "menuRequestRetrofit" :
            default : retrofit = menuRequestRetrofit;
                break;
        }
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL_API+":"+port).client(client)
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static RetrofitInterface getApiService(String type,int port){
        return getInstance(type, port).create(RetrofitInterface.class);
    }

    public static RetrofitInterface getApiService(String type){
        return getInstance(type,4029).create(RetrofitInterface.class);
    }

}

