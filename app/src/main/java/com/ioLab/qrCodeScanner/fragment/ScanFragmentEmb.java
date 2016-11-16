package com.ioLab.qrCodeScanner.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.ioLab.qrCodeScanner.CodeDetails;
import com.ioLab.qrCodeScanner.R;
import com.ioLab.qrCodeScanner.ScannerActivityEmb;
import com.ioLab.qrCodeScanner.utils.History;
import com.ioLab.qrCodeScanner.utils.HistoryChangeEvent;
import com.ioLab.qrCodeScanner.utils.InitiateScanEvent;
import com.ioLab.qrCodeScanner.utils.MyQRCode;
import com.ioLab.qrCodeScanner.utils.Utils;
import com.ioLab.qrCodeScanner.utils.ZXingUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_CODE_TYPE;
import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_COMMENTS;
import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_DATE;
import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_ID;
import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_NAME;

public class ScanFragmentEmb extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private static final String TAG = "SCAN_PAGE_EMB";
    private int mPage;

    private ImageView image;
    private TextView codeText;
    private MyQRCode myQRCode;

    public static ScanFragmentEmb newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ScanFragmentEmb fragment = new ScanFragmentEmb();
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
        View view = inflater.inflate(R.layout.fr_scanning_page_emb, container, false);

        image = (ImageView) view.findViewById(R.id.scannedImage);
        codeText = (TextView) view.findViewById(R.id.barcode_text_result);

        //this is not right to set onClick to FAB here, but as of now it is temporal
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateScan();
            }
        });

        showLastCodeData();

        return view;
    }

    private void initiateScan(){
        IntentIntegrator.forSupportFragment(this)
                .setCaptureActivity(ScannerActivityEmb.class)
                .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                .setPrompt(getString(R.string.promt_to_scan))
                .setOrientationLocked(false) //when app view be able to rotate - check this to work
                .setBeepEnabled(true)
                .setBarcodeImageEnabled(true)
                .initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String name = data.getExtras().get("ScanResultText").toString();
            BarcodeFormat barcodeType = (BarcodeFormat) data.getExtras().get("ScanResultFormat");
            Bitmap bitmap = (Bitmap) data.getExtras().get("ScanResultImg");

            codeText.setText(name);
            image.setImageBitmap(bitmap);

            MyQRCode myQRCode = new MyQRCode(getContext());
            myQRCode.setName(name);
            myQRCode.setCodeType(barcodeType.toString());
            myQRCode.setComments("");
            myQRCode.setDateOfScanning(Calendar.getInstance().getTime());

            //write code to DB and get ID
            History history = new History(getContext());
            long id = history.insertCodeToDB(myQRCode);

            //save image to file and get path
            String absolutePath = Utils.writeFileSD(bitmap, "jpeg", String.valueOf(id));

            //update code in DB with a path
            MyQRCode qr = history.getCodeById(id);
            qr.setPath(absolutePath);
            history.updateCodeInDB(qr);

            EventBus.getDefault().postSticky(new HistoryChangeEvent());

            history.close();
        }
        else {
            Snackbar.make(getActivity().findViewById(R.id.fr_scanning_emb_layout),
                    getResources().getString(R.string.toast_scan_cancel),
                    Snackbar.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showLastCodeData(){
        String name;
        BarcodeFormat barcodeType;
        String path;
        History history = new History(getContext());
        List<MyQRCode> arrayList = history.getAllCodesFromDB();

        if(arrayList != null) {
            myQRCode = arrayList.get(arrayList.size() - 1);
            name = myQRCode.getName();
            //Todo need help to optimize casting without selecting by method
            barcodeType = ZXingUtils.getCodeType(myQRCode.getCodeType());
            path = myQRCode.getPath();

            File imgFile = new File(path);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image.setImageBitmap(myBitmap);
            }
        }
        else{
            //if DB is empty generate dummy code
            myQRCode = null;
            name = getResources().getString(R.string.history_is_empty);
            barcodeType = BarcodeFormat.QR_CODE;

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            float barcodeSize = width < height ? width : height;
            int barcodeSizeInt = (int) Math.round(barcodeSize * 0.7);
            try {
                Bitmap bitmap = ZXingUtils.encodeAsBitmap(name, barcodeType, barcodeSizeInt, barcodeSizeInt);
                image.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        codeText.setText(name);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myQRCode == null){
                    showDialog(getActivity());
                    return;
                }

                Intent intent = new Intent(getActivity(), CodeDetails.class);
                intent.putExtra(KEY_NAME, myQRCode.getName());
                intent.putExtra(KEY_CODE_TYPE, myQRCode.getCodeType());
                intent.putExtra(KEY_COMMENTS, myQRCode.getComments());
                intent.putExtra(KEY_ID, myQRCode.getId());
                Date dateOfScanning = myQRCode.getDateOfScanning();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm", getContext().getResources().getConfiguration().locale);
                String date = dateFormat.format(dateOfScanning);
                intent.putExtra(KEY_DATE, date);

                startActivity(intent);
            }
        });
    }

    //ToDo insert in onActivityCreated code to restore barcode picture and text after rotation
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    private void showDialog(final Activity act) {
        final AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
//        downloadDialog.setTitle(R.string.splash_screen_alert_dialog);
        downloadDialog.setMessage(R.string.history_is_empty);
        downloadDialog.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        downloadDialog.show();
    }

    // EventBus block. To handle history changes.
    // Last scanned code can be changed, so UI must be refreshed
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(HistoryChangeEvent event) {
        showLastCodeData();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(InitiateScanEvent event) {
        initiateScan();
    }
 }