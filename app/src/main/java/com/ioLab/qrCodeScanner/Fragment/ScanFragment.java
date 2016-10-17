package com.ioLab.qrCodeScanner.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.ioLab.qrCodeScanner.CodeDetails;
import com.ioLab.qrCodeScanner.R;
import com.ioLab.qrCodeScanner.ScannerActivity;
import com.ioLab.qrCodeScanner.utils.History;
import com.ioLab.qrCodeScanner.utils.MyQRCode;
import com.ioLab.qrCodeScanner.utils.ZXingUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScanFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    private ImageView image;
    private TextView codeText;
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private MyQRCode myQRCode;

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
        autoFocus = (CompoundButton) view.findViewById(R.id.auto_focus_checkbox);
        useFlash = (CompoundButton) view.findViewById(R.id.use_flash_checkbox);

        Button btnScan = (Button) view.findViewById(R.id.btnScanning);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanInt = new Intent(getActivity(), ScannerActivity.class);
                scanInt.putExtra(ScannerActivity.AutoFocus, autoFocus.isChecked());
                scanInt.putExtra(ScannerActivity.UseFlash, useFlash.isChecked());
                startActivityForResult(scanInt, 1);
            }
        });

        setPageImage();

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
                Snackbar.make(this.getView(), scanCancel, Snackbar.LENGTH_LONG).show();
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
            barcodeType = ZXingUtils.getCodeType(myQRCode.getCodeType());
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
        finally {
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
        }
    }

    //ToDo insert in onActivityCreated code to restore barcode picture and text after rotation
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
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