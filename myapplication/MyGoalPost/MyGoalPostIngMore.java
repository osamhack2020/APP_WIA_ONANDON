package com.example.myapplication.MyGoalPost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.Club.NewClubPost;
import com.example.myapplication.DeletePostAsk;
import com.example.myapplication.FcmPush;
import com.example.myapplication.R;
import com.example.myapplication.model.CommentDTO;
import com.example.myapplication.model.MyGoalContentDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class MyGoalPostIngMore extends AppCompatActivity {

    final int FINISH = 0;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseStorage storage;

    Toolbar toolbar;
    ImageView backPressed;
    ImageView sendComment;
    EditText comment;

    InputMethodManager imm;

    String intentDocument;
    String intentUid;

    FcmPush fcmPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_goal_post_ing_more);

        final int FINISH =  0;

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        fcmPush = new FcmPush();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        backPressed = (ImageView)findViewById(R.id.back_pressed);
        backPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        intentDocument = intent.getStringExtra("document");
        intentUid = intent.getStringExtra("uid");

        // 게시물 fragment에 게시물 고유 번호 정보를 보낸다.
        ScrollMygoalMore fragment = new ScrollMygoalMore();
        Bundle bundle = new Bundle(2);
        bundle.putString("document", intentDocument);
        bundle.putString("uid", intentUid);
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

                firestore.collection(intentDocument).document().set(commentDTO)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MyGoalPostIngMore.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                                comment.setText("");
                                imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
                            }
                        });

                // 댓글이 게시되면 게시물의 댓글 수를 1 증가시킨 후, 서버 정보를 업데이트 시킨다.
                final DocumentReference docRef = firestore.collection("MyGoal").document(intentDocument);
                firestore.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docRef);
                        MyGoalContentDTO myGoalContentDTO = snapshot.toObject(MyGoalContentDTO.class);
                        myGoalContentDTO.commentCount = myGoalContentDTO.commentCount+1;

                        transaction.set(docRef, myGoalContentDTO);
                        return null;
                    }
                });

                firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                                String message = userDTO.army+" "+userDTO.budae+" "+userDTO.rank+" "+userDTO.name;
                                fcmPush.sendMessage(intentUid, "댓글 알림 메세지 입니다.", message+"님이 댓글을 달았습니다.");
                            }
                        });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(auth.getCurrentUser().getUid().equals(intentUid)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.post_menu, menu);
            return true;
        }
        else return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // 사용자가 터치한 항목 객체의 id를 추출한다.
        int id = item.getItemId();
        //분기한다.
        switch(id){
            case R.id.update : return true;
            case R.id.delete :
                Intent intent = new Intent(this, DeletePostAsk.class);
                startActivityForResult(intent, FINISH);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FINISH){
            if(resultCode == RESULT_OK) {
                firestore.collection("MyGoal").document(intentDocument).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            StorageReference httpsReference;

                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                MyGoalContentDTO myGoalContentDTO = documentSnapshot.toObject(MyGoalContentDTO.class);
                                if (myGoalContentDTO.isPhoto == 1) {
                                    httpsReference = storage.getReferenceFromUrl(myGoalContentDTO.imageUri);
                                    httpsReference.delete();
                                }
                            }
                        });
                firestore.collection("MyGoal").document(intentDocument).delete();
                firestore.collection(intentDocument).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            ArrayList<String> contentUidList = new ArrayList<>();

                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot value : queryDocumentSnapshots) {
                                    contentUidList.add(value.getId());
                                }
                                for (int i = 0; i < contentUidList.size(); i++) {
                                    firestore.collection(intentDocument).document(contentUidList.get(i)).delete();
                                }
                            }
                        });

                Toast.makeText(MyGoalPostIngMore.this, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}