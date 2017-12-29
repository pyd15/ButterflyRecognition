package com.example.butterflyrecognition.viewpage;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.butterflyrecognition.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dr.P on 2017/11/6.
 * VPAdapter类作为自定义的ViewPage的适配器，用于生成ViewPage要显示的图片列表
 */
public class VPAdapter extends PagerAdapter {
    private Context context;
    //    private List<Integer> imgs;
    private List<String> imgs;

    public VPAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        imgs = new ArrayList<>();

        //        imgs.add(R.mipmap.zy_28l);
        //        imgs.add(R.mipmap.zy_28q);
        //        imgs.add(R.mipmap.zy_28w);
        //        Log.d("imageList", String.valueOf(imageList.size()));
        for (int i = 0; i < imageList.size(); i++) {
            imgs.add(imageList.get(i));
        }
        //        Log.d("imgs", String.valueOf(imageList.size()));
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.vp_layout_item, null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_item);
        //        iv.setImageResource(imgs.get(position % 3));
        //为选中的当前页面加载对应图片
        if (imgs.size() > 1) {
            //            iv.setImageURI(Uri.parse(imgs.get(position % 3)));
            Glide.with(context).load(imgs.get(position % 3)).into(iv);
        } else {
            //            iv.setImageURI(Uri.parse(imgs.get(0)));
            Glide.with(context).load(imgs.get(0)).into(iv);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


}
