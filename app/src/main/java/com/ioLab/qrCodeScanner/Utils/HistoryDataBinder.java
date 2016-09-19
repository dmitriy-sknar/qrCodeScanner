package com.ioLab.qrCodeScanner.Utils;

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


/**
 * Created by disknar on 11.09.2016.
 */
public class HistoryDataBinder<T> extends BaseAdapter{

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CODE_TYPE = "format";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_DATE = "date";

    LayoutInflater inflater;
    private List<HashMap<String, String>> codeDataCollection;
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
//        final CardView mCardView;
        if(convertView == null){

            convertView = inflater.inflate(R.layout.fr_history_page_list_item_cards_colored, null);
            holder = new ViewHolder();

            holder.tvCodeName = (TextView)convertView.findViewById(R.id.tvCodeName); // city name
            holder.tvCodeFormat = (TextView)convertView.findViewById(R.id.tvCodeFormat); // city weather overview
            holder.tvScanningDate = (TextView)convertView.findViewById(R.id.tvScanningDate); // city temperature
            holder.list_image = (ImageView)convertView.findViewById(R.id.list_image); // thumb image

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
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        // Setting all values in listview
        HashMap hm = (HashMap) codeDataCollection.get(position);

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
