package com.ioLab.qrCodeScanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.ioLab.qrCodeScanner.Utils.History;
import com.ioLab.qrCodeScanner.Utils.MyQRCode;
import com.ioLab.qrCodeScanner.Utils.ZXingUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.num.barcodescannerproject.R;

/**
 * Created by disknar on 01.08.2016.
 */
public class ScanFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    private ImageView image;
    private TextView codeText;
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private MyQRCode myQRCode;

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    public static ScanFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ScanFragment fragment = new ScanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_scanning_page, container, false);

        image = (ImageView) view.findViewById(R.id.scannedImage);
        codeText = (TextView) view.findViewById(R.id.barcode_text_result);

        //Todo make image from history. If history is empty, show some text: "No scanned codes yet"
//        ZXingUtils.setBlankQrcode(getContext());

        autoFocus = (CompoundButton) view.findViewById(R.id.auto_focus_checkbox);
        useFlash = (CompoundButton) view.findViewById(R.id.use_flash_checkbox);

        Button btnScan = (Button) view.findViewById(R.id.btnScanning);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanInt = new Intent(getActivity(), ScanFragmentScanner.class);
                scanInt.putExtra(ScanFragmentScanner.AutoFocus, autoFocus.isChecked());
                scanInt.putExtra(ScanFragmentScanner.UseFlash, useFlash.isChecked());
                startActivityForResult(scanInt, 1);
            }
        });
        setPageImage();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myQRCode == null){
                    showDialog(getActivity());
                    return;
                }

                Intent intent = new Intent(getActivity(), CodeDetails.class);
                intent.putExtra("name", myQRCode.getName());
                intent.putExtra("format", myQRCode.getCodeType());
                intent.putExtra("comments", myQRCode.getComments());
                Date dateOfScanning = myQRCode.getDateOfScanning();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm", getContext().getResources().getConfiguration().locale);
                String date = dateFormat.format(dateOfScanning);
                intent.putExtra("date", date);

                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                String result = data.getExtras().get("ScanResultText").toString();
                codeText.setText(result);
                BarcodeFormat barcodeType = (BarcodeFormat) data.getExtras().get("ScanResultFormat");
//                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();

                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;
                float barcodeSize = width > height ? width : height;
                int barcodeSizeInt = Math.round(barcodeSize);
                try {
                    Bitmap bitmap = ZXingUtils.encodeAsBitmap(result, barcodeType, barcodeSizeInt, barcodeSizeInt);
                    image.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

//                Bitmap bitmap = (Bitmap) data.getExtras().get("ScanResultBitmap");
//                image.setImageBitmap(bitmap);

                MyQRCode myQRCode = new MyQRCode(getContext());
                myQRCode.setName(result);
                myQRCode.setCodeType(barcodeType.toString());
                myQRCode.setComments("");
                myQRCode.setDateOfScanning(Calendar.getInstance().getTime());

                History history = new History(getContext());
                history.insertCodeToDB(myQRCode);

                //get Main activity as a listener (interface) to notify HistoryFragment
                HistoryFragment.OnHistoryChangedListener listener = (HistoryFragment.OnHistoryChangedListener) getActivity();
                listener.onHistoryChange();

            } else if (resultCode == Activity.RESULT_CANCELED){
                String scanCancel = getResources().getString(R.string.toast_scan_cancel);
                Toast.makeText(getContext(), scanCancel, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setPageImage(){
        String name;
        BarcodeFormat barcodeType;
        History history = new History(getContext());
        List<MyQRCode> arrayList = history.getAllCodesFromDB();

        if(arrayList != null) {
            myQRCode = arrayList.get(arrayList.size() - 1);
            name = myQRCode.getName();
            //Todo need help to optimize casting without selecting by method
            barcodeType = getCodeType(myQRCode.getCodeType());
        }
        else{
            myQRCode = null;
            name = getResources().getString(R.string.history_is_empty);
            barcodeType = BarcodeFormat.QR_CODE;
        }

        codeText.setText(name);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        float barcodeSize = width > height ? width : height;
        int barcodeSizeInt = Math.round(barcodeSize);
        try {
            Bitmap bitmap = ZXingUtils.encodeAsBitmap(name, barcodeType, barcodeSizeInt, barcodeSizeInt);
            image.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    //ToDo insert in onActivityCreated code to restore barcode picture and text after rotation
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    private BarcodeFormat getCodeType(String format){
        if (format.equals(BarcodeFormat.AZTEC.toString())){
            return BarcodeFormat.AZTEC;
        }
        else if (format.equals(BarcodeFormat.CODABAR.toString())){
            return BarcodeFormat.CODABAR;
        }
        else if (format.equals(BarcodeFormat.CODE_39.toString())){
            return BarcodeFormat.CODE_39;
        }
        else if (format.equals(BarcodeFormat.CODE_93.toString())){
            return BarcodeFormat.CODE_93;
        }
        else if (format.equals(BarcodeFormat.CODE_128.toString())){
            return BarcodeFormat.CODE_128;
        }
        else if (format.equals(BarcodeFormat.DATA_MATRIX.toString())){
            return BarcodeFormat.DATA_MATRIX;
        }
        else if (format.equals(BarcodeFormat.EAN_8.toString())){
            return BarcodeFormat.EAN_8;
        }
        else if (format.equals(BarcodeFormat.EAN_13.toString())){
            return BarcodeFormat.EAN_13;
        }
        else if (format.equals(BarcodeFormat.ITF.toString())){
            return BarcodeFormat.ITF;
        }
        else if (format.equals(BarcodeFormat.MAXICODE.toString())){
            return BarcodeFormat.MAXICODE;
        }
        else if (format.equals(BarcodeFormat.PDF_417.toString())){
            return BarcodeFormat.PDF_417;
        }
        else if (format.equals(BarcodeFormat.QR_CODE.toString())){
            return BarcodeFormat.QR_CODE;
        }
        else if (format.equals(BarcodeFormat.RSS_14.toString())){
            return BarcodeFormat.RSS_14;
        }
        else if (format.equals(BarcodeFormat.RSS_EXPANDED.toString())){
            return BarcodeFormat.RSS_EXPANDED;
        }
        else if (format.equals(BarcodeFormat.UPC_A.toString())){
            return BarcodeFormat.UPC_A;
        }
        else if (format.equals(BarcodeFormat.UPC_E.toString())){
            return BarcodeFormat.UPC_E;
        }
        else if(format.equals(BarcodeFormat.UPC_EAN_EXTENSION.toString())) {
            return BarcodeFormat.UPC_EAN_EXTENSION;
        }
        else{
            return null;
        }
    }

    private void showDialog(final Activity act) {
        final AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(R.string.splash_screen_alert_dialog);
        downloadDialog.setMessage(R.string.history_is_empty);
        downloadDialog.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        downloadDialog.show();
    }
 }