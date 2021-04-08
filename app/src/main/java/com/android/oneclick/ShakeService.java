package com.android.oneclick;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class ShakeService extends Service {

    private static final String TAG = "ShakeService";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private PowerManager.WakeLock mWakeLock;


    public ShakeService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        NotificationUtils.setNotification(this, "onCreate");

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        super.onStartCommand(intent, flags, startId);
        NotificationUtils.setNotification(this, "onStartCommand");

       /* Intent intent1 = new Intent(this, ShakeService.class);
        startForegroundService(intent1);*/
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector(this);
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {

               /* Intent i = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
                getApplicationContext().startActivity(i);*/

                /*SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(ShakeService.this);
                int value = sharedPreferenceManager.getData("key");
                Log.e(TAG, "onShake: " + value);*/
                /*Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + "8240174191"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }*/

                /*KeyguardManager km = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
                final KeyguardManager.KeyguardLock kl = km
                        .newKeyguardLock("MyKeyguardLock");
                kl.disableKeyguard();*/

                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "myWakeLock:");
                wakeLock.acquire(10*60*1000L);


            }
        });

        /*PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myApp:MyTag");
        mWakeLock.acquire(50*60*1000L *//*10 minutes*//*);*/

        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: " );
        super.onDestroy();
        NotificationUtils.setNotification(this, "onDestroy");
//        mWakeLock.release();
//        mSensorManager.unregisterListener(mShakeDetector);
    }


}
