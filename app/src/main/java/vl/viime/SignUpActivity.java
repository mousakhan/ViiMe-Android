package vl.viime;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText email = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText name = (EditText) findViewById(R.id.name);
        final EditText confirmPassword = (EditText) findViewById(R.id.confirmpassword);
        final EditText username = (EditText) findViewById(R.id.username);
        final Button signUpButton = (Button) findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().matches("") && username.getText() != null) {
                    Toast.makeText(SignUpActivity.this, "Please enter a username",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                if (name.getText().toString().matches("") && name.getText() != null) {
                    Toast.makeText(SignUpActivity.this, "Please enter a name",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.getText().toString().matches("") && email.getText() != null) {
                    Toast.makeText(SignUpActivity.this, "Please enter an email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.getText().toString().matches("") && password.getText() != null) {
                    Toast.makeText(SignUpActivity.this, "Please enter a password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (confirmPassword.getText().toString().matches("") && confirmPassword.getText() != null) {
                    Toast.makeText(SignUpActivity.this, "Please confirm your password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!isEmailValid(email.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Please enter a valid email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }



                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference reference = database.getReference();

                Query query = reference.child("users").orderByChild("username").equalTo(username.getText().toString());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Toast.makeText(SignUpActivity.this, "The username is already taken",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Map<String, String> data= new HashMap<String, String>();
                            data.put("username", username.getText().toString());
                            data.put("name", name.getText().toString());
                            data.put("email", email.getText().toString());

                            String key = reference.child("users").getKey();
                            data.put("id", key);
                            reference.child("users").child(key).setValue(data);

                            return;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        // Change size of password icon
        password.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final float scale = getResources().getDisplayMetrics().density;
                        int dpWidthInPx = (int) (20 * scale);
                        int dpHeightInPx = (int) (20 * scale);

                        Drawable img = ContextCompat.getDrawable(SignUpActivity.this, R.drawable.password);
                        img.setBounds(0, 0, dpWidthInPx, dpHeightInPx);
                        password.setCompoundDrawables(img, null, null, null);
                        password.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        // Change size of email icon
        email.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final float scale = getResources().getDisplayMetrics().density;
                        int dpWidthInPx = (int) (20 * scale);
                        int dpHeightInPx = (int) (20 * scale);

                        Drawable img = ContextCompat.getDrawable(SignUpActivity.this, R.drawable.email);
                        img.setBounds(0, 0, dpWidthInPx, dpHeightInPx);
                        email.setCompoundDrawables(img, null, null, null);
                        email.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        // Change size of confirm password icon
        confirmPassword.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final float scale = getResources().getDisplayMetrics().density;
                        int dpWidthInPx = (int) (20 * scale);
                        int dpHeightInPx = (int) (20 * scale);

                        Drawable img = ContextCompat.getDrawable(SignUpActivity.this, R.drawable.password);
                        img.setBounds(0, 0, dpWidthInPx, dpHeightInPx);
                        confirmPassword.setCompoundDrawables(img, null, null, null);
                        confirmPassword.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        // Change size of username icon
        username.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final float scale = getResources().getDisplayMetrics().density;
                        int dpWidthInPx = (int) (20 * scale);
                        int dpHeightInPx = (int) (20 * scale);

                        Drawable img = ContextCompat.getDrawable(SignUpActivity.this, R.drawable.username);
                        img.setBounds(0, 0, dpWidthInPx, dpHeightInPx);
                        username.setCompoundDrawables(img, null, null, null);
                        username.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        // Change size of name icon
        name.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final float scale = getResources().getDisplayMetrics().density;
                        int dpWidthInPx = (int) (20 * scale);
                        int dpHeightInPx = (int) (20 * scale);

                        Drawable img = ContextCompat.getDrawable(SignUpActivity.this, R.drawable.user);
                        img.setBounds(0, 0, dpWidthInPx, dpHeightInPx);
                        name.setCompoundDrawables(img, null, null, null);
                        name.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });


    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isUsernameValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isUsernameUnique(CharSequence username) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        boolean isUsernameUnique = true;


        Query query = reference.child("users").orderByChild("username").equalTo(username.toString());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(SignUpActivity.this, "The username is already taken",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return false;
    }
}
