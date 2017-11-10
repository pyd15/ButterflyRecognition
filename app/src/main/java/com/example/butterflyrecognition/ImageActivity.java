package com.example.butterflyrecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

import snackbar.SnackBarUtil;

public class ImageActivity extends AppCompatActivity {
    public static final String BUTTERFLY_NAME = "butterfly_image";
    public static final String BUTTERFLY_IMAGE_ID = "butterfly_image_id";
    public static final String IMAGE_URI = "imagePath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        Uri imageUri = Uri.parse(intent.getStringExtra(IMAGE_URI));
        //        String butterflyName = intent.getStringExtra(BUTTERFLY_NAME);
        //        int butterflyImageId = intent.getIntExtra(BUTTERFLY_IMAGE_ID,0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_image);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ImageView butterflyImageView = (ImageView) findViewById(R.id.butterfly_image_view);
        TextView butterflyContentText = (TextView) findViewById(R.id.butterfly_content_text);
        final CoordinatorLayout coordinatorlayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //        collapsingToolbarLayout.setTitle(butterflyName);
        //        Glide.with(this).load(butterflyImageId).into(butterflyImageView);


        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            butterflyImageView.setImageBitmap(bitmap);//将裁剪后的照片显示出来
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //        String butterflyContent = generateButterflyContent(butterflyName);
        String butterflyContent = "双指移动并缩放一只蝴蝶至虚线框内";
        butterflyContentText.setText(butterflyContent);
        butterflyContentText.setGravity(Gravity.CENTER_VERTICAL);

        //                Snackbar.make(view, "Make a comment?", Snackbar.LENGTH_SHORT).setAction("Undo", new View.OnClickListener() {
        //                    @Override
        //                    public void onClick(View view) {
        //                        Toast.makeText(FruitActivity.this,"Comments Failed!",Toast.LENGTH_SHORT).show();;
        //                    }
        //                }).show();
        final Snackbar snackbar = Snackbar.make(coordinatorlayout, "", Snackbar.LENGTH_INDEFINITE);//                                        Make a comment?

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

    private String generateButterflyContent(String fruitName) {
        StringBuilder fruitContent = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            fruitContent.append(fruitName);
        }
        return fruitContent.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
