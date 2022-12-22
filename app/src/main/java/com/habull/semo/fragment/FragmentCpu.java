package com.habull.semo.fragment;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.habull.semo.R;
import com.habull.semo.adapter.ListViewInfoDeviceAdapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class FragmentCpu extends Fragment {
    private ArrayList<Pair<String, String>> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cpu, container, false);

        ListView listView = view.findViewById(R.id.cpuListView);

        list.clear();
        list.addAll(getCpuInfo());

        ListViewInfoDeviceAdapter adapter = new ListViewInfoDeviceAdapter(getContext(), list);
        listView.setAdapter(adapter);

        return view;
    }

    public ArrayList<Pair<String, String>> getCpuInfo() {
        ArrayList<Pair<String, String>> result = new ArrayList<>();

        //get name of cpu
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String s;
            boolean check = false;
            while ((s = br.readLine()) != null) {
                String[] split = s.split(":");

                if (split.length > 1 && split[0].trim().equals("model name")) {
                    check = true;
                    result.add(new Pair<>("Name CPU", split[1].trim()));

                    break;
                }
            }
            if (!check)
                result.add(new Pair<>("Name CPU", ""));

            br.close();
        }
        catch (Exception e) {
            Log.i("error", "");
        }

        // get num of core cpu
        result.add(new Pair<>("Number of core", String.valueOf(Runtime.getRuntime().availableProcessors())));

        // get architecture
        result.add(new Pair<>("Architecture", System.getProperty("os.arch")));

        // get name of gpu
        result.add(new Pair<>("Name GPU", GLES20.glGetString(GLES20.GL_RENDERER)));

        // get vendor of gpu
        result.add(new Pair<>("Vendor GPU", GLES20.glGetString(GLES20.GL_VENDOR)));

        return result;
    }
}
