package com.genesis.cloudcarepatient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewUserDetailsActivity extends AppCompatActivity {

    PatientBean pb;

    private static final String TAG = LoginActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;

    TextView name, email;
    CustomEditText phn, address, bloodGroup, gender, age, adhaar;
    Button submit;
    private CircleImageView personImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_details);
        pb = (PatientBean) getIntent().getSerializableExtra("patientbean");

        name = (TextView) findViewById(R.id.patient_name);
        email = (TextView) findViewById(R.id.patient_email);

        personImage = (CircleImageView) findViewById(R.id.personimage);

        name.setText(pb.getName());
        email.setText(pb.getEmail());
        Glide.with(getApplicationContext()).load(pb.getPhotoURL())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(personImage);

//        name.setText("Asif Ali");
//        email.setText("aachi.mec@gmail.com");

        phn = (CustomEditText) findViewById(R.id.patient_phone);
        address = (CustomEditText) findViewById(R.id.patient_address);
        bloodGroup = (CustomEditText) findViewById(R.id.patient_blood);
        gender = (CustomEditText) findViewById(R.id.patient_gender);
        age = (CustomEditText) findViewById(R.id.patient_age);
        adhaar = (CustomEditText) findViewById(R.id.patient_aadhar);

        submit = (Button) findViewById(R.id.patient_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setPhn(phn.getText().toString());
                pb.setAddress(address.getText().toString());
                pb.setBloodGroup(bloodGroup.getText().toString());
                pb.setGender(gender.getText().toString());
                pb.setAge(age.getText().toString());
                pb.setAdhaarUID(adhaar.getText().toString());



                JSONObject patient = new JSONObject();

                try {

                    String example_url = "http://20.20.4.84:3000/ipfs";


                    patient.put("name",pb.getName());
                    patient.put("address",pb.getAddress());
                    patient.put("age",pb.getAge());
                    patient.put("bloodGroup",pb.getBloodGroup());
                    patient.put("email",pb.getEmail());
                    patient.put("gender",pb.getGender());
                    patient.put("phn",pb.getPhn());
                    patient.put("photoURL",pb.getPhotoURL());
                    patient.put("googleId",pb.getGoogleID());
                    patient.put("adhaarid",pb.getAdhaarUID());





                    Log.e(TAG,patient.toString());


                    requestApi(patient, example_url, "POST",3);
                } catch (JSONException e) {
                    Log.e(TAG,"Json error 100 "+ e.toString());
                    e.printStackTrace();
                }

                SharedPreferences preferences = getApplicationContext().getSharedPreferences("patientbean", 0);
                SharedPreferences.Editor editor = preferences.edit();
                Gson gson = new Gson();
                String pbString = gson.toJson(pb);
                editor.putString("patientbean",pbString);
                editor.apply();

                String jsonret = preferences.getString("patientbean","");
                PatientBean pbret = gson.fromJson(jsonret,PatientBean.class);

                Intent i = new Intent(NewUserDetailsActivity.this,PatientActivity.class);
                i.putExtra("patientbean",pb);
                startActivity(i);
            }
        });



    }

    private void showProgressDialog(String str) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(str);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void requestApi(JSONObject data, String reqUrl, String method, final int reqtyoe) {

        int reqMethod = method.equalsIgnoreCase("POST") ? Request.Method.POST : Request.Method.GET;

//        switch (reqtyoe){
//            case 1: showProgressDialog("Sending");
//                break;
//            case 2: showProgressDialog("Verifying");
//                break;
//            case 3: showProgressDialog("Registering");
//                break;
//        }
        showProgressDialog("Sending");

            JsonObjectRequest request = new JsonObjectRequest(reqMethod, reqUrl, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG,"Volley Response" + response.toString());

//                    switch (reqtyoe){
//                        case 1: parseMobileSend(response);
//                            break;
//                        case 2: parseVerify(response);
//                            break;
//                        case 3: parseRegister(response);
//                            break;
//                    }
                    parseRegister(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e(TAG,"Volley Error my " + error.toString());

                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(request);




    }

    public void parseVerify(JSONObject patient){
        Log.e(TAG,patient.toString());


        hideProgressDialog();

    }

    public void parseMobileSend(JSONObject patient){
        Log.e(TAG,"parse mobile send response " + patient.toString());


        hideProgressDialog();

    }

    public void parseRegister(JSONObject patient){
        Log.e(TAG,"parse register response " + patient.toString());
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("patientbean", 0);

        Gson gson = new Gson();


        String jsonret = preferences.getString("patientbean","");
        PatientBean pbret = gson.fromJson(jsonret,PatientBean.class);

        try {
            pbret.setHash(patient.getString("hash"));
            pbret.setNextHash(patient.getString("nexthash"));

            Log.e("Haaaaaash ",pbret.getNextHash() +"  " +pbret.getHash());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = preferences.edit();
        String pbString = gson.toJson(pbret);
        editor.putString("patientbean",pbString);
        editor.apply();



        hideProgressDialog();

    }

}
