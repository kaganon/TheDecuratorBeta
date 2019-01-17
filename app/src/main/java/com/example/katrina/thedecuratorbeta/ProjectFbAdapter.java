package com.example.katrina.thedecuratorbeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ProjectFbAdapter extends RecyclerView.Adapter<ProjectFbAdapter.ProjectViewHolder> {

    Context context;
    ArrayList<Project> projects;

    public ProjectFbAdapter(Context c, ArrayList<Project> p)
    {
        context = c;
        projects = p;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ProjectViewHolder(LayoutInflater.from(context).inflate(R.layout.item_project_list, viewGroup, false));
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

    class ProjectViewHolder extends RecyclerView.ViewHolder {

        TextView projectName;
        TextView projectBudget;


        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);

            projectName = (TextView) itemView.findViewById(R.id.text_proj_name);
            projectBudget = (TextView) itemView.findViewById(R.id.text_proj_budget);
        }
    }
}
