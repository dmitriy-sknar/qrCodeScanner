package com.ioLab.qrCodeScanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

import app.num.barcodescannerproject.R;

/**
 * Created by disknar on 01.08.2016.
 */
// In this case, the fragment displays simple text based on the page
public class ScanFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
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
//        setBlankQrcode();
        autoFocus = (CompoundButton) view.findViewById(R.id.auto_focus_checkbox);
        useFlash = (CompoundButton) view.findViewById(R.id.use_flash_checkbox);

        Button btnScan = (Button) view.findViewById(R.id.btnScanning);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanInt = new Intent(getActivity(), Scanner.class);
                scanInt.putExtra(Scanner.AutoFocus, autoFocus.isChecked());
                scanInt.putExtra(Scanner.UseFlash, useFlash.isChecked());
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
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();

                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;
                float barcodeSize = width > height ? width : height;
                int barcodeSizeInt = Math.round(barcodeSize);
                try {
                    Bitmap bitmap = encodeAsBitmap(result, barcodeType, barcodeSizeInt, barcodeSizeInt);
                    image.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
//                byte[] arr = (byte[]) data.getExtras().get("ScanResultRawBytes");
//                Bitmap bmp = BitmapFactory.decodeByteArray(arr,0,arr.length);
//                Bitmap bitmap = (Bitmap) data.getExtras().get("ScanResultBitmap");
//                image.setImageBitmap(bitmap);

            } else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getContext(), "Scanning canceled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Bitmap encodeAsBitmap(String code, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        if (code == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(code);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(code, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    //ToDo finish blanck picture creation
    private Bitmap setBlankQrcode(){
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(80, 100);
        tv.setLayoutParams(layoutParams);
        tv.setText("Not scanned yet");
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundColor(Color.TRANSPARENT);

        Bitmap testB;

        testB = Bitmap.createBitmap(80, 100, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(testB);
        tv.layout(0, 0, 80, 100);
        tv.draw(c);

        ImageView iv = image;
        iv.setLayoutParams(layoutParams);
        iv.setBackgroundColor(Color.GRAY);
        iv.setImageBitmap(testB);
        iv.setMaxHeight(80);
        iv.setMaxWidth(80);

        return testB;
    }

    //ToDo insert in onActivityCreated code to restore barcode picture and text after rotation
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }


 }