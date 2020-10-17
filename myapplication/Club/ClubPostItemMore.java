package com.example.myapplication.Club;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.MyGoalPost.MyGoalPostIngMore;
import com.example.myapplication.MyGoalPost.ScrollMygoalMore;
import com.example.myapplication.R;
import com.example.myapplication.model.CommentDTO;
import com.example.myapplication.model.MyGoalContentDTO;
import com.example.myapplication.model.PostDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

public class ClubPostItemMore extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    Toolbar toolbar;
    ImageView backPressed;
    ImageView sendComment;
    EditText comment;

    InputMethodManager imm;

    String name;
    String manager;
    String postUid;
    String budae;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_goal_post_ing_more);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        manager = intent.getStringExtra("manager");
        postUid = intent.getStringExtra("postUid");
        budae = intent.getStringExtra("budae");

        backPressed = (ImageView)findViewById(R.id.back_pressed);
        backPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ScrollClubPostItem fragment = new ScrollClubPostItem();
        Bundle bundle = new Bundle(4);
        bundle.putString("postUid", postUid);
        bundle.putString("name",name);
        bundle.putString("manager", manager);
        bundle.putString("budae", budae);
        fragment.setArguments(bundle);

        // 게시물 fragment를 연다.
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.goal_ing_content, fragment);
        tran.commit();

        sendComment = (ImageView)findViewById(R.id.mygoal_send_comment);
        comment = (EditText)findViewById(R.id.mygoal_comment);
        // 댓글 올리기 이벤트
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentDTO commentDTO = new CommentDTO();

                commentDTO.uid = auth.getCurrentUser().getUid();
                commentDTO.comment = comment.getText().toString();
                commentDTO.timeStamp = System.currentTimeMillis();

                firestore.collection(postUid).document().set(commentDTO)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ClubPostItemMore.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                                comment.setText("");
                                imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
                            }
                        });

                // 댓글이 게시되면 게시물의 댓글 수를 1 증가시킨 후, 서버 정보를 업데이트 시킨다.
                firestore.collection(budae+"동아리게시판").document(postUid).update("commentCount",  FieldValue.increment(1));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(auth.getCurrentUser().getUid().equals(manager)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.post_menu, menu);
        }
        return true;
    }
}