package com.example.myapplication.Club;

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

import com.example.myapplication.R;
import com.example.myapplication.databinding.ClubPageItemBinding;
import com.example.myapplication.databinding.ClubPostListItemBinding;
import com.example.myapplication.model.ClubDTO;
import com.example.myapplication.model.PostDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClubPostList extends Fragment {

    private FirebaseUser user;
    private FirebaseFirestore firestore;

    String name;
    String manager;
    String budae;

    public ClubPostList(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_post_list, container, false);

        name = getArguments().getString("name");
        manager = getArguments().getString("manager");
        budae = getArguments().getString("budae");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.club_post_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());

        return view;
    }

    private class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<PostDTO> contentDTOs;
        private ArrayList<String> contentUidList;

        DetailRecyclerViewAdapter(){
            contentDTOs = new ArrayList<>();
            contentUidList = new ArrayList<>();

            firestore.collection(budae+"동아리게시판").whereEqualTo("name", name)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            contentDTOs.clear();
                            contentUidList.clear();

                            if(value == null) return;
                            for(QueryDocumentSnapshot doc : value){
                                PostDTO item = doc.toObject(PostDTO.class);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_post_list_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final ClubPostListItemBinding binding = ((CustomViewHolder) holder).getBinding();

            binding.explain.setText(contentDTOs.get(position).explain);
            binding.title.setText(contentDTOs.get(position).title);
            binding.kindFirst.setVisibility(View.GONE);
            binding.kindSecond.setVisibility(View.GONE);
            binding.kindThird.setVisibility(View.GONE);
            binding.isPhoto.setVisibility(View.GONE);

            if(contentDTOs.get(position).kind.containsKey("first")){
                binding.kindFirst.setText(contentDTOs.get(position).kind.get("first"));
                binding.kindFirst.setVisibility(View.VISIBLE);
            }
            if(contentDTOs.get(position).kind.containsKey("second")){
                binding.kindSecond.setText(contentDTOs.get(position).kind.get("second"));
                binding.kindSecond.setVisibility(View.VISIBLE);
            }
            if(contentDTOs.get(position).kind.containsKey("third")){
                binding.kindThird.setText(contentDTOs.get(position).kind.get("third"));
                binding.kindThird.setVisibility(View.VISIBLE);
            }
            binding.commentCountShow.setText(contentDTOs.get(position).commentCount+"");
            binding.favoriteCountShow.setText(contentDTOs.get(position).favoriteCount+"");
            binding.clubName.setText(name);

            long postDate = contentDTOs.get(position).timestamp;
            Date date = new Date(postDate);
            String dateFormat = new SimpleDateFormat("MM/dd").format(date);
            binding.postDate.setText(dateFormat);

            if(contentDTOs.get(position).favorites.containsKey(user.getUid())){
                binding.favoriteShow.setImageResource(R.drawable.heart);
            }
            else{
                binding.favoriteShow.setImageResource(R.drawable.empty_heart);
            }

            if(contentDTOs.get(position).isPhoto == 1){
                binding.isPhoto.setVisibility(View.VISIBLE);
            }

            binding.itemPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ClubPostItemMore.class);
                    intent.putExtra("name", name);
                    intent.putExtra("manager", manager);
                    intent.putExtra("postUid", contentUidList.get(position));
                    intent.putExtra("budae", budae);
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

        private ClubPostListItemBinding binding;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        ClubPostListItemBinding getBinding(){
            return binding;
        }
    }
}