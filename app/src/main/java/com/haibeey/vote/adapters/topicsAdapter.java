package com.haibeey.vote.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haibeey.vote.activities.vote;
import com.haibeey.vote.datas.dataHolder;
import com.haibeey.vote.datas.dbHelper;
import com.haibeey.vote.datas.utilities;
import com.haibeey.vote.views.topicsViewHolder;
import com.haibeey.vote.R;

import java.util.ArrayList;


/**
 * Created by haibeey on 11/23/2017.
 */

public class topicsAdapter extends RecyclerView.Adapter<topicsViewHolder> {

    public ArrayList<dataHolder> Topics=new ArrayList<dataHolder>();
    Context context;
    public topicsAdapter(Context context){

        this.context=context;
        Topics=new dbHelper(context).getDataFromTopic();
    }

    @Override
    public topicsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.topics_card_view,parent,false);
        return new topicsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final topicsViewHolder holder, int position) {

        final dataHolder cardContent=Topics.get(position);

        holder.tv1.setText(cardContent.getTitle());
        holder.tv2.setText("Total votes so far "+cardContent.getCount());

        LinearLayout linearLayout=(LinearLayout) holder.itemView.findViewById(R.id.linear);

        ArrayList<String[]> arrayList=cardContent.getChoice();
        for(int choice=0;choice<arrayList.size();choice++){
            //previous view are to be cleared for the new view
            LinearLayout li=(LinearLayout) linearLayout.findViewById(R.id.alayout);
            if(li!=null){
                linearLayout.removeView(li);
            }
        }
        for(int choice=0;choice<arrayList.size();choice++){
            LinearLayout linearLayout1=(LinearLayout)LayoutInflater.from(context).inflate(R.layout.choice_layout,null,false);

            ProgressBar bar=(ProgressBar)linearLayout1.findViewById(R.id.choiceProgess);
            bar.setIndeterminate(false);
            bar.setProgress((Integer.parseInt(arrayList.get(choice)[1])));
            bar.setVisibility(View.GONE);

            TextView textView=(TextView)linearLayout1.findViewById(R.id.choiceName);
            textView.setText(arrayList.get(choice)[0]+" - \t\t\t\t\t\t"+arrayList.get(choice)[1]+" votes");

            linearLayout.addView(linearLayout1);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilities Util=new utilities((Activity)context,context);
                if(Util.isConnected()){
                    Intent I=new Intent(context,vote.class);
                    I.putExtra("title",cardContent.getTitle());
                    context.startActivity(I);
                }else {
                    Snackbar.make(holder.itemView,"No Internet Connection",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return Topics.size();
    }
}
