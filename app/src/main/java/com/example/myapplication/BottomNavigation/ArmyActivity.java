package com.example.myapplication.BottomNavigation;

import android.content.Intent;
import android.media.MediaDrm;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.Activity.ActivityFrame;
import com.example.myapplication.R;
import com.example.myapplication.model.ActivityDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class ArmyActivity extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    TextView titlePreview;
    LinearLayout preview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity_pager, container, false);

        titlePreview = (TextView)view.findViewById(R.id.title_preview);
        preview = (LinearLayout)view.findViewById(R.id.preview);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        firestore.collection("Activity").orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(QueryDocumentSnapshot doc : value){
                    ActivityDTO activityDTO = doc.toObject(ActivityDTO.class);
                    titlePreview.setText(activityDTO.title);
                }
            }
        });

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityFrame.class);
                startActivity(intent);
            }
        });

        return view;
    }
}