package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Club.ClubActivity;
import com.example.myapplication.Club.UpdateClubPage;
import com.example.myapplication.model.ClubDTO;
import com.example.myapplication.model.PostDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdatePost extends AppCompatActivity {

    final int PICK_IMAGE_FROM_ALBUM = 0;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    EditText postTitle;
    EditText postExplain;
    EditText kindFirst;
    EditText kindSecond;
    EditText kindThird;

    TextView complete;
    ImageView cancel;

    ImageView addPhoto;
    ImageView photoPreview;

    CheckBox checkBox;

    String documentUid;
    String postUid;
    String imageUri;

    long storeTime;

    Intent intent;
    private String photoUrl;
    int wasPhoto = 0;
    int change = 0;
    int commentCount = 0;
    int favoriteCount = 0;

    Map<String, Boolean> favorites = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post_public);

        postTitle = (EditText)findViewById(R.id.post_title);
        postExplain = (EditText)findViewById(R.id.post_explain);
        kindFirst = (EditText)findViewById(R.id.kind_first);
        kindSecond = (EditText)findViewById(R.id.kind_second);
        kindThird = (EditText)findViewById(R.id.kind_third);

        complete = (TextView)findViewById(R.id.complete);
        cancel = (ImageView)findViewById(R.id.cancel);

        addPhoto = (ImageView)findViewById(R.id.add_photo);
        photoPreview = (ImageView)findViewById(R.id.photo_preview);

        checkBox = (CheckBox)findViewById(R.id.check);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        intent = getIntent();
        documentUid = intent.getStringExtra("documentUid");
        postUid = intent.getStringExtra("postUid");

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);
            }
        });

        firestore.collection(documentUid).document(postUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        PostDTO postDTO = documentSnapshot.toObject(PostDTO.class);

                        postTitle.setText(postDTO.title);
                        postExplain.setText(postDTO.explain);

                        if(postDTO.kind.containsKey("first")){
                            kindFirst.setText(postDTO.kind.get("first"));
                        }
                        if(postDTO.kind.containsKey("second")){
                            kindSecond.setText(postDTO.kind.get("second"));
                        }
                        if(postDTO.kind.containsKey("third")){
                            kindThird.setText(postDTO.kind.get("third"));
                        }

                        if(postDTO.annonymous == 1){
                            checkBox.setChecked(true);
                        }else{
                            checkBox.setChecked(false);
                        }
                        commentCount = postDTO.commentCount;
                        favoriteCount = postDTO.favoriteCount;
                        storeTime = postDTO.timestamp;
                        favorites = postDTO.favorites;

                        if(postDTO.isPhoto == 1){
                            wasPhoto = 1;
                            imageUri = intent.getStringExtra("imageUri");
                            Glide.with(UpdatePost.this).load(postDTO.imageUri)
                                    .into(photoPreview);
                        }
                    }
                });

        complete = (TextView)findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postTitle.getText().toString().isEmpty() || postTitle.getText().toString().isEmpty()
                        || (kindFirst.getText().toString().isEmpty() && kindSecond.getText().toString().isEmpty()
                        && kindThird.getText().toString().isEmpty())) {
                    Toast.makeText(UpdatePost.this, "항목을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(wasPhoto == 1 && change == 1){
                    Toast.makeText(UpdatePost.this, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();

                    StorageReference httpsReference = storage.getReferenceFromUrl(imageUri);
                    httpsReference.delete();
                    makeImageUri();
                }
                else if(wasPhoto == 0 && change == 1){
                    Toast.makeText(UpdatePost.this, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();

                    makeImageUri();
                }
                else {
                    PostDTO postDTO = new PostDTO();
                    postDTO.explain = postExplain.getText().toString();
                    postDTO.title = postTitle.getText().toString();

                    if(!kindFirst.getText().toString().isEmpty()){
                        postDTO.kind.put("first", kindFirst.getText().toString());
                    }
                    if(!kindSecond.getText().toString().isEmpty()){
                        postDTO.kind.put("second", kindSecond.getText().toString());
                    }
                    if(!kindThird.getText().toString().isEmpty()){
                        postDTO.kind.put("third", kindThird.getText().toString());
                    }

                    if(checkBox.isChecked()){
                        postDTO.annonymous = 1;
                    }else{
                        postDTO.annonymous = 0;
                    }

                    /*
                    수정 필요
                     */

                    postDTO.uid = auth.getCurrentUser().getUid();
                    postDTO.commentCount = commentCount;
                    postDTO.favoriteCount = favoriteCount;
                    postDTO.timestamp = storeTime;
                    postDTO.favorites = favorites;

                    storePost(postDTO);
                    Toast.makeText(UpdatePost.this, "수정 성공", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    setResult(RESULT_FIRST_USER, intent);
                    finish();
                }
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

        PostDTO postDTO = new PostDTO();
        postDTO.explain = postExplain.getText().toString();
        postDTO.title = postTitle.getText().toString();

        if(!kindFirst.getText().toString().isEmpty()){
            postDTO.kind.put("first", kindFirst.getText().toString());
        }
        if(!kindSecond.getText().toString().isEmpty()){
            postDTO.kind.put("second", kindSecond.getText().toString());
        }
        if(!kindThird.getText().toString().isEmpty()){
            postDTO.kind.put("third", kindThird.getText().toString());
        }

        if(checkBox.isChecked()){
            postDTO.annonymous = 1;
        }else{
            postDTO.annonymous = 0;
        }
        postDTO.isPhoto = 1;
        postDTO.imageUri=url;

        postDTO.uid = auth.getCurrentUser().getUid();
        postDTO.commentCount = commentCount;
        postDTO.favoriteCount = favoriteCount;
        postDTO.timestamp = storeTime;
        postDTO.favorites = favorites;

        storePost(postDTO);
        Toast.makeText(UpdatePost.this, "수정 성공", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        setResult(RESULT_FIRST_USER, intent);
        finish();
    }

    public void storePost(PostDTO postDTO){
        firestore.collection(documentUid).document(postUid).set(postDTO)
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