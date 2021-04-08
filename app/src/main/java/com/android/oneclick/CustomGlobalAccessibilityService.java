package com.android.oneclick;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.oneclick.screenOff.DeviceAdmin;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CustomGlobalAccessibilityService extends AccessibilityService {

    private static final String TAG = "CustomGlobalAccessibilityService";

    FrameLayout mLayout;
    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        setTheme(R.style.AppTheme);

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e(TAG, "onServiceConnected: ");
        // Create an overlay and display the action bar
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        params.format = PixelFormat.TRANSLUCENT;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM | Gravity.END;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.overlay_layout, mLayout);
        wm.addView(mLayout, params);
        FloatingActionButton fab = (FloatingActionButton) mLayout.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(CustomGlobalAccessibilityService.this, "working", Toast.LENGTH_SHORT).show();
                showDialog();
            }
        });
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

      /*   dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = 100;   //x position
        wmlp.y = 100;   //y position*/
        dialog.show();

        LinearLayout screenShot = (LinearLayout) customLayout.findViewById(R.id.screen_shot_layout);
        LinearLayout screenOff = (LinearLayout) customLayout.findViewById(R.id.screen_off_layout);
        LinearLayout volumeUp = (LinearLayout) customLayout.findViewById(R.id.volume_up_layout);
        LinearLayout volumeDown = (LinearLayout) customLayout.findViewById(R.id.volume_down_layout);
        LinearLayout homeScreen = (LinearLayout) customLayout.findViewById(R.id.home_screen_layout);
        LinearLayout recentApps = (LinearLayout) customLayout.findViewById(R.id.recent_apps_layout);
        LinearLayout phoneOptions = (LinearLayout) customLayout.findViewById(R.id.phone_option_layout);


        screenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomGlobalAccessibilityService.this, IllusionActivity.class));
                dialog.dismiss();

            }
        });


        homeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performGlobalAction(GLOBAL_ACTION_HOME);
                dialog.dismiss();
            }
        });


        screenOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DevicePolicyManager deviceManger = (DevicePolicyManager) getSystemService(Context. DEVICE_POLICY_SERVICE ) ;
                ComponentName compName = new ComponentName(CustomGlobalAccessibilityService.this, DeviceAdmin.class) ;
                boolean active = deviceManger .isAdminActive(compName) ;
                if(active){
                    deviceManger.lockNow();
                }else{
                    Toast.makeText(CustomGlobalAccessibilityService.this, "permission required", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

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


        recentApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performGlobalAction(GLOBAL_ACTION_RECENTS);
                dialog.dismiss();

            }
        });

        phoneOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);
                dialog.dismiss();

            }
        });
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.e(TAG, "onAccessibilityEvent: ");
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

    private void unusedCode(){

               /* try {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME); //CATEGORY_HOME
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    dialog.dismiss();

                }catch (Exception ex){
                    Log.e(TAG, "onClick: ");
                }*/

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

         // I found a way to do it by implementing a custom AccessibilityService and calling
        // AccessibilityService.performGlobalAction(GLOBAL_ACTION_RECENTS),


    }


}
