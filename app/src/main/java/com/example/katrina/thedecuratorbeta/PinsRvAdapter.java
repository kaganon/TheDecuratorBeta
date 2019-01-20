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

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PinsRvAdapter extends RecyclerView.Adapter<PinsRvAdapter.PinsViewHolder> {

    private static final String TAG = "PinsRvAdapter";

    private Context mContext;
    private List<PDKPin> pinList;


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
        Log.d(TAG, "onBindViewHolder: called");

        viewHolder.pinTitle.setText(pinList.get(position).getNote());

        Glide.with(mContext)
                .asBitmap()
                .load(pinList.get(position).getImageUrl())
                .into(viewHolder.pinImage);


        viewHolder.pinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: CLicked on a pin" + pinList.get(position).getMetadata());
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

        public PinsViewHolder(@NonNull View itemView) {
            super(itemView);
            pinImage = itemView.findViewById(R.id.pin_img);
            pinTitle = itemView.findViewById(R.id.pin_title);

        }
    }




}
