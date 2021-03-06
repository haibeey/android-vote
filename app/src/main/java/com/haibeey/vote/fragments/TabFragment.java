package com.haibeey.vote.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haibeey.vote.R;
import com.haibeey.vote.adapters.topicsAdapter;
import com.haibeey.vote.connections.connection;
import com.haibeey.vote.datas.dbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;

public class TabFragment extends Fragment implements connection.responseListerner{
    connection Connection;
    topicsAdapter topicAdapter;
    dbHelper DbHelper;
    Handler myHandler;
    View v;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView nestedScrollView;
    int query=1;
    private boolean isLoading=false;

    public TabFragment() {
        
        myHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.obj.toString().contains("{") && msg.obj.toString().contains("}")){
                    addToDb(msg.obj.toString());
                }else if(msg.obj.toString().equals("changed")){
                    topicAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        };
    }

    public static TabFragment newInstance(String param1, String param2) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        topicAdapter=new topicsAdapter(getActivity());
        v=inflater.inflate(R.layout.fragment_tab, container, false);
        //getting a netedscrollview
        nestedScrollView=(NestedScrollView)v.findViewById(R.id.scrollView);
        //setting a nestedscrollview
        setScrollViewListener();

        //setting up recyclerview
        swipeRefreshLayout=(SwipeRefreshLayout) v.findViewById(R.id.SWL);
        //setting refreshlayout

        onSwipeReFreshLayout(swipeRefreshLayout);
        //setup adpater
        recyclerView=(RecyclerView) v.findViewById(R.id.tab1RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(topicAdapter);
        return v;
    }


    private void onSwipeReFreshLayout(final SwipeRefreshLayout swipeRefreshLayout){
        if(swipeRefreshLayout!=null){
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    load();
                }
            });
        }
    }

    private void setScrollViewListener(){
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View view = (nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1));
                int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView.getScrollY()));
                //Log.i("what",String.valueOf(diff)+" "+String.valueOf(nestedScrollView.getHeight())+" "+String.valueOf(nestedScrollView.getScrollY()));

                // if diff is zero, then the bottom has been reached
                if (diff <= 10) {
                    load();
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(DbHelper!=null){
            updateTopic();
            topicAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void load(){
        //set up local database and cthe connection class
        if(isLoading){
            if(v!=null){
                Snackbar.make(v," Loading ",Snackbar.LENGTH_SHORT).show();
            }
            return;
        }
        isLoading=true;//flag to not load again while loading

        if(DbHelper==null){
            DbHelper=new dbHelper(getContext());
        }
        //update local database
        Connection=new connection( getActivity(),getContext(),"http://haibeeyy.pythonanywhere.com/");
        Connection.addQuery("category",String.valueOf(query));
        Connection.buildUrlAndRequest();
        Connection.setListenerForFragment(this);
        query++;

        try {
            Connection.getResponseAync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void addToDb(final String result){
        //update the local database in another thread
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray jsonArray1=jsonObject.getJSONArray("users");
                    JSONArray jsonArray2=jsonObject.getJSONArray("topics");
                    //this part is for the user table
                    for(int i=0;i<jsonArray1.length();i++){
                        JSONArray jsonArray=jsonArray1.getJSONObject(i).getJSONArray("topic");
                        //add to the database,checks if  data if present
                        if(!DbHelper.topicInUser(jsonArray.getString(0))){
                            DbHelper.insertInUser(jsonArray.getString(0),String.valueOf(jsonArray.getInt(1)));
                        }else{
                            DbHelper.updateUser(jsonArray.getString(0),jsonArray.getInt(1));
                        }
                    }

                    //this part is for the topic
                    for(int i=0;i<jsonArray2.length();i++){

                        JSONObject jsonObjectt=jsonArray2.getJSONObject(i);
                        int id =jsonObjectt.getInt("id");
                        String title=jsonObjectt.getString("title");
                        String date=jsonObjectt.getString("date");
                        String by=jsonObjectt.getString("created_by");
                        int count=jsonObjectt.getInt("count");
                        JSONArray choices =jsonObjectt.getJSONArray("choices");

                        //add to database ,checks if data is already present
                        if(DbHelper.topicInTopic(id,title)){

                            DbHelper.updateTopic(id,title,date,count,by);

                            for(int choice=0;choice<choices.length();choice++){
                                JSONArray jsonArray3=choices.getJSONArray(choice);
                                DbHelper.updateChoice(id,title,jsonArray3.getString(0),jsonArray3.getInt(1),by);
                            }

                        }else{

                            DbHelper.insertTopic(id,title,date,count,by);

                            for(int choice=0;choice<choices.length();choice++){
                                JSONArray jsonArray3=choices.getJSONArray(choice);
                                DbHelper.insertInChoice(id,title,jsonArray3.getString(0),String.valueOf(jsonArray3.getInt(1)),by);
                            }
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                topicAdapter.Topics=new dbHelper(getContext()).getDataFromTopic();
                Message message=new Message();
                message.obj="changed";
                myHandler.sendMessage(message);
            }
        });

        thread.start();//start the thread

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized  void updateTopic(){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                topicAdapter.Topics=new dbHelper(getContext()).getDataFromTopic();
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failure() {
        Message message=new Message();
        message.obj="failed";
        myHandler.sendMessage(message);
        Snackbar.make(v,R.string.onFailure,Snackbar.LENGTH_SHORT).show();
        isLoading=false;
    }

    @Override
    public void success(Response response) {
        //if sucess send the result to an handler
        try {
            Message message=new Message();
            message.obj=response.body().string();
            myHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message message=new Message();
        message.obj="failed";
        myHandler.sendMessage(message);
        isLoading=false;
    }
}
