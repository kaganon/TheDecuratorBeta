package com.example.katrina.thedecuratorbeta;


import android.content.Intent;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
    private float budgetNum;
    private TextView estimateTv;
    private TextView overBudgetTv;
    private TextView overBudgetPrice;
    private DatabaseReference projectsReference;
    private Project project;
    private User user;
    private List<Pin> savedPinsList;
    private DatabaseReference pinsReference;
    private DatabaseReference pinReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_board);

        Intent intent = getIntent();
        project = ((Intent) intent).getParcelableExtra("Project");
        user = ((Intent) intent).getParcelableExtra("User");

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

        String userId = user.getUserId();
        projectsReference = FirebaseDatabase.getInstance().getReference("User")
                .child(userId);

        getPins();
        getSavedPins();
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

    private List<TextView> getTextViewList() {
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

//        TextView textView = null;
//
//        int i = 0;
//        while (i < textViewList.size()) {
//            boolean hasNoText = (textViewList.get(i).getText().length() == 0);
//
//            if (hasNoText) {
//                textView = textViewList.get(i);
//                break;
//            }
//            i++;
//        }

        return textViewList;
    }


    private List<ImageView> getImageViewList() {

        List<ImageView> imageViewList = new ArrayList<>();

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

//        ImageView imageView = null;
//
////        ArrayList<INt> arr = [ R.id.bottom_center_img, ...]
////        array[i]; inc i
//
//        int i = 0;
//        while (i < imageViewList.size()) {
//
//            boolean hasNoImage = (imageViewList.get(i).getDrawable() == null);
//            Log.d(TAG, "getEmptyImageView: " + imageViewList.get(i).toString());
//
//            if (hasNoImage) {
//                imageView = imageViewList.get(i);
//                break;
//            }
//
//            i++;
//        }

        return imageViewList;
    }


//    private void setBoardPin(PDKPin product, ImageView imageView, TextView textView) {
//
//        String productMetadata = product.getMetadata();
//        try {
//            JSONObject reader = new JSONObject(productMetadata);
//            JSONObject pObjectProduct = reader.getJSONObject("product");
//
//            // Set cost
//            JSONObject pObjectOffer = pObjectProduct.getJSONObject("offer");
//            String pinPrice = pObjectOffer.getString("price");
//
//            String priceWithoutSymbol = pinPrice.substring(1);
//            float pinPriceFl = Float.parseFloat(priceWithoutSymbol);
//
//            DecimalFormat df = new DecimalFormat("###.00");
//            String pinPriceFormatted = df.format(pinPriceFl);
//
//            String formattedPriceForTv = "$" + pinPriceFormatted;
//
//            textView.setText(formattedPriceForTv);
//
//            String productImgUrl = product.getImageUrl();
//            Glide.with(this)
//                    .load(productImgUrl)
//                    .into(imageView);
//
//            Log.d(TAG, "onBindViewHolder: JSON PIN COST " + pinPrice);
//
//
//
//        } catch (JSONException e) {
//            String noCost = "0";
//            textView.setText(noCost);
//            e.printStackTrace();
//        }
//    }

