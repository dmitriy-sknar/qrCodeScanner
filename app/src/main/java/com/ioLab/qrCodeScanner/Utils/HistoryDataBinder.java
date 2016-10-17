package com.ioLab.qrCodeScanner.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioLab.qrCodeScanner.CodeDetails;
import com.ioLab.qrCodeScanner.R;

import java.util.HashMap;
import java.util.List;

public class HistoryDataBinder extends BaseAdapter {

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CODE_TYPE = "format";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_DATE = "date";

    LayoutInflater inflater;
    private List<HashMap<String, String>> codeDataCollection;
    ViewHolder holder;
    private Activity mActivity;
    private ActionMode mActionMode;
    private View view;
    private ShareActionProvider mShareActionProvider;
    private HashMap hm;

    public HistoryDataBinder(Activity act, List<HashMap<String, String>> list) {
        this.codeDataCollection = list;
        inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActivity = act;
    }

    public int getCount() {
        return codeDataCollection.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
//        final CardView mCardView;
        if(convertView == null){

            convertView = inflater.inflate(R.layout.fr_history_page_list_item_cards_colored, null);
            holder = new ViewHolder();

            holder.tvCodeName = (TextView)convertView.findViewById(R.id.tvCodeName);
            holder.tvCodeFormat = (TextView)convertView.findViewById(R.id.tvCodeFormat);
            holder.tvScanningDate = (TextView)convertView.findViewById(R.id.tvScanningDate);
            holder.list_image = (ImageView)convertView.findViewById(R.id.list_image);
            holder.vhView = convertView;
//            mCardView = (CardView) convertView.findViewById(R.id.card_view);
//            mCardView.setOnTouchListener(new CardView.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//
//                    int action = event.getActionMasked();
//                    if (action == MotionEvent.ACTION_DOWN){
//                        mCardView.setCardBackgroundColor(Color.RED);
//                        return false;
//                    }
//                    else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
//                        mCardView.setCardBackgroundColor(Color.WHITE);
//                        return false;
//                    }
//                    else{
//                        return true;
//                    }
//                }
//            });

            convertView.setTag(holder);
            view = convertView;
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            holder.vhView = convertView;
            view = convertView;
        }

        // Setting all values in listview
        hm = (HashMap) codeDataCollection.get(position);

        String codeName = (String) hm.get(KEY_NAME);
        if(codeName.length() > 30)
            codeName = codeName.substring(0,30) + "...";
        holder.tvCodeName.setText(codeName);
        holder.tvCodeFormat.setText((String) hm.get(KEY_CODE_TYPE));
        holder.tvScanningDate.setText((String) hm.get(KEY_DATE));

        String icon_name = hm.get(KEY_CODE_TYPE).toString();
        switch (icon_name){
            case "QR_CODE":
                icon_name = "qrcode";
                break;
            case "DATA_MATRIX":
                icon_name = "data_matrix";
                break;
            case "UPC_A":
                icon_name = "upc_a";
                break;
            case "PDF_417":
                icon_name = "pdf_417";
                break;
            default:
                icon_name = "barcode";
                break;
        }

        String uri = "mipmap/"+ icon_name;
        int imageResource = convertView.getContext().getResources().getIdentifier(
                uri,
                null,
                convertView.getContext().getPackageName());

        Drawable image = convertView.getContext().getResources().getDrawable(imageResource);
        holder.list_image.setImageDrawable(image);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = (String) hm.get(KEY_ID);
                if(id.equals("9999999")){
                    showDialog(mActivity);
                    return;
                }

                Intent intent = new Intent(mActivity, CodeDetails.class);
                intent.putExtra("name", (String) hm.get(KEY_NAME));
                intent.putExtra("format", (String) hm.get(KEY_CODE_TYPE));
                intent.putExtra("comments", "");
                intent.putExtra("date", (String) hm.get(KEY_DATE));

                mActivity.startActivity(intent);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mActionMode != null) {
                    return false;
                }
                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = mActivity.startActionMode(mActionModeCallback);
                mActionMode.setTitle(R.string.select_action); //make text "You selected an item"
                holder.vhView.setSelected(true);

                Snackbar.make(v, "Long click", Snackbar.LENGTH_LONG).show();
                return true;
            }
        });

        return convertView;
    }

    public void clear() {
            if (codeDataCollection != null) {
                codeDataCollection.clear();
            }
    }

    public void addAll(List<HashMap<String, String>> list) {
        if (codeDataCollection != null) {
            codeDataCollection = list;
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.hist_fr_action_mode_menu, menu);

            MenuItem item = menu.findItem(R.id.am_menu_share);
            // Fetch and store ShareActionProvider
            mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

//        private void setShareIntent(Intent shareIntent) {
//            if (mShareActionProvider != null) {
//                mShareActionProvider.setShareIntent(shareIntent);
//            }
//        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.am_menu_delete:
                    view.setSelected(false);

                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.am_menu_share:
                    String id = (String) hm.get(KEY_ID);
                    if(id.equals("9999999")){
                        showDialog(mActivity);
                        return false;
                    }

                    Utils.shareCode(mActivity,
                            (String) hm.get(KEY_NAME),
                            (String) hm.get(KEY_CODE_TYPE),
                            (String) hm.get(KEY_DATE));

                    holder.vhView.setSelected(false);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            holder.vhView.setSelected(false);
        }
    };

    static class ViewHolder{
        TextView tvCodeName;
        TextView tvCodeFormat;
        TextView tvScanningDate;
        ImageView list_image;
        View vhView;
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
}
