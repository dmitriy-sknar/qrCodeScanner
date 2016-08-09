package com.ioLab.qrCodeScanner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.num.barcodescannerproject.R;

/**
 * Created by disknar on 01.08.2016.
 */
// In this case, the fragment displays simple text based on the page
public class HistoryFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private ArrayAdapter<String> mHistoryAdapter;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "Scan 1",
                "Scan 2",
                "Scan 3",
                "Scan 4",
                "Scan 5",
                "Scan 6",
                "Scan 7",
                "Scan 8",
        };
        List<String> dummyHistory = new ArrayList<String>(Arrays.asList(data));

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mHistoryAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.history_list_item, // The name of the layout ID.
                        R.id.list_item_history_textview, // The ID of the textview to populate.
                        dummyHistory);

        View rootView = inflater.inflate(R.layout.fr_history_page, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_history);
        listView.setAdapter(mHistoryAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                String forecast = mHistoryAdapter.getItem(position);
//                Intent intent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, forecast);
//                startActivity(intent);
//            }
//        });

        return rootView;
    }
}