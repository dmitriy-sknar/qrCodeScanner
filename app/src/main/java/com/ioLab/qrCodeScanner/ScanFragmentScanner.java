package com.ioLab.qrCodeScanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by disknar on 01.08.2016.
 */
public class ScanFragmentScanner extends FragmentActivity implements ZXingScannerView.ResultHandler{

    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";

    private ZXingScannerView mScannerView;
    private Result result;
//    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.scanner);
        if(mScannerView == null) {
            mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        }
        setContentView(mScannerView);
        // read parameters from the intent used to launch the activity.
        boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, false);
        boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.setFlash(useFlash);
        mScannerView.setAutoFocus(autoFocus);
        mScannerView.startCamera();         // Start camera
    }

    @Override
    public void handleResult(Result rawResult) {

        //Todo need help how to save scanned real image to show in history. Please help!
//        View myBarCodeView = mScannerView.getRootView();
//        myBarCodeView.setDrawingCacheEnabled(true);
//        mScannerView.setDrawingCacheEnabled(true);
        //Save it in bitmap
//        mBitmap = myBarCodeView.getDrawingCache(true);

        result = rawResult;
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        StringBuilder sb = new StringBuilder();
        sb.append(rawResult.getText()).append("\n").append("Type: ").append(rawResult.getBarcodeFormat().toString());

        showDialog(this, "Scan Result", sb.toString(), "Save result", "Cancel", "Rescan");
        mScannerView.stopCameraPreview();
    }

    private AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo, CharSequence buttonRescan ) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent resultInt = new Intent();
                resultInt.putExtra("ScanResultText", result.getText());
                resultInt.putExtra("ScanResultFormat", result.getBarcodeFormat());
                resultInt.putExtra("ScanResultTime", result.getTimestamp());
                resultInt.putExtra("ScanResultRawBytes", result.getRawBytes());
//                resultInt.putExtra("ScanResultBitmap", mBitmap);
                setResult(RESULT_OK, resultInt);
                finish();
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        downloadDialog.setNeutralButton(buttonRescan, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                resumeScanning();
            }
        });
        return downloadDialog.show();
    }

    public void resumeScanning(){
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onBackPressed() {
        mScannerView.stopCamera();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();
        finish();
    }

}
