package com.android.oneclick;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.oneclick.accessibilityservice.AccessibilityFeatureActivity;
import com.android.oneclick.screenOff.DeviceAdmin;
import com.android.oneclick.screenOff.ScreenOffActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int RC_DRAW_OVER_OTHER_APP = 123;
    private static final int RC_WRITE_EXTERNAL_STORAGE = 57;
    private static final int RESULT_ENABLE = 67;
    private static final int RC_READ_EXTERNAL_STORAGE = 87;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startService(new Intent(this, ShakeService.class));

        final SwitchCompat drawOverTopSwitch = findViewById(R.id.draw_over_top_switch);
        drawOverTopSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Settings.canDrawOverlays(MainActivity.this)) {
//                    startService(new Intent(MainActivity.this, FloatingWidgetService.class));
                    showToast("permission is already granted");
                } else {
                    askForSystemOverlayPermission();
                }

            }
        });

        final SwitchCompat accessibilitySwitch = (SwitchCompat) findViewById(R.id.accessibility_service_switch);
        accessibilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isEnabled) {
                /*if(isEnabled){
                    startActivity(new Intent(MainActivity.this, AccessibilityFeatureActivity.class));
                }*/

                if (isAccessibilityEnabled()) {
                    showToast("permission is already granted");
                } else {
                    askForAccessibilityPermission();
                }
            }
        });

        final SwitchCompat screenOffSwitch = (SwitchCompat) findViewById(R.id.screen_off_switch);
        screenOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isEnabled) {
               /* if(isEnabled){
                    startActivity(new Intent(MainActivity.this, ScreenOffActivity.class));
                }*/
                askPermissionForScreenOffFeature();
            }
        });

        final SwitchCompat screenShotSwitch = (SwitchCompat) findViewById(R.id.screen_shot_switch);
        screenShotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isEnabled) {
                askExternalStorageWritingPermission();

            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String imagePath = "/storage/emulated/0/OneClick/Screenshots/oc_03042021_114919.png";
//                String imagePath = "/storage/emulated/0/Android/data/com.android.oneclick/files/OneClick/Screenshots/oc_03042021_112647.png";
                /*Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(imagePath), "image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);//Uri.fromFile(new File(filePath))*/

                /*Intent intent=new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri= FileProvider.getUriForFile(MainActivity.this,BuildConfig.APPLICATION_ID + ".provider", image);
                intent.setDataAndType(uri, MimeTypeMap.getSingleton().getMimeTypeFromExtension(imagePath));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                File file =  new File(imagePath);
                final Intent intent = new Intent(Intent.ACTION_VIEW)//
                        .setDataAndType(FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", file),
                                "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });


    }

    private void askPermissionForScreenOffFeature() {
        DevicePolicyManager deviceManger = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName compName = new ComponentName(this, DeviceAdmin.class);
        boolean active = deviceManger.isAdminActive(compName);
        if (active) {
            showToast("screen off feature enabled");
        } else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You should enable the app!");
            startActivityForResult(intent, RESULT_ENABLE);
        }
    }

    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogNew);
        View customLayout = LayoutInflater.from(this).inflate(R.layout.custom_layout, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//          dialog.getWindow().setBackgroundDrawable( new ColorDrawable(Color.parseColor("#7dffffff")));
        dialog.show();

    }

    private void showDialog1() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthLcl = (int) (displayMetrics.widthPixels * 0.9f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.9f);

        int width = (int) (widthLcl * 0.85);
        Dialog alertDialog = new Dialog(this, R.style.CustomDialogNew);
        View customLayout = LayoutInflater.from(this).inflate(R.layout.custom_layout, null);
        alertDialog.setContentView(customLayout);
        alertDialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        LinearLayout layout = (LinearLayout) customLayout.findViewById(R.id.volume_down_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "clicked 1", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout layout3 = (LinearLayout) customLayout.findViewById(R.id.recent_apps_layout);
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "clicked 3", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();

    }

    private void askForAccessibilityPermission() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    private boolean isAccessibilityEnabled() {
        String LOGTAG = "ACCESSIBILITY_ERROR";
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE = getApplicationContext().getPackageName() + "/" + TempGlobalAccessibilityService.class.getCanonicalName();
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(LOGTAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {

            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d(LOGTAG, "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE)) {
                        return true;
                    }
                }
            }
        } else {
            Log.d(LOGTAG, "***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }

    private void askExternalStorageWritingPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RC_WRITE_EXTERNAL_STORAGE);
        } else {

            showToast("screenshot feature already enabled");

        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    RC_READ_EXTERNAL_STORAGE);
        } else {
            showToast("reading granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RC_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "writing permission granted", Toast.LENGTH_SHORT).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void askForSystemOverlayPermission() {
        //If the draw over permission is not available to open the settings screen to grant permission.
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, RC_DRAW_OVER_OTHER_APP);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_DRAW_OVER_OTHER_APP) {
            if (!Settings.canDrawOverlays(this)) {
                //Permission is not available. Display error text.
                errorToast();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void errorToast() {
        Toast.makeText(this, "Please grant the permission in order to use.", Toast.LENGTH_LONG).show();
    }

    private void showToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + "/OneClick");

            if (!sdCardDirectory.exists()) {
                sdCardDirectory.mkdirs();
            }
            //  File file = new File(dir, fileName);
            File file = new File(sdCardDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            file.createNewFile();

            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

//            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(file);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            String path = file.getPath();
            //openScreenshot(imageFile);
            Toast.makeText(this, "screenshot taken", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "screenshot error", Toast.LENGTH_SHORT).show();

        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }


}