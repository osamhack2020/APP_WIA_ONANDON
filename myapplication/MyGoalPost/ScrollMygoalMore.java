package com.example.myapplication.MyGoalPost;

import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Comment;
import com.example.myapplication.FcmPush;
import com.example.myapplication.R;
import com.example.myapplication.model.MyGoalContentDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScrollMygoalMore extends Fragment {

    final int TIME_DIVIDE = 24*60*60*1000;

    FirebaseFirestore firestore;

    TextView explainMore;
    TextView dueDateMore;
    TextView titleMore;
    TextView postDateMore;
    TextView userInfo;
    TextView favoriteCount;

    ImageView favorite;
    ImageView photo;

    FcmPush fcmPush;

    int click = 0;
    int count;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_scroll_mygoal_more, container, false);

        explainMore = (TextView)view.findViewById(R.id.explain_more);
        dueDateMore = (TextView)view.findViewById(R.id.due_date_more);
        titleMore = (TextView)view.findViewById(R.id.title_more);
        postDateMore = (TextView)view.findViewById(R.id.post_date_more);
        userInfo = (TextView)view.findViewById(R.id.user_info_more);
        favoriteCount = (TextView)view.findViewById(R.id.favorite_count);
        favorite = (ImageView)view.findViewById(R.id.heart);
        photo = (ImageView)view.findViewById(R.id.photo);

        fcmPush = new FcmPush();

        assert getArguments() != null;
        final String documentUid = getArguments().getString("document");
        final String uid = getArguments().getString("uid");

        firestore = FirebaseFirestore.getInstance();

        // 게시물 정보를 서버에서 불러와 각 항목에 바인딩 시킨다.
        firestore.collection("MyGoal").document(documentUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        MyGoalContentDTO myGoalContentDTO = documentSnapshot.toObject(MyGoalContentDTO.class);

                        explainMore.setText(myGoalContentDTO.explain);
                        titleMore.setText(myGoalContentDTO.title);

                        long postDate = myGoalContentDTO.timestamp;
                        Date date = new Date(postDate);
                        String dateFormat = new SimpleDateFormat("MM/dd").format(date);
                        postDateMore.setText(dateFormat);

                        Calendar todaCal = Calendar.getInstance();
                        long today = todaCal.getTimeInMillis()/TIME_DIVIDE;

                        Calendar ddayCal = Calendar.getInstance();
                        ddayCal.set(myGoalContentDTO.year, myGoalContentDTO.month, myGoalContentDTO.day);
                        long dday = ddayCal.getTimeInMillis()/TIME_DIVIDE;

                        dueDateMore.setText("D-" + (dday-today));
                        favoriteCount.setText(myGoalContentDTO.favoriteCount + " 개");
                        count = myGoalContentDTO.favoriteCount;

                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        if(myGoalContentDTO.favorites.containsKey(uid)){
                            click = 1;
                            favorite.setImageResource(R.drawable.heart);
                        }

                        if(myGoalContentDTO.isPhoto == 1) {
                            photo.setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(myGoalContentDTO.imageUri)
                                    .into(photo);
                        }
                    }
                });

        // 게시물 올린 사람 정보 보여주기
        firestore.collection("UserInfo").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                        userInfo.setText(userDTO.army + " " + userDTO.budae+" "+userDTO.rank+" "+userDTO.name);
                    }
                });


        // 좋아요 클릭 이벤트
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(click == 0){
                    favorite.setImageResource(R.drawable.heart);
                    favoriteCount.setText((++count) + " 개");
                    click = 1;
                }
                else{
                    favorite.setImageResource(R.drawable.empty_heart);
                    favoriteCount.setText((--count) + " 개");
                    click= 0;
                }

                final DocumentReference docRef = firestore.collection("MyGoal").document(documentUid);

                firestore.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docRef);
                        MyGoalContentDTO myGoalContentDTO = snapshot.toObject(MyGoalContentDTO.class);

                        String uidF = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        if(myGoalContentDTO == null){
                            return null;
                        }
                        else if(myGoalContentDTO.favorites.containsKey(uidF)){
                            myGoalContentDTO.favoriteCount = myGoalContentDTO.favoriteCount - 1;
                            myGoalContentDTO.favorites.remove(uidF);
                        }
                        else{
                            myGoalContentDTO.favoriteCount = myGoalContentDTO.favoriteCount + 1;
                            myGoalContentDTO.favorites.put(uidF, true);

                            firestore.collection("UserInfo").document(uidF).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                                            String message = userDTO.army+" "+userDTO.budae+" "+userDTO.rank+" "+userDTO.name;
                                            fcmPush.sendMessage(uid, "알림 메세지 입니다.", message+"님이 좋아요를 눌렀습니다.");
                                        }
                                    });
                        }
                        transaction.set(docRef, myGoalContentDTO);
                        return null;
                    }
                });
            }
        });


        // 댓글 fragement를 게시물 아래에 연다.
        Comment comment = new Comment();
        Bundle bundle = new Bundle(1);
        bundle.putString("document", documentUid);
        comment.setArguments(bundle);

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.comment_content, comment);
        tran.commit();

        return view;
    }
}