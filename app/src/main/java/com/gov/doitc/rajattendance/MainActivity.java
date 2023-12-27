package com.gov.doitc.rajattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.gov.doitc.rajattendance.utils.WebServiceHandler;
import com.gov.doitc.rajattendance.utils.WebServiceListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {
    String attendancestatus = "100";
    ImageView iconmarkin,iconmarkout;
    TextView textmatkin,textmarkout;
    int lgreencolor = -4823892;
    ImageView ivmarkin,ivmarkout;

    Toolbar toolbar;
    String ipAddress, macAddress, strDate;
    CardView cvmarkin,cvmarkout;
    RelativeLayout markin,markout;
    TextView textView,tvdate;
    int Location_Req_Code = 1001;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    boolean isGPSEnabled = false;
    String Flag = "";
    TextView tvname,deptname,markintime,markouttime,tv_year,tv_coursename;
    String strname,strdeptname;

    String DPID, HMAC, CI, SKEYINSIDE, DetaPID, RSDVER, RDSID, DC, MI, MC,LOGinLOGouttime;
    String ERRCODE, ERRINFO, FCOUNT, FTYPE, ICOUNT, ITYPE, NMPOINT, PCOUNT, PTYPE, QSCORE, SRNO;
    String dcmistr,filterDCMCstr,FilterUDC,additionalinfosrno;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    private String strLatitude,strLongitude;
    //private String strssoname="amit.chodhary007";
    private SharedPreferences sharedPreferencesdepartment;
    String ssoLoginTokan,ssoid;
    Animation animation,animation1;
    ImageView logout;
    TextView version;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if(locationResult == null){

            }
            for(Location location: locationResult.getLocations()){
                Log.e("LocationList",String.valueOf(location.getLatitude()+" "+location.getLongitude()));

                strLatitude = String.valueOf(location.getLatitude());
                strLongitude = String.valueOf(location.getLongitude());

                //textView.setText("LAT:-"+String.valueOf(location.getLatitude())+" LONG:-"+String.valueOf(location.getLongitude()));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if(getSupportActionBar() !=null){
           // getSupportActionBar().setTitle("Raj-Upasthiti");
        }

        sharedPreferencesdepartment = getApplicationContext().getSharedPreferences("RajAttendanceLogInTokan", MODE_PRIVATE);
        ssoLoginTokan = sharedPreferencesdepartment.getString("ssoLoginTokan", "null");
        ssoid = sharedPreferencesdepartment.getString("ssoid", "null");
        Log.e("Dash_Board",ssoid);
        Log.e("dashTokan",ssoLoginTokan);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeanim);
        animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeanim1);




        tvname = findViewById(R.id.tv_name);
        deptname = findViewById(R.id.tv_deptname);
        tv_year = findViewById(R.id.tv_year);
        tv_coursename = findViewById(R.id.tv_coursename);
        markintime = findViewById(R.id.tv_markintime);
        markouttime = findViewById(R.id.tv_markouttime);
        ivmarkin = findViewById(R.id.iv_markin);
        ivmarkout = findViewById(R.id.iv_markout);
        version = findViewById(R.id.tv_version);

        textmatkin = findViewById(R.id.tv_textmarkin);
        textmarkout = findViewById(R.id.tv_textmarkout);
        iconmarkin = findViewById(R.id.iv_iconmarkin);
        iconmarkout = findViewById(R.id.iv_iconmarkout);

        logout = findViewById(R.id.iv_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog();
            }
        });

        //check status
        getAttendanceStatus(ssoid,ssoLoginTokan);





       // textView = findViewById(R.id.PresenttextView);
        cvmarkin = findViewById(R.id.cv_markin);
        cvmarkout = findViewById(R.id.cv_markout);
        cvmarkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager locationManager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    // checkSettingStartlocationUpdate();
                    new MaterialAlertDialogBuilder(MainActivity.this)
                            .setTitle("GPS disabled")
                            .setMessage("Your Device's location is Mandatory for this App's Usage. Please switch On your device GPS")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                }
                            }).setCancelable(false)
                            .show();
                }
                else {
                    if(strLatitude == null && strLatitude == null ){
                        Toast.makeText(MainActivity.this, "Cant't get Location..", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(attendancestatus.equals("2006")){

                            alredyMarkoutdialog();
                        }
                        else if(attendancestatus.equals("2020")){
                            holiDayDialog();
                        }
                        else {
                            Flag = "MARKOUT";
                            getRediousApicall(strLatitude, strLongitude, ssoid, ssoLoginTokan);
                        }

                    }
                }
            }
        });
        //markout = findViewById(R.id.rlMarkout);
        cvmarkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LocationManager locationManager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                   // checkSettingStartlocationUpdate();
                    new MaterialAlertDialogBuilder(MainActivity.this)
                            .setTitle("GPS disabled")
                            .setMessage("Your Device's location is Mandatory for this App's Usage. Please switch On your device GPS")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                }
                            }).setCancelable(false)
                            .show();
                }
                else {
                    if(strLatitude == null && strLatitude == null ){
                        Toast.makeText(MainActivity.this, "Cant't get Location..", Toast.LENGTH_SHORT).show();
                    }
                    else {
//                        if(strLatitude.equals("") && strLongitude.equals("")){
//                            Snackbar snackbar = Snackbar
//                                    .make(tvdate, "Cant't get Location ", Snackbar.LENGTH_LONG);
//                            snackbar.show();
//                        }
//                        else {
                        if(attendancestatus.equals("2005")){

                            alredyMarkIndialog();
                        }
                        else if(attendancestatus.equals("2006")){
                            alredyMarkIndialog();
                        }
                        else if(attendancestatus.equals("2020")){
                            holiDayDialog();
                        }
                        else {
                            Flag = "MARKIN";
                            getRediousApicall(strLatitude,strLongitude,ssoid,ssoLoginTokan);
                        }

                    //    }
                    }
                }




            }
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000)
                .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                .setMinUpdateIntervalMillis(500)
                .setMinUpdateDistanceMeters(1)
                .setWaitForAccurateLocation(true)
                .build();
    }

    private void getAttendanceStatus(String ssid,String jwttoken){
        WebServiceHandler webServiceHandler =new WebServiceHandler(MainActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) throws JSONException {

                Log.e("GetStatus",response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                           JSONObject json = new JSONObject(response);
                            String valid = json.getString("valid");
                            if(valid.equals("true")) {
                                String collegeName = (String) json.getString("collegeName");
                                String token = (String) json.getString("token");
                                String message = (String) json.getString("message");
                                String name = (String) json.getString("name");
                                attendancestatus = (String) json.getString("status");
                                String response = (String) json.getString("response");
                                String markInTime = (String) json.getString("markInTime");
                                String markOutTime = (String) json.getString("markOutTime");
                                String appversion = json.getString("appVersion");

                                String courseName = (String) json.getString("courseName");
                                String yearName = json.getString("yearName");


                                version.setText(appversion);

                                //String status = "2007";
                                strname = name;
                                strdeptname = collegeName;

                                // Log.e("name",strname);

                                tvname.setText(name);
                                deptname.setText(collegeName);
                                tv_year.setText(yearName);
                                tv_coursename.setText(courseName);

                                if(markInTime.equals("null")){

                                }
                                else {
                                    markintime.setText(markInTime);
                                }

                                if(markOutTime.equals("null")){

                                }
                                else {
                                    markouttime.setText(markOutTime);
                                }


                                if(attendancestatus.equals("2005")){
                                    cvmarkin.startAnimation(animation);
                                    cvmarkin.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.markin));
                                    cvmarkout.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.white));
                                   // ivmarkin.setVisibility(View.VISIBLE);
                                    textmatkin.setHintTextColor(Color.WHITE);
                                    iconmarkin.setColorFilter(Color.WHITE);


                                }
                                else if(attendancestatus.equals("2006")){
                                    cvmarkin.startAnimation(animation);
                                    cvmarkout.startAnimation(animation1);
                                    cvmarkin.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.markin));
                                    cvmarkout.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.markout));
                                   // ivmarkin.setVisibility(View.VISIBLE);
                                    //ivmarkout.setVisibility(View.VISIBLE);
                                    textmatkin.setHintTextColor(Color.WHITE);
                                    iconmarkin.setColorFilter(Color.WHITE);
                                    textmarkout.setHintTextColor(Color.WHITE);
                                    iconmarkout.setColorFilter(Color.WHITE);

                                }
                                else if(attendancestatus.equals("2007")){
                                    cvmarkin.setCardBackgroundColor(Color.WHITE);
                                    cvmarkout.setCardBackgroundColor(Color.WHITE);
                                   // ivmarkin.setVisibility(View.INVISIBLE);
                                   // ivmarkout.setVisibility(View.INVISIBLE);

                                }
                                else {

                                }

                            }
                            else {
                                SharedPreferences preferences =getSharedPreferences("RajAttendanceLogInTokan",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();
                                finish();
                                //Toast.makeText(getApplicationContext(),"Tokan Not metch",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailer(String failure) throws JSONException {
                Log.e("Rediousfailure",failure);
                Toast.makeText(MainActivity.this, failure, Toast.LENGTH_LONG).show();
            }
        };
        webServiceHandler.attendanceStatus2(ssid,jwttoken);
    }

    private void getRediousApicall(String lat,String longi,String ssoname,String jwttoken){
        WebServiceHandler webServiceHandler =new WebServiceHandler(MainActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) throws JSONException {

                Log.e("RediousResponceDirect",response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(response);
                            String valid = json.getString("valid");
                            if(valid.equals("true")) {
                                String success = (String) json.getString("success");
                                String status = (String) json.getString("status");
                                String response = (String) json.getString("response");
                                //String token = (String) json.getString("token");

                                if(status.equals("2008")){
                                    // faceRdCall();
                                    aadhaarConsernDialog();

                                }
                                else {
                                    //faceRdCall();
                                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                SharedPreferences preferences =getSharedPreferences("RajAttendanceLogInTokan",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();
                                finish();
                                //Toast.makeText(getApplicationContext(),"Tokan Not metch",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailer(String failure) throws JSONException {
                Log.e("Rediousfailure",failure);
                Toast.makeText(MainActivity.this, failure, Toast.LENGTH_LONG).show();
            }
        };
        webServiceHandler.calculateRedious(lat,longi,ssoname,jwttoken);
    }


    private void sendFaceIDPIDTransaction(String LAT,String LONGI,String SSOID,String PIDBLOCK,String jwttoken){
        WebServiceHandler webServiceHandler =new WebServiceHandler(MainActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) throws JSONException {

                Log.e("FACRAUTH_RESP",response);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(response);
                            String valid = json.getString("valid");
                            if(valid.equals("true")) {
                                String token= (String) json.getString("token");
                                String status = (String) json.getString("status");
                                String response = (String) json.getString("response");
                                String markInTime = json.getString("markInTime");

                                if(markInTime.equals("null")){

                                }
                                else {
                                    markintime.setText(markInTime);
                                }


                                if(status.equals("2000")){
                                    cvmarkin.startAnimation(animation);
                                    cvmarkin.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.markin));
                                   // ivmarkin.setVisibility(View.VISIBLE);
                                    textmatkin.setHintTextColor(Color.WHITE);
                                    iconmarkin.setColorFilter(Color.WHITE);

                                    Snackbar snackbar = Snackbar
                                            .make(textmatkin, "Attendance Successfuly recorded", Snackbar.LENGTH_LONG);
                                    snackbar.show();


                                }
                                else if(status.equals("2002")){
                                   // cvmarkin.startAnimation(animation);
                                   // cvmarkin.setCardBackgroundColor(Color.GREEN);
                                   // ivmarkin.setVisibility(View.VISIBLE);
                                    //  Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                                    alredyMarkIndialog();

                                }
                                else if(status.equals("2003")){
                                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();

                                }
                                else if(status.equals("2004")){
                                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();

                                }
                                else {
                                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                SharedPreferences preferences =getSharedPreferences("RajAttendanceLogInTokan",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();
                                finish();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailer(String failure) throws JSONException {
                Log.e("Rediousfailure",failure);
                Toast.makeText(MainActivity.this, failure, Toast.LENGTH_LONG).show();
            }
        };
        webServiceHandler.facerdApiResponce(LAT,LONGI,SSOID,PIDBLOCK,jwttoken);
    }

    private void sendFaceIDPIDTransactionMarkout(String LAT,String LONGI,String SSOID,String PIDBLOCK,String jwttokan){
        WebServiceHandler webServiceHandler =new WebServiceHandler(MainActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) throws JSONException {

                Log.e("MARKOUT",response);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(response);
                            String valid = json.getString("valid");
                            if(valid.equals("true")) {
                                String token= (String) json.getString("token");
                                String status = (String) json.getString("status");
                                String response = (String) json.getString("response");
                                String markOutTime = json.getString("markOutTime");

                                if(markOutTime.equals("null")){

                                }
                                else {
                                    markouttime.setText(markOutTime);
                                }

                                if(status.equals("2001")){
                                    cvmarkout.startAnimation(animation1);
                                    cvmarkout.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.markout));
                                    //ivmarkout.setVisibility(View.VISIBLE);
                                    textmarkout.setHintTextColor(Color.WHITE);
                                    iconmarkout.setColorFilter(Color.WHITE);

                                    Snackbar snackbar = Snackbar
                                            .make(textmarkout, "Markout Successfuly recorded", Snackbar.LENGTH_LONG);
                                    snackbar.show();

                                }
                                else if(status.equals("2002")){
                                   // Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();

                                    alredyMarkoutdialog();
                                }
                                else if(status.equals("2003")){
                                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();

                                }
                                else if(status.equals("2004")){
                                   // Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                                    markoutBeforemarkin();

                                }
                                else if(status.equals("2012")){
                                    alredyMarkoutdialog();
                                }
                                else {

                                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                SharedPreferences preferences =getSharedPreferences("RajAttendanceLogInTokan",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();
                                finish();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailer(String failure) throws JSONException {
                Log.e("Rediousfailure",failure);
                Toast.makeText(MainActivity.this, failure, Toast.LENGTH_LONG).show();
            }
        };
        webServiceHandler.facerdApiResponceOUT(LAT,LONGI,SSOID,PIDBLOCK,jwttokan);
    }

    private void checkSettingStartlocationUpdate(){
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //setting of device setisfy start location update
                        startLocationupdate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof ResolvableApiException){
                            ResolvableApiException apiException = (ResolvableApiException) e;
                            try {
                                apiException.startResolutionForResult(MainActivity.this,1001);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // LocationSettingsStatusCodes.

                  //  Toast.makeText(getApplicationContext(),"GPS is on",Toast.LENGTH_LONG).show();
                } catch (ApiException e) {
                    switch (e.getStatusCode()){
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED :

                            //Open GPS Enable Pop comment on 29/09/2023
//                            try {
//                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
//                                resolvableApiException.startResolutionForResult(MainActivity.this,Location_Req_Code);
//                            } catch (IntentSender.SendIntentException ex) {
//                                ex.printStackTrace();
//                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }

                }
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void startLocationupdate(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
    }
    private void stopLocationUpdate(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }



    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdate();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //genrateipaddress();
        //genratemacaddress();
        genratecurrentdate();
        getDisplayDateTime();
        genrateDeviceuniqueId();

//        LocationManager locationManager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
//
//
//
//        }
//        else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
                    .PERMISSION_GRANTED){
                // getLastLocation();
                checkSettingStartlocationUpdate();
            }
            else {
                askLocationPermission();
            }
     //   }

    }

    public void getLastLocation(){
        @SuppressLint("MissingPermission") Task<Location> locationtask = fusedLocationProviderClient.getLastLocation();
        locationtask.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            Log.e("location",location.toString());
                        }
                        else {
                            Log.e("location","null location");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    public void askLocationPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager
                .PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission
                    .ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Location_Req_Code);
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Location_Req_Code);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Location_Req_Code){
            if(grantResults.length > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                //permission grant
                //getLastLocation();
                checkSettingStartlocationUpdate();
            }
            else {
                //permission not grant
            }
        }
    }

    public  String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.valueOf(number);
    }
    public void faceRdCall () {
        String randomnumber = getRandomNumberString();
        try {
            String pidOption = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<PidOptions ver=\"1.0\" env=\"P\">\n" +
                    "   <Opts format=\"\" pidVer=\"2.0\" otp=\"\" wadh=\"\" />\n" +
                    "   <CustOpts>\n" +
                    "      <Param name=\"txnId\" value=\""+randomnumber+"\"/>\n" +
                    "      <Param name=\"purpose\" value=\"auth\"/>\n" +
                    "      <Param name=\"language\" value=\"en\"/>\n" +
                    "   </CustOpts>\n" +
                    "</PidOptions>";
            Intent intent2 = new Intent();
            intent2.setAction("in.gov.uidai.rdservice.face.CAPTURE");
            intent2.putExtra("request", pidOption);
            startActivityForResult(intent2, 123);
        } catch (Exception e) {
            //    Log.e("Error", e.toString());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == Activity.RESULT_OK){
            try{
                if(data!=null){
                    String result = data.getStringExtra("response");
                    //display pid data
                    //tv_viewpid.setText(result);
                    // Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();
                    setOutputText(result);
                    Log.e("PIDDETA",result);
                }
            }
            catch (Exception e){
                Log.e("Error", "Error while deserialize pid data", e);
            }
        }
        else if(requestCode == Location_Req_Code){
            switch (resultCode){
                case
                        Activity.RESULT_OK:
                                          startLocationupdate();
                   // Toast.makeText(MainActivity.this, "Now GPS trned on ##", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setOutputText(final String pidXML) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(Flag.equals("MARKIN")){
                    Log.e("MARKIN","MARKIN");
                    //markin Attendance api call
                    sendFaceIDPIDTransaction(strLatitude,strLongitude,ssoid,pidXML,ssoLoginTokan);
                }
                else if(Flag.equals("MARKOUT")){
                    Log.e("MARKOUT","MARKOUT");
                    //markout attendance api call
                    sendFaceIDPIDTransactionMarkout(strLatitude,strLongitude,ssoid,pidXML,ssoLoginTokan);
                }

//                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
//                LOGinLOGouttime = df.format(Calendar.getInstance().getTime());
//                DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
//                try {
//                    DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
//                    InputSource localInputSource = new InputSource();
//                    localInputSource.setCharacterStream(new StringReader(message));
//                    Document localDocument = localDocumentBuilder.parse(localInputSource);
//                    DetaPID = localDocument.getElementsByTagName("Data").item(0).getTextContent();
//                    Element localElement2 = (Element) localDocument.getElementsByTagName("DeviceInfo").item(0);
//                    DC = localElement2.getAttribute("dc");
//                    DPID = localElement2.getAttribute("dpId");
//                    MC = localElement2.getAttribute("mc");
//                    MI = localElement2.getAttribute("mi");
//                    RDSID = localElement2.getAttribute("rdsId");
//                    RSDVER = localElement2.getAttribute("rdsVer");
//                    dcmistr = MI+DC;
//                    filterDCMCstr = dcmistr.replaceAll("[^a-zA-Z0-9]+", "");
//                    FilterUDC = filterDCMCstr.substring(0, 15);
//                    Element localelement11 = (Element) localDocument.getElementsByTagName("Param").item(0);
//                    String additionalInfoname = localelement11.getAttribute("name");
//                    additionalinfosrno = localelement11.getAttribute("value");
//                    HMAC = localDocument.getElementsByTagName("Hmac").item(0).getTextContent();
//                    Element localElement3 = (Element) localDocument.getElementsByTagName("Resp").item(0);
//                    ERRCODE = localElement3.getAttribute("errCode");
//                    ERRINFO = localElement3.getAttribute("errInfo");
//                    FCOUNT = localElement3.getAttribute("fCount");
//                    FTYPE = localElement3.getAttribute("fType");
//                    ICOUNT = localElement3.getAttribute("iCount");
//                    ITYPE = localElement3.getAttribute("iType");
//                    NMPOINT = localElement3.getAttribute("nmPoints");
//                    PCOUNT = localElement3.getAttribute("pCount");
//                    PTYPE = localElement3.getAttribute("pType");
//                    QSCORE = localElement3.getAttribute("qScore");
//                    Element localElement4 = (Element) localDocument.getElementsByTagName("Skey").item(0);
//                    CI = localElement4.getAttribute("ci");
//                    SKEYINSIDE = localDocument.getElementsByTagName("Skey").item(0).getTextContent();
//                    String authXML = String.format("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?><authrequest uid=\"" + "711592060075" + "\" subaua=\"0000440000\" tid=\"registered\" ip=\"" + ipAddress + "\" fdc=\"NC\" mec=\"Y\" rc=\"Y\" ts=\"" + strDate + "\" idc=\"NA\" bt=\"FID\" macadd=\"" + macAddress + "\" lot=\"P\" lov=\"302005\" rdsID=\""+RDSID+"\" rdsVer=\""+RSDVER+"\" dpID=\""+DPID+"\" dc=\""+DC+"\" mi=\""+MI+"\" mc=\""+MC+"\" ver=\"2.5\" lk=\"MKcrm4YjHSvsq8ZHdPCOeiBXRNnmMeRJDz4xfru-sr-Pp9W4Es8_wy4\"><deviceInfo fType=\""+FTYPE+"\" iCount=\""+ICOUNT+"\" pCount=\""+PCOUNT+"\" errCode=\""+ERRCODE+"\" errInfo=\""+ERRINFO+"\" fCount=\""+FCOUNT+"\" nmPoints=\""+NMPOINT+"\" qScore=\""+QSCORE+"\" srno=\""+SRNO+"\" error=\""+ERRINFO+"\"/><Skey ci=\"" + CI + "\">" + "%s" + "</Skey><Hmac>" + "%s" + "</Hmac><Data type=\"X\">" + "%s" + "</Data></authrequest>", SKEYINSIDE,HMAC,DetaPID);
//                     faceAuthCall(authXML);
//
//
//                } catch (ParserConfigurationException e) {
//                    e.printStackTrace();
//                } catch (SAXException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


            }
        });
    }
    public void faceAuthCall(String authxml){
    WebServiceHandler webServiceHandler = new WebServiceHandler(MainActivity.this);
    webServiceHandler.serviceListener =new WebServiceListener() {
        @Override
        public void onResponse(String response) throws JSONException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //  Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    //  tv_viewresponce.setText(response);
                  //  Log.e("RESPFACE",response);
                    Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onFailer(String failure) throws JSONException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),failure,Toast.LENGTH_LONG).show();
                    //   tv_viewresponce.setText(failure);
                }
            });
        }
    };
    webServiceHandler.makeRequestfaceAuth(authxml);
}

    //Genrate Ip Address of Device....
    private void genrateipaddress() {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        // Log.e("IpAddress", ipAddress);
    }
    ///Genrate Mac Address of Device
    private void genratemacaddress() {
        WifiManager wimanager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        macAddress = wimanager.getConnectionInfo().getMacAddress();

        Log.e("macAddress", macAddress);

        if (macAddress == null) {
            macAddress = "Device don't have mac address or wi-fi is disabled";
        }
    }

    private void genrateDeviceuniqueId(){
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.e("android_id", android_id);
    }
    //genrate current dateand time
    private void genratecurrentdate() {
        calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZZ", Locale.US);
        strDate = sdf.format(calendar.getTime());
        //  Log.e("CurrentCurrentDate", strDate);
    }

    private void getDisplayDateTime(){
         calendar = Calendar.getInstance();
         dateFormat = new SimpleDateFormat("EEEE LLLL dd,yyyy");
         String date = dateFormat.format(calendar.getTime());
         tvdate = findViewById(R.id.tv_date);
         tvdate.setText(date);
         //Log.e("date",date);

    }
    //toolbar code

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.home_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId =  item.getItemId();
        if(itemId == R.id.logout){
            alertDialog();

        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        finish();
    }
    private void alertDialog() {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.applogo)
                    .setTitle("Raj-Upasthiti")
                    .setMessage("Are you sure you want to Logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences preferences =getSharedPreferences("RajAttendanceLogInTokan",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.clear();
                            editor.apply();
                            Intent intent = new Intent(MainActivity.this, SsologinScreen.class);
                            intent.putExtra("finish", true); // if you are checking for this in your other Activities
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

    }

    private void holiDayDialog(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.applogo)
                .setTitle("MarkIN Alert")
                .setMessage("Holiday today")
                .setPositiveButton("ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }

                })
                // .setNegativeButton("No", null)
                .show();
    }

    private void alredyMarkIndialog(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.applogo)
                .setTitle("MarkIN Alert")
                .setMessage("Already Mark-in done today")
                .setPositiveButton("ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                    }

                })
               // .setNegativeButton("No", null)
                .show();
    }

    private void alredyMarkoutdialog(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.applogo)
                .setTitle("MarkOUT Alert")
                .setMessage("Already Mark-OUT done today")
                .setPositiveButton("ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }

                })
                // .setNegativeButton("No", null)
                .show();
    }

    private void markoutBeforemarkin(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.applogo)
                .setTitle("MarkOUT Alert")
                .setMessage("First markin attenance before markout")
                .setPositiveButton("ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }

                })
                // .setNegativeButton("No", null)
                .show();
    }

    private void aadhaarConsernDialog(){
        View checkBoxView = View.inflate(MainActivity.this, R.layout.conserndialog, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Save to shared preferences
            }
        });
        checkBox.setText("I hereby state that I have no objection to authenticating myself with the AADHAAR-based authentication system and consent to provide my AADHAAR Number and my Face biometrics for the AADHAAR-based Authentication for the aadhaar based biometric attendance system application. \n" +
                "मैं इसके द्वारा कहता हूं कि मुझे आधार-आधारित प्रमाणीकरण प्रणाली के साथ खुद को प्रमाणित करने में कोई आपत्ति नहीं है और मैं आधार सक्षम बायोमेट्रिक उपस्थिति प्रणाली एप्लीकेशन में उपस्थिति दर्ज के लिए आधार-आधारित प्रमाणीकरण के लिए अपना आधार नंबर और अपना चेहरा बायोमेट्रिक्स प्रदान करने के लिए सहमति देता हूं।");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(" Aadhaar Consent");
        builder.setIcon(R.drawable.aadhaaricon);
        builder.setView(checkBoxView)
                .setCancelable(false)
                .setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(checkBox.isChecked()){
                            dialog.cancel();
                            faceRdCall();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Please Check Aadhaar consern",Toast.LENGTH_LONG).show();
                        }
                    }
                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                })
                .show();
    }


}

