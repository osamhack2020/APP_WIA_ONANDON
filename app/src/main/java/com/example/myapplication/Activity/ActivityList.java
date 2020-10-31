package com.example.myapplication.Activity;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityItemBinding;
import com.example.myapplication.model.ActivityDTO;
import com.example.myapplication.model.MyPostDTO;
import com.example.myapplication.model.PostDTO;
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
import java.util.Calendar;
import java.util.Date;

public class ActivityList extends Fragment {

    final int TIME_DIVIDE = 24*60*60*1000;

    private FirebaseUser user;
    private FirebaseFirestore firestore;

    TextView noPost;

    int isSearch;
    String search;

    public ActivityList(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity_list, container, false);

        noPost = view.findViewById(R.id.no_post);
        isSearch = getArguments().getInt("isSearch", 0);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.activity_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());

        return view;
    }

    private class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<ActivityDTO> contentDTOs;
        private ArrayList<String> contentUidList;
        ArrayList<Integer> scrapClick;

        DetailRecyclerViewAdapter(){
            contentDTOs = new ArrayList<>();
            contentUidList = new ArrayList<>();
            scrapClick = new ArrayList<>();

            firestore.collection("Activity").orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            contentDTOs.clear();
                            contentUidList.clear();

                            if(value == null) return;
                            for(QueryDocumentSnapshot doc : value){
                                ActivityDTO item = doc.toObject(ActivityDTO.class);

                                if(isSearch == 1){
                                    search = getArguments().getString("search");
                                    if(item.kind.containsValue(search)){
                                        contentDTOs.add(item);
                                        contentUidList.add(doc.getId());
                                    }
                                }
                                else{
                                    contentDTOs.add(item);
                                    contentUidList.add(doc.getId());
                                }
                                scrapClick.add(-1);
                            }

                            if(contentDTOs.size() == 0){
                                noPost.setVisibility(View.VISIBLE);
                            }
                            else{
                                noPost.setVisibility(View.GONE);
                            }

                            notifyDataSetChanged();
                        }
                    });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final ActivityItemBinding binding = ((CustomViewHolder) holder).getBinding();

            binding.kindFirst.setVisibility(View.GONE);
            binding.kindSecond.setVisibility(View.GONE);
            binding.kindThird.setVisibility(View.GONE);

            binding.userInfoMore.setText(contentDTOs.get(position).name);

            long postDate = contentDTOs.get(position).timestamp;
            Date date = new Date(postDate);
            String dateFormat = new SimpleDateFormat("MM/dd").format(date);
            binding.postDateMore.setText(dateFormat);

            binding.titleMore.setText(contentDTOs.get(position).title);
            binding.explainMore.setText(contentDTOs.get(position).explain);
            binding.participation.setText(contentDTOs.get(position).participation);
            binding.dueDate.setText(contentDTOs.get(position).year + "년 "+ (contentDTOs.get(position).month+1)+"월 "+contentDTOs.get(position).day+ "일 까지");

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

            if(contentDTOs.get(position).scrap.containsKey(user.getUid()) && scrapClick.get(position) == -1){
                binding.scrap.setImageResource(R.drawable.scrap);
                scrapClick.set(position, 1);
            }else if(scrapClick.get(position) == -1){
                binding.scrap.setImageResource(R.drawable.empty_star);
                scrapClick.set(position, 0);
            }

            Calendar todaCal = Calendar.getInstance();
            long today = todaCal.getTimeInMillis()/TIME_DIVIDE;

            Calendar ddayCal = Calendar.getInstance();
            ddayCal.set(contentDTOs.get(position).year, contentDTOs.get(position).month, contentDTOs.get(position).day);
            long dday = ddayCal.getTimeInMillis()/TIME_DIVIDE;

            binding.dueDateMore.setText("D-" + (dday-today));

            Glide.with(getContext()).load(contentDTOs.get(position).imageUri)
                    .into(binding.photo);
            binding.photo.setRectRadius(40f);

            binding.link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentDTOs.get(position).link));
                    startActivity(intent);
                }
            });

            binding.scrapLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(scrapClick.get(position) == 1){
                        scrapClick.set(position, 0);
                        binding.scrap.setImageResource(R.drawable.empty_star);
                    }else{
                        scrapClick.set(position, 1);
                        binding.scrap.setImageResource(R.drawable.scrap);
                    }

                    final DocumentReference docRef = firestore.collection("Activity").document(contentUidList.get(position));
                    firestore.runTransaction(new Transaction.Function<Void>() {
                        @Nullable
                        @Override
                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(docRef);
                            final ActivityDTO activityDTO = snapshot.toObject(ActivityDTO.class);

                            String uidF = user.getUid();

                            if(activityDTO == null){
                                return null;
                            }else if(activityDTO.scrap.containsKey(uidF)){
                                activityDTO.scrap.remove(uidF);

                                firestore.collection(user.getUid()+"_Activity_Scrap")
                                        .document(user.getUid()+"_"+activityDTO.timestamp).delete();
                            }else{
                                activityDTO.scrap.put(uidF, true);

                                MyPostDTO myPost = new MyPostDTO();
                                myPost.documentUid = "Activity";
                                myPost.postUid=contentUidList.get(position);
                                myPost.timestamp=activityDTO.timestamp;
                                myPost.name = "군내 활동 게시판";
                                firestore.collection(user.getUid()+"_Activity_Scrap")
                                        .document(user.getUid()+"_"+activityDTO.timestamp).set(myPost);
                            }

                            transaction.set(docRef, activityDTO);
                            return null;
                        }
                    });

                    if(scrapClick.get(position) == 1){
                        Toast.makeText(getActivity(), "스크랩 되었습니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), "스크랩이 해제되었습니다.", Toast.LENGTH_SHORT).show();
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

        private ActivityItemBinding binding;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        ActivityItemBinding getBinding(){
            return binding;
        }
    }
}