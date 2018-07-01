package com.haibeey.vote.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.haibeey.vote.R;
import com.haibeey.vote.adapters.TabAdapter;
import com.haibeey.vote.connections.connection;
import com.haibeey.vote.datas.cacheClass;
import com.haibeey.vote.datas.dbHelper;
import com.haibeey.vote.datas.utilities;
import com.haibeey.vote.fragments.TabFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public ViewPager viewPager;
    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private DrawerLayout drawerLayout;
    private android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setting up ui
        viewPager=(ViewPager)findViewById(R.id.vpager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name, R.string.app_name);

        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();

        //setting up tab adapter
        tabAdapter=new TabAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(tabAdapter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.sign_up:
                if (item.isVisible()){
                    startActivity(new Intent(getApplicationContext(),SingUp.class));
                }
                return  true;
            case R.id.login:
                if (item.isVisible()){
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }
                return  true;

        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.create) {
            startActivity(new Intent(this,createPoll.class));

        } else if (id == R.id.profile) {
            startActivity(new Intent(this,profile.class));

        } else if (id == R.id.share) {

            Intent I=new Intent(Intent.ACTION_SEND);
            I.setType("text/plain");
            I.putExtra(Intent.EXTRA_TEXT,"Check out this Vote app to create and vote");
            startActivity(I);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
