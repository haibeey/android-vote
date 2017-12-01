package com.haibeey.vote.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;

import com.haibeey.vote.R;
import com.haibeey.vote.connections.connection;
import com.haibeey.vote.fragments.voteSliding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by haibeey on 11/27/2017.
 */

public class voteSlidingAdapter extends FragmentStatePagerAdapter implements connection.responseListerner{
    private ArrayList<String[]> arrayList;
    private Context context;
    private static Handler myHandler;
    private  String topic;
    private int listCount=0;

    public voteSlidingAdapter(FragmentManager fm, Context context,String topic) {
        super(fm);
        //The topic to query to the backend
        this.topic=topic;

        //Ds for adapter population
        arrayList = new ArrayList<>();

        //set up handler to nofify if data set has changed
        myHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                listCount=arrayList.size();
                notifyDataSetChanged();
            }
        };

        //setting the context
        this.context=context;

        //make asynchronous request to get the details of the vote
        connection Connection = new connection((Activity) context, context, "http://haibeeyy.pythonanywhere.com/vote");
        Connection.addQuery("load", "load");
        Connection.addQuery("topic",topic);
        Connection.buildUrlAndRequest();
        Connection.setListenerForAdpter(this);
        try {
            Connection.getResponseAync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Fragment getItem(int position) {
        //return a voteSliding instance
        voteSliding votesliding=new voteSliding();
        String[] arr=arrayList.get(position);
        votesliding.setNameCount(arr[0],arr[1],arr[2],topic);
        return  votesliding;
    }

    @Override
    public int getCount() {
        return listCount;
    }

    @Override
    public void failure() {

    }

    @Override
    public void success(Response response) {

        ResponseBody responseBody=response.body();
        try {
            String result=responseBody.string();//response body in json
            Log.i("resultVote",result);

            JSONObject jsonObject=new JSONObject(result);
            for(int choice=0;choice<jsonObject.length();choice++){
                JSONObject jsonObject1=jsonObject.getJSONObject(String.valueOf(choice));
                arrayList.add(new String[]{jsonObject1.getString("name"),
                        jsonObject1.getString("count"),jsonObject1.getString("imagename")});

                myHandler.sendMessage(new Message());//send message to notify data set has changed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
