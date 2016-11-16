package com.ioLab.qrCodeScanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.ioLab.qrCodeScanner.utils.History;
import com.ioLab.qrCodeScanner.utils.HistoryChangeEvent;
import com.ioLab.qrCodeScanner.utils.MyQRCode;
import com.ioLab.qrCodeScanner.utils.Utils;
import com.ioLab.qrCodeScanner.utils.ZXingUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_CODE_TYPE;
import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_DATE;
import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_ID;
import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_NAME;
import static com.ioLab.qrCodeScanner.utils.ZXingUtils.getBarCodeSizeForVerticalUI;

public class CodeDetails extends AppCompatActivity{

    private Activity mActivity;
    private Intent intent;
    private Bitmap mBitmap;
    private MyQRCode myQRCode;

    @BindView(R.id.backdrop)
    AppCompatImageView backdrop;

    @BindView(R.id.share_text_codename)
    TextView share_text_codename;

    @BindView(R.id.code_type_data)
    TextView code_type_data;

    @BindView(R.id.code_date_data)
    TextView code_date_data;

    @BindView(R.id.generated_code_image)
    AppCompatImageView generated_code_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_details);
        ButterKnife.bind(this);

        mActivity = this;

        initializToolbar();

        intent = getIntent();
        initializFAB(intent);

        History history = new History(this);
        String id = intent.getStringExtra(KEY_ID);
        myQRCode = history.getCodeById(Long.parseLong(id));
        String path = myQRCode.getPath();
        File imgFile = new File(path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            backdrop.setImageBitmap(myBitmap);
        }

        share_text_codename.setText(intent.getStringExtra(KEY_NAME));
        code_type_data.setText(intent.getStringExtra(KEY_CODE_TYPE));
        code_date_data.setText(intent.getStringExtra(KEY_DATE));

        mBitmap = null;
        int barcodeSize = getBarCodeSizeForVerticalUI(this);
        try {
            mBitmap = ZXingUtils.encodeAsBitmap(intent.getStringExtra(KEY_NAME),
                    ZXingUtils.getCodeType(intent.getStringExtra(KEY_CODE_TYPE)),
                    barcodeSize,
                    barcodeSize);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        generated_code_image.setImageBitmap(mBitmap);
    }

    private void initializToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(""); //is title needed?
    }

    private void initializFAB(final Intent intent) {
        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.fab_details_activity);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.shareCode(mActivity,
                        intent.getStringExtra(KEY_NAME),
                        intent.getStringExtra(KEY_CODE_TYPE),
                        intent.getStringExtra(KEY_DATE));
            }
        });
    }

    @OnClick(R.id.fab_details_activity)
    public void shareCode(Button button) {
        Utils.shareCode(mActivity,
                intent.getStringExtra(KEY_NAME),
                intent.getStringExtra(KEY_CODE_TYPE),
                intent.getStringExtra(KEY_DATE));
    }

    @OnClick(R.id.save_png)
    public void savePNG(Button button) {
        //save image to file and show path
        Bitmap bitmap = mBitmap;
        //regenerate bitmap with higher resolution to save
        try {
            bitmap = ZXingUtils.encodeAsBitmap(intent.getStringExtra(KEY_NAME),
                    ZXingUtils.getCodeType(intent.getStringExtra(KEY_CODE_TYPE)),
                    1024,
                    1024);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        StringBuilder id = new StringBuilder(intent.getStringExtra(KEY_DATE));
        String fileName = id.substring(0,10) + "_" + (int) Math.round(100000*Math.random());
        String absolutePath = Utils.writeFileSD(bitmap, "png", fileName);
        Snackbar.make(mActivity.findViewById(R.id.main_content),
                getResources().getString(R.string.saved_as) + " " + absolutePath,
                Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.save_jpg)
    public void saveJPG(Button button) {
        //save image to file and show path
        Bitmap bitmap = mBitmap;
        //regenerate bitmap with higher resolution to save
        try {
            bitmap = ZXingUtils.encodeAsBitmap(intent.getStringExtra(KEY_NAME),
                    ZXingUtils.getCodeType(intent.getStringExtra(KEY_CODE_TYPE)),
                    1024,
                    1024);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        StringBuilder id = new StringBuilder(intent.getStringExtra(KEY_DATE));
        String fileName = id.substring(0,10) + "_" + (int) Math.round(100000*Math.random());
        String absolutePath = Utils.writeFileSD(bitmap, "jpeg", fileName);
        Snackbar.make(mActivity.findViewById(R.id.main_content),
                getResources().getString(R.string.saved_as) + " " + absolutePath,
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_code:
                History history = new History(this);
                history.deleteCodeFromDB(myQRCode.getId());
                history.close();

                Utils.delete(myQRCode.getPath());

                EventBus.getDefault().postSticky(new HistoryChangeEvent());

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
}
