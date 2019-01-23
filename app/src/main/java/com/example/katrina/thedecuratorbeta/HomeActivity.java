package com.example.katrina.thedecuratorbeta;


import android.content.DialogInterface;
import android.content.Intent;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;

import android.widget.Button;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.PDKUser;


import java.util.ArrayList;


import static com.pinterest.android.pdk.Utils.log;

public class HomeActivity extends AppCompatActivity implements ProjectDialog.ProjectDialogListener, ProjectFbAdapter.OnProjectListener, ProjectFbAdapter.OnProjectLongListener {

    private static final String TAG = "HomeActivity";


    private boolean _loading = false;


    private static boolean DEBUG = true;

    private final String USER_FIELDS = "id,image,counts,created_at,first_name,last_name,bio";
    private PDKUser user;

    private TextView userName;
    private ImageView profileImage;

    private Button button;
    private Button deleteButton;

    private DatabaseReference projectReference;

    private RecyclerView recyclerView;
    private ArrayList<Project> projectList;
    private ProjectFbAdapter projectFbAdapter;


    private DatabaseReference userReference;
    private boolean existingUser = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set User Name Information
        userName = (TextView) findViewById(R.id.user_name);
        profileImage = (ImageView) findViewById(R.id.profile_img);

        // Adding new projects
        button = (Button) findViewById(R.id.add_project_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });




        _loading = true;

        getMe();

    }

    public void openDialog() {
        ProjectDialog projectDialog = new ProjectDialog();
        projectDialog.show(getSupportFragmentManager(), "project dialog");
    }

    @Override
    public void applyTexts(String name, String budget) {
        Toast.makeText(this, "New project " + name + "added!",
                Toast.LENGTH_LONG).show();
    }


    private void setUser() {
        userName.setText("Welcome, " + user.getFirstName());
        String userImgUrl = user.getImageUrl();
        profileImage = (ImageView) findViewById(R.id.profile_img);
        Glide.with(this)
                .load(userImgUrl)
                .into(profileImage);
    }

    private void getProjects() {
        String userId = user.getUid();

        recyclerView = (RecyclerView) findViewById(R.id.list_projects);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        projectReference = FirebaseDatabase.getInstance().getReference()
                .child("User").child(userId).child("Project");


        projectReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                projectList = new ArrayList<Project>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Project p = dataSnapshot1.getValue(Project.class);
                    projectList.add(p);
                }

                projectFbAdapter = new ProjectFbAdapter(HomeActivity.this, projectList, HomeActivity.this, HomeActivity.this);
                recyclerView.setAdapter(projectFbAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this,
                        "Ooops, something went wrong:" + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMe() {
        PDKClient.getInstance().getMe(USER_FIELDS, new PDKCallback() {

            @Override
            public void onSuccess(PDKResponse response) {
                if (DEBUG) log(String.format("Status: %d", response.getStatusCode()));
                user = response.getUser();

                setUser();
                getLoggedInUser();
                getProjects();

            }

            @Override
            public void onFailure(PDKException exception) {
                if (DEBUG) log(exception.getDetailMessage());
                Toast.makeText(HomeActivity.this, "Request failed",
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    private void getLoggedInUser() {
        final String userId = user.getUid();

        userReference = FirebaseDatabase.getInstance().getReference()
                .child("User");


        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(userId)) {
                        existingUser = true;
                    }
                }

                if (existingUser == false) {
                    Log.d(TAG, "I am inside the false existing user");
                    User pinterestUser = new User(userId);
                    userReference.child(userId).setValue(pinterestUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this,
                        "Ooops, something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onProjectClick(int position) {
        // NAVIGATE TO THE NEW ACTIVITY
        Log.d(TAG, "onProjectClick: CLICKED!");
        Intent intent = new Intent(this, ProjectBoardActivity.class);
        intent.putExtra("Project", projectList.get(position));
        User loggedInUser = new User(user.getUid());
        intent.putExtra("User", loggedInUser);
        startActivity(intent);

    }

    private void deleteProject(Project project) {
        String projectKey = project.getId();
        Query mQuery = projectReference.orderByKey().equalTo(projectKey);

        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                Toast.makeText(HomeActivity.this, "Project deleted successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }



    @Override
    public void onProjectLongClick(int position) {
        // open delete dialog
        // and delete project
        Project project = projectList.get(position); // reference to the project
        showDeleteDialog(project);
    }

    private void showDeleteDialog(final Project project) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this project?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete project
                deleteProject(project);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });

        builder.create().show();
    }

}
