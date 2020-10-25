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

import com.example.myapplication.PostListFrame;
import com.example.myapplication.R;
import com.example.myapplication.databinding.BudaePostItemBinding;
import com.example.myapplication.databinding.TotalBoardItemBinding;
import com.example.myapplication.model.AlarmDTO;
import com.example.myapplication.model.BoardDTO;
import com.example.myapplication.model.PostDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

public class TotalBudaePost extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseUser user;

    String collection;
    ArrayList<Integer> click;

    public TotalBudaePost(){
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        click = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_total_budae_post, container, false);
        collection = getArguments().getString("collection");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.total_board_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());

        return view;
    }

    private class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.total_board_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final TotalBoardItemBinding binding = ((CustomViewHolder) holder).getBinding();

            binding.name.setText(contentDTOs.get(position).name);
            binding.newPost.setVisibility(View.GONE);

            firestore.collection(contentUidList.get(position)).orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("new", Context.MODE_PRIVATE);
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(QueryDocumentSnapshot doc : value) {
                        final PostDTO postDTO = doc.toObject(PostDTO.class);

                        if(postDTO.timestamp > sharedPreferences.getLong(contentUidList.get(position), 0)){
                            binding.newPost.setVisibility(View.VISIBLE);
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

            if(contentDTOs.get(position).clip.containsKey(user.getUid()) && click.size()== position){
                binding.clip.setImageResource(R.drawable.clip);
                click.add(1);
            }
            else if(click.size()== position){
                binding.clip.setImageResource(R.drawable.not_clip);
                click.add(0);
            }

            binding.clip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(click.get(position) == 1){
                        binding.clip.setImageResource(R.drawable.not_clip);
                        click.add(position, 0);
                        final DocumentReference docRef = firestore.collection(collection)
                                .document(contentUidList.get(position));

                        firestore.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot snapshot = transaction.get(docRef);
                                BoardDTO boardDTO = snapshot.toObject(BoardDTO.class);

                                boardDTO.clip.remove(user.getUid());
                                transaction.set(docRef, boardDTO);
                                return null;
                            }
                        });
                    }
                    else{
                        binding.clip.setImageResource(R.drawable.clip);
                        click.add(position, 1);
                        final DocumentReference docRef = firestore.collection(collection)
                                .document(contentUidList.get(position));

                        firestore.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot snapshot = transaction.get(docRef);
                                BoardDTO boardDTO = snapshot.toObject(BoardDTO.class);

                                boardDTO.clip.put(user.getUid(), true);
                                transaction.set(docRef, boardDTO);
                                return null;
                            }
                        });
                    }

                }
            });


            binding.budaeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("new", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putLong(contentUidList.get(position), System.currentTimeMillis());
                    editor.apply();

                    Intent intent = new Intent(getContext(), PostListFrame.class);
                    intent.putExtra("name", contentDTOs.get(position).name);
                    intent.putExtra("explain", contentDTOs.get(position).explain);
                    intent.putExtra("documentUid", contentUidList.get(position));
                    intent.putExtra("manager", contentDTOs.get(position).manager);
                    startActivity(intent);

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

        private TotalBoardItemBinding binding;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        TotalBoardItemBinding getBinding(){
            return binding;
        }
    }
}