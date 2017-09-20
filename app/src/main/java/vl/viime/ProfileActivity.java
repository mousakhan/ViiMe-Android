package vl.viime;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private ImageView mProfileImageView;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String mName;
    private String mEmail;
    private String mAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        final TextView mUsernameTextView = (TextView) findViewById(R.id.username);
        final EditText mNameEditText = (EditText) findViewById(R.id.name);
        final EditText mEmailEditText = (EditText) findViewById(R.id.email);
        final EditText mAgeEditText = (EditText) findViewById(R.id.birthday);

        mProfileImageView = (ImageView) findViewById(R.id.profile_picture);


        Context context = this;
        PackageManager packageManager = context.getPackageManager();

            mProfileImageView.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    PickImageDialog.build(new PickSetup()).show(ProfileActivity.this);
                }
            });



        mNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !mAgeEditText.hasFocus() && !mEmailEditText.hasFocus()) {
                    hideKeyboard(v);
                    String userId = user.getUid().toString();
                    if (user != null && !userId.equals("") && !mNameEditText.getText().toString().equals(mName)) {
                        DatabaseReference ref = mDatabase.getReference("users/" + userId + "/name");
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
                if (!hasFocus && !mNameEditText.hasFocus() && !mEmailEditText.hasFocus()) {
                    hideKeyboard(v);
                    String userId = user.getUid().toString();
                    if (user != null && !userId.equals("") && !mAgeEditText.getText().toString().equals(mAge)) {
                        DatabaseReference ref = mDatabase.getReference("users/" + userId + "/age");
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
                if (!hasFocus && !mNameEditText.hasFocus() && !mAgeEditText.hasFocus()) {
                    hideKeyboard(v);
                    String userId = user.getUid().toString();
                    if (user != null && !userId.equals("") && !mEmailEditText.getText().toString().equals(mEmail)) {
                        DatabaseReference ref = mDatabase.getReference("users/" + userId + "/email");
                        ref.setValue(mEmailEditText.getText().toString());
                        mEmail = mEmailEditText.getText().toString();
                        Toast.makeText(ProfileActivity.this, R.string.email_updated,
                                Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        String userId = user.getUid().toString();
        System.out.println(userId);

        if (!userId.equals("") && userId != null) {
            DatabaseReference ref = mDatabase.getReference("users/" + userId);

            // Attach a listener to read the data at our posts reference
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
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
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private void uploadFile(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("profile/" + user.getUid());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        Toast.makeText(ProfileActivity.this, R.string.profile_updated,
                Toast.LENGTH_SHORT).show();

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
                Toast.makeText(ProfileActivity.this, R.string.profile_update_success,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

}
