package com.example.katrina.thedecuratorbeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProjectFbAdapter extends RecyclerView.Adapter<ProjectFbAdapter.ProjectViewHolder> {

    private Context context;
    private ArrayList<Project> projects;
    private OnProjectListener mOnProjectListener;
    private OnProjectLongListener mOnProjectLongListener;

    public ProjectFbAdapter(Context c, ArrayList<Project> p, OnProjectListener onProjectListener, OnProjectLongListener onProjectLongListener) {
        context = c;
        projects = p;
        mOnProjectListener = onProjectListener;
        mOnProjectLongListener = onProjectLongListener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = (LayoutInflater.from(context).inflate(R.layout.item_project_list, viewGroup, false));
        return new ProjectViewHolder(view, mOnProjectListener, mOnProjectLongListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        holder.projectName.setText(projects.get(position).getTitle());
        String budget = projects.get(position).getBudget();

        float currentBudget = Float.parseFloat(budget);
        DecimalFormat df = new DecimalFormat("###.00");
        String budgetFormatted = "Budget: $" + df.format(currentBudget);

        holder.projectBudget.setText(budgetFormatted);
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        OnProjectLongListener onProjectLongListener;
        OnProjectListener onProjectListener;

        TextView projectName;
        TextView projectBudget;


        public ProjectViewHolder(@NonNull View itemView, OnProjectListener onProjectListener, OnProjectLongListener onProjectLongListener) {
            super(itemView);

            projectName = (TextView) itemView.findViewById(R.id.text_proj_name);
            projectBudget = (TextView) itemView.findViewById(R.id.text_proj_budget);

            this.onProjectListener = onProjectListener;
            this.onProjectLongListener = onProjectLongListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onProjectListener.onProjectClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onProjectLongListener.onProjectLongClick(getAdapterPosition());
            return false;
        }
    }

    public interface OnProjectListener {
        void onProjectClick(int position);
    }

    public interface OnProjectLongListener {
        void onProjectLongClick(int position);
    }


}
