package com.example.butterflyrecognition.recycleView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.Util.DownImage;
import com.example.butterflyrecognition.db.ButterflyInfo;
import com.example.butterflyrecognition.db.InfoDetail;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Dr.P on 2017/10/10.
 */

public class ButterflyAdapter extends RecyclerView.Adapter<ButterflyAdapter.ViewHolder> {
    private List<InfoDetail> myButterflyList;
    Context context;
    ButterflyInfo butterflyInfo;

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

    public ButterflyAdapter(List<InfoDetail> butterflyList)
    {
        myButterflyList = butterflyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null) {
            context=parent.getContext();
        }
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.butterfly_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.butterflyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int postiton=holder.getAdapterPosition();
                InfoDetail butterflyInfo = myButterflyList.get(postiton);
                Intent intent = new Intent(context,InfoActivity.class);
                intent.putExtra("butterflyNo", butterflyInfo.getId());
                context.startActivity(intent);

            }
        });
        holder.butterflyname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int postiton=holder.getAdapterPosition();
                InfoDetail butterflyInfo = myButterflyList.get(postiton);
                Toast.makeText(view.getContext(),"you clicked text "+ butterflyInfo.getName(),Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InfoDetail butterflyInfo = myButterflyList.get(position);
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
        holder.butterflyname.setText(butterflyInfo.getName());
        holder.butterflyImage.setImageURI(Uri.parse(imagePath));
        holder.butterflylatinName.setText(butterflyInfo.getLatinName());
    }

    @Override
    public int getItemCount() {
        return myButterflyList.size();
    }
}
