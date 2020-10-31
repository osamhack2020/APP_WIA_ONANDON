package com.example.myapplication.Club;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.example.myapplication.NewPostPublic;
import com.example.myapplication.R;
import com.example.myapplication.model.ClubDTO;
import com.example.myapplication.model.PostDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewClubPost extends AppCompatActivity {

    final int PICK_IMAGE_FROM_ALBUM = 0;

    private String photoUrl;
    int isPhoto = 0;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    String budae;
    String documentUid;
    String name;

    EditText postTitle;
    EditText postExplain;
    EditText kindFirst;
    EditText kindSecond;
    EditText kindThird;

    TextView complete;
    ImageView cancel;

    ImageView addPhoto;
    ImageView photoPreview;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_club_post);

        postTitle = (EditText)findViewById(R.id.post_title);
        postExplain = (EditText)findViewById(R.id.post_explain);
        kindFirst = (EditText)findViewById(R.id.kind_first);
        kindSecond = (EditText)findViewById(R.id.kind_second);
        kindThird = (EditText)findViewById(R.id.kind_third);
        kindFirst.setText("#");
        kindSecond.setText("#");
        kindThird.setText("#");

        complete = (TextView)findViewById(R.id.complete);
        cancel = (ImageView)findViewById(R.id.cancel);

        addPhoto = (ImageView)findViewById(R.id.add_photo);
        photoPreview = (ImageView)findViewById(R.id.photo_preview);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        budae = intent.getStringExtra("budae");
        documentUid = intent.getStringExtra("documentUid");
        name = intent.getStringExtra("name");

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postTitle.getText().toString().isEmpty() || postExplain.getText().toString().isEmpty()
                        || ((kindFirst.getText().toString().isEmpty() ||  kindFirst.getText().toString().equals("#"))
                        && (kindSecond.getText().toString().isEmpty() || kindSecond.getText().toString().equals("#"))
                        && (kindThird.getText().toString().isEmpty() || kindThird.getText().toString().equals("#")))) {
                    Toast.makeText(NewClubPost.this, "항목을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
                } else if((!kindFirst.getText().toString().isEmpty() && kindFirst.getText().toString().charAt(0) != '#')
                        || (!kindSecond.getText().toString().isEmpty() && kindSecond.getText().toString().charAt(0) != '#')
                        || (!kindThird.getText().toString().isEmpty() && kindThird.getText().toString().charAt(0) != '#')){
                    Toast.makeText(NewClubPost.this, "태그는 반드시 앞에 '#'이 붙어야 합니다.", Toast.LENGTH_SHORT).show();
                }
                else if (isPhoto == 1) {
                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(NewClubPost.this, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();
                    File file = new File(photoUrl);
                    Uri contentUri = Uri.fromFile(file);

                    long postDate = System.currentTimeMillis();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(postDate));
                    String imageFileName = "JPEG_" + timeStamp + "_.png";
                    final StorageReference storageRef =
                            storage.getReferenceFromUrl("gs://my-application-9e821.appspot.com").child("images").child(imageFileName);
                    UploadTask uploadTask = storageRef.putFile(contentUri);

                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            return storageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri uri = task.getResult();

                            PostDTO postDTO = new PostDTO();
                            postDTO.name = name;
                            postDTO.explain = postExplain.getText().toString();
                            postDTO.title = postTitle.getText().toString();
                            postDTO.isPhoto = 1;
                            if (!kindFirst.getText().toString().isEmpty() && !kindFirst.getText().toString().equals("#")) {
                                postDTO.kind.put("first", kindFirst.getText().toString());
                            }
                            if (!kindSecond.getText().toString().isEmpty() && !kindSecond.getText().toString().equals("#")) {
                                postDTO.kind.put("second", kindSecond.getText().toString());
                            }
                            if (!kindThird.getText().toString().isEmpty() && !kindThird.getText().toString().equals("#")) {
                                postDTO.kind.put("third", kindThird.getText().toString());
                            }
                            postDTO.imageUri = uri.toString();
                            postDTO.uid = documentUid;
                            postDTO.timestamp = System.currentTimeMillis();

                            storePost(postDTO);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(NewClubPost.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } else {
                    PostDTO postDTO = new PostDTO();
                    postDTO.explain = postExplain.getText().toString();
                    postDTO.title = postTitle.getText().toString();
                    if (!kindFirst.getText().toString().isEmpty() && !kindFirst.getText().toString().equals("#")) {
                        postDTO.kind.put("first", kindFirst.getText().toString());
                    }
                    if (!kindSecond.getText().toString().isEmpty() && !kindSecond.getText().toString().equals("#")) {
                        postDTO.kind.put("second", kindSecond.getText().toString());
                    }
                    if (!kindThird.getText().toString().isEmpty() && !kindThird.getText().toString().equals("#")) {
                        postDTO.kind.put("third", kindThird.getText().toString());
                    }
                    postDTO.uid = documentUid;
                    postDTO.timestamp = System.currentTimeMillis();

                    storePost(postDTO);
                    Toast.makeText(NewClubPost.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

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
            isPhoto = 1;
        }
    }

    public void storePost(PostDTO postDTO){
        firestore.collection(budae+"동아리게시판").document().set(postDTO);
    }
}