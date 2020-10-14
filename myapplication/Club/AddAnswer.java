package com.example.myapplication.Club;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.QuestionDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddAnswer extends Activity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    String documentUid;
    String questionUid;

    TextView complete;
    TextView answerExplain;
    ImageView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_answer);

        complete = (TextView)findViewById(R.id.complete);
        answerExplain = (TextView)findViewById(R.id.answer_explain);
        cancel = (ImageView)findViewById(R.id.cancel);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Display dp = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int)(dp.getWidth()*0.9);
        int height = (int)(dp.getHeight()*0.46);
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        Intent intent = getIntent();
        documentUid = intent.getStringExtra("documentUid");
        questionUid = intent.getStringExtra("questionUid");

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerExplain.getText().toString().isEmpty()){
                    Toast.makeText(AddAnswer.this, "답변을 작성해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(update == 1){
                    firestore.collection(documentUid+"_question").document(questionUid).update("answer", answerExplain.getText().toString());
                    Toast.makeText(AddAnswer.this, "수정 되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    final DocumentReference docRef = firestore.collection(documentUid + "_question").document(questionUid);
                    firestore.runTransaction(new Transaction.Function<Void>() {
                        @Nullable
                        @Override
                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(docRef);
                            QuestionDTO questionDTO = snapshot.toObject(QuestionDTO.class);

                            questionDTO.isAnswer = 1;
                            questionDTO.answer = answerExplain.getText().toString();
                            transaction.set(docRef, questionDTO);

                            return null;
                        }
                    });
                    Toast.makeText(AddAnswer.this, "답변이 등록 되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
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
