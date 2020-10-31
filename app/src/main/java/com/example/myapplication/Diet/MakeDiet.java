package com.example.myapplication.Diet;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.DietDTO;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MakeDiet extends AppCompatActivity {

    final int TIME_DIVIDE = 24*60*60*1000;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    TextView dietDate;
    EditText dietDateEdit;

    EditText[] breakfast;
    EditText[] lunch;
    EditText[] dinner;

    ImageView cancel;
    TextView complete;

    Calendar setDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_diet);

        breakfast = new EditText[6];
        lunch = new EditText[6];
        dinner = new EditText[6];

        breakfast[0] = findViewById(R.id.breakfast1); breakfast[1] = findViewById(R.id.breakfast2);
        breakfast[2] = findViewById(R.id.breakfast3); breakfast[3] = findViewById(R.id.breakfast4);
        breakfast[4] = findViewById(R.id.breakfast5); breakfast[5] = findViewById(R.id.breakfast6);

        lunch[0] = findViewById(R.id.lunch1); lunch[1] = findViewById(R.id.lunch2);
        lunch[2] = findViewById(R.id.lunch3); lunch[3] = findViewById(R.id.lunch4);
        lunch[4] = findViewById(R.id.lunch5); lunch[5] = findViewById(R.id.lunch6);

        dinner[0] = findViewById(R.id.dinner1); dinner[1] = findViewById(R.id.dinner2);
        dinner[2] = findViewById(R.id.dinner3); dinner[3] = findViewById(R.id.dinner4);
        dinner[4] = findViewById(R.id.dinner5); dinner[5] = findViewById(R.id.dinner6);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        cancel = (ImageView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dietDate = (TextView)findViewById(R.id.diet_date);
        dietDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog;
                Calendar today = Calendar.getInstance();
                dialog = new DatePickerDialog(MakeDiet.this, R.style.DialogThemeOrange, listener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
                dialog.show();
            }
        });

        dietDateEdit = (EditText)findViewById(R.id.diet_date_edit);
        setDay = Calendar.getInstance();

        complete = (TextView)findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                                DietDTO dietDTO = new DietDTO();
                                dietDTO.postDay = setDay.getTimeInMillis()/TIME_DIVIDE;

                                for(int i = 0; i<6; i++){
                                    if(!breakfast[i].getText().toString().isEmpty()){
                                        dietDTO.breakfast.add(breakfast[i].getText().toString());
                                    }
                                    if(!lunch[i].getText().toString().isEmpty()){
                                        dietDTO.lunch.add(lunch[i].getText().toString());
                                    }
                                    if(!dinner[i].getText().toString().isEmpty()){
                                        dietDTO.dinner.add(dinner[i].getText().toString());
                                    }
                                }

                                storePost(dietDTO, userDTO.army+userDTO.budae+"_diet");
                                Toast.makeText(MakeDiet.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
            }
        });
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            setDay.set(year, month, day);
            dietDateEdit.setText(year+"년 "+(month+1)+"월 "+day+"일");
        }
    };

    public void storePost(DietDTO dietDTO, String collectionName){
        firestore.collection(collectionName).document().set(dietDTO);
    }
}