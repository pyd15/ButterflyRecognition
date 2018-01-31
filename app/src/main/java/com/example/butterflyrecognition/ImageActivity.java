package com.example.butterflyrecognition;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.butterflyrecognition.Util.HttpUtil;
import com.example.butterflyrecognition.db.InfoDetail;
import com.example.butterflyrecognition.fragment.DIYDialog;
import com.example.butterflyrecognition.view.clip.ClipImageLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    ClipImageLayout butterflyImageLayout;
    Toolbar toolbar;
    TextView butterflyContentText;

    String image_camera = null;
    String image_album = null;
    //        String uploadUrl = "http://120.78.72.153:8080/btf/identify.do";
    String uploadUrl = "http://40.125.207.182:8080/identify.do";

    Bitmap clipBitmap;
    File clipImage;
    private ProgressDialog progressDialog;

    private IntentFilter intentFilter;
    private NetworkChangeReciver networkChangeReciver;

    boolean camera_flag = false;
    boolean album_flag = false;
    Gson gson = new Gson();

    public static final String IMAGE_URI_CAMERA = "imagePath_camera";
    public static final String IMAGE_URI_ALBUM = "imagePath_Album";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //                if (Build.VERSION.SDK_INT >= 21) {
        //                    View decorView = getWindow().getDecorView();
        //                    decorView.setSystemUiVisibility(
        //                            View.SYSTEM_UI_FLAG_FULLSCREEN |
        //                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
        //                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
        //                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
        //                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        //                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        //                                    View.SYSTEM_UI_FLAG_IMMERSIVE
        //                                    |
        //                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        //                    );
        //                    getWindow().setStatusBarColor(Color.parseColor("#00889a"));
        //                }

        getWindow().getDecorView().setSystemUiVisibility(
                //                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                //                                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        //                                                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        //                                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        //                |View.SYSTEM_UI_FLAG_VISIBLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        //        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_image);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions =
                        //                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        //                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        //                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= 0x00001000;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar_image);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        butterflyImageLayout = (ClipImageLayout) findViewById(R.id.butterfly_image_view);
        butterflyContentText = (TextView) findViewById(R.id.butterfly_content_text);
        //        LinearLayoLinearLayout hintLayout = (LinearLayout) findViewById(R.id.hint_layout);ut hintLayout = (LinearLayout) findViewById(R.id.hint_layout);
        FrameLayout hintLayout = (FrameLayout) findViewById(R.id.hint_layout);

        cancelbtn = (Button) findViewById(R.id.cancel);
        use_photo = (Button) findViewById(R.id.use_photo);
        cancelbtn.setOnClickListener(this);
        use_photo.setOnClickListener(this);

        //        DIYPopWin mPop=new DIYPopWin(ImageActivity.this,1);
        //        mPop.showAtLocation(ImageActivity.this.findViewById(R.id.image_ll), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 50, 50);

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
                //                butterflyImageLayout.setImagePath(image_camera);
                Log.d("image_camera_parse", Uri.parse(image_camera).getPath());
                butterflyImageLayout.setImagePath(Uri.parse(image_camera).getPath());
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

        String butterflyContent = "双指移动并缩放一只蝴蝶至方框内";
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
                    showProgressDialog();
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
                            clipBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);//将clipBitmap写入clipImage中
                            fos.flush();
                            fos.close();
                            Log.d("image_path_cut", clipImage.getPath());
                        } else {
                            Toast.makeText(this, "asdfsaf", Toast.LENGTH_SHORT).show();
                        }
                        if (clipImage.getPath() != null) {
                            Log.d("image_path_before", clipImage.getPath());
                            Toast.makeText(ImageActivity.this, "裁剪成功！", Toast.LENGTH_SHORT).show();
                        }

                        //      HttpUtil.sendOkHttpPicture(uploadUrl,image_camera, new Callback() {
                        HttpUtil.sendOkHttpPicture(uploadUrl, clipImage.getPath(), new Callback() {
                            @Override
                            public void
                            onResponse(Call call, Response response) throws IOException {
                                final String responeData = response.body().string();
                                Log.d("imageUpload-response", responeData);
                                final InfoDetail butterflyInfo1 = gson.fromJson(responeData, new TypeToken<InfoDetail>() {
                                }.getType());
                                if (responeData != null) {
                                    ImageActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            closeProgressDialog();
                                            //                                            Toast.makeText(ImageActivity.this, "Upload Picture succeed! Name:" + responeData, Toast.LENGTH_LONG).show();
                                            Log.d("response", responeData);
                                            Log.d("image_path", clipImage.getPath());
                                            //ResultDialog resultDialog = new ResultDialog(ImageActivity.this, clipImage.getPath());
                                            camera_flag = false;
                                            DIYDialog resultDialog = new DIYDialog(ImageActivity.this, clipImage.getPath(), butterflyInfo1.getId());//butterflyInfo1.getId()
                                            //                                            resultDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
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
                    showProgressDialog();
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
                        Log.d("image_bitmap", clipBitmap.toString());
                        if (clipBitmap != null) {

                            clipBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                            Log.d("image_path_fos", clipImage.getPath());
                        } else {
                            Toast.makeText
                                    (this, "asdfsaf", Toast.LENGTH_SHORT).show();
                        }
                        if (clipImage.getPath() != null) {
                            Log.d("image_path_before_album", clipImage.getPath());
                            Toast.makeText(ImageActivity.this, "裁剪成功！", Toast.LENGTH_SHORT).show();
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
                                final InfoDetail butterflyInfo1 = gson.fromJson(responeData, new TypeToken<InfoDetail>() {
                                }.getType());
                                if (responeData != null) {
                                    ImageActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            closeProgressDialog();
                                            //                                            Toast.makeText(ImageActivity.this, "Upload Picture succeed! Name:" + responeData, Toast.LENGTH_LONG).show();
                                            Log.d("image_path", clipImage.getPath());
                                            //                                            ResultDialog resultDialog = new ResultDialog(ImageActivity.this, clipImage.getPath());
                                            album_flag = false;
                                            //                                            DIYPopWin mPop=new DIYPopWin(ImageActivity.this,butterflyInfo1.getId());
                                            //                                            mPop.showAtLocation(ImageActivity.this.findViewById(R.id.image_ll), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                                            DIYDialog resultDialog1 = new DIYDialog(ImageActivity.this, clipImage.getPath(), butterflyInfo1.getId());//
                                            //                                                                                        NoneDialog resultDialog1=new NoneDialog(ImageActivity.this, clipImage.getPath(), id);
                                            //                                            resultDialog1.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                            resultDialog1.setCancelable(false);
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
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ImageActivity.this);
            progressDialog.setMessage("识别中，请稍后...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
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

    /**
     * 设置顶部及底部的沉浸式,这个方法需要子类来调，必须在setContentView()之后调用
     */
    public void setTranslucentBar(final Toolbar toolbar, final View navigationView, int barColor) {
        //当手机的版本在4.4的时候，设置透明之后，再设置高度，然后再设置显示的颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (toolbar != null) {
                toolbar.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
                        layoutParams.height += getBarHeight("status_bar_height");
                        toolbar.setLayoutParams(layoutParams);
                    }
                });

                toolbar.setBackgroundColor(barColor);
            }
            if (navigationView != null) {
                //这时还要判断当前手机有没有底部虚拟导航栏，如果没有的话，我们不做处理
                if (hasNavigationView(getWindowManager())) {
                    navigationView.post(new Runnable() {
                        @Override
                        public void run() {
                            ViewGroup.LayoutParams layoutParams = navigationView.getLayoutParams();
                            layoutParams.height += getBarHeight("navigation_bar_height");
                            navigationView.setLayoutParams(layoutParams);
                        }
                    });
                    navigationView.setBackgroundColor(barColor);
                }
            }
            //5.0及以上
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(barColor);
            getWindow().setNavigationBarColor(barColor);
            toolbar.setBackgroundColor(barColor);
        } else {
            //4.4以下，没有沉浸式这一说，我们不做任何处理
        }
    }

    /**
     * 判断当前手机是否含有底部导航栏，如果没有的话返回false，有的话返回true
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean hasNavigationView(WindowManager wm) {
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getRealMetrics(outMetrics);
        //获取屏幕实际的高度，实际的高度包括内容的高度和底部导航的高度
        int realHeight = outMetrics.heightPixels;

        outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        //屏幕中内容的高度
        int heightPixels = outMetrics.heightPixels;
        //如果实际的高度和大于内容的高度说明该手机含有底部导航，否则没有
        return realHeight - heightPixels > 0;
    }

    /**
     * 获取状态栏或是底部导航栏的高度
     *
     * @return
     */
    public int getBarHeight(String dimenName) {
        int height = 0;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            String s = clazz.getField(dimenName).get(object).toString();
            int heightId = Integer.parseInt(s);
            //dp 2 px
            height = getResources().getDimensionPixelSize(heightId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }
}

