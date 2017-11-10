package com.teamvii.dogscheduler.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.teamvii.dogscheduler.R;
import com.teamvii.dogscheduler.background.MyService;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {
    boolean isSwitchChecked = false;
    Switch mSwitch;
    TextView result;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Intent broadcasting;
    String[] permissions = {
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };
    final int permissionRequestCode = 912;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("SwitchState", MODE_PRIVATE);
        editor = preferences.edit();

        result = findViewById(R.id.result);
        mSwitch = findViewById(R.id.switch1);
        broadcasting = new Intent(getApplicationContext(), MyService.class);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            PackageManager pm = getPackageManager();

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mSwitch.isChecked()) {
                    isSwitchChecked = true;
                    pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), MyService.class),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    startService(broadcasting);
                } else if (!mSwitch.isChecked()) {
                    isSwitchChecked = false;
                    pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), MyService.class),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    stopService(broadcasting);
                }
                editor.putBoolean("state", isSwitchChecked);
                editor.apply();
            }
        });
        if (preferences != null) {
            isSwitchChecked = preferences.getBoolean("state", false);
            mSwitch.setChecked(isSwitchChecked);
        }


        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permission)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    alertDialog(this, permission, "This app is designed to forward all Messages");
                } else {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            permissions,
                            permissionRequestCode);
                }
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {
            case permissionRequestCode: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    toast("Permission Granted");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    finish();
                }
            }
        }
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public AlertDialog alertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                permissions,
                                permissionRequestCode);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        return alertDialog.create();
    }

}
