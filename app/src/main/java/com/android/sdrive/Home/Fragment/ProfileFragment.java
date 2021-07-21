package com.android.sdrive.Home.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sdrive.All_URL_End_Point;
import com.android.sdrive.Component.SessionManager;
import com.android.sdrive.Home.HomeActivity;
import com.android.sdrive.Login_Pages.Login_Activity;
import com.android.sdrive.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    EditText et_name, et_email, et_password, et_repet_password;
    TextView btn_save;
    SessionManager session;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, null);
        ButterKnife.bind(this, rootView);
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("please wait..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);
        // name
        final String user_id = user.get(SessionManager.KEY_USER_ID);

        final String name = user.get(SessionManager.KEY_NAME);

        // email
        String email = user.get(SessionManager.KEY_EMAIL);

        et_name = (EditText) rootView.findViewById(R.id.user_name_et);
        et_email = (EditText) rootView.findViewById(R.id.user_email_et);
        et_password = (EditText) rootView.findViewById(R.id.user_password_et);
        et_repet_password = (EditText) rootView.findViewById(R.id.user_repet_password_et);
        btn_save = (TextView) rootView.findViewById(R.id.save_btn);

        Toast.makeText(getActivity(), "session" + user_id, Toast.LENGTH_SHORT).show();

        et_name.setText(name);
        et_email.setText(email);


        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "sagar");




        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.profile();
                progressDialog.show();
                final String name = et_name.getText().toString();
                final String pass = et_password.getText().toString();
                final String repass = et_repet_password.getText().toString();
               if (!pass.equals(repass)) {
                    et_repet_password.setError("Invalid Password or Password must be 8 character");
                } else {
                    AndroidNetworking.post(All_URL_End_Point.PROFILE_UPDATE)
                            .addBodyParameter("user_id", user_id)
                            .addBodyParameter("name", name)
                            .addBodyParameter("password",pass )
                            .setTag("test")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // response get by server

                                    // do anything with response
                                    Log.w("------", "update+--" + response);
                                    try {

                                        //  status chacking by the condition if it is true then you go for the home page or not valid then it goes else condition

                                        if (response.getBoolean("status")) {
                                            progressDialog.dismiss();
                                            JSONObject data = new JSONObject(response.getString("data"));
                                            String user_id = data.getString("id");
                                            String user_name = data.getString("name");
                                            String user_email = data.getString("email");
                                            // Toast.makeText(Login_Activity.this, user_id+user_name+user_email, Toast.LENGTH_SHORT).show();

                                            session.createLoginSession(user_id, user_name, user_email);
                                        } else {

                                            progressDialog.dismiss();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onError(ANError error) {
                                    Log.w("------", "hello" + error);
                                    // handle error
                                    progressDialog.dismiss();
                                }
                            });

                }

            }
        });




        return rootView;
    }

}
