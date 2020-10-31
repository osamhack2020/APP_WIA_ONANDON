package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    final int MAKE_ACCOUNT = 1;
    final int MAIN_ACTIVITY = 2;

    FirebaseAuth auth;
    EditText emailEdit;
    EditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        emailEdit = (EditText) findViewById(R.id.email_edittext);
        passwordEdit = (EditText)findViewById(R.id.password_edittext);

    }

    // 사용자가 로그인할 때 호출되는 메서드
    public void signinEmail(View view){
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // 로그인에 성공하면 메인 페이지로 이동한다.
                            moveMainPage(auth.getCurrentUser());
                        }else{
                            // 로그인에 실패하면 에러메세지를 토스트로 띄운다.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 사용자가 회원가입을 할 때 호출되는 메서드
    public void makeAccount(View view){
        Intent intent = new Intent(this, MakeAccount.class);
        startActivityForResult(intent, MAKE_ACCOUNT);
    }

    // 메인 페이지로 이동시키는 메서드
    public void moveMainPage(FirebaseUser user){
        if(user != null){
            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            LoginActivity.this.finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        moveMainPage(auth.getCurrentUser());
    }

}