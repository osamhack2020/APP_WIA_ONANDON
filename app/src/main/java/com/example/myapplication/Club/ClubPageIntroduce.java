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
import com.example.myapplication.RoundImageView;
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

    //!!!!!!!!!!!!!!!!!
    TextView update;
    TextView addQuestionTitle;
    TextView questionCount;

    RoundImageView photo;
    ImageView addQuestion;

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
        update = (TextView)view.findViewById(R.id.update);

        photo = (RoundImageView) view.findViewById(R.id.photo);
        addQuestion = (ImageView)view.findViewById(R.id.add_question);
        addQuestionTitle = (TextView)view.findViewById(R.id.add_question_title);
        questionCount = (TextView)view.findViewById(R.id.question_count);

        documentUid = getArguments().getString("documentUid");
        budae = getArguments().getString("budae");
        photo.setRectRadius(40f);

        firestore.collection(budae+"동아리").document(documentUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final ClubDTO clubDTO = documentSnapshot.toObject(ClubDTO.class);

                        photo.setVisibility(View.GONE);
                        kindFirst.setVisibility(View.GONE);
                        kindSecond.setVisibility(View.GONE);
                        kindThird.setVisibility(View.GONE);
                        update.setVisibility(View.GONE);

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

                        questionCount.setText(clubDTO.questionCount+"");
                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(clubDTO.manager)){
                            addQuestion.setVisibility(View.GONE);
                            addQuestionTitle.setText("질문 글에 답변해 주세요.");
                        }
                        else{
                            addQuestion.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), QuestionPopUp.class);
                                    intent.putExtra("documentUid", documentUid);
                                    intent.putExtra("budae", budae);
                                    startActivity(intent);
                                }
                            });
                        }

                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(clubDTO.manager)){
                            update.setVisibility(View.VISIBLE);
                            update.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), UpdateClubPage.class);
                                    intent.putExtra("documentUid", documentUid);
                                    intent.putExtra("budae", budae);

                                    if(clubDTO.isPhoto == 1) {
                                        intent.putExtra("imageUri", clubDTO.imageUri);
                                    }
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