package com.haibeey.vote.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import  com.haibeey.vote.R;
/**
 * Created by haibeey on 11/23/2017.
 */

public class topicsViewHolder extends RecyclerView.ViewHolder {
    public TextView tv1,tv2,tv3,tv4;
    public topicsViewHolder(View itemView) {
        super(itemView);
        tv1=(TextView) itemView.findViewById(R.id.cardTitle);
        tv2=(TextView) itemView.findViewById(R.id.count);
        tv3=(TextView) itemView.findViewById(R.id.one);
        tv4=(TextView) itemView.findViewById(R.id.two);
    }
}
