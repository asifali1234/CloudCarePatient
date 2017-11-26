package com.genesis.cloudcarepatient;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cuboid.cuboidcirclebutton.CuboidButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.gson.Gson;
import com.luseen.spacenavigation.SpaceNavigationView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class PatientActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , ResultCallback<Status>, OnItemClickWatcher {

    private SpaceNavigationView spaceNavigationView;

    private CuboidButton bookAppointments, historyAppointments, scheduledAppointments, doctorSchedule;
    private TextView personName, personEmail, personBloodGroup, personPhone, personGender;
    private CircleImageView personImage;

    private PatientBean pb;




    protected static final String TAG = "MainActivity";
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    // UI elements.
    @Bind(R.id.start_updates_button)
    Button mRequestActivityUpdatesButton;
    @Bind(R.id.stop_updates_button)
    Button mRemoveActivityUpdatesButton;
    @Bind(R.id.detected_activities_title)
    TextView mActivityTitle;
    @Bind(R.id.activity_list)
    RecyclerView mActivityList;
    @Bind(R.id.seekBar)
    SeekBar mTotalActivity;

    protected int[] dataArray = new int[6];
    protected ActivityAdapter activityAdapter;
    protected boolean serviceIsStarted;




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

        if(pb!=null) {
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




        ButterKnife.bind(this);

        mActivityList.addItemDecoration(new DividerItemDecoration(this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mActivityList.setLayoutManager(layoutManager);
        activityAdapter = new ActivityAdapter(dataArray, this, this);
        mActivityList.setAdapter(activityAdapter);

        // Get a receiver for broadcasts from ActivityDetectionIntentService.
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        serviceIsStarted = getUpdatesRequestedState();
        setButtonsEnabledState();
        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();
        // Disable seekbar
        mTotalActivity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

//
//        Button b = (Button) findViewById(R.id.view_health);
//
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(PatientActivity.this,ViewRecordActivity.class);
//                startActivity(i);
//            }
//        });





    }










    private void checkCurrentState() {
        for (int i = 0; i < 6; i++) {
            dataArray[i] = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                    .getInt(Constants.RESULT + Utils.getStringActivity(i), 0);
        }
        setSeekbarProgress();
        if (activityAdapter != null) {
            activityAdapter.notifyDataSetChanged();
        }
    }

    private void setSeekbarProgress() {
        int totalActiv = dataArray[1] + dataArray[2];
        if (totalActiv > 30 ){
            totalActiv = 30;
        }
        mTotalActivity.setProgress(totalActiv);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * ActivityRecognition API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCurrentState();
        setButtonsEnabledState();
        // Register the broadcast receiver that informs this activity of the DetectedActivity
        // object broadcast sent by the intent service.
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the broadcast receiver that was registered during onResume().
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Log.d(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.d(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.d(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Registers for activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code requestActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesService} starts receiving callbacks when
     * activities are detected.
     */
    @OnClick(R.id.start_updates_button)
    public void requestActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        //startService(new Intent(this, DetectedActivitiesService.class));
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    /**
     * Removes activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#removeActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code removeActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesService} stops receiving callbacks about
     * detected activities.
     */
    @OnClick(R.id.stop_updates_button)
    public void removeActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
        stopService(new Intent(this, DetectedActivitiesService.class));
    }

    /**
     * Runs when the result of calling requestActivityUpdates() and removeActivityUpdates() becomes
     * available. Either method can complete successfully or with an error.
     *
     * @param status The Status returned through a PendingIntent when requestActivityUpdates()
     *               or removeActivityUpdates() are called.
     */
    public void onResult(@NonNull Status status) {

        Log.d(TAG, "onResult() called with: " + "status = [" + status + "]");
        if (status.isSuccess()) {
            // Toggle the status of activity updates requested, and save in shared preferences.
            serviceIsStarted = !serviceIsStarted;
            setUpdatesRequestedState(serviceIsStarted);
            setButtonsEnabledState();
        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Ensures that only one button is enabled at any time. The Request Activity Updates button is
     * enabled if the user hasn't yet requested activity updates. The Remove Activity Updates button
     * is enabled if the user has requested activity updates.
     */
    private void setButtonsEnabledState() {

        if (serviceIsStarted) {
            mRequestActivityUpdatesButton.setEnabled(false);
            mRemoveActivityUpdatesButton.setEnabled(true);
        } else {
            mRequestActivityUpdatesButton.setEnabled(true);
            mRemoveActivityUpdatesButton.setEnabled(false);
        }
    }

    /**
     * Sets the boolean in SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private void setUpdatesRequestedState(boolean requestingUpdates) {
        getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .edit()
                .putBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, requestingUpdates)
                .apply();
    }

    /**
     * Retrieves the boolean from SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private boolean getUpdatesRequestedState() {
        return getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .getBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, false);
    }

    @Override
    public void onItemClick(View v, int position) {

    }

    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "activity-detection-response-receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (activityAdapter != null) {
                checkCurrentState();
            }
        }
    }









}
