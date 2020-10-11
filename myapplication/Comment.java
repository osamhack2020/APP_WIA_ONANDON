package com.example.myapplication;

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

import com.example.myapplication.databinding.CommentBinding;
import com.example.myapplication.model.CommentDTO;
import com.example.myapplication.model.MyGoalContentDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Comment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private long deletePressedTime = 0;
    String documentUid;

    public Comment(){
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        documentUid = getArguments().getString("document");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.comment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());

        return view;
    }

    private class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<CommentDTO> commentDTOs;
        private ArrayList<String> documentIDs;

        DetailRecyclerViewAdapter(){
            commentDTOs = new ArrayList<>();
            documentIDs = new ArrayList<>();

            firestore.collection(documentUid).orderBy("timeStamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    commentDTOs.clear();
                    documentIDs.clear();

                    if(value == null) return;
                    for(QueryDocumentSnapshot doc : value){
                        CommentDTO item = doc.toObject(CommentDTO.class);
                        commentDTOs.add(item);
                        documentIDs.add(doc.getId());
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final CommentBinding binding = ((CustomViewHolder)holder).getBinding();

            if(commentDTOs.size() == 0){
                binding.commentUser.setText("댓글이 없습니다. 댓글을 달아주세요.");
                binding.commentDate.setText("");
                binding.commentExplain.setText("");
                binding.deleteComment.setVisibility(View.INVISIBLE);
            }
            else {
                binding.commentExplain.setText(commentDTOs.get(position).comment);

                firestore.collection("UserInfo").document(commentDTOs.get(position).uid).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                                binding.commentUser.setText(userDTO.army + " " + userDTO.budae + " " + userDTO.rank + " " + userDTO.name);
                            }
                        });

                long postDate = commentDTOs.get(position).timeStamp;
                Date date = new Date(postDate);
                String dateFormat = new SimpleDateFormat("MM/dd").format(date);
                binding.commentDate.setText(dateFormat);

                final String delete = documentIDs.get(position);

                if (user.getUid().equals(commentDTOs.get(position).uid)) {
                    binding.deleteComment.setVisibility(View.VISIBLE);
                    binding.deleteComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (System.currentTimeMillis() > deletePressedTime + 2000) {
                                deletePressedTime = System.currentTimeMillis();
                                Toast.makeText(getActivity(), "버튼을 한번 더 누르시면 삭제됩니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(System.currentTimeMillis() <= deletePressedTime + 2000) {
                                firestore.collection(documentUid).document(delete).delete();
                                Toast.makeText(getActivity(), "댓글을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                deletePressedTime = 0;

                                final DocumentReference docRef = firestore.collection("MyGoal").document(documentUid);
                                firestore.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                        DocumentSnapshot snapshot = transaction.get(docRef);
                                        MyGoalContentDTO myGoalContentDTO = snapshot.toObject(MyGoalContentDTO.class);
                                        myGoalContentDTO.commentCount = myGoalContentDTO.commentCount-1;

                                        transaction.set(docRef, myGoalContentDTO);
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
            if(commentDTOs.size()==0) return 1;
            else return commentDTOs.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder{

            private CommentBinding binding;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);

                binding = DataBindingUtil.bind(itemView);
            }

            CommentBinding getBinding(){
                return binding;
            }
        }
    }
}