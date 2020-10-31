package com.example.myapplication.MyGoalPost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.example.myapplication.BottomNavigation.MyGoal;
import com.example.myapplication.R;
import com.example.myapplication.UpdatePost;
import com.example.myapplication.model.MyGoalContentDTO;
import com.example.myapplication.model.PostDTO;
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
import java.util.HashMap;
import java.util.Map;

public class UpdateGoal extends AppCompatActivity {

    final int PICK_IMAGE_FROM_ALBUM = 0;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    private String photoUrl;
    int isPhoto = 0;

    ImageView cancel;
    TextView complete;
    TextView goalDueDate;
    EditText MyGoalPostTitle;
    EditText MyGoalPostExplain;
    EditText goalDueDateEdit;

    EditText kindFirst;
    EditText kindSecond;
    EditText kindThird;

    ImageView addPhoto;
    ImageView photoPreview;
    ProgressBar progressBar;

    int yearInput, monthInput, dayInput;

    Intent intent;
    int wasPhoto = 0;
    int change = 0;
    int commentCount = 0;
    int favoriteCount = 0;

    String documentUid;
    String postUid;
    String imageUri;

    Long storeTime;

    Map<String, Boolean> favorites = new HashMap<>();
    Map<String, String> tag = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        goalDueDate = (TextView)findViewById(R.id.goal_due_date);
        goalDueDateEdit = (EditText)findViewById(R.id.goal_due_date_edit);
        MyGoalPostTitle = (EditText)findViewById(R.id.my_goal_post_title);
        MyGoalPostExplain = (EditText)findViewById(R.id.my_goal_post_explain);
        addPhoto = (ImageView)findViewById(R.id.add_photo);
        photoPreview = (ImageView)findViewById(R.id.photo_preview);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        kindFirst = (EditText)findViewById(R.id.kind_first);
        kindSecond = (EditText)findViewById(R.id.kind_second);
        kindThird = (EditText)findViewById(R.id.kind_third);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        intent = getIntent();
        documentUid = intent.getStringExtra("documentUid");
        postUid = intent.getStringExtra("postUid");

        goalDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog;
                Calendar today = Calendar.getInstance();
                dialog = new DatePickerDialog(UpdateGoal.this, R.style.DialogTheme, listener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
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

        firestore.collection(documentUid).document(postUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       MyGoalContentDTO myGoalContentDTO = documentSnapshot.toObject(MyGoalContentDTO.class);

                        MyGoalPostTitle.setText(myGoalContentDTO.title);
                        MyGoalPostExplain.setText(myGoalContentDTO.explain);

                        if(myGoalContentDTO.kind.containsKey("first")){
                            kindFirst.setText(myGoalContentDTO.kind.get("first"));
                        }
                        else{
                            kindFirst.setText("#");
                        }

                        if(myGoalContentDTO.kind.containsKey("second")){
                            kindSecond.setText(myGoalContentDTO.kind.get("second"));
                        }
                        else{
                            kindSecond.setText("#");
                        }

                        if(myGoalContentDTO.kind.containsKey("third")){
                            kindThird.setText(myGoalContentDTO.kind.get("third"));
                        }
                        else{
                            kindThird.setText("#");
                        }

                        commentCount = myGoalContentDTO.commentCount;
                        favoriteCount = myGoalContentDTO.favoriteCount;
                        storeTime = myGoalContentDTO.timestamp;
                        favorites = myGoalContentDTO.favorites;
                        tag = myGoalContentDTO.kind;

                        yearInput = myGoalContentDTO.year;
                        monthInput = myGoalContentDTO.month;
                        dayInput = myGoalContentDTO.day;
                        goalDueDateEdit.setText(yearInput+"년 "+(monthInput+1)+"월 "+dayInput+"일");

                        if(myGoalContentDTO.isPhoto == 1){
                            wasPhoto = 1;
                            imageUri = intent.getStringExtra("imageUri");
                            Glide.with(UpdateGoal.this).load(myGoalContentDTO.imageUri)
                                    .into(photoPreview);
                        }
                    }
                });

        complete = (TextView)findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyGoalPostTitle.getText().toString().isEmpty() || MyGoalPostExplain.getText().toString().isEmpty()
                        || goalDueDateEdit.getText().toString().isEmpty()
                        || ((kindFirst.getText().toString().isEmpty() ||  kindFirst.getText().toString().equals("#"))
                        && (kindSecond.getText().toString().isEmpty() || kindSecond.getText().toString().equals("#"))
                        && (kindThird.getText().toString().isEmpty() || kindThird.getText().toString().equals("#")))) {
                    Toast.makeText(UpdateGoal.this, "항목을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if((!kindFirst.getText().toString().isEmpty() && kindFirst.getText().toString().charAt(0) != '#')
                        || (!kindSecond.getText().toString().isEmpty() && kindSecond.getText().toString().charAt(0) != '#')
                        || (!kindThird.getText().toString().isEmpty() && kindThird.getText().toString().charAt(0) != '#')){
                    Toast.makeText(UpdateGoal.this, "태그는 반드시 앞에 '#'이 붙어야 합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(wasPhoto == 1 && change == 1){
                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(UpdateGoal.this, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();

                    StorageReference httpsReference = storage.getReferenceFromUrl(imageUri);
                    httpsReference.delete();
                    makeImageUri();
                }
                else if(wasPhoto == 0 && change == 1){
                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(UpdateGoal.this, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();

                    makeImageUri();
                }
                else {
                    MyGoalContentDTO myGoalContentDTO = new MyGoalContentDTO();
                    myGoalContentDTO.explain = MyGoalPostExplain.getText().toString();
                    myGoalContentDTO.title = MyGoalPostTitle.getText().toString();

                    if (!kindFirst.getText().toString().isEmpty() && !kindFirst.getText().toString().equals("#")) {
                        myGoalContentDTO.kind.put("first", kindFirst.getText().toString());
                    }
                    if (!kindSecond.getText().toString().isEmpty() && !kindSecond.getText().toString().equals("#")) {
                        myGoalContentDTO.kind.put("second", kindSecond.getText().toString());
                    }
                    if (!kindThird.getText().toString().isEmpty() && !kindThird.getText().toString().equals("#")) {
                        myGoalContentDTO.kind.put("third", kindThird.getText().toString());
                    }

                    myGoalContentDTO.kind.put("army", tag.get("army"));
                    myGoalContentDTO.kind.put("budae", tag.get("budae"));
                    myGoalContentDTO.kind.put("rank", tag.get("rank"));

                    if(wasPhoto == 1 && change != 1){
                        myGoalContentDTO.imageUri = imageUri;
                        myGoalContentDTO.isPhoto = 1;
                    }

                    myGoalContentDTO.year = yearInput;
                    myGoalContentDTO.month = monthInput;
                    myGoalContentDTO.day = dayInput;

                    myGoalContentDTO.uid = auth.getCurrentUser().getUid();
                    myGoalContentDTO.commentCount = commentCount;
                    myGoalContentDTO.favoriteCount = favoriteCount;
                    myGoalContentDTO.timestamp = storeTime;
                    myGoalContentDTO.favorites = favorites;

                    storePost(myGoalContentDTO);
                    Toast.makeText(UpdateGoal.this, "수정 성공", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    setResult(RESULT_FIRST_USER, intent);
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

        MyGoalContentDTO myGoalContentDTO = new MyGoalContentDTO();
        myGoalContentDTO.explain = MyGoalPostExplain.getText().toString();
        myGoalContentDTO.title = MyGoalPostTitle.getText().toString();

        if (!kindFirst.getText().toString().isEmpty() && !kindFirst.getText().toString().equals("#")) {
            myGoalContentDTO.kind.put("first", kindFirst.getText().toString());
        }
        if (!kindSecond.getText().toString().isEmpty() && !kindSecond.getText().toString().equals("#")) {
            myGoalContentDTO.kind.put("second", kindSecond.getText().toString());
        }
        if (!kindThird.getText().toString().isEmpty() && !kindThird.getText().toString().equals("#")) {
            myGoalContentDTO.kind.put("third", kindThird.getText().toString());
        }

        myGoalContentDTO.kind.put("army", tag.get("army"));
        myGoalContentDTO.kind.put("budae", tag.get("budae"));
        myGoalContentDTO.kind.put("rank", tag.get("rank"));

        myGoalContentDTO.isPhoto = 1;
        myGoalContentDTO.imageUri=url;

        myGoalContentDTO.year = yearInput;
        myGoalContentDTO.month = monthInput;
        myGoalContentDTO.day = dayInput;

        myGoalContentDTO.uid = auth.getCurrentUser().getUid();
        myGoalContentDTO.commentCount = commentCount;
        myGoalContentDTO.favoriteCount = favoriteCount;
        myGoalContentDTO.timestamp = storeTime;
        myGoalContentDTO.favorites = favorites;

        storePost(myGoalContentDTO);
        Toast.makeText(UpdateGoal.this, "수정 성공", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);

        Intent intent = new Intent();
        setResult(RESULT_FIRST_USER, intent);
        finish();
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

    public void storePost(MyGoalContentDTO myGoalContentDTO){
        firestore.collection(documentUid).document(postUid).set(myGoalContentDTO)
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

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            yearInput = year;
            monthInput = month;
            dayInput = day;
            goalDueDateEdit.setText(year+"년 "+(month+1)+"월 "+day+"일");
        }
    };
}