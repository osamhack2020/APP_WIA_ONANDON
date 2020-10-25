package com.example.myapplication.market;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.Club.ClubActivity;
import com.example.myapplication.Club.ClubPageFrame;
import com.example.myapplication.Club.ClubTotalPost;
import com.example.myapplication.MyGoalPost.MyGoalSearch;
import com.example.myapplication.MyGoalPost.NewPost;
import com.example.myapplication.NewPostPublic;
import com.example.myapplication.PostList;
import com.example.myapplication.PostListFrame;
import com.example.myapplication.PostSearch;
import com.example.myapplication.R;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class MarketActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    ArrayList<Fragment> frag_list = new ArrayList<Fragment>();
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager pager;

    int tabClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("구매글"));
        tabLayout.addTab(tabLayout.newTab().setText("판매글"));

        PostList postListPurchase = new PostList();
        Bundle bundle1 = new Bundle(3);
        bundle1.putString("documentUid", "purchase");
        bundle1.putString("name", "장터 게시판");
        bundle1.putString("manager", "");
        postListPurchase.setArguments(bundle1);
        frag_list.add(postListPurchase);

        PostList postListSelling = new PostList();
        Bundle bundle2 = new Bundle(3);
        bundle2.putString("documentUid", "selling");
        bundle2.putString("name", "장터 게시판");
        bundle2.putString("manager", "");
        postListSelling.setArguments(bundle2);
        frag_list.add(postListSelling);

        pager = (ViewPager)findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab){
                pager.setCurrentItem(tab.getPosition());
                tabClick = tab.getPosition();
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
                if(tabClick == 0){
                    Intent intent = new Intent(this, PostSearch.class);
                    intent.putExtra("documentUid", "purchase");
                    intent.putExtra("name", "장터 게시판");
                    intent.putExtra("manager", "");
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(this, PostSearch.class);
                    intent.putExtra("documentUid", "selling");
                    intent.putExtra("name", "장터 게시판");
                    intent.putExtra("manager", "");
                    startActivity(intent);
                }
                break;
            case R.id.newpost:
                if(tabClick == 0){
                    firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                                    Intent intentPost = new Intent(MarketActivity.this, NewPostPublic.class);
                                    intentPost.putExtra("army", userDTO.army);
                                    intentPost.putExtra("budae", userDTO.budae);
                                    intentPost.putExtra("rank", userDTO.rank);
                                    intentPost.putExtra("documentUid", "purchase");
                                    intentPost.putExtra("name", "장터 게시판");
                                    startActivity(intentPost);
                                }
                            });
                }
                else{
                    firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                                    Intent intentPost = new Intent(MarketActivity.this, NewPostPublic.class);
                                    intentPost.putExtra("army", userDTO.army);
                                    intentPost.putExtra("budae", userDTO.budae);
                                    intentPost.putExtra("rank", userDTO.rank);
                                    intentPost.putExtra("documentUid", "selling");
                                    intentPost.putExtra("name", "장터 게시판");
                                    startActivity(intentPost);
                                }
                            });
                }
                break;
        }

        return true;
    }
}