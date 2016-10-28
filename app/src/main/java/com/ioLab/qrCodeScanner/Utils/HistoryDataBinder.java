package com.ioLab.qrCodeScanner.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
    private View view;
    private HashMap hm;

    public HistoryDataBinder(Activity act, List<HashMap<String, String>> list) {
        this.codeDataCollection = list;
        inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActivity = act;
    }

    public int getCount() {
        return codeDataCollection.size();
    }

    public Object getItem(int position) {
        return codeDataCollection.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){

            convertView = inflater.inflate(R.layout.fr_history_page_list_item_cards_colored, null);
            holder = new ViewHolder();

            holder.tvCodeName = (TextView)convertView.findViewById(R.id.tvCodeName);
            holder.tvCodeFormat = (TextView)convertView.findViewById(R.id.tvCodeFormat);
            holder.tvScanningDate = (TextView)convertView.findViewById(R.id.tvScanningDate);
            holder.list_image = (ImageView)convertView.findViewById(R.id.list_image);
            holder.vhView = convertView;

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

    static class ViewHolder{
        TextView tvCodeName;
        TextView tvCodeFormat;
        TextView tvScanningDate;
        ImageView list_image;
        View vhView;
    }
}
