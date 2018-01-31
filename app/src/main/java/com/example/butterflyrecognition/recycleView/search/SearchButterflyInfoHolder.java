package com.example.butterflyrecognition.recycleView.search;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.Util.ImageUtil;
import com.example.butterflyrecognition.recycleView.indexBar.ButterflyInfo_copy;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dr.P on 2017/11/10.
 */
public class SearchButterflyInfoHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.butterfly_image)
    ImageView butterflyImage;

    @BindView(R.id.butterfly_name)
    TextView butterflyName;

    @BindView(R.id.butterfly_latinName)
    TextView butterflyLatinName;

    View butterflyView;
    Context context;
    AssetManager assetManager;

    public SearchButterflyInfoHolder(Context context, View itemView) {
        super(itemView);
        butterflyView = itemView;
        this.context = context;
        ButterKnife.bind(this, itemView);
    }

    public void bind(ButterflyInfo_copy butterflyInfo) throws IOException {

        String imagePath=butterflyInfo.getImagePath();
        //        Log.d("ButterflyAdapter", butterflyInfo.getImagePath());
        //1-7日修改
        //        if (imagePath == null) {
        //            try {
        //                imagePath=(String)new DownImage().execute(butterflyInfo.getImageUrl()).get();
        //            } catch (InterruptedException e) {
        //                e.printStackTrace();
        //            } catch (ExecutionException e) {
        //                e.printStackTrace();
        //            }
        //        }

        butterflyName.setText(butterflyInfo.getName());
        //        butterflyImage.setImageURI(Uri.parse(imagePath));

        //        String[] images=imagePath.split(",");
        //1-28日修改
        InputStream assetFile = null;
        try {
            assetFile = context.getAssets().open("btf/" + butterflyInfo.getName() + "0.jpg");
            //            Log.e("asset",assetFile.toString());
            //            assetFile = context.getClass().getResourceAsStream("/assets/" + ButterflyActivity.images[1]);
            //            if (assetFile.read()==-1) {
            //                assetFile = context.getAssets().open("btf/zanwu.jpg");
            //            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] imagebyte = ImageUtil.getImageFromAsset(assetFile);
        Glide.with(butterflyView.getContext()).load(imagebyte).into(butterflyImage);
        //        Glide.with(butterflyView.getContext()).load(images[1]).into(butterflyImage);
        butterflyLatinName.setText(butterflyInfo.getLatinName());
    }
}
