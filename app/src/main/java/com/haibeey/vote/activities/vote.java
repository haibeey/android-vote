package com.haibeey.vote.activities;

import android.content.Intent;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.haibeey.vote.R;
import com.haibeey.vote.adapters.voteSlidingAdapter;

public class vote extends AppCompatActivity {
    ViewPager viewPager;
    FragmentStatePagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setTitle("Vote");

        Intent I=getIntent();
        TextView textView=(TextView)findViewById(R.id.voteTopic);
        textView.setText(I.getStringExtra("title"));

        viewPager=(ViewPager)findViewById(R.id.voteViewPager);
        adapter=new voteSlidingAdapter(getSupportFragmentManager(),this,I.getStringExtra("title"));
        viewPager.setAdapter(adapter);
    }
}
