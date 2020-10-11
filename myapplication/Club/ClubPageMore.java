package com.example.myapplication.Club;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

public class ClubPageMore extends AppCompatActivity {

    String documentUid;
    String budae;
    String name;

    ArrayList<Fragment> frag_list = new ArrayList<Fragment>();
    TextView toolbarTitle;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_page_more);

        Intent intent = getIntent();
        documentUid = intent.getStringExtra("documentUid");
        budae = intent.getStringExtra("budae");
        name = intent.getStringExtra("name");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        toolbarTitle.setText(name);

        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("소개"));
        tabLayout.addTab(tabLayout.newTab().setText("게시물"));

        ClubPageIntroduce clubPageIntroduce = new ClubPageIntroduce();
        Bundle bundle = new Bundle(2);
        bundle.putString("documentUid", documentUid);
        bundle.putString("budae", budae);
        clubPageIntroduce.setArguments(bundle);
        frag_list.add(clubPageIntroduce);

        Practice sub = new Practice();
        sub.string = "1 번 째 프래그 먼트";
        frag_list.add(sub);

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
            return frag_list.get(position);
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }
}