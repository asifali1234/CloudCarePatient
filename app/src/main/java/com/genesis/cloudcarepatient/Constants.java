package com.genesis.cloudcarepatient;

/**
 * Constants used in this app.
 */
public interface Constants {

    String PACKAGE_NAME = "org.belichenko.a.patientactivity";
    String ACTIVITY_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";
    String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES";
    String RESULT = PACKAGE_NAME + ".RESULT_";
    String ARRAY_ACTIVITIES = PACKAGE_NAME + ".ARRAY_ACTIVITIES";
    String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";
    int NOTIFY_ID = 69587412;
    int PENDING_ID = 69587413;
    /**
     * The desired time between activity detections. Larger values result in fewer activity
     * detections while improving battery life. A value of 0 results in activity detections at the
     * fastest possible rate. Getting frequent updates negatively impact battery life and a real
     * app may prefer to request less frequent updates.
     */
    long DETECTION_INTERVAL_IN_MILLISECONDS = 2600;
    long ALL_INTERVAL_IN_HOURS = 6;
    long MILLISECONDS_IN_ONE_HOUR = 3600600;

    String ACTIVITY_UPDATES_REQUESTED_KEY = PACKAGE_NAME + ".UPDATES_REQUESTED_KEY";

}

