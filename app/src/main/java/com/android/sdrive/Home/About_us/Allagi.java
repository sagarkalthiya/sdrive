package com.android.sdrive.Home.About_us;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Created by hari on 15/4/18.
 */

public class Allagi {

    static ArrayList<Fragment> fragments;
    private Activity activity;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> text = new ArrayList<>();
    private ArrayList<Integer> imagesList = new ArrayList<>();
    static long transitionDuration = 700;

    private Allagi(@NonNull Activity activity, ArrayList<String> list,ArrayList<String> text, ArrayList<Integer> imagesList, ArrayList<Fragment> fragmentsList) {
        this.activity = activity;
        this.list = list;
        this.text = text;
        this.imagesList = imagesList;
        fragments = fragmentsList;
    }

    public static Allagi initialize(@NonNull Activity activity, ArrayList<String> list,ArrayList<String> text,ArrayList<Integer> imagesList, ArrayList<Fragment> fragmentsList) {
        return new Allagi(activity, list,text,imagesList, fragmentsList);
    }

    public static ArrayList<Fragment> getFragments() {
        return fragments;
    }

    public void start() {
        activity.finish();
        MenuListActivity.startActivity(activity, list,text, imagesList);
    }

    public void setTransitionDuration(long milliSeconds) {
        transitionDuration = milliSeconds;
    }

    public static long getTransitionDuration() {
        return transitionDuration;
    }

}
