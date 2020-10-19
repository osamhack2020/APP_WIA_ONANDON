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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.Club.ClubPostItemMore;
import com.example.myapplication.MyGoalPost.MyGoalPostIngMore;
import com.example.myapplication.MyGoalPost.ScrollMygoalMore;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class PostMore extends AppCompatActivity {

    final int FINISH = 0;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseStorage storage;

    Toolbar toolbar;
    ImageView backPressed;
    ImageView sendComment;
    EditText comment;

    InputMethodManager imm;

    String documentUid;
    String postUid;
    String intentUid;
    String manager;
    int annonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_goal_post_ing_more);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        Intent intent = getIntent();
        documentUid = intent.getStringExtra("documentUid");
        postUid = intent.getStringExtra("postUid");
        intentUid = intent.getStringExtra("intentUid");
        manager = intent.getStringExtra("manager");
        annonymous = intent.getIntExtra("annonymous", 0);

        backPressed = (ImageView)findViewById(R.id.back_pressed);
        backPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 게시물 fragment에 게시물 고유 번호 정보를 보낸다.
        ScrollPostMore fragment = new ScrollPostMore();
        Bundle bundle = new Bundle(5);
        bundle.putString("documentUid", documentUid);
        bundle.putString("postUid", postUid);
        bundle.putString("intentUid", intentUid);
        bundle.putString("manager", manager);
        bundle.putInt("annonymous", annonymous);
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
                                Toast.makeText(PostMore.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                                comment.setText("");
                                imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
                            }
                        });

                // 댓글이 게시되면 게시물의 댓글 수를 1 증가시킨 후, 서버 정보를 업데이트 시킨다.
                final DocumentReference docRef = firestore.collection(documentUid).document(postUid);
                firestore.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docRef);
                        PostDTO postDTO = snapshot.toObject(PostDTO.class);
                        postDTO.commentCount = postDTO.commentCount+1;

                        transaction.set(docRef, postDTO);
                        return null;
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(auth.getCurrentUser().getUid().equals(intentUid) || auth.getCurrentUser().getUid().equals(manager)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.post_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // 사용자가 터치한 항목 객체의 id를 추출한다.
        int id = item.getItemId();
        //분기한다.
        switch(id){
            case R.id.update :
                final DocumentReference docRef = firestore.collection(documentUid).document(postUid);
                firestore.runTransaction(new Transaction.Function<Void>() {

                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docRef);
                        PostDTO postDTO = snapshot.toObject(PostDTO.class);

                        Intent updateIntent = new Intent(PostMore.this, UpdatePost.class);
                        updateIntent.putExtra("documentUid", documentUid);
                        updateIntent.putExtra("postUid", postUid);
                        if(postDTO.isPhoto == 1){
                            updateIntent.putExtra("imageUri", postDTO.imageUri);
                        }
                        startActivityForResult(updateIntent, FINISH);
                        return null;
                    }
                });
                return true;
            case R.id.delete :
                Intent deleteIntent = new Intent(this, DeletePostAsk.class);
                startActivityForResult(deleteIntent, FINISH);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FINISH){
            if(resultCode == RESULT_OK) {

                final DocumentReference docRef = firestore.collection(documentUid).document(postUid);
                firestore.runTransaction(new Transaction.Function<Void>() {
                    StorageReference httpsReference = storage.getReference();

                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docRef);
                        PostDTO postDTO = snapshot.toObject(PostDTO.class);

                        if(postDTO.isPhoto == 1){
                            httpsReference = storage.getReferenceFromUrl(postDTO.imageUri);
                            httpsReference.delete();
                        }
                        return null;
                    }
                });

                firestore.collection(documentUid).document(postUid).delete();
                firestore.collection(postUid).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            ArrayList<String> contentUidList = new ArrayList<>();

                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot value : queryDocumentSnapshots) {
                                    contentUidList.add(value.getId());
                                }
                                for (int i = 0; i < contentUidList.size(); i++) {
                                    firestore.collection(postUid).document(contentUidList.get(i)).delete();
                                }
                            }
                        });

                Toast.makeText(PostMore.this, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if(resultCode == RESULT_FIRST_USER){
                finish();
            }
        }
    }
}