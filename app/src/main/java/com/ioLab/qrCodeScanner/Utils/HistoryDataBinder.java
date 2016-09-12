package com.ioLab.qrCodeScanner.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import app.num.barcodescannerproject.R;

/**
 * Created by disknar on 11.09.2016.
 */
public class HistoryDataBinder<T> extends BaseAdapter{

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CODE_TYPE = "format";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_DATE = "date";
    private static final String KEY_ICON = "icon";

    LayoutInflater inflater;
    ImageView thumb_image;
    private List<HashMap<String, String>> codeDataCollection;
//    List<HashMap<String,String>> codeDataCollection;
    ViewHolder holder;

    public HistoryDataBinder(Activity act, List<HashMap<String, String>> list) {
        this.codeDataCollection = list;
        inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(convertView == null){

            view = inflater.inflate(R.layout.fr_history_page_list_item_colored, null);
            holder = new ViewHolder();

            holder.tvCodeName = (TextView)view.findViewById(R.id.tvCodeName); // city name
            holder.tvCodeFormat = (TextView)view.findViewById(R.id.tvCodeFormat); // city weather overview
            holder.tvScanningDate =  (TextView)view.findViewById(R.id.tvScanningDate); // city temperature
            holder.list_image =(ImageView)view.findViewById(R.id.list_image); // thumb image

            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        // Setting all values in listview
        HashMap hm = (HashMap) codeDataCollection.get(position);

        String codeName = (String) hm.get(KEY_NAME);
        if(codeName.length() > 30)
            codeName = codeName.substring(0,30) + "...";
        holder.tvCodeName.setText(codeName);
        holder.tvCodeFormat.setText((String) hm.get(KEY_CODE_TYPE));
        holder.tvScanningDate.setText((String) hm.get(KEY_DATE));

        //Todo Setting an image
//        String uri = "mipmap/"+ hm.get(KEY_ICON);
//        int imageResource = vi.getContext().getApplicationContext().getResources().getIdentifier(uri, null, vi.getContext().getApplicationContext().getPackageName());
//        Drawable image = vi.getContext().getResources().getDrawable(imageResource);
//        holder.list_image.setImageDrawable(image);

        return view;
    }

    public void clear() {
            if (codeDataCollection != null) {
                codeDataCollection.clear();
            }
    }

    public void addAll(List<HashMap<String, String>> list) {
        if (codeDataCollection != null) {
            codeDataCollection = list;
//            for(HashMap<String, String> ls : list){
//                codeDataCollection.add(ls);
//            }
        }
    }

    static class ViewHolder{

        TextView tvCodeName;
        TextView tvCodeFormat;
        TextView tvScanningDate;
        ImageView list_image;
    }
}
