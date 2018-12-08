package lgm.photobooth;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends Activity {
        private static final String TAG = "EmailPassword";
        private EditText mEmailField;
        private EditText mPasswordField;

        private FirebaseAuth mAuth;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            // Views
            mEmailField = findViewById(R.id.email);
            mPasswordField = findViewById(R.id.password);

            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();
        }

        @Override
        public void onStart() {
            super.onStart();

            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            switchToMainActivity(currentUser);

        }

       public void onClickRegisterButton(View view) {
            switchToRegisterActivity();
       }

       public void onClickResetPasswordButton(View view) {
            switchToResetPasswordActivity();
        }

       public void onClickLogInButton(View view) {
            String email = mEmailField.getText().toString();
            String password = mPasswordField.getText().toString();

            Log.d(TAG, "signIn:" + email);
            if (!validateForm()) {
                return;
            }
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                switchToMainActivity(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


        private boolean validateForm() {
            boolean valid = true;

            String email = mEmailField.getText().toString();
            if (TextUtils.isEmpty(email)) {
                mEmailField.setError("Required.");
                valid = false;
            } else {
                mEmailField.setError(null);
            }

            String password = mPasswordField.getText().toString();
            if (TextUtils.isEmpty(password)) {
                mPasswordField.setError("Required.");
                valid = false;
            } else {
                mPasswordField.setError(null);
            }

            return valid;
        }

        private void  switchToMainActivity(FirebaseUser user) {
            if (user!=null) {
                Intent mIntent = new Intent(this, MainActivity.class);
                mIntent.putExtra("LoggedUser",user);
                startActivity(mIntent);
            }
        }

        private void  switchToRegisterActivity() {
            Intent mIntent = new Intent(this, RegisterActivity.class);
            startActivity(mIntent);
        }

        private void  switchToResetPasswordActivity() {
            Intent mIntent = new Intent(this, ResetPasswordActivity.class);
            startActivity(mIntent);
        }
}
