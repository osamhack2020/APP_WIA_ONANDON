package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateBoard extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    ImageView cancel;
    TextView complete;
    EditText name;
    EditText explain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_board);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        complete = (TextView)findViewById(R.id.complete);
        cancel = (ImageView)findViewById(R.id.cancel);
        name = (EditText)findViewById(R.id.board_title);
        explain = (EditText)findViewById(R.id.board_explain);
    }
}