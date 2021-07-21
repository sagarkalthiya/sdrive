package com.android.sdrive.Home.About_us;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hari on 19/4/18.
 */

public class CustomPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private List<String> mFragmentTitles;
    private List<String> mFragmentText;

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
        mFragmentTitles = new ArrayList<>();
        mFragmentText = new ArrayList<>();
    }

    public void addFragment(Fragment fragment, String title, String text) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
        mFragmentText.add(text);
    }

    public void addFragmentsList(ArrayList<Fragment> fragments, ArrayList<String> titles, ArrayList<String> text) {
        mFragments = fragments;
        mFragmentTitles = titles;
        mFragmentText = text;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

}
