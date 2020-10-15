package com.example.myapplication;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;

import org.threeten.bp.DayOfWeek;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Vacation> mArrayList;
    private CustomAdapter mAdapter;
    private MaterialCalendarView mCalendarView;
    private Vacation[] userVacationData = {new Vacation("연가", "연가", 24), new Vacation("위로", "신병위로", 4), new Vacation("위로", "수료식", 1)};
    private int count = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_calendar);

        //ImageButton mAddButton = (ImageButton) findViewById(R.id.button_add_vacation);

        mCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();
        for(Vacation vac : userVacationData) {
            mArrayList.add(0, vac);
            mCalendarView.addDecorator(vac.getDecorator());
        }

        mCalendarView.state().edit()
                .setFirstDayOfWeek(DayOfWeek.WEDNESDAY)
                .setMinimumDate(CalendarDay.from(2000, 1, 1))
                .setMaximumDate(CalendarDay.from(2030, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        //Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);

        mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
        mCalendarView.setSelectionColor(Color.parseColor("#B2D0FF"));
        mAdapter = new CustomAdapter(this, mArrayList, mCalendarView);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        Button buttonInsert = (Button)findViewById(R.id.button_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                View view = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.edit_vacation, null, false);
                builder.setView(view);

                final Button buttonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                final EditText editTextType = (EditText) view.findViewById(R.id.edittext_dialog_type);
                final EditText editTextName = (EditText) view.findViewById(R.id.edittext_dialog_name);
                final EditText editTextPeriod = (EditText) view.findViewById(R.id.edittext_dialog_period);

                buttonSubmit.setText("추가");

                final AlertDialog dialog = builder.create();

                buttonSubmit.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        String strType = editTextType.getText().toString();
                        String strName = editTextName.getText().toString();
                        int strPeriod = 0;
                        try {
                            strPeriod = Integer.parseInt(editTextPeriod.getText().toString().replaceAll("[^0-9]", ""));
                        } catch(Exception e) {
                            strPeriod = 0;
                        }

                        if(!strType.equals("") && !strName.equals("") && strPeriod > 0) {
                            Vacation vac = new Vacation(strType, strName, strPeriod);
                            mCalendarView.addDecorator(vac.getDecorator());
                            mArrayList.add(0, vac);
                            mAdapter.notifyItemInserted(0);
                            //mAdapter.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "0일 이하는 등록이 불가능합니다", Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

    }

}