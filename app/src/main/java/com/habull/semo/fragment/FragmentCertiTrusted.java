package com.habull.semo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.habull.semo.R;
import com.habull.semo.adapter.ListViewInfoCertiAdapter;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;

public class FragmentCertiTrusted extends Fragment {
    private ArrayList<X509Certificate> list = new ArrayList<>();
    private ListViewInfoCertiAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_certi_trusted, container, false);

        ListView listView = view.findViewById(R.id.certiTrustedListView);

        adapter = new ListViewInfoCertiAdapter(getContext(), list);
        listView.setAdapter(adapter);

        list.clear();
        list.addAll(getTrustCerti());
        adapter.notifyDataSetChanged();

        return view;
    }

    public ArrayList<X509Certificate> getTrustCerti() {
        ArrayList<X509Certificate> list = new ArrayList<>();

        try {
            KeyStore ks = KeyStore.getInstance("AndroidCAStore");

            if (ks != null) {
                ks.load(null, null);
                Enumeration<String> aliases = ks.aliases();

                while (aliases.hasMoreElements()) {
                    String alias = (String) aliases.nextElement();
                    list.add((X509Certificate) ks.getCertificate(alias));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sortListTrustCerti(list);

        return list;
    }

    public void sortListTrustCerti(ArrayList<X509Certificate> list) {
        Collections.sort(list, new Comparator<X509Certificate>() {
            @Override
            public int compare(X509Certificate t1, X509Certificate t2) {
                // get string of certificate
                String s1 = t1.getIssuerDN().getName();
                String s2 = t2.getIssuerDN().getName();

                // name of certificate t1
                String[] nameSplit1 = s1.split(",");
                String name1 = "";
                for (String i: nameSplit1) {
                    if (i.contains("O=")) {
                        String[] oValue = i.split("=");
                        name1 = oValue[1];
                    }
                }

                // name of certificate t2
                String[] nameSplit2 = s2.split(",");
                String name2 = "";
                for (String i: nameSplit2) {
                    if (i.contains("O=")) {
                        String[] oValue = i.split("=");
                        name2 = oValue[1];
                    }
                }

                return name1.toLowerCase().compareTo(name2.toLowerCase());
            }
        });
    }
}
