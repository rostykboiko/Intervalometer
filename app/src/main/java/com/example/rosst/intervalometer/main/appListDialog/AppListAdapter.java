package com.example.rosst.intervalometer.main.appListDialog;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rosst.intervalometer.R;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
    private List<ResolveInfo> appItemList;
    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView appIcon;
        private TextView appTitle, appDesc;

        ViewHolder(View view) {
            super(view);
            appIcon = view.findViewById(R.id.app_icon);
            appTitle = view.findViewById(R.id.app_title);
            appDesc = view.findViewById(R.id.app_desc);
        }
    }

    AppListAdapter(List<ResolveInfo> appItemList, Context context) {
        this.appItemList = appItemList;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResolveInfo appInfo = appItemList.get(position);

        holder.appIcon.setImageDrawable(appInfo.loadIcon(context.getPackageManager()));
        holder.appTitle.setText(appInfo.loadLabel(context.getPackageManager()));
        holder.appDesc.setText(appInfo.activityInfo.name);
    }

    @Override
    public int getItemCount() {
        return appItemList.size();
    }

}
