package com.haibeey.vote.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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

    public TabFragment() {
        myHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                addToDb(msg.obj.toString());
                topicAdapter.Topics=new dbHelper(getContext()).getDataFromTopic();
                topicAdapter.notifyDataSetChanged();
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
        View v=inflater.inflate(R.layout.fragment_tab, container, false);
        //setup adpater
        RecyclerView recyclerView=(RecyclerView) v.findViewById(R.id.tab1RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(topicAdapter);

        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //set up local database and cthe connection class
        DbHelper=new dbHelper(context);
        //update local database
        Connection=new connection((Activity) context,context,"http://haibeeyy.pythonanywhere.com/");
        Connection.buildUrlAndRequest();
        Connection.setListenerForFragment(this);

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
                        String title=jsonObjectt.getString("title");
                        String date=jsonObjectt.getString("date");
                        String by=jsonObjectt.getString("created_by");
                        int count=jsonObjectt.getInt("count");
                        JSONArray choices =jsonObjectt.getJSONArray("choices");

                        //add to database ,checks if data is already present
                        if(DbHelper.topicInTopic(title)){

                            DbHelper.updateTopic(title,date,count,by);

                            for(int choice=0;choice<choices.length();choice++){
                                JSONArray jsonArray3=choices.getJSONArray(choice);
                                DbHelper.updateChoice(title,jsonArray3.getString(0),jsonArray3.getInt(1),by);
                            }

                        }else{

                            DbHelper.insertTopic(title,date,count,by);

                            for(int choice=0;choice<choices.length();choice++){
                                JSONArray jsonArray3=choices.getJSONArray(choice);
                                DbHelper.insertInChoice(title,jsonArray3.getString(0),String.valueOf(jsonArray3.getInt(1)),by);
                            }
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        thread.start();//start the thread

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failure() {

    }

    @Override
    public void success(Response response) {
        //if sucess sent the the result to an handler
        try {
            Message message=new Message();
            message.obj=response.body().string();
            myHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}