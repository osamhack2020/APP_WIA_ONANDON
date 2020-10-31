package com.example.myapplication.MyGoalPost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

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

import com.example.myapplication.MakeAccount;
import com.example.myapplication.NewPostPublic;
import com.example.myapplication.R;
import com.example.myapplication.model.MyGoalContentDTO;
import com.example.myapplication.model.PostDTO;
import com.example.myapplication.model.TagDTO;
import com.example.myapplication.model.UserDTO;
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
import java.util.Calendar;
import java.util.Date;

public class NewPost extends AppCompatActivity {

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

    String army;
    String budae;
    String rank;

    int yearInput, monthInput, dayInput;

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
        kindFirst.setText("#");
        kindSecond.setText("#");
        kindThird.setText("#");

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        army = intent.getStringExtra("army");
        budae = intent.getStringExtra("budae");
        rank = intent.getStringExtra("rank");

        goalDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog;
                Calendar today = Calendar.getInstance();
                dialog = new DatePickerDialog(NewPost.this, R.style.DialogTheme, listener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
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
                if (MyGoalPostTitle.getText().toString().isEmpty() || MyGoalPostExplain.getText().toString().isEmpty()
                        || goalDueDateEdit.getText().toString().isEmpty()
                        || ((kindFirst.getText().toString().isEmpty() ||  kindFirst.getText().toString().equals("#"))
                        && (kindSecond.getText().toString().isEmpty() || kindSecond.getText().toString().equals("#"))
                        && (kindThird.getText().toString().isEmpty() || kindThird.getText().toString().equals("#")))) {
                    Toast.makeText(NewPost.this, "항목을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if((!kindFirst.getText().toString().isEmpty() && kindFirst.getText().toString().charAt(0) != '#')
                        || (!kindSecond.getText().toString().isEmpty() && kindSecond.getText().toString().charAt(0) != '#')
                        || (!kindThird.getText().toString().isEmpty() && kindThird.getText().toString().charAt(0) != '#')){
                    Toast.makeText(NewPost.this, "태그는 반드시 앞에 '#'이 붙어야 합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(isPhoto == 1){
                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(NewPost.this, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();
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

                            final MyGoalContentDTO post = new MyGoalContentDTO();
                            post.content="MyGoal";
                            post.explain = MyGoalPostExplain.getText().toString();
                            post.title = MyGoalPostTitle.getText().toString();
                            post.uid = auth.getCurrentUser().getUid();
                            post.timestamp = System.currentTimeMillis();
                            post.year = yearInput;
                            post.month = monthInput;
                            post.day = dayInput;
                            post.isPhoto = 1;
                            post.imageUri = uri.toString();

                            if (!kindFirst.getText().toString().isEmpty() && !kindFirst.getText().toString().equals("#")) {
                                post.kind.put("first", kindFirst.getText().toString());
                            }
                            if (!kindSecond.getText().toString().isEmpty() && !kindSecond.getText().toString().equals("#")) {
                                post.kind.put("second", kindSecond.getText().toString());
                            }
                            if (!kindThird.getText().toString().isEmpty() && !kindThird.getText().toString().equals("#")) {
                                post.kind.put("third", kindThird.getText().toString());
                            }

                            post.kind.put("army", "#"+army);
                            post.kind.put("budae", "#"+budae);
                            post.kind.put("rank", "#"+rank);

                            storePost(post);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(NewPost.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
                else {
                    final MyGoalContentDTO post = new MyGoalContentDTO();
                    post.content="MyGoal";
                    post.explain = MyGoalPostExplain.getText().toString();
                    post.title = MyGoalPostTitle.getText().toString();
                    post.uid = auth.getCurrentUser().getUid();
                    post.timestamp = System.currentTimeMillis();
                    post.year = yearInput;
                    post.month = monthInput;
                    post.day = dayInput;

                    if (!kindFirst.getText().toString().isEmpty() && !kindFirst.getText().toString().equals("#")) {
                        post.kind.put("first", kindFirst.getText().toString());
                    }
                    if (!kindSecond.getText().toString().isEmpty() && !kindSecond.getText().toString().equals("#")) {
                        post.kind.put("second", kindSecond.getText().toString());
                    }
                    if (!kindThird.getText().toString().isEmpty() && !kindThird.getText().toString().equals("#")) {
                        post.kind.put("third", kindThird.getText().toString());
                    }

                    post.kind.put("army", "#"+army);
                    post.kind.put("budae", "#"+budae);
                    post.kind.put("rank", "#"+rank);

                    storePost(post);
                    Toast.makeText(NewPost.this, "업로드 성공", Toast.LENGTH_SHORT).show();
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
            isPhoto = 1;
        }
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

    public void storePost(final MyGoalContentDTO myGoalContentDTO){
        firestore.collection("MyGoal").document().set(myGoalContentDTO)
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

        final DocumentReference docRef = firestore.collection("MyGoal_tag").document("tag");
        firestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);

                if(snapshot.exists()) {
                    final TagDTO tagDTO = snapshot.toObject(TagDTO.class);

                    if (myGoalContentDTO.kind.containsKey("first")) {
                        String first = myGoalContentDTO.kind.get("first");
                        if (!tagDTO.tag.contains(first)) {
                            tagDTO.tag.add(first);
                        }
                    }
                    if (myGoalContentDTO.kind.containsKey("second")) {
                        String second = myGoalContentDTO.kind.get("second");
                        if (!tagDTO.tag.contains(second)) {
                            tagDTO.tag.add(second);
                        }
                    }
                    if (myGoalContentDTO.kind.containsKey("third")) {
                        String third = myGoalContentDTO.kind.get("third");
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
                    final TagDTO tagDTO = new TagDTO();
                    if (myGoalContentDTO.kind.containsKey("first")) {
                        String first = myGoalContentDTO.kind.get("first");
                        tagDTO.tag.add(first);
                    }
                    if (myGoalContentDTO.kind.containsKey("second")) {
                        String second = myGoalContentDTO.kind.get("second");
                        tagDTO.tag.add(second);
                    }
                    if (myGoalContentDTO.kind.containsKey("third")) {
                        String third = myGoalContentDTO.kind.get("third");
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
    }
}