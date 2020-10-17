package com.example.myapplication.BottomNavigation;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.MyGoalPost.MyGoalPostIngMore;
import com.example.myapplication.R;
import com.example.myapplication.model.MyGoalContentDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyGoal extends Fragment {

    FirebaseFirestore firestore;
    FirebaseUser user;

    TextView dueDate;
    TextView userInfo;
    TextView postDate;
    TextView title;
    TextView explain;
    TextView favoriteCount;
    TextView commentCount;

    ImageView isPhoto;
    ImageView favoriteShow;

    LinearLayout preview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final int TIME_DIVIDE = 24*60*60*1000;

        View view = inflater.inflate(R.layout.fragment_my_goal, container, false);

        dueDate = (TextView)view.findViewById(R.id.due_date);
        userInfo = (TextView)view.findViewById(R.id.user_info);
        postDate = (TextView)view.findViewById(R.id.post_date);
        title = (TextView)view.findViewById(R.id.title);
        explain = (TextView)view.findViewById(R.id.explain);
        favoriteCount = (TextView)view.findViewById(R.id.favorite_count_show);
        commentCount = (TextView)view.findViewById(R.id.comment_count_show);
        isPhoto = (ImageView)view.findViewById(R.id.is_photo);
        favoriteShow = (ImageView)view.findViewById(R.id.favorite_show);
        preview = (LinearLayout)view.findViewById(R.id.preview);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        assert getArguments() != null;
        final String documentUid = getArguments().getString("document");
        final String uid = getArguments().getString("uid");

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyGoalPostIngMore.class);
                intent.putExtra("document", documentUid);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        firestore.collection("MyGoal").document(documentUid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        MyGoalContentDTO myGoalContentDTO = value.toObject(MyGoalContentDTO.class);

                        if(myGoalContentDTO != null) {
                            explain.setText(myGoalContentDTO.explain);
                            title.setText(myGoalContentDTO.title);

                            long postDateLong = myGoalContentDTO.timestamp;
                            Date date = new Date(postDateLong);
                            String dateFormat = new SimpleDateFormat("MM/dd").format(date);
                            postDate.setText(dateFormat);

                            Calendar todaCal = Calendar.getInstance();
                            long today = todaCal.getTimeInMillis() / TIME_DIVIDE;

                            Calendar ddayCal = Calendar.getInstance();
                            ddayCal.set(myGoalContentDTO.year, myGoalContentDTO.month, myGoalContentDTO.day);
                            long dday = ddayCal.getTimeInMillis() / TIME_DIVIDE;

                            dueDate.setText("D-" + (dday - today));

                            favoriteCount.setText(myGoalContentDTO.favoriteCount + "");
                            commentCount.setText(myGoalContentDTO.commentCount + "");

                            if (myGoalContentDTO.favorites.containsKey(user.getUid())) {
                                favoriteShow.setImageResource(R.drawable.heart);
                            } else {
                                favoriteShow.setImageResource(R.drawable.empty_heart);
                            }

                            isPhoto.setVisibility(View.INVISIBLE);
                            if (myGoalContentDTO.isPhoto == 1) {
                                isPhoto.setVisibility(View.VISIBLE);
                            }
                            kindFirst.setVisibility(View.GONE);
                            kindSecond.setVisibility(View.GONE);
                            more.setVisibility(View.GONE);

                            int cnt = 0;
                            if(myGoalContentDTO.kind.containsKey("first")){
                                kindFirst.setText(myGoalContentDTO.kind.get("first"));
                                kindFirst.setVisibility(View.VISIBLE);
                                if(myGoalContentDTO.kind.get("first").length() >= 4){
                                    cnt++;
                                }
                            }
                            if(myGoalContentDTO.kind.containsKey("second")){
                                kindSecond.setText(myGoalContentDTO.kind.get("second"));
                                kindSecond.setVisibility(View.VISIBLE);
                                if(myGoalContentDTO.kind.get("second").length() >= 4){
                                    cnt++;
                                }
                            }
                            if(myGoalContentDTO.kind.containsKey("third")){
                                more.setVisibility(View.VISIBLE);
                            }
                            if(cnt == 2 && myGoalContentDTO.isPhoto == 1){
                                kindSecond.setVisibility(View.GONE);
                                more.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        firestore.collection("UserInfo").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                        userInfo.setText(userDTO.army + " " + userDTO.budae+" "+userDTO.rank+" "+userDTO.name);
                    }
                });

        return view;
    }
}
