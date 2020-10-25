package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.databinding.ClubPostListItemBinding;
import com.example.myapplication.databinding.UserPostItemBinding;
import com.example.myapplication.model.MyPostDTO;
import com.example.myapplication.model.PostDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserPost extends Fragment {

    private FirebaseUser user;
    private FirebaseFirestore firestore;

    String documentUid;
    ListenerRegistration registration;

    public UserPost(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_post, container, false);

        documentUid = getArguments().getString("documentUid");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.user_post_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());
        return view;
    }

    private class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<MyPostDTO> contentDTOs;
        private ArrayList<String> contentUidList;

        DetailRecyclerViewAdapter(){
            contentDTOs = new ArrayList<>();
            contentUidList = new ArrayList<>();

            firestore.collection(documentUid).orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            contentDTOs.clear();
                            contentUidList.clear();

                            if(value == null) return;
                            for(QueryDocumentSnapshot doc : value){
                                MyPostDTO item = doc.toObject(MyPostDTO.class);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final UserPostItemBinding binding = ((CustomViewHolder) holder).getBinding();

            binding.boardName.setText(contentDTOs.get(position).name);

           firestore.collection(contentDTOs.get(position).documentUid).document(contentDTOs.get(position).postUid).get()
                   .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                       @Override
                       public void onSuccess(DocumentSnapshot documentSnapshot) {
                           if(!documentSnapshot.exists()){
                               delete(contentUidList.get(position));
                           }
                           else{
                               final PostDTO postDTO = documentSnapshot.toObject(PostDTO.class);

                               if(documentUid.equals(user.getUid()+"_Scrap") && !postDTO.scrap.containsKey(user.getUid())){
                                   firestore.collection(documentUid).document(contentUidList.get(position)).delete();
                               }
                               else{
                                   binding.explain.setText(postDTO.explain);
                                   binding.title.setText(postDTO.title);

                                   long postDate = postDTO.timestamp;
                                   Date date = new Date(postDate);
                                   String dateFormat = new SimpleDateFormat("MM/dd").format(date);
                                   binding.postDate.setText(dateFormat);

                                   binding.itemPost.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           Intent intent = new Intent(getContext(), PostMore.class);
                                           intent.putExtra("documentUid", contentDTOs.get(position).documentUid);
                                           intent.putExtra("postUid", contentDTOs.get(position).postUid);
                                           intent.putExtra("manager", "");
                                           intent.putExtra("intentUid", postDTO.uid);
                                           intent.putExtra("annonymous", postDTO.annonymous);
                                           intent.putExtra("name", contentDTOs.get(position).name);
                                           startActivityForResult(intent, position);
                                       }
                                   });
                               }
                           }
                       }
                   });
        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }
    }

    public void delete(String postUid){
        firestore.collection(documentUid).document(postUid).delete();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder{

        private UserPostItemBinding binding;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        UserPostItemBinding getBinding(){
            return binding;
        }
    }
}