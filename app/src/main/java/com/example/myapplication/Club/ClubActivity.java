package com.example.myapplication.Club;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ClubActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    TextView toolbarTitle;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager pager;

    Intent intent;
    String budae;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        intent = getIntent();
        budae = intent.getStringExtra("budae");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                        toolbarTitle.setText(userDTO.army+ " " +userDTO.budae+" 동아리 게시판");
                    }
                });

        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("게시물"));
        tabLayout.addTab(tabLayout.newTab().setText("동아리 페이지"));

        pager = (ViewPager)findViewById(R.id.pager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab){
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab){

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab){

            }
        });

        int set = intent.getIntExtra("set", 0);
        if(set == 1){
            pager.setCurrentItem(set);
        }
    }

    class PagerAdapter extends FragmentStatePagerAdapter {
        int numOfTabs;

        public PagerAdapter(@NonNull FragmentManager fm, int numOfTabs) {
            super(fm);
            this.numOfTabs = numOfTabs;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0 :
                    Bundle bundle = new Bundle(1);
                    bundle.putString("budae", budae);
                    ClubTotalPost total = new ClubTotalPost();
                    total.setArguments(bundle);
                    return total;
                case 1 :
                    ClubPageFrame club = new ClubPageFrame();
                    return club;
            }
            return null;
            // return frag_list.get(position);
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences("new", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putLong("Club", System.currentTimeMillis());
        editor.apply();

        finish();
    }
}