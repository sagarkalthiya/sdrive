package com.android.sdrive.Home;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sdrive.Component.ConnectivityReceiver;
import com.android.sdrive.Component.MyApplication;
import com.android.sdrive.Component.ResideMenu;
import com.android.sdrive.Component.ResideMenuItem;
import com.android.sdrive.Component.SessionManager;
import com.android.sdrive.DataBase.DatabaseHelper;
import com.android.sdrive.DataBase.Tables.FileStorage;
import com.android.sdrive.Home.About_us.Aboutus_Activity;
import com.android.sdrive.Home.Fragment.AboutusFragment;
import com.android.sdrive.Home.Fragment.HomeFragment;
import com.android.sdrive.Home.Fragment.ProfileFragment;
import com.android.sdrive.Login_Pages.Login_Activity;
import com.android.sdrive.R;

import java.util.HashMap;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener , ConnectivityReceiver.ConnectivityReceiverListener{

    private ResideMenu resideMenu;
    private HomeActivity mContext;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemAboutus;
    private ResideMenuItem itemlogout;
    SessionManager session;
    DatabaseHelper databaseHelper;
    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        session = new SessionManager(getApplicationContext());
       session.checkLogin();
        databaseHelper = new DatabaseHelper(getApplicationContext());
        homeFragment = new HomeFragment();
        checkConnection();
        mContext = this;
        setUpMenu();
        if( savedInstanceState == null )
            changeFragment(new HomeFragment());

      //  Fregment(new HomeFragment());
    }


    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setUse3D(true);
        resideMenu.setBackground(R.drawable.back_img);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome     = new ResideMenuItem(this, R.drawable.home,     "Home");
        itemProfile  = new ResideMenuItem(this, R.drawable.profile_pic,  "Profile");
        itemAboutus = new ResideMenuItem(this, R.drawable.aboutus, "About Us");
        itemlogout = new ResideMenuItem(this, R.drawable.logout, "Log Out");

        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        itemAboutus.setOnClickListener(this);
        itemlogout.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemAboutus, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemlogout, ResideMenu.DIRECTION_RIGHT);

        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

     /*   findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });*/
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {

        if (view == itemHome){
            changeFragment(new HomeFragment());
        }else if (view == itemProfile){
            changeFragment(new ProfileFragment());
        }else if (view == itemAboutus){
            Intent i = new Intent(getApplicationContext(), Aboutus_Activity.class);
            startActivity(i);
        }else if (view == itemlogout){
            //changeFragment(new SettingsFragment());
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Logout")
                    .setMessage("Are you sure you want to Logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseHelper.DeleteTable(FileStorage.TABLE_NAME);
                            session.logoutUser();
                            Intent i = new Intent(getApplicationContext(), Login_Activity.class);
                            startActivity(i);
                        }
                    }).setNegativeButton("No", null).show();

        }

        resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
           // Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
           // Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.HomeFrameLayout, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu(){
        return resideMenu;
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("No", null).show();
    }




    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
           // homeFragment;
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
