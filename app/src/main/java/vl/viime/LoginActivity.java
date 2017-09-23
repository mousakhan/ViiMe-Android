package vl.viime;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private Button signInButton;
    private CallbackManager callbackManager;
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
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_profile:
                Intent myIntent = new Intent(LoginActivity.this, ProfileActivity.class);
                LoginActivity.this.startActivity(myIntent);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        final AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                        mAuth.signInWithCredential(credential)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users/" + user.getUid());
                                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.child("username").exists()) {
                                                        Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                                        LoginActivity.this.startActivity(myIntent);
                                                    } else {
                                                        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                                                        final EditText edittext = new EditText(LoginActivity.this);
                                                        alert.setMessage("The username cannot start or end with -, _, . or a number, can contain no white spaces, emojis, or special characters, must be 3-15 characters long and all in lowercase.");
                                                        alert.setTitle("Create a username");
                                                        alert.setView(edittext);

                                                        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                //What ever you want to do with the value
                                                                Editable YouEditTextValue = edittext.getText();

                                                            }
                                                        });

                                                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                // what ever you want to do with No option.
                                                                LoginManager.getInstance().logOut();
                                                                mAuth.signOut();
                                                            }
                                                        });

                                                        alert.show();
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError)  {
                                                }
                                            });
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        }

                    @Override
                    public void onCancel() {
                        // Do nothing if they cancel FB
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // Let them know there's an error
                        Toast.makeText(LoginActivity.this, R.string.facebook_error,
                                Toast.LENGTH_SHORT).show();
                    }
                });


        mAuth = FirebaseAuth.getInstance();
        // If user is already logged in, just go straight to Home page
//        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
//            Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
//            LoginActivity.this.startActivity(myIntent);
//            LoginActivity.this.finish();
//            return;
//        }

        signInButton = (Button) findViewById(R.id.signInButton);
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);
        password.setTransformationMethod(new PasswordTransformationMethod());

        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signInButton.setText(R.string.sign_in_loading);
                disableUI();
                signin(email.getText().toString(), password.getText().toString());
            }
        });


        // Change size of email button
        email.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final float scale = getResources().getDisplayMetrics().density;
                        int dpWidthInPx = (int) (20 * scale);
                        int dpHeightInPx = (int) (20 * scale);

                        Drawable img = ContextCompat.getDrawable(LoginActivity.this, R.drawable.email);
                        img.setBounds(0, 0, dpWidthInPx, dpHeightInPx);
                        email.setCompoundDrawables(img, null, null, null);
                        email.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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

                        Drawable img = ContextCompat.getDrawable(LoginActivity.this, R.drawable.password);
                        img.setBounds(0, 0, dpWidthInPx, dpHeightInPx);
                        password.setCompoundDrawables(img, null, null, null);
                        password.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signInButton.setText(R.string.sign_in_loading);
                    disableUI();
                    signin(email.getText().toString(), password.getText().toString());
                    return true;
                }
                else {
                    return false;
                }
            }
        });



        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !password.hasFocus()) {
                    hideKeyboard(v);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        Button forgotPassword = (Button) findViewById(R.id.forgotPassword);


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                LoginActivity.this.startActivity(myIntent);
            }
        });

        Button signUpButton = (Button) findViewById(R.id.signUp);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                LoginActivity.this.startActivity(myIntent);
            }
        });

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void disableUI() {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(false);
        }
    }

    private void enableUI() {
        signInButton.setText(R.string.sign_in);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(true);
        }
    }


    // This function will sign in the user to Firebase
    private void signin(String email, String password) {

        // If email textfield is empty, ask them to enter one and enable the UI
        if (email.equals("")) {
            Toast.makeText(LoginActivity.this, R.string.enter_email,
                    Toast.LENGTH_SHORT).show();
            enableUI();
            return;
        }
        // If password textfield is empty, ask them to enter one and enable the UI
        if (password.equals("")) {
            Toast.makeText(LoginActivity.this, R.string.enter_password,
                    Toast.LENGTH_SHORT).show();
            enableUI();
            return;
        }


        // Sign in using Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user.
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                            enableUI();
                            return;
                        }


                        final FirebaseUser user = mAuth.getCurrentUser();

                        // If email is not verified, then ask to re-send verification email
                        // also, sign out the user.
                        if (user != null && !user.isEmailVerified()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(R.string.email_verification)
                                    .setNegativeButton(R.string.maybe_later, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mAuth.signOut();
                                        };
                                    })
                                    .setPositiveButton(R.string.resend, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            user.sendEmailVerification();
                                            mAuth.signOut();
                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.show();
                            enableUI();
                            return;
                        } else {
                            // Login successful, move onto the home page
                            Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            LoginActivity.this.startActivity(myIntent);
                            LoginActivity.this.finish();
                        }

                        enableUI();

                    }
                });
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

}
