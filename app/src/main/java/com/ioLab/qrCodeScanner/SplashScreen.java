package com.ioLab.qrCodeScanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.concurrent.TimeUnit;

import app.num.barcodescannerproject.R;

public class SplashScreen extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //to consider if to continue work if camera is missing on the device
        if(isCameraAvailable()) {
            new Thread() {
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        else{
            showDialog(this);
        }
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void showDialog(final Activity act) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(R.string.splash_screen_alert_dialog);
        downloadDialog.setMessage(R.string.splash_screen_alert_dialog_message);
        downloadDialog.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        downloadDialog.show();
    }
}
