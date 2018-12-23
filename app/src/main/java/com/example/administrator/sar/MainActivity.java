package com.example.administrator.sar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.LocaleDisplayNames;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Fragment ProfileFragment;
    Fragment MapFragment;
    Fragment CommunityFragment;
    Fragment SettingFragment;
    Fragment ARFragment;
    ImageView ProfileImage;
    ActionBar actionBar;
    static FragmentTransaction ft;
    private TextView userName;
    private TextView userEmail;
    private FirebaseAuth auth;
    private FirebaseUser user;

    String TAG = "MainActivity- ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "requestPermissions");
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            //requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        }

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); //홈버튼 display (뒤로가기 대용)
        actionBar.setHomeAsUpIndicator(R.drawable.stamp);
        toolbar.setBackgroundColor(Color.rgb(80,169,255));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorToolbar));



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        /*
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        */

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerViewview = navigationView.getHeaderView(0);
        userEmail = (TextView) headerViewview.findViewById(R.id.header_userEmail);
        userName = (TextView) headerViewview.findViewById(R.id.header_userName);
        ProfileImage = (ImageView)headerViewview.findViewById(R.id.imageView);


        userEmail.setText(user.getEmail());

        //Log.d(TAG,"getDisplayName: "+auth.getCurrentUser().getDisplayName().length());

        userName.setText(user.getDisplayName());
        /*
        if(user.getDisplayName().equals("") ){
            Log.d(TAG,"getDisplayName: true");
            userName.setText(user.getDisplayName());
        }else {
            Log.d(TAG,"getDisplayName: false");
            userName.setText("익명");
        }
        */

        if (auth.getCurrentUser().getPhotoUrl() != null){
            Glide.with(this).load(auth.getCurrentUser().getPhotoUrl()).into(ProfileImage);
            //ProfileImage.setImageURI(auth.getCurrentUser().getPhotoUrl());
        }
        //Glide.with(layout).load(auth.getCurrentUser().getPhotoUrl()).into(ProfileImage);

         ProfileFragment = new ProfileFragment();
         MapFragment = new MapFragment();
         CommunityFragment = new CommunityFragment();
         SettingFragment = new SettingFragment();
         ARFragment = new ARFragment();

        FragmentTrans(MapFragment);
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");

        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            actionBar.setHomeAsUpIndicator(R.drawable.stamp);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: "+item.getItemId());
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_menu:
                drawer.openDrawer(GravityCompat.END);
                return true;

            case android.R.id.home:
                actionBar.setHomeAsUpIndicator(R.drawable.stamp);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();
                //actionBar.setHomeAsUpIndicator(R.drawable.stamp);
                /*
                if(!(ft.isEmpty())){
                    onBackPressed();
                }*/
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected");
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_map:
                FragmentTrans(MapFragment);
                break;
            case R.id.nav_AR:
                FragmentTrans(ARFragment);
                break;
            case R.id.nav_Community:
                FragmentTrans(CommunityFragment);
                break;
            case R.id.nav_Setting:
                FragmentTrans(SettingFragment);
                break;
            case R.id.logOut:
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
        }

        drawer.closeDrawer(GravityCompat.END);

        return true;
    }

    public void ProfileClick(View v) {
        Log.d(TAG, "ProfileClick");
        FragmentTrans(ProfileFragment);
        drawer.closeDrawer(GravityCompat.END);
    }

    public void FragmentTrans(Fragment fragment){
        if (fragment != null) {
            Log.d(TAG, "FragmentTrans: "+fragment);
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            ft.commit();
        }
    }
}
