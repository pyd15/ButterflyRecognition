package com.example.butterflyrecognition.Util;


import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        // 回调onFinish()方法
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        // 回调onError()方法
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void sendOkHttpRequest(final String address, final okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpPicture(final String uploadUrl, final String localPath, final okhttp3.Callback callback) throws IOException {
        //修改各种 Timeout
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        Log.d("image_file", localPath);
        Log.d("image_file", Uri.parse(localPath).getPath());
        File file = new File(Uri.parse(localPath).getPath());

        MediaType imageType = MediaType.parse("image/jpg; charset=utf-8");
        RequestBody fileBody = RequestBody.create(imageType, file);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                ///                .addPart(
                //                        Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + fileName + "\""),
                //                        RequestBody.create(MEDIA_TYPE_PNG, file))
                //                .addPart(
                //                        Headers.of("Content-Disposition", "form-data; name=\"imagetype\""),
                //                        RequestBody.create(null, imageType))
                //                .addPart(
                //                        Headers.of("Content-Disposition", "form-data; name=\"userphone\""),
                //                        RequestBody.create(null, userPhone))

                .addFormDataPart("file", "head_image", fileBody)
                //                .addFormDataPart("imagetype", imageType)
                //                .addFormDataPart("userphone", userPhone)
                .build();
        //                = RequestBody.create(imageType, file);
        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);


        //如果不需要可以直接写成 OkHttpClient client = new OkHttpClient.Builder().build();

        //        Response response = client
        //                .newCall(request)
        //                .execute();
        //        return response.body().string() + ":" + response.code();
    }

    /**
     * 从网络中获取图片，以流的形式返回
     * @return
     */
    public static InputStream getImageViewInputStream() throws IOException {
        InputStream inputStream = null;
        URL url = new URL("http://120.78.72.153:8080/ButterflySystem/images/1.jpg");                    //服务器地址
        if (url != null) {
            //打开连接
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(8000);//设置网络连接超时的时间为3秒
            httpURLConnection.setRequestMethod("GET");        //设置请求方法为GET
            httpURLConnection.setDoInput(true);                //打开输入流
            int responseCode = httpURLConnection.getResponseCode();    // 获取服务器响应值
            if (responseCode == HttpURLConnection.HTTP_OK) {        //正常连接
                inputStream = httpURLConnection.getInputStream();        //获取输入流
            }
        }
        return inputStream;
    }

}
