package com.example.rosst.intervalometer.floatingButtonService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rosst.intervalometer.R;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {
    private List<String> optionsList;
    private Activity activity;
    private LayoutInflater layoutInflater;

    SpinnerAdapter(List<String> optionsList, Service service) {
        this.optionsList = optionsList;
        this.layoutInflater = LayoutInflater.from(service);
    }

    public SpinnerAdapter(List<String> optionsList, Activity activity) {
        this.optionsList = optionsList;
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return optionsList.size();
    }

    @Override
    public Object getItem(int position) {
        return optionsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View converter = view;
        if (view == null)
            converter = layoutInflater.inflate(R.layout.frames_spinner, null);
            TextView optionItem = (TextView)converter.findViewById(R.id.list_item);
            optionItem.setText(optionsList.get(position));

        return converter;
    }
}
