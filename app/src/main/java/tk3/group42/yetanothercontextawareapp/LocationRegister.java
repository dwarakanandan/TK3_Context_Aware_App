package tk3.group42.yetanothercontextawareapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

public class LocationRegister {

    private static final String TAG = "LocationRegister";
    private static LocationRegister mInstance = null;
    private PendingIntent mPendingIntent;
    private FusedLocationProviderClient fusedLocationClient;

    public static LocationRegister getInstance() {
        if (mInstance == null) {
            mInstance = new LocationRegister();
        }
        return mInstance;
    }

    private LocationRegister() {

    }

    public void registerForLocationUpdates(Context mContext) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20 * 1000);
        mLocationRequest.setFastestInterval(20 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Intent mIntent = new Intent(mContext, LocationReceiver.class);
        mIntent.setAction("tk3.group42.yetanothercontextawareapp.LOCATION_UPDATE");
        mIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, mIntent, 0);

        fusedLocationClient = new FusedLocationProviderClient(mContext);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "registerForLocationUpdates: Permission check failed");
            return;
        }
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mPendingIntent);
    }

    public void unRegisterForLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(mPendingIntent);
    }
}
