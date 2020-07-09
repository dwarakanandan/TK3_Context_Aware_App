package tk3.group42.yetanothercontextawareapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.DetectedActivity;

import static tk3.group42.yetanothercontextawareapp.MainActivity.CHANNEL_ID;

public class ContextAwareActionProcessor {

    private static final String TAG = "ContextAwareActionProce";
    final int DARMSTADT_HBF = 1;
    final int HERRENGARTEN = 2;
    final int ULB_DARMSTADT = 3;
    final float RADIUS_IN_METERS = 100.0f;

    private Context context;

    public ContextAwareActionProcessor(Context context) {
        this.context = context;
    }

    public void processActionForLocation(double latitude, double longitude) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        
        int currentDetectedActivity = sharedPref.getInt(FitnessUtility.CURRENT_DETECTED_ACTIVITY, DetectedActivity.UNKNOWN);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_run);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d(TAG, "processActionForLocation: Current Detected Activity = " + FitnessUtility.getActivityDisplayName(currentDetectedActivity));
        
        switch (getLocationCode(latitude, longitude)) {
            case DARMSTADT_HBF:
                if (currentDetectedActivity == DetectedActivity.WALKING) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flixtrain.com/train/darmstadt"));
                    PendingIntent pIntent = PendingIntent.getActivity(context, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = notificationBuilder
                            .setContentTitle("Detected walking near Darmstadt hauptbahnhof !")
                            .setContentText("Search for train schedules ?")
                            .addAction(R.drawable.ic_run, "Yes", pIntent)
                            .build();
                    notificationManager.notify(10, notification);
                }
                break;
            case HERRENGARTEN:
                if (currentDetectedActivity == DetectedActivity.RUNNING || currentDetectedActivity == DetectedActivity.ON_BICYCLE) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_APP_MUSIC);
                    PendingIntent pIntent = PendingIntent.getActivity(context, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = notificationBuilder
                            .setContentTitle("Detected exercise near Herrengarten !")
                            .setContentText("Listen to some music while exercising ?")
                            .addAction(R.drawable.ic_run, "Yes", pIntent)
                            .build();
                    notificationManager.notify(11, notification);
                }
                break;
            case ULB_DARMSTADT:
                if (currentDetectedActivity == DetectedActivity.STILL) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_SOUND_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pIntent = PendingIntent.getActivity(context, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = notificationBuilder
                            .setContentTitle("Detected stationary near ULB Darmstadt !")
                            .setContentText("Launch sound setting to modify ring/media volume ?")
                            .addAction(R.drawable.ic_run, "Yes", pIntent)
                            .build();
                    notificationManager.notify(12, notification);
                }
                break;
            default:
        }
    }

    int getLocationCode(double latitude, double longitude) {
        float[] results = new float[1];

        Location.distanceBetween(49.872312, 8.629538,
                latitude, longitude, results);
        Log.d(TAG, "getLocationCode: distance from DARMSTADT_HBF = " + results[0]);
        if (results[0] <= RADIUS_IN_METERS) {
            return DARMSTADT_HBF;
        }

        Location.distanceBetween(49.878096, 8.652099,
                latitude, longitude, results);
        Log.d(TAG, "getLocationCode: distance from HERRENGARTEN = " + results[0]);
        if (results[0] <= RADIUS_IN_METERS) {
            return HERRENGARTEN;
        }

        Location.distanceBetween(49.876423, 8.657697,
                latitude, longitude, results);
        Log.d(TAG, "getLocationCode: distance from ULB_DARMSTADT = " + results[0]);
        if (results[0] <= RADIUS_IN_METERS) {
            return ULB_DARMSTADT;
        }

        Log.d(TAG, "getLocationCode: Location provided not in vicinity of any tracked targets!");
        return -1;
    }
}
