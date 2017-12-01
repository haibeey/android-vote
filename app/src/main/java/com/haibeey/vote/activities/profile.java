package com.haibeey.vote.activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.haibeey.vote.R;
import com.haibeey.vote.connections.connection;
import com.haibeey.vote.datas.cacheClass;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Response;

public class profile extends AppCompatActivity implements connection.responseListerner{

    private CircleImageView circleImageView;
    private TextView tv1,tv2,tv3;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("My Profile");

        //create insatance of view element
        cardView=(CardView)findViewById(R.id.cc);
        circleImageView=(CircleImageView)findViewById(R.id.dimage);
        tv1=(TextView)findViewById(R.id.dname);
        tv2=(TextView)findViewById(R.id.dname2);
        tv3=(TextView)findViewById(R.id.dname3);

        cacheClass cacheclass=new cacheClass(this,"login");
        String mail=cacheclass.getString("email","email");
        String firstName=cacheclass.getString("firstName","firstname");
        String lastName=cacheclass.getString("lastName","lastname");

        //setting up available profile
        tv1.setText(firstName);
        tv2.setText(lastName);
        tv3.setText(mail);

        connection con=new connection(this,this,"http://haibeeyy.pythonanywhere.com/profile");
        con.addQuery("email",mail);
        con.buildUrlAndRequest();
        con.setListenerForActivity(this);

        try {
            con.getResponseAync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failure() {
        Snackbar.make(cardView,"Something went Wrong",Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void success(Response response) {
        try {
            String result=response.body().string();
            final JSONObject jsonObject=new JSONObject(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        tv1.setText(jsonObject.getString("first_name"));
                        tv2.setText(jsonObject.getString("last_name"));
                        Picasso.with(getApplicationContext()).load("http://haibeeyy.pythonanywhere.com/file/"+jsonObject.getString("image")).into(circleImageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
