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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.model.MyPostDTO;
import com.example.myapplication.model.PostDTO;
import com.example.myapplication.model.TagDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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

public class NewPostPublic extends AppCompatActivity {

    final int PICK_IMAGE_FROM_ALBUM = 0;

    private String photoUrl;
    int isPhoto = 0;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    String documentUid;
    String name;
    String army;
    String budae;
    String rank;

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
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post_public);

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

        checkBox = (CheckBox)findViewById(R.id.check);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        documentUid = intent.getStringExtra("documentUid");
        name = intent.getStringExtra("name");
        army = intent.getStringExtra("army");
        budae = intent.getStringExtra("budae");
        rank = intent.getStringExtra("rank");

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
                    Toast.makeText(NewPostPublic.this, "항목을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if((!kindFirst.getText().toString().isEmpty() && kindFirst.getText().toString().charAt(0) != '#')
                        || (!kindSecond.getText().toString().isEmpty() && kindSecond.getText().toString().charAt(0) != '#')
                        || (!kindThird.getText().toString().isEmpty() && kindThird.getText().toString().charAt(0) != '#')){
                    Toast.makeText(NewPostPublic.this, "태그는 반드시 앞에 '#'이 붙어야 합니다.", Toast.LENGTH_SHORT).show();
                }
                else if (isPhoto == 1) {
                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(NewPostPublic.this, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();
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
                            postDTO.uid = auth.getCurrentUser().getUid();
                            postDTO.timestamp = System.currentTimeMillis();
                            if(checkBox.isChecked()){
                                postDTO.annonymous = 1;
                            }

                            postDTO.kind.put("army", "#"+army);
                            postDTO.kind.put("budae", "#"+budae);
                            postDTO.kind.put("rank", "#"+rank);

                            storePost(postDTO);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(NewPostPublic.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
                else {
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
                    postDTO.uid = auth.getCurrentUser().getUid();
                    postDTO.timestamp = System.currentTimeMillis();
                    if(checkBox.isChecked()){
                        postDTO.annonymous = 1;
                    }

                    postDTO.kind.put("army", "#"+army);
                    postDTO.kind.put("budae", "#"+budae);
                    postDTO.kind.put("rank", "#"+rank);

                    storePost(postDTO);
                    Toast.makeText(NewPostPublic.this, "업로드 성공", Toast.LENGTH_SHORT).show();
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

    public void storePost(final PostDTO postDTO){
        firestore.collection(documentUid).document(postDTO.timestamp+"_"+postDTO.uid).set(postDTO);

        final DocumentReference docRef = firestore.collection(documentUid+"_tag").document("tag");
        firestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);

                if(snapshot.exists()) {
                    TagDTO tagDTO = snapshot.toObject(TagDTO.class);

                    if (postDTO.kind.containsKey("first")) {
                        String first = postDTO.kind.get("first");
                        if (!tagDTO.tag.contains(first)) {
                            tagDTO.tag.add(first);
                        }
                    }
                    if (postDTO.kind.containsKey("second")) {
                        String second = postDTO.kind.get("second");
                        if (!tagDTO.tag.contains(second)) {
                            tagDTO.tag.add(second);
                        }
                    }
                    if (postDTO.kind.containsKey("third")) {
                        String third = postDTO.kind.get("third");
                        if (!tagDTO.tag.contains(third)) {
                            tagDTO.tag.add(third);
                        }
                    }
                    if(!tagDTO.tag.contains("#"+army)){
                        tagDTO.tag.add("#"+army);
                    }
                    if(!tagDTO.tag.contains("#"+budae)){
                        tagDTO.tag.add("#"+budae);
                    }
                    if(!tagDTO.tag.contains("#"+rank)){
                        tagDTO.tag.add("#"+rank);
                    }
                    transaction.set(docRef, tagDTO);
                }
                else{
                    TagDTO tagDTO = new TagDTO();
                    if (postDTO.kind.containsKey("first")) {
                        String first = postDTO.kind.get("first");
                        tagDTO.tag.add(first);
                    }
                    if (postDTO.kind.containsKey("second")) {
                        String second = postDTO.kind.get("second");
                        tagDTO.tag.add(second);
                    }
                    if (postDTO.kind.containsKey("third")) {
                        String third = postDTO.kind.get("third");
                        tagDTO.tag.add(third);
                    }
                    if(!tagDTO.tag.contains("#"+army)){
                        tagDTO.tag.add("#"+army);
                    }
                    if(!tagDTO.tag.contains("#"+budae)){
                        tagDTO.tag.add("#"+budae);
                    }
                    if(!tagDTO.tag.contains("#"+rank)){
                        tagDTO.tag.add("#"+rank);
                    }
                    transaction.set(docRef, tagDTO);
                }
                return null;
            }
        });

        MyPostDTO myPost = new MyPostDTO();
        myPost.documentUid = documentUid;
        myPost.postUid=postDTO.timestamp+"_"+postDTO.uid;
        myPost.timestamp=postDTO.timestamp;
        myPost.name = name;
        firestore.collection(auth.getCurrentUser().getUid()+"_MyPost").document().set(myPost);
    }
}