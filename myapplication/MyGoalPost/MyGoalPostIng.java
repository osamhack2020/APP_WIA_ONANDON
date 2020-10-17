package com.example.myapplication.MyGoalPost;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.databinding.MyGoalIngItemBinding;
import com.example.myapplication.model.MyGoalContentDTO;
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
import java.util.Calendar;
import java.util.Date;

public class MyGoalPostIng extends Fragment {

    final int TIME_DIVIDE = 24*60*60*1000;

    private FirebaseUser user;
    private FirebaseFirestore firestore;


    int isSearch;
    String search;


    public MyGoalPostIng(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_goal_post_ing, container, false);

        isSearch = getArguments().getInt("isSearch", 0);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.mygoal_post_ing_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());

        return view;
    }

    public class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<MyGoalContentDTO> contentDTOs;
        private ArrayList<String> contentUidList;

        DetailRecyclerViewAdapter(){
            contentDTOs = new ArrayList<>();
            contentUidList = new ArrayList<>();

            firestore.collection("MyGoal").orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    contentDTOs.clear();
                    contentUidList.clear();

                    if(value == null) return;
                    for(QueryDocumentSnapshot doc : value){
                        MyGoalContentDTO item = doc.toObject(MyGoalContentDTO.class);

                        if(isSearch == 1){
                            search = getArguments().getString("search");
                            if(item.kind.containsValue(search)){
                                contentDTOs.add(item);
                                contentUidList.add(doc.getId());
                            }
                        }
                        else {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_goal_ing_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            final MyGoalIngItemBinding binding = ((CustomViewHolder)holder).getBinding();
            final String intentDocument = contentUidList.get(position);
            final String intentUid = contentDTOs.get(position).uid;

            binding.kindFirst.setVisibility(View.GONE);
            binding.kindSecond.setVisibility(View.GONE);
            binding.kindThird.setVisibility(View.GONE);
            binding.more.setVisibility(View.GONE);

            binding.explain.setText(contentDTOs.get(position).explain);
            binding.title.setText(contentDTOs.get(position).title);

            long postDate = contentDTOs.get(position).timestamp;
            Date date = new Date(postDate);
            String dateFormat = new SimpleDateFormat("MM/dd").format(date);
            binding.postDate.setText(dateFormat);

            Calendar todaCal = Calendar.getInstance();
            long today = todaCal.getTimeInMillis()/TIME_DIVIDE;

            Calendar ddayCal = Calendar.getInstance();
            ddayCal.set(contentDTOs.get(position).year, contentDTOs.get(position).month, contentDTOs.get(position).day);
            long dday = ddayCal.getTimeInMillis()/TIME_DIVIDE;

            binding.dueDate.setText("D-" + (dday-today));
            binding.isPhoto.setVisibility(View.INVISIBLE);

            firestore.collection("UserInfo").document(contentDTOs.get(position).uid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                            binding.userInfo.setText(userDTO.army + " " + userDTO.budae+" "+userDTO.rank+" "+userDTO.name);
                        }
                    });

            binding.favoriteCountShow.setText(contentDTOs.get(position).favoriteCount + "");

            if(contentDTOs.get(position).favorites.containsKey(user.getUid())){
                binding.favoriteShow.setImageResource(R.drawable.heart);
            }
            else{
                binding.favoriteShow.setImageResource(R.drawable.empty_heart);
            }

            if(contentDTOs.get(position).isPhoto == 1){
                binding.isPhoto.setVisibility(View.VISIBLE);
            }

            int cnt = 0;
            if(contentDTOs.get(position).kind.containsKey("first")){
                binding.kindFirst.setText(contentDTOs.get(position).kind.get("first"));
                binding.kindFirst.setVisibility(View.VISIBLE);
                if(contentDTOs.get(position).kind.get("first").length() >= 4){
                    cnt++;
                }
            }
            if(contentDTOs.get(position).kind.containsKey("second")){
                binding.kindSecond.setText(contentDTOs.get(position).kind.get("second"));
                binding.kindSecond.setVisibility(View.VISIBLE);
                if(contentDTOs.get(position).kind.get("second").length() >= 4){
                    cnt++;
                }
            }
            if(contentDTOs.get(position).kind.containsKey("third")){
                binding.kindThird.setText(contentDTOs.get(position).kind.get("third"));
                binding.kindThird.setVisibility(View.VISIBLE);
                if(contentDTOs.get(position).kind.get("third").length() >= 4){
                    cnt++;
                }
            }
            if(cnt == 3 && contentDTOs.get(position).isPhoto == 1){
                binding.more.setVisibility(View.VISIBLE);
                binding.kindThird.setVisibility(View.GONE);
            }

            binding.commentCountShow.setText(contentDTOs.get(position).commentCount+"");
            binding.itemMyGoal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), MyGoalPostIngMore.class);
                    intent.putExtra("document", intentDocument);
                    intent.putExtra("uid", intentUid);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder{

            private MyGoalIngItemBinding binding;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);

                binding = DataBindingUtil.bind(itemView);
            }

            MyGoalIngItemBinding getBinding(){
                return binding;
            }
        }
    }
}