package com.example.myapplication.BottomNavigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Club.ClubPostList;
import com.example.myapplication.PostListFrame;
import com.example.myapplication.R;
import com.example.myapplication.databinding.BudaePostItemBinding;
import com.example.myapplication.databinding.ClubPostListItemBinding;
import com.example.myapplication.model.BoardDTO;
import com.example.myapplication.model.PostDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/*
[BudaePost.java]
BudaePost 파일은 사용자가 소속된 부대의 게시판 리스트를
리사이클러뷰로 보여주는 자바 파일입니다.

1. 'R.layout.budae_post_item' 레이아웃이 하나의 게시판을 구성하며,
레이아웃을 누르면 해당 게시판으로 이동됩니다.
2. 게시판으로 이동할 때, SharedPreferences에 게시판으로 이동한 시간을 저장합니다.
BudaePost.class는 각 게시판에 새로운 게시물이 업로드 될 때마다 'new' 표시로
새로운 게시물이 업로드 되없음을 사용자에게 알려주는데, SharedPreferences에 저장된
시간 값은 이러한 기능을 구현하는 데 사용됩니다.
 */

public class BudaePost extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseUser user;

    String collection;

    public BudaePost(){
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budae_post, container, false);
        collection = getArguments().getString("collection");


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.budae_post_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());
        return view;
    }

    private class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<BoardDTO> contentDTOs;
        private ArrayList<String> contentUidList;

        DetailRecyclerViewAdapter() {
            contentDTOs = new ArrayList<>();
            contentUidList = new ArrayList<>();

            firestore.collection(collection).orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            contentDTOs.clear();
                            contentUidList.clear();

                            if (value == null) return;
                            for (QueryDocumentSnapshot doc : value) {
                                BoardDTO item = doc.toObject(BoardDTO.class);
                                if(item.clip.containsKey(user.getUid())){
                                    contentDTOs.add(item);
                                    contentUidList.add(doc.getId());
                                }
                            }

                            notifyDataSetChanged();
                        }
                    });
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budae_post_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final BudaePostItemBinding binding = ((CustomViewHolder) holder).getBinding();

            binding.name.setText(contentDTOs.get(position).name);
            binding.newPost.setVisibility(View.GONE);

            firestore.collection(contentUidList.get(position)).orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("new", Context.MODE_PRIVATE);
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.size() == 0){
                        binding.preview.setText("게시물이 없습니다.");
                        binding.newPost.setVisibility(View.GONE);
                    }
                    for(QueryDocumentSnapshot doc : value) {
                        final PostDTO postDTO = doc.toObject(PostDTO.class);
                        binding.preview.setText(postDTO.explain);

                        if(postDTO.timestamp > sharedPreferences.getLong(contentUidList.get(position), 0)){
                            binding.newPost.setVisibility(View.VISIBLE);
                        }else{
                            binding.newPost.setVisibility(View.GONE);
                        }

                        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                            @Override
                            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                                if(s.equals(contentUidList.get(position))){
                                    binding.newPost.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });

            binding.budaeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // 게시판으로 들어간 시간을 SharedPreferences에 저장
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("new", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putLong(contentUidList.get(position), System.currentTimeMillis());
                    editor.apply();

                    // 해당 게시판으로 이동
                    Intent intent = new Intent(getContext(), PostListFrame.class);
                    intent.putExtra("name", contentDTOs.get(position).name);
                    intent.putExtra("explain", contentDTOs.get(position).explain);
                    intent.putExtra("documentUid", contentUidList.get(position));
                    intent.putExtra("manager", contentDTOs.get(position).manager);
                    startActivity(intent);


                    // 'new' 표시가 떴으면 게시판으로 들어가는 순간 숨김
                    if(binding.newPost.getVisibility() == View.VISIBLE){
                        binding.newPost.setVisibility(View.GONE);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder{

        private BudaePostItemBinding binding;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        BudaePostItemBinding getBinding(){
            return binding;
        }
    }

}