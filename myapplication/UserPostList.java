package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserPostList extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    FrameLayout postContent;

    Toolbar toolbar;
    TextView toolbarTitle;
    TextView noPost;

    String name;
    String documentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post_list);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        noPost = (TextView)findViewById(R.id.no_post);
        postContent = (FrameLayout)findViewById(R.id.post_content);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        documentUid = intent.getStringExtra("documentUid");

        toolbarTitle.setText(name);

        UserPost userPost = new UserPost();
        Bundle bundle = new Bundle(1);
        bundle.putString("documentUid", documentUid);
        userPost.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.post_content, userPost);
        tran.commit();

        firestore.collection(documentUid).limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.size() != 0){
                            noPost.setVisibility(View.GONE);
                            postContent.setVisibility(View.VISIBLE);
                        }
                        else{
                            noPost.setVisibility(View.VISIBLE);
                            postContent.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UserPost userPost = new UserPost();
        Bundle bundle = new Bundle(1);
        bundle.putString("documentUid", documentUid);
        userPost.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.post_content, userPost);
        tran.commit();

        firestore.collection(documentUid).limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.size() != 0){
                            noPost.setVisibility(View.GONE);
                            postContent.setVisibility(View.VISIBLE);
                        }
                        else{
                            noPost.setVisibility(View.VISIBLE);
                            postContent.setVisibility(View.GONE);
                        }
                    }
                });
    }
}