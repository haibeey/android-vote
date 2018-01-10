package com.haibeey.vote.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.haibeey.vote.R;
import com.haibeey.vote.activities.Home;
import com.haibeey.vote.activities.profile;
import com.haibeey.vote.connections.connection;
import com.haibeey.vote.datas.cacheClass;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Response;

public class TabFragment2 extends Fragment implements connection.responseListerner{
    Context context;
    String id,theTopic;
    private static int options=0;
    Fragment me;
    EditText editTextOption;
    ProgressBar progressBar;
    CircleImageView circleImageView;
    private  EditText editText;
    private boolean done=false;

    View v;
    public TabFragment2(){

    }


    public static TabFragment2 newInstance(String param1, String param2) {
        TabFragment2 fragment = new TabFragment2();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_tab_fragment2, container, false);
        setButtonListener(v);
        return v;
    }

    private void setButtonListener(View v){
        Button button2,button3;//buttons


        editText=(EditText)v.findViewById(R.id.oneChild);
        button2=(Button)v.findViewById(R.id.optionButton);
        button3=(Button)v.findViewById(R.id.createButton);
        editTextOption=(EditText)v.findViewById(R.id.twoChild);
        progressBar=(ProgressBar)v.findViewById(R.id.login_progress);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theTopic=editText.getText().toString();
                new AlertDialog.Builder(getContext()).setPositiveButton(
                        "Continue with out image", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //make request to add a pending option
                                new requester().execute("cache","http://haibeeyy.pythonanywhere.com/create_poll","",editTextOption.getText().toString());
                            }
                        }
                ).setNegativeButton("Select an Image", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //gets the image
                        Intent I=new Intent(Intent.ACTION_PICK);
                        I.setType("image/*");
                        circleImageView.setVisibility(View.VISIBLE);
                        startActivityForResult(I,1);
                    }
                }).show();
            }
        });

        //create the poll
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(options<2){
                    Snackbar.make(v,"You Need To Add Atleast two options",Snackbar.LENGTH_SHORT).show();
                }else{
                    new requester().execute("final","http://haibeeyy.pythonanywhere.com/create_poll");
                }

            }
        });
        //set the imageView
        circleImageView=(CircleImageView)v.findViewById(R.id.optionImage);
    }

    @Override
    public void onActivityResult(int req,int res,Intent data){
        try{

            super.onActivityResult(req,res,data);
            Uri selectedImage = data.getData();
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage));
            CircleImageView circleImageView=(CircleImageView)v.findViewById(R.id.optionImage);
            circleImageView.setImageBitmap(bitmap);
            actionOnButtonAddOption(getRealPathFromURI(selectedImage));

        }catch (Exception e){
            Snackbar.make(v,"you didnt select any image",Snackbar.LENGTH_SHORT).show();
        }
    }

    public String getRealPathFromURI(Uri uri) {
        //gets data path given a url
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context=context;
        cacheClass cacheclass=new cacheClass(context,"login");
        id=cacheclass.getString("email","email");
        me=this;
    }

    private void actionOnButtonAddOption(String imageName){
        new requester().execute("cache","http://haibeeyy.pythonanywhere.com/create_poll",imageName,editTextOption.getText().toString());
        new requester().execute("save","http://haibeeyy.pythonanywhere.com/create_poll",imageName);
    }

    @Override
    public void failure() {
        Snackbar.make(v,"error connecting",Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void success(Response response) {
        JSONObject jobj= null;
        try {
            String g=response.body().string();
            Log.i("resultab",g);
            jobj = new JSONObject(g);
            if(jobj.getString("response").equals("ok")){
                options+=1;
                Snackbar.make(v,"option is pending",Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class requester extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {

                if (params[0].equals("cache")){
                    //setups the cache
                    connection Connection=new connection(getActivity(),context,params[1]);
                    Connection.addQuery("cache","cache");
                    Connection.addQuery("topic",theTopic);
                    Connection.addQuery("email",id);
                    Connection.addQuery("name",params[3]);
                    Connection.addQuery("image",params[2]);
                    Connection.buildUrlAndRequest();
                    try {
                        Connection.setListenerForFragment(me);
                        Connection.getResponseAync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(params[0].equals("save")){
                    //network call to save an  image
                    connection Connection2=new connection(getActivity(),context,params[1]);
                    File file=new File(params[2]);
                    String[] stringArr=new String[]{"save","save"};
                    Connection2.buildUrlAndPostRequestWithFile(file,params[2],stringArr);//sends the file

                    try {
                        String result=Connection2.getResponse().body().string();
                        Log.i("resultforfile",result);
                        if(new JSONObject(result).getString("response").equals("ok")){
                            //Toast.makeText(context,"image file added successully",Toast.LENGTH_SHORT);
                            //Snackbar.make
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Snackbar.make(v,"something went wrong when add file ",Snackbar.LENGTH_SHORT).show();
                    }catch (JSONException e){
                        Snackbar.make(v,"something went wrong when add file ",Snackbar.LENGTH_SHORT).show();
                    }
                }
                else if(params[0].equals("final")){
                    connection Connection3=new connection(getActivity(),context,params[1]);
                    Connection3.addQuery("topic",theTopic);
                    Connection3.addQuery("email",id);
                    Connection3.buildUrlAndRequest();
                    try {
                        String result =Connection3.getResponse().body().string();
                        try {
                            JSONObject jsonObject=new JSONObject(result);
                            if(jsonObject.getString("response").equals("ok")){
                                Snackbar.make(v,"you created a poll go invite users to vote",Snackbar.LENGTH_SHORT).show();

                                Intent I=new Intent(context,Home.class);
                                startActivity(I);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(v,"unable to create poll please try again",Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Snackbar.make(v,"unable to create poll please try again",Snackbar.LENGTH_SHORT).show();
                    }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            editTextOption.setText("");
            theTopic=editText.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            circleImageView.setVisibility(View.GONE);
        }

    }
}
