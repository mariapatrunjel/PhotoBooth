package lgm.photobooth;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfileActivity extends Activity {
    //Firebase
    private DatabaseReference mDatabaseRef;
    private FirebaseUser mLoggedUser;
    private DatabaseReference mDatabaseUserRef;

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mAddress;
    private EditText mPhone;
    private  User updateUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mLoggedUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseUserRef = mDatabaseRef.child("user-profile").child(mLoggedUser.getUid());

        mFirstName = findViewById(R.id.firstName);
        mLastName = findViewById(R.id.lastName);
        mAddress = findViewById(R.id.address);
        mPhone = findViewById(R.id.phone);

        mDatabaseUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = "";
                String username = "";
                String firstName = "";
                String lastName = "";
                String address = "";
                String phone = "";

                if(dataSnapshot.child("username").getValue()!=null)
                    username = dataSnapshot.child("username").getValue().toString();
                if(dataSnapshot.child("email").getValue()!=null)
                    email = dataSnapshot.child("email").getValue().toString();
                if(dataSnapshot.child("first-name").getValue()!=null)
                    firstName = dataSnapshot.child("first-name").getValue().toString();
                if(dataSnapshot.child("last-name").getValue()!=null)
                    lastName = dataSnapshot.child("last-name").getValue().toString();
                if(dataSnapshot.child("address").getValue()!=null)
                    address = dataSnapshot.child("address").getValue().toString();
                if(dataSnapshot.child("phone").getValue()!=null)
                   phone = dataSnapshot.child("phone").getValue().toString();

                updateUser = new User(mLoggedUser.getUid().toString(),email,username,firstName,lastName,address,phone);

                mFirstName.setText(firstName);
                mLastName.setText(lastName);
                mAddress.setText(address);
                mPhone.setText(phone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyProfileActivity.this, "Can't change user profile",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickSaveButton(View view){
        updateUser.setFirstName(mFirstName.getText().toString());
        updateUser.setLastName(mLastName.getText().toString());
        updateUser.setAddress( mAddress.getText().toString());
        updateUser.setPhoneNumber( mPhone.getText().toString());
        mDatabaseUserRef.updateChildren(updateUser.toMap());
    }
}
