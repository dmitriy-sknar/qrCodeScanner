package com.ioLab.qrCodeScanner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioLab.qrCodeScanner.R;

public class GeneratorFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
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

 }