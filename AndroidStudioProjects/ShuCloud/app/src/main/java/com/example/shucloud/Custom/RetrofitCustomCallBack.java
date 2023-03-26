package com.example.shucloud.Custom;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RetrofitCustomCallBack<T> implements Callback<T> {

    private final int mRetryLimitCount;
    private int mRetryCount = 0;

    public RetrofitCustomCallBack() {
        this.mRetryLimitCount = 3;
    }

    public RetrofitCustomCallBack(int retryLimitCount) {
        this.mRetryLimitCount = retryLimitCount;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        onFinalResponse(call, response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        mRetryCount++;
        if (mRetryCount > mRetryLimitCount) {
            onFinalFailure(call, t);
            return;
        }

        retry(call);
    }

    private void retry(Call<T> call) {
        call.clone().enqueue(this);
    }

    public abstract void onFinalFailure(Call<T> call, Throwable t);
    public abstract void onFinalResponse(Call<T> call, Response<T> response);
}