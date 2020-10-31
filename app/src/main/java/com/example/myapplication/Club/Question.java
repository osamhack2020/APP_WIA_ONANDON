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
import android.widget.Toast;

import com.example.myapplication.Club.ClubPage;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ClubPageItemBinding;
import com.example.myapplication.databinding.CommentBinding;
import com.example.myapplication.databinding.QuestionBinding;
import com.example.myapplication.model.ClubDTO;
import com.example.myapplication.model.MyGoalContentDTO;
import com.example.myapplication.model.QuestionDTO;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Question extends Fragment {

    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private long deletePressedTime = 0;

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
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final QuestionBinding binding = ((CustomViewHolder) holder).getBinding();
            final int intentPosition = position;

            binding.addAnswer.setVisibility(View.GONE);
            binding.addAnswerText.setVisibility(View.GONE);
            binding.answer.setVisibility(View.GONE);
            binding.answerTitle.setVisibility(View.GONE);
            binding.update.setVisibility(View.GONE);

            if(user.getUid().equals(manager) && contentDTOs.size() == 0){
                binding.questionUser.setText("질문 글이 없습니다.");
                binding.questionDate.setText("");
                binding.questionExplain.setText("");
                binding.deleteQuestion.setVisibility(View.INVISIBLE);
            }
            else {
                if (user.getUid().equals(manager) && contentDTOs.get(position).isAnswer != 1) {
                    binding.addAnswer.setVisibility(View.VISIBLE);
                    binding.addAnswerText.setVisibility(View.VISIBLE);

                    binding.addAnswer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), AddAnswer.class);
                            intent.putExtra("documentUid", documentUid);
                            intent.putExtra("questionUid", contentUidList.get(intentPosition));
                            startActivity(intent);
                        }
                    });
                }

                if (contentDTOs.get(position).isAnswer == 1) {
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
                                binding.questionUser.setText(userDTO.army + " " + userDTO.budae + " " + userDTO.rank + " " + userDTO.name);
                            }
                        });

                if(user.getUid().equals(manager) && contentDTOs.get(position).isAnswer == 1){
                    binding.update.setVisibility(View.VISIBLE);
                    binding.update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), AddAnswer.class);
                            intent.putExtra("documentUid", documentUid);
                            intent.putExtra("questionUid", contentUidList.get(intentPosition));
                            intent.putExtra("update", 1);
                            intent.putExtra("answer", contentDTOs.get(position).answer);
                            startActivity(intent);
                        }
                    });
                }

                final String delete = contentUidList.get(intentPosition);
                if(user.getUid().equals(contentDTOs.get(position).uid) || user.getUid().equals(manager)){
                    binding.deleteQuestion.setVisibility(View.VISIBLE);
                    binding.deleteQuestion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (System.currentTimeMillis() > deletePressedTime + 2000) {
                                deletePressedTime = System.currentTimeMillis();
                                Toast.makeText(getActivity(), "버튼을 한번 더 누르시면 삭제됩니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(System.currentTimeMillis() <= deletePressedTime + 2000) {
                                firestore.collection(documentUid+"_question").document(delete).delete();
                                Toast.makeText(getActivity(), "질문을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                deletePressedTime = 0;

                                final DocumentReference docRef = firestore.collection(budae+"동아리").document(documentUid);
                                firestore.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                        DocumentSnapshot snapshot = transaction.get(docRef);
                                        ClubDTO clubDTO = snapshot.toObject(ClubDTO.class);
                                        clubDTO.questionCount = clubDTO.questionCount-1;

                                        transaction.set(docRef, clubDTO);
                                        return null;
                                    }
                                });
                            }
                        }
                    });
                }

            }
        }

        @Override
        public int getItemCount() {
            if(user.getUid().equals(manager) && contentDTOs.size() == 0){
                return 1;
            }
            else return contentDTOs.size();
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