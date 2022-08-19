package com.dsis.myappblog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dsis.myappblog.Fragments.DashboardFragment;
import com.dsis.myappblog.models.Post;
import com.dsis.myappblog.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private Button btnAddPost, btnChangeAddPost;
    private ImageView imgPost;
    private EditText txtDesc;
    private ProgressBar progressBar;
    private Bitmap bitmap = null;
    private static final int GALLERY_CHANGE_POST_PHOTO = 3;
    private SharedPreferences userPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        init();
    }

    private void init() {
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnAddPost = findViewById(R.id.btnAddPost);
        btnChangeAddPost = findViewById(R.id.btnAddPostPhoto);
        imgPost = findViewById(R.id.imgAddPost);
        txtDesc = findViewById(R.id.txtDescAddPost);
        progressBar = findViewById(R.id.progressBarAddPost);
        progressBar.setVisibility(View.INVISIBLE);

        imgPost.setImageURI(getIntent().getData());
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnAddPost.setOnClickListener(view -> {
            if (!txtDesc.getText().toString().isEmpty()) {
                btnAddPost.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                addPost();
            }
            else {
                Toast.makeText(this, getText(R.string.desc_required), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addPost() {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.ADD_POST, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject postObject = object.getJSONObject("post");
                    JSONObject userObject = postObject.getJSONObject("user");

                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setName(userObject.getString("name")+ " " + userObject.getString("lastname"));
                    user.setPhoto(userObject.getString("photo"));

                    Post post = new Post();
                    post.setUser(user);
                    post.setId(postObject.getInt("id"));
                    post.setSelfLike(false);
                    post.setPhoto(postObject.getString("photo"));
                    post.setDesc(postObject.getString("desc"));
                    post.setComments(0);
                    post.setLikes(0);
                    post.setDate(postObject.getString("created_at"));

                    DashboardFragment.arrayList.add(0,post);
                    DashboardFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    DashboardFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, getText(R.string.posted), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    btnAddPost.setEnabled(true);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            btnAddPost.setEnabled(true);
            progressBar.setVisibility(View.VISIBLE);
        }, error -> {
            error.printStackTrace();
            btnAddPost.setEnabled(true);
            progressBar.setVisibility(View.VISIBLE);
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer" + token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("desc", txtDesc.getText().toString().trim());
                map.put("photo", bitmapToString(bitmap));
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(AddPostActivity.this);
        queue.add(request);
    }

    public void ChangePhoto(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, GALLERY_CHANGE_POST_PHOTO);
    }

    public void cancelPost(View view) {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CHANGE_POST_PHOTO && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            imgPost.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String bitmapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte [] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }
        return "";
    }
}