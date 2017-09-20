package vl.viime;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import java.io.ByteArrayOutputStream;


public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
    private String mName;
    private String mEmail;
    private String mAge;
    private ImageView mProfileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final TextView mUsernameTextView = (TextView) findViewById(R.id.username);
        final EditText mNameEditText = (EditText) findViewById(R.id.name);
        final EditText mEmailEditText = (EditText) findViewById(R.id.email);
        final EditText mAgeEditText = (EditText) findViewById(R.id.birthday);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProfileImageView = (ImageView) findViewById(R.id.profile_picture);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        String userId = mUser.getUid().toString();
        Log.d(TAG, userId);
        // If user id exists, then get all the data from backend
        if (!userId.equals("") && userId != null) {
            DatabaseReference ref = mDatabase.child("users/" + userId);
            Log.d(TAG, ref.toString());
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
                    if (!profile.equals("") && profile != null) {
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
                PickImageDialog.build(new PickSetup()).show(ProfileActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the code is image capture, then set the profile image to it
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mProfileImageView.setImageBitmap(imageBitmap);
            uploadFile(imageBitmap);
        }
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // This function will upload the file to FIrebase Storage
    private void uploadFile(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Getting the location of where to store the image on Firebase
        StorageReference storageRef = storage.getReferenceFromUrl("profile/" + mUser.getUid());

        // Setting up the image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        // Let the user know we're updating their profile
        Toast.makeText(ProfileActivity.this, R.string.profile_updated,
                Toast.LENGTH_SHORT).show();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // If the upload fails, let them know
                Toast.makeText(ProfileActivity.this, R.string.profile_update_fail,
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // If it's successful, let them know it's done
                Toast.makeText(ProfileActivity.this, R.string.profile_update_success,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

}
