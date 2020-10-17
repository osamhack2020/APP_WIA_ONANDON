package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

    Toolbar toolbar;
    TextView toolbarTitle;
    TextView explain;
    TextView noPost;

    String name;
    String explainIntent;
    String documentUid;

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

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        explainIntent = intent.getStringExtra("explain");
        documentUid = intent.getStringExtra("documentUid");

        toolbarTitle.setText(name);
        explain.setText(explainIntent);

        firestore.collection(documentUid).limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.size() != 0){
                            noPost.setVisibility(View.GONE);
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