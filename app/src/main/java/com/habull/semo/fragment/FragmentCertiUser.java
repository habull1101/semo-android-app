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
import java.util.Enumeration;

public class FragmentCertiUser extends Fragment {
    private ArrayList<X509Certificate> list = new ArrayList<>();
    private ListViewInfoCertiAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_certi_user, container, false);

        ListView listView = view.findViewById(R.id.certiUserListView);

        adapter = new ListViewInfoCertiAdapter(getContext(), list);
        listView.setAdapter(adapter);

        list.clear();
        list.addAll(getUserCerti());
        adapter.notifyDataSetChanged();

        Log.i("size1", String.valueOf(list.size()));

        return view;
    }

    public ArrayList<X509Certificate> getUserCerti() {
        ArrayList<X509Certificate> list = new ArrayList<>();

        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");

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

        return list;
    }
}
