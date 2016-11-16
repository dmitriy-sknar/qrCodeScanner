package com.ioLab.qrCodeScanner.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.ioLab.qrCodeScanner.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Utils extends Activity{

    private static final String LOG_TAG = "ioLogs";
    private static final String FILENAME = "barcode";
    private static final String DIR_SD = "BarCodes";
    private static final String FILENAME_SD = "barcode_SD";

    public static File getSDPath() {
        String sdState = android.os.Environment.getExternalStorageState(); //Получаем состояние SD карты (подключена она или нет) - возвращается true и false соответственно
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) // если true
        {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            return sdDir;

//            String folder = android.os.Environment.getExternalStorageDirectory().toString();
//            return folder;
        }
        return null;
    }

    public static void createDir(String folder) {
        File f1 = new File(folder); //Создаем файловую переменную
        if (!f1.exists()) { //Если папка не существует
            f1.mkdirs();  //создаем её
        }
    }

    public static boolean copy(String from, String to) {
        try {
            File fFrom = new File(from);
            if (fFrom.isDirectory()) { // Если директория, копируем все ее содержимое
                createDir(to);
                String[] FilesList = fFrom.list();
                for (int i = 0; i <= FilesList.length; i++)
                    if (!copy(from + "/" + FilesList[i], to + "/" + FilesList[i]))
                        return false; // Если при копировании произошла ошибка
            } else if (fFrom.isFile()) { // Если файл просто копируем его
                File fTo = new File(to);
                InputStream in = new FileInputStream(fFrom); // Создаем потоки
                OutputStream out = new FileOutputStream(fTo);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close(); // Закрываем потоки
                out.close();
            }
        } catch (IOException e) { // Обработка ошибок
            e.printStackTrace();
        }
        return true; // При удачной операции возвращаем true
    }

    public static void delete(String path) {
        File file = new File(path); //Создаем файловую переменную
        if (file.exists()) { //Если файл или директория существует
            String deleteCmd = "rm -r " + path; //Создаем текстовую командную строку
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd); //Выполняем системные команды
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean move(String from,String to) {
        try {
            File fFrom = new File(from);
            if (fFrom.isDirectory()) { // Если директория, копируем все ее содержимое
                createDir(to);
                String[] FilesList = fFrom.list();
                for (int i = 0; i <= FilesList.length; i++)
                    if (!copy(from + "/" + FilesList[i], to + "/" + FilesList[i]))
                        return false; // Если при копировании произошла ошибка
            } else if (fFrom.isFile()) { // Если файл просто копируем его
                File fTo = new File(to);
                InputStream in = new FileInputStream(fFrom); // Создаем потоки
                OutputStream out = new FileOutputStream(fTo);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close(); // Закрываем потоки
                out.close();
            }
        } catch (IOException e) { // Обработка ошибок
        }
        String deleteCmd = "rm -r " + from; //Создаем текстовую командную строку в которой удаляем начальный файл
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(deleteCmd); //Выполняем удаление с помощью команд
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true; // При удачной операции возвращаем true
    }

    public void writeFile() {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_PRIVATE)));
            // пишем данные
            bw.write("Содержимое файла");
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile() {
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.d(LOG_TAG, str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String writeFileSD(Bitmap bitmap, String format, String id) {
        // check the state of SD card
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-card is not ready: " + Environment.getExternalStorageState());
            return "error";
        }
        // get path to SD
        File sdPath = Environment.getExternalStorageDirectory();
        // add my dir to the path
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // make dir
        if(!sdPath.exists()) {
            sdPath.mkdirs();
        }
        // make File object, that contains path to file
        File file = new File(sdPath, id + "." + format);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            if (format.equals("png")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            }
            else if(format.equals("jpeg")){
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            }
            fOut.flush();
            fOut.close();
            Log.d(LOG_TAG, "File was written to SD: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static void readFileSD() {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.d(LOG_TAG, str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void shareCode(Activity mActivity, String name, String type, String date){
        String shareText =
                mActivity.getApplicationContext().getResources().getString(R.string.share_text_start) + "\n"
                        + mActivity.getApplicationContext().getResources().getString(R.string.share_text_codename)
                        + name + "\n"
                        + mActivity.getApplicationContext().getResources().getString(R.string.share_text_codetype)
                        + type + "\n"
                        + mActivity.getApplicationContext().getResources().getString(R.string.share_text_scandate)
                        + date + "\n" + "\n"
                        + mActivity.getApplicationContext().getResources().getString(R.string.share_text_end);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        mActivity.startActivity(shareIntent);
    }
}
