package vl.viime;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    private void signin(String email, String password) {
        if (email.equals("")) {
            Toast.makeText(LoginActivity.this, "Please enter an email",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.equals("")) {
            Toast.makeText(LoginActivity.this, "Please enter a password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Change icon size of email
        final EditText email = (EditText) findViewById(R.id.email);
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


        final EditText password = (EditText) findViewById(R.id.password);
        password.setTransformationMethod(new PasswordTransformationMethod());

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
                    signin(email.getText().toString(), password.getText().toString());
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        final Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signin(email.getText().toString(), password.getText().toString());

            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {


                if (!hasFocus && !password.hasFocus()) {
                    Log.d(TAG, "ID is "+v.getId());
                    Log.d(TAG, "Password "+ R.id.password);
                    hideKeyboard(v);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    Log.d(TAG, "Hiding in 2");
                    hideKeyboard(v);
                }
            }
        });


    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
