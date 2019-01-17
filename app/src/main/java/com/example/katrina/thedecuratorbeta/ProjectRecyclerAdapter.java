package com.example.katrina.thedecuratorbeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProjectRecyclerAdapter extends RecyclerView.Adapter<ProjectRecyclerAdapter.ViewHolder> {

    private final Context mContext;
//    private final List<ProjectInfo> mProjects;
    private final LayoutInflater layoutInflater;

    public ProjectRecyclerAdapter(Context context) {
        mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = layoutInflater.inflate(R.layout.item_project_list, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView projectName;
        public final TextView projectBudget;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            projectName = (TextView) itemView.findViewById(R.id.text_proj_name);
            projectBudget = (TextView) itemView.findViewById(R.id.text_proj_budget);

        }
    }
}