//    private void getTotalEstimate(PDKPin product) {
//
//        // Estimate starts at 0
//        String currentEstimate = estimateTv.getText().toString();
//        float currentEstimateNum = Float.parseFloat(currentEstimate);
//
//        String productMetadata = product.getMetadata();
//
//        try {
//
//            JSONObject reader = new JSONObject(productMetadata);
//            JSONObject pObjectProduct = reader.getJSONObject("product");
//
//            // Get Cost
//            JSONObject pObjectOffer = pObjectProduct.getJSONObject("offer");
//            String pinPrice = pObjectOffer.getString("price");
//
//            float currentPrice = getPrice(pinPrice);
//            float totalEstimate = currentPrice + currentEstimateNum;
//
//            DecimalFormat df = new DecimalFormat("###.00");
//            String totalEstimateText = df.format(totalEstimate);
//
//
//            estimateTv.setText(totalEstimateText);
//
//            if (totalEstimate > budgetNum) {
//                float budgetDiff = totalEstimate - budgetNum;
//
//                String budgetDiffFormatted = df.format(budgetDiff);
//                String fullBudgetFormatted = "$" + budgetDiffFormatted;
//
//                overBudgetTv.setVisibility(View.VISIBLE);
//                overBudgetPrice.setVisibility(View.VISIBLE);
//                overBudgetPrice.setText(fullBudgetFormatted);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private float getPrice(String productPrice) {
        float price = 0.0f;

        if (productPrice.length() > 0) {
            String priceWithoutSymbol = productPrice.substring(1);
            price = Float.parseFloat(priceWithoutSymbol);
        }

        return price;
    }

    private void addPinToFbDb(PDKPin product) {
        // Get product image url
        String imgUrl = product.getImageUrl();

        // Get product price
        String productMetadata = product.getMetadata();
        try {
            JSONObject reader = new JSONObject(productMetadata);
            JSONObject pObjectProduct = reader.getJSONObject("product");

            JSONObject pObjectOffer = pObjectProduct.getJSONObject("offer");
            String pinPrice = pObjectOffer.getString("price");

            String priceWithoutSymbol = pinPrice.substring(1);
            float pinPriceFl = Float.parseFloat(priceWithoutSymbol);

            DecimalFormat df = new DecimalFormat("###.00");
            String pinPriceFormatted = df.format(pinPriceFl);

            String projectId = project.getId();

            String pinId = projectsReference.child("Project").child(projectId)
                    .child("Pin").push().getKey();

            // ADD DATA TO FB
            Pin pin = new Pin(pinPriceFormatted, imgUrl, pinId);
            projectsReference.child("Project").child(projectId)
                    .child("Pin").child(pinId).setValue(pin);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPinClick(int position) {
        PDKPin product = pinList.get(position); // reference to the pin selected

        // check if imageview is empty, then send to board pin product, and send the
        // image view as a parameter into the setBoardPin method
//        TextView textView = getEmptyTextView();
//        ImageView imageView = getEmptyImageView();

//        if ((textView != null) && (imageView != null)) {
//            setBoardPin(product, imageView, textView);
//            getTotalEstimate(product);

        // add pin to firebase
        if (savedPinsList.size() <= 6) {
            addPinToFbDb(product);
        } else {
            Toast.makeText(this, "BOARD FULL. PLEASE REMOVE A PRODUCT BEFORE ADDING A NEW ONE.", Toast.LENGTH_LONG).show();
        }

//        } else {
//            Toast.makeText(this, "PLEASE REMOVE A PROJECT BEFORE ADDING A NEW ONE"
//                    , Toast.LENGTH_LONG).show();
//        }

    }


    private void getSavedPins() {
        String projectId = project.getId();
//        String projectBudget = project.getBudget();
//        final float budgetNum = Float.parseFloat(projectBudget);

        pinsReference = projectsReference.child("Project")
                .child(projectId).child("Pin");

        pinsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                savedPinsList = new ArrayList<Pin>();

                Log.d(TAG, "onDataChange: ON DATA CHANGE TRIGGERED");
                float totalSavedEstimate = 0.0f;
                float totalOverBudget = 0.0f;
                float budgetDiff = 0.0f;


                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    ImageView imageView = getEmptyImageView();
//                    TextView textView = getEmptyTextView();
                    Pin p = ds.getValue(Pin.class);
                    savedPinsList.add(p);


//                    if ((textView != null) && (imageView != null)) {
//                        // set image
//                        Glide.with(getApplicationContext())
//                                .load(p.getImgUrl())
//                                .into(imageView);
//
//                        // set price
//
//                        String priceFormatted = "$" + p.getPrice();
//                        textView.setText(priceFormatted);
//
//                        Log.d(TAG, "onDataChange: " + p.getImgUrl());
//                        Log.d(TAG, "onDataChange: " + p.getId());
//                        Log.d(TAG, "onDataChange: " + p.getPrice());
//                    } else {
//                        Toast.makeText(ProjectBoardActivity.this, "Too many pins added",
//                                Toast.LENGTH_LONG).show();
//
//                        //TODO --- DON'T ADD TOO MANY PINS, BC THIS WILL BE AN ERROR
//                        // MAKE SURE TO ONLY HAVE 6 PINS IN THE DB
//                    }
                    String price = p.getPrice();
                    float priceNum = Float.parseFloat(price);
                    totalSavedEstimate += priceNum;


//                    savedPinsList.add(p);
                }

                if (totalSavedEstimate > budgetNum) {
                    budgetDiff = totalSavedEstimate - budgetNum;
                } else if (totalSavedEstimate <= budgetNum) {
                    budgetDiff = 0.0f;
                }

                totalOverBudget += budgetDiff;

                // TODO
                // get over budget:
                // budget = 1000 --ALWAYS SAME
                // totalSaved estimate = 1500 -- CHANGES
                // total Overbudget = 500
                // total Overbudget = totalSavedEstimate - Budget;


                // on remove
                // budget 1000
                // take out 300 product
                // total saved = 1200
                // total over budget = 200
                // total over budget = total estimate - budget



                int i = 0;
                while (i < savedPinsList.size() && (i < 6)) {
                    List<ImageView> imageViewList = getImageViewList();
                    List<TextView> textViewList = getTextViewList();

                    Glide.with(getApplicationContext())
                            .load(savedPinsList.get(i).getImgUrl())
                            .into(imageViewList.get(i));

                    String priceFormatted = "$" + savedPinsList.get(i).getPrice();
                    textViewList.get(i).setText(priceFormatted);

                    i++;
                }

                while (i >= savedPinsList.size() && (i < 6)) {
                    List<ImageView> imageViewList = getImageViewList();
                    List<TextView> textViewList = getTextViewList();

                    imageViewList.get(i).setImageDrawable(null);
                    textViewList.get(i).setText("");

                    i++;

                }


//                for (Pin p : savedPinsList) {
//                    List<ImageView> imageViewList = getEmptyImageView();
////                    ImageView imageView = getEmptyImageView();
//                    TextView textView = getEmptyTextView();
//
//                    Log.d(TAG, "onDataChange: IMAGEVIEW ID: " + imageView.getId());
//                    Log.d(TAG, "onDataChange: TEXTVIEW ID:" + textView.getId());
//
//                    if ((textView != null) && (imageView != null)) {
//                        // set image
//                        for (ImageView imageView : imageViewList) {
//
//                        }
//                        Glide.with(getApplicationContext())
//                                .load(p.getImgUrl())
//                                .into(imageView);
//
//                        // set price
//                        String priceFormatted = "$" + p.getPrice();
//                        textView.setText(priceFormatted);
//
//                        Log.d(TAG, "onDataChange: URL" + p.getImgUrl());
//                        Log.d(TAG, "onDataChange: ID" + p.getId());
//                        Log.d(TAG, "onDataChange: PRICE" + p.getPrice());
//
//                    } else {
//                        Toast.makeText(ProjectBoardActivity.this, "Too many pins added",
//                                Toast.LENGTH_LONG).show();
//
//                        //TODO --- DON'T ADD TOO MANY PINS, BC THIS WILL BE AN ERROR
//                        // TODO --- MAKE SURE TO ONLY HAVE 6 PINS IN THE DB
//                    }
//                }
                DecimalFormat df = new DecimalFormat("#00.00");
                String totalEstimateText = df.format(totalSavedEstimate);
                estimateTv.setText(totalEstimateText);

                if (totalOverBudget > 0) {
                    String budgetDiffFormatted = df.format(totalOverBudget);
                    String fullBudgetFormatted = "$" + budgetDiffFormatted;

                    overBudgetTv.setVisibility(View.VISIBLE);
                    overBudgetPrice.setVisibility(View.VISIBLE);
                    overBudgetPrice.setText(fullBudgetFormatted);
                } else {
                    float newOverBudget = 0.0f;
                    String budgetDiffFormatted = df.format(newOverBudget);
                    String fullBudgetFormatted = "$" + budgetDiffFormatted;

                    overBudgetTv.setVisibility(View.GONE);
                    overBudgetPrice.setVisibility(View.GONE);
                    overBudgetPrice.setText(fullBudgetFormatted);
                }

//                if (totalSavedEstimate > budgetNum) {
//                    float budgetDiff = totalSavedEstimate - budgetNum;
//                    totalOverBudget += budgetDiff;
//




////
//                if ((currentOverBudgetNum >= 0) && (currentPinNum >= currentOverBudgetNum)) {
//                    float newOverBudget = 0.0f;
//                    String newOverBudgetText = String.format(Float.toString(newOverBudget), "%.2f");
//                    overBudgetPrice.setText(newOverBudgetText);
//                    overBudgetTv.setVisibility(View.GONE);
//                    overBudgetPrice.setVisibility(View.GONE);
//
//                } else if ((currentOverBudgetNum > 0) && (currentPinNum <= currentOverBudgetNum)) {
//                    float newOverBudget = currentOverBudgetNum - currentPinNum;
//                    String newOverBudgetText = String.format(Float.toString(newOverBudget), "%.2f");
//                    overBudgetPrice.setText(newOverBudgetText);

//                }


//                for (Pin pin : savedPinsList) {
//                    ImageView imageView = getEmptyImageView();
//                    TextView textView = getEmptyTextView();
//
//                    if ((textView != null) && (imageView != null)) {
//                        // set image
//                        Glide.with(ProjectBoardActivity.this)
//                                .load(pin.getImgUrl())
//                                .into(imageView);
//
//                        // set price
//                        String priceFormatted = "$" + pin.getPrice();
//                        textView.setText(priceFormatted);
//
//                    } else {
//                        Toast.makeText(ProjectBoardActivity.this, "Too many pins added",
//                                Toast.LENGTH_LONG).show();
//
//                        //TODO --- DON'T ADD TOO MAKE PINS, BC THIS WILL BE AN ERROR
//                        // MAKE SURE TO ONLY HAVE 6 PINS IN THE DB
//                    }
//
//                    Log.d(TAG, "onDataChange: " + savedPinsList.get(0).toString());
//                    Log.d(TAG, "onDataChange: " + pin.getImgUrl());
//                    Log.d(TAG, "onDataChange: " + pin.getId());
//                    Log.d(TAG, "onDataChange: " + pin.getPrice());
//                }

//                getInitialEstimate(savedPinsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProjectBoardActivity.this, "Oops, something went wrong:" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getInitialEstimate(List<Pin> savedPinsList) {
        float totalEstimate = 0.0f;

        for (Pin pin : savedPinsList) {
            Log.d(TAG, "getInitialEstimate: " + savedPinsList.get(0).toString());
            Log.d(TAG, "getInitialEstimate: " + pin.getPrice());
            Log.d(TAG, "getInitialEstimate: " + pin.getId());
            Log.d(TAG, "getInitialEstimate: " + pin.getImgUrl());
            String price = pin.getPrice();
            float priceNum = Float.parseFloat(price);
            totalEstimate += priceNum;
        }

        DecimalFormat df = new DecimalFormat("###.00");
        String totalEstimateFormatted = df.format(totalEstimate);

        estimateTv.setText(totalEstimateFormatted);

        if (totalEstimate > budgetNum) {
            float budgetDiff = totalEstimate - budgetNum;

            String budgetDiffFormatted = df.format(budgetDiff);
            String fullBudgetFormatted = "$" + budgetDiffFormatted;

            overBudgetTv.setVisibility(View.VISIBLE);
            overBudgetPrice.setVisibility(View.VISIBLE);
            overBudgetPrice.setText(fullBudgetFormatted);
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

        if ((currentOverBudgetNum >= 0) && (currentPinNum >= currentOverBudgetNum)) {
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


//    projectReference = FirebaseDatabase.getInstance().getReference()
//                .child("User").child(userId).child("Project");
//
//
//    private void deleteProject(Project project) {
//        String projectKey = project.getId();
//        Query mQuery = projectReference.orderByKey().equalTo(projectKey);
//
//        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                    ds.getRef().removeValue();
//                }
//                Toast.makeText(HomeActivity.this, "Project deleted successfully", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
//
//            }
//        });
//
//    }

    private void deletePin(String pinText) {
        pinReference = FirebaseDatabase.getInstance().getReference()
                .child("User").child(user.getUserId()).child("Project");

        String pinPriceWithoutSymbol = pinText.substring(1);

        Query pinQuery = pinReference.child(project.getId())
                .child("Pin").orderByChild("price").equalTo(pinPriceWithoutSymbol);

        Log.d(TAG, "deletePin: " + pinQuery);
        Log.d(TAG, "deletePin: " + pinPriceWithoutSymbol);

        pinQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProjectBoardActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    public void topLeftPinClick(View view) {
        ImageView imageView = (ImageView) view;
        TextView textView = findViewById(R.id.top_left_txt);

        String pinText = textView.getText().toString();
        deletePin(pinText);
//        getSavedPins();


//        if (textView.getText().toString().length() != 0) {
//            subtractEstimateCosts(textView);
//        }
////
//        imageView.setImageDrawable(null);
//        textView.setText("");
    }

    public void topRightPinClick(View view) {
        ImageView imageView = (ImageView) view;
        TextView textView = findViewById(R.id.top_right_txt);

//        if (textView.getText().toString().length() != 0) {
//            subtractEstimateCosts(textView);
//        }

        String pinText = textView.getText().toString();
        if (pinText.length() != 0) {
            deletePin(pinText);
        }
//        getSavedPins();

////
//        imageView.setImageDrawable(null);
//        textView.setText("");

    }

    public void centerPinClick(View view) {
        ImageView imageView = (ImageView) view;
        TextView textView = findViewById(R.id.center_txt);

//        if (textView.getText().toString().length() != 0) {
//            subtractEstimateCosts(textView);
//        }
        String pinText = textView.getText().toString();
        if (pinText.length() != 0) {
            deletePin(pinText);
        }


////
//        imageView.setImageDrawable(null);
//        textView.setText("");
//        getSavedPins();

    }

    public void bottomLeftPinClick(View view) {
        ImageView imageView = (ImageView) view;
        TextView textView = findViewById(R.id.bottom_left_txt);

//        if (textView.getText().toString().length() != 0) {
//            subtractEstimateCosts(textView);
//        }
        String pinText = textView.getText().toString();
        if (pinText.length() != 0) {
            deletePin(pinText);
        }


////
//        imageView.setImageDrawable(null);
//        textView.setText("");

//        getSavedPins();

    }

    public void bottomRightPinClick(View view) {
        ImageView imageView = (ImageView) view;
        TextView textView = findViewById(R.id.bottom_right_txt);

//        if (textView.getText().toString().length() != 0) {
//            subtractEstimateCosts(textView);
//        }
        String pinText = textView.getText().toString();
        if (pinText.length() != 0) {
            deletePin(pinText);
        }
//        getSavedPins();

//
//        imageView.setImageDrawable(null);
//        textView.setText("");
    }

    public void bottomCenterPinClick(View view) {
        ImageView imageView = (ImageView) view;
        TextView textView = findViewById(R.id.bottom_center_txt);

//        if (textView.getText().toString().length() != 0) {
//            subtractEstimateCosts(textView);
//        }

        String pinText = textView.getText().toString();
        if (pinText.length() != 0) {
            deletePin(pinText);
        }
//        getSavedPins();

////
//        imageView.setImageDrawable(null);
//        textView.setText("");
    }


}




