package com.example.myapplication.BottomNavigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.Club.ClubActivity;
import com.example.myapplication.MyGoalPost.MyGoalActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.MyGoalContentDTO;
import com.example.myapplication.model.PostDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    ViewPager pager;
    RelativeLayout clubLayout;
    TextView textview;
    TextView preview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        pager = (ViewPager)view.findViewById(R.id.pager);
        textview = (TextView)view.findViewById(R.id.budaePost);
        clubLayout = (RelativeLayout)view.findViewById(R.id.club_layout);
        preview = (TextView)view.findViewById(R.id.preview);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        textview.setPaintFlags(textview.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

        clubLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                                movinClubPage(userDTO.budae);
                            }
                        });
            }
        });

        firestore.collection("MyGoal").orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            ArrayList<Fragment> frag_list;

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                frag_list = new ArrayList<Fragment>();

                TodayDiet todayDiet = new TodayDiet();
                frag_list.add(todayDiet);

                for(QueryDocumentSnapshot doc : value){
                    if(value.size() != 0) {
                        MyGoalContentDTO item = doc.toObject(MyGoalContentDTO.class);

                        String intentUid = item.uid;
                        MyGoal sub = new MyGoal();
                        Bundle bundle = new Bundle(1);
                        bundle.putString("uid", intentUid);
                        sub.setArguments(bundle);
                        frag_list.add(sub);
                    }
                }

                FragmentManager manager = getChildFragmentManager();
                PagerAdapter pagerAdapter = new PagerAdapter(manager, frag_list.size());
                pager.setAdapter(pagerAdapter);
                pager.setCurrentItem(0);

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

        firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                        firestore.collection(userDTO.budae+"동아리게시판").orderBy("timestamp", Query.Direction.DESCENDING)
                                .limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(value.size() == 0){
                                    preview.setText("게시물이 없습니다.");
                                }
                                for(QueryDocumentSnapshot doc : value) {
                                    PostDTO postDTO = doc.toObject(PostDTO.class);
                                    preview.setText(postDTO.explain);

                                }
                            }
                        });

                        BudaePost budaePost = new BudaePost();
                        Bundle bundle = new Bundle(1);
                        bundle.putString("collection", userDTO.budae+"게시판");
                        budaePost.setArguments(bundle);

                        FragmentManager manager = getChildFragmentManager();
                        FragmentTransaction tran = manager.beginTransaction();
                        tran.replace(R.id.budae_content, budaePost);
                        tran.commit();
                    }
                });

        BudaePost totalBudaePost = new BudaePost();
        Bundle bundle = new Bundle(1);
        bundle.putString("collection", "total_board");
        totalBudaePost.setArguments(bundle);

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.total_content, totalBudaePost);
        tran.commit();

        return view;
    }

    public void movinClubPage(String budae){
        Intent intent = new Intent(getActivity(), ClubActivity.class);
        intent.putExtra("budae", budae);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}