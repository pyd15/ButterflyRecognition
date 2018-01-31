package com.example.butterflyrecognition.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.butterflyrecognition.MainActivity;
import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.db.InfoDetail;
import com.example.butterflyrecognition.recycleView.InfoActivity;

import org.litepal.crud.DataSupport;

/**
 * TODO: document your custom view class.
 */
public class DIYPopWin extends PopupWindow {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private int id = 1;
    public View mCreateView;

    private Button btnTakePhoto, btnSelect, btnCancel;
    private View mPopView;
    private Context mContext;
    //    private OnItemClickListener mListener;
    private OnClickListener mListener;

    public DIYPopWin(Context context, int id) {
        super(context);
        this.id = id;
        setPopupWindow();
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private View init(Context context) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = LayoutInflater.from(context);
        //绑定布局
        mPopView = inflater.inflate(R.layout.diy_dialog, null);

        MyOnClickListener listener = new MyOnClickListener(0);

        ImageView imageView = (ImageView) mPopView.findViewById(R.id.picture);
        TextView name = (TextView) mPopView.findViewById(R.id.Name);
        TextView latinName = (TextView) mPopView.findViewById(R.id.latinname);
        TextView kind = (TextView) mPopView.findViewById(R.id.Kind);
        TextView detail = (TextView) mPopView.findViewById(R.id.detail);
        Button checkcompare = (Button) mPopView.findViewById(R.id.checkcompare);
        TextView home = (TextView) mPopView.findViewById(R.id.homepage);

        InfoDetail infoDetail = DataSupport.find(InfoDetail.class, id);
        //        InputStream assetFile=null;
        //        try {
        //            assetFile = mContext.getAssets().open("btf/"+infoDetail.getName()+"0.jpg");
        //            //            assetFile = context.getClass().getResourceAsStream("/assets/" + ButterflyActivity.images[1]);
        //            //        Glide.with(mContext).load(getImagePaths(infoDetail).get(0)).into(imageView);
        //            byte[] imagebyte = ImageUtil.getImageFromAsset(assetFile);
        //            Glide.with(mCreateView.getContext()).load(imagebyte).into(imageView);
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }

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
        return mPopView;
    }

    /**
     * 设置窗口的相关属性
     */
    @SuppressLint("InlinedApi")
    private void setPopupWindow() {
        this.setContentView(mPopView);// 设置View
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setAnimationStyle(R.style.diy_pop_win_anim_style);// 设置动画
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明
        //        mPopView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁
        //
        //            @Override
        //            public boolean onTouch(View v, MotionEvent event) {
        //                // TODO Auto-generated method stub
        //                int height = mPopView.findViewById(R.id.ll1).getTop();
        //                int y = (int) event.getY();
        //                if (event.getAction() == MotionEvent.ACTION_UP) {
        //                    if (y < height) {
        //                        dismiss();
        //                    }
        //                }
        //                return true;
        //            }
        //        });
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
                    Intent intent = new Intent(mContext, InfoActivity.class);
                    intent.putExtra("butterflyNo", id);
                    intent.putExtra("activity", InfoActivity.class.getSimpleName());
                    InfoDetail infoDetail = DataSupport.find(InfoDetail.class, id);
                    //                    String[] images = infoDetail.getImagePath().split(",");
                    //                    ArrayList<String> imageList = new ArrayList<String>();
                    //                    for (int i = 0; i < images.length; i++) {
                    //                        imageList.add(images[i]);
                    //                    }
                    //                    intent.putExtra("imageList", getImagePaths(infoDetail));
                    mContext.startActivity(intent);
                    break;
                case R.id.homepage:
                    Intent intent1 = new Intent(mContext, MainActivity.class);
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
        this.mListener = listener;
    }

    /**
     * 定义一个接口，公布出去 在Activity中操作按钮的单击事件
     */
    public interface OnItemClickListener {
        void setOnItemClick(View v);
    }

    //    public void setOnItemClickListener(OnItemClickListener listener) {
    //        this.mListener = listener;
    //    }

    //    @Override
    //    public void onClick(View v) {
    //        // TODO Auto-generated method stub
    //        if (mListener != null) {
    //            mListener.setOnItemClick(v);
    //        }
    //    }
}
