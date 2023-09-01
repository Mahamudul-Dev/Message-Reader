package com.uniquepaybd.uniquepay;

import static com.uniquepaybd.uniquepay.LoginManager.getSavedUid;
import static com.uniquepaybd.uniquepay.LoginManager.getUserEmail;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    // Other variables...

    private NetworkChangeReceiver networkChangeReceiver;

    private static final int SMS_PERMISSION_CODE = 101;
    private static final int NOTIFICATION_PERMISSION_CODE = 102;

    private TextView runTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runTxt = findViewById(R.id.runTxt);

        networkChangeReceiver = new NetworkChangeReceiver(this);
        registerNetworkChangeReceiver();

        Log.d("email",getUserEmail(getApplicationContext()));
        Log.d("uid", getSavedUid(getApplicationContext()));

        // Check and request SMS permissions if needed
        if (checkSmsPermission()) {
            // SMSReceiver will display incoming messages as toasts.
            if (checkNotificationPermission()) {
                Intent serviceIntent = new Intent(this, MyBackgroundService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
            } else {
                requestNotificationPermission();
            }
        } else {
            requestSmsPermission();
        }
    }
    private void registerNetworkChangeReceiver() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }
    private void unregisterNetworkChangeReceiver() {
        unregisterReceiver(networkChangeReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChangeReceiver();
    }
    public void updateNetworkStatus(boolean isConnected) {
        // Handle network connectivity changes here
        if (isConnected) {
            runTxt.setText("App is running");
            runTxt.setTextColor(getColor(R.color.green));
        } else {
            runTxt.setText("App is not running due to network connectivity");
            runTxt.setTextColor(getColor(R.color.red));
        }
    }
    @Override
    public void onBackPressed() {
        // Display a confirmation dialog when the back button is pressed
        new AlertDialog.Builder(this)
                .setTitle("Exit Confirmation")
                .setIcon(R.drawable.baseline_exit_to_app_24)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes,Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Finish the activity and exit the app
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog and do nothing
                        dialog.dismiss();
                    }
                })
                .show();
    }


    // Check if SMS permissions are granted
    private boolean checkSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    // Request SMS permissions
    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
    }

    // Check if notification permissions are granted
    private boolean checkNotificationPermission() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    // Request notification permissions
    private void requestNotificationPermission() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivityForResult(intent, NOTIFICATION_PERMISSION_CODE);
    }
    private void requestSmsPermission2() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);

    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                ToastHelper.showCustomToast(this, "SMS permission denied. The app may not work correctly.", null);
                requestSmsPermission2();
            }
        }
    }

    // Handle notification permission request results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (checkNotificationPermission()) {
                // Notification permission granted
                Functions.createNotification(MainActivity.this,"Watching","Your app is running");
            } else {
                ToastHelper.showCustomToast(this,"Notification permission denied. Some features may not work.",null);
            }
        }
    }


}
