package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.BottomNavigation.MakeBoard;
import com.example.myapplication.model.BoardDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateBoard extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    ImageView cancel;
    TextView complete;
    EditText name;
    EditText explain;

    String collection;
    String documentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_board);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        complete = (TextView)findViewById(R.id.complete);
        cancel = (ImageView)findViewById(R.id.cancel);
        name = (EditText)findViewById(R.id.board_title);
        explain = (EditText)findViewById(R.id.board_explain);

        Intent intent = getIntent();
        collection = intent.getStringExtra("collection");
        documentUid = intent.getStringExtra("documentUid");

        firestore.collection(collection).document(documentUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        BoardDTO boardDTO = documentSnapshot.toObject(BoardDTO.class);

                        name.setText(boardDTO.name);
                        explain.setText(boardDTO.explain);
                    }
                });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty() || name.getText().toString().isEmpty()){
                    Toast.makeText(UpdateBoard.this, "항목을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                        firestore.collection(collection).document(documentUid).update("name", name.getText().toString());
                        firestore.collection(collection).document(documentUid).update("explain", explain.getText().toString());

                        Toast.makeText(UpdateBoard.this, "수정 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }
}