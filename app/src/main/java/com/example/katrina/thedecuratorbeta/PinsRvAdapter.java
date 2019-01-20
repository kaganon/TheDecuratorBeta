package com.example.katrina.thedecuratorbeta;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pinterest.android.pdk.PDKPin;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PinsRvAdapter extends RecyclerView.Adapter<PinsRvAdapter.PinsViewHolder> {

    private static final String TAG = "PinsRvAdapter";

    private Context mContext;
    private List<PDKPin> pinList;
    private List<String> products;


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

        viewHolder.pinTitle.setText(pinList.get(position).getNote());
        viewHolder.pinCost.setText(pinList.get(position).getNote());

        Glide.with(mContext)
                .asBitmap()
                .load(pinList.get(position).getImageUrl())
                .into(viewHolder.pinImage);


        viewHolder.pinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productString = pinList.get(position).getMetadata();

                try {
                    JSONObject reader = new JSONObject(productString);
                    JSONObject productP = reader.getJSONObject("product");
                    String pinName = productP.getString("name");
                    Log.d(TAG, "onClick: JSON PIN NAME " + pinName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject reader = new JSONObject(productString);
                    JSONObject productP = reader.getJSONObject("product");
                    JSONObject pinPrice = productP.getJSONObject("offer");
                    String pinPrice2 = pinPrice.getString("price");

                    Log.d(TAG, "onClick: JSON PIN PRICE " + pinPrice2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }




                products = Arrays.asList(pinList.get(position).getMetadata().split(","));
                List<String> names;
                names = Arrays.asList(pinList.get(position).getMetadata().split("name"));
                List<String> prices;




                String product = products.get(0);
                String product2 = products.get(1);
                String product3 = products.get(2);
                String product4 = products.get(4);
                String name1 = names.get(1);



                Log.d(TAG, "onClick: first:" + product + "| ..second: " + product2);
                Log.d(TAG, "onClick: " + products.toString());
                Log.d(TAG, "onClick: third:" + product3 + "| fourth: " + product4);
                Log.d(TAG, "onClick: " + name1);



                Log.d(TAG, "onClick: " + pinList.get(position).getMetadata());
                Log.d(TAG, "onClick: " + pinList.get(position).getMetadata());
                Toast.makeText(mContext, pinList.get(position).getLink(), Toast.LENGTH_SHORT).show();


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
