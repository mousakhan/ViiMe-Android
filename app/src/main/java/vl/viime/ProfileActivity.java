package vl.viime;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
    private String mName;
    private String mEmail;
    private String mAge;
    private ImageView mProfileImageView;
    private EditText mAgeEditText;
    private Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Profile");

        final TextView mUsernameTextView = (TextView) findViewById(R.id.username);
        final EditText mNameEditText = (EditText) findViewById(R.id.name);
        final EditText mEmailEditText = (EditText) findViewById(R.id.email);
        mAgeEditText = (EditText) findViewById(R.id.birthday);
        final ListView mListView = (ListView) findViewById(R.id.rewards_list);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProfileImageView = (ImageView) findViewById(R.id.profile_picture);
        mUser = FirebaseAuth.getInstance().getCurrentUser();


        Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


//        DatabaseReference ref = mDatabase.child("users/" + mUser.getUid() + "/personal-deals");
//        // Attach a listener to read the data at the users node
//        ref.child("/personal-deals").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            // Getting the user information and setting it into the views
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
////                if (dataSnapshot != null) {
////                    String[] list = {"Item 1", "Item 2"};
//                    ArrayAdapter adapter = new ArrayAdapter(ProfileActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, list) {
//                        @Override
//                        public View getView(int position, View convertView, ViewGroup parent) {
//                            View view = super.getView(position, convertView, parent);
//                            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
//                            TextView text2 = (TextView) view.findViewById(android.R.id.text2);
//
//                            text1.setText("Subitem 1");
//                            text2.setText("Subitem 2");
//                            return view;
//                        }
//                    };
//
//                    mListView.setAdapter((ListAdapter)adapter);
////
////                    mListView.setEmptyView(findViewById(R.id.empty));
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "Being cancelled");
//            }
//        });



        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new
                DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }

                };
        mAgeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    new DatePickerDialog(ProfileActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return false;
            }
        });


        String userId = mUser.getUid().toString();
        // If user id exists, then get all the data from backend
        if (!mUser.getUid().equals("") && mUser.getUid() != null) {
            DatabaseReference ref = mDatabase.child("users/" + userId);
            // Attach a listener to read the data at the users node
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                // Getting the user information and setting it into the views
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String username = (String) dataSnapshot.child("username").getValue();
                    mUsernameTextView.setText(username);
                    String name = (String) dataSnapshot.child("name").getValue();
                    mNameEditText.setText(name);
                    mName = name;
                    String email = (String) dataSnapshot.child("email").getValue();
                    mEmailEditText.setText(email);
                    mEmail = email;
                    String age = (String) dataSnapshot.child("age").getValue();
                    mAgeEditText.setText(age);
                    mAge = age;
                    String profile = (String) dataSnapshot.child("profile").getValue();
                    // Make sure profile exists, then set it to the imageview
                    if (profile != null && !profile.equals("")) {
                        Glide.with(ProfileActivity.this)
                                .load(profile)
                                .dontAnimate()
                                .placeholder(R.drawable.empty_profile)
                                .into(mProfileImageView);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "Being cancelled");
                }
            });
        }

        // When profile clicked, show dialog to choose gallery or camera
        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup())
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                if (r.getError() == null) {
                                    // Set bitmap then upload file to Firebase
                                    mProfileImageView.setImageBitmap(r.getBitmap());
                                    uploadFile();
                                } else {

                                }
                            }
                        }).show(ProfileActivity.this);

            }
        });

        mNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // If clicking out of name field, and not into age or email, then hide keyboard
                if (!hasFocus && !mAgeEditText.hasFocus() && !mEmailEditText.hasFocus()) {
                    hideKeyboard(v);
                    String userId = mUser.getUid().toString();
                    // If name has changed, then update it in Firebase
                    if (mUser != null && !userId.equals("") && !mNameEditText.getText().toString().equals(mName)) {
                        DatabaseReference ref = mDatabase.child("users/" + userId + "/name");
                        ref.setValue(mNameEditText.getText().toString());
                        mName = mNameEditText.getText().toString();
                        Toast.makeText(ProfileActivity.this, R.string.name_updated,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        mAgeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // If clicking out of age field, and not into name or email, then hide keyboard
                if (!hasFocus && !mNameEditText.hasFocus() && !mEmailEditText.hasFocus()) {
                    hideKeyboard(v);
                    String userId = mUser.getUid().toString();
                    // If age has changed, then update it in Firebase
                    if (mUser != null && !userId.equals("") && !mAgeEditText.getText().toString().equals(mAge)) {
                        DatabaseReference ref = mDatabase.child("users/" + userId + "/age");
                        ref.setValue(mAgeEditText.getText().toString());
                        mAge = mAgeEditText.getText().toString();
                        Toast.makeText(ProfileActivity.this, R.string.age_updated,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mEmailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // If clicking out of email field, and not into name or age, then hide keyboard
                if (!hasFocus && !mNameEditText.hasFocus() && !mAgeEditText.hasFocus()) {
                    hideKeyboard(v);
                    String userId = mUser.getUid().toString();
                    // If email has changed, then update it in Firebase
                    if (mUser != null && !userId.equals("") && !mEmailEditText.getText().toString().equals(mEmail)) {
                        DatabaseReference ref = mDatabase.child("users/" + userId + "/email");
                        ref.setValue(mEmailEditText.getText().toString());
                        mEmail = mEmailEditText.getText().toString();
                        Toast.makeText(ProfileActivity.this, R.string.email_updated,
                                Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // This function will upload the file to FIrebase Storage
    private void uploadFile() {
        FirebaseStorage storage = FirebaseStorage.getInstance();


        // Create a reference to "mountains.jpg"
        StorageReference storageRef = storage.getReference().child("profile/" + mUser.getUid());

        // Let the user know we're updating their profile
        Toast.makeText(ProfileActivity.this, R.string.profile_updated,
                Toast.LENGTH_SHORT).show();

        // Get the data from an ImageView as bytes
        mProfileImageView.setDrawingCacheEnabled(true);
        mProfileImageView.buildDrawingCache();
        Bitmap bitmap = mProfileImageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ProfileActivity.this, R.string.profile_update_fail,
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                String userId = mUser.getUid().toString();
                Toast.makeText(ProfileActivity.this, R.string.profile_update_success,
                        Toast.LENGTH_SHORT).show();
                DatabaseReference ref = mDatabase.child("users/" + userId + "/profile");
                ref.setValue(downloadUrl.toString());
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                // Clicking help button
                CharSequence options[] = new CharSequence[] {"Contact Us", "Rate Our App", "Exit"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Thank you for using ViiMe! Let us know how we can improve.");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri data = Uri.parse("mailto:"
                                    + "support@viime.ca"
                                    + "?subject=" + "ViiMe Feedback" + "&body=" + "");
                            intent.setData(data);
                            startActivity(intent);
                        }

                        if (which == 1) {
                            final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                            final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

                            if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0)
                            {
                                startActivity(rateAppIntent);
                            } else
                            {
                                Toast.makeText(ProfileActivity.this, "An error has occured. Please try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (which == 2) {
                            dialog.cancel();
                        }
                        // the user clicked on colors[which]
                    }
                });
                builder.show();
                return super.onOptionsItemSelected(item);
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu,menu);
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
            }
        }

        return true;
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mAgeEditText.setText(sdf.format(myCalendar.getTime()));
    }


}
