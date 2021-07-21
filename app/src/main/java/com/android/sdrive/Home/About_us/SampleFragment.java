package com.android.sdrive.Home.About_us;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.sdrive.R;


public class SampleFragment extends Fragment {

    Context context;
    TextView titleView,fragmnet_text;

    public SampleFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sample, container, false);
        String title = "Fragment";
        String text = "text";
        if (getArguments() != null) {
            title = getArguments().getString("title");
            text = getArguments().getString("text");
        }
        titleView = view.findViewById(R.id.fragmentTitle);
        fragmnet_text = view.findViewById(R.id.fragmnet_text);
        titleView.setText(title);
        fragmnet_text.setText(text);
        return view;
    }

    public static SampleFragment newInstance(String title,String fragmnet_text) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("text", fragmnet_text);
        SampleFragment fragment = new SampleFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
