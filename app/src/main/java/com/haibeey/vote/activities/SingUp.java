package com.haibeey.vote.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haibeey.vote.R;
import com.haibeey.vote.connections.connection;
import com.haibeey.vote.datas.cacheClass;
import com.haibeey.vote.datas.utilities;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingUp extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView first_name;
    private AutoCompleteTextView last_name;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private  CircleImageView circleImageView;
    String fileName;

    private  connection Con;
    private String baseUrl="http://haibeeyy.pythonanywhere.com/signup";
    utilities mUtilities;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Sign Up");

        Con=new connection(this,this,baseUrl);
        mUtilities=new utilities(this,this);

        // Set up the login form.
        first_name = (AutoCompleteTextView) findViewById(R.id.first_name);
        last_name = (AutoCompleteTextView) findViewById(R.id.last_name);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        linearLayout=(LinearLayout)findViewById(R.id.signupLL);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        circleImageView=(CircleImageView)findViewById(R.id.decircle);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I=new Intent(Intent.ACTION_PICK);
                I.setType("image/*");
                startActivityForResult(I,1);
            }
        });
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String last=last_name.getText().toString();
        String first=first_name.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(first)){
            first_name.setError("This field can't be empty");
            cancel=true;
        }
        if (TextUtils.isEmpty(last)){
            last_name.setError("This field can't be empty");
            cancel=true;
        }
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            if(mUtilities.isConnected()){
                mAuthTask = new UserLoginTask(first,last,email, password);
                mAuthTask.execute((Void) null);
            }else{
                Snackbar.make(linearLayout,"No internet connection",Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.matches("[a-z 0-9 A-Z]+@[a-z]+.com");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int req,int res,Intent data){
        try{
            super.onActivityResult(req,res,data);
            Uri selectedImage = data.getData();
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
            circleImageView.setImageBitmap(bitmap);
            fileName=getRealPathFromURI(selectedImage);
        }catch (Exception e){
            Snackbar.make(linearLayout,"you didnt select any image",Snackbar.LENGTH_SHORT).show();
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String first;
        private final String last;
        String response;
        String message;

        UserLoginTask(String firstName,String lastName,String email, String password) {
            mEmail = email;
            mPassword = password;
            first=firstName;
            last=lastName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(fileName==null){
                Snackbar.make(linearLayout,"you need to select an Image",Snackbar.LENGTH_SHORT).show();
                return false;
            }
            File file=new File(fileName);
            if(!file.isFile()){
                Snackbar.make(linearLayout,"you need to select an Image",Snackbar.LENGTH_SHORT).show();
                return false;
            }
            Con.buildUrlAndPostRequestWithFile(file,fileName,new String[]{"email",mEmail},new String[]{"firstname",first},
                    new String[]{"password",mPassword},new String[]{"lastname",last});

            try {
                response = Con.getResponse().body().string();
                JSONObject jObj=new JSONObject(response);
                message=jObj.getString("message");
                if (!jObj.getString("response").equals("ok")){
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;}
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                cacheClass CacheClass=new cacheClass(getApplicationContext(),"login");
                CacheClass.putString("email",mEmail);
                CacheClass.putString("password",weekEncrypt(mPassword));
                CacheClass.putString("firstName",first);
                CacheClass.putString("lastName",last);
                gotoMainActivity();
            } else {
                Snackbar.make(linearLayout,message,Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    private void gotoMainActivity(){
        startActivity(new Intent(getApplicationContext(),Home.class));
    }

    private String weekEncrypt(String name){
        Random random=new Random();
        String alphabets="abcdefghijklmnopqrstuvwxyz";
        String res="";
        for(int i=0;i<name.length();i++){
            int r=random.nextInt(26);
            res+=name.substring(i,i+1)+alphabets.substring(r,r+1);
        }
        return res;
    }
}

