package com.example.myapplication.Diet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class DietPager extends Fragment {

    final int TIME_DIVIDE = 24*60*60*1000;

    TextView[] menu;
    TextView date;
    int click=-1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diet_pager, container, false);

        menu = new TextView[18];
        date = view.findViewById(R.id.date);

        menu[0] = view.findViewById(R.id.breakfast1); menu[1] = view.findViewById(R.id.breakfast2);
        menu[2] = view.findViewById(R.id.breakfast3); menu[3] = view.findViewById(R.id.breakfast4);
        menu[4] = view.findViewById(R.id.breakfast5); menu[5] = view.findViewById(R.id.breakfast6);

        menu[6] = view.findViewById(R.id.lunch1); menu[7] = view.findViewById(R.id.lunch2);
        menu[8] = view.findViewById(R.id.lunch3); menu[9] = view.findViewById(R.id.lunch4);
        menu[10] = view.findViewById(R.id.lunch5); menu[11] = view.findViewById(R.id.lunch6);

        menu[12] = view.findViewById(R.id.dinner1); menu[13] = view.findViewById(R.id.dinner2);
        menu[14] = view.findViewById(R.id.dinner3); menu[15] = view.findViewById(R.id.dinner4);
        menu[16] = view.findViewById(R.id.dinner5); menu[17] = view.findViewById(R.id.dinner6);

        Long postDay = getArguments().getLong("time");
        ArrayList<String> breakfast = getArguments().getStringArrayList("breakfast");
        ArrayList<String> lunch = getArguments().getStringArrayList("lunch");
        ArrayList<String> dinner = getArguments().getStringArrayList("dinner");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(postDay*TIME_DIVIDE);
        date.setText(calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DATE));


        for(int i = 0; i<breakfast.size(); i++){
            menu[i].setText(breakfast.get(i));
        }
        for(int i = 0; i<6-breakfast.size(); i++){
            menu[breakfast.size()+i].setText("");
        }

        for(int i = 0; i<lunch.size(); i++){
            menu[6+i].setText(lunch.get(i));
        }
        for(int i = 0; i<6-lunch.size(); i++){
            menu[6+lunch.size()+i].setText("");
        }

        for(int i = 0; i<dinner.size(); i++){
            menu[12+i].setText(dinner.get(i));
        }
        for(int i = 0; i<6-dinner.size(); i++){
            menu[12+dinner.size()+i].setText("");
        }

        for(int i = 0; i<18; i++){
            final int finalI = i;
            menu[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(click != -1){
                        menu[click].setSelected(false);
                    }
                    menu[finalI].setSelected(true);
                    click = finalI;
                }
            });
        }

        return view;
    }
}