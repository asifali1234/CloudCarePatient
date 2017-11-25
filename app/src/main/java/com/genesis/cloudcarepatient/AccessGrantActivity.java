package com.genesis.cloudcarepatient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class AccessGrantActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;

    TextView doctordetails;
    Button grant, reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_grant);

        doctordetails = (TextView) findViewById(R.id.doctordetails);

        grant = (Button) findViewById(R.id.grant_access);
        reject = (Button) findViewById(R.id.reject_access);

        grant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String example_url = "http://ec2-13-58-90-106.us-east-2.compute.amazonaws.com/checkUserexists";
                String example_url = "http://20.20.4.84:3000/checkUserexists";

                JSONObject auth = new JSONObject();


                try {
                    auth.put("", "");



                    requestApi(auth, example_url, "POST");

                }
                catch (JSONException e){
                    Log.e(TAG,"Json Error " +  e.toString());
                }






            }
        });


        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }






    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void requestApi(JSONObject data, String reqUrl, String method) {

        int reqMethod = method.equalsIgnoreCase("POST") ? Request.Method.POST : Request.Method.GET;

        showProgressDialog();
        JsonObjectRequest request = new JsonObjectRequest(reqMethod, reqUrl, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG,"Volley Response" + response.toString());
                parseLogin(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Volley Error my" + error.toString());

            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

    public void parseLogin(JSONObject patient){
        Log.e(TAG,patient.toString());



        hideProgressDialog();
        Intent i = new Intent(AccessGrantActivity.this,PatientActivity.class);
        startActivity(i);
    }








}
