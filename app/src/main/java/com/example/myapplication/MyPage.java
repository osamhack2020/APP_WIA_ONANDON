package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyPage extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    ImageView cancel;

    TextView name;
    TextView email;
    TextView army;
    TextView budae;
    TextView rank;

    TextView getId;
    TextView logout;
    EditText id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        name = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        army = (TextView)findViewById(R.id.army_answer);
        budae = (TextView)findViewById(R.id.budae_answer);
        rank = (TextView)findViewById(R.id.rank_answer);
        id = (EditText) findViewById(R.id.id);

        getId = (TextView)findViewById(R.id.get_id);
        logout = (TextView)findViewById(R.id.logout);

        firestore.collection("UserInfo").document(auth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                name.setText(userDTO.name);
                email.setText(" / "+auth.getCurrentUser().getEmail());
                army.setText(userDTO.army);
                budae.setText(userDTO.budae);
                rank.setText(userDTO.rank);
            }
        });

        getId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id.setVisibility(View.VISIBLE);
                id.setText(auth.getCurrentUser().getUid());
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(MyPage.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                MyPage.this.finish();
                Toast.makeText(MyPage.this, "로그아웃", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        cancel = (ImageView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}