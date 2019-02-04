package lgm.photobooth;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private String userId;
    private String status;
    private String author;
    private Uri photoUrl;

    private String image;

    public Post() {
    }

    public Post(String userId, String author, String status, Uri photoUrl) {
        this.userId = userId;
        this.status = status;
        this.author = author;
        this.photoUrl = photoUrl;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", userId);
        result.put("status",status);
        result.put("photo",photoUrl.toString());
        result.put("author",author);
        return result;
    }

    @Exclude
    public Map<String, Object> toMapWithoutUserId() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("status",status);
        result.put("photo",photoUrl.toString());
        result.put("author",author);
        return result;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImage() {return image;}

    public void setImage(String image) {this.image=image;}

}
