package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.model.AlarmDTO;
import com.example.myapplication.model.MyGoalContentDTO;
import com.example.myapplication.model.MyPostDTO;
import com.example.myapplication.model.PostDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScrollPostMore extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    TextView explainMore;
    TextView dueDateMore;
    TextView titleMore;
    TextView postDateMore;
    TextView userInfo;
    TextView favoriteCount;

    TextView kindFirst;
    TextView kindSecond;
    TextView kindThird;

    ImageView favorite;
    RoundImageView photo;
    ImageView scrap;

    LinearLayout scrapLayout;
    LinearLayout heartLayout;
    ProgressBar progressBar;

    FcmPush fcmPush;

    int click = 0;
    int count;

    String documentUid;
    String postUid;
    String intentUid;
    String manager;
    String name;
    int annonymous;

    // update
    int scrapClick=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scroll_post_more, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        explainMore = (TextView)view.findViewById(R.id.explain_more);
        dueDateMore = (TextView)view.findViewById(R.id.due_date_more);
        titleMore = (TextView)view.findViewById(R.id.title_more);
        postDateMore = (TextView)view.findViewById(R.id.post_date_more);
        userInfo = (TextView)view.findViewById(R.id.user_info_more);
        favoriteCount = (TextView)view.findViewById(R.id.favorite_count);
        favorite = (ImageView)view.findViewById(R.id.heart);
        heartLayout = (LinearLayout)view.findViewById(R.id.heart_layout);

        photo = (RoundImageView)view.findViewById(R.id.photo);
        scrapLayout = (LinearLayout)view.findViewById(R.id.scrap_layout);
        scrap = (ImageView)view.findViewById(R.id.scrap);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        kindFirst = (TextView)view.findViewById(R.id.kind_first);
        kindSecond = (TextView)view.findViewById(R.id.kind_second);
        kindThird = (TextView)view.findViewById(R.id.kind_third);

        fcmPush = new FcmPush();

        documentUid = getArguments().getString("documentUid");
        postUid = getArguments().getString("postUid");
        intentUid = getArguments().getString("intentUid");
        manager = getArguments().getString("manager");
        annonymous = getArguments().getInt("annonymous");
        name = getArguments().getString("name");

        photo.setRectRadius(40f);

        // 게시물 정보를 서버에서 불러와 각 항목에 바인딩 시킨다.
        firestore.collection(documentUid).document(postUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        PostDTO postDTO = documentSnapshot.toObject(PostDTO.class);

                        explainMore.setText(postDTO.explain);
                        titleMore.setText(postDTO.title);

                        long postDate = postDTO.timestamp;
                        Date date = new Date(postDate);
                        String dateFormat = new SimpleDateFormat("MM/dd").format(date);
                        postDateMore.setText(dateFormat);

                        favoriteCount.setText(postDTO.favoriteCount + "");
                        count = postDTO.favoriteCount;

                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        if(postDTO.favorites.containsKey(uid)){
                            click = 1;
                            favorite.setImageResource(R.drawable.heart);
                        }

                        if(postDTO.isPhoto == 1) {
                            progressBar.setVisibility(View.VISIBLE);
                            photo.setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(postDTO.imageUri)
                                    .into(photo);
                            progressBar.setVisibility(View.GONE);
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                        }

                        kindFirst.setVisibility(View.GONE);
                        kindSecond.setVisibility(View.GONE);
                        kindThird.setVisibility(View.GONE);

                        if(postDTO.kind.containsKey("first")){
                            kindFirst.setText(postDTO.kind.get("first"));
                            kindFirst.setVisibility(View.VISIBLE);
                        }
                        if(postDTO.kind.containsKey("second")){
                            kindSecond.setText(postDTO.kind.get("second"));
                            kindSecond.setVisibility(View.VISIBLE);
                        }
                        if(postDTO.kind.containsKey("third")){
                            kindThird.setText(postDTO.kind.get("third"));
                            kindThird.setVisibility(View.VISIBLE);
                        }

                        if(postDTO.annonymous == 1){
                            userInfo.setText("익명");
                        }

                        if(postDTO.scrap.containsKey(auth.getCurrentUser().getUid())){
                            scrapClick=1;
                            scrap.setImageResource(R.drawable.scrap);
                        }else{
                            scrapClick=0;
                            scrap.setImageResource(R.drawable.empty_star);
                        }

                        // 댓글 fragement를 게시물 아래에 연다.
                        Comment comment = new Comment();
                        Bundle bundle = new Bundle(4);
                        bundle.putString("document", postUid);
                        bundle.putString("collection", documentUid);
                        bundle.putString("manager", manager);
                        bundle.putString("uid", postDTO.uid);
                        comment.setArguments(bundle);

                        FragmentManager manager = getChildFragmentManager();
                        FragmentTransaction tran = manager.beginTransaction();
                        tran.replace(R.id.comment_content, comment);
                        tran.commit();
                    }
                });

        if(annonymous==0){
            firestore.collection("UserInfo").document(intentUid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                            userInfo.setText(userDTO.army + " " + userDTO.budae + " " + userDTO.rank + " " + userDTO.name);
                        }
                    });
        }

        scrapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scrapClick == 1){
                    scrapClick=0;
                    scrap.setImageResource(R.drawable.empty_star);
                }else{
                    scrapClick=1;
                    scrap.setImageResource(R.drawable.scrap);
                }

                final DocumentReference docRef = firestore.collection(documentUid).document(postUid);
                firestore.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docRef);
                        final PostDTO postDTO = snapshot.toObject(PostDTO.class);

                        String uidF = auth.getCurrentUser().getUid();

                        if(postDTO == null){
                            return null;
                        }else if(postDTO.scrap.containsKey(auth.getCurrentUser().getUid())){
                            postDTO.scrap.remove(uidF);

                            firestore.collection(auth.getCurrentUser().getUid()+"_Scrap")
                                    .document(auth.getCurrentUser().getUid()+"_"+postDTO.timestamp).delete();
                        }else{
                            postDTO.scrap.put(uidF, true);

                            MyPostDTO myPost = new MyPostDTO();
                            myPost.documentUid = documentUid;
                            myPost.postUid=postUid;
                            myPost.timestamp=postDTO.timestamp;
                            myPost.name = name;
                            firestore.collection(auth.getCurrentUser().getUid()+"_Scrap")
                                    .document(auth.getCurrentUser().getUid()+"_"+postDTO.timestamp).set(myPost);
                        }

                        transaction.set(docRef, postDTO);
                        return null;
                    }
                });

                if(scrapClick == 1){
                    Toast.makeText(getActivity(), "스크랩 되었습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "스크랩이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 좋아요 클릭 이벤트
        heartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(click == 0){
                    favorite.setImageResource(R.drawable.heart);
                    favoriteCount.setText((++count) + "");
                    click = 1;
                }
                else{
                    favorite.setImageResource(R.drawable.empty_heart);
                    favoriteCount.setText((--count) + "");
                    click= 0;
                }

                final DocumentReference docRef = firestore.collection(documentUid).document(postUid);
                firestore.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docRef);
                        PostDTO postDTO = snapshot.toObject(PostDTO.class);

                        String uidF = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        if(postDTO == null){
                            return null;
                        }
                        else if(postDTO.favorites.containsKey(uidF)){
                            postDTO.favoriteCount = postDTO.favoriteCount - 1;
                            postDTO.favorites.remove(uidF);
                        }
                        else{
                            postDTO.favoriteCount = postDTO.favoriteCount + 1;
                            postDTO.favorites.put(uidF, true);

                            firestore.collection("UserInfo").document(uidF).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                                            String message = userDTO.army+" "+userDTO.budae+" "+userDTO.rank+" "+userDTO.name;
                                            fcmPush.sendMessage(intentUid, "좋아요 알림 메세지 입니다.", message+"님이 좋아요를 눌렀습니다.");
                                        }
                                    });

                            if(!intentUid.equals(auth.getCurrentUser().getUid())) {
                                AlarmDTO alarmDTO = new AlarmDTO();
                                alarmDTO.doUid = auth.getCurrentUser().getUid();
                                alarmDTO.documentUid = documentUid;
                                alarmDTO.postUid = postUid;
                                alarmDTO.manager = manager;
                                alarmDTO.annonymous = annonymous;
                                alarmDTO.key = 0;
                                alarmDTO.timestamp = System.currentTimeMillis();
                                alarmDTO.name = name;
                                firestore.collection(intentUid + "_Alarm").document().set(alarmDTO);
                            }
                        }

                        transaction.set(docRef, postDTO);
                        return null;
                    }
                });
            }
        });

        return view;
    }

}