package com.example.katrina.thedecuratorbeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pinterest.android.pdk.PDKPin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class PinsRvAdapter extends RecyclerView.Adapter<PinsRvAdapter.PinsViewHolder> {

    private static final String TAG = "PinsRvAdapter";

    private Context mContext;
    private List<PDKPin> pinList;
    private String pinName;
    private String pinPrice;
    private String productMetadata;


    public PinsRvAdapter(Context c, List<PDKPin> p) {
        mContext = c;
        pinList = p;
    }

    @NonNull
    @Override
    public PinsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = (LayoutInflater.from(mContext).inflate(R.layout.fragment_pins, viewGroup, false));
        return new PinsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PinsViewHolder viewHolder, final int position) {

        // Set pinned product image
        Glide.with(mContext)
                .asBitmap()
                .load(pinList.get(position).getImageUrl())
                .into(viewHolder.pinImage);


        // Set pinned product title + cost
        productMetadata = pinList.get(position).getMetadata();

        try {
            JSONObject reader = new JSONObject(productMetadata);
            JSONObject pObjectProduct = reader.getJSONObject("product");

            // Set title
            pinName = pObjectProduct.getString("name");
            viewHolder.pinTitle.setText(pinName);

            Log.d(TAG, "onBindViewHolder: JSON PIN NAME " + pinName);

            // Set cost
            JSONObject pObjectOffer = pObjectProduct.getJSONObject("offer");
            pinPrice = pObjectOffer.getString("price");
            viewHolder.pinCost.setText(pinPrice);

            Log.d(TAG, "onBindViewHolder: JSON PIN COST " + pinPrice);

        } catch (JSONException e) {
            String noTitle = "No title";
            String noCost = "No cost";
            viewHolder.pinTitle.setText(noTitle);
            viewHolder.pinCost.setText(noCost);
            e.printStackTrace();
        }


        viewHolder.pinImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                productMetadata = pinList.get(position).getMetadata();

                try {
                    JSONObject reader = new JSONObject(productMetadata);
                    JSONObject pObjectProduct = reader.getJSONObject("product");

                    // Set title
                    pinName = pObjectProduct.getString("name");
                    Log.d(TAG, "onClick: JSON PIN NAME " + pinName);

                    // Set cost
                    JSONObject pObjectOffer = pObjectProduct.getJSONObject("offer");
                    pinPrice = pObjectOffer.getString("price");

                    Log.d(TAG, "onClick: JSON PIN COST " + pinPrice);

                    Log.d(TAG, "onClick: JSON:" + pObjectProduct.toString());

                    Toast.makeText(mContext, "Name:" + pinName + " " + "Price: " + pinPrice, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return pinList.size();
    }

    class PinsViewHolder extends RecyclerView.ViewHolder {

        CircleImageView pinImage;
        TextView pinTitle;
        TextView pinCost;


        public PinsViewHolder(@NonNull View itemView) {
            super(itemView);
            pinImage = itemView.findViewById(R.id.pin_img);
            pinTitle = itemView.findViewById(R.id.pin_title);
            pinCost = itemView.findViewById(R.id.pin_cost);

        }
    }


}
