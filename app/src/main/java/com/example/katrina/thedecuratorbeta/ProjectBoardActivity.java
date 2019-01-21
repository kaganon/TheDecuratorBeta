package com.example.katrina.thedecuratorbeta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.pinterest.android.pdk.Utils.log;

public class ProjectBoardActivity extends AppCompatActivity {

    private static final String TAG = "ProjectBoardActivity";


    private static boolean DEBUG = true;

    private List<PDKPin> pinList;

    private static final String PIN_FIELDS = "id,link,creator,image,counts,note,created_at,board,metadata";


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


        // ---------- START OF PINS ---------- //
        getPins();

        // ---------- SET IMAGES ON THE BOARD -- //
        setBoardImages();
    }


    private void getPins() {
        PDKClient.getInstance().getMyPins(PIN_FIELDS, new PDKCallback() {

            @Override
            public void onSuccess(PDKResponse response) {
                if (DEBUG) log(String.format("Status: %d", response.getStatusCode()));
                pinList = response.getPinList();

                setPins();
            }

            @Override
            public void onFailure(PDKException exception) {
                if (DEBUG) log(exception.getDetailMessage());
                Toast.makeText(ProjectBoardActivity.this, "Request failed",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setPins() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView pinRecyclerview = findViewById(R.id.pins_recycler_view);
        pinRecyclerview.setLayoutManager(layoutManager);

        PinsRvAdapter adapter = new PinsRvAdapter(this, pinList);
        pinRecyclerview.setAdapter(adapter);
    }

    private void setBoardImages() {
        // Set Circle Images
        ImageView topLeftImg, topRightImg, centerImg, bottomLeftImg, bottomRightImg, bottomCenterImg;

        topLeftImg = (ImageView) findViewById(R.id.top_left_img);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1523755231516" +
                        "-e43fd2e8dca5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80")
                .into(topLeftImg);

        topRightImg = (ImageView) findViewById(R.id.top_left_img);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1523755231516" +
                        "-e43fd2e8dca5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80")
                .into(topRightImg);

        centerImg = (ImageView) findViewById(R.id.top_left_img);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1523755231516" +
                        "-e43fd2e8dca5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80")
                .into(centerImg);

        bottomLeftImg = (ImageView) findViewById(R.id.top_left_img);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1523755231516" +
                        "-e43fd2e8dca5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80")
                .into(bottomLeftImg);

        bottomRightImg = (ImageView) findViewById(R.id.top_left_img);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1523755231516" +
                        "-e43fd2e8dca5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80")
                .into(bottomRightImg);

        bottomCenterImg = (ImageView) findViewById(R.id.top_left_img);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1523755231516" +
                        "-e43fd2e8dca5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80")
                .into(bottomCenterImg);

    }
}




