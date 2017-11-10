package com.example.butterflyrecognition.recycleView.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.db.ButterflyInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2016/3/18.
 */
public class SearchButterflyInfoAdapter extends RecyclerView.Adapter<SearchButterflyInfoHolder> {


    private List<ButterflyInfo> mList;

    public SearchButterflyInfoAdapter(List<ButterflyInfo> list) {
        this.mList = list;
    }

    @Override
    public SearchButterflyInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_search_people, parent, false);
        return new SearchButterflyInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchButterflyInfoHolder holder, int position) {
        holder.bind(mList.get(position));
    }



    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setFilter(List<ButterflyInfo> peoples) {
        mList = new ArrayList<>();
        mList.addAll(peoples);
        notifyDataSetChanged();
    }

    public void animateTo(List<ButterflyInfo> peoples) {
        applyAndAnimateRemovals(peoples);
        applyAndAnimateAdditions(peoples);
        applyAndAnimateMovedItems(peoples);
    }

    private void applyAndAnimateRemovals(List<ButterflyInfo> peoples) {
        for (int i = mList.size() - 1; i >= 0; i--) {
            final ButterflyInfo people = mList.get(i);
            if (!peoples.contains(people)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ButterflyInfo> peoples) {
        for (int i = 0, count = peoples.size(); i < count; i++) {
            final ButterflyInfo people = mList.get(i);
            if (!mList.contains(people)) {
                addItem(i, people);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ButterflyInfo> peoples) {
        for (int toPosition = peoples.size() - 1; toPosition >= 0; toPosition--) {
            final ButterflyInfo people = peoples.get(toPosition);
            final int fromPosition = mList.indexOf(people);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }


    public ButterflyInfo removeItem(int position) {
        final ButterflyInfo people = mList.remove(position);
        notifyItemRemoved(position);
        return people;
    }


    public void addItem(int position, ButterflyInfo people) {
        mList.add(position, people);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ButterflyInfo people = mList.remove(fromPosition);
        mList.add(toPosition, people);
        notifyItemMoved(fromPosition, toPosition);
    }

}
