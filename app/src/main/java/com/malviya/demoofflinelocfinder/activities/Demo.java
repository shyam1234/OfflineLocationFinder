package com.malviya.demoofflinelocfinder.activities;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

/**
 * Created by 23508 on 7/6/2017.
 */

public class Demo extends ArrayAdapter {
    public Demo(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }
}
