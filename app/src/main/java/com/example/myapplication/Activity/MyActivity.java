package com.example.myapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.example.myapplication.UserPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Objects;

public class MyActivity extends AppCompatActivity {

    FrameLayout postContent;

    Toolbar toolbar;
    TextView toolbarTitle;
    TextView noPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        noPost = (TextView)findViewById(R.id.no_post);
        postContent = (FrameLayout)findViewById(R.id.post_content);

        MyActivityList myActivityList = new MyActivityList();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.post_content, myActivityList);
        tran.commit();
    }
}