package com.example.myapplication.Diet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.BottomNavigation.MyGoal;
import com.example.myapplication.R;
import com.example.myapplication.model.DietDTO;
import com.example.myapplication.model.MyGoalContentDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class Diet extends AppCompatActivity {

    final int TIME_DIVIDE = 24*60*60*1000;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    TextView makeDiet;

    String budae;
    String army;

    ViewPager pager;
    ArrayList<Fragment> frag_list;
    int setPager = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        makeDiet = (TextView)findViewById(R.id.make_diet);
        pager = (ViewPager)findViewById(R.id.diet);

        makeDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Diet.this, MakeDiet.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        army = intent.getStringExtra("army");
        budae = intent.getStringExtra("budae");

        firestore.collection(army+budae+"_diet").orderBy("postDay", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    ArrayList<Fragment> frag_list;
                    Calendar calendar;
                    int index = 0;

                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        frag_list = new ArrayList<Fragment>();
                        calendar = Calendar.getInstance();

                        for(QueryDocumentSnapshot doc : value){
                            if(value.size() != 0) {
                                DietDTO dietDTO = doc.toObject(DietDTO.class);

                                if(dietDTO.postDay == calendar.getTimeInMillis()/TIME_DIVIDE){
                                    setPager = index;
                                }

                                DietPager dietPager = new DietPager();
                                Bundle bundle = new Bundle(4);
                                bundle.putLong("time", dietDTO.postDay);
                                bundle.putStringArrayList("breakfast", dietDTO.breakfast);
                                bundle.putStringArrayList("lunch", dietDTO.lunch);
                                bundle.putStringArrayList("dinner", dietDTO.dinner);
                                dietPager.setArguments(bundle);
                                frag_list.add(dietPager);
                                index++;
                            }
                            else{
                                NoneDiet noneDiet = new NoneDiet();
                                frag_list.add(noneDiet);
                            }
                        }

                        FragmentManager manager = getSupportFragmentManager();
                        PagerAdapter pagerAdapter = new PagerAdapter(manager, frag_list.size());
                        pager.setAdapter(pagerAdapter);
                        pager.setCurrentItem(setPager);

                        // viewpager 양쪽 미리보기 설정
                        int dpValue = 30;
                        float d = getResources().getDisplayMetrics().density;
                        int margin = (int) (dpValue * d);

                        pager.setClipToPadding(false);
                        pager.setPadding(margin, 0, margin, 0);
                        pager.setPageMargin(margin/2);

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
                });

    }
}