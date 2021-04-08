package com.android.oneclick.screenOff;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.oneclick.R;

public class ScreenOffActivity extends AppCompatActivity {
    static final int RESULT_ENABLE = 1 ;
    DevicePolicyManager deviceManger ;
    ComponentName compName ;
    Button btnEnable , btnLock ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_off);
        btnEnable = findViewById(R.id. btnEnable ) ;
        btnLock = findViewById(R.id. btnLock ) ;
        deviceManger = (DevicePolicyManager)
                getSystemService(Context. DEVICE_POLICY_SERVICE ) ;
        compName = new ComponentName( this, DeviceAdmin.class) ;
        boolean active = deviceManger .isAdminActive( compName ) ;
        if (active) {
            btnEnable .setText( "Disable" ) ;
            btnLock .setVisibility(View. VISIBLE ) ;
        } else {
            btnEnable .setText( "Enable" ) ;
            btnLock .setVisibility(View. GONE ) ;
        }
    }

    public void enablePhone (View view) {
        boolean active = deviceManger .isAdminActive( compName ) ;
        if (active) {
            deviceManger .removeActiveAdmin( compName ) ;
            btnEnable .setText( "Enable" ) ;
            btnLock .setVisibility(View. GONE ) ;
        } else {
            Intent intent = new Intent(DevicePolicyManager. ACTION_ADD_DEVICE_ADMIN ) ;
            intent.putExtra(DevicePolicyManager. EXTRA_DEVICE_ADMIN , compName ) ;
            intent.putExtra(DevicePolicyManager. EXTRA_ADD_EXPLANATION , "You should enable the app!" ) ;
            startActivityForResult(intent , RESULT_ENABLE ) ;
        }
    }

    public void lockPhone (View view) {
        deviceManger .lockNow() ;
    }

    @Override
    protected void onActivityResult ( int requestCode , int resultCode , @Nullable Intent data) {
        super .onActivityResult(requestCode , resultCode , data) ;
        switch (requestCode) {
            case RESULT_ENABLE :
                if (resultCode == Activity. RESULT_OK ) {
                    btnEnable .setText( "Disable" ) ;
                    btnLock .setVisibility(View. VISIBLE ) ;
                } else {
                    Toast. makeText (getApplicationContext() , "Failed!" ,
                            Toast. LENGTH_SHORT ).show() ;
                }
                return;
        }
    }
}