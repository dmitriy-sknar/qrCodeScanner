package com.ioLab.qrCodeScanner;

import android.app.Activity;
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

import java.util.Calendar;

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


    //ToDo insert in onActivityCreated code to restore barcode picture and text after rotation
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }


 }