package com.example.butterflyrecognition.recycleView.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.db.ButterflyInfo;
import com.example.butterflyrecognition.recycleView.InfoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dr.P on 2017/11/10.
 */
public class SearchButterflyInfoAdapter extends RecyclerView.Adapter<SearchButterflyInfoHolder> {
//public class SearchButterflyInfoAdapter extends RecyclerView.Adapter<SearchButterflyInfoAdapter.ViewHolder> {
    Context context;
    ButterflyInfo butterflyInfo;

    private List<ButterflyInfo> myButterflyList;

    static  class ViewHolder extends  RecyclerView.ViewHolder{
        View butterflyView;
        ImageView butterflyImage;
        TextView butterflyname;
        TextView butterflylatinName;

        public ViewHolder(View view) {
            super(view);
            butterflyView =view;
            butterflyImage =(ImageView)view.findViewById(R.id.butterfly_image);
            butterflyname =(TextView)view.findViewById(R.id.butterfly_name);
            butterflylatinName = (TextView) view.findViewById(R.id.butterfly_latinName);
        }
    }

    public SearchButterflyInfoAdapter(List<ButterflyInfo> butterflyList) {
        this.myButterflyList = butterflyList;
    }

    @Override
    public SearchButterflyInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null) {
            context=parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.butterfly_item, parent, false);
        final SearchButterflyInfoHolder holder = new SearchButterflyInfoHolder(view);
        holder.butterflyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int postiton=holder.getAdapterPosition();
                ButterflyInfo butterflyInfo = myButterflyList.get(postiton);
                Intent intent = new Intent(context,InfoActivity.class);
                intent.putExtra("butterflyNo", butterflyInfo.getId());
                context.startActivity(intent);
                Activity activity = (Activity) context;
                activity.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        return holder;
    }

//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        ButterflyInfo butterflyInfo = myButterflyList.get(position);
//        String imagePath=butterflyInfo.getImagePath();
//        Log.d("ButterflyAdapter", butterflyInfo.getImagePath());
//        if (imagePath == null) {
//            try {
//                imagePath=(String)new DownImage().execute(butterflyInfo.getImageUrl()).get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
//        holder.butterflyname.setText(butterflyInfo.getName());
//        holder.butterflyImage.setImageURI(Uri.parse(imagePath));
//        holder.butterflylatinName.setText(butterflyInfo.getLatinName());
//    }

    @Override
    public void onBindViewHolder(SearchButterflyInfoHolder holder, int position) {
        holder.bind(myButterflyList.get(position));
    }



    @Override
    public int getItemCount() {
        return myButterflyList.size();
    }

    public void setFilter(List<ButterflyInfo> butterflyInfos) {
        myButterflyList = new ArrayList<>();
        myButterflyList.addAll(butterflyInfos);
        notifyDataSetChanged();
    }

    public void animateTo(List<ButterflyInfo> butterflyInfos) {
        applyAndAnimateRemovals(butterflyInfos);
        applyAndAnimateAdditions(butterflyInfos);
        applyAndAnimateMovedItems(butterflyInfos);
    }

    private void applyAndAnimateRemovals(List<ButterflyInfo> butterflyInfos) {
        for (int i = myButterflyList.size() - 1; i >= 0; i--) {
            final ButterflyInfo butterflyInfo = myButterflyList.get(i);
            if (!butterflyInfos.contains(butterflyInfo)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ButterflyInfo> butterflyInfos) {
        for (int i = 0, count = butterflyInfos.size(); i < count; i++) {
            final ButterflyInfo butterflyInfo = myButterflyList.get(i);
            if (!myButterflyList.contains(butterflyInfo)) {
                addItem(i, butterflyInfo);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ButterflyInfo> butterflyInfos) {
        for (int toPosition = butterflyInfos.size() - 1; toPosition >= 0; toPosition--) {
            final ButterflyInfo people = butterflyInfos.get(toPosition);
            final int fromPosition = myButterflyList.indexOf(people);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }


    public ButterflyInfo removeItem(int position) {
        final ButterflyInfo people = myButterflyList.remove(position);
        notifyItemRemoved(position);
        return people;
    }


    public void addItem(int position, ButterflyInfo people) {
        myButterflyList.add(position, people);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ButterflyInfo people = myButterflyList.remove(fromPosition);
        myButterflyList.add(toPosition, people);
        notifyItemMoved(fromPosition, toPosition);
    }

}
