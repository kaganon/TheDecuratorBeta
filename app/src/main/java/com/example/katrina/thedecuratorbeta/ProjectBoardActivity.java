package com.example.katrina.thedecuratorbeta;


import android.content.Intent;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


import static com.pinterest.android.pdk.Utils.log;

public class ProjectBoardActivity extends AppCompatActivity implements PinsRvAdapter.OnPinListener {

    private static final String TAG = "ProjectBoardActivity";
    private static boolean DEBUG = true;
    private List<PDKPin> pinList;
    private ImageView topLeftImg, topRightImg, centerImg, bottomLeftImg, bottomRightImg, bottomCenterImg;
    private TextView topLeftCost, topRightCost, centerCost, bottomLeftCost, bottomRightCost, bottomCenterCost;
    private static final String PIN_FIELDS = "id,link,creator,image,counts,note,created_at,board,metadata";
    private Float budgetNum;
    private TextView estimateTv;
    private TextView overBudgetTv;
    private TextView overBudgetPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_board);

        Intent intent = getIntent();
        Project project = ((Intent) intent).getParcelableExtra("Project");

        String title = project.getTitle();
        String budget = project.getBudget();
        budgetNum = Float.parseFloat(budget);
        estimateTv = findViewById(R.id.project_board_estimate);
        overBudgetTv = findViewById(R.id.project_over_budget);
        overBudgetPrice = findViewById(R.id.over_budget_price);

        DecimalFormat df = new DecimalFormat("###.00");
        String budgetText = df.format(budgetNum);
        String budgetTextFormat = "Budget: $" + budgetText;

        TextView projectTitle = findViewById(R.id.project_board_title);
        TextView projectBudget = findViewById(R.id.project_board_budget);

        projectTitle.setText(title);
        projectBudget.setText(budgetTextFormat);


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

            String priceWithoutSymbol = pinPrice.substring(1);
            float pinPriceFl = Float.parseFloat(priceWithoutSymbol);

            DecimalFormat df = new DecimalFormat("###.00");
            String pinPriceFormatted = df.format(pinPriceFl);

            String formattedPriceForTv = "$" + pinPriceFormatted;

            textView.setText(formattedPriceForTv);

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

    private void getTotalEstimate(PDKPin product) {

        // Estimate starts at 0
        String currentEstimate = estimateTv.getText().toString();
        float currentEstimateNum = Float.parseFloat(currentEstimate);

        String productMetadata = product.getMetadata();

        try {

            JSONObject reader = new JSONObject(productMetadata);
            JSONObject pObjectProduct = reader.getJSONObject("product");

            // Get Cost
            JSONObject pObjectOffer = pObjectProduct.getJSONObject("offer");
            String pinPrice = pObjectOffer.getString("price");

            float currentPrice = getPrice(pinPrice);
            float totalEstimate = currentPrice + currentEstimateNum;

            String totalEstimateText = String.format(Float.toString(totalEstimate), "%.2f");

            estimateTv.setText(totalEstimateText);

            if (totalEstimate > budgetNum) {
                Float budgetDiff = totalEstimate - budgetNum;
                String budgetDiffFormatted = String.format(String.valueOf(budgetDiff), "%.2f");
                String fullBudgetFormatted = "$" + budgetDiffFormatted;

                overBudgetTv.setVisibility(View.VISIBLE);
                overBudgetPrice.setVisibility(View.VISIBLE);
                overBudgetPrice.setText(fullBudgetFormatted);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private float getPrice(String productPrice) {
        float price = 0.0f;

        if (productPrice.length() != 0) {
            String priceWithoutSymbol = productPrice.substring(1);
            price = Float.parseFloat(priceWithoutSymbol);
        }

        return price;
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
            getTotalEstimate(product);
        } else {
            Toast.makeText(this, "CANNOT ADD ANYMORE PROJECTS", Toast.LENGTH_LONG).show();
        }

    }

    private void subtractEstimateCosts(TextView textView) {

        // Subtract price from total estimate and over budget estimate
        String currentPinCost = textView.getText().toString();
        float currentPinNum = getPrice(currentPinCost);

        String currentEstimate = estimateTv.getText().toString();
        float currentEstimateNum = Float.parseFloat(currentEstimate);

        String currentOverBudget = overBudgetPrice.getText().toString();
        float currentOverBudgetNum = getPrice(currentOverBudget);

        if (currentEstimateNum > 0) {
            float newEstimate = currentEstimateNum - currentPinNum;
            String newEstimateText = String.format(Float.toString(newEstimate), "%.2f");
            estimateTv.setText(newEstimateText);
        }

        if ((currentOverBudgetNum > 0) && (currentPinNum >= currentOverBudgetNum)){
            float newOverBudget = 0.0f;
            String newOverBudgetText = String.format(Float.toString(newOverBudget), "%.2f");
            overBudgetPrice.setText(newOverBudgetText);
            overBudgetTv.setVisibility(View.GONE);
            overBudgetPrice.setVisibility(View.GONE);

        } else if ((currentOverBudgetNum > 0) && (currentPinNum <= currentOverBudgetNum)) {
            float newOverBudget = currentOverBudgetNum - currentPinNum;
            String newOverBudgetText = String.format(Float.toString(newOverBudget), "%.2f");
            overBudgetPrice.setText(newOverBudgetText);
        }
    }

    public void topLeftPinClick(View view) {
        ImageView imageView = (ImageView) view;
        TextView textView = findViewById(R.id.top_left_txt);

        if (textView.getText().toString().length() != 0) {
            subtractEstimateCosts(textView);
        }

        imageView.setImageDrawable(null);
        textView.setText("");
    }

    public void topRightPinClick(View view) {
        ImageView imageView = (ImageView) view;
        TextView textView = findViewById(R.id.top_right_txt);

        if (textView.getText().toString().length() != 0) {
            subtractEstimateCosts(textView);
        }

        imageView.setImageDrawable(null);
        textView.setText("");

    }

    public void centerPinClick(View view) {
        ImageView imageView = (ImageView) view;
        TextView textView = findViewById(R.id.center_txt);

        if (textView.getText().toString().length() != 0) {
            subtractEstimateCosts(textView);
        }

        imageView.setImageDrawable(null);
        textView.setText("");

    }

    public void bottomLeftPinClick(View view) {
        ImageView imageView = (ImageView) view;
        TextView textView = findViewById(R.id.bottom_left_txt);

        if (textView.getText().toString().length() != 0) {
            subtractEstimateCosts(textView);
        }

        imageView.setImageDrawable(null);
        textView.setText("");

    }

    public void bottomRightPinClick(View view) {
        ImageView imageView = (ImageView) view;
        TextView textView = findViewById(R.id.bottom_right_txt);

        if (textView.getText().toString().length() != 0) {
            subtractEstimateCosts(textView);
        }

        imageView.setImageDrawable(null);
        textView.setText("");
    }

    public void bottomCenterPinClick(View view) {
        ImageView imageView = (ImageView) view;
        TextView textView = findViewById(R.id.bottom_center_txt);

        if (textView.getText().toString().length() != 0) {
            subtractEstimateCosts(textView);
        }

        imageView.setImageDrawable(null);
        textView.setText("");
    }





}




