package com.gov.doitc.rajattendance.utils;
import static com.gov.doitc.rajattendance.utils.WebUrls.Attendance_Status;
import static com.gov.doitc.rajattendance.utils.WebUrls.FACERD_TRANSACTION;
import static com.gov.doitc.rajattendance.utils.WebUrls.FACERD_TRANSACTIONOUT;
import static com.gov.doitc.rajattendance.utils.WebUrls.Login_Status;
import static com.gov.doitc.rajattendance.utils.WebUrls.calculaterediousapi;
import static com.gov.doitc.rajattendance.utils.WebUrls.getDept;
import static com.gov.doitc.rajattendance.utils.WebUrls.ssologin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by user on 6/7/2017.
 */

public class WebServiceHandler {

    private OkHttpClient okHttpClient;
    private OkHttpClient client = new OkHttpClient();
    private RequestBody requestBody;
    private Request request;
    private Context context;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private ProgressDialog pDialog;
    public WebServiceListener serviceListener;
    public static final MediaType JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");



    public WebServiceHandler(Context context) {
        this.context = context;


        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @SuppressLint("BadHostnameVerifier")
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("please wait..");
            // pDialog = new ProgressDialog (context,SweetAlertDialog.PROGRESS_TYPE);
            pDialog.setCancelable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void  Ssologinfunction(String email,String password){
        //   Log.e("janaadhaarnumber",janaadhaarnumber);
        RequestBody formBody = new FormBody.Builder()
                .addEncoded("SSOID", email)
                .addEncoded("Password", password)
                .build();

        pDialog.show();
        request = new Request.Builder()
                .url(ssologin)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                try {
                    serviceListener.onResponse(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void apicall(){
        pDialog.show();
        request = new Request.Builder()
                .url("https://rajattendance.rajasthan.gov.in/UIDAttendance/master/attendance/json/department")
                .method("POST",RequestBody.create(null, new byte[0]))
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("dept", String.valueOf(e));
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                try {
                    serviceListener.onResponse(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void ssoLogin11(String email,String password){

        RequestBody formBody = new FormBody.Builder()
                .add("UserName", email)
                .add("Password", password)
                .build();
        pDialog.show();
        request = new Request.Builder()
                .url(ssologin)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                try {
                    serviceListener.onResponse(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getLoginStatus(String ssoname, String jwttoken){
        RequestBody formBody = new FormBody.Builder()
                .add("ssoId", ssoname)
                .build();
        // pDialog.show();
        request = new Request.Builder()
                .url(Login_Status)
                .addHeader("Authorization","Bearer " + jwttoken)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    serviceListener.onResponse(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void attendanceStatus(String ssoname,String jwttoken){
        Log.e("SSONAme",ssoname);
        RequestBody formBody = new FormBody.Builder()
                .add("ssoId", ssoname)
                .build();
       // pDialog.show();
        request = new Request.Builder()
                .url(Attendance_Status)
                .addHeader("Authorization","Bearer " + jwttoken)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    serviceListener.onResponse(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void attendanceStatus2(String ssoname,String jwttokan){
        Log.e("SSONAme",ssoname);
        RequestBody formBody = new FormBody.Builder()
                .add("ssoId", ssoname)
                .build();
         pDialog.show();
        request = new Request.Builder()
                .url(Attendance_Status)
                .addHeader("Authorization","Bearer " + jwttokan)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                try {
                    serviceListener.onResponse(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void calculateRedious(String lat,String longi,String ssoname,String jwttoken){
        Log.e("SSONAme",jwttoken+" "+ssoname);
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(calculaterediousapi).newBuilder();
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("ssoId", ssoname);
//        params.put("latitude",lat);
//        params.put("longitude",longi);
//
//        JSONObject jsonObject = new JSONObject(params);
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
//        String url = urlBuilder.build().toString();


        RequestBody formBody = new FormBody.Builder()
                .add("ssoId", ssoname)
                .add("latitude",lat)
                .add("longitude",longi)
                .build();
        pDialog.show();
        request = new Request.Builder()
                .url(calculaterediousapi)
                .addHeader("Authorization","Bearer " + jwttoken)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                try {
                    serviceListener.onResponse(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void facerdApiResponce(String LAT,String LONGI,String SSOID,String PIDBLOCK,String jwttoken){
        RequestBody formBody = new FormBody.Builder()
                .add("lettitude", LAT)
                .add("longitude", LONGI)
                .add("ssoId", SSOID)
                .add("message", PIDBLOCK)
                .build();
        pDialog.show();
        request = new Request.Builder()
                .url(FACERD_TRANSACTION)
                .addHeader("Authorization","Bearer " + jwttoken)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                try {
                    serviceListener.onResponse(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void facerdApiResponceOUT(String LAT,String LONGI,String SSOID,String PIDBLOCK,String jwttoken){
        RequestBody formBody = new FormBody.Builder()
                .add("lettitude", LAT)
                .add("longitude", LONGI)
                .add("ssoId", SSOID)
                .add("message", PIDBLOCK)
                .build();
        pDialog.show();
        request = new Request.Builder()
                .url(FACERD_TRANSACTIONOUT)
                .addHeader("Authorization","Bearer " + jwttoken)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                try {
                    serviceListener.onResponse(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void  Ssologinfunctiondirect(String email,String password){

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        RequestBody formBody = new FormBody.Builder()
                .add("Application", "RU")
                .add("UserName", email)
                .add("Password", password)
                .build();


        pDialog.show();
        request = new Request.Builder()
                .url(ssologin)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                try {
                    serviceListener.onResponse(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void makeRequestfaceAuth(String authxml){
        pDialog.show();
        // HttpUrl.Builder urlBuilder = HttpUrl.parse(PreprodBioAuth).newBuilder();
        RequestBody body = RequestBody .create( MediaType .parse("application/xml"), authxml );
        // String url = urlBuilder.build().toString();
        request = new Request.Builder()
                .url("https://api.sewadwaar.rajasthan.gov.in/app/live/was/bio/auth/prod?client_id=977c74a2-672b-4c2c-b8d3-4bbe1368d195")
                .addHeader("appname", "FID(Mobile)")
                .post(body)
                .build();
        //       Log.e("URL",body1.toString());
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                Log.e("error","urlConnectionError");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();

                    try {
                        // Log.e("sending", "urlConnectionError");
                        serviceListener.onResponse(response.body().string());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }




}




