package vl.viime;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        final EditText email = (EditText) findViewById(R.id.email);
        // Change size of email button
        email.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final float scale = getResources().getDisplayMetrics().density;
                        int dpWidthInPx = (int) (20 * scale);
                        int dpHeightInPx = (int) (20 * scale);

                        Drawable img = ContextCompat.getDrawable(ForgotPasswordActivity.this, R.drawable.email);
                        img.setBounds(0, 0, dpWidthInPx, dpHeightInPx);
                        email.setCompoundDrawables(img, null, null, null);
                        email.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        final Button resetPassword = (Button) findViewById(R.id.signInButton);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String e = email.getText().toString();
                if (e.equals("")) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter an email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                FirebaseAuth.getInstance().sendPasswordResetEmail(e)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this, task.getException().getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Toast.makeText(ForgotPasswordActivity.this, "Reset Email Sent",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            }
                        });
            }
        });

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
