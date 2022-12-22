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

import java.security.cert.X509Certificate;
import java.util.ArrayList;

public class ListViewInfoCertiAdapter extends ArrayAdapter<X509Certificate> {
    private Context context;
    private ArrayList<X509Certificate> list;

    public ListViewInfoCertiAdapter(@NonNull Context context, ArrayList<X509Certificate> list) {
        super(context, R.layout.listview_item_certi, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listview_item_certi, parent, false);

        TextView nameCerti = view.findViewById(R.id.nameCertiTv);
        TextView infoCerti = view.findViewById(R.id.infoCertiTv);

        X509Certificate certificate = list.get(position);
        String name = certificate.getIssuerDN().getName();

        // name of certificate and name issue
        String[] nameSplit = name.split(",");
        String[] oValue;
        String[] cnValue;
        for (String i: nameSplit) {
            if (i.contains("O=")) {
                oValue = i.split("=");
                nameCerti.setText(oValue[1]);
            }

            if (i.contains("CN=")) {
                cnValue = i.split("=");
                infoCerti.setText(cnValue[1]);
            }
        }

        return view;
    }
}
