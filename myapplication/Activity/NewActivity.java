package com.example.myapplication.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.example.myapplication.NewPostPublic;
import com.example.myapplication.R;
import com.example.myapplication.model.ActivityDTO;
import com.example.myapplication.model.TagDTO;
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

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewActivity extends AppCompatActivity {

    final int PICK_IMAGE_FROM_ALBUM = 0;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    private String photoUrl;
    int isPhoto = 0;

    ImageView cancel;
    TextView complete;
    TextView dueDate;
    EditText title;
    EditText explain;
    EditText dueDateEdit;
    EditText link;
    EditText participation;
    EditText name;

    ImageView addPhoto;
    ImageView photoPreview;

    EditText kindFirst;
    EditText kindSecond;
    EditText kindThird;

    ProgressBar progressBar;

    int yearInput, monthInput, dayInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        dueDate = (TextView)findViewById(R.id.goal_due_date);
        dueDateEdit = (EditText)findViewById(R.id.goal_due_date_edit);
        title = (EditText)findViewById(R.id.my_goal_post_title);
        explain = (EditText)findViewById(R.id.my_goal_post_explain);
        link = (EditText)findViewById(R.id.link);
        participation = (EditText)findViewById(R.id.participation);
        name = (EditText)findViewById(R.id.name);

        kindFirst = (EditText)findViewById(R.id.kind_first);
        kindSecond = (EditText)findViewById(R.id.kind_second);
        kindThird = (EditText)findViewById(R.id.kind_third);
        kindFirst.setText("#");
        kindSecond.setText("#");
        kindThird.setText("#");

        addPhoto = (ImageView)findViewById(R.id.add_photo);
        photoPreview = (ImageView)findViewById(R.id.photo_preview);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog;
                Calendar today = Calendar.getInstance();
                dialog = new DatePickerDialog(NewActivity.this, R.style.DialogTheme, listener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
                dialog.getDatePicker().setMinDate(today.getTime().getTime());
                dialog.show();
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);
            }
        });

        complete = (TextView)findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(explain.getText().toString().isEmpty() || title.getText().toString().isEmpty() || dueDateEdit.getText().toString().isEmpty()
                   || link.getText().toString().isEmpty() || participation.getText().toString().isEmpty() || name.getText().toString().isEmpty()
                   || ((kindFirst.getText().toString().isEmpty() ||  kindFirst.getText().toString().equals("#"))
                        && (kindSecond.getText().toString().isEmpty() || kindSecond.getText().toString().equals("#"))
                        && (kindThird.getText().toString().isEmpty() || kindThird.getText().toString().equals("#")))){
                    Toast.makeText(NewActivity.this, "항목을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if((!kindFirst.getText().toString().isEmpty() && kindFirst.getText().toString().charAt(0) != '#')
                        || (!kindSecond.getText().toString().isEmpty() && kindSecond.getText().toString().charAt(0) != '#')
                        || (!kindThird.getText().toString().isEmpty() && kindThird.getText().toString().charAt(0) != '#')){
                    Toast.makeText(NewActivity.this, "태그는 반드시 앞에 '#'이 붙어야 합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(isPhoto == 1){
                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(NewActivity.this, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();
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

                            ActivityDTO post = new ActivityDTO();
                            post.explain = explain.getText().toString();
                            post.title = title.getText().toString();
                            post.timestamp = System.currentTimeMillis();
                            post.year = yearInput;
                            post.month = monthInput;
                            post.day = dayInput;
                            post.imageUri = uri.toString();
                            post.link = link.getText().toString();
                            post.participation = participation.getText().toString();
                            post.name = name.getText().toString();

                            if (!kindFirst.getText().toString().isEmpty() && !kindFirst.getText().toString().equals("#")) {
                                post.kind.put("first", kindFirst.getText().toString());
                            }
                            if (!kindSecond.getText().toString().isEmpty() && !kindSecond.getText().toString().equals("#")) {
                                post.kind.put("second", kindSecond.getText().toString());
                            }
                            if (!kindThird.getText().toString().isEmpty() && !kindThird.getText().toString().equals("#")) {
                                post.kind.put("third", kindThird.getText().toString());
                            }

                            storePost(post);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(NewActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
                else{
                    Toast.makeText(NewActivity.this, "군내 활동 게시판은 반드시 사진을 첨부해야 합니다.", Toast.LENGTH_SHORT).show();
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
            isPhoto = 1;
        }
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            yearInput = year;
            monthInput = month;
            dayInput = day;
            dueDateEdit.setText(year+"년 "+(month+1)+"월 "+day+"일");
        }
    };

    public void storePost(final ActivityDTO activityDTO){
        firestore.collection("Activity").document().set(activityDTO)
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

        final DocumentReference docRef = firestore.collection("Activity_tag").document("tag");
        firestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);

                if(snapshot.exists()) {
                    TagDTO tagDTO = snapshot.toObject(TagDTO.class);

                    if (activityDTO.kind.containsKey("first")) {
                        String first = activityDTO.kind.get("first");
                        if (!tagDTO.tag.contains(first)) {
                            tagDTO.tag.add(first);
                        }
                    }
                    if (activityDTO.kind.containsKey("second")) {
                        String second = activityDTO.kind.get("second");
                        if (!tagDTO.tag.contains(second)) {
                            tagDTO.tag.add(second);
                        }
                    }
                    if (activityDTO.kind.containsKey("third")) {
                        String third = activityDTO.kind.get("third");
                        if (!tagDTO.tag.contains(third)) {
                            tagDTO.tag.add(third);
                        }
                    }
                    transaction.set(docRef, tagDTO);
                }
                else{
                    TagDTO tagDTO = new TagDTO();
                    if (activityDTO.kind.containsKey("first")) {
                        String first = activityDTO.kind.get("first");
                        tagDTO.tag.add(first);
                    }
                    if (activityDTO.kind.containsKey("second")) {
                        String second = activityDTO.kind.get("second");
                        tagDTO.tag.add(second);
                    }
                    if (activityDTO.kind.containsKey("third")) {
                        String third = activityDTO.kind.get("third");
                        tagDTO.tag.add(third);
                    }
                    transaction.set(docRef, tagDTO);
                }
                return null;
            }
        });
    }
}