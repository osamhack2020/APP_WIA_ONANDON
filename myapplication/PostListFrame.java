package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.myapplication.MyGoalPost.MyGoalSearch;
import com.example.myapplication.MyGoalPost.NewPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class PostListFrame extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    FrameLayout postContent;

    Toolbar toolbar;
    TextView toolbarTitle;
    TextView explain;
    TextView open;
    TextView noPost;

    String name;
    String explainIntent;
    String documentUid;
    String manager;

    int click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list_frame);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        explain = (TextView)findViewById(R.id.explain);
        noPost = (TextView)findViewById(R.id.no_post);
        open = (TextView)findViewById(R.id.open);
        postContent = (FrameLayout)findViewById(R.id.post_content);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        explainIntent = intent.getStringExtra("explain");
        documentUid = intent.getStringExtra("documentUid");
        manager = intent.getStringExtra("manager");

        toolbarTitle.setText(name);
        explain.setText(explainIntent);
        explain.setVisibility(View.GONE);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(click == 0){
                    click = 1;
                    open.setText("숨기기");
                    explain.setVisibility(View.VISIBLE);
                }else{
                    click = 0;
                    open.setText("펼쳐보기");
                    explain.setVisibility(View.GONE);
                }
            }
        });

        PostList postList = new PostList();
        Bundle bundle = new Bundle(2);
        bundle.putString("documentUid", documentUid);
        bundle.putString("manager", manager);
        postList.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.post_content, postList);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mygoal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.search:
                Intent intent = new Intent(this, MyGoalSearch.class);
                startActivity(intent);
                break;
            case R.id.newpost:
                Intent intentPost = new Intent(this, NewPostPublic.class);
                intentPost.putExtra("documentUid", documentUid);
                startActivity(intentPost);
                break;
        }

        return true;
    }
}