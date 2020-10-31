package com.example.myapplication.MyGoalPost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.myapplication.R;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class MyGoalActivity extends AppCompatActivity {

    final int SEARCH = 1;
    final int NEW_POST = 2;

    Toolbar toolbar;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_goal);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        MyGoalPostIng sub1 = new MyGoalPostIng();
        Bundle bundle = new Bundle(1);
        bundle.putInt("isSearch", 0);
        sub1.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.my_goal, sub1);
        tran.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mygoal_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.search:
                Intent intent = new Intent(this, MyGoalSearch.class);
                startActivityForResult(intent, SEARCH);
                break;
            case R.id.newpost:
                firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                                Intent intentPost = new Intent(MyGoalActivity.this, NewPost.class);
                                intentPost.putExtra("army", userDTO.army);
                                intentPost.putExtra("budae", userDTO.budae);
                                intentPost.putExtra("rank", userDTO.rank);
                                startActivityForResult(intentPost, NEW_POST);
                            }
                        });
                break;
        }

        return true;
    }
}