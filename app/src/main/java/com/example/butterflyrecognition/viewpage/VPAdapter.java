package com.example.butterflyrecognition.viewpage;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.Util.ImageUtil;
import com.example.butterflyrecognition.db.InfoDetail;

import java.io.IOException;
import java.io.InputStream;
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
    private InfoDetail infoDetail;
    InputStream assetFile = null;

    public VPAdapter(Context context, ArrayList<String> imageList, InfoDetail infoDetail) {
        this.context = context;
        imgs = new ArrayList<>();
        this.infoDetail = infoDetail;
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
            try {
                assetFile = context.getAssets().open("btf/" + infoDetail.getName() + (position % 3) + ".jpg");
                //            assetFile = context.getClass().getResourceAsStream("/assets/" + ButterflyActivity.images[1]);
                //            iv.setImageURI(Uri.parse(imgs.get(position % 3)));
                byte[] imagebyte = ImageUtil.getImageFromAsset(assetFile);

                //            Glide.with(context).load(imgs.get(position % imgs.size())).into(iv);
                Glide.with(context).load(imagebyte).into(iv);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //            iv.setImageURI(Uri.parse(imgs.get(0)));
            try {
                assetFile = context.getAssets().open("btf/" + infoDetail.getName() + "0.jpg");
                //            assetFile = context.getClass().getResourceAsStream("/assets/" + ButterflyActivity.images[1]);
                //            Glide.with(context).load(imgs.get(0)).into(iv);
                byte[] imagebyte = ImageUtil.getImageFromAsset(assetFile);
                Glide.with(context).load(imagebyte).into(iv);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


}
