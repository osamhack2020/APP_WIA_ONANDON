package com.example.myapplication.Club;

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
import com.example.myapplication.Comment;
import com.example.myapplication.R;
import com.example.myapplication.RoundImageView;
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
import java.util.Calendar;
import java.util.Date;

public class ScrollClubPostItem extends Fragment {

    final int TIME_DIVIDE = 24*60*60*1000;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    TextView explainMore;
    TextView postDate;
    TextView titleMore;
    TextView clubName;
    TextView favoriteCount;

    TextView kindFirst;
    TextView kindSecond;
    TextView kindThird;

    ImageView favorite;
    RoundImageView photo;
    ImageView scrap;
    LinearLayout scrapLayout;
    ProgressBar progressBar;

    String postUid;
    String name;
    String manager;
    String budae;

    int scrapClick=0;
    int click = 0;
    int count;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scroll_club_post_item, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        explainMore = (TextView)view.findViewById(R.id.explain_more);
        titleMore = (TextView)view.findViewById(R.id.title_more);
        postDate = (TextView)view.findViewById(R.id.post_date);
        clubName = (TextView)view.findViewById(R.id.club_name);
        favoriteCount = (TextView)view.findViewById(R.id.favorite_count);

        kindFirst = (TextView)view.findViewById(R.id.kind_first);
        kindSecond = (TextView)view.findViewById(R.id.kind_second);
        kindThird = (TextView)view.findViewById(R.id.kind_third);
        scrapLayout = (LinearLayout)view.findViewById(R.id.scrap_layout);
        scrap = (ImageView)view.findViewById(R.id.scrap);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        favorite = (ImageView)view.findViewById(R.id.heart);
        photo = (RoundImageView) view.findViewById(R.id.photo);

        postUid = getArguments().getString("postUid");
        name = getArguments().getString("name");
        manager = getArguments().getString("manager");
        budae = getArguments().getString("budae");

        photo.setRectRadius(40f);

        firestore.collection(budae + "동아리게시판").document(postUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        PostDTO postDTO = documentSnapshot.toObject(PostDTO.class);

                        explainMore.setText(postDTO.explain);
                        titleMore.setText(postDTO.title);
                        clubName.setText(name);

                        long postDateLong = postDTO.timestamp;
                        Date date = new Date(postDateLong);
                        String dateFormat = new SimpleDateFormat("MM/dd").format(date);
                        postDate.setText(dateFormat);

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
                        }else{
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

                        if(postDTO.scrap.containsKey(auth.getCurrentUser().getUid())){
                            scrapClick=1;
                            scrap.setImageResource(R.drawable.scrap);
                        }else{
                            scrapClick=0;
                            scrap.setImageResource(R.drawable.empty_star);
                        }
                    }
                });

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

                final DocumentReference docRef = firestore.collection(budae + "동아리게시판").document(postUid);
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
                            myPost.documentUid = budae + "동아리게시판";
                            myPost.postUid=postUid;
                            myPost.timestamp=postDTO.timestamp;
                            myPost.name = "동아리 게시판";
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

        favorite.setOnClickListener(new View.OnClickListener() {
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

                final DocumentReference docRef = firestore.collection(budae+"동아리게시판").document(postUid);
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
                        }
                        transaction.set(docRef, postDTO);
                        return null;
                    }
                });
            }
        });

        // 댓글 fragement를 게시물 아래에 연다.
        Comment comment = new Comment();
        Bundle bundle = new Bundle(4);
        bundle.putString("document", postUid);
        bundle.putString("manager", manager);
        bundle.putString("collection", budae+"동아리게시판");
        comment.setArguments(bundle);

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.comment_content, comment);
        tran.commit();

        return view;
    }
}