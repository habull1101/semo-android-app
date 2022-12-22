package com.habull.semo.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.habull.semo.R;

import java.util.ArrayList;

public class ListViewInfoDeviceAdapter extends ArrayAdapter<Pair<String, String>> {
    private Context context;
    private ArrayList<Pair<String, String>> list;

    public ListViewInfoDeviceAdapter(@NonNull Context context, ArrayList<Pair<String, String>> list) {
        super(context, R.layout.listview_item_info, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listview_item_info, parent, false);

        TextView title = view.findViewById(R.id.titleTv);
        TextView value = view.findViewById(R.id.valueTv);

        Pair<String, String> pair = list.get(position);
        title.setText(pair.first);
        value.setText(pair.second);

        return view;
    }
}
