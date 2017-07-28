package com.malviya.demoofflinelocfinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.malviya.demoofflinelocfinder.R;
import com.malviya.demoofflinelocfinder.activities.PlaceAPI;
import com.malviya.demoofflinelocfinder.model.SearchPlaceDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 23508 on 6/22/2017.
 */

public class BookAutoCompleteAdapter extends BaseAdapter implements Filterable {

    //private static final int MAX_RESULTS = 10;
    //private static final String REQUEST_PLACE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    //private static final String GOOGLE_KEY = "AIzaSyAWcFkZJYMVeTR8ZpphSKJuBVs0G9X-UMg";
    //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=ban&types=(cities)&key=API_KEY
    //private ArrayList<SearchPlaceDataModel> mList = new ArrayList<>();

    private PlaceAPI mPlaceAPI;
    private Context mContext;
    private List<SearchPlaceDataModel> resultList = new ArrayList<SearchPlaceDataModel>();
    public BookAutoCompleteAdapter(Context context) {
        mContext = context;
        mPlaceAPI = new PlaceAPI(mContext);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public SearchPlaceDataModel getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown_item_2line, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position).getDescription());
        ((TextView) convertView.findViewById(R.id.text2)).setText(getItem(position).getPlace_id());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    resultList = mPlaceAPI.autocomplete(constraint.toString());
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<SearchPlaceDataModel>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

}
