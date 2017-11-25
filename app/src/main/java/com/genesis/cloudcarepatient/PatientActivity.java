package com.genesis.cloudcarepatient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cuboid.cuboidcirclebutton.CuboidButton;
import com.google.gson.Gson;
import com.luseen.spacenavigation.SpaceNavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientActivity extends AppCompatActivity {

    private SpaceNavigationView spaceNavigationView;

    private CuboidButton bookAppointments, historyAppointments, scheduledAppointments, doctorSchedule;
    private TextView personName, personEmail, personBloodGroup, personPhone, personGender;
    private CircleImageView personImage;

    private PatientBean pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);



        personBloodGroup = (TextView) findViewById(R.id.personbloodgroup);
        personEmail = (TextView) findViewById(R.id.personemail);
        personGender = (TextView) findViewById(R.id.persongender);
        personImage = (CircleImageView) findViewById(R.id.personimage);
        personName = (TextView) findViewById(R.id.personname);
        personPhone = (TextView) findViewById(R.id.personphone);

//        pb = (PatientBean) getIntent().getSerializableExtra("patientbean");

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("patientbean", 0);

        Gson gson = new Gson();


        String jsonret = preferences.getString("patientbean","");
        pb = gson.fromJson(jsonret,PatientBean.class);


        personBloodGroup.setText(pb.getBloodGroup());
        personGender.setText(pb.getGender());
        personEmail.setText(pb.getEmail());
        personPhone.setText(pb.getPhn());
        personName.setText(pb.getName());

        Glide.with(getApplicationContext()).load(pb.getPhotoURL())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(personImage);











    }
}
