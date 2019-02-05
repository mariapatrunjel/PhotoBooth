package lgm.photobooth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;

public class AddNewPostActivity extends Activity {
    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseUser mLoggedUser;
    private DatabaseReference mDatabaseUser;

    private EditText mStatusField;
    private StorageReference mStorage;

    private ImageButton mImageButton;
    private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mLoggedUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("user-profile").child(mLoggedUser.getUid());

        mStatusField = findViewById(R.id.status);
        mImageButton = findViewById(R.id.imageButton);
    }

    public void onImportImageFromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    public void onClickAddPostButton(View view){

        final String status = mStatusField.getText().toString();

        if (!validateForm()) {
            return;
        }


        final StorageReference ref = mStorage.child("post_images").child(uri.getLastPathSegment());
        ref.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    final String downloadUrl = downloadUri.toString();
                    final DatabaseReference newPostRef = mDatabase.child("posts").push();
                    final DatabaseReference newUserPostRef =mDatabase.child("user-posts").child(mLoggedUser.getUid()).child(newPostRef.getKey());

                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String username = dataSnapshot.child("username").getValue().toString();
                            Post newPost = new Post(mLoggedUser.getUid(),username,status,downloadUrl);
                            newPostRef.updateChildren(newPost.toMap());
                            newUserPostRef.updateChildren(newPost.toMapWithoutUserId());

                            uri = null;
                            mImageButton.setImageURI(uri);
                            mImageButton.setImageResource(R.drawable.img);
                            mStatusField.setText("");

                            Toast.makeText(AddNewPostActivity.this, "Post made successfully",
                                    Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(AddNewPostActivity.this, "Can't upload post",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Handle failures
                    // ...
                    Toast.makeText(AddNewPostActivity.this, "Can't upload post",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validateForm() {
        boolean valid = true;

        String status = mStatusField.getText().toString();
        if (TextUtils.isEmpty(status)) {
            mStatusField.setError("Required.");
            valid = false;
        } else {
            mStatusField.setError(null);
        }

        if(uri==null){
            Toast.makeText(AddNewPostActivity.this, "Can't make post without image",
                    Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;

    }

    @Override
    // image from gallery result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            uri = data.getData();
            mImageButton.setImageURI(uri);
        }
    }
}
