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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.butterflyrecognition.fragment.ResultDialog;

import java.io.FileNotFoundException;

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

    public static final String BUTTERFLY_NAME = "butterfly_image";

    public static final String BUTTERFLY_IMAGE_ID = "butterfly_image_id";
    public static final String IMAGE_URI_CAMERA = "imagePath_camera";
    public static final String IMAGE_URI_ALBUM = "imagePath_Album";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //        if (Build.VERSION.SDK_INT >= 21) {
        //            View decorView = getWindow().getDecorView();
        //            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        //            getWindow().setStatusBarColor(Color.TRANSPARENT);
        //        }
        setContentView(R.layout.activity_image);

        ImageView butterflyImageView = (ImageView) findViewById(R.id.butterfly_image_view);
        TextView butterflyContentText = (TextView) findViewById(R.id.butterfly_content_text);
        //        LinearLayoLinearLayout hintLayout = (LinearLayout) findViewById(R.id.hint_layout);ut hintLayout = (LinearLayout) findViewById(R.id.hint_layout);
        FrameLayout hintLayout = (FrameLayout) findViewById(R.id.hint_layout);

        cancelbtn = (Button) findViewById(R.id.cancel);
        use_photo = (Button) findViewById(R.id.use_photo);
        cancelbtn.setOnClickListener(this);
        use_photo.setOnClickListener(this);

        Intent intent = getIntent();
        String image_camera = null;
        String image_album = null;
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
            } catch (FileNotFoundException e) {
                Log.d("image1FileNotFound", imageUri1.toString());
            }
        } else if (image_album != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(image_album);
            butterflyImageView.setImageBitmap(bitmap);
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
                ResultDialog resultDialog = new ResultDialog(ImageActivity.this);
                resultDialog.setCancelable(false);
                resultDialog.show();
                break;
        }
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
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        super.onBackPressed();
    }
}
