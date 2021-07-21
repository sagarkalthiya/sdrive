package com.android.sdrive.Login_Pages;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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


public class Signup_Activity extends AppCompatActivity {

    private FloatingActionButton fab;
    private CardView cvAdd;
    LinearLayout linerone,linerywo;
    EditText et_name, et_email, et_password ,et_repeat_password;
    View view;
    Button button;
    SessionManager session;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_signup);
        ShowEnterAnimation();
        session = new SessionManager(getApplicationContext());
        progressDialog = new ProgressDialog(Signup_Activity.this);
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("please wait..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);
        initView();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }

    private void initView() {
        fab = findViewById(R.id.fab);
        cvAdd = findViewById(R.id.cv_add);
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_repeat_password =findViewById(R.id.et_repeat_password);

        button = findViewById(R.id.btn_sign_up);
        linerone = findViewById(R.id.linerone);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = et_name.getText().toString();
                final String email = et_email.getText().toString();
                final String password = et_password.getText().toString();
                final String repeat_password =et_repeat_password.getText().toString();
                if(name.isEmpty()){
                    et_name.setError("Enter a name");
                } else if (!isValidEmail(email)) {
                    et_email.setError("Invalid Email");
                } else if (!isValidPassword(password)) {
                    et_password.setError("Invalid Password or Password must be 8 character");
                } else if(!password.equals(repeat_password)){
                    et_repeat_password.setError("Invalid Password");
                } else {
                    progressDialog.show();
                   AndroidNetworking.post(All_URL_End_Point.SIGN_UP)
                            .addBodyParameter("name", name)
                            .addBodyParameter("email", email)
                            .addBodyParameter("password", password)
                            .setTag("test")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // do anything with response
                                    Log.w("------", "hello+--" + response);
                                    try {

                                        if (response.getBoolean("status")) {
                                            Log.w("wwwwwwww", "resss+--" + response.getBoolean("status"));
                                            if (response.getBoolean("account")){
                                                JSONObject data = new JSONObject(response.getString("data"));
                                                String user_id = data.getString("id");
                                                String user_name = data.getString("name");
                                                String user_email = data.getString("email");

                                                session.createLoginSession(user_id,user_name, user_email);
                                                progressDialog.dismiss();
                                                Explode explode = new Explode();
                                                explode.setDuration(500);
                                                getWindow().setExitTransition(explode);
                                                getWindow().setEnterTransition(explode);
                                                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(Signup_Activity.this);
                                                Intent i2 = new Intent(Signup_Activity.this, HomeActivity.class);
                                                startActivity(i2, oc2.toBundle());
                                            }else {
                                                progressDialog.dismiss();
                                                Snackbar snackbar  = Snackbar.make(findViewById(android.R.id.content), "Account already created with this email", Snackbar.LENGTH_LONG);
                                                snackbar.show();
                                            }
                                    }



                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError error) {
                                    Log.w("------>", "hello" + error);
                                    // handle error
                                    progressDialog.dismiss();
                                }
                            });
                }
            }
        });




    }

/*
    public void showToast() {

        View toastLayout = getLayoutInflater().inflate(R.layout.custom_toast, null);

        Toast toast = new Toast(getApplicationContext());
        toast.setView(toastLayout);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

    }*/

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                Signup_Activity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @Override
    public void onBackPressed() {
        animateRevealClose();
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

}
