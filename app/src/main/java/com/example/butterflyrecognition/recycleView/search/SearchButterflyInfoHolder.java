package com.example.butterflyrecognition.recycleView.search;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.db.ButterflyInfo;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wang on 2016/3/18.
 */
public class SearchButterflyInfoHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.holder_search_people_avatar)
    ImageView avatar;

    @BindView(R.id.holder_search_people_name)
    TextView name;

    @BindView(R.id.holder_search_people_des)
    TextView des;

    public SearchButterflyInfoHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(ButterflyInfo butterflyInfo) {

        ArrayList<Integer> avatarList = new ArrayList<>();


        avatarList.add(R.mipmap.avatar1);
        avatarList.add(R.mipmap.avatar2);
        avatarList.add(R.mipmap.avatar3);
        avatarList.add(R.mipmap.avatar4);
        avatarList.add(R.mipmap.avatar5);
        avatarList.add(R.mipmap.avatar6);
        avatarList.add(R.mipmap.avatar7);
        avatarList.add(R.mipmap.avatar8);

        name.setText(butterflyInfo.getName());
        des.setText(butterflyInfo.getFeature());
        avatar.setImageResource(avatarList.get(new Random().nextInt(avatarList.size())));
    }
}
