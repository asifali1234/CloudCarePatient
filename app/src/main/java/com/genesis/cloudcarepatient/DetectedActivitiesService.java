package com.genesis.cloudcarepatient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayDeque;

/**
 * IntentService for handling incoming intents that are generated as a result of requesting
 * activity updates using
 * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
public class DetectedActivitiesService extends Service {

    protected static final String TAG = "DetectedActivities";
    protected ArrayDeque<Integer> dataArray;
    protected long arrayCapacity;
    protected static boolean isServiceRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreate() called with: ");
        }

        arrayCapacity = (Constants.ALL_INTERVAL_IN_HOURS * Constants.MILLISECONDS_IN_ONE_HOUR)
                / Constants.DETECTION_INTERVAL_IN_MILLISECONDS;
        String array = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .getString(Constants.ARRAY_ACTIVITIES, null);
        if (array != null) {
            Type collectionType = new TypeToken<ArrayDeque<Integer>>() {
            }.getType();
            dataArray = new Gson().fromJson(array, collectionType);
        } else {
            dataArray = new ArrayDeque<>();
        }
        makeNotification();
        isServiceRunning = true;
    }

    private void makeNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, PatientActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                Constants.PENDING_ID, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(this)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_patient)
                .setContentTitle("Patient activity is running.")
                .setContentText("Don't forget turn off service when it doesn't need.")
                .setOngoing(true)
                .setAutoCancel(false);
        Notification n = builder.build();
        nm.notify(Constants.NOTIFY_ID, n);
    }

    private void cancelNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(Constants.NOTIFY_ID);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDestroy() called with: ");
        }
        isServiceRunning = false;
        getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .edit()
                .putString(Constants.ARRAY_ACTIVITIES, new Gson().toJson(dataArray))
                .apply();
        super.onDestroy();
        cancelNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isServiceRunning = true;
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onStartCommand() called with: " + "intent = ["
                    + intent + "], flags = ["
                    + flags + "], startId = ["
                    + startId + "]");
        }
        if (intent != null) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity detectedActivities = result.getMostProbableActivity();
            if (dataArray.size() >= arrayCapacity) {
                dataArray.removeFirst();
            }
            dataArray.add(detectedActivities.getType());

            // Log activity.
            if (BuildConfig.DEBUG) {
                Log.d(TAG, detectedActivities.toString());
            }
            CalculateActivityResult calculate = new CalculateActivityResult();
            calculate.execute();

            // Broadcast the list of detected activities.
            Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
            localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class CalculateActivityResult extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int result[] = new int[6];
            for (Integer activity : dataArray) {
                result[activity] = result[activity] + 1;
            }
            SharedPreferences.Editor spEditor = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME
                    , MODE_PRIVATE).edit();
            for (int i = 0; i < 6; i++) {
                int temp = (int)(((float) result[i] / (float) dataArray.size()) * 100f);
                spEditor.putInt(Constants.RESULT + Utils.getStringActivity(i), temp);
            }
            spEditor.apply();

            return null;
        }
    }

}

