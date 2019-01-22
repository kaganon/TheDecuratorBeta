package com.example.katrina.thedecuratorbeta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.PDKUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;
import static com.pinterest.android.pdk.Utils.log;

public class ProjectDialog extends AppCompatDialogFragment {

    private EditText projectName;
    private EditText projectBudget;
    private ProjectDialogListener listener;
    private TextView userName;
    private final String USER_FIELDS = "id,image,counts,created_at,first_name,last_name,bio";
    private static boolean DEBUG = true;
    private PDKUser user;

    DatabaseReference projectsReference;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        getUser();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        projectsReference = FirebaseDatabase.getInstance().getReference("User");

        builder.setView(view)
                .setTitle("Project Details")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // DO NOTHING
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = projectName.getText().toString();
                        String budget = projectBudget.getText().toString();
                        String budgetFormat = "$" + projectBudget.getText().toString();
                        listener.applyTexts(title, budgetFormat);

                        addProject(title, budget);
                    }
                });

        projectName = view.findViewById(R.id.edit_project_name);
        projectBudget = view.findViewById(R.id.edit_project_budget);


        return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ProjectDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ProjectDialogListener");
        }

    }

    public void getUser() {
        PDKClient.getInstance().getMe(USER_FIELDS, new PDKCallback() {

            @Override
            public void onSuccess(PDKResponse response) {
                if (DEBUG) log(String.format("Status: %d", response.getStatusCode()));
                user = response.getUser();

            }

            @Override
            public void onFailure(PDKException exception) {
                if (DEBUG) log(exception.getDetailMessage());
            }
        });
    }


    public void addProject(final String title, final String budget) {

//
//
//        PDKClient.getInstance().getMe(USER_FIELDS, new PDKCallback() {
//
//            @Override
//            public void onSuccess(PDKResponse response) {
//                if (DEBUG) log(String.format("Status: %d", response.getStatusCode()));
//                user = response.getUser();

                String id = user.getUid();

                String projectId = projectsReference.child(id)
                        .child("Project").push().getKey();

                Project project = new Project(title, budget, projectId);

                projectsReference.child(id).child("Project").child(projectId)
                        .setValue(project);
//            }
//
//            @Override
//            public void onFailure(PDKException exception) {
//                if (DEBUG) log(exception.getDetailMessage());
//            }
//        });

    }



    public interface ProjectDialogListener {
        void applyTexts(String name, String budget);
    }
}

