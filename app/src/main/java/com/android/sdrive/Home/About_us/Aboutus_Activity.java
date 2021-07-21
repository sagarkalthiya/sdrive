package com.android.sdrive.Home.About_us;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.android.sdrive.R;

import java.util.ArrayList;

public class Aboutus_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> menuList = new ArrayList<>();
        ArrayList<String> text = new ArrayList<>();     //menu titles
        ArrayList<Integer> imagesList = new ArrayList<>();      //menu backgrounds
        ArrayList<Fragment> fragmentsList = new ArrayList<>();      //fragments for each menu headers in second activity

        menuList.add("About Us Application");       //add titles
        menuList.add("SAGAR");
        menuList.add("Rohit Namdev");
        menuList.add("Rohit Chouhan");
        menuList.add("Nilesh Pal");
        menuList.add("Atul Yadav");
        menuList.add("Jaishree");


        text.add("About Us Application");       //add fragment name
        text.add("SAGAR");
        text.add("Rohit Namdev");
        text.add("Rohit Chouhan");
        text.add("Nilesh Pal");
        text.add("Atul Yadav");
        text.add("Jaishree");

        imagesList.add(R.drawable.img_aboutus_application);        //add background images
        imagesList.add(R.drawable.img_sagar);
        imagesList.add(R.drawable.img_namdee);
        imagesList.add(R.drawable.img_chouhan);
        imagesList.add(R.drawable.img_nilesh);
        imagesList.add(R.drawable.img_temur);
        imagesList.add(R.drawable.img_jayshree);

        String about = getResources().getString(R.string.about_us_app);
        String sagar = getResources().getString(R.string.sagar);
        String namdev = getResources().getString(R.string.namdev);
        String chouhan = getResources().getString(R.string.chouhan);
        String nilu = getResources().getString(R.string.nilesh);
        String temur = getResources().getString(R.string.temur);
        String jayshree = getResources().getString(R.string.jayshree);


        fragmentsList.add(SampleFragment.newInstance("About Us Application",about));      //add fragment instances
        fragmentsList.add(SampleFragment.newInstance("SAGAR",sagar));
        fragmentsList.add(SampleFragment.newInstance("Rohit Namdev",namdev));
        fragmentsList.add(SampleFragment.newInstance("Rohit Chouhan",chouhan));
        fragmentsList.add(SampleFragment.newInstance("Nilesh Pal",nilu));
        fragmentsList.add(SampleFragment.newInstance("Atul Yadav",temur));
        fragmentsList.add(SampleFragment.newInstance("Jaishree",jayshree));


        Allagi allagi = Allagi.initialize(Aboutus_Activity.this,text, menuList, imagesList, fragmentsList);
        allagi.start();         //start the menu list activity

    }
}