package com.example.myapplication.Club;

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

import com.example.myapplication.Club.ClubPage;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ClubPageItemBinding;
import com.example.myapplication.databinding.CommentBinding;
import com.example.myapplication.databinding.QuestionBinding;
import com.example.myapplication.model.ClubDTO;
import com.example.myapplication.model.QuestionDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Question extends Fragment {

    private FirebaseUser user;
    private FirebaseFirestore firestore;

    String budae;
    String documentUid;
    String manager;

    public Question() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quesiton, container, false);

        budae = getArguments().getString("budae");
        documentUid = getArguments().getString("documentUid");
        manager = getArguments().getString("manager");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.club_question_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());

        return view;
    }

    private class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<QuestionDTO> contentDTOs;
        private ArrayList<String> contentUidList;

        DetailRecyclerViewAdapter(){
            contentDTOs = new ArrayList<>();
            contentUidList = new ArrayList<>();

            firestore.collection(documentUid+"_question").orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            contentDTOs.clear();
                            contentUidList.clear();

                            if(value == null) return;
                            for(QueryDocumentSnapshot doc : value){
                                QuestionDTO item = doc.toObject(QuestionDTO.class);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final QuestionBinding binding = ((CustomViewHolder) holder).getBinding();

            binding.addAnswer.setVisibility(View.GONE);
            binding.addAnswerText.setVisibility(View.GONE);
            binding.answer.setVisibility(View.GONE);
            binding.answerTitle.setVisibility(View.GONE);


            if(user.getUid().equals(manager) && contentDTOs.get(position).isAnswer != 1){
                binding.addAnswer.setVisibility(View.VISIBLE);
                binding.addAnswerText.setVisibility(View.VISIBLE);
            }

            if(contentDTOs.get(position).isAnswer == 1){
                binding.answer.setVisibility(View.VISIBLE);
                binding.answerTitle.setVisibility(View.VISIBLE);
                binding.answer.setText(contentDTOs.get(position).answer);
            }

            binding.questionExplain.setText(contentDTOs.get(position).explain);

            long postDate = contentDTOs.get(position).timestamp;
            Date date = new Date(postDate);
            String dateFormat = new SimpleDateFormat("MM/dd").format(date);
            binding.questionDate.setText(dateFormat);

            firestore.collection("UserInfo").document(contentDTOs.get(position).uid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                            binding.questionUser.setText(userDTO.army + " " + userDTO.budae + " "+userDTO.rank+" "+userDTO.name);
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder{

        private QuestionBinding binding;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        QuestionBinding getBinding(){
            return binding;
        }
    }
}