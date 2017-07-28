package com.malviya.demoofflinelocfinder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malviya.demoofflinelocfinder.R;
import com.malviya.demoofflinelocfinder.model.MainDataModel;

import java.util.ArrayList;

/**
 * Created by 23508 on 6/15/2017.
 */

public class PlotLocAdapter extends RecyclerView.Adapter<PlotLocAdapter.MyViewHolder> {

    private final Context mContext;
    private final ArrayList<MainDataModel> mList;

    public PlotLocAdapter(Context mainActivity, ArrayList<MainDataModel> mListOfLatiLongi) {
        mContext = mainActivity;
        mList = mListOfLatiLongi;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new MyViewHolder(inflater.inflate(R.layout.plot_loc_row, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
         holder.lati_longi.setText(mList.get(position).getLatitude()+" - "+mList.get(position).getLongitude());
         holder.accuracy.setText(""+mList.get(position).getAccuracy());
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public final TextView lati_longi;
        private final TextView accuracy;

        public MyViewHolder(View itemView) {
            super(itemView);
            lati_longi = (TextView)itemView.findViewById(R.id.textview_main_row_lati_longi);
            accuracy = (TextView)itemView.findViewById(R.id.textview_main_row_accuracy);
        }
    }
}
