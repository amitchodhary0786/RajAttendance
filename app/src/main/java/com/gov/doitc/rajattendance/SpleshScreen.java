package com.gov.doitc.rajattendance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.gov.doitc.rajattendance.utils.WebServiceHandler;
import com.gov.doitc.rajattendance.utils.WebServiceListener;

import org.json.JSONException;
import org.json.JSONObject;

public class SpleshScreen extends AppCompatActivity {
    TextView splesh;
    String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh_screen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        splesh = findViewById(R.id.tv_splesh);

        versionName = BuildConfig.VERSION_NAME;

        spleshfunction();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void spleshfunction() {
        Boolean networkcheck = isNetworkAvailable();
        if (networkcheck) {
            SharedPreferences sharedPreferencesdepartment = getApplicationContext().getSharedPreferences("RajAttendanceLogInTokan", MODE_PRIVATE);
            String ssoLoginTokan = sharedPreferencesdepartment.getString("ssoLoginTokan", "null");
            String SSOID = sharedPreferencesdepartment.getString("ssoid", "null");
            Log.e("tokan@@", ssoLoginTokan + " " + SSOID);
            getAttendanceStatus(SSOID, ssoLoginTokan);
            //checkAppVersion();
        } else {
            Snackbar snackbar = Snackbar
                    .make(splesh, "No internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();

        }
    }



    private void getAttendanceStatus(String ssid, String tokan) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(SpleshScreen.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) throws JSONException {

                Log.e("GetLoginStatus01", response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(response);
                            String valid = json.getString("valid");
                            String appVersion = json.getString("appVersion");
                            if (appVersion.equals(versionName)) {
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(2000);
                                        } catch (Exception e) {

                                        } finally {
                                            if (valid.equals("true")) {

                                                Intent intent = new Intent(SpleshScreen.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Intent intent = new Intent(SpleshScreen.this, SsologinScreen.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                    }

                                };
                                thread.start();

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertDialog();
                                        //Toast.makeText(SpleshScreen.this, "update application playstore", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailer(String failure) throws JSONException {
                Toast.makeText(SpleshScreen.this, failure, Toast.LENGTH_LONG).show();
            }
        };
        webServiceHandler.getLoginStatus(ssid, tokan);
    }

    private void alertDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.applogo)
                .setTitle("High Priority")
                .setMessage("download the new version of the application")
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        //Log.e("packagename",appPackageName);
                        //String appPackageName = "com.dreamplug.androidapp";
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }

                })
                .show();

    }

}