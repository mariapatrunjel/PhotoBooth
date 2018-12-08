package lgm.photobooth;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends Activity {

        private static final String TAG = "EmailPassword";
        private EditText mEmailField;
        private EditText mUsernameField;
        private EditText mPasswordField;
        private EditText mPasswordRetypedField;

        private DatabaseReference mDatabase;
        private DatabaseReference mDatabaseUsers;
        private FirebaseAuth mAuth;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);

            // Views
            mEmailField = findViewById(R.id.email);
            mUsernameField = findViewById(R.id.username);
            mPasswordField = findViewById(R.id.password);
            mPasswordRetypedField = findViewById(R.id.passwordRetyped);

            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabaseUsers = mDatabase.child("user-profile");
        }


        public void onClickRegisterButton(View view) {
            final String email = mEmailField.getText().toString();
            final String password = mPasswordField.getText().toString();
            final String username = mUsernameField.getText().toString();
            Log.d(TAG, "createAccount:" + email);
            if (!validateForm()) {
                return;
            }
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                User newUser = new User (mAuth.getCurrentUser().getUid(),email,username,"","","","");
                                DatabaseReference newUserRef = mDatabase.child("/user-profile/" + newUser.getUserId());
                                newUserRef.updateChildren(newUser.toMap());

                                Toast.makeText(RegisterActivity.this, "CreateUserWithEmail:success.",
                                        Toast.LENGTH_SHORT).show();

                                switchToLoginActivity();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterActivity.this, "Register failed.",
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

            String username = mUsernameField.getText().toString();
            if (TextUtils.isEmpty(username)) {
                mUsernameField.setError("Required.");
                valid = false;
            } else {
                mUsernameField.setError(null);
            }

            String password = mPasswordField.getText().toString();
            if (TextUtils.isEmpty(password)) {
                mPasswordField.setError("Required.");
                valid = false;
            } else {
                if(password.length()<6){
                    mPasswordField.setError("To short.");
                    valid = false;
                }else {
                    mPasswordField.setError(null);
                }
            }

            String passwordRetyped = mPasswordRetypedField.getText().toString();
            if (TextUtils.isEmpty(passwordRetyped)) {
                mPasswordRetypedField.setError("Required.");
                valid = false;
            } else {
                if(!password.equals(passwordRetyped)){
                    mPasswordField.setError("Not the same.");
                    mPasswordRetypedField.setError("Not the same.");
                    valid = false;
                }else {
                    mPasswordField.setError(null);
                    mPasswordRetypedField.setError(null);
                }
            }
            return valid;
        }

        private void  switchToLoginActivity() {
            Intent mIntent = new Intent(this, LoginActivity.class);
            startActivity(mIntent);
        }


}
