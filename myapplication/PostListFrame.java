package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.MyGoalPost.MyGoalSearch;
import com.example.myapplication.MyGoalPost.NewPost;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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
    TextView update;
    TextView change;
    TextView isManager;

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
        open = (TextView)findViewById(R.id.open);
        postContent = (FrameLayout)findViewById(R.id.post_content);
        isManager = (TextView)findViewById(R.id.is_manager);
        update = (TextView)findViewById(R.id.update);
        change = (TextView)findViewById(R.id.change);

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
        isManager.setVisibility(View.GONE);
        update.setVisibility(View.GONE);
        change.setVisibility(View.GONE);

        if(auth.getCurrentUser().getUid().equals(manager)){
            isManager.setVisibility(View.VISIBLE);
            update.setVisibility(View.VISIBLE);
            change.setVisibility(View.VISIBLE);

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                                    Intent intent = new Intent(PostListFrame.this, UpdateBoard.class);
                                    intent.putExtra("budae", userDTO.budae);
                                    intent.putExtra("documentUid", documentUid);
                                    startActivityForResult(intent, 0);
                                }
                            });
                }
            });
        }

        // 펼쳐보기 버튼을 누르면 게시판의 설명이 펼쳐지면서 사용자에게 노출된다.
        // 숨기기 버튼을 누르면 게시판의 설명이 숨겨진다.
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

        // 게시물 리스트를 recyclerview로 보여주는 PostList.class를 FrameLayout에 실행시킨다.
        PostList postList = new PostList();
        Bundle bundle = new Bundle(3);
        bundle.putString("documentUid", documentUid);
        bundle.putString("manager", manager);
        bundle.putString("name", name);
        postList.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.post_content, postList);
        tran.commit();
    }

    // 백버튼을 누르면 게시판을 떠난 시간이 SharedPreferences 변수에 저장된다.
    // 새로운 게시물이 업로드 될 때마다, 게시판의 미리보기에 'new' 표시가 뜨게 되는데, 이 기능 구현하기 위해
    // SharedPreferences 변수에 적절한 시간 변수를 저장해 준다.
    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences("new", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putLong(documentUid, System.currentTimeMillis());
        editor.apply();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mygoal_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.search:
                Intent intent = new Intent(this, PostSearch.class);
                intent.putExtra("documentUid", documentUid);
                intent.putExtra("name", name);
                intent.putExtra("manager", manager);
                startActivity(intent);
                break;
            case R.id.newpost:
                firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                                Intent intentPost = new Intent(PostListFrame.this, NewPostPublic.class);
                                intentPost.putExtra("army", userDTO.army);
                                intentPost.putExtra("budae", userDTO.budae);
                                intentPost.putExtra("rank", userDTO.rank);
                                intentPost.putExtra("documentUid", documentUid);
                                intentPost.putExtra("name", name);
                                startActivity(intentPost);
                            }
                        });
                break;
        }

        return true;
    }
}