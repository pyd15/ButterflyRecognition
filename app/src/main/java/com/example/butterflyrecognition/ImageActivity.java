package com.example.butterflyrecognition;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.butterflyrecognition.Util.HttpUtil;
import com.example.butterflyrecognition.fragment.DIYDialog;
import com.example.butterflyrecognition.view.clip.ClipImageLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import com.githang.clipimage.ClipImageView;

/**
 * Created by Dr.P on 2017/10/10.
 */

public class ImageActivity extends AppCompatActivity implements View.OnClickListener {

    Button cancelbtn;
    Button use_photo;
    //    ZoomImageView butterflyImageView;
    //    ClipImageView butterflyImageView;
    //    ClipImageView1 butterflyImageView;
    ClipImageLayout butterflyImageLayout;
    Toolbar toolbar;
    TextView butterflyContentText;

    String image_camera = null;
    String image_album = null;
    String uploadUrl = "http://120.78.72.153:8080/btf/identify.do";

    Bitmap clipBitmap;
    File clipImage;

    private IntentFilter intentFilter;
    private NetworkChangeReciver networkChangeReciver;

    boolean camera_flag = false;
    boolean album_flag = false;

    public static final String BUTTERFLY_NAME = "butterfly_image";
    public static final String BUTTERFLY_IMAGE_ID = "butterfly_image_id";
    public static final String IMAGE_URI_CAMERA = "imagePath_camera";
    public static final String IMAGE_URI_ALBUM = "imagePath_Album";
    public static final String IMAGE = "imagePath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //        if (Build.VERSION.SDK_INT >= 21) {
        //            View decorView = getWindow().getDecorView();
        //            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        //            getWindow().setStatusBarColor(Color.TRANSPARENT);
        //        }
        setContentView(R.layout.activity_image);

        toolbar = (Toolbar) findViewById(R.id.toolbar_image);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //        butterflyImageLayout = (ZoomImageView) findViewById(R.id.butterfly_image_view);
        //        butterflyImageLayout = (ClipImageView) findViewById(R.id.butterfly_image_view);
        //        butterflyImageLayout = (ClipImageView1) findViewById(R.id.butterfly_image_view);
        butterflyImageLayout = (ClipImageLayout) findViewById(R.id.butterfly_image_view);
        butterflyContentText = (TextView) findViewById(R.id.butterfly_content_text);
        //        LinearLayoLinearLayout hintLayout = (LinearLayout) findViewById(R.id.hint_layout);ut hintLayout = (LinearLayout) findViewById(R.id.hint_layout);
        FrameLayout hintLayout = (FrameLayout) findViewById(R.id.hint_layout);

        cancelbtn = (Button) findViewById(R.id.cancel);
        use_photo = (Button) findViewById(R.id.use_photo);
        cancelbtn.setOnClickListener(this);
        use_photo.setOnClickListener(this);

        Intent intent = getIntent();
        Uri imageUri1 = null;
        if (intent.getStringExtra(IMAGE_URI_CAMERA) != null) {
            image_camera = intent.getStringExtra(IMAGE_URI_CAMERA);
            imageUri1 = Uri.parse(image_camera);
            Log.d("image_camera", image_camera);
        }
        if (intent.getStringExtra(IMAGE_URI_ALBUM) != null) {
            image_album = intent.getStringExtra(IMAGE_URI_ALBUM);
            Log.d("image_album", image_album);
        }
        //                Glide.with(this).load(getImagePath(imageUri,null)).into(butterflyImageLayout);

