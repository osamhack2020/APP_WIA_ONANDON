package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MakeAccount extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    TextInputLayout passwordLayout;
    TextInputLayout armyLayout;

    EditText makeEmail;
    EditText makePassword;
    EditText makeName;
    EditText army;
    EditText budae;
    EditText rank;

    InputMethodManager imm;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_account);

        passwordLayout = findViewById(R.id.password_textinputlayout);
        armyLayout = findViewById(R.id.army_textinputlayout);

        makeEmail = (EditText)findViewById(R.id.make_email_edittext);
        makePassword = passwordLayout.getEditText();
        makeName = (EditText)findViewById(R.id.make_name_edittext);
        army = armyLayout.getEditText();
        budae = (EditText)findViewById(R.id.budae_edittext);
        rank = (EditText)findViewById(R.id.rank_edittext);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        passwordLayout.setCounterEnabled(true);
        passwordLayout.setCounterMaxLength(16);
        passwordLayout.setPasswordVisibilityToggleEnabled(true);

        makePassword.addTextChangedListener(password_textwatcher);

        ChoiceArmy armyListener = new ChoiceArmy();
        army.setOnTouchListener(armyListener);
    }

    private final TextWatcher password_textwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String text = charSequence.toString();
            if(text.length() > 16){
                passwordLayout.setError("비밀번호는 최대 16자리 입니다.");
            }else{
                passwordLayout.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    // 군 선택 editText
    class ChoiceArmy implements View.OnTouchListener{

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            PopupMenu pop = new PopupMenu(MakeAccount.this, view);
            Menu menu = pop.getMenu();
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.army_popup, menu);

            PopupListener listener = new PopupListener();
            pop.setOnMenuItemClickListener(listener);
            pop.show();

            return false;
        }
    }

    class PopupListener implements PopupMenu.OnMenuItemClickListener{

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();

            switch(id){
                case R.id.army :
                    army.setText("육군");
                    break;
                case R.id.airforce :
                    army.setText("공군");
                    break;
                case R.id.navy :
                    army.setText("해군");
                    break;
                case R.id.marine :
                    army.setText("해병대");
                    break;
            }
            imm.hideSoftInputFromWindow(army.getWindowToken(), 0);
            return false;
        }
    }

    // 회원가입 버튼
    public void btnMethod(View view){
        final String email = makeEmail.getText().toString();
        final String password = makePassword.getText().toString();
        final String name = makeName.getText().toString();
        final String getArmy = army.getText().toString();
        final String getBudae = budae.getText().toString();
        final String getRank = rank.getText().toString();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MakeAccount.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            UserDTO userDTO = new UserDTO();
                            userDTO.uid = auth.getCurrentUser().getUid();
                            userDTO.name = name;
                            userDTO.army = getArmy;
                            userDTO.budae = getBudae;
                            userDTO.rank = getRank;
                            storeUserInfo(userDTO);

                            moveMainPage(auth.getCurrentUser());
                        }
                        else{
                            Toast.makeText(MakeAccount.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 로그인 정보를 firestore에 저장한다.
    public void storeUserInfo(UserDTO userDTO){
        firestore.collection("UserInfo").document(userDTO.uid).set(userDTO)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("store", "사용자 데이터를 저장했습니다.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("store", "사용자 데이터 저장에 실패했습니다.");
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        moveMainPage(auth.getCurrentUser());
    }

    // 메인 페이지로 이동
    public void moveMainPage(FirebaseUser user){
        if(user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            MakeAccount.this.finish();
        }
    }
}