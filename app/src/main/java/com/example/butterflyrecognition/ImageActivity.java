package com.example.butterflyrecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.butterflyrecognition.Util.HttpUtil;
import com.example.butterflyrecognition.fragment.ResultDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import snackbar.SnackBarUtil;

/**
 * Created by Dr.P on 2017/10/10.
 */

public class ImageActivity extends AppCompatActivity implements View.OnClickListener {

    //    @BindView(R.id.toolbar_image)
    //    Toolbar toolbar;

    //    @BindView(R.id.butterfly_image_view)
    //    ImageView butterflyImageView;

    //    @BindView(R.id.butterfly_content_text)
    //    TextView butterflyContentText;
    //
    //    @BindView(R.id.coordinator_layout)
    //    CoordinatorLayout coordinatorLayout;
    //
    //    @BindView(R.id.image_collapsing_toolbar)
    //    CollapsingToolbarLayout collapsingToolbarLayout;

    Button cancelbtn;
    Button use_photo;
    ImageView butterflyImageView;
    Toolbar toolbar;
    TextView butterflyContentText;

    String image_camera = null;
    String image_album = null;
    String uploadUrl = "http://120.78.72.153:8080/btf/identify.do";

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
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        butterflyImageView = (ImageView) findViewById(R.id.butterfly_image_view);
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
        //        Uri imageUri2 = Uri.parse(image_camera);
        //        Log.d("image_album", image_album);
        //        Log.d("path", image_camera);
        //                Glide.with(this).load(getImagePath(imageUri,null)).into(butterflyImageView);

        if (image_camera != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri1));
                butterflyImageView.setImageBitmap(bitmap);//将裁剪后的照片显示出来
                //                Glide.with(this).load(imageUri1).into(butterflyImageView);
                //                Glide.with(this).load(image_album).override(700,870).fitCenter().into(butterflyImageView);
                camera_flag = true;
            } catch (FileNotFoundException e) {
                Log.d("image1FileNotFound", imageUri1.toString());
            }
        } else if (image_album != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(image_album);
            //            butterflyImageView.setImageBitmap(bitmap);
            Glide.with(this).load(image_album).into(butterflyImageView);
            //            Glide.with(this).load(image_album).override(1700,1870).into(butterflyImageView);
            album_flag = true;
            // new BitmapImageViewTarget(butterflyImageView) {
            //                @Override
            //                protected void setResource(Bitmap resource) {
            //                    super.setResource(resource);
            //                    int width = resource.getWidth();
            //                    int height = resource.getHeight();
            //                    //获取imageView的宽
            //                    int imageViewWidth=butterflyImageView.getWidth();
            //                    //计算缩放比例
            //                    float sy= (float) (imageViewWidth* 0.1)/(float) (width * 0.1);
            //                    //计算图片等比例放大后的高
            //                    int imageViewHeight= (int) (height * sy);
            //                    ViewGroup.LayoutParams params =butterflyImageView.getLayoutParams();
            //                    params.height = imageViewHeight;
            //                    .setLayoutParams(params);
            //                }
            //            });
            //            try {
            //
            //                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri2));
            //                butterflyImageView.setImageBitmap(bitmap);//将裁剪后的照片显示出来
            //            } catch (FileNotFoundException e) {
            //                            Log.d("image2FileNotFound", imageUri2.toString());
            //            }
        } else {
            Toast.makeText(this, "Failed to get image!", Toast.LENGTH_SHORT).show();
        }

        String butterflyContent = "双指移动并缩放一只蝴蝶至虚线框内";
        butterflyContentText.setText(butterflyContent);
        butterflyContentText.setGravity(Gravity.CENTER_VERTICAL);

        final Snackbar snackbar = Snackbar.make(hintLayout, "", Snackbar.LENGTH_INDEFINITE);

        //设置snackbar自带的按钮监听器
        snackbar.setAction(R.string.string, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ImageActivity.this, "照片发送成功!", Toast.LENGTH_SHORT).show();
                snackbar.dismiss();
            }
        });

        //添加新按钮
        SnackBarUtil.SnackbarAddView(snackbar, R.layout.snackbarbtn, 0);
        //设置新添加的按钮监听器
        SnackBarUtil.SetAction(snackbar, R.id.cancel_btn, "取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ImageActivity.this, "照片发送失败!", Toast.LENGTH_SHORT).show();
                snackbar.dismiss();
            }
        });
        //        snackbar.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                onBackPressed();
                break;
            case R.id.use_photo:
                //                replaceFragment(new AnotherFragment());
                //                ResultDialog resultDialog = new ResultDialog(ImageActivity.this);
                //                resultDialog.setCancelable(false);
                //                resultDialog.show();
                if (camera_flag) {
                    try {
                        HttpUtil.sendOkHttpPicture(uploadUrl, image_camera, new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String responeData = response.body().string();
                                Log.d("imageUpload-response", responeData);
                                if (responeData != null) {
                                    ImageActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ImageActivity.this, "Upload Picture succeed! Name:" + responeData, Toast.LENGTH_LONG).show();
                                            ResultDialog resultDialog = new ResultDialog(ImageActivity.this);
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
                                        Toast.makeText(ImageActivity.this, "Upload Picture failed!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {
                    }
                } else if (album_flag) {
                    try {
                        HttpUtil.sendOkHttpPicture(uploadUrl, image_album, new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String responeData = response.body().string();
                                Log.d("imageUpload-response", responeData);
                                if (responeData != null) {
                                    ImageActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ImageActivity.this, "Upload Picture succeed! Name:" + responeData, Toast.LENGTH_LONG).show();
                                            ResultDialog resultDialog = new ResultDialog(ImageActivity.this);
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
                                        Toast.makeText(ImageActivity.this, "Upload Picture failed!", Toast.LENGTH_LONG).show();
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
    public String put(MediaType mediaType, String uploadUrl, String localPath) throws IOException {
        File file = new File(localPath);
        RequestBody body = RequestBody.create(mediaType, file);
        Request request = new Request.Builder()
                .url(uploadUrl)
                .put(body)
                .build();
        //修改各种 Timeout
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        //如果不需要可以直接写成 OkHttpClient client = new OkHttpClient.Builder().build();

        Response response = client
                .newCall(request)
                .execute();
        return response.body().string() + ":" + response.code();
    }

    //上传JPG图片
    public String putImg(String uploadUrl, String localPath) throws IOException {
        MediaType imageType = MediaType.parse("image/jpg; charset=utf-8");
        return put(imageType, uploadUrl, localPath);
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.hint_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        super.onBackPressed();
    }
}
