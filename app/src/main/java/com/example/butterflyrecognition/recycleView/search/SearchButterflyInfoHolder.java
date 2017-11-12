package com.example.butterflyrecognition.recycleView.search;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.Util.DownImage;
import com.example.butterflyrecognition.recycleView.indexBar.ButterflyInfo_copy;

import java.util.concurrent.ExecutionException;

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

    public SearchButterflyInfoHolder(View itemView) {
        super(itemView);
        butterflyView = itemView;
        ButterKnife.bind(this, itemView);
    }

    public void bind(ButterflyInfo_copy butterflyInfo) {

        String imagePath=butterflyInfo.getImagePath();
        Log.d("ButterflyAdapter", butterflyInfo.getImagePath());
        if (imagePath == null) {
            try {
                imagePath=(String)new DownImage().execute(butterflyInfo.getImageUrl()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        butterflyName.setText(butterflyInfo.getName());
        //        butterflyImage.setImageURI(Uri.parse(imagePath));
        Glide.with(butterflyView.getContext()).load(imagePath).into(butterflyImage);
        butterflyLatinName.setText(butterflyInfo.getLatinName());
    }
}
