package com.ioLab.qrCodeScanner.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ioLab.qrCodeScanner.CodeDetails;
import com.ioLab.qrCodeScanner.R;
import com.ioLab.qrCodeScanner.utils.History;
import com.ioLab.qrCodeScanner.utils.HistoryChangeEvent;
import com.ioLab.qrCodeScanner.utils.HistoryDataBinder;
import com.ioLab.qrCodeScanner.utils.MyQRCode;
import com.ioLab.qrCodeScanner.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_CODE_TYPE;
import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_COMMENTS;
import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_DATE;
import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_ID;
import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_NAME;
import static com.ioLab.qrCodeScanner.utils.MyQRCode.KEY_PATH;

public class HistoryFragment extends Fragment implements AbsListView.MultiChoiceModeListener {
    private static final String ARG_PAGE = "ARG_PAGE";
    private final String LOG_TAG = "ioLabLog";
    private int mPage;
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

            List<MyQRCode> list =  history.getAllCodesFromDB();
            for(MyQRCode code : list){
                Utils.delete(code.getPath());
            }
            history.clearDB();
            refreshHistoryList(); //refresh listView
            EventBus.getDefault().postSticky(new HistoryChangeEvent());

            String historyCleared = getResources().getString(R.string.history_cleared);
            Snackbar.make(this.getView(), historyCleared, Snackbar.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu,
                                    final View v, final ContextMenu.ContextMenuInfo menuInfo) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myQRCodes = history.getAllCodesFromDB();

        List<HashMap<String, String>> codeDataCollection = new ArrayList<>();
        HashMap<String,String> map;

        if (myQRCodes != null) {
            Collections.reverse(myQRCodes);
            for (int i = 0; i < myQRCodes.size(); i++) {

                MyQRCode codeItem = myQRCodes.get(i);
                map = makeDataSet(codeItem);
                codeDataCollection.add(map);
            }
        }
        else{
            map = new HashMap<String, String>();
            map.put(KEY_NAME, getResources().getString(R.string.history_is_empty));
            map.put(KEY_ID, "9999999"); //dummy id to check if history is empty in HistoryDataBinder
            map.put(KEY_CODE_TYPE, "");
            map.put(KEY_COMMENTS, "");
            map.put(KEY_DATE, "");
            map.put(KEY_PATH, "");

            codeDataCollection.add(map);
        }

        bindingData = new HistoryDataBinder(getActivity(), codeDataCollection);

        Log.i("BEFORE", "<<------------- Before SetAdapter-------------->>");
        View rootView = inflater.inflate(R.layout.fr_history_page, container, false);
        listView = (ListView) rootView.findViewById(R.id.listview_history);
        listView.setAdapter(bindingData);
        Log.i("AFTER", "<<------------- After SetAdapter-------------->>");

        registerForContextMenu(listView);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this);

        initializClickListeners();

        return rootView;
    }

    private void initializClickListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(myQRCodes == null){
                    showDialog(getActivity());
                    return;
                }

                HashMap<String, String> hm = (HashMap<String, String>) listView.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), CodeDetails.class);
                intent.putExtra("name", hm.get(KEY_NAME));
                intent.putExtra("format", hm.get(KEY_CODE_TYPE));
                intent.putExtra("comments", hm.get(KEY_COMMENTS));
                intent.putExtra("date", hm.get(KEY_DATE));
                intent.putExtra("id", hm.get(KEY_ID));
                intent.putExtra("path", hm.get(KEY_PATH));

                startActivity(intent);
            }
        });
    }

    private HashMap<String, String> makeDataSet(MyQRCode myQRCode){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY_NAME, myQRCode.getName());
        map.put(KEY_CODE_TYPE, myQRCode.getCodeType());
        map.put(KEY_COMMENTS, myQRCode.getComments());

        Date dateOfScanning = myQRCode.getDateOfScanning();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm",
                getContext().getResources().getConfiguration().locale);
        String date = dateFormat.format(dateOfScanning);
        map.put(KEY_DATE, date);
        map.put(KEY_ID, myQRCode.getId());
        map.put(KEY_PATH, myQRCode.getPath());
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
        Collections.reverse(codesHistory);
        bindingData.addAll(codesHistory);

        bindingData.notifyDataSetChanged();
        listView.invalidateViews();
        listView.refreshDrawableState();
    }

    private void showDialog(final Activity act) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(act);
//        dialog.setTitle(R.string.splash_screen_alert_dialog);
        dialog.setMessage(R.string.history_is_empty);
        dialog.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    // Called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.hist_fr_action_mode_menu, menu);
        return true;
    }

    // Called each time the action mode is shown. Always called after onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        String title = getString(R.string.select_action) + " " + "1";
        mode.setTitle(title); //make text "You selected an item"
        return true; // Return false if nothing is done
    }

    // Called when the user selects a contextual menu item
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        SparseBooleanArray checkedItems;

        switch (item.getItemId()) {
            case R.id.am_menu_delete:
                checkedItems = listView.getCheckedItemPositions();
                int deletedItemsCount = 0;
                for(int i=0; i < checkedItems.size(); i++){
                    HashMap<String, String> hm =
                            (HashMap<String, String>) listView
                                    .getAdapter()
                                    .getItem(checkedItems.keyAt(i));
                    String path = history.getCodeById(Long.parseLong(hm.get(KEY_ID))).getPath();
                    history.deleteCodeFromDB(hm.get(KEY_ID));
                    //delete scanned image
                    Utils.delete(path);

                    deletedItemsCount++;
                }
                refreshHistoryList();
                EventBus.getDefault().postSticky(new HistoryChangeEvent());

                String message = getString(R.string.deleted_count) + " " + deletedItemsCount;

                mode.finish(); // Action picked, so close the CAB
                Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                        message,
                        Snackbar.LENGTH_LONG).show();
                return true;

            case R.id.am_menu_share:
                if(listView.getCheckedItemCount() > 1){
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage(R.string.select_one_item);
                    dialog.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                    return false;
                }

                checkedItems= listView.getCheckedItemPositions();
                int checkedItem = checkedItems.keyAt(0);
                HashMap<String, String> hm =
                        (HashMap<String, String>) listView.getAdapter().getItem(checkedItem);

                String id = hm.get(KEY_ID);
                if(id.equals("9999999")){
                    showDialog(getActivity());
                    return false;
                }

                Utils.shareCode(getActivity(),
                         hm.get(KEY_NAME),
                         hm.get(KEY_CODE_TYPE),
                         hm.get(KEY_DATE));

                mode.finish(); // Action picked, so close the CAB
                return true;

            default:
                return false;
        }
    }

    // Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        listView.clearChoices();
//        for (int i = 0; i < listView.getChildCount(); i++) {
//            listView.setItemChecked(i, false);
//            listView.getChildAt(i).setSelected(false);
//            listView.setSelection(i);
//            listView.setSelected(false);
//        }
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        String title = getString(R.string.select_action) + " " + listView.getCheckedItemCount();
        mode.setTitle(title); //make text "Selected"
    }

    //EventBus block. To handle history changes
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
        refreshHistoryList();
    }

}