package com.ioLab.qrCodeScanner;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ioLab.qrCodeScanner.Utils.History;
import com.ioLab.qrCodeScanner.Utils.MyQRCode;

import java.util.ArrayList;
import java.util.List;

import app.num.barcodescannerproject.R;

/**
 * Created by disknar on 01.08.2016.
 */

public class HistoryFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";
    private final String LOG_TAG = "ioLabLog";
    private int mPage;
    private ArrayAdapter<String> mHistoryAdapter;
    private List<MyQRCode> myQRCodes;
    private List<String> codesHistory;
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
        codesHistory = new ArrayList<>();

        if (myQRCodes != null) {
            for (MyQRCode mc : myQRCodes) {
                codesHistory.add(mc.getName());
            }
        }
        else {
            codesHistory.add(getResources().getString(R.string.history_is_empty));
        }

        mHistoryAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.fr_history_page_list_item, // The name of the layout ID.
                R.id.list_item_history_textview, // The ID of the textview to populate.
                codesHistory);

        View rootView = inflater.inflate(R.layout.fr_history_page, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        listView = (ListView) rootView.findViewById(R.id.listview_history);
        listView.setAdapter(mHistoryAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyQRCode myQRCode = myQRCodes.get(position);
                Intent intent = new Intent(getActivity(), CodeDetails.class);
                intent.putExtra("name", myQRCode.getName());
                intent.putExtra("format", myQRCode.getCodeType());
                intent.putExtra("comments", myQRCode.getComments());
                intent.putExtra("date", myQRCode.getDateOfScanning().getTime()/1000);

                startActivity(intent);
            }
        });

        return rootView;
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
                codesHistory.add(mc.getName());
            }
        }
        else codesHistory.add(getResources().getString(R.string.history_is_empty));

        mHistoryAdapter.clear();
        mHistoryAdapter.addAll(codesHistory);
        mHistoryAdapter.notifyDataSetChanged();
        listView.invalidateViews();
        listView.refreshDrawableState();
    }

    public interface OnHistoryChangedListener {
        void onHistoryChange();
    }
}