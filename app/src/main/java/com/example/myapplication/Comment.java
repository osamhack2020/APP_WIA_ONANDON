package com.example.myapplication;

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
import android.widget.Toast;

import com.example.myapplication.databinding.CommentBinding;
import com.example.myapplication.model.CommentDTO;
import com.example.myapplication.model.MyGoalContentDTO;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// 각 게시물에 달린 댓글을 RecyclerView를 활용한 리스트 뷰로 보여주는 프래그먼트
public class Comment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private long deletePressedTime = 0;
    String documentUid;
    String manager;
    String collection;
    String uid;
    int managerDelete = 0;

    public Comment(){
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        // 댓글을 보여줄 게시물의 고유 ID
        documentUid = getArguments().getString("document");
        // 게시판의 관리자가 있다면, 관리자 정보를 얻어온다
        manager = getArguments().getString("manager");
        // 해당 게시물이 업로드 된 게시판의 고유 ID
        collection = getArguments().getString("collection");
        uid = getArguments().getString("uid");

        if(manager != null && user.getUid().equals(manager)){
            managerDelete = 1;
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.comment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());

        return view;
    }

    private class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<CommentDTO> commentDTOs;
        private ArrayList<String> documentIDs;

        DetailRecyclerViewAdapter(){
            // 서버로 부터 받을 데이터를 저장할 ArrayList 선언
            commentDTOs = new ArrayList<>();
            documentIDs = new ArrayList<>();


            // 게시물을 업로드한 시간을 기준으로 데이터를 받아온다.
            firestore.collection(documentUid).orderBy("timeStamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    commentDTOs.clear();
                    documentIDs.clear();

                    // 서버로 부터 받은 객체 데이터를 ArrayList에 저장
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
                // 달린 댓글이 없을 경우
                binding.commentUser.setText("댓글이 없습니다. 댓글을 달아주세요.");
                binding.commentUser.setTextColor(Color.parseColor("#a0a0a0"));
                binding.commentDate.setText("");
                binding.commentExplain.setText("");
                binding.deleteComment.setVisibility(View.INVISIBLE);
            }
            else {
                binding.commentExplain.setText(commentDTOs.get(position).comment);
                binding.commentUser.setTextColor(Color.parseColor("#9B0000"));

                // 현 사용자가 게시판의 관리자인 경우, 댓글 칸에 '관리자' 표시가 뜨도록 설정.
                if(manager != null && commentDTOs.get(position).uid.equals(manager)){
                    binding.commentUser.setText("관리자");
                }
                else if(uid != null && commentDTOs.get(position).uid.equals(uid)){
                    binding.commentUser.setText("글쓴이");
                }
                else {
                    // 일반 사용자인 경우, 사용자의 정보가 간략하게 표시된다.
                    firestore.collection("UserInfo").document(commentDTOs.get(position).uid).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                                    binding.commentUser.setText(userDTO.army + " " + userDTO.budae + " " + userDTO.rank + " " + userDTO.name);
                                    binding.commentUser.setTextColor(ContextCompat.getColor(getContext(), R.color.common_google_signin_btn_text_light_pressed));
                                }
                            });
                }

                // 게시물이 업로드 된 날짜 추출
                long postDate = commentDTOs.get(position).timeStamp;
                Date date = new Date(postDate);
                String dateFormat = new SimpleDateFormat("MM/dd").format(date);
                binding.commentDate.setText(dateFormat);

                // 현 사용자가 관리자인 경우, 혹은 해당 댓글이 현 사용자가 작성한 댓글인 경우, 댓글을 삭제할 수 있도록 한다.
                final String delete = documentIDs.get(position);
                if (user.getUid().equals(commentDTOs.get(position).uid) || managerDelete == 1) {
                    binding.deleteComment.setVisibility(View.VISIBLE);
                    binding.deleteComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // 삭제 버튼을 2초 이내 두 번 눌러야 댓글이 삭재된다.
                            if (System.currentTimeMillis() > deletePressedTime + 2000) {
                                deletePressedTime = System.currentTimeMillis();
                                Toast.makeText(getActivity(), "버튼을 한번 더 누르시면 삭제됩니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(System.currentTimeMillis() <= deletePressedTime + 2000) {
                                firestore.collection(documentUid).document(delete).delete();
                                Toast.makeText(getActivity(), "댓글을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                deletePressedTime = 0;

                                // [댓글이 삭제되면 서버에 저장된 해당  게시물의 데이터를 업데이트 해준다.]

                                // 게시물이 업로드 된 게시판이 '나의 도전 이야기' 게시판인 경우
                                if(collection.equals("MyGoal")) {
                                    final DocumentReference docRef = firestore.collection(collection).document(documentUid);
                                    firestore.runTransaction(new Transaction.Function<Void>() {
                                        @Nullable
                                        @Override
                                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                            DocumentSnapshot snapshot = transaction.get(docRef);
                                            MyGoalContentDTO myGoalContentDTO = snapshot.toObject(MyGoalContentDTO.class);

                                            // 댓글 수를 하나 감소 시킨 후, 서버 데이터를 업데이트 한다.
                                            myGoalContentDTO.commentCount = myGoalContentDTO.commentCount - 1;

                                            transaction.set(docRef, myGoalContentDTO);
                                            return null;
                                        }
                                    });
                                }
                                else{
                                    // 게시물이 업로드 된 게시판이 '동아리 게시판'일 경우

                                    // 관리자 정보를 넘겨주는 게시판은 '동아리 게시판' 밖에 없으므로,
                                    // manager 변수에 저장된 정보의 유무에 따라 '동아리 게시판'에 업로드 된 게시물들을 분류해 낸다.
                                    // 예) manager 변수가 null 값이면 관리자 정보가 넘어 오지 않았다는 의미이므로, '동아리 게시판'의 게시물이 아님.
                                    // 댓글이 게시되면 게시물의 댓글 수를 1 증가시킨 후, 서버 정보를 업데이트 시킨다.
                                    final DocumentReference docRef = firestore.collection(collection).document(documentUid);
                                    firestore.runTransaction(new Transaction.Function<Void>() {
                                        @Nullable
                                        @Override
                                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                            DocumentSnapshot snapshot = transaction.get(docRef);
                                            PostDTO postDTO = snapshot.toObject(PostDTO.class);
                                            postDTO.commentCount = postDTO.commentCount-1;

                                            transaction.set(docRef, postDTO);
                                            return null;
                                        }
                                    });
                                }
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