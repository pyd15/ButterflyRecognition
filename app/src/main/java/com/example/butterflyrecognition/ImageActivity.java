package com.example.butterflyrecognition;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

import snackbar.SnackBarUtil;

/**
 * Created by Dr.P on 2017/10/10.
 */

public class ImageActivity extends AppCompatActivity {

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

    public static final String BUTTERFLY_NAME = "butterfly_image";
    public static final String BUTTERFLY_IMAGE_ID = "butterfly_image_id";
    public static final String IMAGE_URI = "imagePath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageView butterflyImageView = (ImageView) findViewById(R.id.butterfly_image_view);
        TextView butterflyContentText = (TextView) findViewById(R.id.butterfly_content_text);
        LinearLayout hintLayout = (LinearLayout) findViewById(R.id.hint_layout);
        Intent intent = getIntent();
        Uri imageUri = Uri.parse(intent.getStringExtra(IMAGE_URI));
        Log.d("imageUri_ImageActivity", imageUri.toString());

        //                Glide.with(this).load(getImagePath(imageUri,null)).into(butterflyImageView);


        try {
            ////            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.toString());
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            butterflyImageView.setImageBitmap(bitmap);//将裁剪后的照片显示出来
        } catch (FileNotFoundException e) {
            Log.d("FileNotFound", imageUri.toString());
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
        snackbar.show();
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
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        super.onBackPressed();
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
        }
        return path;
    }
}
