package com.example.katrina.thedecuratorbeta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pinterest.android.pdk.PDKBoard;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.PDKUser;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.pinterest.android.pdk.Utils.log;

public class HomeActivity extends AppCompatActivity implements ProjectDialog.ProjectDialogListener, ProjectFbAdapter.OnProjectListener {

    private static final String TAG = "HomeActivity";


    // boards:
    private ImageView boardImage;
    private PDKCallback myBoardsCallback;
    private PDKResponse myBoardsResponse;
    private GridView _gridView;
    private boolean _loading = false;
    private BoardsAdapter _boardsAdapter;
    private static final String BOARD_FIELDS = "id,name,description,creator,image,counts,created_at";

    private static boolean DEBUG = true;

    private final String USER_FIELDS = "id,image,counts,created_at,first_name,last_name,bio";
    PDKUser user;
    PDKBoard pdkBoard;
    private TextView userName;
    private ImageView profileImage;

    // Adding new project
    private TextView projectName;
    private TextView projectBudget;
    private Button button;

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


        // This is just for testing
        projectName = (TextView) findViewById(R.id.project_name);
        projectBudget = (TextView) findViewById(R.id.project_budget);

        // Adding new projects
        button = (Button) findViewById(R.id.add_project_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });


        _boardsAdapter = new BoardsAdapter(this);
        _gridView = (GridView) findViewById(R.id.grid_view);

////        _listView.setAdapter(_boardsAdapter);
//        _gridView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v,
//                                            ContextMenu.ContextMenuInfo menuInfo) {
//                MenuInflater inflater = getMenuInflater();
//                inflater.inflate(R.menu.context_menu_boards, menu);
//            }
//        });

        _gridView.setAdapter(_boardsAdapter);


        myBoardsCallback = new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                _loading = false;
                myBoardsResponse = response;
                _boardsAdapter.setBoardList(response.getBoardList());
            }

            @Override
            public void onFailure(PDKException exception) {
                _loading = false;
                Log.e(getClass().getName(), exception.getDetailMessage());
            }
        };
        _loading = true;

        getMe();

    }

    public void openDialog() {
        ProjectDialog projectDialog = new ProjectDialog();
        projectDialog.show(getSupportFragmentManager(), "project dialog");
    }

    @Override
    public void applyTexts(String name, String budget) {
        projectName.setText(name);
        projectBudget.setText(budget);
    }

    private void setUser() {
        userName.setText("Welcome, " + user.getFirstName() + " " + user.getLastName() + user.getUid());
        Picasso.with(this).load(user.getImageUrl()).into(profileImage);
    }

    private void getProjects() {
        String userId = user.getUid();

        recyclerView = (RecyclerView) findViewById(R.id.list_projects);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

                projectFbAdapter = new ProjectFbAdapter(HomeActivity.this, projectList, HomeActivity.this);
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

                Log.d(TAG, "ds-userID:" + userId);

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(userId)) {
                        existingUser = true;
                    }

                    Log.d(TAG, "ds-key:" + ds.getKey());
                    Log.d(TAG, "ds-userID - inside loop:" + userId);
                    Log.d(TAG, "ds-value User class:" + ds.getValue());
                    Log.d(TAG, "ds-value User class userId:" + ds.getValue(User.class).getUserId());
                    Log.d(TAG, "ds-children" + dataSnapshot.getChildren().toString());
                    Log.d(TAG, "ds-Existing User - in loop:" + existingUser);
                }

                Log.d(TAG, "ds-Existing User - outside loop:" + existingUser);

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


    private void initializeDisplayProject() {
        final RecyclerView recyclerProjects = (RecyclerView) findViewById(R.id.list_projects);
        final LinearLayoutManager projectsLayoutManager = new LinearLayoutManager(this);
        recyclerProjects.setLayoutManager(projectsLayoutManager);
    }


    private void fetchBoards() {
        _boardsAdapter.setBoardList(null);
        PDKClient.getInstance().getMyBoards(BOARD_FIELDS, myBoardsCallback);
    }

    private void loadNext() {
        if (!_loading && myBoardsResponse.hasNext()) {
            _loading = true;
            myBoardsResponse.loadNext(myBoardsCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        fetchBoards();
    }

    @Override
    public void onProjectClick(int position) {
        // NAVIGATE TO THE NEW ACTIVITY
        Log.d(TAG, "onProjectClick: CLICKED!");
        Intent intent = new Intent(this, ProjectBoardActivity.class);
        intent.putExtra("Project", projectList.get(position));
        startActivity(intent);

    }

    private class BoardsAdapter extends BaseAdapter {

        private List<PDKBoard> _boardList;
        private Context _context;

        public BoardsAdapter(Context context) {
            _context = context;
        }

        public void setBoardList(List<PDKBoard> list) {
            if (_boardList == null) _boardList = new ArrayList<PDKBoard>();
            if (list == null) _boardList.clear();
            else _boardList.addAll(list);
            notifyDataSetChanged();
        }


        public List<PDKBoard> getBoardList() {
            return _boardList;
        }

        @Override
        public int getCount() {
            return _boardList == null ? 0 : _boardList.size();
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

            //load more pins if about to reach end of list
            if (_boardList.size() - position < 5) {
                loadNext();
            }

            if (convertView == null) {
                LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_item_board, parent, false);

                viewHolder = new ViewHolderItem();
                viewHolder.boardName = (TextView) convertView.findViewById(R.id.board_title);
                viewHolder.boardImage = (ImageView) convertView.findViewById(R.id.board_img);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolderItem) convertView.getTag();
            }

            PDKBoard boardItem = _boardList.get(position);
            if (boardItem != null) {
                viewHolder.boardName.setText(boardItem.getName());
                Picasso.with(_context.getApplicationContext()).load(boardItem.getImageUrl()).into(viewHolder.boardImage);
            }

            return convertView;

        }

        private class ViewHolderItem {
            TextView boardName;
            ImageView boardImage;
        }

    }


}
