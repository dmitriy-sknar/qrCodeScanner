package com.ioLab.qrCodeScanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ioLab.qrCodeScanner.Utils.MyQRCode;

import java.util.Date;


/**
 * Created by disknar on 08.09.2016.
 */
public class CodeDetails extends AppCompatActivity {

    private MyQRCode myQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        myQRCode = new MyQRCode(
                this,
                intent.getStringExtra("name"),
                new Date (intent.getLongExtra("date", 0L)),
                intent.getStringExtra("format"),
                intent.getStringExtra("comments"));


    }




}
