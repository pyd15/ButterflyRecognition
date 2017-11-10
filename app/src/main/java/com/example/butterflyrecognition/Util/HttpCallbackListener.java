package com.example.butterflyrecognition.Util;

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

}