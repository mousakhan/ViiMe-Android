package vl.viime;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RedemptionActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String m_Text = "";
    private Location currLocation = null;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redemption);

        getSupportActionBar().setTitle("Deal Information");

        Intent intent = getIntent();

        final String code = intent.getStringExtra("redemptionCode");

        TextView dealTitle = (TextView) findViewById(R.id.deal_title);
        dealTitle.setText(intent.getStringExtra("title"));

        String validity = intent.getStringExtra("validTo");
        TextView dealValidity = (TextView) findViewById(R.id.deal_validity);
        dealValidity.setText(intent.getStringExtra("shortDescription"));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date parsed = format.parse(validity.toString());
            System.out.println(parsed);

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(parsed);
            int year = calendar.get(Calendar.YEAR);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR);
            int min = calendar.get(Calendar.MINUTE);
            int isAM = calendar.get(Calendar.AM_PM);


            String month = new SimpleDateFormat("MMMM").format(parsed);

            dealValidity.setText(intent.getStringExtra("shortDescription") + " Deal valid until " + month + " " + day + ", " + year);
        } catch (ParseException e) {
            //Handle exception here, most of the time you will just log it.
            e.printStackTrace();
        }


        final int numberOfPeopleRequired = Integer.parseInt(intent.getStringExtra("numberOfPeople"));


        TextView numberOfPeopleToBringText = (TextView) findViewById(R.id.number_of_people_text);
        numberOfPeopleToBringText.setText("Bring out " + (numberOfPeopleRequired - 1) + " friends to redeem this deal!");


        // Initialize contacts
        final ArrayList<User> users = new ArrayList<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final FirebaseUser user = mAuth.getCurrentUser();
        System.out.println(user.getUid());
        DatabaseReference ref = mDatabase.child("users/" + user.getUid());
        final User currentUser = new User();
        // Attach a listener to read the data at the users node

        // Create adapter passing in the sample user data


        final RecyclerView myList = (RecyclerView) findViewById(R.id.number_of_people_view);
        // Attach the adapter to the recyclerview to populate items


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("I HEREEE");
                currentUser.username = (String) dataSnapshot.child("username").getValue().toString();
                currentUser.profile = (String) dataSnapshot.child("profile").getValue().toString();
                users.add(currentUser);
                for (int i = 1; i < numberOfPeopleRequired; i++) {
                    User user = new User();
                    user.username = "?";
                    user.profile = "";
                    users.add(user);
                }
                UserAdapter adapter = new UserAdapter(RedemptionActivity.this, users);
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(RedemptionActivity.this, LinearLayoutManager.HORIZONTAL, false);
                myList.setAdapter(adapter);
                myList.setHasFixedSize(true);
                myList.setLayoutManager(layoutManager);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button redeem = (Button) findViewById(R.id.redemption_button);
        redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RedemptionActivity.this);
                builder.setTitle("Please enter the redemption code");

// Set up the input
                final EditText input = new EditText(RedemptionActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("Redeem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();

                        if (!m_Text.equals(code)) {
                            Toast.makeText(RedemptionActivity.this, R.string.wrong_redemption_code,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }


                        if (ContextCompat.checkSelfPermission(RedemptionActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission(RedemptionActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                        PackageManager.PERMISSION_GRANTED) {
                            DatabaseReference redemptionRef = mDatabase.child("redemptions");
                            LocationManager lm = (LocationManager)getSystemService(RedemptionActivity.LOCATION_SERVICE);
                            LocationListener locationListenerGPS =new LocationListener() {
                                @Override
                                public void onLocationChanged(android.location.Location location) {
                                    currLocation = location;
                                }

                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {

                                }

                                @Override
                                public void onProviderEnabled(String provider) {

                                }

                                @Override
                                public void onProviderDisabled(String provider) {

                                }
                            };

                            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListenerGPS);

                            Location redemptionLocation = null;
                            try {
                                redemptionLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            } catch (SecurityException e) {
                                System.out.println(e.getLocalizedMessage());
                            }
                            if (redemptionLocation != null) {
                                double longitude = redemptionLocation.getLongitude();
                                double latitude = redemptionLocation.getLatitude();
                                lm.removeUpdates(locationListenerGPS);
                                Map newRedemption = new HashMap();
                                newRedemption.put("latitude", latitude);
                                newRedemption.put("longitude", longitude);
                                newRedemption.put("redeemed", ServerValue.TIMESTAMP);

                                mDatabase.child("redemptions").push().setValue(newRedemption);

                                Intent intent = new Intent(RedemptionActivity.this, HomeActivity.class);
                                intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(RedemptionActivity.this, R.string.redemption_successful,
                                        Toast.LENGTH_SHORT).show();
                            } else {

                            }Toast.makeText(RedemptionActivity.this, R.string.redemption_unsuccessful,
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RedemptionActivity.this);
                            builder.setTitle(R.string.gps_not_found_title);  // GPS not found
                            builder.setMessage(R.string.gps_not_found_message); // Want to enable?
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(RedemptionActivity.this, new String[] {
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                                    android.Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSIONS_REQUEST_LOCATION);
                                }
                            });
                            builder.setNegativeButton(R.string.no, null);
                            builder.create().show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();





            }
        });
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    public class RedemptionLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            currLocation = location;
        }

        @Override
        public void onProviderEnabled(String provider) {


        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

}



