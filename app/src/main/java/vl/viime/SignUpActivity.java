package vl.viime;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.common.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReference = mDatabase.getReference();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();
        
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);
        password.setTransformationMethod(new PasswordTransformationMethod());
        final EditText name = (EditText) findViewById(R.id.name);
        final EditText confirmPassword = (EditText) findViewById(R.id.confirmpassword);
        confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
        final EditText username = (EditText) findViewById(R.id.username);
        final Button signUpButton = (Button) findViewById(R.id.signUpButton);


        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !email.hasFocus() && !confirmPassword.hasFocus() && !username.hasFocus() && !password.hasFocus()) {
                    hideKeyboard(v);
                }
            }
        });


        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !email.hasFocus() && !confirmPassword.hasFocus() && !name.hasFocus() && !password.hasFocus()) {
                    hideKeyboard(v);
                }
            }
        });

        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !email.hasFocus() && !name.hasFocus() && !username.hasFocus() && !password.hasFocus()) {
                    hideKeyboard(v);
                }
            }
        });


        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !password.hasFocus() && !confirmPassword.hasFocus() && !username.hasFocus() && !name.hasFocus()) {
                    hideKeyboard(v);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !email.hasFocus() && !confirmPassword.hasFocus() && !username.hasFocus() && !name.hasFocus()) {
                    hideKeyboard(v);
                }
            }
        });

        confirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information

                                        // Add user to users node
                                        Map<String, String> data = new HashMap<String, String>();
                                        data.put("username", username.getText().toString());
                                        data.put("name", name.getText().toString());
                                        data.put("email", email.getText().toString());
                                        data.put("age", "");
                                        data.put("profile", "");
                                        String key = mReference.child("users").push().getKey();
                                        data.put("id", key);
                                        mReference.child("users").child(key).setValue(data);

                                        Toast.makeText(SignUpActivity.this, "Sign up successful. Email verification sent.",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        mAuth.getCurrentUser().sendEmailVerification();
                                        return;
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpActivity.this, "Sign up failed. " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            });

                    return true;
                }
                else {
                    return false;
                }
            }
        });


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

                if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Please ensure the passwords are the same",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!isUsernameValid(username.getText())) {
                    Toast.makeText(SignUpActivity.this, "The username cannot start or end with -, _, . or a number, can contain no white spaces, emojis, or special characters, must be 3-15 characters long and all in lowercase.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                Query query = mReference.child("users").orderByChild("username").equalTo(username.getText().toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(SignUpActivity.this, "The username is already taken",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            final FirebaseAuth mAuth = FirebaseAuth.getInstance();

                            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information

                                                // Add user to users node
                                                Map<String, String> data = new HashMap<String, String>();
                                                data.put("username", username.getText().toString());
                                                data.put("name", name.getText().toString());
                                                data.put("email", email.getText().toString());
                                                data.put("age", "");
                                                data.put("profile", "");
                                                String key = mReference.child("users").push().getKey();
                                                data.put("id", key);
                                                mReference.child("users").child(key).setValue(data);

                                                Toast.makeText(SignUpActivity.this, "Sign up successful. Email verification sent.",
                                                        Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                mAuth.getCurrentUser().sendEmailVerification();
                                                Log.d(TAG, "createUserWithEmail:success");
                                                return;
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(SignUpActivity.this, "Sign up failed. " + task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    });


                            return;
                        }
                    };

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

    private boolean isUsernameValid(CharSequence username) {


        if (username.toString().length() < 3 ||  username.toString().length() > 15) {
            return false;
        }

        if (!username.toString().toLowerCase().equals(username.toString())) {
            Log.d(TAG, "Not all lowercase");
            return false;
        }

        String firstChar = username.toString().substring(0, 1);
        String lastChar = username.toString().substring(username.toString().length() - 1);


        Log.d(TAG, firstChar);
        Log.d(TAG, lastChar);

        // Check for whitespace in string
        if (containsWhiteSpace(username.toString())) {
            Log.d(TAG, "Whitespace");
            return false;
        }


        if (lastChar.equals(".") || lastChar.equals("_") || lastChar.equals("-") || firstChar.equals(".") || firstChar.equals("_") || firstChar.equals("-")) {
            Log.d(TAG, "Illegal chracter requirement");
            return false;
        }

        if (!username.toString().matches("^[a-zA-Z0-9_.-]+$")) {
            Log.d(TAG, "REGEX requirement");
            return false;
        }

        return true;
    }

    private static boolean containsWhiteSpace(final String testCode){
        if(testCode != null){
            for(int i = 0; i < testCode.length(); i++){
                if(Character.isWhitespace(testCode.charAt(i))){
                    return true;
                }
            }
        }
        return false;
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
