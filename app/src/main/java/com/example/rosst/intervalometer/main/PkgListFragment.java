package com.example.rosst.intervalometer.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;

import com.example.rosst.intervalometer.R;
import com.example.rosst.intervalometer.utilities.Utils;

import java.util.List;

public class PkgListFragment extends DialogFragment {
    private Context context;
    private List<ResolveInfo> appListInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);


        onActivityCreated(savedInstanceState);
        initRecycleView(v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        appListInfo = getAppInfoList(context);

    }

    private List<ResolveInfo> getAppInfoList(Context context) {
        if (context != null)
        appListInfo = Utils.getListOfApps(context);

        return appListInfo;
    }

    private void initRecycleView(View view) {
        final RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);

        AppListAdapter appListAdapter = new AppListAdapter(appListInfo, context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(appListAdapter);
        appListAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context,
                        mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.camera_app_name),
                                appListInfo.get(position).activityInfo.name);
                        editor.putString(getString(R.string.camera_app_package),
                                appListInfo.get(position).activityInfo.packageName);
                        editor.apply();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }
}
