package com.example.butterflyrecognition;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.example.butterflyrecognition.Util.HttpAction;
import com.example.butterflyrecognition.Util.HttpUtil;
import com.example.butterflyrecognition.recycleView.ButterflyActivity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Dr.P on 2017/10/10.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CROP_PHOTO_FORCAMERA = 3;
    public static final int CROP_PHOTO_FORALBUM = 4;
    private Uri imageUri = null;
    Uri imageUri1 = null;
    private String imagePath;
    ScaleAnimation scaleAnimation;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button takePhoto;
    private Button choosePhoto;
    private Button search;

    private ProgressDialog progressDialog;

    String address = "http://120.78.72.153:8080/btf/getInfo.do";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10);
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.drawmenu);
        }
        actionBar.setElevation(100f);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);


        navigationView.setCheckedItem(R.id.nav_call);//将call菜单设置为默认选中
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //此处用于处理点击任意菜单项后的逻辑
                //                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.nav_call:
                        Toast.makeText(MainActivity.this, "请联系#18927512657#", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_friends:
                        Toast.makeText(MainActivity.this, "#蝴蝶识别项目组#", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_location:
                        Toast.makeText(MainActivity.this, "#当前版本#-1.1", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_mail:
                        Toast.makeText(MainActivity.this, "#拍照或选图上传即可#", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_task:
                        Toast.makeText(MainActivity.this, "#绝不干任何有损用户利益的事#", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        takePhoto = (Button) findViewById(R.id.take_photo);
        choosePhoto = (Button) findViewById(R.id.choose_from_album);
        search = (Button) findViewById(R.id.search);

        scaleAnimation = new ScaleAnimation(0, 2, 0, 2, Animation.RELATIVE_TO_SELF, 1F, Animation.RELATIVE_TO_SELF, 1F);
        scaleAnimation.setDuration(1000);
        //        takePhoto.startAnimation(scaleAnimation);
        //        choosePhoto.startAnimation(scaleAnimation);
        //        search.startAnimation(scaleAnimation);
        takePhoto.setOnClickListener(this);
        choosePhoto.setOnClickListener(this);
        search.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //动态申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo:
                //                        takePhoto.startAnimation(scaleAnimation);//设置点击后缩放效果
                //创建File对象用于存储拍摄的照片
                File outputImage = new File(getExternalCacheDir(), "tempImage.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    //别忘了注册FileProvider内容提供器
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.butterflyrecognition.fileProvider", outputImage);
                    Log.d("imageUri_sdk>24", imageUri.toString());
                } else {
                    imageUri = Uri.fromFile(outputImage);
                    Log.d("imageUri_sdk<24", imageUri.toString());
                }
                //启动相机
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CROP_PHOTO_FORCAMERA);
                break;
            case R.id.choose_from_album:
                //                choosePhoto.startAnimation(scaleAnimation);//设置点击后缩放效果
                openAlbum();
                break;
            case R.id.search:
                //                search.startAnimation(scaleAnimation);//设置点击后缩放效果
                SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                boolean flag = preferences.getBoolean("info", false);
                if (!flag) {
                    try {
                        queryFromServer();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                }
                Intent intent1 = new Intent(MainActivity.this, ButterflyActivity.class);
                startActivity(intent1);
                //                实现淡入淡出的效果1
                //                overridePendingTransition(R.anim.splash_screen_fade, R.anim.splash_screen_hold);
                //                实现淡入淡出的效果2
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                //                由左向右滑入的效果
                //                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                //                int version = android.os.Build.VERSION.SDK_INT;
                //                if(version > 5 ){
                //设置跳转动画
                //                    overridePendingTransition(R.anim.in_from_right_to_center, R.anim.out_from_center_to_left);
                //        }
                break;
            default:
                break;
        }
    }

    private void openAlbum() {
        //        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        Intent intent1 = new Intent(Intent.ACTION_PICK);
        ////                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //        Intent intent1 = new Intent("android.intent.action.GET_CONTENT");
        intent1.setType("image/*");
        setResult(RESULT_OK, intent1);
        startActivityForResult(intent1, CHOOSE_PHOTO);//打开相册
    }

    //获得读取本地存储权限后执行的操作
    //    @Override
    //    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    //        switch (requestCode) {
    //            case 1:
    //                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    //                    openAlbum();
    //                } else {
    //                    Toast.makeText(this, "You denied the permission!", Toast.LENGTH_SHORT).show();
    //                }
    //                break;
    //            default:
    //        }
    //    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //将拍得的照片裁剪后显示出来
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    //                    intent.putExtra("aspectX", 1);
                    //                    intent.putExtra("aspectY", 1);
                    //裁剪的大小
                    //                    intent.putExtra("outputX", 180);
                    //                    intent.putExtra("outputY", 180);
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.putExtra("imagePath_camera", imageUri);
                    startActivityForResult(intent, CROP_PHOTO_FORCAMERA);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {

                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4以上系统用此方法处理图片
                        //                        imageUri1= handleImageOnKitKat(data);
                        imagePath = handleImageOnKitKat(data);
                        imageUri1 = data.getData();
                        Log.d("image above4.4", imagePath);
                        Log.d("image Uri above4.4", imageUri1.toString());
                    } else {
                        //其他系统用此方法处理图片
                        imageUri1 = handleImageBeforeKitKat(data);
                        Log.d("image below4.4", imageUri1.toString());
                        imageUri = imageUri1;
                    }
                    //                    Intent intent = new Intent("com.android.camera.action.CROP");
                    Intent intent = new Intent(this, ImageActivity.class);
                    intent.setDataAndType(imageUri1, "image/*");
                    //                    intent.setType("image/*");
                    intent.putExtra("crop", true);//允许裁剪/
                    intent.putExtra("scale", true);//允许缩放
                    if (imagePath != null) {
                        intent.putExtra("imagePath_Album", imagePath);
                        Log.d("imagePath_Album", imagePath);
                    } else {
                        intent.putExtra("imagePath_Album", imageUri1.getPath());
                        Log.d("imagePath_Album_Uri", imageUri.getPath());
                    }
                    //                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri1);//设置图片的输出位置
                    //                    startActivityForResult(intent, CROP_PHOTO_FORALBUM);//跳转至处理相册中选取的图片
                    startActivity(intent);
                }
                break;
            case CROP_PHOTO_FORCAMERA:
                Intent intent2 = new Intent(this, ImageActivity.class);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent2.putExtra("imagePath_camera", imageUri.toString());
                //                intent2.putExtra("imagePath", imageUri.toString());
                startActivity(intent2);

                break;
            case CROP_PHOTO_FORALBUM:
                Intent intent1 = new Intent(this, ImageActivity.class);
                if (imagePath != null) {
                    intent1.putExtra("imagePath_Album", imagePath);
                    //                    intent1.putExtra("imagePath", imagePath);
                    Log.d("imagePath_Album", imagePath);
                } else {
                    intent1.putExtra("imagePath_Album", imageUri1.getPath());
                    //                    intent1.putExtra("imagePath", imageUri1.getPath());
                    Log.d("imagePath_Album_Uri", imageUri.getPath());
                }
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Log.d("imagedata", data.toString());
        Log.d("imageUridata", data.getData().toString());
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //若为document类型的uri则通过document id处理
            String docID = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docID.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads"), Long.valueOf(docID));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //若为contetn类型的url，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //若为file类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        //        displayImage(imagePath);//根据图片路径显示图片
        return imagePath;
        //    return uri;
    }

    private Uri handleImageBeforeKitKat(Intent data) {
        Log.d("imagedata19", data.toString());
        Log.d("imageUridata19", data.getData().toString());
        Uri uri = data.getData();
        return uri;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Failed to get image!", Toast.LENGTH_SHORT).show();
        }
        return path;
    }

    private void queryFromServer() throws InterruptedException {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responeData = response.body().string();
                boolean result = false;
                try {
                    result = HttpAction.parseJSONWithGSON(responeData);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (result) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                            editor.putBoolean("info", true);
                            editor.apply();
                            //                            Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MainActivity.this, "Fail!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载toolbar菜单文件
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                // intent.setType("text/plain"); //纯文本
            /*
             * 图片分享 it.setType("image/png"); 　//添加图片 File f = new
             * File(Environment.getExternalStorageDirectory()+"/name.png");
             *
             * Uri uri = Uri.fromFile(f); intent.putExtra(Intent.EXTRA_STREAM,
             * uri); 　
             */
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                //分享图片
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                //分享文本
                intent.putExtra(Intent.EXTRA_TEXT, "I have successfully share my message through my app");
                //                intent.putExtra("Kdescription", "wwwwwwwwwwwwwwwwwwww");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

}

/*
程序逻辑：
调用摄像头拍照裁剪后显示：
1.首先创建一个File对象用于存放拍下的照片并将其存放在手机sd卡的缓存目录下（放在缓存目录下可跳过运行时权限处理）
2.判断：若系统版本低于7.0，则调用Uri的fromFile方法将File对象转为Uri对象，该对象标识着output_image.jpg的本地真实路径
             否则调用FileProvider的getUriForFile方法将File对象转为一个封装过的Uri对象，因为直接使用本地真实路径的Uri在7.0系统以后被认为是不安全的
             FileProvider是一种特殊的内容提供器，可以提高应用的安全性
3.构建一个将action指定为android.media.action.IMAGE_CAPTURE的Intent对象，再填入刚得到的Uri对象并用putExtra方法指定图片的输出地址
   最后调用startActivityForResult方法启动活动，打开照相机程序，拍下的照片会输出到tempImage.jpg中
4.startActivityForResult方法的结果返回到onActivityResult方法中，再创建一个Intent对象用putExtra方法设置图片是否可被裁剪和缩放，最后调用BitmapFactory的decodeStream方法（参数为getContentResolver().openInputStream(imageUri)）将照片解析为Bitmap对象并在ImageView对象中显示

从相册中选取图片裁剪后显示：
1.首先进行运行时动态申请SD卡读取权限，然后构建一个action指定为android.intent.action.GET_CONTENT的Intent对象并为其设置一些必要参数，然后调用startActivityForResult方法进入相册选择照片
2.判断：4.4版本及以上的系统用handleImageOnKitKat方法处理图片，否则用handleImageBeforeKitKat方法处理，原因在于4.4以上版本的系统选取相册中图片只会返回一个封装过的图片Uri，所以要对改Uri进行另外的解析
3.调用startActivityForResult方法启动活动，打开相册，选中的照片会输出到fromAlbumImage.jpg中
4.startActivityForResult方法的结果返回到onActivityResult方法中，再创建一个Intent对象用putExtra方法设置图片是否可被裁剪和缩放，最后调用BitmapFactory的decodeFile方法（参数为照片的Uri对象）将照片解析为Bitmap对象并在ImageView对象中显示
 */