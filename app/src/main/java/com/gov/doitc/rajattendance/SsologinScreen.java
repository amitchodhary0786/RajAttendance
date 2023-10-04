package com.gov.doitc.rajattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.gov.doitc.rajattendance.encreaprion.AES;
import com.gov.doitc.rajattendance.utils.NetworkUtils;
import com.gov.doitc.rajattendance.utils.WebServiceHandler;
import com.gov.doitc.rajattendance.utils.WebServiceListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SsologinScreen extends AppCompatActivity {
    EditText userid,password;
    Button submit;
    String str_email,str_password,enc_pass;
    private NetworkUtils newtworkutils;
    @Override
    protected void onStart() {
        super.onStart();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        newtworkutils = new NetworkUtils();
        if (newtworkutils.haveNetworkConnection(SsologinScreen.this)) {

        }
        else {
            Snackbar snackbar = Snackbar
                    .make(userid, "No internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssologin_screen);

        userid = (EditText)findViewById(R.id.login_edtUsername);
        password = (EditText)findViewById(R.id.login_edtPassword);
        submit = (Button) findViewById(R.id.btn_loginsso);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });

    }

    public void checkValidation(){
        str_email = userid.getText().toString().trim();
        str_password = password.getText().toString().trim();
        if(str_email.equals("")){
            Toast.makeText(getApplicationContext(),"Please Enter SSOID",Toast.LENGTH_LONG).show();
        }
        else if(str_password.equals("")){
            Toast.makeText(getApplicationContext(),"Please Enter Password",Toast.LENGTH_LONG).show();
        }

        else {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //enc_pass = passwordencreaption(str_password);
                        SsologinfunctionsDirect(str_email,str_password);
                       // apiCall();

                    } catch (Exception e) {

                    }
                }
            });
        }
    }




    public void SsologinfunctionsDirect(String str_email,String enc_pass1){
        Log.e("@@@",str_email+" "+enc_pass1);
        WebServiceHandler webServiceHandler =new WebServiceHandler(SsologinScreen.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) throws JSONException {

                  Log.e("SSOResponceDirect",response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(response);
                            String userId = (String) json.getString("userId");
                            String token = (String) json.getString("token");
                            String ssoid = (String)json.getString("ssoid");
                            String userType = (String)json.getString("userType");
                            String valid = json.getString("valid");


                            if(valid.equals("true")){
                                SharedPreferences sharedPreferencesdepartment = getApplicationContext().getSharedPreferences("RajAttendanceLogInTokan",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferencesdepartment.edit();
                                editor.putString("ssoLoginTokan",token);
                                editor.putString("ssoid",ssoid);
                                editor.commit();

                                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else {
                                Snackbar snackbar = Snackbar
                                        .make(userid, "SSO LogIn Failed ", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailer(String failure) throws JSONException {
                Log.e("SSOResponcefailure",failure);
            }
        };
        webServiceHandler.ssoLogin11(str_email,enc_pass1);
    }

    public String passwordencreaption(String str_password){

        AES aes = new AES();
        String password = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            password = aes.encrypt(str_password,"R@j$S0@02{09}19#");
        }

        //      Log.e("encreaptttt", aes.encrypt(str_password,"R@j$S0@02{09}19#"));

        return password;
    }

}