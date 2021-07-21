package com.android.sdrive.Login_Pages;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sdrive.All_URL_End_Point;
import com.android.sdrive.Component.SessionManager;
import com.android.sdrive.Home.HomeActivity;
import com.android.sdrive.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Login_Activity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btGo;
    private CardView cv;
    private TextView forget_btn;
    private FloatingActionButton fab;
    String email = null;
    private JSONObject jsonObject;
    // Session Manager Class
    SessionManager session;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // accessing the layout
        setContentView(R.layout.activity_login);
        initView();
        setListener();
        session = new SessionManager(getApplicationContext());
        progressDialog = new ProgressDialog(Login_Activity.this);
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("please wait..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);

    }

    // inti view access the component of xml layout
    private void initView() {
        etUsername = findViewById(R.id.et_email);
      //  Toast.makeText(this, "Android Device Registered Email Address: " + email, Toast.LENGTH_LONG).show();

        etPassword = findViewById(R.id.et_password);
        btGo = findViewById(R.id.bt_go);
        cv = findViewById(R.id.cv);
        fab = findViewById(R.id.fab);
        forget_btn = findViewById(R.id.forget_btn);


    }

// listerner it is using while user click on the button or other thinghs
    private void setListener() {


        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(Login_Activity.this);
                final String email = etUsername.getText().toString();
                final String pass = etPassword.getText().toString();
                if (!isValidEmail(email)) {
                    etUsername.setError("Invalid Email");
                } else if (!isValidPassword(pass)) {
                    etPassword.setError("Invalid Password or Password must be 8 character");
                } else {
                    //  network compiler post the method on server
                    progressDialog.show();
                  AndroidNetworking.post(All_URL_End_Point.LOGIN)
                            .addBodyParameter("email", email)
                            .addBodyParameter("password", pass)
                            .setTag("test")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                   // response get by server

                                    // do anything with response
                                    Log.w("------", "hello+--" + response);
                                    try {

                                        //  status chacking by the condition if it is true then you go for the home page or not valid then it goes else condition
                                        Log.w("wwwwwwww", "resss+--" + response.getBoolean("status"));
                                        if (response.getBoolean("status")== true) {
                                            JSONObject data = new JSONObject(response.getString("data"));
                                            String user_id = data.getString("id");
                                            String user_name = data.getString("name");
                                            String user_email = data.getString("email");
                                           // Toast.makeText(Login_Activity.this, user_id+user_name+user_email, Toast.LENGTH_SHORT).show();

                                            session.createLoginSession(user_id,user_name, user_email);
                                            progressDialog.dismiss();
                                            Explode explode = new Explode();
                                            explode.setDuration(500);
                                            getWindow().setExitTransition(explode);
                                            getWindow().setEnterTransition(explode);
                                            ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(Login_Activity.this);
                                            Intent i2 = new Intent(Login_Activity.this, HomeActivity.class);
                                            startActivity(i2, oc2.toBundle());
                                        }else {
                                            progressDialog.dismiss();
                                            Snackbar snackbar  = Snackbar.make(findViewById(android.R.id.content), "Username or password wrong.", Snackbar.LENGTH_LONG);
                                            snackbar.show();

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError error) {
                                    Log.w("------", "hello" + error);
                                    progressDialog.dismiss();
                                    Snackbar snackbar  = Snackbar.make(findViewById(android.R.id.content), "Somethings wrong.", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    // handle error
                                }
                            });

                }

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login_Activity.this, fab, fab.getTransitionName());
                startActivity(new Intent(Login_Activity.this, Signup_Activity.class), options.toBundle());
            }
        });

        forget_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login_Activity.this, fab, fab.getTransitionName());
                startActivity(new Intent(Login_Activity.this, Forgetpass_Activity.class), options.toBundle());
            }
        });

    }


    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{3,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 7) {
            return true;
        }
        return false;
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onRestart() {
        super.onRestart();
        fab.setVisibility(View.GONE);

        // forget_btn.setVisibility(View.GONE);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
        // forget_btn.setVisibility(View.VISIBLE);
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
}