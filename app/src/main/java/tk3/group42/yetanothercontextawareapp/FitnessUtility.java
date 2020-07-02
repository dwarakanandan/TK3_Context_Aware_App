package tk3.group42.yetanothercontextawareapp;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.location.DetectedActivity;

public class FitnessUtility {
    public static final String STILL = "STILL";
    public static final String WALKING = "WALKING";
    public static final String RUNNING = "RUNNING";
    public static final String BICYCLE = "BICYCLE";
    public static final String VEHICLE = "VEHICLE";
    public static final String UNKNOWN = "UNKNOWN";
    public static String CURRENT_LATITUDE = "CURRENT_LATITUDE";
    public static String CURRENT_LONGITUDE = "CURRENT_LONGITUDE";

    public static String getActivityDisplayName(int activityId) {
        switch (activityId) {
            case DetectedActivity.STILL:
                return STILL;
            case DetectedActivity.WALKING:
                return WALKING;
            case DetectedActivity.RUNNING:
                return RUNNING;
            case DetectedActivity.ON_BICYCLE:
                return BICYCLE;
            case DetectedActivity.IN_VEHICLE:
                return VEHICLE;
            default:
                return UNKNOWN;
        }
    }

    public static int parseActivityDisplayName(String displayName) {
        switch (displayName) {
            case STILL:
                return DetectedActivity.STILL;
            case WALKING:
                return DetectedActivity.WALKING;
            case RUNNING:
                return DetectedActivity.RUNNING;
            case BICYCLE:
                return DetectedActivity.ON_BICYCLE;
            case VEHICLE:
                return DetectedActivity.IN_VEHICLE;
            default:
                return DetectedActivity.UNKNOWN;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
