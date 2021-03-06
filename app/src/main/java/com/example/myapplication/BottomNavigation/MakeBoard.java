package com.example.myapplication.BottomNavigation;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Club.NewClubPost;
import com.example.myapplication.R;
import com.example.myapplication.model.BoardDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class


MakeBoard extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    ImageView cancel;
    TextView complete;
    EditText name;
    EditText explain;

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

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty() || name.getText().toString().isEmpty()){
                    Toast.makeText(MakeBoard.this, "항목을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                                    BoardDTO boardDTO = new BoardDTO();
                                    boardDTO.manager = auth.getCurrentUser().getUid();
                                    boardDTO.name = name.getText().toString();
                                    boardDTO.explain = explain.getText().toString();
                                    boardDTO.timestamp = System.currentTimeMillis();

                                    firestore.collection(userDTO.budae+"게시판").document().set(boardDTO);
                                    Toast.makeText(MakeBoard.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
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
}