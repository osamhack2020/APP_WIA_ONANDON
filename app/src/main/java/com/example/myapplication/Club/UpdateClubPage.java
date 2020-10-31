package com.example.myapplication.Club;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.ClubDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.grpc.Context;


public class UpdateClubPage extends AppCompatActivity {

    final int PICK_IMAGE_FROM_ALBUM = 0;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    ImageView cancel;
    TextView complete;
    EditText clubTitle;
    EditText clubExplain;
    EditText clubRepresent;
    EditText representNumber;
    EditText period;
    EditText kindFirst;
    EditText kindSecond;
    EditText kindThird;

    ImageView addPhoto;
    ImageView photoPreview;

    String budae;
    String documentUid;
    String imageIntent;

    long storeTime;

    Intent intent;
    private String photoUrl;
    int wasPhoto = 0;
    int questionCount = 0;
    int change = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_club_page);


        clubTitle = (EditText)findViewById(R.id.club_title);
        clubExplain = (EditText)findViewById(R.id.club_explain);
        clubRepresent = (EditText)findViewById(R.id.club_represent);
        representNumber = (EditText)findViewById(R.id.represent_number);
        period = (EditText)findViewById(R.id.period);
        kindFirst = (EditText)findViewById(R.id.kind_first);
        kindSecond = (EditText)findViewById(R.id.kind_second);
        kindThird = (EditText)findViewById(R.id.kind_third);
        addPhoto = (ImageView)findViewById(R.id.add_photo);
        photoPreview = (ImageView)findViewById(R.id.photo_preview);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        intent = getIntent();
        budae = intent.getStringExtra("budae");
        documentUid = intent.getStringExtra("documentUid");

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);
            }
        });

        firestore.collection(budae+"동아리").document(documentUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ClubDTO clubDTO = documentSnapshot.toObject(ClubDTO.class);

                        clubTitle.setText(clubDTO.name);
                        clubExplain.setText(clubDTO.explain);
                        clubRepresent.setText(clubDTO.represent);
                        representNumber.setText(clubDTO.number);
                        period.setText(clubDTO.period);

                        if(clubDTO.kind.containsKey("first")){
                            kindFirst.setText(clubDTO.kind.get("first"));
                        }
                        if(clubDTO.kind.containsKey("second")){
                            kindSecond.setText(clubDTO.kind.get("second"));
                        }
                        if(clubDTO.kind.containsKey("third")){
                            kindThird.setText(clubDTO.kind.get("third"));
                        }
                        storeTime = clubDTO.timestamp;
                        questionCount = clubDTO.questionCount;

                        if(clubDTO.isPhoto == 1){
                            imageIntent = intent.getStringExtra("imageUri");
                            wasPhoto = 1;

                            Glide.with(UpdateClubPage.this).load(clubDTO.imageUri)
                                    .into(photoPreview);
                        }
                    }
                });


        complete = (TextView)findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clubTitle.getText().toString().isEmpty() || clubTitle.getText().toString().isEmpty()
                        || clubRepresent.getText().toString().isEmpty() || representNumber.getText().toString().isEmpty()
                        || period.getText().toString().isEmpty()
                        || (kindFirst.getText().toString().isEmpty() && kindSecond.getText().toString().isEmpty()
                        && kindThird.getText().toString().isEmpty())){
                    Toast.makeText(UpdateClubPage.this, "항목을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(wasPhoto == 1 && change == 1){
                    Toast.makeText(UpdateClubPage.this, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();

                    StorageReference httpsReference = storage.getReferenceFromUrl(imageIntent);
                    httpsReference.delete();

                    makeImageUri();
                }
                else if(wasPhoto == 0 && change == 1){
                    Toast.makeText(UpdateClubPage.this, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();

                    makeImageUri();
                }
                else {
                    ClubDTO clubDTO = new ClubDTO();
                    clubDTO.explain = clubExplain.getText().toString();
                    clubDTO.name = clubTitle.getText().toString();
                    clubDTO.represent = clubRepresent.getText().toString();
                    clubDTO.number = representNumber.getText().toString();
                    clubDTO.period = period.getText().toString();
                    if(!kindFirst.getText().toString().isEmpty()){
                        clubDTO.kind.put("first", kindFirst.getText().toString());
                    }
                    if(!kindSecond.getText().toString().isEmpty()){
                        clubDTO.kind.put("second", kindSecond.getText().toString());
                    }
                    if(!kindThird.getText().toString().isEmpty()){
                        clubDTO.kind.put("third", kindThird.getText().toString());
                    }
                    clubDTO.manager = auth.getCurrentUser().getUid();
                    clubDTO.timestamp = storeTime;
                    clubDTO.questionCount = questionCount;

                    if(wasPhoto == 1 && change != 1){
                        clubDTO.imageUri = imageIntent;
                        clubDTO.isPhoto = 1;
                    }

                    storePost(clubDTO, documentUid);
                    Toast.makeText(UpdateClubPage.this, "수정 성공", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UpdateClubPage.this, ClubActivity.class);
                    intent.putExtra("set", 1);
                    startActivity(intent);
                    finish();
                }
            }
        });

        cancel = (ImageView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == RESULT_OK) {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader cursorLoader = new CursorLoader(this, data.getData(), proj,
                    null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            photoUrl = cursor.getString(column_index);
            photoPreview.setImageURI(data.getData());
            change = 1;
        }
    }

    public void makeImageUri(){
        File file = new File(photoUrl);
        Uri contentUri = Uri.fromFile(file);

        long postDate = System.currentTimeMillis();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(postDate));
        String imageFileName = "JPEG_"+timeStamp+"_.png";
        final StorageReference storageRef =
                storage.getReferenceFromUrl("gs://my-application-9e821.appspot.com").child("images").child(imageFileName);
        UploadTask uploadTask = storageRef.putFile(contentUri);

        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){

            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri uri = task.getResult();
                NewPost(uri.toString());
            }
        });
    }

    public void NewPost(String url){
        ClubDTO clubDTO = new ClubDTO();
        clubDTO.explain = clubExplain.getText().toString();
        clubDTO.name = clubTitle.getText().toString();
        clubDTO.represent = clubRepresent.getText().toString();
        clubDTO.number = representNumber.getText().toString();
        clubDTO.period = period.getText().toString();
        if(!kindFirst.getText().toString().isEmpty()){
            clubDTO.kind.put("first", kindFirst.getText().toString());
        }
        if(!kindSecond.getText().toString().isEmpty()){
            clubDTO.kind.put("second", kindSecond.getText().toString());
        }
        if(!kindThird.getText().toString().isEmpty()){
            clubDTO.kind.put("third", kindThird.getText().toString());
        }
        clubDTO.manager = auth.getCurrentUser().getUid();
        clubDTO.timestamp = storeTime;
        clubDTO.questionCount = questionCount;
        clubDTO.imageUri = url;
        clubDTO.isPhoto = 1;

        storePost(clubDTO, documentUid);
        Toast.makeText(UpdateClubPage.this, "수정 성공", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ClubActivity.class);
        intent.putExtra("set", 1);
        startActivity(intent);
        finish();
    }

    public void storePost(ClubDTO clubDTO, String documentUid){
        firestore.collection(budae+"동아리").document(documentUid).set(clubDTO)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("store", "사용자 데이터를 저장했습니다.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("store", "사용자 데이터 저장에 실패했습니다.");
                    }
                });
    }
}