        if (image_camera != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri1));
                //                butterflyImageLayout.setImageBitmap(bitmap);//将裁剪后的照片显示出来
                //                butterflyImageLayout = new ClipImageLayout(this, null, image_camera);
                butterflyImageLayout.setImagePath(image_camera);
                //                                Glide.with(this).load(imageUri1).into(butterflyImageLayout.mZoomImageView);
                //                Glide.with(this).load(image_album).override(700,870).fitCenter().into(butterflyImageLayout);
                camera_flag = true;
            } catch (FileNotFoundException e) {
                Log.d("image1FileNotFound", imageUri1.toString());
            }
        } else if (image_album != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(image_album);
            //            Bitmap bitmap = BitmapFactory.decodeFile(turnFilePath);
            //            butterflyImageLayout.setImageBitmap(bitmap);
            //            butterflyImageLayout = new ClipImageLayout(this, null, image_album);
            butterflyImageLayout.setImagePath(image_album);
            //            Glide.with(this).load(image_album).into(butterflyImageLayout);
            //            Glide.with(this).load(image_album).override(1700,1870).into(butterflyImageLayout);
            album_flag = true;
        } else {
            Toast.makeText(this, "Failed to get image!", Toast.LENGTH_SHORT).show();
        }

        String butterflyContent = "双指移动并缩放一只蝴蝶至虚线框内";
        butterflyContentText.setText(butterflyContent);
        butterflyContentText.setGravity(Gravity.CENTER_VERTICAL);

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReciver = new NetworkChangeReciver();
        registerReceiver(networkChangeReciver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载toolbar菜单文件
        getMenuInflater().inflate(R.menu.image_toolbar, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                onBackPressed();
                break;
            case R.id.use_photo:
                if (camera_flag) {
                    try {
                        clipImage = new File
                                ("/sdcard/DCIM/Screenshots/clip.jpg");//设置文件名称
                        if (clipImage.exists()) {
                            clipImage.delete();
                        }

                        clipImage.createNewFile();
                        FileOutputStream fos = new FileOutputStream(clipImage);

                        //                        clipBitmap = butterflyImageLayout.getClippedBitmap();
                        clipBitmap = butterflyImageLayout.clip();
                        if (clipBitmap != null) {
                            clipBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                        } else {
                            Toast.makeText(this, "asdfsaf", Toast.LENGTH_SHORT).show();
                        }
                        if (clipImage.getPath() != null) {
                            Log.d("image_path", clipImage.getPath());
                        }

                        //      HttpUtil.sendOkHttpPicture(uploadUrl,image_camera, new Callback() {
                        HttpUtil.sendOkHttpPicture(uploadUrl, clipImage.getPath(), new Callback() {
                            @Override
                            public void
                            onResponse(Call call, Response response) throws IOException {
                                final String responeData = response.body().string();
                                Log.d("imageUpload-response", responeData);
                                if (responeData != null) {
                                    ImageActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ImageActivity.this, "Upload Picture succeed! Name:" + responeData, Toast.LENGTH_LONG).show();
                                            Log.d("image_path", clipImage.getPath());
                                            int id = 1;
                                            //                                            ResultDialog resultDialog = new ResultDialog(ImageActivity.this, clipImage.getPath());
                                            DIYDialog resultDialog = new DIYDialog(ImageActivity.this, clipImage.getPath(), id);
                                            resultDialog.setCancelable(false);
                                            resultDialog.show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call call, IOException e) {
                                ImageActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ImageActivity.this, "Upload Picture failed!+" + clipImage.getPath(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                } else if (album_flag) {
                    try {
                        clipImage = new File
                                ("/sdcard/DCIM/Screenshots/clip.jpg");//设置文件名称
                        if (clipImage.exists()) {
                            clipImage.delete();
                        }

                        clipImage.createNewFile();
                        FileOutputStream fos = new FileOutputStream(clipImage);

                        //                        clipBitmap = butterflyImageLayout.getClippedBitmap();
                        clipBitmap = butterflyImageLayout.clip();
                        if (clipBitmap != null) {

                            clipBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                        } else {
                            Toast.makeText
                                    (this, "asdfsaf", Toast.LENGTH_SHORT).show();
                        }
                        if
                                (clipImage.getPath() != null) {
                            Log.d
                                    ("image_path", clipImage.getPath());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        // HttpUtil.sendOkHttpPicture(uploadUrl,image_album, new Callback() {
                        HttpUtil.sendOkHttpPicture(uploadUrl, clipImage.getPath(), new Callback() {
                            @Override
                            public void
                            onResponse(Call call, Response response) throws IOException {
                                final String
                                        responeData = response.body().string();
                                Log.d("imageUpload-response", responeData);
                                if (responeData != null) {
                                    ImageActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ImageActivity.this, "Upload Picture succeed! Name:" + responeData, Toast.LENGTH_LONG).show();
                                            Log.d("image_path", clipImage.getPath());
                                            //                                            ResultDialog resultDialog = new ResultDialog(ImageActivity.this, clipImage.getPath());
                                            int id = 1;
                                            DIYDialog resultDialog1 = new DIYDialog(ImageActivity.this, clipImage.getPath(), id);
                                            resultDialog1.setCancelable(false);
                                            Toast.makeText(ImageActivity.this, "What the hell!", Toast.LENGTH_LONG).show();
                                            resultDialog1.show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void
                            onFailure(Call call, IOException e) {

                                ImageActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ImageActivity.this, "UploadPicture failed!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {
                    }
                }
                break;
        }
    }

    /**
     * @param mediaType MediaType
     * @param uploadUrl put请求地址
     * @param localPath 本地文件路径
     * @return 响应的结果 和 HTTP status code
     * @throws IOException
     */
    public String put(MediaType mediaType,
                      String uploadUrl, String localPath) throws
            IOException {
        File file = new File(localPath);
        RequestBody body =
                RequestBody.create(mediaType, file);
        Request request = new
                Request.Builder()
                .url(uploadUrl)
                .put(body)
                .build();
        //修改各种 Timeout
        OkHttpClient client = new
                OkHttpClient.Builder()
                .connectTimeout(600,
                        TimeUnit.SECONDS)
                .readTimeout(200,
                        TimeUnit.SECONDS)
                .writeTimeout(600,
                        TimeUnit.SECONDS)
                .build();
        //如果不需要可以直接写成 OkHttpClient
        client = new OkHttpClient.Builder().build();

        Response response = client
                .newCall(request)
                .execute();
        return response.body().string() + ":"
                + response.code();
    }

    //上传JPG图片
    public String putImg(String uploadUrl,
                         String localPath) throws IOException {
        MediaType imageType =
                MediaType.parse("image/jpg; charset=utf-8");
        return put(imageType, uploadUrl,
                localPath);
    }


    private void replaceFragment(Fragment
                                         fragment) {
        FragmentManager fragmentManager =
                getSupportFragmentManager();
        FragmentTransaction transaction =
                fragmentManager.beginTransaction();
        transaction.replace(R.id.hint_layout,
                fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected
            (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition
                (android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReciver);
    }

    class NetworkChangeReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
            } else {
                Toast.makeText(context, "无法连接至服务器，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

