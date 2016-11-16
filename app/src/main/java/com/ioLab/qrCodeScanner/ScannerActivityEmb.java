package com.ioLab.qrCodeScanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

/**
 * Custom Scannner Activity extending from Activity to display a my custom layout form scanner view.
 */
public class ScannerActivityEmb extends Activity
        implements DecoratedBarcodeView.TorchListener {

//    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageView switchFlashlightButton;
    private boolean flashIsOn = false;
    private BarcodeResult mResult;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                pauseScan();
                handleResult(result);
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_emb);

        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        switchFlashlightButton = (ImageView)findViewById(R.id.btn_switch_flashlight);
        barcodeScannerView.setTorchListener(this);
        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }
        barcodeScannerView.decodeContinuous(callback);

//        capture = new CaptureManager(this, barcodeScannerView);
//        capture.initializeFromIntent(getIntent(), savedInstanceState);
//        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
//        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
//        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

   //Check if the device's camera has a Flashlight.
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
        if (!flashIsOn) {
            barcodeScannerView.setTorchOn();
            flashIsOn = true;
        } else {
            barcodeScannerView.setTorchOff();
            flashIsOn = false;
        }
    }

    @Override
    public void onTorchOn() {
        switchFlashlightButton.setImageResource(R.mipmap.ic_flash);
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setImageResource(R.mipmap.ic_flash_off);
    }

    public void triggerScan() {
        barcodeScannerView.decodeSingle(callback);
    }
    public void pauseScan() {
        barcodeScannerView.pause();
    }
    public void resumeScanning() {
        barcodeScannerView.resume();
    }

    private void handleResult(BarcodeResult result) {
        mResult = result;

        StringBuilder sb = new StringBuilder();
        sb.append(result.getText()).append("\n")
                .append(getResources().getString(R.string.share_text_codetype))
                .append(" ")
                .append(result.getBarcodeFormat()
                        .toString());

        String dialogTitle = getResources().getString(R.string.scan_dialog_title);
        String dialogBtnSaveResult = getResources().getString(R.string.scan_dialog_btn_save_result);
        String dialogBtnCancel = getResources().getString(R.string.scan_dialog_btn_cancel);
        String dialogBtnRescan = getResources().getString(R.string.scan_dialog_btn_rescan);

        showDialog(this, dialogTitle, sb.toString(), dialogBtnSaveResult, dialogBtnCancel, dialogBtnRescan);
    }

    private void showDialog(final Activity act, CharSequence title,
                            CharSequence message, CharSequence buttonYes,
                            CharSequence buttonNo, CharSequence buttonRescan ) {
        AlertDialog.Builder scanDialog = new AlertDialog.Builder(act);
        scanDialog.setTitle(title);
        scanDialog.setMessage(message);
        scanDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                triggerScan();
                Intent resultInt = new Intent();
                resultInt.putExtra("ScanResultText", mResult.getText());
                resultInt.putExtra("ScanResultFormat", mResult.getBarcodeFormat());
                resultInt.putExtra("ScanResultTime", mResult.getTimestamp());
                resultInt.putExtra("ScanResultImg", mResult.getBitmap());
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
                mResult = null;
                resumeScanning();
                dialogInterface.dismiss();
            }
        });
        scanDialog.show();
    }
}
