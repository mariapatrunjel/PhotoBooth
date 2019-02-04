package lgm.photobooth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Vector;

public class MainActivity extends Activity {

    private FirebaseAuth mAuth;

    private FirebaseDatabase mDb;
    private DatabaseReference mPostRef;

    private TextView mAuthorView;
    private ImageView mPhotoView;
    private TextView mStatusView;

    private Vector<Post> mPosts=new Vector<>();
    private int mIndex=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuthorView = findViewById(R.id.authorText);
        mPhotoView =findViewById(R.id.imageView);
        mStatusView =findViewById(R.id.statusText);

        mAuth = FirebaseAuth.getInstance();

        mDb=FirebaseDatabase.getInstance();

        mPostRef = mDb.getReference().child("posts");

        mPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot el : dataSnapshot.getChildren())
                {
                    String author = "";
                    String status = "";
                    String photo = "";

                    if(el.child("author").getValue()!=null)
                        author=el.child("author").getValue().toString();

                    if(el.child("status").getValue()!=null)
                        status=el.child("status").getValue().toString();

                    if(el.child("photo").getValue()!=null)
                        photo=el.child("photo").getValue().toString();

                    Uri url=Uri.parse(photo);
                    Post post=new Post(el.getValue().toString(), author, status, url);
                    post.setImage(photo);
                    mPosts.add(post);
                }

                if(mPosts.size()>0)
                {
                    mAuthorView.setText(mPosts.lastElement().getAuthor());
                    Picasso.get().load(mPosts.lastElement().getPhotoUrl()).fit().into(mPhotoView);
                    mStatusView.setText(mPosts.lastElement().getStatus());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Can't change user profile",
                        Toast.LENGTH_SHORT).show();
            }
        });


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

    public void onClickPreviousPicture(View view)
    {
        if (mIndex <= 0)
        {
            mIndex = mPosts.size();
        }
        --mIndex;
        changeDisplayedPicture();
    }

    public void onClickNextPicture(View view)
    {
        if (mIndex >= mPosts.size()-1)
        {
            mIndex = -1;
        }
        ++mIndex;
        changeDisplayedPicture();
    }

    private void changeDisplayedPicture()
    {
        if(mPosts.size()>0)
        {
            mAuthorView.setText(mPosts.elementAt(mIndex).getAuthor());
            Picasso.get().load(mPosts.elementAt(mIndex).getPhotoUrl()).fit().into(mPhotoView);
            mStatusView.setText(mPosts.elementAt(mIndex).getStatus());
        }
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
