package com.example.shucloud.Custom;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Response;

public abstract class RxjavaCustomCallback implements SingleObserver {

    private final int mRetryLimitCount;
    private int mRetryCount = 0;

    public RxjavaCustomCallback() {
        this.mRetryLimitCount = 3;
    }

    public RxjavaCustomCallback(int retryLimitCount) {
        this.mRetryLimitCount = retryLimitCount;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onSuccess(Object o) {

    }


}
