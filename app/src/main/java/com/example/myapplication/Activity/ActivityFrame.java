package com.example.myapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.PostSearch;
import com.example.myapplication.R;
import com.example.myapplication.model.ManagerDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Objects;


public class ActivityFrame extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        ActivityList activityList = new ActivityList();
        Bundle bundle = new Bundle(1);
        bundle.putInt("isSearch", 0);
        activityList.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.post_content, activityList);
        tran.commit();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences("new", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putLong("Activity", System.currentTimeMillis());
        editor.apply();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        firestore.collection("Activity_manager").document("manager").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ManagerDTO managerDTO = documentSnapshot.toObject(ManagerDTO.class);

                        if (managerDTO.manager.equals(auth.getCurrentUser().getUid())) {
                            MenuInflater inflater = getMenuInflater();
                            inflater.inflate(R.menu.mygoal_menu, menu);
                        }
                        else{
                            MenuInflater inflater = getMenuInflater();
                            inflater.inflate(R.menu.my_goal_search_menu, menu);
                        }
                    }
                });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.search:
            case R.id.real_search:
                Intent intent = new Intent(this, ActivitySearch.class);
                startActivity(intent);
                break;
            case R.id.newpost:
                Intent intent2 = new Intent(this, NewActivity.class);
                startActivity(intent2);
                break;
        }
        return true;
    }
}