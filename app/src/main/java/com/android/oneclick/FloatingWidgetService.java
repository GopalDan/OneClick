package com.android.oneclick;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.oneclick.screenOff.DeviceAdmin;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.annotation.Nullable;

public class FloatingWidgetService extends Service {

    private final static float CLICK_DRAG_TOLERANCE = 10; // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.
    private static final String TAG = "FloatingWidgetService";

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    private View mOverlayView;
    private int fabX = 0;
    private int fabY = 50;


    private View.OnTouchListener fabOnTouchListener = new View.OnTouchListener() {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    //remember the initial position.
                    initialX = params.x;
                    initialY = params.y;


                    //get the touch location
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();

                    return false;

                case MotionEvent.ACTION_UP:

                    //xDiff and yDiff contain the minor changes in position when the view is clicked.
                    float xDiff = event.getRawX() - initialTouchX;
                    float yDiff = event.getRawY() - initialTouchY;

                    if (Math.abs(xDiff) < CLICK_DRAG_TOLERANCE && Math.abs(yDiff) < CLICK_DRAG_TOLERANCE) { // A click
//                        showDialog();
                        showDialogNew();
                        return false;
                    } else { // A drag
                         mWindowManager.updateViewLayout(mOverlayView, params);
                        return true; // Consumed
                    }

                case MotionEvent.ACTION_MOVE:

                    int xDiff2 = Math.round(event.getRawX() - initialTouchX);
                    int yDiff2 = Math.round(event.getRawY() - initialTouchY);


                    //Calculate the X and Y coordinates of the view.
                    params.x = initialX + xDiff2;
                    params.y = initialY + yDiff2;

                    fabX = initialX + xDiff2;
                    fabY = initialY + yDiff2;
                    //Update the layout with new X & Y coordinates
                    mWindowManager.updateViewLayout(mOverlayView, params);


                    return false;
            }
            return false;
        }

    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.AppTheme);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mOverlayView == null) {

            mOverlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);

            int LAYOUT_FLAG;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            }
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG, // WindowManager.LayoutParams.TYPE_PHONE
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);


            //Specify the view position
            params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
            params.x = 0;
            params.y = 100;


            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mOverlayView, params);

            FloatingActionButton fab = (FloatingActionButton) mOverlayView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    showDialog();
                }
            });
            fab.setOnTouchListener(fabOnTouchListener);


        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = LayoutInflater.from(this).inflate(R.layout.custom_layout, null);
        builder.setView(customLayout);
        // create and show the alert dialog
        final AlertDialog dialog = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = fabX;   //x position
        wmlp.y = fabY;   //y position

        LinearLayout screenShot = (LinearLayout) customLayout.findViewById(R.id.screen_shot_layout);
        LinearLayout screenOff = (LinearLayout) customLayout.findViewById(R.id.screen_off_layout);
        LinearLayout volumeUp = (LinearLayout) customLayout.findViewById(R.id.volume_up_layout);
        LinearLayout volumeDown = (LinearLayout) customLayout.findViewById(R.id.volume_down_layout);
        LinearLayout homeScreen = (LinearLayout) customLayout.findViewById(R.id.home_screen_layout);
        LinearLayout recentApps = (LinearLayout) customLayout.findViewById(R.id.recent_apps_layout);


        screenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FloatingWidgetService.this, IllusionActivity.class));
//                Toast.makeText(FloatingWidgetService.this, "Toasting A", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });


        homeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME); //CATEGORY_HOME
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    dialog.dismiss();

                }catch (Exception ex){
                    Log.e(TAG, "onClick: ");
                }

               /* Intent goHome = new Intent(Intent.ACTION_MAIN);
                goHome.setClassName("com.sec.android.app.launcher", "com.android.launcher2.Launcher");
                startActivity(goHome);*/

               /* PackageManager pm = getPackageManager();
                Intent i = new Intent("android.intent.action.MAIN");
                i.addCategory("android.intent.category.HOME");
                List<ResolveInfo> lst = pm.queryIntentActivities(i, 0);
                if (lst != null) {
                    for (ResolveInfo resolveInfo : lst) {
                        try {
                            Intent home = new Intent("android.intent.action.MAIN");
                            home.addCategory("android.intent.category.HOME");
                            home.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                            startActivity(home);
                            dialog.dismiss();
                            break;
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }*/

               /*Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
                intentToResolve.addCategory(Intent.CATEGORY_HOME);
                intentToResolve.setPackage("com.android.launcher");
                ResolveInfo ri = getPackageManager().resolveActivity(intentToResolve, 0);
                if (ri != null)
                {
                    Intent intent = new Intent(intentToResolve);
                    intent.setClassName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                }*/


            }
        });


        screenOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               DevicePolicyManager deviceManger = (DevicePolicyManager) getSystemService(Context. DEVICE_POLICY_SERVICE ) ;
               ComponentName  compName = new ComponentName(FloatingWidgetService.this, DeviceAdmin.class) ;
                boolean active = deviceManger .isAdminActive(compName) ;
                if(active){
                    deviceManger.lockNow();
                }else{
                    Toast.makeText(FloatingWidgetService.this, "permission required", Toast.LENGTH_SHORT).show();
                }

               // I found a way to do it by implementing a custom AccessibilityService and calling
                // AccessibilityService.performGlobalAction(GLOBAL_ACTION_RECENTS),

            }
        });


        volumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            }
        });

        volumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            }
        });

        homeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                AccessibilityService accessibilityService = new CustomGlobalAccessibilityService();
