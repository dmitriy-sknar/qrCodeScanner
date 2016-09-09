package com.ioLab.qrCodeScanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ioLab.qrCodeScanner.Utils.MyQRCode;

import java.util.Date;

import app.num.barcodescannerproject.R;

/**
 * Created by disknar on 08.09.2016.
 */
public class CodeDetails extends Activity {

    private MyQRCode myQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.details_layout);
        setContentView(R.layout.splash);

        Intent intent = getIntent();
        myQRCode = new MyQRCode(
                this,
                intent.getStringExtra("name"),
                new Date (intent.getLongExtra("date", 0L)),
                intent.getStringExtra("format"),
                intent.getStringExtra("comments"));


    }




}
