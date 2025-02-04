package vl.viime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class FriendsActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private static final String TAG = "FriendsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user = mAuth.getCurrentUser();

        final ListView friendsListView = (ListView) findViewById(R.id.friends_list);

        getSupportActionBar().setTitle("Friends List");

//        final List<Map<String,String>> friends = new ArrayList<Map<String,String>>();
//

        if (user != null && !user.getUid().equals("")) {
            mDatabase.child("users/" + user.getUid() + "/friends").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final long count = dataSnapshot.getChildrenCount();
                    final List<String> friends = new ArrayList<String>();
                    final List<String> profilePictures = new ArrayList<String>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mDatabase.child("users/" + snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userDatasnapshot) {
                                    String username = (String) userDatasnapshot.child("username").getValue();
                                    String id = (String) userDatasnapshot.child("id").getValue();
                                    String profile = (String) userDatasnapshot.child("profile").getValue();
                                    Map<String, String> map = new HashMap<>();
                                    map.put("username", username);
                                    map.put("id", id);

                                    friends.add(username);
                                    profilePictures.add(profile);

                                    // If all the users have been fetched, then show the view
                                    if(count == friends.size()) {
                                        FriendList friendsArrayAdapter = new
                                                FriendList(FriendsActivity.this, friends.toArray(new String[friends.size()]), profilePictures.toArray(new String[profilePictures.size()]));
                                        friendsListView.setAdapter(friendsArrayAdapter);
                                    }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



    }
}
