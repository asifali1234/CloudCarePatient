package com.genesis.cloudcarepatient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    private static final int existingUser = 0;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private String personName = "";
    private String personEmail = "";
    private String personPhotoUrl = "";
    private String googleID = "";

    SharedPreferences signInInfo;
    SharedPreferences.Editor signInEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInInfo = getApplicationContext().getSharedPreferences("MyPref", 0);


        ImageView gsignin = (ImageView) findViewById(R.id.gimage);

        gsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //updateUI(false);
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public void parseLogin(JSONObject patient){
        Log.e(TAG,patient.toString());

        PatientBean pb = new PatientBean();

        try {

            pb.setName(personName);
            pb.setEmail(personEmail);
            pb.setPhotoURL(personPhotoUrl);
            pb.setGoogleID(googleID);

            if(!patient.getBoolean("registered")){
                hideProgressDialog();
                Intent i = new Intent(LoginActivity.this,NewUserDetailsActivity.class);
                i.putExtra("patientbean",pb);
                startActivity(i);
            }
            else{

//                pb.setName(patient.getString("name"));
//                pb.setAddress(patient.getString("address"));
//                pb.setAge(patient.getString("age"));
//                pb.setBloodGroup(patient.getString("bloodGroup"));
//                pb.setEmail(patient.getString("email"));
//                pb.setGender(patient.getString("gender"));
//                pb.setPhn(patient.getString("phn"));
//                pb.setGoogleID(patient.getString("googleid"));
//                pb.setPhotoURL(patient.getString("photoURL"));

                SharedPreferences preferences = getApplicationContext().getSharedPreferences("patientbean", 0);
                Gson gson = new Gson();
                String jsonret = preferences.getString("patientbean","");
                pb = gson.fromJson(jsonret,PatientBean.class);

                Intent i = new Intent(LoginActivity.this,PatientActivity.class);



                hideProgressDialog();

                i.putExtra("patientbean",pb);
                startActivity(i);

            }


        } catch (JSONException e) {
            Log.e(TAG,"JSON Error in Parse" + e.toString());
            e.printStackTrace();
        }

        hideProgressDialog();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.e(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + (acct != null ? acct.getDisplayName() : "NO Account Detected"));

            personName = acct != null ? acct.getDisplayName() : "NO Account Detected";
            personPhotoUrl = acct != null ? acct.getPhotoUrl().toString() : "NO Account Detected";
            personEmail = acct != null ? acct.getEmail() : "NO Account Detected";
            googleID = acct != null ? acct.getId() : null;

            Toast.makeText(this, personName +"  "+personEmail, Toast.LENGTH_LONG).show();

            Log.e(TAG, "Name: " + personName + ", email: " + personEmail
                    + ", Image: " + personPhotoUrl);



            JSONObject auth = new JSONObject();

            JSONObject patient = new JSONObject();

            try {
                auth.put("googleId", googleID);
                auth.put("playerid",AppController.getDeviceID());
//                String example_url = "http://ec2-13-58-90-106.us-east-2.compute.amazonaws.com/checkUserexists";
                String example_url = "http://20.20.4.84:3000/checkUserexists";
                Log.e(TAG,auth.toString());


//                patient.put("name",personName);
//                patient.put("address","address");
//                patient.put("age","address");
//                patient.put("bloodGroup","address");
//                patient.put("email","address");
//                patient.put("gender","address");
//                patient.put("phn","address");
//                patient.put("photoURL","address");


                requestApi(auth, example_url, "POST");

            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {

            // Signed out, show unauthenticated UI.

        }
    }



}