package com.example.butterflyrecognition;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    public static final int CROP_PHOTO_FORCAMERA=3;
    public static final int CROP_PHOTO_FORALBUM=4;
    private ImageView picture;
    private Uri imageUri;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button takePhoto;
    private Button choosePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //隐藏标题栏
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icons_menu3);
        }

        navigationView.setCheckedItem(R.id.nav_call);//将call菜单设置为默认选中
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //此处用于处理点击任意菜单项后的逻辑
                drawerLayout.closeDrawers();
                return true;
            }
        });

        Button takePhoto = (Button) findViewById(R.id.take_photo);
        Button choosePhoto = (Button) findViewById(R.id.choose_from_album);
        picture = (ImageView) findViewById(R.id.picture);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建File对象用于存储拍摄的照片
                File outputImage = new File(getExternalCacheDir(),"tempImage.jpg");;
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
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                startActivityForResult(intent,TAKE_PHOTO);
                startActivityForResult(intent,CROP_PHOTO_FORCAMERA);
            }
        });

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //动态申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    File outputImage = new File(getExternalCacheDir(),"fromAlbumImage.jpg");;
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
                    } else {
                        imageUri = Uri.fromFile(outputImage);
                    }
                    openAlbum();
                }
            }
        });
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setDataAndType(imageUri,"image/*");
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//设置图片的输出位置
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //将拍得的照片裁剪后显示出来
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO_FORCAMERA);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri imageUri1=null;
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4以上系统用此方法处理图片
                        imageUri1= handleImageOnKitKat(data);
                    } else {
                        //其他系统用此方法处理图片
                        imageUri1 = handleImageBeforeKitKat(data);
                    }
                    imageUri=imageUri1;
//                    displayImage(imageUri.getPath());

                    Intent intent = new Intent("com.android.camera.action.CROP");
//                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setDataAndType(imageUri,"image/*");
                    intent.putExtra("crop", true);//允许裁剪
                    intent.putExtra("scale", true);//允许缩放
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri1);//设置图片的输出位置
                    startActivityForResult(intent,CROP_PHOTO_FORALBUM);//跳转至处理相册中选取的图片
                }
                break;
            case CROP_PHOTO_FORCAMERA:
//                try {
//                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                                        picture.setImageBitmap(bitmap);//将裁剪后的照片显示出来
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
                Intent intent = new Intent(this, ImageActivity.class);
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra("imagePath", imageUri.toString());
                startActivity(intent);

                break;
            case CROP_PHOTO_FORALBUM:
                displayImage( imageUri.getPath());
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private Uri handleImageOnKitKat(Intent data) {
        String imagePath=null;
        Uri uri=data.getData();
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
            imagePath=uri.getPath();
        }
        //        displayImage(imagePath);//根据图片路径显示图片
        //        return imagePath;
        return uri;
    }

    private Uri handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        return uri;
    }

    private String getImagePath(Uri uri, String selection) {
        String path=null;
        //通过Uri和selection获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "Failed to get image!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载toolbar菜单文件
        getMenuInflater().inflate(R.menu.toolbar,menu);
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
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                //分享图片
                intent.putExtra(Intent.EXTRA_STREAM,imageUri);
                //分享文本
                intent.putExtra(Intent.EXTRA_TEXT, "I have successfully share my message through my app");
//                intent.putExtra("Kdescription", "wwwwwwwwwwwwwwwwwwww");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:break;
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