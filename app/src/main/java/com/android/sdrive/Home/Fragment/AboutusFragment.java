package com.android.sdrive.Home.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sdrive.R;

import butterknife.ButterKnife;

public class AboutusFragment  extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_us, null);
        ButterKnife.bind(this, rootView);


        return rootView;
    }

}
