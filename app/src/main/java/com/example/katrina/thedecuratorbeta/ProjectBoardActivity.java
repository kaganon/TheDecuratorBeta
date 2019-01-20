package com.example.katrina.thedecuratorbeta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    private PDKCallback myPinsCallback;
    private PDKResponse myPinsResponse;
    private PDKPin pinItem;
    private List<PDKPin> pinList;
    private PinsAdapter pinAdapter;
    private boolean _loading = false;
    private static final String PIN_FIELDS = "id,link,creator,image,counts,note,created_at,board,metadata";


    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<Object> metadata = new ArrayList<>();


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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false );
        RecyclerView pinRecyclerview = findViewById(R.id.pins_recycler_view);
        pinRecyclerview.setLayoutManager(layoutManager);

        PinsRvAdapter adapter = new PinsRvAdapter(this, pinList);
        pinRecyclerview.setAdapter(adapter);
    }

    private class PinsAdapter extends BaseAdapter {

        private List<PDKPin> _pinList;
        private Context _context;
        public PinsAdapter(Context c) {
            _context = c;
        }

        public void setPinList(List<PDKPin> list) {
            if (_pinList == null) _pinList = new ArrayList<PDKPin>();
            if (list == null) _pinList.clear();
            else _pinList.addAll(list);
            notifyDataSetChanged();
        }

        public List<PDKPin> getPinList() {
            return _pinList;
        }


        @Override
        public int getCount() {
            return _pinList == null ? 0 : _pinList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderItem viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
                convertView = inflater.inflate(R.layout.fragment_pins, parent, false);

                viewHolder = new ViewHolderItem();
                viewHolder.pinTitle = (TextView) convertView.findViewById(R.id.pin_title);
                viewHolder.pinImage = (CircleImageView) convertView.findViewById(R.id.pin_img);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderItem) convertView.getTag();
            }

            PDKPin pinItem = _pinList.get(position);

            if (pinItem != null) {
                viewHolder.pinTitle.setText(pinItem.getMetadata());
                Picasso.with(_context.getApplicationContext()).load(pinItem.getImageUrl()).into(viewHolder.pinImage);
            }

            return convertView;

        }

        private class ViewHolderItem {
            CircleImageView pinImage;
            TextView pinTitle;
        }
    }











}
