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
import android.widget.Toast;

import com.example.myapplication.PostList;
import com.example.myapplication.PostMore;
import com.example.myapplication.R;
import com.example.myapplication.databinding.AlarmBinding;
import com.example.myapplication.databinding.ClubPostListItemBinding;
import com.example.myapplication.model.AlarmDTO;
import com.example.myapplication.model.PostDTO;
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

public class NotificationFragment extends Fragment {

    private FirebaseUser user;
    private FirebaseFirestore firestore;

    public NotificationFragment(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.notification_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());

        return view;
    }

    private class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<AlarmDTO> contentDTOs;
        private ArrayList<String> contentUidList;

        DetailRecyclerViewAdapter() {
            contentDTOs = new ArrayList<>();
            contentUidList = new ArrayList<>();

            firestore.collection(user.getUid()+"_Alarm").orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            contentDTOs.clear();
                            contentUidList.clear();

                            if (value == null) return;
                            for (QueryDocumentSnapshot doc : value) {
                                AlarmDTO item = doc.toObject(AlarmDTO.class);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final AlarmBinding binding = ((CustomViewHolder) holder).getBinding();

            long postDate = contentDTOs.get(position).timestamp;
            Date date = new Date(postDate);
            String dateFormat = new SimpleDateFormat("MM/dd").format(date);
            binding.date.setText(dateFormat);

            firestore.collection("UserInfo").document(contentDTOs.get(position).doUid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                            if(contentDTOs.get(position).key == 0){
                                binding.message.setText(userDTO.army+" "+userDTO.budae+" "+userDTO.rank+" "+userDTO.name+"님이 좋아요를 눌렀습니다.");
                                binding.photo.setImageResource(R.drawable.heart_alarm);
                            }
                            else if(contentDTOs.get(position).key == 1){
                                binding.message.setText(userDTO.army+" "+userDTO.budae+" "+userDTO.name+"님이 메세지를 눌렀습니다.");
                                binding.photo.setImageResource(R.drawable.message_alarm);
                            }
                        }
                    });

            binding.boardName.setText(contentDTOs.get(position).name);

            binding.notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firestore.collection(contentDTOs.get(position).documentUid).document(contentDTOs.get(position).postUid).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        AlarmDTO alarmDTO = documentSnapshot.toObject(AlarmDTO.class);
                                        Intent intent = new Intent(getContext(), PostMore.class);
                                        intent.putExtra("documentUid", contentDTOs.get(position).documentUid);
                                        intent.putExtra("postUid", contentDTOs.get(position).postUid);
                                        intent.putExtra("manager", contentDTOs.get(position).manager);
                                        intent.putExtra("intentUid", user.getUid());
                                        intent.putExtra("annonymous", contentDTOs.get(position).annonymous);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(getActivity(), "게시물이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder{

        private AlarmBinding binding;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        AlarmBinding getBinding(){
            return binding;
        }
    }
}