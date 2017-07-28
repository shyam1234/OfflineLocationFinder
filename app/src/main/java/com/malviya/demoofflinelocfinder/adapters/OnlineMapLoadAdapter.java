package com.malviya.demoofflinelocfinder.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malviya.demoofflinelocfinder.R;
import com.malviya.demoofflinelocfinder.model.MapLogDataModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by 23508 on 6/19/2017.
 */

public class OnlineMapLoadAdapter extends RecyclerView.Adapter<OnlineMapLoadAdapter.MyViewHolder> {
    private final Context mContext;
    private final ArrayList<MapLogDataModel> mList;
    private View.OnClickListener mListener;

    public OnlineMapLoadAdapter(Context pContext, ArrayList<MapLogDataModel> pList) {
        mContext = pContext;
        mList = pList;
        mListener = (View.OnClickListener) pContext;
    }

    @Override
    public OnlineMapLoadAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.map_log_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OnlineMapLoadAdapter.MyViewHolder holder, int position) {
        setImageFromFile(holder.imgview_map, mList.get(position).getFilePath());
        holder.textview_location.setText("Location: "+mList.get(position).getLocation());
        holder.textview_acre.setText("Acre: "+mList.get(position).getAcre());
        holder.textview_date.setText("Date: "+getTimeFormatted(mList.get(position).getLastModified()));
        holder.rel_holder.setOnClickListener(mListener);
        holder.rel_holder.setTag(position);
        holder.imgview_map.setOnClickListener(mListener);
        holder.imgview_map.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        protected final ImageView imgview_map;
        protected final TextView textview_location;
        protected final TextView textview_acre;
        protected final TextView textview_date;
        protected final RelativeLayout rel_holder;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgview_map = (ImageView) itemView.findViewById(R.id.imgview_map_log_row);
            textview_location = (TextView) itemView.findViewById(R.id.textview_map_log_loc);
            textview_acre = (TextView) itemView.findViewById(R.id.textview_map_log_acre);
            textview_date = (TextView) itemView.findViewById(R.id.textview_map_log_date);
            rel_holder = (RelativeLayout) itemView.findViewById(R.id.rel_map_log_row);
        }
    }


    public void setImageFromFile(ImageView imageView, String filepath){
        File imgFile = new File(filepath);
        if(imgFile.exists())
        {
            imageView.setImageURI(Uri.fromFile(imgFile));
        }
    }

    private String getTimeFormatted(String millisec) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(millisec));
            return formatter.format(calendar.getTime());
        }catch (Exception e){
            Log.e("Malviya","Exception from getTimeFormatted "+e.getMessage());
        }
        return "";
    }
}
