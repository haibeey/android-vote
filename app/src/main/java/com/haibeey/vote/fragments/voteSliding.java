package com.haibeey.vote.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.haibeey.vote.R;
import com.haibeey.vote.adapters.voteSlidingAdapter;
import com.haibeey.vote.connections.connection;
import com.haibeey.vote.datas.utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Response;


public class voteSliding extends Fragment implements connection.responseListerner {
    String name;
    String count;
    CircleImageView circleImageView;
    String imagename;
    String title;
    Fragment me;
    View v;
    public voteSliding() {
        // Required empty public constructor
    }

    public static voteSliding newInstance(String count, String name) {
        voteSliding fragment = new voteSliding();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setNameCount(String name,String count,String imagename,String title){
        //this method is to be called from the adapter
        this.name=name;
        this.count=count;
        this.imagename=imagename;
        this.title=title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me=this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_vote_sliding, container, false);
        TextView textView1=(TextView)v.findViewById(R.id.dcount);
        TextView textView2=(TextView)v.findViewById(R.id.dname);
        Button button=(Button)v.findViewById(R.id.vote);
        circleImageView=(CircleImageView)v.findViewById(R.id.dimage);
        //loads the image
        Picasso.with(getContext()).load("http://haibeeyy.pythonanywhere.com/file/"+imagename).into(circleImageView);


        //set up a listener to vote
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection Con=new connection(getActivity(),getContext(),"http://haibeeyy.pythonanywhere.com/vote");
                Con.addQuery("ip", utilities.getLocalIPAddress());
                Con.addQuery("choices",name);
                Con.addQuery("topic",title);
                Con.addQuery("vote","vote");
                Con.setListenerForFragment(me);
                Con.buildUrlAndRequest();
                try {
                    Con.getResponseAync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        textView1.setText(count+ " votes");
        textView2.setText(name);
        getActivity().findViewById(R.id.votebar).setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void failure() {
        Snackbar.make(v,"Something went wrong,Try Again!",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void success(Response response) {

        try {
            String result=response.body().string();
            JSONObject jsonObject=new JSONObject(result);
            if(jsonObject.getString("response").equals("ok") && !jsonObject.getString("message").equals("you voted before")){
                Snackbar.make(v,"you just voted for "+name,Snackbar.LENGTH_LONG).show();
                getActivity().finish();
            }else{
                Snackbar.make(v,"you  can only vote once ",Snackbar.LENGTH_LONG).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
