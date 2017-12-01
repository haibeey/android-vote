package com.haibeey.vote.connections;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by haibeey on 11/22/2017.
 */

public class connection  {
    OkHttpClient client;
    Activity activity;
    Context context;
    Request request;
    HttpUrl.Builder urlBuilder;
    String url;
    responseListerner mylisterner;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public connection(Activity activity, Context context,String url){
        client=new OkHttpClient();
        this.activity=activity;
        this.context=context;
        this.url=url;
        urlBuilder=HttpUrl.parse(url).newBuilder();

    }

    public void addQuery(String key,String value){
        urlBuilder.addQueryParameter(key,value);
    }

    public void buildUrlAndRequest(){
        String url=urlBuilder.build().toString();
        request=new Request.Builder().url(url).build();
    }

    public void setListenerForFragment(Fragment fragment){
        mylisterner=(responseListerner) fragment;
    }

    public void setListenerForAdpter(FragmentStatePagerAdapter adapter){
        mylisterner=(responseListerner) adapter;
    }

    public void setListenerForActivity(Context context){
        mylisterner=(responseListerner)context;
    }

    public void buildUrlAndPostRequestWithFile(File file,String fileName,String[]...params){

        MultipartBody.Builder builder=new MultipartBody.Builder().setType(MultipartBody.FORM);
        for(int i=0;i<params.length;i++){
            builder.addFormDataPart(params[i][0],params[i][1]);
        }
        builder.addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("image/png"), file));
        RequestBody body=builder.build();
        request=new Request.Builder().url(url).post(body).build();

    }

    public void buildUrlAndPostRequest(String[]...params){

        MultipartBody.Builder builder=new MultipartBody.Builder().setType(MultipartBody.FORM);
        for(int i=0;i<params.length;i++){
            builder.addFormDataPart(params[i][0],params[i][1]);
        }
        RequestBody body=builder.build();
        request=new Request.Builder().url(url).post(body).build();

    }

    public Response getResponse() throws IOException{
        return client.newCall(request).execute();
    }


    public  void getResponseAync() throws IOException{
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mylisterner.failure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mylisterner.success(response);
            }
        });
    }
    public  interface responseListerner{
        void failure();
        void success(Response response);
    }

    public  void fileUpload(File file,String fileName) throws IOException{
        RequestBody body=new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("image/png"), file))
                .build();
        request=new Request.Builder().url(url).post(body).build();
    }

}

