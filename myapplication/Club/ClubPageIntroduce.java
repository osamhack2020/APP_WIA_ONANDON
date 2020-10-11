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

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.ClubDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClubPageIntroduce extends Fragment {

    FirebaseFirestore firestore;

    String documentUid;
    String budae;

    TextView kindFirst;
    TextView kindSecond;
    TextView kindThird;

    TextView explain;
    TextView period;
    TextView represent;
    TextView number;

    ImageView photo;
    ImageView addQuestion;
    TextView addQuestionTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_page_introduce, container, false);
        firestore = FirebaseFirestore.getInstance();

        kindFirst = (TextView)view.findViewById(R.id.kind_first);
        kindSecond = (TextView)view.findViewById(R.id.kind_second);
        kindThird = (TextView)view.findViewById(R.id.kind_third);

        explain = (TextView)view.findViewById(R.id.explain);
        period = (TextView)view.findViewById(R.id.period);
        represent = (TextView)view.findViewById(R.id.represent);
        number = (TextView)view.findViewById(R.id.number);

        photo = (ImageView)view.findViewById(R.id.photo);
        addQuestion = (ImageView)view.findViewById(R.id.add_question);
        addQuestionTitle = (TextView)view.findViewById(R.id.add_question_title);

        documentUid = getArguments().getString("documentUid");
        budae = getArguments().getString("budae");

        firestore.collection(budae+"동아리").document(documentUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ClubDTO clubDTO = documentSnapshot.toObject(ClubDTO.class);

                        photo.setVisibility(View.GONE);
                        kindFirst.setVisibility(View.GONE);
                        kindSecond.setVisibility(View.GONE);
                        kindThird.setVisibility(View.GONE);

                        explain.setText(clubDTO.explain);
                        period.setText(clubDTO.period);
                        represent.setText(clubDTO.represent);
                        number.setText(clubDTO.number);

                        if(clubDTO.kind.containsKey("first")){
                            kindFirst.setText(clubDTO.kind.get("first"));
                            kindFirst.setVisibility(View.VISIBLE);
                        }
                        if(clubDTO.kind.containsKey("second")){
                            kindSecond.setText(clubDTO.kind.get("second"));
                            kindSecond.setVisibility(View.VISIBLE);
                        }
                        if(clubDTO.kind.containsKey("third")){
                            kindThird.setText(clubDTO.kind.get("third"));
                            kindThird.setVisibility(View.VISIBLE);
                        }

                        if(clubDTO.isPhoto == 1){
                            photo.setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(clubDTO.imageUri)
                                    .into(photo);
                        }

                        addQuestion.setVisibility(View.VISIBLE);
                        addQuestionTitle.setVisibility(View.VISIBLE);

                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(clubDTO.manager)){
                            addQuestion.setVisibility(View.GONE);
                            addQuestionTitle.setVisibility(View.GONE);
                        }
                        else{
                            addQuestion.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), QuestionPopUp.class);
                                    intent.putExtra("documentUid", documentUid);
                                    startActivity(intent);
                                }
                            });
                        }

                        Question question = new Question();
                        Bundle bundle = new Bundle(3);
                        bundle.putString("budae", budae);
                        bundle.putString("documentUid", documentUid);
                        bundle.putString("manager", clubDTO.manager);
                        question.setArguments(bundle);

                        FragmentManager manager = getChildFragmentManager();
                        FragmentTransaction tran = manager.beginTransaction();
                        tran.replace(R.id.question_content, question);
                        tran.commit();
                    }
                });

        return view;
    }
}