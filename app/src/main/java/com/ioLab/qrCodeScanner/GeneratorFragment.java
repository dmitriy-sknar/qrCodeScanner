package com.ioLab.qrCodeScanner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import app.num.barcodescannerproject.R;

/**
 * Created by disknar on 01.08.2016.
 */
public class GeneratorFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    private ImageView image;
    private TextView codeText;

    public static GeneratorFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        GeneratorFragment fragment = new GeneratorFragment();
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


        Button btnScan = (Button) view.findViewById(R.id.btnScanning);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

//                String result = data.getExtras().get("ScanResultText").toString();
//                codeText.setText(result);
//                BarcodeFormat barcodeType = (BarcodeFormat) data.getExtras().get("ScanResultFormat");
////                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
//
//                Display display = getActivity().getWindowManager().getDefaultDisplay();
//                Point size = new Point();
//                display.getSize(size);
//                int width = size.x;
//                int height = size.y;
//                float barcodeSize = width > height ? width : height;
//                int barcodeSizeInt = Math.round(barcodeSize);
//                try {
//                    Bitmap bitmap = encodeAsBitmap(result, barcodeType, barcodeSizeInt, barcodeSizeInt);
//                    image.setImageBitmap(bitmap);
//                } catch (WriterException e) {
//                    e.printStackTrace();
//                }
//
//            } else if (resultCode == Activity.RESULT_CANCELED){
//                String scanCancel = getResources().getString(R.string.toast_scan_cancel);
//                Toast.makeText(getContext(), scanCancel, Toast.LENGTH_LONG).show();
//
 }