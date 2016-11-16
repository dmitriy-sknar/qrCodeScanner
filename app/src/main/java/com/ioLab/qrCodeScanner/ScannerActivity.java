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

public class ScannerActivity extends FragmentActivity implements ZXingScannerView.ResultHandler{

    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";

    private ZXingScannerView mScannerView;
    private Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.scanner);
        if(mScannerView == null) {
            mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        }
        setContentView(mScannerView);
        // read parameters from the intent used to launch the activity.
        boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, true);
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
//        View v1 = getWindow().getDecorView().getRootView();
//        v1.setDrawingCacheEnabled(true);
//        mScannerView.setDrawingCacheEnabled(true);
//        myBarCodeView.setDrawingCacheEnabled(true);
//        Bitmap mBitmap = myBarCodeView.getDrawingCache();
//        Bitmap mBitmap2 = mScannerView.getDrawingCache();//Save it in bitmap
//        Bitmap mBitmap3 = Bitmap.createBitmap(v1.getDrawingCache());
//        mScannerView.setDrawingCacheEnabled(false);
//        myBarCodeView.setDrawingCacheEnabled(false);
//        v1.setDrawingCacheEnabled(false);

        result = rawResult;
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        StringBuilder sb = new StringBuilder();
        sb.append(rawResult.getText()).append("\n")
                .append(getResources().getString(R.string.share_text_codetype))
                .append(" ")
                .append(rawResult.getBarcodeFormat()
                        .toString());

        String dialogTitle = getResources().getString(R.string.scan_dialog_title);
        String dialogBtnSaveResult = getResources().getString(R.string.scan_dialog_btn_save_result);
        String dialogBtnCancel = getResources().getString(R.string.scan_dialog_btn_cancel);
        String dialogBtnRescan = getResources().getString(R.string.scan_dialog_btn_rescan);

        showDialog(this, dialogTitle, sb.toString(), dialogBtnSaveResult, dialogBtnCancel, dialogBtnRescan);
        mScannerView.stopCameraPreview();
    }

    private void showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo, CharSequence buttonRescan ) {
        AlertDialog.Builder scanDialog = new AlertDialog.Builder(act);
        scanDialog.setTitle(title);
        scanDialog.setMessage(message);
        scanDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
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
        scanDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        scanDialog.setNeutralButton(buttonRescan, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                resumeScanning();
            }
        });
        scanDialog.show();
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
