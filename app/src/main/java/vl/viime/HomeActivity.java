package vl.viime;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ArrayList<Venue> venues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final GridView gridView = (GridView)findViewById(R.id.gridview);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("venue");
        // Attach a listener to read the data at the users node
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                venues = new ArrayList<Venue>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Venue venue = new Venue();
                    venue.name = (String)snapshot.child("name").getValue();
                    venue.address = (String)snapshot.child("address").getValue();
                    venue.code = (String)snapshot.child("code").getValue();
                    venue.id = (String)snapshot.child("id").getValue();
                    venue.description = (String)snapshot.child("description").getValue();
                    venue.price = (String)snapshot.child("price").getValue();
                    venue.city = (String)snapshot.child("city").getValue();
                    venue.cuisine = (String)snapshot.child("cuisine").getValue();
                    venue.logo = (String)snapshot.child("logo").getValue();
                    venue.website = (String)snapshot.child("website").getValue();
                    venue.type = (String)snapshot.child("type").getValue();
                    venue.number = (String)snapshot.child("number").getValue();
                    venue.latitude = (double)snapshot.child("lat").getValue();
                    venue.longitude = (double)snapshot.child("long").getValue();
                    venue.numberOfDeals = snapshot.child("deals").getChildrenCount();
                    venue.deals = new ArrayList<String>();
                    for (String key : ( (Map<String,?>)snapshot.child("deals").getValue()).keySet()) {
                        // This should never be the case, but just in case
                        if (key != null) {
                            venue.deals.add(key);
                        }
                    }

                    venues.add(venue);
                    if (count == venues.size()) {
                        VenuesAdapter venuesAdapter = new VenuesAdapter(HomeActivity.this, venues);
                        gridView.setAdapter(venuesAdapter);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                System.out.println(venues.get(position).toString());
                Intent i = new Intent(HomeActivity.this, DealActivity.class);
                i.putExtra("venue", venues.get(position));
                startActivity(i);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_friends:
                // Clicking into friends list
                Intent friendsIntent = new Intent(HomeActivity.this, FriendsActivity.class);
                HomeActivity.this.startActivity(friendsIntent);
                return true;

            case R.id.action_profile:
                // Clicking profile page
                Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                HomeActivity.this.startActivity(profileIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}
