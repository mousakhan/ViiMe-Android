package vl.viime;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.net.URLEncoder;
import java.util.ArrayList;

public class DealActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private Venue venue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);




        Intent intent = getIntent();
        venue = (Venue) intent.getSerializableExtra("venue");
        getSupportActionBar().setTitle(venue.name);

        TextView description = (TextView) findViewById(R.id.venue_description);
        description.setText(venue.description);
        final TextView number = (TextView) findViewById(R.id.phone_text);
        number.setText(venue.number);
        final ImageView numberIcon = (ImageView) findViewById(R.id.phone_icon);
        final TextView website = (TextView) findViewById(R.id.website_text);
        website.setText(venue.website);
        final ImageView websiteIcon = (ImageView) findViewById(R.id.website_icon);
        final TextView address = (TextView) findViewById(R.id.address_text);
        address.setText(venue.address);
        final ImageView addressIcon = (ImageView) findViewById(R.id.address_icon);


        TextView numberOfDealsText = (TextView) findViewById(R.id.number_of_deals);
        numberOfDealsText.setText(venue.numberOfDeals + " Deals Available");

        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number.getText().toString()));
                if (ContextCompat.checkSelfPermission(DealActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                }
            }
        });

        numberIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number.getText().toString()));
                if (ContextCompat.checkSelfPermission(DealActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                }
            }
        });

        websiteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = website.getText().toString();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = website.getText().toString();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });


        addressIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(String.format("geo:0,0?q=%s",
                                URLEncoder.encode(address.getText().toString())))));
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(String.format("geo:0,0?q=%s",
                                URLEncoder.encode(address.getText().toString())))));
            }
        });


        ImageView logo = (ImageView) findViewById(R.id.venue_logo);

        // 4
        Glide.with(DealActivity.this)
                .load(venue.logo)
                .dontAnimate()
                .into(logo);


        final ArrayList<Deal> deals = new ArrayList<Deal>();


        final LinearLayout layout = (LinearLayout) findViewById(R.id.deals_list);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("deal");
        // Attach a listener to read the data at the users node
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = (String) snapshot.child("id").getValue();
                    System.out.println("The id is" + id);
                    System.out.println("The deals are" + venue.deals.toString());
                    if (venue.deals.contains(id)) {
                        System.out.println("The id in here is ");
                        Deal deal = new Deal();
                        deal.title = (String) snapshot.child("title").getValue();
                        deal.numberOfPeople = (String) snapshot.child("number-of-people").getValue();
                        deal.shortDescription = (String) snapshot.child("short-description").getValue();
                        deal.recurringFrom = (String) snapshot.child("recurring-from").getValue();
                        deal.recurringTo = (String) snapshot.child("recurring-to").getValue();
                        deal.validTo = (String) snapshot.child("valid-to").getValue();
                        deal.validFrom = (String) snapshot.child("valid-from").getValue();
                        deal.venueId = (String) snapshot.child("venue-id").getValue();
                        deal.numberOfRedemptionsAllowed = (String) snapshot.child("num-redemptions").getValue();
                        deals.add(deal);

                        ArrayAdapter adapter = new ArrayAdapter(DealActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, deals) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                text1.setTextColor(getResources().getColor(R.color.white));
                                text1.setTextSize(12);
                                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                                text2.setTextColor(getResources().getColor(R.color.gray));
                                text2.setTextSize(10);
                                String title = deals.get(position).title.toString();
                                String shortDescription = deals.get(position).shortDescription.toString();
                                text1.setText(title);
                                text2.setText(shortDescription);
                                return view;
                            }
                        };

                        final int adapterCount = adapter.getCount();

                        if (adapterCount == venue.deals.size()) {
                            for (int i = 0; i < adapterCount; i++) {
                                View item = adapter.getView(i, null, null);
                                final int position = i;
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(DealActivity.this, RedemptionActivity.class);
                                        String title = deals.get(position).title.toString();
                                        String shortDescription = deals.get(position).shortDescription.toString();
                                        String validTo = deals.get(position).validTo.toString();
                                        String validFrom = deals.get(position).validFrom.toString();
                                        String recurringTo = deals.get(position).recurringTo.toString();
                                        String recurringFrom = deals.get(position).recurringFrom.toString();
                                        String numberOfRedemptionsAllowed = deals.get(position).numberOfRedemptionsAllowed.toString();
                                        String numberOfPeople = deals.get(position).numberOfPeople.toString();

                                        intent.putExtra("title", title);
                                        intent.putExtra("shortDescription", shortDescription);
                                        intent.putExtra("validTo", validTo);
                                        intent.putExtra("validFrom", validFrom);
                                        intent.putExtra("recurringTo", recurringTo);
                                        intent.putExtra("recurringFrom", recurringFrom);
                                        intent.putExtra("numberOfPeople", numberOfPeople);
                                        intent.putExtra("numberOfRedemptionsAllowed", numberOfRedemptionsAllowed);
                                        intent.putExtra("venue", venue.id);
                                        intent.putExtra("redemptionCode", venue.code);

                                        startActivity(intent);
                                    }
                                });

                                layout.addView(item);
                            }
                        }


                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
