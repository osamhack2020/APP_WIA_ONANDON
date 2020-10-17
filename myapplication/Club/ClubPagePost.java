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
import com.example.myapplication.model.ClubDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ClubPagePost extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    String budae;
    String documentUid;

    TextView addPostText;
    ImageView addPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club_page_post, container, false);

        budae = getArguments().getString("budae");
        documentUid = getArguments().getString("documentUid");

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        addPostText = (TextView)view.findViewById(R.id.add_post_text);
        addPost = (ImageView)view.findViewById(R.id.add_post);

        addPostText.setVisibility(View.GONE);
        addPost.setVisibility(View.GONE);

        firestore.collection(budae+"동아리").document(documentUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        final ClubDTO clubDTO = documentSnapshot.toObject(ClubDTO.class);
                        if(auth.getCurrentUser().getUid().equals(clubDTO.manager)){
                            addPostText.setVisibility(View.VISIBLE);
                            addPost.setVisibility(View.VISIBLE);

                            addPost.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), NewClubPost.class);
                                    intent.putExtra("budae", budae);
                                    intent.putExtra("name", clubDTO.name);
                                    intent.putExtra("documentUid", documentUid);
                                    startActivity(intent);
                                }
                            });
                        }

                        ClubPostList clubPostList = new ClubPostList();
                        Bundle bundle = new Bundle(3);
                        bundle.putString("name", clubDTO.name);
                        bundle.putString("manager", clubDTO.manager);
                        bundle.putString("budae", budae);
                        clubPostList.setArguments(bundle);

                        FragmentManager manager = getChildFragmentManager();
                        FragmentTransaction tran = manager.beginTransaction();
                        tran.replace(R.id.post_content, clubPostList);
                        tran.commit();
                    }
                });

        return view;
    }
}