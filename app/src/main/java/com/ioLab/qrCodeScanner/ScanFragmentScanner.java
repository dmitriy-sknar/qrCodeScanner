package com.ioLab.qrCodeScanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.zxing.Result;

import app.num.barcodescannerproject.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by disknar on 01.08.2016.
 */
public class ScanFragmentScanner extends FragmentActivity implements ZXingScannerView.ResultHandler{

    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";

    private ZXingScannerView mScannerView;
    private Result result;

    GLSurfaceView gl = new GLSurfaceView(this);

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
        mScannerView.setDrawingCacheEnabled(true);
        View myBarCodeView = mScannerView.getRootView();
        myBarCodeView.setDrawingCacheEnabled(true);
        Bitmap mBitmap = myBarCodeView.getDrawingCache();
        Bitmap mBitmap2 = mScannerView.getDrawingCache();//Save it in bitmap
        mScannerView.setDrawingCacheEnabled(false);
        myBarCodeView.setDrawingCacheEnabled(false);

        // create bitmap screen capture
        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        Bitmap b1 = takeScreenshot(myBarCodeView, Bitmap.Config.ARGB_8888);
        Bitmap b2 = takeScreenshot(mScannerView, Bitmap.Config.ARGB_8888);
        Bitmap b3 = takeScreenshot(v1, Bitmap.Config.ARGB_8888);


        gl.setZOrderOnTop(true);


        result = rawResult;
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        StringBuilder sb = new StringBuilder();
        sb.append(rawResult.getText()).append("\n").append("Type: ").append(rawResult.getBarcodeFormat().toString());

        String dialogTitle = getResources().getString(R.string.scan_dialog_title);
        String dialogBtnSaveResult = getResources().getString(R.string.scan_dialog_btn_save_result);
        String dialogBtnCancel = getResources().getString(R.string.scan_dialog_btn_cancel);
        String dialogBtnRescan = getResources().getString(R.string.scan_dialog_btn_rescan);

        showDialog(this, dialogTitle, sb.toString(), dialogBtnSaveResult, dialogBtnCancel, dialogBtnRescan);
        mScannerView.stopCameraPreview();
    }

    public static Bitmap takeScreenshot(View view, Bitmap.Config quality) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), quality);
        Canvas canvas = new Canvas(bitmap);

        Drawable backgroundDrawable = view.getBackground();
        if (backgroundDrawable != null) {
            backgroundDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);

        return bitmap;
    }

    private void showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo, CharSequence buttonRescan ) {
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
        downloadDialog.show();
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
