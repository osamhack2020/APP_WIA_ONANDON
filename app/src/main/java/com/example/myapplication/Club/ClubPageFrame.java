package com.example.myapplication.Club;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClubPageFrame extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    TextView addPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_page_frame, container, false);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        addPage = (TextView)view.findViewById(R.id.add_page);

        firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                        addPage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), NewClubPage.class);
                                intent.putExtra("budae", userDTO.budae);
                                startActivity(intent);
                            }
                        });

                        ClubPage clubPage = new ClubPage();
                        Bundle bundle = new Bundle(1);
                        bundle.putString("budae", userDTO.budae);
                        clubPage.setArguments(bundle);

                        FragmentManager manager = getChildFragmentManager();
                        FragmentTransaction tran = manager.beginTransaction();
                        tran.replace(R.id.club_content, clubPage);
                        tran.commit();
                    }
                });

        return view;
    }
}