package com.example.butterflyrecognition.Util;

import android.util.Log;

import com.example.butterflyrecognition.db.ButterflyInfo;
import com.example.butterflyrecognition.db.InfoDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Dr.P on 2017/11/10.
 * runas /user:Dr.P "cmd /k"
 */

public class HttpAction {


    private void sendRequestWithHttpURLConnection() {
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://120.78.72.153:8080/ButterflySystem/getInfo.do");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(80000);
                    connection.setReadTimeout(80000);
                    InputStream in = connection.getInputStream();
                    // 下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
//                    showResponse(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址是电脑本机
                            .url("http://120.78.72.153:8080/ButterflySystem/getInfo.do")
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                    //                                        parseJSONWithJSONObject(responseData);
                    //                    parseXMLWithSAX(responseData);
                    //                    parseXMLWithPull(responseData);
                    //                                        showResponse(responseData);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
//        Log.d("Flagout", flag[0].toString());
    }


    public static void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String imageUrl = jsonObject.getString("image");
                String name = jsonObject.getString("name");
                String latinName = jsonObject.getString("latinName");
                String type = jsonObject.getString("type");
                String feature = jsonObject.getString("feature");
                String area = jsonObject.getString("area");
                String protect = jsonObject.getString("protect");
                String rare = jsonObject.getString("rare");
                String uniqueToChina = jsonObject.getString("uniqueToChina");
                int id=jsonObject.getInt("id");
                showInfo("MainActivity", "id is " +
                        id, "name is " + name, "latinName is " +
                        latinName, "type is " + type, "feature is " +
                        feature, "area is " + area, "rare is " +
                        rare, "uniqueToChina is " +
                        uniqueToChina, "imageUrl is " + imageUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean parseJSONWithGSON(String jsonData) throws ExecutionException, InterruptedException {
        try {
            Boolean flag = false;
            Gson gson = new Gson();
            ButterflyInfo butterflyInfo1 = gson.fromJson(jsonData, new TypeToken<ButterflyInfo>() {
            }.getType());
            //            Log.e("imagebutterflyInfo1", butterflyInfo1.setToDefault());
            List<InfoDetail> butterflyInfoList = butterflyInfo1.infoDetailList;
            Connector.getDatabase();
            for (InfoDetail butterflyInfo : butterflyInfoList) {
                butterflyInfo.setImageUrl("http://120.78.72.153:8080" + butterflyInfo.getImageUrl());
                butterflyInfo.setImagePath((String) new DownImage().execute(butterflyInfo.getImageUrl()).get());
                Log.d("FilePath", butterflyInfo.getImagePath());
                butterflyInfo.save();
                if (butterflyInfo.save()) {
                    flag = true;
                }
                Log.d("flag", flag.toString());
                showInfo("Activity", "id is " +
                        butterflyInfo.getId(), "name is " +
                        butterflyInfo.getName(), "latinName is " +
                        butterflyInfo.getLatinName(), "type is " +
                        butterflyInfo.getType(), "feature is " +
                        butterflyInfo.getFeature(), "area is " +
                        butterflyInfo.getArea(), "rare is " +
                        butterflyInfo.getRare(), "uniqueToChina is " +
                        butterflyInfo.getUniqueToChina(), "imageUrl is " +
                        butterflyInfo.getImageUrl());
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void showInfo(String activity, String msg, String msg2, String msg3, String msg4, String msg5, String msg6, String msg7, String msg8, String msg9) {
        Log.d(activity, msg);
        Log.d(activity, msg2);
        Log.d(activity, msg3);
        Log.d(activity, msg4);
        Log.d(activity, msg5);
        Log.d(activity, msg6);
        Log.d(activity, msg7);
        Log.d(activity, msg8);
        Log.d(activity, msg9);
    }

}


//            butterflyInfo.setLatinName(butterflyInfo.getLatinName());
//            butterflyInfo.setFeature(butterflyInfo.getFeature());
//            butterflyInfo.setArea(butterflyInfo.getArea());
//            butterflyInfo.setId(butterflyInfo.getId());
//            butterflyInfo.setProtect(butterflyInfo.getProtect());
//            butterflyInfo.setRare(butterflyInfo.getRare());
//            butterflyInfo.setImageUrl(butterflyInfo.getImageUrl());
//            butterflyInfo.setUniqueToChina(butterflyInfo.getUniqueToChina());