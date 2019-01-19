package com.example.katrina.thedecuratorbeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProjectFbAdapter extends RecyclerView.Adapter<ProjectFbAdapter.ProjectViewHolder> {


    private Context context;
    private ArrayList<Project> projects;
    private OnProjectListener mOnProjectListener;

    public ProjectFbAdapter(Context c, ArrayList<Project> p, OnProjectListener onProjectListener) {
        context = c;
        projects = p;
        mOnProjectListener = onProjectListener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = (LayoutInflater.from(context).inflate(R.layout.item_project_list, viewGroup, false));
        return new ProjectViewHolder(view, mOnProjectListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        holder.projectName.setText(projects.get(position).getTitle());
        holder.projectBudget.setText("Budget: $" + projects.get(position).getBudget());
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnProjectListener onProjectListener;

        TextView projectName;
        TextView projectBudget;


        public ProjectViewHolder(@NonNull View itemView, OnProjectListener onProjectListener) {
            super(itemView);

            projectName = (TextView) itemView.findViewById(R.id.text_proj_name);
            projectBudget = (TextView) itemView.findViewById(R.id.text_proj_budget);
            this.onProjectListener = onProjectListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onProjectListener.onProjectClick(getAdapterPosition());
        }
    }

    public interface OnProjectListener {
        void onProjectClick(int position);
    }
}
