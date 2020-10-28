package com.example.myapplication.BottomNavigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.Activity.ActivityFrame;
import com.example.myapplication.Activity.MyActivity;
import com.example.myapplication.Club.ClubActivity;
import com.example.myapplication.R;
import com.example.myapplication.UserPostList;
import com.example.myapplication.market.MarketActivity;
import com.example.myapplication.model.ActivityDTO;
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

public class DashboardFragment extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    ImageView addBudae;
    ImageView addTotalBudae;
    RelativeLayout clubLayout;
    RelativeLayout myPost;
    RelativeLayout scrap;
    RelativeLayout myComment;
    RelativeLayout marketLayout;
    RelativeLayout activityLayout;
    RelativeLayout activityScrap;

    TextView marketNewPost;
    TextView activityNewPost;

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        activityNewPost = (TextView)view.findViewById(R.id.new_post1);
        marketNewPost = (TextView)view.findViewById(R.id.new_post2);
        activityNewPost.setVisibility(View.GONE);
        marketNewPost.setVisibility(View.GONE);

        // 동아리 게시판으로 이동시켜 주는 클릭 이벤트 리스너
        clubLayout = (RelativeLayout)view.findViewById(R.id.club_layout);
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

        addBudae = (ImageView)view.findViewById(R.id.add_budae);
        addBudae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MakeBoard.class);
                startActivity(intent);
            }
        });

        myPost = (RelativeLayout)view.findViewById(R.id.my_post);
        myPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserPostList.class);
                intent.putExtra("name", "내가 쓴 글");
                intent.putExtra("documentUid", auth.getCurrentUser().getUid()+"_MyPost");
                startActivity(intent);
            }
        });

        scrap = (RelativeLayout)view.findViewById(R.id.scrap);
        scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserPostList.class);
                intent.putExtra("name", "스크랩");
                intent.putExtra("documentUid", auth.getCurrentUser().getUid()+"_Scrap");
                startActivity(intent);
            }
        });

        activityScrap = (RelativeLayout)view.findViewById(R.id.activity_scrap);
        activityScrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyActivity.class);
                startActivity(intent);
            }
        });

        myComment = (RelativeLayout)view.findViewById(R.id.my_comment_post);
        myComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserPostList.class);
                intent.putExtra("name", "내가 댓글 쓴 글");
                intent.putExtra("documentUid", auth.getCurrentUser().getUid()+"_MyComment");
                startActivity(intent);
            }
        });

        marketLayout = (RelativeLayout)view.findViewById(R.id.market_layout);
        marketLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("new", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putLong("purchase", System.currentTimeMillis());
                editor.apply();

                Intent intent = new Intent(getContext(), MarketActivity.class);
                startActivity(intent);

                if(marketNewPost.getVisibility() == View.VISIBLE){
                    marketNewPost.setVisibility(View.GONE);
                }
            }
        });

        firestore.collection("purchase").orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("new", Context.MODE_PRIVATE);
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.size() == 0){
                    marketNewPost.setVisibility(View.GONE);
                }
                for(QueryDocumentSnapshot doc : value) {
                    PostDTO postDTO = doc.toObject(PostDTO.class);
                    if(postDTO.timestamp > sharedPreferences.getLong("purchase", 0)){
                        marketNewPost.setVisibility(View.VISIBLE);
                    }else{
                        marketNewPost.setVisibility(View.GONE);
                    }

                    sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                            if(s.equals("purchase")){
                                marketNewPost.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        activityLayout = (RelativeLayout)view.findViewById(R.id.activity_layout);
        activityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("new", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putLong("Activity", System.currentTimeMillis());
                editor.apply();

                Intent intent = new Intent(getContext(), ActivityFrame.class);
                startActivity(intent);

                if(activityNewPost.getVisibility() == View.VISIBLE){
                    activityNewPost.setVisibility(View.GONE);
                }
            }
        });

        firestore.collection("Activity").orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("new", Context.MODE_PRIVATE);
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.size() == 0){
                    activityNewPost.setVisibility(View.GONE);
                }
                for(QueryDocumentSnapshot doc : value) {
                    ActivityDTO activityDTO = doc.toObject(ActivityDTO.class);

                    if(activityDTO.timestamp > sharedPreferences.getLong("Activity", 0)){
                        activityNewPost.setVisibility(View.VISIBLE);
                    }
                    else{
                        activityNewPost.setVisibility(View.GONE);
                    }

                    sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                            if(s.equals("Activity")){
                                activityNewPost.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        addTotalBudae = (ImageView)view.findViewById(R.id.add_total_budae);
        addTotalBudae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MakeTotalBoard.class);
                startActivity(intent);
            }
        });

        // 사용자의 부대 정보를 추출하여, 사용자가 소속된 부대의 게시판 정보를 불러온다.
        firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressBar.setVisibility(View.VISIBLE);
                        UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                        // 사용자들이 커스텀한 부대 게시판의 리스트를 보여주는 프래그먼트 생성
                        TotalBudaePost budaePost = new TotalBudaePost();
                        Bundle bundle = new Bundle(1);
                        bundle.putString("collection", userDTO.budae+"게시판");
                        budaePost.setArguments(bundle);

                        FragmentManager manager = getChildFragmentManager();
                        FragmentTransaction tran = manager.beginTransaction();
                        tran.replace(R.id.budae_content, budaePost);
                        tran.commit();
                        progressBar.setVisibility(View.GONE);
                    }
                });

        TotalBudaePost totalBudaePost = new TotalBudaePost();
        Bundle bundle = new Bundle(1);
        bundle.putString("collection", "total_board");
        totalBudaePost.setArguments(bundle);

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.total_content, totalBudaePost);
        tran.commit();

        return view;
    }

    // 동아리 게시판으로 이동시켜 주는 함수
    public void movinClubPage(String budae){
        Intent intent = new Intent(getActivity(), ClubActivity.class);
        intent.putExtra("budae", budae);
        startActivity(intent);
    }
}