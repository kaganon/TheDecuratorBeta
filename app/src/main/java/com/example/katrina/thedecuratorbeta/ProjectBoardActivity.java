package com.example.katrina.thedecuratorbeta;


import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

import static com.pinterest.android.pdk.Utils.log;

public class ProjectBoardActivity extends AppCompatActivity implements PinsRvAdapter.OnPinListener {

    private static final String TAG = "ProjectBoardActivity";
    private static boolean DEBUG = true;
    private List<PDKPin> pinList;
    private ImageView topLeftImg, topRightImg, centerImg, bottomLeftImg, bottomRightImg, bottomCenterImg;
    private TextView topLeftCost, topRightCost, centerCost, bottomLeftCost, bottomRightCost, bottomCenterCost;
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

    private TextView getEmptyTextView() {
        List<TextView> textViewList = new ArrayList<>();

        topLeftCost = findViewById(R.id.top_left_txt);
        textViewList.add(topLeftCost);

        topRightCost = findViewById(R.id.top_right_txt);
        textViewList.add(topRightCost);

        centerCost = findViewById(R.id.center_txt);
        textViewList.add(centerCost);

        bottomLeftCost = findViewById(R.id.bottom_left_txt);
        textViewList.add(bottomLeftCost);

        bottomRightCost = findViewById(R.id.bottom_right_txt);
        textViewList.add(bottomRightCost);

        bottomCenterCost = findViewById(R.id.bottom_center_txt);
        textViewList.add(bottomCenterCost);

        TextView textView = null;

        int i = 0;
        while (i < textViewList.size()) {
            boolean hasNoText = (textViewList.get(i).getText().length() == 0);

            if (hasNoText) {
                textView = textViewList.get(i);
                break;
            }
            i++;
        }

        return textView;
    }


    private ImageView getEmptyImageView() {
        final List<ImageView> imageViewList = new ArrayList<>();

        topLeftImg = findViewById(R.id.top_left_img);
        imageViewList.add(topLeftImg);

        topRightImg = findViewById(R.id.top_right_img);
        imageViewList.add(topRightImg);

        centerImg = findViewById(R.id.center_img);
        imageViewList.add(centerImg);

        bottomLeftImg = findViewById(R.id.bottom_left_img);
        imageViewList.add(bottomLeftImg);

        bottomRightImg = findViewById(R.id.bottom_right_img);
        imageViewList.add(bottomRightImg);

        bottomCenterImg = findViewById(R.id.bottom_center_img);
        imageViewList.add(bottomCenterImg);

        ImageView imageView = null;

        int i = 0;
        while (i < imageViewList.size()) {

            boolean hasNoImage = (imageViewList.get(i).getDrawable() == null);

            if (hasNoImage) {
                imageView = imageViewList.get(i);
                break;
            }
            i++;
        }

        return imageView;

    }


    private void setBoardPin(PDKPin product, ImageView imageView, TextView textView) {

        String productMetadata = product.getMetadata();
        try {
            JSONObject reader = new JSONObject(productMetadata);
            JSONObject pObjectProduct = reader.getJSONObject("product");

            // Set cost
            JSONObject pObjectOffer = pObjectProduct.getJSONObject("offer");
            String pinPrice = pObjectOffer.getString("price");
            textView.setText(pinPrice);

            String productImgUrl = product.getImageUrl();
            Glide.with(this)
                    .load(productImgUrl)
                    .into(imageView);

            Log.d(TAG, "onBindViewHolder: JSON PIN COST " + pinPrice);

        } catch (JSONException e) {
            String noCost = "No cost";
            textView.setText(noCost);
            e.printStackTrace();
        }
    }


    @Override
    public void onPinClick(int position) {
        PDKPin product = pinList.get(position); // reference to the pin selected
        // check if imageview is empty, then send to board pin product, and send the
        // image view as a parameter into the setBoardPin method
        TextView textView = getEmptyTextView();
        ImageView imageView = getEmptyImageView();

        if ((textView != null) && (imageView != null)) {
            setBoardPin(product, imageView, textView);
        } else {
            Toast.makeText(this, "CANNOT ADD ANYMORE PROJECTS", Toast.LENGTH_LONG).show();
        }

    }


}




