package com.example.myapplication.BottomNavigation;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.Diet.Diet;
import com.example.myapplication.MyGoalPost.MyGoalActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TodayDiet extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    TextView dateP;
    LinearLayout preview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today_diet, container, false);

        dateP = (TextView)view.findViewById(R.id.date);
        preview = (LinearLayout)view.findViewById(R.id.preview);

        long postDate = System.currentTimeMillis();
        Date date = new Date(postDate);
        String dateFormat = new SimpleDateFormat("MM월 dd일").format(date);
        dateP.setText(dateFormat);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        final UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                        preview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), Diet.class);
                                intent.putExtra("army", userDTO.army);
                                intent.putExtra("budae", userDTO.budae);
                                startActivity(intent);
                            }
                        });
                    }
                });

        return view;
    }
}