//                accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
//                startService(new Intent(FloatingWidgetService.this, CustomGlobalAccessibilityService.class));

            }
        });

        recentApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AccessibilityService accessibilityService = new CustomGlobalAccessibilityService();
//                accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                startService(new Intent(FloatingWidgetService.this, CustomGlobalAccessibilityService.class));
            }
        });

        dialog.show();

    }

    private void showDialogNew() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int widthLcl = (int) (displayMetrics.widthPixels*0.9f);
        int heightLcl = (int) (displayMetrics.heightPixels*0.9f);

        int width = (int) (widthLcl *0.85);
        final Dialog dialog = new Dialog(this, R.style.CustomDialogNew);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View customLayout = LayoutInflater.from(this).inflate(R.layout.custom_layout, null);
        dialog.setContentView(customLayout);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);

        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = LayoutInflater.from(this).inflate(R.layout.custom_layout, null);
        builder.setView(customLayout);
        // create and show the alert dialog
        final AlertDialog dialog = builder.create();*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = fabX;   //x position
        wmlp.y = fabY;   //y position

        LinearLayout screenShot = (LinearLayout) customLayout.findViewById(R.id.screen_shot_layout);
        LinearLayout screenOff = (LinearLayout) customLayout.findViewById(R.id.screen_off_layout);
        LinearLayout volumeUp = (LinearLayout) customLayout.findViewById(R.id.volume_up_layout);
        LinearLayout volumeDown = (LinearLayout) customLayout.findViewById(R.id.volume_down_layout);
        LinearLayout homeScreen = (LinearLayout) customLayout.findViewById(R.id.home_screen_layout);
        LinearLayout recentApps = (LinearLayout) customLayout.findViewById(R.id.recent_apps_layout);


        screenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FloatingWidgetService.this, IllusionActivity.class));
//                Toast.makeText(FloatingWidgetService.this, "Toasting A", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });


        homeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME); //CATEGORY_HOME
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    dialog.dismiss();

                }catch (Exception ex){
                    Log.e(TAG, "onClick: ");
                }

               /* Intent goHome = new Intent(Intent.ACTION_MAIN);
                goHome.setClassName("com.sec.android.app.launcher", "com.android.launcher2.Launcher");
                startActivity(goHome);*/

               /* PackageManager pm = getPackageManager();
                Intent i = new Intent("android.intent.action.MAIN");
                i.addCategory("android.intent.category.HOME");
                List<ResolveInfo> lst = pm.queryIntentActivities(i, 0);
                if (lst != null) {
                    for (ResolveInfo resolveInfo : lst) {
                        try {
                            Intent home = new Intent("android.intent.action.MAIN");
                            home.addCategory("android.intent.category.HOME");
                            home.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                            startActivity(home);
                            dialog.dismiss();
                            break;
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }*/

               /*Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
                intentToResolve.addCategory(Intent.CATEGORY_HOME);
                intentToResolve.setPackage("com.android.launcher");
                ResolveInfo ri = getPackageManager().resolveActivity(intentToResolve, 0);
                if (ri != null)
                {
                    Intent intent = new Intent(intentToResolve);
                    intent.setClassName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                }*/


            }
        });


        screenOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DevicePolicyManager deviceManger = (DevicePolicyManager) getSystemService(Context. DEVICE_POLICY_SERVICE ) ;
                ComponentName  compName = new ComponentName(FloatingWidgetService.this, DeviceAdmin.class) ;
                boolean active = deviceManger .isAdminActive(compName) ;
                if(active){
                    deviceManger.lockNow();
                }else{
                    Toast.makeText(FloatingWidgetService.this, "permission required", Toast.LENGTH_SHORT).show();
                }

                // I found a way to do it by implementing a custom AccessibilityService and calling
                // AccessibilityService.performGlobalAction(GLOBAL_ACTION_RECENTS),

            }
        });


        volumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            }
        });

        volumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            }
        });

        homeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                AccessibilityService accessibilityService = new CustomGlobalAccessibilityService();
//                accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
//                startService(new Intent(FloatingWidgetService.this, CustomGlobalAccessibilityService.class));

            }
        });

        recentApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AccessibilityService accessibilityService = new CustomGlobalAccessibilityService();
//                accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                startService(new Intent(FloatingWidgetService.this, CustomGlobalAccessibilityService.class));
            }
        });

        dialog.show();

    }

    private void showDialogNew2(){
        Dialog alertDialog = new Dialog(this, R.style.CustomDialog);
        final View customLayout = LayoutInflater.from(this).inflate(R.layout.custom_layout, null);
//        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(customLayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

}
