package com.example.katrina.thedecuratorbeta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProjectBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_board);

        Intent intent = getIntent();
        Project project = ((Intent) intent).getParcelableExtra("Project");

        String title = project.getTitle();
        String budget = project.getBudget();

        TextView projectTitle = findViewById(R.id.project_board_title);
        TextView projectBudget = findViewById(R.id.project_board_budget);

        projectTitle.setText(title);
        projectBudget.setText(budget);
    }
}
