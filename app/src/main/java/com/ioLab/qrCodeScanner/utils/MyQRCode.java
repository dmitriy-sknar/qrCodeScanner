package com.ioLab.qrCodeScanner.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.Locale;

public class MyQRCode {

    private String id;
    private String name;
    private Date dateOfScanning;
    private String codeType;
    @Nullable
    private String comments;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;

    private Locale currentLocale;

    //todo refactor this context -> Locale
    public MyQRCode(Context context) {
        this.currentLocale = context.getResources().getConfiguration().locale;
    }

    public MyQRCode(Context context, @Nullable String comments) {
        this.currentLocale = context.getResources().getConfiguration().locale;
        this.comments = comments;
    }


    public MyQRCode(Context context, String name, Date dateOfScanning, String codeType,
                    String comments, String id, String path) {
        this.currentLocale = context.getResources().getConfiguration().locale;
        this.name = name;
        this.dateOfScanning = dateOfScanning;
        this.codeType = codeType;
        this.comments = comments;
        this.id = id;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Nullable
    public String getComments() {
        return comments;
    }

    public void setComments(@Nullable String comments) {
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
