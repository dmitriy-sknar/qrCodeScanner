package com.ioLab.qrCodeScanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ioLab.qrCodeScanner.Utils.History;
import com.ioLab.qrCodeScanner.Utils.HistoryDataBinder;
import com.ioLab.qrCodeScanner.Utils.MyQRCode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.num.barcodescannerproject.R;

/**
 * Created by disknar on 01.08.2016.
 */

public class HistoryFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";
    private static final String KEY_NAME = "name";
    private static final String KEY_CODE_TYPE = "format";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_DATE = "date";
    private final String LOG_TAG = "ioLabLog";
    private int mPage;
//    private ArrayAdapter<String> mHistoryAdapter;
    private HistoryDataBinder bindingData;
    private List<MyQRCode> myQRCodes;
    private List<HashMap<String, String>> codesHistory;
    private History history;
    private ListView listView;

    public static HistoryFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        setHasOptionsMenu(true);
        history = new History(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.history_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_clear_history_db) {

            history.clearDB();
            refreshHistoryList(); //refresh listView

            String historyCleared = getResources().getString(R.string.history_cleared);
            Toast.makeText(getActivity(), historyCleared, Toast.LENGTH_LONG).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myQRCodes = history.getAllCodesFromDB();

        List<HashMap<String, String>> codeDataCollection = new ArrayList<>();
        HashMap<String,String> map;

        if (myQRCodes != null) {
            for (int i = 0; i < myQRCodes.size(); i++) {

                MyQRCode codeItem = myQRCodes.get(i);
                map = makeDataSet(codeItem);
                codeDataCollection.add(map);
            }
        }
        else{
            map = new HashMap<String, String>();
            map.put(KEY_NAME, getResources().getString(R.string.history_is_empty));
            map.put(KEY_CODE_TYPE, "");
            map.put(KEY_COMMENTS, "");
            map.put(KEY_DATE, "");

            codeDataCollection.add(map);
        }

        bindingData = new HistoryDataBinder(getActivity(), codeDataCollection);

        Log.i("BEFORE", "<<------------- Before SetAdapter-------------->>");

        View rootView = inflater.inflate(R.layout.fr_history_page, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        listView = (ListView) rootView.findViewById(R.id.listview_history);
//        listView.setAdapter(mHistoryAdapter); //old adapter
        listView.setAdapter(bindingData);

        Log.i("AFTER", "<<------------- After SetAdapter-------------->>");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(myQRCodes == null){
                    showDialog(getActivity());
                    return;
                }

                MyQRCode myQRCode = myQRCodes.get(position);

                Intent intent = new Intent(getActivity(), CodeDetails.class);
                intent.putExtra("name", myQRCode.getName());
                intent.putExtra("format", myQRCode.getCodeType());
                intent.putExtra("comments", myQRCode.getComments());
                Date dateOfScanning = myQRCode.getDateOfScanning();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm", getContext().getResources().getConfiguration().locale);
                String date = dateFormat.format(dateOfScanning);
                intent.putExtra("date", date);

                startActivity(intent);
            }
        });

         return rootView;
    }

    private HashMap<String, String> makeDataSet(MyQRCode myQRCode){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY_NAME, myQRCode.getName());
        map.put(KEY_CODE_TYPE, myQRCode.getCodeType());
        map.put(KEY_COMMENTS, myQRCode.getComments());

        Date dateOfScanning = myQRCode.getDateOfScanning();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm", getContext().getResources().getConfiguration().locale);
        String date = dateFormat.format(dateOfScanning);
        map.put(KEY_DATE, date);
        return map;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "HistoryFragment onResume");
    }

    public void refreshHistoryList(){
        if(history == null){
            history = new History(getContext());
        }
        myQRCodes = history.getAllCodesFromDB();
        codesHistory = new ArrayList<>();

        if (myQRCodes != null) {
            for (MyQRCode mc : myQRCodes) {
                codesHistory.add(makeDataSet(mc));
            }
        }
        else {
            HashMap<String, String> hm = new HashMap<>();
            hm.put(KEY_NAME, getResources().getString(R.string.history_is_empty));
            hm.put(KEY_CODE_TYPE, "");
            hm.put(KEY_COMMENTS, "");
            hm.put(KEY_DATE, "");

            codesHistory.add(hm);
        }

        bindingData.clear();
        bindingData.addAll(codesHistory);

        bindingData.notifyDataSetChanged();
        listView.invalidateViews();
        listView.refreshDrawableState();
    }

    private void showDialog(final Activity act) {
        final AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(R.string.splash_screen_alert_dialog);
        downloadDialog.setMessage(R.string.history_is_empty);
        downloadDialog.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        downloadDialog.show();
    }

    public interface OnHistoryChangedListener {
        void onHistoryChange();
    }
}