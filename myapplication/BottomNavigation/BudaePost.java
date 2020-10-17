package com.example.myapplication.BottomNavigation;

import android.content.Intent;
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

public class BudaePost extends Fragment {

    private FirebaseUser user;
    private FirebaseFirestore firestore;

    String budae;

    public BudaePost(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budae_post, container, false);
        budae = getArguments().getString("budae");

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

            firestore.collection(budae + "게시판").orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            contentDTOs.clear();
                            contentUidList.clear();

                            if (value == null) return;
                            for (QueryDocumentSnapshot doc : value) {
                                BoardDTO item = doc.toObject(BoardDTO.class);
                                contentDTOs.add(item);
                                contentUidList.add(doc.getId());
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

            firestore.collection(contentUidList.get(position)).orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.size() == 0){
                        binding.preview.setText("게시물이 없습니다.");
                    }
                    for(QueryDocumentSnapshot doc : value) {
                        PostDTO postDTO = doc.toObject(PostDTO.class);
                        binding.preview.setText(postDTO.explain);
                    }
                }
            });

            binding.budaeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), PostListFrame.class);
                    intent.putExtra("name", contentDTOs.get(position).name);
                    intent.putExtra("explain", contentDTOs.get(position).explain);
                    intent.putExtra("documentUid", contentUidList.get(position));
                    startActivity(intent);
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