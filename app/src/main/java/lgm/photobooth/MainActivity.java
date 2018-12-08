package lgm.photobooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {

    private DatabaseReference mDatabase;
    private FirebaseUser loggedUser;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

    }


    public void onClickNewPostButton(View view){
        switchToAddNewPostActivity();
    }

    public void onClickLogoutButton(View view){
        mAuth.signOut();
        switchToLoginActivity();
    }

    public void onClickMyProfileButton(View view){
        switchToMyProfileActivity();
    }
    private void switchToAddNewPostActivity() {
        Intent mIntent = new Intent(this, AddNewPostActivity.class);
        startActivity(mIntent);
    }
    private void switchToLoginActivity() {
        Intent mIntent = new Intent(this, LoginActivity.class);
        startActivity(mIntent);
    }

    private void switchToMyProfileActivity() {
        Intent mIntent = new Intent(this, MyProfileActivity.class);
        startActivity(mIntent);
    }
}
