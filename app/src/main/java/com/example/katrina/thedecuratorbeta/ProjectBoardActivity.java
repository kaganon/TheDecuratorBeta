package com.example.katrina.thedecuratorbeta;


import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;


import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


import java.util.List;


import static com.pinterest.android.pdk.Utils.log;

public class ProjectBoardActivity extends AppCompatActivity implements PinsRvAdapter.OnPinListener {

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

        getPins();

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

        PinsRvAdapter adapter = new PinsRvAdapter(ProjectBoardActivity.this, pinList, ProjectBoardActivity.this);
        pinRecyclerview.setAdapter(adapter);
    }

    private void setBoardImages(PDKPin product) {
        ImageView topLeftImg, topRightImg, centerImg, bottomLeftImg, bottomRightImg, bottomCenterImg;
        TextView topLeftCost, topRightcost, centerCost, bottomeLeftCost, bottomRightCost, bottomCenterCost;

        String productMetadata = product.getMetadata();

        try {
            JSONObject reader = new JSONObject(productMetadata);
            JSONObject pObjectProduct = reader.getJSONObject("product");

            // Set cost
            JSONObject pObjectOffer = pObjectProduct.getJSONObject("offer");
            String pinPrice = pObjectOffer.getString("price");
            topLeftCost = findViewById(R.id.top_left_txt);
            topLeftCost.setText(pinPrice);

            topLeftImg = (ImageView) findViewById(R.id.top_left_img);
            String productImgUrl = product.getImageUrl();
            Glide.with(this)
                    .load(productImgUrl)
                    .into(topLeftImg);

            Log.d(TAG, "onBindViewHolder: JSON PIN COST " + pinPrice);


        } catch (JSONException e) {
            String noCost = "No cost";
            topLeftCost = findViewById(R.id.top_left_txt);
            topLeftCost.setText(noCost);
            e.printStackTrace();
        }




//        topRightImg = (ImageView) findViewById(R.id.top_left_img);
//        Glide.with(this)
//                .load("https://images.unsplash.com/photo-1523755231516" +
//                        "-e43fd2e8dca5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80")
//                .into(topRightImg);
//
//        centerImg = (ImageView) findViewById(R.id.top_left_img);
//        Glide.with(this)
//                .load("https://images.unsplash.com/photo-1523755231516" +
//                        "-e43fd2e8dca5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80")
//                .into(centerImg);
//
//        bottomLeftImg = (ImageView) findViewById(R.id.top_left_img);
//        Glide.with(this)
//                .load("https://images.unsplash.com/photo-1523755231516" +
//                        "-e43fd2e8dca5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80")
//                .into(bottomLeftImg);
//
//        bottomRightImg = (ImageView) findViewById(R.id.top_left_img);
//        Glide.with(this)
//                .load("https://images.unsplash.com/photo-1523755231516" +
//                        "-e43fd2e8dca5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80")
//                .into(bottomRightImg);
//
//        bottomCenterImg = (ImageView) findViewById(R.id.top_left_img);
//        Glide.with(this)
//                .load("https://images.unsplash.com/photo-1523755231516" +
//                        "-e43fd2e8dca5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80")
//                .into(bottomCenterImg);

    }

    private void setBoardImages2(PDKPin product) {
        ImageView topLeftImg, topRightImg, centerImg, bottomLeftImg, bottomRightImg, bottomCenterImg;
        TextView topLeftCost, topRightCost, centerCost, bottomeLeftCost, bottomRightCost, bottomCenterCost;

        String productMetadata = product.getMetadata();

        try {
            JSONObject reader = new JSONObject(productMetadata);
            JSONObject pObjectProduct = reader.getJSONObject("product");

            // Set cost
            JSONObject pObjectOffer = pObjectProduct.getJSONObject("offer");
            String pinPrice = pObjectOffer.getString("price");
            topRightCost = findViewById(R.id.top_right_txt);
            topRightCost.setText(pinPrice);

            topRightImg = (ImageView) findViewById(R.id.top_right_img);
            String productImgUrl = product.getImageUrl();
            Glide.with(this)
                    .load(productImgUrl)
                    .into(topRightImg);

            Log.d(TAG, "onBindViewHolder: JSON PIN COST " + pinPrice);


        } catch (JSONException e) {
            String noCost = "No cost";
            topRightCost = findViewById(R.id.top_right_txt);
            topRightCost.setText(noCost);
            e.printStackTrace();
        }
    }

        @Override
    public void onPinClick(int position) {
        PDKPin product = pinList.get(position); // reference to the pin selected

        TextView topLeftCost = (TextView) findViewById(R.id.top_left_txt);
        int textLength = topLeftCost.getText().length();

        if (textLength == 0) {
            setBoardImages(product);
        } else {
            setBoardImages2(product);
            Toast.makeText(this, "Didn't work", Toast.LENGTH_LONG).show();
        }

    }
}




