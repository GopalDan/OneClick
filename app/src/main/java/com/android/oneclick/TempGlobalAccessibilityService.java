package com.android.oneclick;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.oneclick.screenOff.DeviceAdmin;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TempGlobalAccessibilityService extends AccessibilityService {

    private static final String TAG = "CustomGlobalAccessibilityService";
    private final static float CLICK_DRAG_TOLERANCE = 10; // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.

    FrameLayout mLayout;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    private View mOverlayView;
    private int fabX = 0;
    private int fabY = 50;
    FloatingActionButton fab;
    private boolean isScreenShotClicked = false;

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

                    return true;

                case MotionEvent.ACTION_UP:

                    //xDiff and yDiff contain the minor changes in position when the view is clicked.
                    float xDiff = event.getRawX() - initialTouchX;
                    float yDiff = event.getRawY() - initialTouchY;

                    if (Math.abs(xDiff) < CLICK_DRAG_TOLERANCE && Math.abs(yDiff) < CLICK_DRAG_TOLERANCE) { // A click
//                        Toast.makeText(TempGlobalAccessibilityService.this, "Floating click", Toast.LENGTH_SHORT).show();
//                        showDialog();
                       fab.setVisibility(View.GONE);
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


                    return true;
            }
            return true;
        }

    };

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


//        startService(new Intent(this, ShakeService.class));
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
        fab = (FloatingActionButton) mOverlayView.findViewById(R.id.fab);
        fab.setAlpha(0.95f);

       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TempGlobalAccessibilityService.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });*/
        fab.setOnTouchListener(fabOnTouchListener);


        /*// Create an overlay and display the action bar
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
                Toast.makeText(TempGlobalAccessibilityService.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });*/

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
                startActivity(new Intent(TempGlobalAccessibilityService.this, IllusionActivity.class));
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
                ComponentName compName = new ComponentName(TempGlobalAccessibilityService.this, DeviceAdmin.class) ;
                boolean active = deviceManger .isAdminActive(compName) ;
                if(active){
                    deviceManger.lockNow();
                }else{
                    Toast.makeText(TempGlobalAccessibilityService.this, "permission required", Toast.LENGTH_SHORT).show();
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

    private void showDialogNew() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int widthLcl = (int) (displayMetrics.widthPixels*0.9f);
        int heightLcl = (int) (displayMetrics.heightPixels*0.9f);

        int width = (int) (widthLcl *0.90);
        final Dialog dialog = new Dialog(this, R.style.CustomDialogNew);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View customLayout = LayoutInflater.from(this).inflate(R.layout.custom_layout, null);
        dialog.setContentView(customLayout);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //When you touch outside of dialog bounds,
                        //the dialog gets canceled and this method executes.
                        //Toast.makeText(TempGlobalAccessibilityService.this, "touch cancel", Toast.LENGTH_SHORT).show();
                        if(!isScreenShotClicked) {
                            fab.setVisibility(View.VISIBLE);
                        }else{
                            // delay of three seconds
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    //do something
                                    fab.setVisibility(View.VISIBLE);
                                    isScreenShotClicked = false;

                                }
                            }, 3000 );//time in milisecond
                        }

                    }
                }
        );
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

//        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = fabX;   //x position
        wmlp.y = fabY;   //y position

       /* dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Toast.makeText(TempGlobalAccessibilityService.this, "touch outside", Toast.LENGTH_SHORT).show();

            }
        });*/

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
                isScreenShotClicked = true;
                startActivity(new Intent(TempGlobalAccessibilityService.this, IllusionActivity.class));
                dialog.cancel();

            }
        });


        homeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isScreenShotClicked = false;
                performGlobalAction(GLOBAL_ACTION_HOME);
                dialog.cancel();

            }
        });


        screenOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isScreenShotClicked = false;
                DevicePolicyManager deviceManger = (DevicePolicyManager) getSystemService(Context. DEVICE_POLICY_SERVICE ) ;
                ComponentName compName = new ComponentName(TempGlobalAccessibilityService.this, DeviceAdmin.class) ;
                boolean active = deviceManger .isAdminActive(compName) ;
                if(active){
                    deviceManger.lockNow();
                }else{
                    Toast.makeText(TempGlobalAccessibilityService.this, "permission required", Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();

            }
        });

        volumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isScreenShotClicked = false;

                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            }
        });

        volumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isScreenShotClicked = false;

                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            }
        });


        recentApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isScreenShotClicked = false;

                performGlobalAction(GLOBAL_ACTION_RECENTS);
                dialog.cancel();

            }
        });

        phoneOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isScreenShotClicked = false;

                performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);
                dialog.cancel();

            }
        });

        dialog.show();

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }


}
