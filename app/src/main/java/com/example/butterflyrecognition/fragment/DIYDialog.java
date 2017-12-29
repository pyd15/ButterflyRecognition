package com.example.butterflyrecognition.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.butterflyrecognition.MainActivity;
import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.db.InfoDetail;
import com.example.butterflyrecognition.recycleView.InfoActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by Dr.P on 2017/12/23.
 * runas /user:Dr.P "cmd /k"
 */

public class DIYDialog extends BaseDialog {

    //界面显示的数据

    private String clipImagePath;
    private int id = 1;

    private OnClickListener mListener;

    public DIYDialog(Context context, String clipImagePath, int id) {
        super(context);
        this.clipImagePath = clipImagePath;
        this.id = id;
        mCreateView = initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置Dialog没有标题。需在setContentView之前设置，在之后设置会报错
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置Dialog背景透明效果，必须设置一个背景，否则会有系统的Dialog样式：外部白框
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(mCreateView);//添加视图布局
        //        setContentView(R.layout.activity_diy);//添加视图布局

        MyOnClickListener listener = new MyOnClickListener(0);


        ImageView imageView = (ImageView) findViewById(R.id.picture);
        TextView name = (TextView) findViewById(R.id.Name);
        TextView latinName = (TextView) findViewById(R.id.latinname);
        TextView kind = (TextView) findViewById(R.id.Kind);
        TextView detail = (TextView) findViewById(R.id.detail);
        Button checkcompare = (Button) findViewById(R.id.checkcompare);
        TextView home = (TextView) findViewById(R.id.homepage);


        InfoDetail infoDetail = DataSupport.find(InfoDetail.class, id);
        Glide.with(mContext).load(infoDetail.getImagePath()).into(imageView);
        imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.night));
        name.setText(infoDetail.getName());
        latinName.setText(infoDetail.getLatinName());
        kind.setText(infoDetail.getType());
        detail.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        home.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        detail.setText("查看详情");
        home.setText("返回首页");
        checkcompare.setOnClickListener(listener);
        detail.setOnClickListener(listener);//设置点击监听器
        home.setOnClickListener(listener);
        setLayout();


        Toast.makeText(mContext, "What the hell!!!", Toast.LENGTH_LONG).show();
    }

    private View initView() {
        View view = getLayoutInflater().inflate(R.layout.activity_diy, null);
        return view;
    }

    public class MyOnClickListener implements View.OnClickListener {
        private int mPosition;

        public MyOnClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            //            Toast.makeText(mContext, mTexts[mPosition], Toast.LENGTH_SHORT).show();

            switch (v.getId()) {
                case R.id.checkcompare:
                    Toast.makeText(mContext, "识别率", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.detail:
                    Intent intent = new Intent(getContext(), InfoActivity.class);
                    intent.putExtra("butterflyNo", 1);
                    intent.putExtra("activity", InfoActivity.class.getSimpleName());
                    InfoDetail infoDetail = DataSupport.find(InfoDetail.class, 1);
                    String[] images = infoDetail.getImagePath().split(",");
                    ArrayList<String> imageList = new ArrayList<String>();
                    for (int i = 0; i < images.length; i++) {
                        imageList.add(images[i]);
                    }
                    intent.putExtra("imageList", imageList);
                    mContext.startActivity(intent);
                    Toast.makeText(mContext, "detail", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.homepage:
                    Intent intent1 = new Intent(getContext(), MainActivity.class);
                    mContext.startActivity(intent1);
                    break;
                default:
                    break;
            }

            //            Intent intent = new Intent(getContext(), InfoActivity.class);
            //            intent.putExtra("butterflyNo", 1);
            //            InfoDetail infoDetail = DataSupport.find(InfoDetail.class, 1);
            //            String[] images = infoDetail.getImagePath().split(",");
            //            ArrayList<String> imageList = new ArrayList<String>();
            //            for (int i = 0; i < images.length; i++) {
            //                imageList.add(images[i]);
            //            }
            //            intent.putExtra("imageList", imageList);
            //            mContext.startActivity(intent);
            //            if(mListener!=null){
            //                mListener.OnClick(v,mPosition);//调用自定义接口, TODO（分享在调用界面实现分享功能）
            //            }
        }
    }

    public interface OnClickListener {
        void OnClick(View v, int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }

    private void setLayout() {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = mScreenWidth;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        ;//水平居中、底部
        this.getWindow().setAttributes(params);
    }

}
