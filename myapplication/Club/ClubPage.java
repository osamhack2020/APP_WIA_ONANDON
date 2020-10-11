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

import com.example.myapplication.MyGoalPost.MyGoalPostIng;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ClubPageItemBinding;
import com.example.myapplication.databinding.MyGoalIngItemBinding;
import com.example.myapplication.model.ClubDTO;
import com.example.myapplication.model.MyGoalContentDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ClubPage extends Fragment {

    private FirebaseUser user;
    private FirebaseFirestore firestore;

    String budae;

    public ClubPage(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club_page, container, false);

        budae = getArguments().getString("budae");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.club_page_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());

        return view;
    }

    private class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<ClubDTO> contentDTOs;
        private ArrayList<String> contentUidList;

        DetailRecyclerViewAdapter(){
            contentDTOs = new ArrayList<>();
            contentUidList = new ArrayList<>();

            firestore.collection(budae+"동아리").orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            contentDTOs.clear();
                            contentUidList.clear();

                            if(value == null) return;
                            for(QueryDocumentSnapshot doc : value){
                                ClubDTO item = doc.toObject(ClubDTO.class);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_page_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final ClubPageItemBinding binding = ((CustomViewHolder) holder).getBinding();

            final String intentDocument = contentUidList.get(position);
            final String name = contentDTOs.get(position).name;

            binding.explain.setText(contentDTOs.get(position).explain);
            binding.name.setText(contentDTOs.get(position).name);
            binding.kindFirst.setVisibility(View.GONE);
            binding.kindSecond.setVisibility(View.GONE);
            binding.kindThird.setVisibility(View.GONE);

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
            binding.questionCount.setText(contentDTOs.get(position).questionCount+"");

            binding.clubItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ClubPageMore.class);
                    intent.putExtra("documentUid", intentDocument);
                    intent.putExtra("budae", budae);
                    intent.putExtra("name", name);
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

        private ClubPageItemBinding binding;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        ClubPageItemBinding getBinding(){
            return binding;
        }
    }
}