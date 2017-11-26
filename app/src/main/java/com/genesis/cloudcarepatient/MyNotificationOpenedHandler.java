package com.genesis.cloudcarepatient;

/**
 * Created by asif on 25/11/17.
 */

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    // This fires when a notification is opened by tapping on it.
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String activityToBeOpened;

        //While sending a Push notification from OneSignal dashboard
        // you can send an addtional data named "activityToBeOpened" and retrieve the value of it and do necessary operation
        //If key is "activityToBeOpened" and value is "AccessGrantActivity", then when a user clicks
        //on the notification, AccessGrantActivity will be opened.
        //Else, if we have not set any additional data MainActivity is opened.
        Log.e("OneSignal Notification", data.toString());
        Intent i = new Intent(AppController.getContext(),AccessGrantActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            i.putExtra("email",data.getString("email"));
            i.putExtra("name",data.getString("name"));
            i.putExtra("reqtype",data.getString("reqtype"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppController.getContext().startActivity(i);

        Log.e("OneSignal Notifican sdc", data.toString());

        try{

            Intent i2 = new Intent(AppController.getContext(),AccessGrantActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                i.putExtra("email",data.getString("email"));
                i.putExtra("name",data.getString("name"));
                i.putExtra("reqtype",data.getString("reqtype"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AppController.getContext().startActivity(i2);

        }
        catch (Exception e){
            Log.e("error in intent" , e.toString());
        }


        Log.e("OneSigndsdcsd Notition", data.toString());

//        if (data != null) {
//            activityToBeOpened = data.optString("activityToBeOpened", null);
//            if (activityToBeOpened != null && activityToBeOpened.equals("AccessGrantActivity")) {
//                Log.e("OneSignalExample", "customkey set with value: " + activityToBeOpened);
//                Intent intent = new Intent(AppController.getContext(), AccessGrantActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                AppController.getContext().startActivity(intent);
//            } else if (activityToBeOpened != null && activityToBeOpened.equals("MainActivity")) {
//                Log.e("OneSignalExample", "customkey set with value: " + activityToBeOpened);
//                Intent intent = new Intent(AppController.getContext(), PatientActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                AppController.getContext().startActivity(intent);
//            } else {
//                Log.e("OneSignalExample", "customkey set with value: ANY" + activityToBeOpened);
//                Intent intent = new Intent(AppController.getContext(), PatientActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                AppController.getContext().startActivity(intent);
//            }

//        }

        //If we send notification with action buttons we need to specidy the button id's and retrieve it to
        //do the necessary operation.
//        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
//            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
//            if (result.action.actionID.equals("ActionOne")) {
//                Toast.makeText(AppController.getContext(), "ActionOne Button was pressed", Toast.LENGTH_LONG).show();
//            } else if (result.action.actionID.equals("ActionTwo")) {
//                Toast.makeText(AppController.getContext(), "ActionTwo Button was pressed", Toast.LENGTH_LONG).show();
//            }
//        }
    }
}