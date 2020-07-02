package tk3.group42.yetanothercontextawareapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

public class LocationReceiver extends BroadcastReceiver {
    private static final String TAG = "LocationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationResult locationResult = LocationResult.extractResult(intent);
        if (locationResult == null) {
            return;
        }

        float stepCount = ActivityTransitionReceiverService.getStepCount();
        Location location = locationResult.getLastLocation();

        LocationRegister locationRegister = LocationRegister.getInstance();
        locationRegister.unRegisterForLocationUpdates();

        Log.d(TAG, "onLocationResult: with accuracy:"+ location.getAccuracy() + " latitude:" + location.getLatitude() + " longitude:" + location.getLongitude() + " stepCount:" + stepCount);

        Runnable runnable = () -> {

            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            sharedPref.edit().putString(FitnessUtility.CURRENT_LATITUDE, String.valueOf(location.getLatitude())).apply();
            sharedPref.edit().putString(FitnessUtility.CURRENT_LONGITUDE, String.valueOf(location.getLongitude())).apply();

            try {
                Thread.sleep(20000);
            } catch (InterruptedException ignored) {

            } finally {
                locationRegister.registerForLocationUpdates(context);
            }
        };

        new Thread(runnable).start();
    }
}
