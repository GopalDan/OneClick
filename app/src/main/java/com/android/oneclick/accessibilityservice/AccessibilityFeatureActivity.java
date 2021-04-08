package com.android.oneclick.accessibilityservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.oneclick.CustomGlobalAccessibilityService;
import com.android.oneclick.R;

public class AccessibilityFeatureActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_feature);
        if(isAccessibilityEnabled()){
            Toast.makeText(this, "Already granted", Toast.LENGTH_SHORT).show();
        }else{
            askForAccessibilityPermission();
        }
    }


    private void askForAccessibilityPermission(){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    private boolean isAccessibilityEnabled() {
        String LOGTAG = "ACCESSIBILITY_ERROR";
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE = getApplicationContext().getPackageName() + "/" + CustomGlobalAccessibilityService.class.getCanonicalName();
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(LOGTAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1) {

            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d(LOGTAG, "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE)){
                        return true;
                    }
                }
            }
        }
        else {
            Log.d(LOGTAG, "***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }


}