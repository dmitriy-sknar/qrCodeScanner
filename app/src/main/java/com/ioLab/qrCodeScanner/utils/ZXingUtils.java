package com.ioLab.qrCodeScanner.utils;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

public class ZXingUtils {
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    public static Bitmap encodeAsBitmap(String code, BarcodeFormat format, int img_width, int img_height) throws WriterException {
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

    public static BarcodeFormat getCodeType(String format){
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
}
