package com.example.butterflyrecognition.recycleView.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.db.ButterflyInfo;
import com.example.butterflyrecognition.recycleView.InfoActivity;
import com.example.butterflyrecognition.recycleView.indexBar.ButterflyInfo_copy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dr.P on 2017/11/10.
 */
public class SearchButterflyInfoAdapter extends RecyclerView.Adapter<SearchButterflyInfoHolder> {
    Context context;
    ButterflyInfo butterflyInfo;

    //    private List<ButterflyInfo_copy> myButterflyList;
    private List<ButterflyInfo_copy> myButterflyList;


    public SearchButterflyInfoAdapter(List<ButterflyInfo_copy> butterflyList) {
        //public SearchButterflyInfoAdapter(List<InfoDetail> butterflyList) {
        this.myButterflyList = butterflyList;
    }

    public List<ButterflyInfo_copy> getDatas() {
        return myButterflyList;
    }

    public SearchButterflyInfoAdapter setDatas(List<ButterflyInfo_copy> datas) {
        myButterflyList = datas;
        return this;
    }

    @Override
    public SearchButterflyInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null) {
            context=parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.butterfly_item, parent, false);
        final SearchButterflyInfoHolder holder = new SearchButterflyInfoHolder(view);
        holder.butterflyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int postiton=holder.getAdapterPosition();
                ButterflyInfo_copy butterflyInfo = myButterflyList.get(postiton);
                String[] images = butterflyInfo.getImagePath().split(",");
                ArrayList<String> imageList = new ArrayList<String>();
                for (int i = 0; i < images.length; i++) {
                    imageList.add(images[i]);
                }
                Intent intent = new Intent(context,InfoActivity.class);
                intent.putStringArrayListExtra("imageList", imageList);
                intent.putExtra("butterflyNo", butterflyInfo.getId());
                context.startActivity(intent);
                Activity activity = (Activity) context;
                activity.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchButterflyInfoHolder holder, int position) {
        holder.bind(myButterflyList.get(position));
    }

    @Override
    public int getItemCount() {
        return myButterflyList.size();
    }

    public void setFilter(List<ButterflyInfo_copy> butterflyInfos) {
        myButterflyList = new ArrayList<>();
        myButterflyList.addAll(butterflyInfos);
        notifyDataSetChanged();
    }

    public void animateTo(List<ButterflyInfo_copy> butterflyInfos) {
        applyAndAnimateRemovals(butterflyInfos);
        applyAndAnimateAdditions(butterflyInfos);
        applyAndAnimateMovedItems(butterflyInfos);
    }

    private void applyAndAnimateRemovals(List<ButterflyInfo_copy> butterflyInfos) {
        for (int i = myButterflyList.size() - 1; i >= 0; i--) {
            final ButterflyInfo_copy butterflyInfo = myButterflyList.get(i);
            if (!butterflyInfos.contains(butterflyInfo)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ButterflyInfo_copy> butterflyInfos) {
        for (int i = 0, count = butterflyInfos.size(); i < count; i++) {
            final ButterflyInfo_copy butterflyInfo = myButterflyList.get(i);
            if (!myButterflyList.contains(butterflyInfo)) {
                addItem(i, butterflyInfo);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ButterflyInfo_copy> butterflyInfos) {
        for (int toPosition = butterflyInfos.size() - 1; toPosition >= 0; toPosition--) {
            final ButterflyInfo_copy people = butterflyInfos.get(toPosition);
            final int fromPosition = myButterflyList.indexOf(people);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }


    public ButterflyInfo_copy removeItem(int position) {
        final ButterflyInfo_copy people = myButterflyList.remove(position);
        notifyItemRemoved(position);
        return people;
    }


    public void addItem(int position, ButterflyInfo_copy people) {
        myButterflyList.add(position, people);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ButterflyInfo_copy people = myButterflyList.remove(fromPosition);
        myButterflyList.add(toPosition, people);
        notifyItemMoved(fromPosition, toPosition);
    }

}
