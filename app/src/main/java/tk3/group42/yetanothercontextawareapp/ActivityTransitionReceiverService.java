package tk3.group42.yetanothercontextawareapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

import static tk3.group42.yetanothercontextawareapp.MainActivity.CHANNEL_ID;


public class ActivityTransitionReceiverService extends Service {
    private static final String TAG = "ActivityTransitionRecei";
    private static BroadcastReceiver mActivityTransitionReceiver;
    private String ACTION_STOP_SERVICE = "tk3.group42.yetanothercontextawareapp.ACTION_STOP_SERVICE";
    static float stepCount;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityTransitionReceiver();
        setupStepSensor();
    }

    public static float getStepCount() {
        return stepCount;
    }

    private SensorEventListener mStepSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            stepCount = event.values[0];
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void setupStepSensor() {
        SensorManager mSensorManager;
        Sensor mStep;
        // Get sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Get the default sensor of specified type
        mStep = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (mStep != null) {
            mSensorManager.registerListener(mStepSensorListener, mStep, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            Log.d(TAG,"User killed service");
            stopSelf();
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent stopSelf = new Intent(this, ActivityTransitionReceiverService.class);
        stopSelf.setAction(this.ACTION_STOP_SERVICE);
        PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf,PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Yet Another Context Aware App")
                .setContentText("Tracking activities...")
                .setSmallIcon(R.drawable.ic_run)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_run, "Stop Tracking",
                        pStopSelf)
                .build();

        startForeground(1, notification);
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mActivityTransitionReceiver);
        mActivityTransitionReceiver = null;
    }

    private void registerActivityTransitionReceiver() {
        mActivityTransitionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: Registered Activity Transition Receiver");
                if (ActivityTransitionResult.hasResult(intent)) {
                    ActivityTransitionResult activityTransitionResult = ActivityTransitionResult.extractResult(intent);
                    processTransition(activityTransitionResult);
                }
            }

            void processTransition(final ActivityTransitionResult activityTransitionResult) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (ActivityTransitionEvent activityTransitionEvent : activityTransitionResult.getTransitionEvents()) {

                            if (activityTransitionEvent.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                                Log.d(TAG, "run: " + FitnessUtility.getActivityDisplayName(activityTransitionEvent.getActivityType()) + " TRANSITION_ENTER");
                            } else {
                                Log.d(TAG, "run: " + FitnessUtility.getActivityDisplayName(activityTransitionEvent.getActivityType()) + " TRANSITION_EXIT");
                            }
                        }
                    }
                });
            }
        };
        IntentFilter intentFilter = new IntentFilter("tk3.group42.yetanothercontextawareapp.ACTIVITY_TRANSITION");
        registerReceiver(mActivityTransitionReceiver, intentFilter);
    }
}
