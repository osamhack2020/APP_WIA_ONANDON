package com.example.myapplication.Club;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.DeletePostAsk;
import com.example.myapplication.MyGoalPost.MyGoalPostIngMore;
import com.example.myapplication.MyGoalPost.ScrollMygoalMore;
import com.example.myapplication.NewPostPublic;
import com.example.myapplication.PostListFrame;
import com.example.myapplication.PostMore;
import com.example.myapplication.PostSearch;
import com.example.myapplication.R;
import com.example.myapplication.UpdatePost;
import com.example.myapplication.model.CommentDTO;
import com.example.myapplication.model.MyGoalContentDTO;
import com.example.myapplication.model.PostDTO;
import com.example.myapplication.model.UserDTO;
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

import java.util.ArrayList;
import java.util.Objects;

public class ClubPostItemMore extends AppCompatActivity {

    final int FINISH = 0;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseStorage storage;

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
        storage = FirebaseStorage.getInstance();

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.update:
                final DocumentReference docRef = firestore.collection(budae+"동아리게시판").document(postUid);
                firestore.runTransaction(new Transaction.Function<Void>() {

                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docRef);
                        PostDTO postDTO = snapshot.toObject(PostDTO.class);

                        Intent updateIntent = new Intent(ClubPostItemMore.this, UpdateClubPost.class);
                        updateIntent.putExtra("name", name);
                        updateIntent.putExtra("uid", postDTO.uid);
                        updateIntent.putExtra("documentUid", budae+"동아리게시판");
                        updateIntent.putExtra("postUid", postUid);
                        if(postDTO.isPhoto == 1){
                            updateIntent.putExtra("imageUri", postDTO.imageUri);
                        }
                        startActivityForResult(updateIntent, FINISH);
                        return null;
                    }
                });
                break;
            case R.id.delete:
                Intent deleteIntent = new Intent(this, DeletePostAsk.class);
                startActivityForResult(deleteIntent, FINISH);
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FINISH){
            if(resultCode == RESULT_OK) {

                final DocumentReference docRef = firestore.collection(budae+"동아리게시판").document(postUid);
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

                firestore.collection(budae+"동아리게시판").document(postUid).delete();
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

                Toast.makeText(ClubPostItemMore.this, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if(resultCode == RESULT_FIRST_USER){
                finish();
            }
        }
    }
}