package com.ioLab.qrCodeScanner.Utils;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * Created by disknar on 12.08.2016.
 */
public class MyQRCode implements Serializable{

    private String name;
    private Date dateOfScanning;
    private String codeType;
    @Nullable
    private String comments;

    private Locale currentLocale;

    //todo refactor this context -> Locale
    public MyQRCode(Context context) {
        this.currentLocale = context.getResources().getConfiguration().locale;
    }

    public MyQRCode(Context context, @Nullable String comments) {
        this.currentLocale = context.getResources().getConfiguration().locale;
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfScanning() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm", currentLocale);
//        return dateFormat.format(dateOfScanning);
        return dateOfScanning;
    }

    public void setDateOfScanning(Date dateOfScanning) {
        this.dateOfScanning = dateOfScanning;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

}
