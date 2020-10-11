package com.example.myapplication.Club;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MyGoalPost.NewPost;
import com.example.myapplication.R;
import com.example.myapplication.model.QuestionDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class QuestionPopUp extends Activity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    String documentUid;
    EditText questionExplain;
    TextView complete;
    ImageView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_pop_up);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        questionExplain = (EditText)findViewById(R.id.question_explain);
        complete = (TextView)findViewById(R.id.complete);
        cancel = (ImageView)findViewById(R.id.cancel);

        Display dp = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int)(dp.getWidth()*0.9);
        int height = (int)(dp.getHeight()*0.45);
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        Intent intent = getIntent();
        documentUid = intent.getStringExtra("documentUid");

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionDTO questionDTO = new QuestionDTO();

                questionDTO.timestamp = System.currentTimeMillis();
                questionDTO.explain = questionExplain.getText().toString();
                questionDTO.uid = auth.getCurrentUser().getUid();

                firestore.collection(documentUid+"_question").document().set(questionDTO)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(QuestionPopUp.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                finish();
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
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}