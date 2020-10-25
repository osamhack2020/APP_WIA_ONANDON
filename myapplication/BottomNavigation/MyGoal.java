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

import com.example.myapplication.MyGoalPost.MyGoalActivity;
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

    TextView userInfo;

    LinearLayout preview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_goal, container, false);


        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        userInfo = (TextView)view.findViewById(R.id.user);
        preview = (LinearLayout)view.findViewById(R.id.preview);

        assert getArguments() != null;
        final String uid = getArguments().getString("uid");

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyGoalActivity.class);
                startActivity(intent);
            }
        });

        firestore.collection("UserInfo").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                        userInfo.setText(userDTO.army + " " + userDTO.budae+" "+userDTO.name+"님의 도전이야기");
                    }
                });


        return view;
    }
}